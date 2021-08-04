package jda.modules.jdatool;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.jdatool.setup.DomainAppToolSetUp;
import jda.modules.jdatool.setup.DomainAppToolSetUpIntf;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.mosa.software.ApplicationLauncher;
import jda.mosa.software.ApplicationLauncherLight;
import jda.util.SwTk;

/**
 * @overview
 *  A tool that reads a set of domain classes directly from the input and generate a complete domain application 
 *  from them. 
 *  
 * @author dmle
 */
public class DomainAppTool {
  
  public static void main(String[] args) {
    if (args.length < 1) {
      // TODO: display help message
      System.out.println("Usage: java DomainAppTool [command] [<class1> ...]");
      System.exit(0);
    }
    
    DomainAppToolSetUpIntf su;
    
    try {
      su = getSetUp();
      DomainAppTool tool = new DomainAppTool();
      
      tool.run(su, args);
    } catch (InstantiationException | IllegalAccessException
        | ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  /**
   * @effects 
   *
   * @version 3.3
   */
  private static DomainAppToolSetUpIntf getSetUp() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    String suName = null;
    
    try {
      suName = SetUpBasic.getSystemProperty(PropertyName.setup_class.getSysPropName());
    } catch (NotFoundException e) {
      // ignore
    }
    
    if (suName != null) {
      return (DomainAppToolSetUpIntf) Class.forName(suName).newInstance();
    } else {
      return new DomainAppToolSetUp();
    }
  }

  /**
   * @effects 
   *
   * @version
   */
  protected void run(DomainAppToolSetUpIntf su, String[] args) {
    String cmd = args[0];
    String[] classNames;
    try {
      Cmd command = Cmd.lookUp(cmd);
      
      int classArgIndex;
      if (command == null) {  
        // no command specified from command line
        classArgIndex = 0;
      } else {
        classArgIndex = 1;
      }
      
      // v2.8: support serialised option
      boolean serialisedConfig = ((SetUpBasic)su).isSerialisedConfiguration();
      
      if (!serialisedConfig && command == null) {
        // default command: config
        command = Cmd.Configure;
      }
      
      if (command != null) {
        // run set-up first and then run application 
        //      System.out.printf("Invalid command: %s%n (must be one of: %s)%n", 
        //          cmd, Arrays.toString(SetUp.Command.values()));
        //      System.exit(1);
        
        //v5.2: fixed bug 
        // if (args.length > 1) {
        if (args.length > classArgIndex) {
        // end 5.2
          // class names are specified, load them
          classNames = new String[args.length-classArgIndex];
          int j = 0;
          for (int i = classArgIndex; i < args.length; i++) {
            classNames[j] = args[i];
            j++;
          }
          
          // set up the application modules from the domain classes
          su.loadClasses(classNames);
        }
        
        // run the set-up command (this may or may not require the domain classes (above))
        runSetUp((SetUpBasic)su, //cmd
            command
            );
        
        // run application (if cmd is create configure)
        if (command == Cmd.Configure)
          runApp((SetUpBasic)su);
      } else {
        // command = null /\ serialisedConfig = true
        // run application without set-up
        classNames = args;
        
        su.loadClasses(classNames);
        
        runAppWithoutSetUp((SetUpBasic)su);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }    
  }

  private static void runSetUp(SetUpBasic su, Cmd cmd) throws Exception {
    su.log(MessageCode.UNDEFINED, "Running setting up command: {0}...", cmd);
    //su.run(cmd);
    su.run(cmd, null);
  }
  
  private static void runApp(SetUpBasic su) throws 
    //IllegalArgumentException, DataSourceException 
    Exception
  {
    su.log(MessageCode.UNDEFINED, "Running the application...");

    ApplicationLauncher launcher = new ApplicationLauncherLight(su);
    String lang = null; // use language option in the configuration
    launcher.launch(su, lang);
  }
  
  private static void runAppWithoutSetUp(SetUpBasic su) 
      throws //IllegalArgumentException, DataSourceException 
      Exception {
    su.log(MessageCode.UNDEFINED, "Running the application...");
    ApplicationLauncher launcher = new ApplicationLauncher(su);
    String lang = null; // use language option in the configuration
    launcher.launch(su, lang);
  }

  /**
   * @requires 
   *  command != null /\ domainClasses != null /\ domainClasses are valid domain classes 
   *  
   * @effects 
   *  set up a "DomainAppTool" software by running <tt>command</tt> for 
   *  the modules whose domain classes are <tt>domainClasses</tt>.
   *  
   *  Throws NotPossibleException if such a software has not been configured in the system. 
   *   
   * @version 4.0 
   */
  public static void runSetUp(Cmd command, Class...domainClasses) {
    if (command == null || domainClasses == null) 
      return;
    
    setDefaultArgs();
    
    String[] args = getArgsFrom(command, domainClasses);
    
    main(args);
  }
  
  /**
   * @requires 
   *  domainClasses != null /\ domainClasses are valid domain classes
   *  
   * @effects 
   *  run a "DomainAppTool" software consisting of modules whose domain classes are <tt>domainClasses</tt>.
   *  
   *  Throws NotPossibleException if such a software has not been configured in the system. 
   *   
   * @version 4.0 
   */
  public static void run(Class...domainClasses) {
    if (domainClasses == null) 
      return;
    
    setDefaultArgs();
    
    String[] args = getArgsFrom(null, domainClasses);
    
    main(args);
  }

  /**
   * @requires 
   *  classes != null /\ classes.length > 0
   *  
   * @effects 
   *  return <tt>String[]</tt> containing <tt>command.name()</tt> (if it is specified) and 
   *   FQNs of each class in <tt>classes</tt>
   * @version 4.0
   */
  private static String[] getArgsFrom(Cmd command, Class[] classes) {
    if (command == null && (classes == null || classes.length == 0)) 
      return new String[0];
    
    String[] args;
    
    int i;
    if (command != null) {
      args = new String[classes.length + 1];
      args[0] = command.name();
      i=1;
    } else {
      args = new String[classes.length];
      i=0;
    }
    
    for (Class c : classes) {
      args[i] = c.getName();
      i++;
    }
    
    return args;
  }

  /**
   * @effects 
   *  set up the most basic run-time arguments and system properties
   * @version 4.0 
   */
  private static void setDefaultArgs() {
    // logging = true
    SwTk.setSystemProperty(PropertyName.Logging, "true");
  }
}
