package jda.mosa.software;

import java.util.ArrayList;
import java.util.List;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.jdatool.DomainAppToolSoftware;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.module.Module;

/**
 * @overview 
 *  Represents software. A software is composed of {@link Module}s.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0 
 */
public class Software {

  private SetUp su;
  private Module mainModule;
  private List<Module> nonMainModules;
  
  // helpers
  /** used by {@link #run()} to launch this*/
  private SoftwareLauncher launcher;

  /**
   * @requires su has been initialised with a data source configuration 
   * 
   * @effects 
   *  initialise this with <tt>su</tt>, 
   *  connect to the underlying data source (if specified), 
   *  and register the configuration schema (if not already)
   *  
   *  <p>throws NotPossibleException if failed for some reasons.
   *  @version 
   *  - 5.1c: improved to display error dialog before throwing exception
   */
  public Software(SetUp su) throws NotPossibleException {
    this.su = su;
    
    nonMainModules = new ArrayList<>();
    
    try {
      // connect to data source
      connectDataSource();
      
      // register the configuration schema
      su.registerConfigurationSchema();
    } catch (Exception e) {
      // v5.1: added error dialog before throwing exception 
      ControllerBasic.displayIndependentError(e);
      
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONNECT_DB, e);
    }
  }


  /**
   * @effects 
   *  return this.{@link #su} 
   */
  public SetUp getSetUp() {
    return su;
  }

//  /**
//   * This method is <b>ONLY</b> used for programs whose <tt>Configuration</tt> does not use 
//   * a data source to store objects (i.e. all objects are stored only in memory).
//   * 
//   * <br>It differs from {@link #exec(Class, String[])} in that it calls the {@link Cmd#Configure}
//   * command on the <tt>SetUp</tt> object before running the program with that object. 
//   * 
//   * @effects 
//   * <pre>
//   *  let su = suCls.newInstance
//   *  <b>call su.run(Cmd.Configure, args);</b>
//   *  
//   *  if <tt>args != null</tt>
//   *    run <tt>this</tt> using <tt>su</tt> and with <tt>args</tt> as command line arguments
//   *  else 
//   *   run <tt>this</tt> using <tt>su</tt> and without command line arguments
//   *  
//   *  </pre>
//   *  
//   */
//  public final void runNonSerialised(String[] args) {
//    ApplicationLauncher launcher;
//
//    try {
//      // run all program modules in memory (including the domain modules)
//  
//      // configure the program modules
//      su.run(Cmd.Configure, args);
//      
//      // if there are security then configure the security 
//      if (su.getConfig().getUseSecurity()) {
//        su.run(Cmd.ConfigureSecurity, args);
//      }
//      
//      launcher = new ApplicationLauncherLight(su);
//      
//      String lang = null;
//      launcher.launch(su, lang);
//    } catch (Exception e) {
//      //v3.1: no need to display error here as this is already 
//      // handled by other components 
//      // ControllerBasic.displayIndependentError(e);
//      System.exit(1);
//    }
//  }
  
  /**
   * @effects 
   *  initialise the base configuration of this software 
   *  
   *  <p>throws NotPossibleException if failed.
   */
  public void initConfig() throws NotPossibleException {
    // create base configuration schema
    try {
      su.createBaseConfiguration();
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, e, new Object[] {"Software.init"});
    }
  }

  /**
   * @modifies this
   * @effects 
   *  add <tt>module</tt> to this.{@link #nonMainModules}
   * @version 4.0
   */
  public void addNonMainModule(Module module) {
    nonMainModules.add(module);
  }
  
  /**
   * @effects 
   *  create a connection to the underlying data source (if not already)
   *  
   *  <p>throws DataSourceException if failed
   */
  public void connectDataSource() throws DataSourceException {
    if (!su.isDodmInit()) {
      su.initDODM();
    }
  }
  
  /**
   * @effects 
   * <pre>
   *  let suCls = set-up class specified by system property \/ defaultSetUpCls
   *  let su = suCls.newInstance
   *  if <tt>args != null</tt>
   *    run <tt>this</tt> using <tt>su</tt> and with <tt>args</tt> as command line arguments
   *  else 
   *   run <tt>this</tt> using <tt>su</tt> and without command line arguments
   *  
   *  </pre>
   * @version 3.0
   *  - support setup-class from system property
   */
  public final void run(String[] args) throws NotPossibleException {
    String lang = null;
    
    try {
      if (!su.isSerialisedConfiguration()) {
        // run all program modules in memory (the module data may still be serialised)
        
        if (su.getConfig().getUseSecurity()) {
          su.run(Cmd.ConfigureSecurity, args);
        }
      } 
    } catch (Exception e) {
      //v3.1: no need to display error here as this is already 
      // handled by other components 
      // ControllerBasic.displayIndependentError(e);
      System.exit(1);
    }
    
    if (launcher == null) 
      launcher = new SoftwareLauncher(this);

    launcher.launch();
  }
  
  /**
   * @effects 
   *  execute this to show the main GUI
   */
  public final void run() throws NotPossibleException {
    run(null);
  }

  /**
   * This method is <b>ONLY</b> used for programs whose <tt>Configuration</tt> does not use 
   * a data source to store objects (i.e. all objects are stored only in memory).
   * 
   * <br>It differs from {@link #exec(Class, String[])} in that it calls the {@link Cmd#Configure}
   * command on the <tt>SetUp</tt> object before running the program with that object. 
   * 
   * @effects 
   * <pre>
   *  let su = suCls.newInstance
   *  <b>call su.run(Cmd.Configure, args);</b>
   *  
   *  if <tt>args != null</tt>
   *    run <tt>this</tt> using <tt>su</tt> and with <tt>args</tt> as command line arguments
   *  else 
   *   run <tt>this</tt> using <tt>su</tt> and without command line arguments
   *  
   *  </pre>
   *  
   */
  protected static final void execNonSerialised(Class<? extends SetUp> suCls, String[] args) {
    SetUp su = SetUpBasic.createInstance(suCls);
    
    (new Software(su)).run(args);
  }
  
  /**
   * @effects 
   * <pre>
   *  let suCls = set-up class specified by system property \/ defaultSetUpCls
   *  let su = suCls.newInstance
   *  if <tt>args != null</tt>
   *    run <tt>this</tt> using <tt>su</tt> and with <tt>args</tt> as command line arguments
   *  else 
   *   run <tt>this</tt> using <tt>su</tt> and without command line arguments
   *  
   *  </pre>
   * @version 3.0
   *  - support setup-class from system property
   */
  protected static final void exec(Class<? extends SetUp> defaultSetUpCls, String[] args) {
    String lang = null;
    
    //TODO: use args
//    if (args.length > 0) {
//      lang = args[0];
//    }

    // v3.0: support specification of setup class via system property
    String setUpClsName = System.getProperty(PropertyName.setup_class.getSysPropName());
    
    Class<? extends SetUp> suCls;
    
    try {
      if (setUpClsName == null) {
        suCls = defaultSetUpCls;
      } else {
        suCls = (Class<? extends SetUp>) Class.forName(setUpClsName);
      }
      
      SetUp su = SetUpBasic.createInstance(suCls);
      
      (new Software(su)).run(args);
    } catch (Exception e) {
      //v3.1: no need to display error here as this is already 
      // handled by other components 
      // ControllerBasic.displayIndependentError(e);
      System.exit(1);
    }
  }
  
  /**
   * Use this method when both set-up class and necessary parameters are set in the system property. 
   * 
   * @requires 
   *  set-up class and necessary parameters are set in the system property.
   * @effects 
   *  call {@link #exec(Class, String[])} with <tt>(null,null)</tt>
   * @version 3.3
   */
  protected static final void exec() {
    exec(null, null);
  }
  
  /**
   * @requires 
   *  set-up class and necessary parameters are set in the system property.
   *  
   * @effects 
   *  call {@link Software#exec()}
   *  
   * @version 3.3
   */
  public static void main(String[] args) {
    exec(null, args);
  }


  /**
   * @effects 
   *  set this.{@link #mainModule} = <tt>mainModule</tt> 
   */
  public void setMainModule(Module mainModule) {
    this.mainModule = mainModule;
  }


  /**
   * @effects 
   *  return this.{@link #mainModule} 
   */
  public Module getMainModule() {
    return mainModule;
  }

  /**
   * @effects 
   *  if exists non-main module whose domain class is <tt>domainCls</tt>
   *    return it
   *  else
   *    throws NotFoundException
   *    
   * @version 4.0
   */
  public Module getNonMainModule(Class domainCls) throws NotFoundException {
    Class model;
    for (Module m : nonMainModules) {
      model = m.getModel();
      if (model != null && model.equals(domainCls)) {
        return m;
      }
    }
    
    // not found
    throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {domainCls});
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Software (" + su.getAppName() + ")";
  }
}
