package jda.modules.mosar.software.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import jda.modules.dcsl.util.DClassTk;
import jda.modules.mosar.backend.base.controllers.DefaultNestedRestfulController;
import jda.modules.mosar.backend.base.controllers.DefaultRestfulController;
import jda.modules.mosar.backend.base.services.SimpleDomServiceAdapter;
import jda.modules.mosar.config.GenerationMode;
import jda.modules.mosar.config.LangPlatform;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.software.backend.generators.AnnotationGenerator;
import jda.modules.mosar.software.backend.generators.ServiceTypeGenerator;
import jda.modules.mosar.software.backend.generators.WebControllerGenerator;
import jda.modules.mosar.utils.ClassAssocUtils;
import jda.modules.mosar.utils.InheritanceUtils;

/**
 * @overview 
 *  Executes the backend software automation flow.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class BESoftware {

  private final WebControllerGenerator webControllerGenerator;
  private final ServiceTypeGenerator serviceTypeGenerator;
  private BEGenOutput output;
  
  private Consumer<List<Class>> generateCompleteCallback;
  
  private RFSGenConfig cfg;

  private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool");

  public BESoftware(RFSGenConfig cfg) {

    this.cfg = cfg;
    
    LangPlatform targetType = cfg.getBeLangPlatform();
    GenerationMode generationMode = cfg.getGenMode();
    String outputPackage = cfg.getBeTargetPackage();
    String outputPath = cfg.getBeOutputPath();
    
    this.webControllerGenerator = WebControllerGenerator.getInstance(
            generationMode, outputPackage, targetType, outputPath);
    this.serviceTypeGenerator = ServiceTypeGenerator.getInstance(
            generationMode, outputPackage, outputPath);
  }

  public void setGenerateCompleteCallback(Consumer<List<Class>> generateCompleteCallback) {
      this.generateCompleteCallback = generateCompleteCallback;
  }
  
  /**
   * @modifies domain model (as returned by cfg.getDomainModel())
   * 
   * @effects 
   *  initialises this using <code>cfg</code>
   */
  public BESoftware init() {
    logger.info("Initiating BESoftware...");

    Class[] domainModel = cfg.getDomainModel();

    List<Class<?>> ignored = getIgnoredClasses(domainModel);
    
    logger.info("init(): ignoredClasses = " + ignored);
    
    // A temporary design requirement for Json serialisation from domain objects
    // annotate domain classes with suitable Json annotations
    /* ducmle: feature#55
    AnnotationGenerator anoGen = AnnotationGenerator.instance();
    for (Class cls : domainModel) {
      if (!ignored.contains(cls)) {
        cls = anoGen.generateInheritanceAnnotations(cls);
      }
    }
    */
    
    return this;
  }
  
  /**
   * @effects 
   *  Generates (and compiles) the back-end software and return the compiled classes as {@link BEGenOutput}.
   *  
   *  <p>The source code is saved to the target output package directory specified in <code>cfg</code>. 
   *  
   * @param model 
   */
  public BESoftware generate() {
//    System.out.println("------------");

//    RESTfulBackEndGenerator generator = new RESTfulBackEndGenerator(
//        cfg.getBeLangPlatform(),
//        cfg.getGenMode(),
//        cfg.getBeTargetPackage(),
//        cfg.getBeOutputPath());
//    
//    return generator.run(cfg.getDomainModel());
    logger.info("Generating backend...");

    Class[] domainModel = cfg.getDomainModel();
    
    List<Class<?>> ignored = getIgnoredClasses(domainModel);
    Class<?> tempClass;
    
    final List<Class> generatedControllerClasses = new LinkedList<>();
    final Map<String, Class> generatedServiceClasses = new HashMap<>();
    
    for (Class<?> cls : domainModel) {
        if (ignored.contains(cls)) 
          continue;
//        cls = annotationGenerator.generateCircularAnnotations(cls, classes);
        // ducmle: removed to assume this in the model
//        cls = annotationGenerator.generateInheritanceAnnotations(cls);

        tempClass = serviceTypeGenerator.generateAutowiredServiceType(cls, cfg);
        if (tempClass != null)
          generatedServiceClasses.put(cls.getCanonicalName(), tempClass);
        
        tempClass = webControllerGenerator.getRestfulController(cls, cfg);
        if (tempClass != null)
          generatedControllerClasses.add(tempClass);
        
        List<Class<?>> nestedClasses = ClassAssocUtils.getNested(cls);
        for (Class<?> nested : nestedClasses) {
            if (nested == cls) continue;
            tempClass = webControllerGenerator.getNestedRestfulController(cls, nested, cfg);
            if (tempClass != null)
              generatedControllerClasses.add(tempClass);
        }
    }

    // TODO: deprecated
    List generatedClasses = new ArrayList<>(generatedServiceClasses.values());
    generatedClasses.addAll(generatedControllerClasses);
    
    if (generateCompleteCallback != null)
      onGenerateComplete(generatedClasses);
    // end deprecated
    
    output = new BEGenOutput();
    if (!generatedServiceClasses.isEmpty())
      output.setServices(generatedServiceClasses);
    
    if(!generatedControllerClasses.isEmpty())
      output.setControllers(generatedControllerClasses);
    
    return this;
//    System.out.println("------------");
  }
  
  
  /**
   * Use this method IMMEDIATELY AFTER the generated classes have been compiled by the generator
   * @effects 
   *  run the back-end software after it has been generated by {@link #generate(RFSGenConfig)}.
   */
  public BESoftware run() {
    logger.info("Running backend...");

    if (output == null || output.isEmpty()) {
      logger.info("No backend components to run. Terminating...");
      return null;
    }
    
    final Class[] model = cfg.getDomainModel();
    Collection<Class> comps = output.getComponents();

    logger.debug("model: " + model.length);
    Stream.of(model).forEach(c -> logger.debug(c.getName()));
    
    logger.debug("num-comps: " + comps.size());
    comps.forEach(c -> logger.debug(c.getName()));

    /* ducmle: old code
    Class<? extends BESpringApp> springAppCls = cfg.getBeAppClass();
    // run SpringBoot
    BESpringApp app = DClassTk.createObject(springAppCls, 
        RFSGenConfig.class,
        cfg);
    
    app.run(comps);
    */
    Class<? extends BEApp> appCls = cfg.getBeAppClass();

    BEApp app = DClassTk.createObject(appCls, 
        RFSGenConfig.class,
        cfg);
    
    if (cfg.getBeThreaded()) {
      app.runThreaded(comps);
    } else {
      app.run(comps);
    }
    
    return this;
  }
  
  /**
   * Use this method AFTER the generated classes have been compiled and made available in the class path.
   * @effects 
   *  Run the back end software that was generated by {@link #generate(RFSGenConfig)}.
   *  This is invoked using the same generator configuration and not the generator output. 
   */
  public BESoftware runLater() {
    logger.info("Running backend...");

    String backendTargetPackage = cfg.getBeTargetPackage();
    Class[] model = cfg.getDomainModel();
    
//    System.out.println("model: " + model.length);
//    Stream.of(model).forEach(System.out::println);
    
    // load classes from the backEndPath
    Reflections refl = new Reflections(backendTargetPackage);
    refl.expandSuperTypes();
    Class[] superTypes = {
        SimpleDomServiceAdapter.class,
        DefaultRestfulController.class,
        DefaultNestedRestfulController.class
    };
    
    Collection<Class> comps = new ArrayList<>();
    
    for (Class supType : superTypes) {
      Set<? extends Class> comps1 = refl.getSubTypesOf(supType);
      comps1.stream().filter(c -> 
          c.isAnnotationPresent(Service.class) || 
          c.isAnnotationPresent(RestController.class)
      )
      .forEach(c -> comps.add(c));
    }
    
    logger.debug("num-comps: " + comps.size());
    comps.forEach(c -> logger.debug(c.getName()));
    
    // run SpringBoot
    Class<? extends BEApp> appCls = cfg.getBeAppClass();
    BEApp app = DClassTk.createObject(appCls, 
        RFSGenConfig.class,
        cfg);
    
    if (cfg.getBeThreaded()) {
      app.runThreaded(comps);
    } else {
      app.run(comps);
    }
    
    return this;
  }
  
  /**
   * @effects return output
   */
  public BEGenOutput getOutput() {
    return output;
  }

  private void onGenerateComplete(List<Class> generatedClasses) {
      generateCompleteCallback.accept(generatedClasses);
  }
  
  /**
   * Ignored classes are subclasses of others.
   */
  private static List<Class<?>> getIgnoredClasses(Class[] classes) {
      return Stream.of(classes).map(c -> InheritanceUtils.getSubtypesOf(c))
              .reduce((l1, l2) -> {
                  l1.addAll(l2);
                  return l1;
              }).orElse(List.of());
  }
//  /**
//   * @effects 
//   *  executes SpringBoot application from the specified back-end components
//   *  @deprecated
//   */
//  public void run(Class<?>[] model, Class<?>[] auxModel,
//      String backendTargetPackage
////    ,String backendSrcPath
//      ) {
//    // initialisation
//    init(model, auxModel);
//    
//    // load classes from the backEndPath
//    Reflections refl = new Reflections(backendTargetPackage); 
//    Set<? extends Class> comps = refl.getSubTypesOf(Class.class);
//    
//    // run SpringBoot
//    new BackendMain(model).run(comps);
//  }
//
//  /**
//   * Similar to {@link RFSGen#init}
//   *  @deprecated
//   *  
//   */
//  private void init(Class<?>[] model, Class<?>[] auxModel) {
//    DomainTypeRegistry regist = DomainTypeRegistry.getInstance();
//    regist.addDomainTypes(model);
//    for (Class<?> other : auxModel) {
//      regist.addDomainType(other);
//    }
//  }
}
