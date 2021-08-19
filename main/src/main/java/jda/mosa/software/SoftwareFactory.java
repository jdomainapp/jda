package jda.mosa.software;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dodm.DODMBasic;
import jda.modules.jdatool.DomainAppTool;
import jda.modules.jdatool.setup.DomainAppToolSetUpGen;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.setup.SetUpFactory;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.module.Module;
import jda.mosa.software.aio.SoftwareAio;
import jda.mosa.software.aio.SoftwareStandardAio;
import jda.mosa.software.impl.DomSoftware;
import jda.mosa.software.impl.UISoftware;
import jda.mosa.software.impl.WebSoftware;
import jda.util.SwTk;

/**
 * @overview 
 *  Represents the factory for {@link Software} and {@link Module}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class SoftwareFactory {

  private static final boolean debug = Toolkit.getDebug(SoftwareFactory.class);

  /**
   * @requires 
   *  systemCls contains module configuration classes /\ the first of these is the config of the main module 
   * 
   * @effects 
   *  Create and return a {@link SoftwareAio} whose configuration is specified by <tt>systemCls</tt>, 
   *  whose setup-cls is specified by {@link SetUpGen}
   *  and will be stored in the memory.
   *   
   * @version 5.2
   */
  public static SoftwareAio createSoftwareAioWithMemoryBasedConfig(Class systemCls) {
    // create the software configuration object
    final Class SetUpCls = SetUpGen.class;
    
    SetUpBasic.setSystemProperty(PropertyName.setup_SerialiseConfiguration, "false");
    
    SoftwareAio sw = new SoftwareStandardAio(SetUpCls, systemCls);
    
    return sw;
  }
  

  /**
   * @effects 
   *  Create and return a {@link SoftwareAio} whose configuration is specified by <tt>systemCls</tt>, 
   *  whose setup-cls is specified by {@link SetUpGen}.
   *  
   * @version 5.2 
   */
  public static SoftwareAio createStandardSoftwareAio(Class systemCls) {
    final Class SetUpCls = SetUpGen.class;

    return createStandardSoftwareAio(systemCls, SetUpCls);
  }
  
  /**
   * @effects 
   * 
   */
  public static SoftwareAio createStandardSoftwareAio(Class sysCls,
      Class<? extends SetUpGen> setUpCls) {
    SoftwareAio sw = new SoftwareStandardAio(setUpCls, sysCls);
    
    return sw;    
  }

  
  /**
   * @requires 
   *  systemCls contains module configuration classes /\ the first of these is the config of the main module 
   * 
   * @effects 
   *  Create and return a {@link Software} whose configuration is specified by <tt>systemCls</tt>
   *  and will be stored in the memory.
   *   
   * @version 4.0
   * @deprecated as of v5.2. Use {@link #createSoftwareAioWithMemoryBasedConfig(Class)} instead.
   */
  public static Software createSoftwareWithMemoryBasedConfig(Class systemCls) throws NotFoundException, DataSourceException {
    // create the software configuration object
    SetUp su = SetUpFactory.createSetUpWithMemoryBasedConfig(systemCls); // create a memory-based set-up object    
    
    List<List<Class>> cfgClsList = su.getModuleDescriptors();
    
    if (cfgClsList == null || cfgClsList.isEmpty())
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, new Object[] {SoftwareFactory.class.getSimpleName(), "createSoftware", systemCls.getSimpleName()});
    
    List<Class> moduleCfgClses = cfgClsList.get(0); //su.getModuleDescriptors().get(0);
    
    // ASSUME: main module is the first module
    Class mainModuleCfgCls = moduleCfgClses.get(0);
    Class[] nonMainModuleCfgClses = moduleCfgClses.subList(1, moduleCfgClses.size()).toArray(new Class[moduleCfgClses.size()-1]);
    
    return createSoftware(su, mainModuleCfgCls, nonMainModuleCfgClses);
  }
  
  /**
   * @effects 
   *  Create and return a {@link Software} whose entire database (incl. configuration and data) are 
   *  stored in memory.
   *   
   * @version 4.0
   */
  public static Software createDefaultSoftware(Class mainModuleCfgCls, Class[] nonMainModuleCfgClasses) throws NotFoundException, DataSourceException {
    // create the software configuration object
    SetUp su = SetUpFactory.createDefaultSetUp(); // create a memory-based set-up object    
    
    return createSoftware(su, mainModuleCfgCls, nonMainModuleCfgClasses);
  }

  /**
   * @effects 
   *  Create and run a {@link DomainAppTool} on the selected domain classes.
   *  
   *  Throws Exception if failed.
   *   
   * @version 5.2c
   */
  public static void createSoftwareToolWithMemoryBasedConfig(Class[] domainClasses) {
    SwTk.setSystemProperty(PropertyName.setup_SerialiseConfiguration, "false");
    SwTk.setSystemProperty(PropertyName.Logging, "true");
    DomainAppTool.run(domainClasses);
  }
  
  /**
   * @effects 
   *  Create and return a {@link Software} whose database (incl. configuration and data) are 
   *  stored in a pre-defined type of relational database.
   *   
   * @version 4.0
   */
  public static Software createStandardSoftware(Class mainModuleCfgCls, Class[] nonMainModuleCfgClasses) throws NotFoundException, DataSourceException {
    SetUp su = SetUpFactory.createStandardSetUp(); // create a standard set-up object    
    
    Software sw = createSoftware(su, mainModuleCfgCls, nonMainModuleCfgClasses);
    
    return sw;
  }
  
  /**
   * @effects 
   *  Create and return a {@link Software} whose configuration is managed by <tt>su</tt> and whose 
   *  modules are specified in <tt>mainModuleCfgCls, nonMainModuleCfgClasses</tt> 
   *   
   * @version 4.0
   */
  private static Software createSoftware(final SetUp su, Class mainModuleCfgCls, Class...nonMainModuleCfgClasses) 
  throws NotFoundException, NotPossibleException, DataSourceException {
    Software sw;
    Collection<ApplicationModule> moduleCfgs;
//    sw = new Software(su);
//    
//    if (su.existsSoftware()) {
//      // software already exists, load it
//      // su.createInitApplicationConfiguration();
//      
//      // load the module configs
//      moduleCfgs = loadModuleConfigs(sw, mainModuleCfgCls, nonMainModuleCfgClasses);
//      
//    } else {
      // software not yet exists, create new
    su.createApplicationConfiguration();
    
    // create software 
    sw = new Software(su);
    
    // create the configuration schema (in the data source)
    sw.initConfig();

    // create all the module configs
    moduleCfgs = createModuleConfigs(sw, mainModuleCfgCls, nonMainModuleCfgClasses);
//    }
    

    // create the Modules (after all module configs have been created!)
    ApplicationModule mainModuleCfg = moduleCfgs.iterator().next();
    
    ApplicationModule[] otherModuleCfgs = null;
    if (moduleCfgs.size() > 1) {
      otherModuleCfgs = new ApplicationModule[moduleCfgs.size()-1];
      int index = 0;
      for (ApplicationModule mcfg : moduleCfgs) {
        if (index == 0) {
          index++;
          continue; // ignore: main module config
        }
        otherModuleCfgs[index-1] = mcfg;
        index++;
      }
    }

    createSoftwareModules(sw, mainModuleCfg, otherModuleCfgs);

    return sw;
  }
  
//  /**
//   * @effects 
//   *  load from the underlying data source the configurations of the specified modules of <tt>sw</tt>
//   * @version 4.0
//   */
//  private static Collection<ApplicationModule> loadModuleConfigs(Software sw,
//      Class mainModuleCfgCls, Class[] nonMainModuleCfgClasses) throws NotFoundException, NotPossibleException, DataSourceException {
//    Collection<ApplicationModule> moduleCfgs = new ArrayList<>();
//    
//    /* TODO: load the configurations of the specified modules
//    ApplicationModule mainModuleCfg = loadMainModuleCfg(sw);
//    
//    moduleCfgs.add(mainModuleCfg);
//    
//    loadNonMainModuleCfgs(sw, moduleCfgs);
//    */
//    
//    // TODO: remove this loading when we can selectively load config of the specified modules
//    sw.getSetUp().loadConfiguration();
//
//    ApplicationModule mainModuleCfg = retrieveMainModuleCfg(sw);
//    
//    moduleCfgs.add(mainModuleCfg);
//    
//    retrieveNonMainModuleCfgs(sw, moduleCfgs);
//    
//    return moduleCfgs;
//  }
//
//  /**
//   * @effects 
//   *  retrieve from the data source the configuration of the main module of <tt>sw</tt>
//   * @version 4.0
//   */
//  private static ApplicationModule retrieveMainModuleCfg(Software sw) throws NotFoundException, DataSourceException {
//    DODMBasic dodm = sw.getSetUp().getDODM();
//    
//    Class<ApplicationModule> c = ApplicationModule.class;
//    String attribName = ApplicationModule.AttributeName_type;
//    Op op = Op.EQ;
//    ModuleType val = ModuleType.DomainMain;
//    ApplicationModule module = dodm.getDom().retrieveObject(c, attribName, op, val);
//    
//    if (module == null) {
//      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {Type.Main.name()});
//    } else {
//      return module;
//    }
//  }
//
//  /**
//   * @modifies moduleCfgs
//   * @effects 
//   *   retrieve from the data source configurations of all non-main modules of <tt>sw</tt> and 
//   *   add them to <tt>moduleCfgs</tt>
//   *    
//   * @version 4.0
//   */
//  private static void retrieveNonMainModuleCfgs(Software sw,
//      Collection<ApplicationModule> moduleCfgs) {
//    // TODO
//  }
  
  /**
   * @effects 
   *  create in <tt>sw</tt> the configurations of the modules whose configuration data are specified 
   *  in <tt>mainModuleCfgCls, nonMainModuleCfgClasses</tt>. 
   *  Return the result as {@link Collection}; the first element of which is the config of the 
   *  main module.
   *  
   *  <p>throws NotPossibleException, DataSourceException if failed.
   *  
   * @version 4.0 
   */
  private static Collection<ApplicationModule> createModuleConfigs(Software sw, 
      Class mainModuleCfgCls, Class[] nonMainModuleCfgClasses) throws NotPossibleException, DataSourceException {
    SetUp su = sw.getSetUp();
    List<Class> moduleCfgClses = su.getDependencyModules(nonMainModuleCfgClasses);

    if (moduleCfgClses != null) {
      // add funcModuleCfgClasses to moduleCfgClses
      for (Class fmd : nonMainModuleCfgClasses) {
        if (!moduleCfgClses.contains(fmd)) moduleCfgClses.add(0,fmd);
      }
        
      moduleCfgClses.add(0, mainModuleCfgCls);
    } else {
      moduleCfgClses = new ArrayList<>();
      moduleCfgClses.add(mainModuleCfgCls); Collections.addAll(moduleCfgClses, nonMainModuleCfgClasses);
    }

    Class[] moduleCfgArr = moduleCfgClses.toArray(new Class[moduleCfgClses.size()]);
    
    Collection<ApplicationModule> moduleCfgs = su.createModuleConfigs(moduleCfgArr);
    
    return moduleCfgs;
  }
  
  /**
   * @modifies <tt>sw</tt>
   * @effects 
   *  create in <tt>sw</tt> the main module, whose configuration is defined in <tt>mainModuleCfg</tt>, 
   *  and other non-main modules, whose configurations are defined in <tt>nonMainModuleCfgs</tt> 
   *  
   * @version 4.0
   */
  private static void createSoftwareModules(Software sw, ApplicationModule mainModuleCfg, ApplicationModule[] nonMainModuleCfgs) throws NotPossibleException, DataSourceException {
    final boolean withGUI = false;  // create GUI at run-time when used
    // 2a: create main module
    createMainModule(sw, mainModuleCfg);
    
    if (nonMainModuleCfgs != null) {
      // 2b: create other modules
      createNonMainModules(sw, withGUI, nonMainModuleCfgs);
    }
  }

  
//  /**
//   * @modifies sw
//   * @effects 
//   *  Create and return a {@link Module} in <tt>sw</tt> from its configuration defined by <tt>moduleCfgCls</tt>.
//   *  If this module depends on other modules then create those modules as well.
//   *  
//   * @requires 
//   *  the underlying configuration schema has been created /\ sw has been created by one of the <tt>createX</tt> methods of this 
//   *  
//   * @version 4.0
//   */
//  public static <M extends Module> M createModuleWithDependency(Software sw, Class moduleCfgCls) throws NotPossibleException, DataSourceException {
//    
//    List<Class> moduleCfgs = sw.getSetUp().getDependencyModules(moduleCfgCls);
//
//    List<M> modules;
//    if (moduleCfgs != null) {
//      moduleCfgs.add(0, moduleCfgCls);
//      modules = createModules(sw, moduleCfgs.toArray(new Class[moduleCfgs.size()]));
//    } else {
//      modules = createModules(sw, moduleCfgCls);
//    }
//        
//    return modules.get(0);
//  }
//  
//  /**
//   * @modifies sw
//   * 
//   * @effects 
//   *  create and return {@link Module}[] in <tt>sw</tt>, created from <tt>moduleCfgClasses</tt>    
//   * 
//   * @requires 
//   *  the underlying configuration schema has been created /\ sw has been created by one of the <tt>createX</tt> methods of this 
//   *     
//   * @version 4.0
//   */
//  public static <M extends Module> List<M> createModules(Software sw, Class...moduleCfgClasses) throws DataSourceException,
//      NotPossibleException {
//    SetUp su = sw.getSetUp();
//    Collection<ApplicationModule> moduleCfgs = su.createModuleConfigs(moduleCfgClasses);
//
//    // create the Modules
//    boolean withGUI = true;
//    List<M> modules = createModules(sw, withGUI, moduleCfgs.toArray(new ApplicationModule[moduleCfgs.size()]));
//    
//    return modules;
//  }

 

  /**
   * @param mainModuleCfgCls 
   * @modifies sw
   * 
   * @effects 
   *  load the main module and initialise its <tt>Controller</tt> and gui, 
   *  set this module into <tt>sw</tt>,  
   *  return the <tt>Module</tt> object
   */
  private static Module createMainModule(Software sw,  ApplicationModule module) throws 
  NotFoundException, NotPossibleException, DataSourceException {
    //TODO: create proper Module object when Module is separated from ControllerBasic
    
    DODMBasic dodm = sw.getSetUp().getDODM();
    Configuration config = sw.getSetUp().getConfig();
    
    // create main controller
    ControllerBasic mainCtl = ControllerBasic.createController(dodm, module, null, config);

    // create main gui
    mainCtl.createGUI();
    
    // set into sw
    sw.setMainModule(mainCtl);
    
    return mainCtl;
  }
  
  /**
   * @modifies <tt>sw</tt>
   * @effects <pre>
   *  for each ApplicationModule m in modules
   *    if m has not already been created
   *      creates m's controller and 
   *        if withGUI = true then also m's gui
   *      add m's controller to sw
   *  </pre>
   *  
   *  <p>This method does not touch the modules that have already been created. These modules
   *  are simply hidden from the user.
   *  
   * @version 4.0
   */
  private static <M extends Module> List<M> createNonMainModules(Software sw, boolean withGUI, ApplicationModule...modules) {
    //TODO: create proper Module objects when Module is separated from ControllerBasic
    // main module
    ControllerBasic mainCtl = sw.getMainModule().getController();
    
    DODMBasic dodm = sw.getSetUp().getDODM();
    
    Configuration config = sw.getSetUp().getConfig();

    List<ApplicationModule> composites = new ArrayList();
    List<ControllerBasic> controllers = new ArrayList();
    ControllerBasic controller;
    
    for (ApplicationModule moduleCfg : modules) {
      if (mainCtl.getContext().lookUpModule(moduleCfg)) {
        // already created: ignore
        if (debug)
          System.out.printf("...Mô-đun đã được tạo (bỏ qua): %s%n", moduleCfg.getName());
        continue;
      }
      
      if (moduleCfg.isComposite()) {
        composites.add(moduleCfg);
        continue;
      }     
      
      // create controller
      // get the gui region for this module may be null
      if (debug)
        System.out.println("..." + moduleCfg);

      if (moduleCfg.hasController()) { // v2.7.2
        controller = ControllerBasic.createController(dodm, moduleCfg, mainCtl, config);
  
        if (debug)
          System.out.println("......: " + controller);
  
        sw.addNonMainModule(controller);
        
        controllers.add(controller);
      }
    } // end modules

    // create module GUIs for non-composite controllers
    /*v2.7.4: add check */
    if (withGUI) {
      if (debug)
        System.out.println("Tạo giao diện các mô-đun");
      Iterator<ControllerBasic> cit = controllers.iterator();
      while (cit.hasNext()) {
        controller = cit.next();
        if (controller.hasGUI()) { // v3.2: getGUI() != null) {
          if (debug)
            System.out.println("..." + controller.getName());
          controller.createGUI();
          controller.postCreateGUI();
        }
      }
    }
    // v3.2: added this case to support modules whose views that are created at start-up
    else { // withGUI = false
      Iterator<ControllerBasic> cit = controllers.iterator();
      while (cit.hasNext()) {
        controller = cit.next();
        if (controller.hasGUI() &&
            controller.getGUI().getGUIConfig().getProperty(PropertyName.view_createOnStartUp, Boolean.class, Boolean.FALSE)) { 
          // create on start-up 
          if (debug)
            System.out.println("...tạo giao diện: " + controller.getName());
          controller.createGUI();
          controller.postCreateGUI();
        }
      }      
    }
    
    // initialise composite controllers last because they may need to access 
    // data controllers of the other controllers
    if (debug)
      System.out.println("Tạo các mô-đun phức hợp");
    
    for (ApplicationModule module : composites) {
      if (debug)
        System.out.println("..." + module);
      
      if (module.hasController()) { // v2.7.2
        controller = ControllerBasic.createController(dodm, module, mainCtl, config);
        sw.addNonMainModule(controller);

        /*v2.7.4: add check */
        if (withGUI) {
          if (controller.hasGUI()) { //v3.2: getGUI() != null) {
            if (debug)
              System.out.println("-> giao diện: " + controller.getName());
            controller.createGUI();
            controller.postCreateGUI();
          }
        }// v3.2: added this case to support modules whose views that are created at start-up
        else { // withGUI = false
          if (controller.hasGUI() &&
              controller.getGUI().getGUIConfig().getProperty(PropertyName.view_createOnStartUp, Boolean.class, Boolean.FALSE)) { 
            // create on start-up 
            if (debug)
              System.out.println("...tạo giao diện: " + controller.getName());
            controller.createGUI();
            controller.postCreateGUI();
          }
        }
      }
    }
    
    // return result
    if (controllers.isEmpty())
      return null;
    else
      return (List<M>)controllers;
  }

  /**
   * @effects 
   *  create and return a default software object
   */
  public static DomSoftware createDefaultDomSoftware() {
    return new DomSoftware();
  }

  /**
   * @effects 
   *  create and return a standard DOM software object from the configuration recorded in <code>scc</code>
   * @version 5.4.1
   */
  public static DomSoftware createStandardDomSoftware(Class scc) {
    return new DomSoftware(scc);
  }
  
  /**
   * @effects 
   *  create and return a standard Swing-based software object
   */
  public static UISoftware createUIDomSoftware() {
    return new UISoftware(null, DomainAppToolSetUpGen.class);
  }
  
//  /**
//   * @effects 
//   *   create and return a web-based software object  
//   */
//  public static WebSoftware createWebSoftware(Class scc) {
//    // TODO: use a standard SCC 
//    // return new WebSoftware(DomainAppToolSetUpGen.class);
//    throw new NotImplementedException("Not yet implemented");
//  }
}
