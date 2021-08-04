package jda.mosa.software;

import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.mosa.controller.ControllerBasic;
import jda.util.SwTk;

/**
 * @overview
 *  Launch an application given its <tt>SetUp</tt> object.
 *  
 * @author dmle
 */
//v2.7.3: public final class ApplicationLauncher 
public class ApplicationLauncher {
  private SetUpBasic su;
  private ControllerBasic main;
  private ControllerBasic splashScreen;
 
  private static final boolean debug = Toolkit.getDebug(ApplicationLauncher.class);
  private static final boolean loggingOn = Toolkit.getLoggingOn(ApplicationLauncher.class);

  // v2.7.3: private ApplicationLauncher(SetUp s) {
  public ApplicationLauncher(SetUpBasic s) {
    su = s;
  }
  
  /**
   * @effects 
   *  start main GUI and a default child window (if specified)
   * @deprecated as of v2.7.2 
   */
  private void start() {
    main.showGUI();
    
    // initialise security
    main.initSecurity();
    
    // show the default GUI
    if (main.isLoggedIn())
      main.runDefaultModule();
  }

  /**
   * @effects 
   *  start main GUI and a default child window (if specified) 
   */
  protected void startMain() {
    main.showGUI();    
  }

  /**
   * @effects
   *  if splashScreen is not null
   *    splashScreen.showGUI
   */
  private void startSplashScreen() {
    if (splashScreen != null)
      splashScreen.run();
  }

  protected void startSecurity() {
    main.initSecurity();
  }
  
  /**
   * @effects
   *  a part of {@link #launchCommon()} which performs tasks, such as creating functional modules.
   * @version 
   * - 5.2: support startUpModules  
   */
  protected void postLogin() throws NotFoundException, DataSourceException {
    // finalise the main gui
    main.postCreateGUI();    
    
    createFunctionalModules();
    
    // v2.7.3: update main GUI again after creating the functional modules 
    main.postCreateFunctionalModules();

    if (main.isSecurityEnabled())
      main.postLogin();
    else 
      main.startUpModules(); // v5.2
    
    if (main.isLoggedIn())
      main.runDefaultModule();
  }
  
//  /**
//   * @effects 
//   *  finalise the main GUI 
//   */
//  private void finaliseMain() {
//    main.postCreateGUI();    
//  }
//
//  private void runDefaultModule() {
//    // show the default GUI
//    if (main.isLoggedIn())
//      main.runDefaultModule();
//  }
  
  private void loadBaseConfiguration() throws DataSourceException {
    su.loadBaseConfiguration();
  }
  
  /**
   * @requires
   *  su != null
   *  
   * @effects 
   *  load configuration from data source
   */
  private void loadApplicationConfiguration() throws NotPossibleException, NotFoundException, DataSourceException {
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Đăng ký mô hình"
          "Loading application configuration"
          );
    su.loadConfiguration();
  }  
  
  /**
   * @effects 
   *  load and initialise the main module
   */
  protected void createMainModule() throws NotFoundException, DataSourceException {
    DODMBasic schema = su.getDODM();

    // application configuration
    Configuration config = su.getConfig();

    //debug: 
    //System.out.println(schema.getDom().getObjects(ApplicationModule.class));

    main = SwTk.createMainModule(schema, config);
  }

  /**
   * @effects 
   *  load and initialise security modules
   */
  protected void createSecurityModules() throws NotFoundException, DataSourceException {
    SwTk.createSecurityModules(main);
  }
  
  /**
   * @effects 
   *  load and initialise the system modules
   */
  protected void createSystemModules() throws NotFoundException, DataSourceException {
//    System.out.printf("loading system modules: %n  filter list:%n  %s%n", 
//        main.getDodm().getDom().getObjects(ApplicationModule.class));
    
    SwTk.createSystemModules(main);

//    System.out.printf("loaded...: %n  filter list:%n  %s%n", 
//        main.getDodm().getDom().getObjects(ApplicationModule.class));
  }
  
  /**
   * @effects 
   *  if user specified input domain classes for the application modules
   *    create functional modules that use these classes and that user has permission for
   *  else
   *    create all functional modules that user has permission for
   */
  private void createFunctionalModules() throws NotFoundException, DataSourceException {
    Class[] inputDomainClasses = su.getInputModelClasses();
    if (inputDomainClasses == null) {
      // no input domain classes, create all modules
      createAllFunctionalModules();
    } else {
      // only create the modules that use the specified domain classes
      createSelectedFunctionalModules(inputDomainClasses);
    }
  }
  
  /**
   * @requires 
   *  main module has been created
   *  
   * @effects 
   *  if security is enabled
   *    load and initialise <b>all</b> the functional modules that the user has permissions for
   *  else
   *    load and initialise <b>all</b> functional modules
   */
  private void createAllFunctionalModules() throws NotFoundException, DataSourceException {
    SwTk.createAllFunctionalModules(main);
  }  

  /**
   * @requires 
   *  main module has been created /\ inputDomainClasses != null
   *  
   * @effects 
   *  if security is enabled
   *    load and initialise the <b>selected</b> functional modules that the user specified as input and that she  
   *    has permissions for
   *  else
   *    load and initialise the <b>selected</b> functional modules that the user specified as input
   *  
   *  <p>Throws IllegalArgumentException if no input domain classes were specified
   */
  private void createSelectedFunctionalModules(Class[] inputDomainClasses) throws IllegalArgumentException, NotFoundException, DataSourceException {
    SwTk.createFunctionalModules(main, inputDomainClasses);
  }  

  /**
   * @effects 
   *  if ModuleSplashScreen is defined in data source
   *    load and initialise it as <tt>splashScreen</tt>
   *  else
   *    do nothing
   */
  private void createSplashScreenModule() throws NotFoundException, NotPossibleException, DataSourceException {
    //debug: 
    //System.out.println(schema.getDom().getObjects(ApplicationModule.class));

    boolean serialised = false;
    splashScreen = SwTk.createModuleWithView(main, SplashInfo.class, serialised);    
  }
  
  /**
   * @effects creates all the child program modules of <code>this</code>
   * @version 2.7.1
   * @deprecated as of v2.7.2
   */
  private void createModules() throws DataSourceException, NotFoundException {
    final DODMBasic schema = su.getDODM();

    // application configuration
    final Configuration config = su.getConfig();

    // read the module and GUI configurations from the database
    log(MessageCode.UNDEFINED,
        //"Tạo các mô-đun"
        "Creating application modules"
        );

    Collection<ApplicationModule> modules = schema.getDom().getObjects(ApplicationModule.class);

    if (modules == null || modules.isEmpty()) {
      throw new NotFoundException(NotFoundException.Code.SETTINGS_NOT_FOUND,
          "Không tìm thấy cấu hình mô-đun chương trình");
    }

    // creating the module main and the functional modules
    List<ControllerBasic> controllers = new ArrayList();
    //Type type;
    ControllerBasic controller;

    /**
     * (1) create the main controller 
     * (2) loop through to create other controllers:
     *    - create non-composite controller first
     *    - then create composite controllers 
     */
    // main module
    ApplicationModule moduleMain = null;
    OUTER: for (ApplicationModule module : modules) {
      if (module.getType().isType(ModuleType.DomainMain)) {
        // found main
        if (debug)
          log(MessageCode.UNDEFINED,"...{0}",module);
        
        main = ControllerBasic.createController(schema, module, null, config);
        
        if (debug)
          log(MessageCode.UNDEFINED,"......: {0}", main);

        controllers.add(main);
        moduleMain = module;
        break OUTER;
      }
    } // end main controller
    
    // v2.6.4.b: add check for main
    if (main == null) {
      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, 
          "Không tìm thấy mô-đun {0}", RegionType.Main.name());
    }
    
    // create non-main controllers and record 
    // all composite ones in a list to create later
    List<ApplicationModule> composites = new ArrayList();
    for (ApplicationModule module : modules) {
      if (module.isComposite()) {
        composites.add(module);
        continue;
      }
      
      // module main already processed
      if (module == moduleMain) {
        continue;
      }
      
      // create controller
      // get the gui region for this module may be null
      if (debug)
        log(MessageCode.UNDEFINED,"...{0}", module);

      controller = ControllerBasic.createController(schema, module, main, config);

      if (debug)
        log(MessageCode.UNDEFINED,"......: {0}", controller);

      controllers.add(controller);
    } // end modules

    // create module GUIs for non-composite controllers
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Tạo giao diện các mô-đun"
          "Creating user interface"
          );
    Iterator<ControllerBasic> cit = controllers.iterator();
    while (cit.hasNext()) {
      controller = cit.next();
      if (controller.getGUI() != null) {
        if (debug)
          log(MessageCode.UNDEFINED,"...{0}", controller.getName());
        controller.createGUI();
      }
    }
    
    // initialise composite controllers last because they may need to access 
    // data controllers of the other controllers
    log(MessageCode.UNDEFINED,
        //"Tạo các mô-đun phức hợp"
        "Creating composite modules"
        );
    for (ApplicationModule module : composites) {
      if (debug)
        log(MessageCode.UNDEFINED,"...{0}", module);
      
      controller = ControllerBasic.createController(schema, module, main, config);
      
      if (controller.getGUI() != null) {
        if (debug)
          log(MessageCode.UNDEFINED,
              //"-> giao diện: {0}"
              "-> UI: {0}"
              , controller.getName());
        controller.createGUI();
      }
    }
  }

  /**
   * @effects 
   *  return the main <tt>Controller</tt> of the application
   * @version 2.6.4.b
   */
  protected ControllerBasic getMainController() {
    return main;
  }
  
  /**
   * @version 2.7.3:
   */
  public ControllerBasic launch(SetUpBasic su, String lang)  
      throws Exception {

    /*v2.7.4: catch exception to display error messages 
    */
    try {
      if (su == null) {
        throw new IllegalArgumentException("Setup object is not specified");
      }
      
      // command line args takes precedence over JVM's arguments
      if (lang != null) {
        System.setProperty("Language", lang);
      }
  
      loadBaseConfiguration();
      
      loadApplicationConfiguration();
      
      // v2.7.3: pre-run conditions
      preLaunch();
      
      launchCommon(); 
  
      // v2.7.3: post-run conditions
      postLaunch();
  
      // v2.6.4.b: added return of this controller
      return getMainController();
    } catch (IllegalArgumentException ex) {
      ApplicationRuntimeException e = new ApplicationRuntimeException(MessageCode.FAIL_TO_LAUNCH_APPLICATION, ex, new Object[] {""});
      ControllerBasic.displayIndependentError(e);
      throw ex;
    } 
    //v3.1: catch (ApplicationException | ApplicationRuntimeException e) {
    catch (Exception e) {
      ControllerBasic.displayIndependentError(e);
      throw e;      
    }
  }
  
  /**
   * @requires 
   *  {@link #loadApplicationConfiguration()} has been performed.
   *  
   * @version 
   *  - 2.7.3
   */
  protected void preLaunch() {
    // run pre-conditions
//    log(MessageCode.UNDEFINED, 
//        //"Cài đặt dữ liệu khởi tạo...."
//        "Pre-launch..."
//        );
  }
  
  /**
   * @requires 
   *  {@link #loadApplicationConfiguration()} has been performed
   *  
   * @effects 
   *  perform common launch tasks
   */
  protected void launchCommon() throws NotFoundException, DataSourceException {
    // main module
    createMainModule();
    
    startMain();
    
    // security modules
    createSecurityModules();

    startSecurity();

    // v2.7.4: splash screen (if any) 
    createSplashScreenModule();
    
    startSplashScreen();

    // system modules
    createSystemModules();
    
    // functional modules and other tasks that are performed after security is passed
    postLogin();    
  }

  // v2.7.3: specialise this for each module
  protected void postLaunch() {
    // run pre-conditions
//    log(MessageCode.UNDEFINED, 
//        //"Cài đặt dữ liệu khởi tạo...."
//        "Post-launch..."
//        );
  }
  
  /**
   * Run an application with a set-up object.
   *  
   * @effects 
   *  if <tt>lang != null</tt> set system property <tt>Lang</tt> to <tt>lang</tt>, 
   *  initialise a <tt>Runner</tt> object with <tt>su</tt>, 
   *  prepare the application resources and run the application.
   *  
   *  <p>If succeeded, return the main <tt>Controller</tt> of the application.
   */
  public static ControllerBasic run(SetUpBasic su, String lang) 
      throws Exception //IllegalArgumentException, DataSourceException 
      {
    ApplicationLauncher r = new ApplicationLauncher(su);
    return r.launch(su, lang);
  }
  
  /*
  // not yet tested
  private void createMainModule() throws NotPossibleException, DBException {
    // application configuration
    Configuration config = su.getConfiguration();
    
    // domain schema
    DomainSchema schema = su.getDomainSchema();
    
    ApplicationModule module = su.loadMainModule();
    
    if (module == null) {
      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, 
          "Không tìm thấy mô-đun {0}", Type.Main.name());
    }
    
    if (debug)
      System.out.println("..." + module);
    
    main = Controller.createController(schema, module, null, config);
    main.createGUI();
  }
  
  // not yet tested
  private void createFunctionalModules() throws DBException {

    Collection<ApplicationModule> modules = su.loadFunctionalModules();
    if (modules == null)  // no modules
      return;
    
    // application configuration
    Configuration config = su.getConfiguration();
    
    // domain schema
    DomainSchema schema = su.getDomainSchema();
    
    Collection<ApplicationModule> composites = new ArrayList();    
    if (debug)
      System.out.println("Tạo các mô-đun cơ bản");
    
    List<Controller> primitiveControllers = new ArrayList();
    Controller controller;

    for (ApplicationModule module : modules) {
      if (module.isComposite()) {
        composites.add(module);
        continue;
      }
      // create controller
      if (debug)
        System.out.println("..." + module);

      controller = Controller.createController(schema, module, main, config);

      if (debug)
        System.out.println("......: " + controller);

      primitiveControllers.add(controller);
    } // end modules

    // create module GUIs for non-composite controllers
    if (debug)
      System.out.println("...Tạo giao diện các mô-đun cơ bản");
    Iterator<Controller> cit = primitiveControllers.iterator();
    while (cit.hasNext()) {
      controller = cit.next();
      if (controller.getGUI() != null) {
        if (debug)
          System.out.println("......" + controller.getName());
        controller.createGUI();
      }
    }
    
    // initialise composite controllers last because they may need to access 
    // data controllers of the other controllers
    System.out.println("Tạo các mô-đun phức hợp");
    for (ApplicationModule module : composites) {
      if (debug)
        System.out.println("..." + module);
      
      controller = Controller.createController(schema, module, main,config);
      
      if (controller.getGUI() != null) {
        if (debug)
          System.out.println("...giao diện: " + controller.getName());
        controller.createGUI();
      }
    }
  }
  */

  /**
   * @effects 
   *    print log message for <tt>code</tt>
   */
  private void log(MessageCode code, String message, Object...data) {
    if (debug || loggingOn) {
      //TODO: convert message based on language
      if (data != null && data.length > 0) {
        Format fmt = new MessageFormat(message);
        message = fmt.format(data);
        //message = String.format(message, data);
      }
      
      System.out.println(message);
    }
  }
}
