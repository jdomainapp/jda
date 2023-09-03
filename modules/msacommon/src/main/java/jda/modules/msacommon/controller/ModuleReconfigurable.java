package jda.modules.msacommon.controller;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.msacommon.model.ModuleDesc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Method;

/**
 * @overview A default interface representing the common behaviours that are required
 * of a module if it is to be considered to be reconfigurable by the Service-Reconfiguration subsystem.</p>
 *
 * @author ducmle
 */
public interface ModuleReconfigurable {

  /**
   * Provides a default implementation for this method.
   *
   * @requires
   *  the service identified by <tt>serviceName</tt> has been started /\
   *  there exists a service-handling-method defined in this service class for <tt>serviceName</tt>
   *
   * @effects
   *  register the service identified by <tt>serviceName</tt> as a child service of this
   *
   * @version 1.0
   */
  @RequestMapping(value="registerChildService")
  default ResponseEntity registerChildService(@RequestPart("childName") String serviceName) {
    String servicePath = "/"+serviceName+"/**";
    RequestMappingInfo mappingInfo = RequestMappingInfo.paths(servicePath).build();
    Method handleMethod;
    try {
      Class[] methodArgs = new Class[2];
      methodArgs[0]= HttpServletRequest.class;
      methodArgs[1]= HttpServletResponse.class;
      String handleServiceMethod = ControllerTk.toCamelCase(serviceName);
      handleServiceMethod = "handle" +
          (String.valueOf(handleServiceMethod.charAt(0))).toUpperCase() +
          handleServiceMethod.substring(1);

      handleMethod = getClass().getDeclaredMethod(handleServiceMethod, methodArgs);

      RequestMappingHandlerMapping requestMappingHandlerMapping = getApplicationContext()
          .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

      requestMappingHandlerMapping.registerMapping(mappingInfo, this, handleMethod);

      return  ResponseEntity.status(HttpStatus.OK)
          .body(String.format("Registering child service '%s': OK", serviceName));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(String.format("Failed to register child service '%s' to parent: %s", serviceName, e.getMessage()));
    }
  }

  /**
   * @effects
   *  remove the module specified by <tt>moduleDesc</tt> from the service tree of this.
   */
  @RequestMapping("removeModule")
  default ResponseEntity removeModule(@RequestBody ModuleDesc moduleDesc){
    String domainClsName = moduleDesc.getDomainClsName();

    // remove
    ResponseEntity response = null;
    ControllerRegistry ctrlRegistry = getControllerRegistry();
    if (ctrlRegistry.containsKeyByDomainCls(domainClsName, true)) {
      ctrlRegistry.removeByDomainCls(domainClsName);
      response = ResponseEntity.ok().build();
    } else {
      response = ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(String.format("Module %s not found in the module registry of the service %s", domainClsName, this.getClass().getSimpleName()));
    }

    return response;
  }

  /**
   * @effects
   *	Invoked by service-reconfigurer service to run and add the specified <tt>module</tt> as a child of
   *  <tt>parentModule</tt>	of this service's tree.
   *  The module's code is contained in the JAR <tt>file</tt>.
   * @version 1.0
   */
  @PostMapping(value = "/runModule")
  default ResponseEntity<?> runModule(
      @RequestPart("targetServ") String targetServ,
      @RequestPart("pid") String pid,
      @RequestPart("module") String module,
      @RequestPart("parentModule") String parentModule,
      @RequestPart("service") String service,
      @RequestPart("file") MultipartFile file) {

    // save file to deploy-path
    File deployPath = ControllerTk.getDeployPath(getApplicationContext().getEnvironment(), module, false);
    File dartFile = new File(deployPath.getPath(), module + ".jar");
    if (!deployPath.exists()) {
      deployPath.mkdirs();
    } else if (dartFile.exists()) {	// delete existing files
      dartFile.delete();
    }

    try {
      file.transferTo(dartFile);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(e.getMessage());
    }

    boolean result = true;
    String errMsg = null;
    Class domainCls = null;
    try {
      // load the @Controller class from the jar file
      Class<DefaultController<?,?>> controllerCls = ControllerTk.loadModuleFromJarFile(getClass().getClassLoader(), dartFile);

      // run the controller and registers it to the registry

      DefaultController<?, ?> controllerInstance = ControllerTk.newControllerInstance(controllerCls);
      // extract the domain class from the generic type of the controller
      domainCls = controllerInstance.getGenericType();

      final ControllerRegistry controllerRegistry = getControllerRegistry();
      controllerRegistry.putByClass(domainCls, controllerInstance);
    } catch (NotPossibleException e) {
      result = false;
      errMsg = e.getMessage();
    }

    if (result) {
      // register module as child of parentModule
      //HttpStatus status = registerAsChild(module, targetServ, parentModule);
      registerChildModule(module, domainCls, parentModule);

//      logger.info("Module execution: completed");
      return ResponseEntity.ok(String.format("Run module (OK): %s (file: %s)",
          module, dartFile));
    } else {
//      logger.info("Module execution: NOT completed (successfully)");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          String.format("Failed to execute module: %s (file: %s) %s",
              module,dartFile, (errMsg != null) ? "(Error: " + errMsg +")" : ""));
    }
  }

  /**
   * @effects
   *	registers <tt>module</tt> as the child module of <tt>parentModule</tt> in the current service tree of this.
   *
   * @version 1.0
   */
  default void registerChildModule(String module, Class domainCls, String parentModule) {
    // get the parent module controller
    RedirectController parentCtrl = RedirectControllerRegistry.getInstance().get(parentModule);

    // register module's controller to the registry of parent module controller
    RedirectController.getPathmap().put("/"+module, domainCls);
  }

  /**
   * This is the opposite of {@link #registerChildService(String)}.
   *
   * @effects remove service specified by <tt>moduleDesc</tt> from the service tree of this
   */
  @RequestMapping("unregisterService")
  default ResponseEntity unregisterService(@RequestBody ModuleDesc moduleDesc){
    // remove the action mapping for the service handling method
    String serviceName = moduleDesc.getService();
    String servicePath = "/"+serviceName+"/**";
    RequestMappingInfo mappingInfo = RequestMappingInfo.paths(servicePath).build();
    try {
      RequestMappingHandlerMapping requestMappingHandlerMapping = getApplicationContext()
          .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

      requestMappingHandlerMapping.unregisterMapping(mappingInfo);

      return  ResponseEntity.status(HttpStatus.OK)
          .body(String.format("Unregister child service '%s': OK", serviceName));

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(String.format("Failed to unregister child service '%s': %s", serviceName, e.getMessage()));
    }
  }

  ApplicationContext getApplicationContext();

  ControllerRegistry getControllerRegistry();

  /**
   * @effects
   *	a customised actuator to show the key status of this service. This includes the controller registry etc.
   * @version 1.0
   */
  @RequestMapping(value = "/myactuator/show")
  default ResponseEntity<?> myactuatorShow() {
    //todo: add other status information to the response Json
    String controllerRegistry = getControllerRegistry().toJson().toString();

    return ResponseEntity.status(HttpStatus.OK)
        .header("content-type", "application/json")
        .body(controllerRegistry);
  }
}
