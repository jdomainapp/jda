package jda.mosa.software.basic;

import jda.modules.common.types.properties.PropertyName;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpBasic;
import jda.mosa.software.ApplicationLauncher;
import jda.mosa.software.ApplicationLauncherLight;

/**
 * @overview 
 *  Represents a standard program for use to define <tt>DomainApp</tt> applications.
 *  
 *  <p>The following example describes how to code such an application:
 *  <pre>
 *     public class MyApp extends Program {
 *       // application entry point
 *       public static void main(String[] args) {
 *         try {
 *           new MyApp().exec(MyAppSetUp.class, args);
 *         } catch (Exception e) {
 *           e.printStackTrace();
 *           System.exit(1);
 *         }
 *       }
 *     }  
 *  </pre>
 *  
 * @author dmle
 * @version 
 * - 2.8 <br>
 */
public class Program {
  

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
  protected final void execNonSerialised(Class<? extends SetUp> suCls, String[] args) {
    SetUp su = SetUpBasic.createInstance(suCls);
    
    ApplicationLauncher launcher;

    try {
      // run all program modules in memory (including the domain modules)
  
      // configure the program modules
      su.run(Cmd.Configure, args);
      
      // if there are security then configure the security 
      if (su.getConfig().getUseSecurity()) {
        su.run(Cmd.ConfigureSecurity, args);
      }
      
      launcher = new ApplicationLauncherLight(su);
      
      String lang = null;
      launcher.launch(su, lang);
    } catch (Exception e) {
      //v3.1: no need to display error here as this is already 
      // handled by other components 
      // ControllerBasic.displayIndependentError(e);
      System.exit(1);
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
   * @version 3.0 <br>
   *  - support setup-class from system property<br>
   *  - 5.2b: throws exception instead of System.exit(1)
   */
  protected final void exec(Class<? extends SetUp> defaultSetUpCls, String[] args) throws Exception {
    String lang = null;
    
    //TODO: use args
//    if (args.length > 0) {
//      lang = args[0];
//    }

    // v3.0: support specification of setup class via system property
    String setUpClsName = System.getProperty(PropertyName.setup_class.getSysPropName());
    
    Class<? extends SetUp> suCls;
    
    //v5.2c: try {
      if (setUpClsName == null) {
        suCls = defaultSetUpCls;
      } else {
        suCls = (Class<? extends SetUp>) Class.forName(setUpClsName);
      }
      
      SetUp su = SetUpBasic.createInstance(suCls);
      
      // v2.8
      //ApplicationLauncher.run(su, lang);
      
      ApplicationLauncher launcher;
  
      if (!su.isSerialisedConfiguration()) {
        // run all program modules in memory (the module data may still be serialised)
  
        // configure the program modules
        su.run(Cmd.Configure, args);
        
        // if there are security then configure the security 
        if (su.getConfig().getUseSecurity()) {
          su.run(Cmd.ConfigureSecurity, args);
        }
        
        launcher = new ApplicationLauncherLight(su);
      } else {
        // set command line arguments
        su.setArgs(args);
        
        // load modules from data source and run them (assume all modules have already been created in data source) 
        launcher = new ApplicationLauncher(su);
      }
      
      launcher.launch(su, lang);
    /* v5.2c:
    } 
    catch (Exception e) {
      //v3.1: no need to display error here as this is already 
      // handled by other components 
      // ControllerBasic.displayIndependentError(e);
      System.exit(1);
    }
    */
  }
  
  
  /**
   * Use this method when the set-up class is set in the system property. 
   * 
   * @requires 
   *  set-up class is set in the system property.
   * @effects 
   *  call {@link #exec(Class, String[])} with <tt>(null,args)</tt>
   * @version 3.3
   */
  protected final void exec(String[] args) throws Exception  {
    exec(null, args);
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
  protected final void exec() throws Exception {
    exec(null, null);
  }
  
  /**
   * @requires 
   *  set-up class and necessary parameters are set in the system property.
   *  
   * @effects 
   *  initialise Program
   *  call {@link Program#exec(args)}
   *  
   * @version 3.3
   */
  public static void main(String[] args) throws Exception {
    //v5.2b: (new Program()).exec();
    (new Program()).exec(args);
  }
}
