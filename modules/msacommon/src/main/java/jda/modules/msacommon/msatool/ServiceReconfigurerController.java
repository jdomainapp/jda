package jda.modules.msacommon.msatool;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.model.ModuleDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

@Controller
public abstract class ServiceReconfigurerController extends ServiceReconfigurerControllerBasic {

	private static final Logger logger = LoggerFactory.getLogger(ServiceReconfigurerController.class);

	@Value("${spring.gateway.server}")
	private String gatewayServer;

	/**
	 * @effects
	 *	handle HTTP-POST request for promoting the specified <tt>module</tt>, which is a descendant of the service <tt>sourceServ</tt>, to become a child of <tt>targetServ</tt>
	 *
	 * @version 1.0
	 */
	@PostMapping(value = "/promote")
	public ResponseEntity<?> promote(@RequestParam String sourceServ,
		@RequestParam String module, @RequestParam String targetServ) {
		
		ModuleDesc md = transform(sourceServ, module);
		String servName = md.getService();

		try {
			String targetSR = lookUpReconfigurer(sourceServ);

			HttpStatus initResult = initRunService(targetSR, targetServ, md);

			boolean result = promoteCompleted(sourceServ, md);

			if (result)
				return ResponseEntity.ok(String.format("Promoting module '%s' to service '%s': OK", module, servName));
			else
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(String.format("Failed to promote module '%s' to service '%s'", module, servName));
		} catch (NotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	/**
	 * @effects
	 *	handle HTTP-POST request for demoting the specified <tt>demoteServ</tt>, which is a descendant of the service <tt>sourceServ</tt>, to become a child module of <tt>targetParentModule</tt> of the target service <tt>targetServ</tt>.
	 *
	 * @version 1.0
	 */
	@PostMapping(value = "/demote")
	public ResponseEntity<?> demote(@RequestParam String sourceServ,
																	@RequestParam String demoteServ,
																	@RequestParam String targetServ,
																	@RequestParam String targetParentModule) {
		// deform service
		ModuleDesc md = deform(sourceServ, demoteServ);
		String module = md.getModule();
		String deployServName = getServiceUrl(demoteServ);

		// stop service (if not already)
		boolean shutOk = ServiceMonitor.executeRequest(deployServName, MonitorAction.shutdown, null);

		if (!shutOk) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(String.format("Failed to shut down service '%s'", demoteServ));
		}

		try {
			String targetSR = lookUpReconfigurer(sourceServ);
			md.setParentModule(targetParentModule);

			// run module and register it on the target
			initRunModule(targetSR, targetServ, md);

			boolean result = demoteCompleted(sourceServ, md);

			if (result)
				return ResponseEntity.ok(String.format("Succeeded demoting service '%s' to module '%s'", demoteServ, module));
			else
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(String.format("Failed to demote service '%s' to module '%s'", sourceServ, module));
		} catch (NotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (NotPossibleException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	/**
	 * @effects	creates a microservice-project whose code structure is defined based on ModuleDesc object, that represents the serialised details of <tt>module</tt> of the <tt>sourceServ</tt> service.
	 * <br> The structural details include module's id, name and the deployed .jar
	 *
	 * @version 1.0
	 */
	private ModuleDesc transform(String sourceServ, String module) {
		String pid = module;
		String deployServName = getServiceNameFromModule(module);
		String jarFile = transformModuleToDeployableService(sourceServ, module, deployServName);
		ModuleDesc md = new ModuleDesc(pid, module, jarFile);
		md.setService(deployServName);
		return md;
	}

	/**
	 * @effects	creates a microservice-project whose code structure is transformed from the code of the specified <tt>module</tt>. Return the built JAR file of the microservice project, which is the result of executing this Maven command <tt>mvn clean package spring-boot:repackage</tt>
	 *
	 * @version 1.0
	 */
	private String transformModuleToDeployableService(String sourceServ, String module, String deployServName) {
		// let deployServName = lowerCase(module) + "-service"
//		String deployServName = getServiceNameFromModule(module);
		File dservDeployPath = getDeployPath(deployServName, true);

		// todo: for now assume file deploy.jar exists in the dservDeployPath folder
		// let dm = Folder(module), ds = Folder(sourceServ)
//		File dm = null;
//		File ds = null;
		// let dservices = ds.parent
		// create service dir for deployServ: dserv = dservices.mkdir("deployServName")
//		File dservices = ds.getParentFile();
//		File dserv = new File(dservices.getPath(), deployServName);
//		File dservTarget = new File(dserv.getPath(), "target");
//		dservTarget.mkdirs();

		// copy: dm -> dserv
		// transform dserv.src to become a service
		// create deployable jar in dserv.target using cmd `mvn clean package spring-boot:repackage`

		// let fdeploy = File(dserv.target, deployServName + ".jar")
		// return fdeploy
		String deployServJarName = deployServName + ".jar";
		File fdeploy = new File(dservDeployPath.getPath(), deployServJarName);

		return fdeploy.toString();
	}

	/**
	 * This is the reverse operation of {@link #transform(String, String)}.
	 *
	 * @requires
	 * 	<tt>childService</tt> is a child service of <tt>parentServ</tt>
	 *
	 * @effects
	 *	deform service <tt>childService</tt> that is a child of <tt>parentServ</tt> back to become a module.
	 *
	 * @version 1.0
	 */
	private ModuleDesc deform(String parentServ, String childService) {
		String module = getModuleNameFromService(childService);
		String pid = module;
		String jarFile = deformServiceToDeployableModule(parentServ, childService);
		ModuleDesc md = new ModuleDesc(pid, module, jarFile);
		md.setService(childService);
		return md;
	}
	
	/**
	 * @requires
	 * 	 <tt>childService</tt> is a child service of <tt>parentServ</tt>
	 *
	 * @effects
	 *   creates a Maven-project whose code structure is transformed from the code of the specified <tt>childService</tt>. Return the built JAR file of the project, which is the result of executing this Maven command <tt>mvn clean package spring-boot:repackage</tt>
	 * @version 1.0
	 */
	private String deformServiceToDeployableModule(String parentServ, String childService) {
		// let deployServName = lowerCase(module) + "-service"
		String deployModuleName = getModuleNameFromService(childService);
		File dmoduleDeployPath = getDeployPath(deployModuleName, true);

		// todo: for now assume file deploy.jar exists in the dmoduleDeployPath folder
		// let dm = Folder(module), ds = Folder(sourceServ)
//		File dm = null;
//		File ds = null;
		// let dservices = ds.parent
		// create service dir for deployServ: dserv = dservices.mkdir("deployServName")
//		File dservices = ds.getParentFile();
//		File dserv = new File(dservices.getPath(), deployServName);
//		File dservTarget = new File(dserv.getPath(), "target");
//		dservTarget.mkdirs();

		// copy: dm -> dserv
		// transform dserv.src to become a service
		// create deployable jar in dserv.target using cmd `mvn clean package spring-boot:repackage`

		// let fdeploy = File(dserv.target, deployServName + ".jar")
		// return fdeploy
		String deployJarName = deployModuleName + ".jar";
		File fdeploy = new File(dmoduleDeployPath.getPath(), deployJarName);

		return fdeploy.toString();
	}

	/**
	 * @effects requests the specified service-reconfigurer <tt>targetSR</tt> to create and deploy a service, whose structure is created from the module represented by <tt>md</tt> and that the new service is a child of <tt>targetServ</tt>.
	 *
	 * Return {@link HttpStatus} of the operation.
	 *
	 * @version 1.0
	 */
	public HttpStatus initRunService(String targetSR, String targetServ, ModuleDesc md) {
		// send md to targetSR to run service
		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = getMultiPartRequestFromModuleDesc(targetServ, md);
		String pathAction = "runService";
//		String gw = getApplicationContextEnv().getProperty("spring.gateway.server");
		String fullTargetURL = ControllerTk.getServicePath(gatewayServer, targetSR, pathAction);

		ResponseEntity<String> restExchange = restTemplate.exchange(fullTargetURL, HttpMethod.POST, reqEntity, String.class);

		//restExchange.getStatusCode()
		return restExchange.getStatusCode();
	}

	@PostMapping(value = "/runService")
	public ResponseEntity<?> runService(
			@RequestPart("targetServ") String targetServ,
			@RequestPart("pid") String pid,
			@RequestPart("module") String module,
			@RequestPart("service") String service,
			@RequestPart("file") MultipartFile file) {

		// save file to deploy-path
		File deployServicePath = getDeployPath(service, false);
		File dartFile = new File(deployServicePath.getPath(), service + ".jar");
		if (!deployServicePath.exists()) {
			deployServicePath.mkdirs();
		} else if (dartFile.exists()) {	// delete existing files
			dartFile.delete();
		}

		try {
			file.transferTo(dartFile);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}

		// execute service
		String serviceUrl = getServiceUrl(service);
		boolean result = executeService(serviceUrl, dartFile);

		if (result) {
			// register service as child of targetServ
			HttpStatus status = registerAsChild(service, targetServ);

			if (status.equals(HttpStatus.OK)) {
				return ResponseEntity.ok("Run service: success");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
						String.format("Failed to execute service: %s (%s)", dartFile, status ));
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to execute service: " + dartFile);
		}
	}

	/**
	 * Unlike {@link #initRunService(String, String, ModuleDesc)}, which invokes the path action {@link #runService(String, String, String, String, MultipartFile)} of the service-reconfigurer, this method invokes the path action <tt>runModule</tt> of the target domain service.
	 *
	 * @effects requests the specified target service <tt>targetServ</tt> to execute the module specified in <tt>md</tt>
	 * and that the new module is a child of <tt>targetParentModule</tt>.
	 *
	 * Throws NotPossibleException if fails.
	 *
	 * @version 1.0
	 */
	private void initRunModule(String targetSR, String targetServ, ModuleDesc md) throws NotPossibleException {
		// send md to targetSR to run service
		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = getMultiPartRequestFromModuleDesc(targetServ, md);
		String pathAction = "runModule";
		String fullTargetURL = ControllerTk.getServicePath(gatewayServer, targetServ, pathAction);

		ResponseEntity<String> restExchange = restTemplate.exchange(fullTargetURL, HttpMethod.POST, reqEntity, String.class);

		HttpStatus status = restExchange.getStatusCode();
		if (!status.is2xxSuccessful()) {
			throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, new String[] {
				"", "initRunModule", restExchange.getBody() });
		}
	}

	/**
	 *
	 * @effects
	 *	execute the service contained in <tt>deployJarFile</tt>, using <tt>java -jar</tt>
	 * @version 1.0
	 */
	public boolean executeService(String serviceUrl, File deployJarFile) {
		String cmd = "java -jar " + deployJarFile.getPath();
		File workDir = null;
		Environment env = getApplicationContextEnv();
		String logFilePath = env.getProperty("service.shell.logFile");
		File logFile = new File(Objects.requireNonNull(logFilePath));
		ToolkitIO.touchPath(logFile.getParent());

		// create a service monitor function to check service for "Up" after
		// starting it
		Function<Object, Integer> servMonitorFunc = null;
		try {
			servMonitorFunc = new ServiceMonitor(serviceUrl, MonitorAction.health,
					ServiceMonitor.healthContentHandler)
					.getMonitorFunc();
		} catch (URISyntaxException e) {
			// should not happen
		}

		boolean waitFor = false;
		boolean isServiceStared = ToolkitIO.executeSysCommand(cmd, workDir, logFile, waitFor, servMonitorFunc);

		if (isServiceStared)
			logger.info("Service execution: completed");
		else
			logger.info("Service execution: NOT completed (successfully)");

		return isServiceStared;
	}

	/**
	 * @effects Register <tt>deployServName</tt> as the child of <tt>targetServ</tt>.
	 * Return the {@link HttpStatus}.
	 *
	 * @version 1.0
	 */
	private HttpStatus registerAsChild(String deployServName, String targetServ) {
		String targetServUrl = gatewayServer + "/" + targetServ;
		String registerChildPath = targetServUrl + "/registerChildService";

		// send a request to targetServ
		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity =
				getMultipartRequestEntityWithParams("childName", deployServName);

		ResponseEntity<String> restExchange = restTemplate.exchange(registerChildPath, HttpMethod.POST, reqEntity, String.class);

		return restExchange.getStatusCode();
	}

	/**
	 * @effects
	 *	perform post-tasks relating to the promotion of <tt>module</tt>. This includes removing <tt>module</tt> from the service tree of <tt>sourceServ</tt>
	 *
	 * @version 1.0
	 */
	public boolean promoteCompleted(String sourceServ, ModuleDesc module) {
		// send a removeModule(module) request to sourceServ
		String pathAction = "removeModule";
		String fullTargetURL = ControllerTk.getServicePath(gatewayServer, sourceServ, pathAction);

		URI uri = null;
		try {
			uri = new URI(fullTargetURL);
		} catch (URISyntaxException e) {
			// should not happen
		}

		RequestEntity reqEntity = RequestEntity.post(uri).body(module);

		ResponseEntity<String> restExchange = restTemplate.exchange(reqEntity, String.class);

		return restExchange.getStatusCode().equals(HttpStatus.OK);
	}

	private boolean demoteCompleted(String sourceServ, ModuleDesc module) {
		// send a removeModule(module) request to sourceServ
		String pathAction = "unregisterService";
		String fullTargetURL = ControllerTk.getServicePath(gatewayServer, sourceServ, pathAction);

		URI uri = null;
		try {
			uri = new URI(fullTargetURL);
		} catch (URISyntaxException e) {
			// should not happen
		}

		RequestEntity reqEntity = RequestEntity.post(uri).body(module);

		ResponseEntity<String> restExchange = restTemplate.exchange(reqEntity, String.class);

		return restExchange.getStatusCode().equals(HttpStatus.OK);
	}

	private String getServiceUrl(String serviceName) {
		String serviceUrl = gatewayServer + "/" + serviceName;
		return serviceUrl;
	}

	protected HttpEntity<LinkedMultiValueMap<String, Object>> getMultiPartRequestFromModuleDesc(String targetServ, ModuleDesc module) {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		map.add("targetServ", targetServ);
		map.add("pid", module.getPid());
		map.add("module", module.getModule());
		map.add("service", module.getService());

		if (module.getParentModule()  != null) {
			map.add("parentModule", module.getParentModule());
		}

		map.add("file", new FileSystemResource(new File(module.getJarFile())));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		return requestEntity;
	}

	/**
	 * A more general version of {@link #getMultiPartRequestFromModuleDesc(String, ModuleDesc)} that supports arbitrary array of name-value pairs
	 *
	 * @effects
	 *	create and return a {@link HttpEntity} representing the HTTP request, containing parameters
	 * specified by <tt>nameValuePairs</tt>.
	 * @version 1.0
	 */
	protected HttpEntity<LinkedMultiValueMap<String, Object>> getMultipartRequestEntityWithParams(String...nameValuePairs) {
		if (nameValuePairs == null || nameValuePairs.length == 0 || (nameValuePairs.length % 2 != 0)) {
			throw new IllegalArgumentException(Arrays.toString(nameValuePairs));
		}

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		for (int i = 0; i < nameValuePairs.length; i=i+2) {
			map.add(nameValuePairs[i], nameValuePairs[i+1]);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		return requestEntity;
	}

	/**
	 * This is the reverse of {@link #getModuleNameFromService(String)}
	 * @effects
	 *	return the standard service name from the specified <tt>module</tt> name.
	 *
	 * @version 1.0
	 */
	private String getServiceNameFromModule(String module) {
		return module.toLowerCase() + "-service";
	}

	/**
	 * @requires
	 *	<tt>servName</tt> follows the standard service naming convention: <tt>xyz-service</tt>
	 *
	 * @effects
	 *	extract the service name from <tt>servName</tt> and return it as the module name
	 *
	 * @version 1.0
	 */
	private String getModuleNameFromService(String servName) {
		if (servName.endsWith("-service")) {
			return servName.substring(0,servName.indexOf("-service"));
		} else {
			return servName;
		}
	}
	
	private File getDeployPath(String deployedArtifactName, boolean sourceOrTarget) {
		Environment env = getApplicationContextEnv();
		return ControllerTk.getDeployPath(env, deployedArtifactName, sourceOrTarget);
		/*String propName = sourceOrTarget ? "path.service.deploySource" : "path.service.deployTarget";
		String serviceDeployPath = env.getProperty(propName);
		File dservDeployPath = new File(serviceDeployPath, deployedArtifactName);

		return dservDeployPath;*/
	}

	/**
	 * @effects
	 * 	requests service <tt>sourceServ</tt> for the URL of the <tt>reconfigurer-service</tt> running on its machine,
	 * 	and return the URL as output
	 */
	protected abstract String lookUpReconfigurer(String sourceServ);

	/**
	 * @effects
	 *	return the run-time {@link ApplicationContext} of this
	 * @version 1.0
	 */
	protected abstract Environment getApplicationContextEnv();

}
