package jda.modules.setup.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import javax.swing.ImageIcon;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Company;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.Configuration.Organisation;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.SwTk;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ChangeListener;

/**
 * @overview 
 *  Represents the 'Set-Up' class, responsible for setting up an application. It defines a set of standard stages of a  
 *  set up process and an extenssion, which can be used by sub-types to define additional set-up stages. 
 * 
 * <p>
 * Among the standard set-up tasks include:
 * <ol>
 * <li>Set up the application folder
 * <li>Set up the database and its tables
 * <li>Set up the default configuration settings (if any)
 * <li>Create the application run script
 * </ol>
 * 
 * <p>However, due to historical reasons, not <b>all standard stages</b> are implemented in this class (i.e. 
 * their implementations are left blank). In particular, these include:
 * <ol>
 *  <li>create security configuration (if specified)
 * </ol>
 * 
 * @author dmle
 */
@DClass(serialisable=false)
public abstract class SetUpBasic {
  /**
   * @overview 
   *  A helper class used to run {@link SetUpBasic#fireStateChanged(boolean)}
   *  on a thread or not depending on the boolean input. 
   *  
   * @author dmle
   */
  private class RunFireStateChanged extends Thread {

    private int counter;
    
    private static final int RunInterval = 100;  // milli-secs 
    
    @Override
    public void run() {
      while (true) {
        int currCount = counter;
        
        if (currCount > 0) {
          //System.out.println("current count: " + currCount);
          
          for (int i = 0; i < currCount; i++)
            fireStateChanged();
          
          synchronized (this) {
            counter = counter - currCount;
          }
        }
        
        try {
          Thread.sleep(RunInterval);
        } catch (InterruptedException e) {
        }
      }
    }
    
    public void run(boolean threaded) {
      if (threaded) {
        /*TODO: uncomment this for use with executor service
         * BUG: the executor service keeps running even after all log messages have been created
        if (threadExec == null)
          threadExec = Executors.newSingleThreadExecutor();
          
        threadExec.execute(this);
        */
        synchronized (this) {
          counter++; 
        }
      } else {
        fireStateChanged();
      }
    }
    
    private void fireStateChanged() {
      for (ChangeListener l : changeListeners) {
        l.stateChanged(changeEvent);
      }
    }
  } // end RunFireStateChanged

  /** the full-featured application configuration */
  //@DomainConstraint(name="config",type=DomainConstraint.Type.Domain)
  protected Configuration config;

  //@DomainConstraint(name="command",type=DomainConstraint.Type.Domain,optional=true)
  private Cmd command;
  
  //@DomainConstraint(name="status",type=DomainConstraint.Type.String,auto=true)
  private StringBuffer status;
  
 // v2.8
  private List<ChangeListener> changeListeners;
  private ChangeEvent changeEvent;
  
  // v2.8
  private Boolean isSerialised;
  private boolean isConfigured;

  private String[] args;
  
  /**
   * Message dialog code constants. These codes are used to identify 
   * the messages that are being displayed and hence are used by the system 
   * to localise the message content. 
   */
  public static enum MessageCode implements InfoCode {    
    VALIDATE_CONFIGURATION(""), 
    ERR_COMMAND_NOT_EXECUTABLE(""), 
    SETTING_UP_APPLICATION(""), 
    PROGRAM_SETTINGS(""), 
    INITIAL_CONFIGURATION(""), 
    PRE_SETUP(""), 
    DELETE_PROGRAM_FOLDER(""), 
    CREATE_PROGRAM_FOLDER(""), 
    COPY_SETUP_FILES(""), 
    SETUP_DATABASE_AND_CONFIGURATION(""), 
    INIT_DODM(""), 
    REGISTER_CONFIGURATION(""), 
    CREATE_CONFIGURATION(""), 
    // not yet defined
    UNDEFINED(""), 
    // ERRORS
    /**
     * 0: error details
     */
    FAIL_TO_LAUNCH_APPLICATION("Lỗi chạy chương trình: {0}"),
    ;   
    
    private String text;
    
    private MessageCode(String text) {
      this.text = text;
    }
    
    @Override
    public String getText() {
      return text;
    }     

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;
    
    @Override
    public MessageFormat getMessageFormat() {
      if (messageFormat == null) {
        messageFormat = new MessageFormat(text);
      }
      
      return messageFormat;
    }
  } /** end {@link MessageCode}*/
  
  // ///////
  protected DODMBasic dodm;
  
  /** 
   * The initial {@link SetUpConfigBasic}.
   * @version
   * -v2.7.1: defined <br>
   * - 4.0: renamed: sufg -> initialSuCfg 
   */
  private SetUpConfigBasic initialSuCfg;

  /** 
   * The full-featured {@link SetUpConfigBasic}.
   * @version 4.0  
   */
  private SetUpConfigBasic suCfg;

  // v3.0
  private RunFireStateChanged runFireStateChanged;

//  // v2.7.2:
//  private SetUpSecurity sus;

  // constants
  protected static final boolean debug = Toolkit.getDebug(SetUpBasic.class);
  protected static final boolean loggingOn = Toolkit.getLoggingOn(SetUpBasic.class);
  
  /**File separator*/
  protected static final String SEP = System.getProperty("file.separator");
  protected static final String URL_SEP = "/";

  private static final String DIR_BIN = "bin";
  private static final String DIR_SCRIPT = "bin" + SEP + "scripts";

  // constructor method
  public SetUpBasic() {
    // default
    GUIToolkit.initLookAndFeel();
    
    changeListeners = new ArrayList<>();
    
    ChangeEventSource src = new ChangeEventSource(
        // v3.2: MasterSetUp.class
        SetUpBasic.class
        );
    src.add(this);
    changeEvent = new ChangeEvent(src);
  }

  /**
   * @effects 
   *  create and return an instance of <tt>suCls</tt>
   *  
   * @version 2.8
   */
  public static <T extends SetUpBasic> T createInstance(Class<T> suCls) throws NotPossibleException {
    try {
      // invoke the constructor to create object 
      T instance = suCls.newInstance();
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {suCls.getSimpleName(), ""});
    }
  }
  
  /**
   * @effects 
   *  register <tt>l</tt> to listen to state change event fired by this
   */
  public void addChangeListener(ChangeListener l) {
    changeListeners.add(l);
  }
  
  /**
   * @param b 
   * @effects 
   *  inform all <tt>ChangeListener</tt>s of <tt>this</tt> that the state has been changed.
   *  <p>if <tt>threaded = true</tt> do this on a separate thread.
   * @version 
   * - 3.0: support threaded
   */
  private void fireStateChanged(boolean threaded) {
    /* v3.0
    for (ChangeListener l : changeListeners) {
      l.stateChanged(changeEvent);
    }
    */
    if (runFireStateChanged == null) {
      runFireStateChanged = new RunFireStateChanged();
    }

    if (threaded && runFireStateChanged.getState() == Thread.State.NEW) {
      runFireStateChanged.start();
    }
    
    runFireStateChanged.run(threaded);
  }
  
  /**
   * @version 3.0
   */
  private boolean hasStateChangedListeners() {
    return changeListeners != null && !changeListeners.isEmpty();
  }
  
  /**
   * @effects 
   *  if a system property is specified for storing configuration data to data source
   *    return true
   *  else 
   *    return false 
   *    
   * @version 2.8
   */
  public boolean isSerialisedConfiguration() {
    if (isSerialised == null) {
      /* v5.1: moved to ApplicationToolKit
      String propName = PropertyName.setup_SerialiseConfiguration.getSysPropName();
      String serialisedStr = System.getProperty(propName);
      
      if (serialisedStr != null) {
        // no property
        try {
          isSerialised = Boolean.parseBoolean(serialisedStr);
        } catch (Exception e) {
          isSerialised = true;
          // invalid value
          log(MessageCode.UNDEFINED, "Invalid property value: {0} = {1}", propName, serialisedStr);
        }
      } else {
        isSerialised = true;
      }
      */
      isSerialised = SwTk.getSystemPropertyBoolean(PropertyName.setup_SerialiseConfiguration, Boolean.TRUE);
    } 
    
    //System.out.println(this.getClass().getSimpleName() + ": serialised = " + isSerialised);
    
    return isSerialised;
  }

  /**
   * @effects 
   *  if a system property is specified for not running post-setup 
   *    return true
   *  else 
   *    return false 
   *    
   * @version 3.1
   */
  public boolean isPostSetUpOn() {
    // there is no need to cache this because it is not invoked frequently 
    //if (isSerialised == null) {
      String propName = PropertyName.setup_PostSetUpOn.getSysPropName();
      String tf = System.getProperty(propName);
      
      if (tf != null) {
        // property
        try {
          return Boolean.parseBoolean(tf);
        } catch (Exception e) {
          // invalid value
          log(MessageCode.UNDEFINED, "Invalid property value: {0} = {1}", propName, tf);
          return true;
        }
      } else {
        return true;
      }
    //} 
  }
  
  /**
   * @effect
   *  if configuration has been created 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  protected boolean isConfigured() {
    return isConfigured;
  }

  protected void setIsConfigured(boolean tf) {
    isConfigured = tf;
  }
  
  /**
   * @effects 
   *  set the system property whose name is <tt>propName.name()</tt> to <tt>val</tt>
   *  <br>return the previous value of the property (if exists) or return </tt>null</tt> if otherwise
   * @version 
   * - 2.8 <br>
   * - 5.2: changed to static
   */
  public static String setSystemProperty(PropertyName propName, String val) {
    String property = propName.getSysPropName();
    
    String old = System.getProperty(property);
    System.setProperty(property, val);
    
    return old;
  }
  
  
  /**
   * @effects 
   *  if exists <tt>System</tt> property named <tt>propName</tt>
   *    return its value
   *  else
   *    throw NotFoundException 
   * @version 2.8
   */
  public static String getSystemProperty(String propName) throws NotFoundException {
    String val = System.getProperty(propName);
    
    if (val == null) {
      throw new NotFoundException(NotFoundException.Code.PROPERTY_NOT_FOUND, new Object[] {propName});
    } else {
      return val;
    }
  }
  
  public void setArgs(String[] args) {
    this.args = args;
  }

  /**
   * This method is used to parse the command line arguments passed in for set-up
   * 
   * @effects
   *  if args = null
   *    return null
   *     
   *  if exists element <tt>ith</tt> of <tt>args</tt>
   *    return it
   *  else
   *    return null
   *  @version 2.7.4
   */
  protected String getArg(String[] args, int i) {
    if (args == null) return null;
    
    if (i >=0 && i <= args.length-1) {
      return args[i];
    } else {
      return null;
    }
  }
  
  /**
   * This method is used to parse the command line arguments passed in for set-up
   * 
   * @effects <pre>
   *  if this.args = null
   *    throws NotPossibleException
   *     
   *  if exists this.args[i] that are of type expectedType
   *    cast & return it 
   *  
   *  Throws NotPossibleException if i is invalid; ConstraintViolationException if this.args[i] is not of the expected type 
   *    </pre>
   *  @version 2.8
   */
  public <T> T getArg(int i, Class<T> expectedType) throws NotPossibleException, ConstraintViolationException {
    if (args == null) 
      throw new NotPossibleException(NotPossibleException.Code.NO_INPUT_ARGUMENTS);
    
    if (i >=0 && i <= args.length-1) {
      String a = args[i];
      T val = DODMToolkit.parseValue(a, expectedType);
      
      return val;
    } else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARRAY_INDEX, new Object[] {i, args.length-1});
    }
  }


  /**
   * @effects 
   *  if exist command line args from index <tt>ith</tt> 
   *    return them 
   *  else
   *    return <tt>null</tt>
   *  @version 2.8
   */
  public String[] getArgs(int i) {
    String[] subArgs = null;
    if (args != null && args.length > i) {
      subArgs = new String[args.length-i];
      System.arraycopy(args, i, subArgs, 0, subArgs.length);
    }
    
    return subArgs;
  }

  protected void validate() throws NotPossibleException, NotFoundException {
    log(MessageCode.VALIDATE_CONFIGURATION, 
        //"Kiểm tra tham số cài đặt...."
        "Validating program settings..."
        );

    if (config == null || 
        config.getAppName() == null || 
        config.getAppFolder() == null || 
        config.getDodmConfig() == null
        || config.getSetUpFolder() == null)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          new Object[] {this.getClass().getSimpleName(),"validate", "", "Không đủ tham số cài đặt"} );

    // TODO: check the setup folder
    // File suFolder = new File(setUpFolder);
    // File[] suFiles = suFolder.listFiles();
    // for (File sf: suFiles) {
    // if (sf.isDirectory() && sf.getName().equals(DIR_BIN)) {
    //
    // }
    // }
  }

  /**
   * This method is invoked by the <tt>main</tt> method of the 
   * sub-classes of this class to execute itself. 
   * 
   * @effects 
   *  run the specified <tt>SetUp</tt> object <tt>su</tt> with arguments <tt>args</tt> 
   */
  protected static void run(SetUpBasic su, String[] args) throws Exception {
    String cmd = null;
    if (args != null && args.length > 0) {
      cmd = args[0];
      
      if (cmd.equals("")) // v2.8: added this
        cmd = null;
    }

    /*v2.8
    su.run(cmd, args);
    */
    if (cmd == null) {
      su.run(Cmd.SetUp, args);
    } else {
  
      boolean found = false;
      for (Cmd cmdObj : Cmd.values()) {
        if (cmd.equalsIgnoreCase(cmdObj.name())) {
          su.run(cmdObj, args);
          found = true;
          break;
        }
      }
      
      if (!found) {
        su.log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: {0}", cmd);
      }
    }
  }

  // v2.8
//  /**
//   * @effects 
//   *  if cmd != null
//   *    run <tt>this</tt> with command <tt>cmd</tt>
//   *  else
//   *    run <tt>this</tt> with the default set of set up commands
//   */
//  public void run(String cmd) throws IOException, DataSourceException { //Exception {
//    run(cmd, null);
//  }

// v2.8  
//  /**
//   * @effects 
//   *  run <tt>cmd</tt> with <tt>args</tt> as input (if any)
//   * @version 2.7.4
//   */
//  private void run(String cmd, String[] args) throws DataSourceException, IOException {
//    if (cmd == null) {
//      run(Cmd.All, args);
//    } else {
//  
//      boolean found = false;
//      for (Cmd cmdObj : Cmd.values()) {
//        if (cmd.equalsIgnoreCase(cmdObj.name())) {
//          run(cmdObj, args);
//          found = true;
//          break;
//        }
//      }
//      
//      if (!found) {
//        log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: {0}", cmd);
//      }
//    }
//  }
  
//  /**
//   * @effects 
//   *  if cmd != null
//   *    run <tt>this</tt> with command <tt>cmd</tt>
//   *  else
//   *    run <tt>this</tt> with the default set of set up commands
//   */
//  public void run(String cmd, String[] args) throws IOException, DataSourceException { //Exception {
//    if (cmd == null) {
//      run(Cmd.All, args);
//    } else {
//      boolean found = false;
//      for (Cmd cmdObj : Cmd.values()) {
//        if (cmd.equalsIgnoreCase(cmdObj.name())) {
//          run(cmdObj, args);
//          found = true;
//          break;
//        }
//      }
//      
//      if (!found) {
//        log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: {0}", cmd);
//      }
//    }
//  }
  
  // v2.8
//  /**
//   * @effects 
//   *  run the specified command
//   * @version
//   *  v2.6.4b added this method
//   *  v2.7.4: added support for GUI-typed error dialog
//   */
//  public void run(Cmd cmd) throws IOException, DataSourceException {
//    run(cmd, null);
////    log(MessageCode.SETTING_UP_APPLICATION,
////        //"Cài đặt chương trình...."
////        "Setting up the program..."
////        );
////
////    //TODO: read all VM arguments here
////    boolean isSerialised = isSerialisedConfiguration();
////
////    try { // v2.7.4
////      // initialise application configuration
////      initApplicationConfiguration();
////  
////      if (cmd == Cmd.Configure) {
////        createConfiguration(isSerialised);
////      }
////  //    else if (cmd.equalsIgnoreCase(Command.ConfigureDomain.name())) {
////  //      setUpDomainConfiguration();
////  //    } 
////      else if (cmd == Cmd.DeleteConfig) {
////        deleteConfigurationSchema();
////      } else if (cmd == Cmd.DeleteDomainSchema) {
////        /*v2.7.3: not guaranteed to work 
////        deleteDomainSchema();
////         */
////        deleteDomainSchema();
////      } else if (cmd == Cmd.DeleteDomainData) {
////        deleteDomainData();
////      } else if (cmd == Cmd.PostSetUpDb) {
////        postSetUpDB();
////      } else if (cmd == Cmd.PostSetUp) {
////        postSetUp();
////      } 
//////      else if (cmd == Command.ConfigureSecurity) {
//////        createSecurityConfiguration();
//////      } else if (cmd == Command.SetUpSecurity) {
//////        setUpSecurity();
//////      } 
////      // v2.6.4b
////      else if (cmd == Cmd.RegisterConfigurationSchema) {
////        registerConfigurationSchema();
////      }
////      // v2.7
////      else if (cmd == Cmd.LoadConfiguration) {
////        loadConfiguration();
////      }
////      else if (cmd == Cmd.All) {
////        runAll(isSerialised);     
////      }
////      else {  // extended commands
////        runExtended(cmd, null);
////        //v2.7.3: log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: ",cmd);
////      }
////    } catch (Exception e) {
////      // v2.7.4: added gui dialog
////      ControllerBasic.displayIndependentError(e);
////      throw e;
////    }
//  }

  /**
   * @effects 
   *  run the specified command
   *  
   * @version 2.7.4
   */
  public void run(Cmd cmd, String[] args) throws IOException, DataSourceException {
    log(MessageCode.SETTING_UP_APPLICATION,
        //"Cài đặt chương trình...."
        "Setting up the program..."
        );
    // v2.8
    // set args
    if (args != null)
      this.args = args;
    
    boolean isSerialised = isSerialisedConfiguration();
    
    try { // v2.7.4
      // initialise application configuration
      //TODO: should we consider loading the configuration from data source if serialised = true
      // v2.8: initApplicationConfiguration();
      // initialise <tt>config</tt> with a <tt>Configuration</tt> object needed to run the set-up commands. 
      // IMPORTANT This <tt>Configuration</tt> is not loaded from the data source.
      if (config  == null)  // added this check
        createApplicationConfiguration();
  
      // v5.1: added this command to list
      if (cmd == Cmd.List) {
        System.out.printf("Command list: %n");
        for (Cmd c : Cmd.values()) {
          System.out.println("  " + c);
        }
      } else if (cmd == Cmd.Configure) {
        createConfiguration(isSerialised);
      }
  //    else if (cmd.equalsIgnoreCase(Command.ConfigureDomain.name())) {
  //      setUpDomainConfiguration();
  //    } 
      else if (cmd == Cmd.DeleteConfig) {
        deleteConfigurationSchema();
      } else if (cmd == Cmd.CreateDomainSchema) {
        createDomainSchema();
      } else if (cmd == Cmd.DeleteDomainSchema) {
        /*v2.7.3: not guaranteed to work 
        deleteDomainSchema();
         */
        deleteDomainSchema();
      } else if (cmd == Cmd.DeleteDomainData) {
        deleteDomainData();
      } else if (cmd == Cmd.PostSetUpDb) {
        postSetUpDB();
      } else if (cmd == Cmd.PostSetUp) {
        postSetUp();
      } 
//      else if (cmd == Command.ConfigureSecurity) {
//        createSecurityConfiguration();
//      } else if (cmd == Command.SetUpSecurity) {
//        setUpSecurity();
//      } 
      // v2.6.4b
      else if (cmd == Cmd.RegisterConfigurationSchema) {
        registerConfigurationSchema();
      }
      // v2.7
      else if (cmd == Cmd.LoadConfiguration) {
        loadConfiguration();
      }
      else if (cmd == Cmd.SetUp) {
        runSetUp(isSerialised);     
      } else if (cmd == Cmd.SetUpLight) {
        runSetUpLight(isSerialised);     
      }
      else {  // extended commands
        // v2.7.4: add args
        //runExtended(cmd);
        runExtended(cmd, args);
        //v2.7.3: log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: ",cmd);
      }
    } catch (Exception e) {
      // v2.7.4: added gui dialog
      ControllerBasic.displayIndependentError(e);
      throw e;
    }
  }
  
  protected void runExtended(Cmd cmd, 
      String[] args // v2.7.4
      ) throws DataSourceException, IOException {
    // to be written by sub-types
    log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: ",cmd);
  }

  protected void runSetUp(boolean isSerialised) throws IOException, DataSourceException {
//    printSettings();
//
//    // now set up
//    preSetUp();
//    
//    setUpDB();
//
//    // set up configuration 
//    setUpConfiguration();
    runAllConfig();
    
//    // set up basic security resources
//    setUpSecurity();

    // for sub-classes to add other routines
//    postSetUpDB();
//
//    createRunScript();
//
//    postSetUp();
    runAllFinalise();
  }

  /**
   * @effects 
   *  run a light-weight version of {@link #runSetUp(boolean)}
   * @version 5.1 
   */
  protected void runSetUpLight(boolean isSerialised) throws IOException, DataSourceException {
    runConfigLight();
    runFinaliseLight();
  }
  
  protected void runAllConfig() throws IOException, DataSourceException {
    printSettings();

    // now set up
    preSetUp();
    
    // v2.8: setUpDB();
    // connect to the db
    initDODM();

    // set up configuration 
    setUpConfiguration();
  }
  
  /**
   * @effects 
   *  A light-weight version of {@link #runAllConfig()}
   * @version 5.1 
   */
  protected void runConfigLight() throws IOException, DataSourceException {
    printSettings();

    // now set up
    // preSetUp();
    
    // v2.8: setUpDB();
    // connect to the db
    initDODM();

    // set up configuration 
    setUpConfiguration();
  }
  
  protected void runAllFinalise() throws DataSourceException, IOException {
    postSetUpDB();

    createRunScript();

    postSetUp();    
  }
  
  /**
   * @effects 
   *  A light-weight version of {@link #runAllFinalise()}.
   *  
   * @version 5.1 
   */
  protected void runFinaliseLight() throws DataSourceException, IOException {
    postSetUpDB();

    //createRunScript();

    //postSetUp();    
  }
  
  protected void printSettings() {
//    System.out
//        .println("------------------------------------------------------");
//    StringBuffer sb = new StringBuffer("Tham số chương trình:");
//    sb.append("\n");
//    sb.append("+ Tên chương trình        : " + config.getAppName()).append("\n")
//        .append("+ Thư mục cài đặt         : " + config.getSetUpFolder()).append("\n")
//        .append("+ Thư mục cài chương trình: " + config.getAppFolder()).append("\n")
//        .append("+ Tên CSDL                : " + config.getDbName()).append("\n");
    
//    log(MessageCode.PROGRAM_SETTINGS, "Tham số chương trình:");
//    log(MessageCode.PROGRAM_NAME,"+ Tên chương trình        : ",config.getAppName());
//    log(MessageCode.PROGRAM_SETUP_FOLDER,"+ Thư mục cài đặt         : ",config.getSetUpFolder());
//    log(MessageCode.PROGRAM_APP_FOLDER,"+ Thư mục cài chương trình: ",config.getAppFolder());
//    log(MessageCode.PROGRAM_DB,"+ Tên CSDL                : ",config.getDbName());

    String os = System.getProperty("os.name").toLowerCase();
    
    StringBuffer sb = new StringBuffer("------------------------------------------------------\n");
    sb.append("Program settings:\n");
    sb.append("\n");
    sb.append(  "+ Operating system    : "+ os).append("\n")
        .append("+ Program name        : " + config.getAppName()).append("\n")
        .append("+ Set up folder       : " + config.getSetUpFolder()).append("\n")
        .append("+ Program folder      : " + config.getAppFolder()).append("\n")
        .append("+ Database            : " + config.getDodmConfig().getOsmConfig().getProtocolURL()).append("\n");
    sb.append("------------------------------------------------------\n");
    log(MessageCode.PROGRAM_SETTINGS, sb.toString());
    
//    System.out
//        .println("------------------------------------------------------");
  }

//  /**
//   * @effects 
//   *  initialise <tt>config</tt> with a <tt>Configuration</tt> object needed to run the set-up commands. 
//   *  
//   *  <p><b>IMPORTANT</b>: This <tt>Configuration</tt> is not loaded from the data source.
//   *  
//   * @throws NotPossibleException
//   * @throws NotFoundException
//   * @throws DataSourceException
//   */
//  private void initApplicationConfiguration() throws NotPossibleException, NotFoundException, DataSourceException {
//    log(MessageCode.INITIAL_CONFIGURATION,
//        //"Khởi tạo cấu hình chương trình cơ bản");
//        "Initialising program configuration");
//    
////    if (schema == null)
////      connectDB();
//    
//    // create application-specific configuration 
//    createApplicationConfiguration();  
//  }
  
  /**
   * @effects 
   *  return a well known application folder suitable for the underlying operating system
   */
  protected String getWellKnownAppFolder(String appName) {
    //String os = System.getProperty("os.name");
    String fsep = System.getProperty("file.separator");
    
    String parentFolder = System.getProperty("user.home");

    return parentFolder+fsep+appName;
    
    /* use well-known application folder
    if (os.startsWith("Window")) {
      return "C:"+fsep+"program files"+fsep+appName;
    } else {
      return fsep+"opt"+fsep+appName;
    }
    */
  }
  
  protected void preSetUp() throws IOException {    
    log(MessageCode.PRE_SETUP,
        //"Cài đặt thư mục chương trình...."
        "Preparing to run set-up..."
        );
    // v2.7.4: create other folders (if not already created)
    //createApplicationDirs(true);
    // FIXED: create only the application dir
    createApplicationDir(
        /* v3.1: application folder may have been created by postSetUp of a module 
         * during the set-up program
         * Generally speaking, therefore, application folder needs to be deleted 
         * before running set-up 
        true
         */
        true
        );
    
    // copy all the files in the set-up folder there
    File suFolder = new File(config.getSetUpFolder());
    File appDir = new File(config.getAppFolder());
    
    log(MessageCode.COPY_SETUP_FILES,
        //"Tải phần mềm vào thư mục..."
        "Copying the set-up files..."
        );

    copyDir(suFolder, appDir);
  }


//  /**
//   * @requires 
//   *  config != null
//   * @effects 
//   *  if application folders already exist and deleteIfExist = true
//   *    delete them 
//   *    
//   *  if application folders not yet exist OR they were deleted
//   *    create new folders
//   *  else
//   *    do nothing
//   *    
//   * <p>Print error if failed
//   * @version 2.7.3
//   * 
//   * @deprecated v 2.7.4
//   */
//  public void createApplicationDirs(boolean deleteIfExist) {
//    if (config != null) {
//      try {
//        makeDir(config.getAppFolder(), deleteIfExist);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      
//      String appFolderPref = config.getAppFolder() + SEP;
//      
//      try {
//        makeDir(appFolderPref + config.getExportFolder(), deleteIfExist);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//
//      try {
//        makeDir(appFolderPref + config.getImportFolder(), deleteIfExist);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//  }

  /**
   * @effects 
   *  create the application's program directory (if <tt>deleteIfExist = true</tt> then delete if it already existed)  
   */
  public void createApplicationDir(boolean deleteIfExist) {
    if (config != null) {
      try {
        makeDir(config.getAppFolder(), deleteIfExist);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @effects 
   *  if application folder as specified by {@link #config} does not exists
   *    create it
   *  else
   *    do nothing
   *  
   *  <p>throws NotPossibleException if failed.
   *  @version 3.1
   */
  public void createApplicationDirIfNotExist() throws NotPossibleException {
    ToolkitIO.createFolderIfNotExists(new File(config.getAppFolder()));    
  }
  
  /**
   * @requires
   *  {@link #createApplicationDir(boolean)} has been invoked
   * @effects 
   *  create <tt>dirName</tt> relative to {@link Configuration#getAppFolder()} 
   *  (if <tt>deleteIfExist = true</tt> then delete if already exists) 
   */
  public File createApplicationSubDir(String dirName, boolean deleteIfExist) {
    File dir = null;
    if (config != null) {
      String appFolderPref = config.getAppFolder() + SEP;
      
      try {
        dir = makeDir(appFolderPref + dirName, deleteIfExist);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }
    return dir;
  }
  

  /**
   * @requires 
   *  path {@link Configuration#getAppFolder()}<tt>/parentSubDir</tt> exists
   *  
   * @effects 
   *  create in dir {@link Configuration#getAppFolder()}<tt>/parentSubDir</tt>
   *    the sub-path whose elements are <tt>subPathElements</tt>
   *    (if <tt>deleteIfExist = true</tt> the delete any path elements that already exist)
   *    
   *  <p>throws NotPossibleException if failed
   */
  public File createApplicationSubDirPath(boolean deleteIfExist,
      final String parentSubDir, String...subPathElements) throws NotPossibleException {
    File dir = null;
    if (config != null) {
      String appFolderPref = config.getAppFolder() + SEP;
      String parentSubDirPath = appFolderPref + SEP + parentSubDir;
      String subPath = parentSubDirPath;
      
      for (String pathE : subPathElements) {
        try {
          subPath += SEP + pathE;
          dir = makeDir(subPath, deleteIfExist);
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_FOLDER,
              e, new Object[] {subPath});
        }
      }
    }
    
    return dir;
  }
  
  /**
   * @effects 
   *  if the directory whose absolute path is <tt>path</tt> does not yet exists
   *    create it in the local file system
   *    return dir as <tt>File</tt>
   *    
   *  <p>throws IOException if failed
   */
  protected File makeDir(String path, boolean deleteIfExist) throws IOException {
    File dir = new File(path);
    
    boolean existed = dir.exists();
    
    if (deleteIfExist && existed) {
      // delete existing dir
      delDir(dir);
      existed = false;
    }
    
    if (!existed) {
      boolean ok = dir.mkdir();
      if (!ok) {
        throw new IOException("Failed to create folder: " + path);
      }
    }
    
    return dir;
  }
  
  // v2.8: not used
//  protected void setUpDB() throws DataSourceException, IOException {
//    log(MessageCode.SETUP_DATABASE_AND_CONFIGURATION,
//        //"Cài đặt dữ liệu và cấu hình chương trình..."
//        "Setting up program data and configuration..."
//        );
//
//    // connect to the db
//    initDODM();
//  }

  /**
   * @effects 
   *  initialise an <tt>DODM</tt> from the initial application configuration 
   *  
   *  <p>Throws DataSourceException if failed.
   */
  public DODMBasic initDODM() throws DataSourceException {
    if (debug)
      log(MessageCode.INIT_DODM,
          //"Kết nối dữ liệu " + getDBName()
          "Initialising data manager component"
          );
    
    // v2.8:
    //dodm = DODMBasic.getInstance(createInitApplicationConfiguration());
    
    if (config != null)
      dodm = DODMBasic.getInstance(config);
    else
      dodm = DODMBasic.getInstance(
          loadInitApplicationConfiguration()
          );
    
    return dodm;
  }

  /**
   * This is used by {@link #initDODM()}.
   * 
   * @effects 
   *  load the initial <tt>Configuration</tt> that contains ONLY a sub-set of the key 
   *  settings, enough to get connected to the data source.
   * @version 2.8
   * @note the default implementation is to return the same <tt>Configuration</tt> as {@link #createInitApplicationConfiguration()}
   */
  protected Configuration loadInitApplicationConfiguration() {
    return createInitApplicationConfiguration();
  }


  
//  protected void runDBScripts() throws DBException {
//    if (schema == null) {
//      connectDB();
//    }
//
//    // run db scripts (if any)
//    File scriptFolder = new File(setUpFolder + SEP + DIR_SCRIPT);
//    File[] sfiles = scriptFolder.listFiles();
//    if (sfiles != null && sfiles.length > 0) {
//      DBToolKit dbt = schema.getDBManager();
//      for (File sf : sfiles) {
//        System.out.println("Chạy tệp dữ liệu: " + sf.getName());
//        dbt.executeStatementsFromFile(sf.getAbsolutePath());
//      }
//    }
//  }
  
  /**
   * @effects 
   *  Perform a task for post-setup the database  
   */
  protected void postSetUpDB() throws DataSourceException {
    // to override by sub-classes to perform tasks after the main set-up 
    // has been run
  }

  /**
   * @effects 
   *  register the configuration schema 
   */
  protected void registerConfigurationSchema() throws DataSourceException {
    log(MessageCode.REGISTER_CONFIGURATION,
        //"Đăng ký cấu hình"
        "Registering configuration..."
        );
     
     if (dodm == null) {
       initDODM();
     }

     /**
      * Default configuration
      */
     // create the setting schema and the main settings
     /*v2.7.3: uses setup-config-type 
     SetUpConfiguration sufg = new SetUpConfiguration(schema, config);
     */
     SetUpConfigBasic sufg = createSetUpConfigurationInstance();
     
     sufg.registerConfigurationSchema(this, true);
   }
  
  /**
   * @effects 
   *   set up the default and domain configurations.
   */
  protected void setUpConfiguration() throws DataSourceException {
    if (dodm == null) {
      initDODM();
    }

    boolean serialised = true;  // v2.8
    
    /**
     * Default configuration
     */
    /*v2.7.3: uses setup-config-type 
    SetUpConfiguration sufg = new SetUpConfiguration(schema, config);
    */
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();
    
    sufg.deleteConfigurationSchema();

    // register the domain classes
    sufg.registerConfigurationSchema(this, true);

    sufg.createConfiguration(this, serialised);

    /***
     * Domain configuration
     */
    // register 
    registerDomainClasses(); 

    // delete
    clearDomainConfiguration(sufg);

    // delete data
    deleteDomainData();

    // create 
    createDomainConfiguration(sufg, serialised);
  }

  /**
   * @effects 
   *  create the default and the domain-specific configuration in <tt>schema</tt> 
   */
  protected void createConfiguration(
      boolean serialised  // v2.8
      ) throws DataSourceException
  , NotPossibleException  // v2.7.3
  {
    log(MessageCode.CREATE_CONFIGURATION,
        //"Cài đặt cấu hình"
        "Creating the configuration"
        );
    
    if (dodm == null) {
      initDODM();
    }

    /**
     * Default configuration
     */
    // create the setting schema and the main settings
    /*v2.7.3: uses setup-config-type 
    SetUpConfiguration sufg = new SetUpConfiguration(schema, config);
    */
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();
    
    sufg.registerConfigurationSchema(this, true);

    sufg.clearConfigurationSchema();

    sufg.createConfiguration(this, serialised);
    
    /**
     * Domain configuration
     */
    // register 
    registerDomainClasses(); 

    // delete
    //clearDomainConfiguration(sufg);
    
    // create
    createDomainConfiguration(sufg, serialised);
    
    // v2.8
    isConfigured =  true;
  }
  
  /**
   * @effects 
   *  create and return a set-up configuration instance specified by the application 
   *  (i.e. after having a <tt>Configuration</tt>) 
   *  
   * @version 
   * - 2.7.3<br>
   * - 4.0: improved to cache the created instance for re-use between related method invocations of this
   */
  public SetUpConfigBasic createSetUpConfigurationInstance() {
    /** v4.0
    // get setup-config-type from the config
    Configuration config = getConfig();
    
    Class<? extends SetUpConfigBasic> suCfgType = config.getSetUpConfigurationType();
    
    // create the instance
    SetUpConfigBasic sufg = SetUpConfigBasic.createInstance(suCfgType, dodm, config);
    
    return sufg;
    */
    if (suCfg == null) {
      // get setup-config-type from the config
      Configuration config = getConfig();
      
      Class<? extends SetUpConfigBasic> suCfgType = config.getSetUpConfigurationType();
      
      // create the instance
      suCfg = SetUpConfigBasic.createInstance(suCfgType, dodm, config);
    }
    
    return suCfg;
  }

  /**
   * This method creates an instance of {@link SetUpConfigBasic}, which is needed by operations 
   * that only require the <b>initial</b> <tt>Configuration</tt> settings.
   *   
   * @effects 
   *  return the <b>base</b> set-up configuration (i.e. before having a <tt>Configuration</tt>).
   *  
   *  <p>It uses the initial configuration (if available) that is created by {@link #loadInitApplicationConfiguration()}.
   *  
   * @version 
   *  - 2.8: use SetUpConfig type in init configuration if available 
   *  
   */
  public SetUpConfigBasic createSetUpConfigurationBasicInstance() {
    if (initialSuCfg == null) {
      Configuration initConfig = loadInitApplicationConfiguration();
      Class<? extends SetUpConfigBasic> suCfgType = null;
      if (initConfig != null) {
        suCfgType = initConfig.getSetUpConfigurationType();
      }

      if (suCfgType == null) {
        // not specified -> use default
        suCfgType = SetUpConfigBasic.class;
      }

      initialSuCfg = SetUpConfigBasic.createInstance(suCfgType, dodm); 
          //new SetUpConfigBasic(dodm);
    }
    
    return initialSuCfg;
  }
  
  /**
   * This is invoked by {@link #createConfiguration()} to allow sub-classes of this class
   * to define their own domain-specific configuration. 
   * 
   * @requires <code>schema != null &&</code> the default configuration is
   *           loaded into the schema
   * @effects 
   *  create <tt>Module</tt>, <tt>RegionToolMenuItem</tt> and <tt>RegionGui</tt> in <tt>schema</tt> 
   *  for each module configuration in the domain. 
   */
  protected void createDomainConfiguration(SetUpConfigBasic sucfg
      , boolean serialisedConfig  // v2.8
      )
      throws DataSourceException, NotFoundException {
    log(MessageCode.UNDEFINED,
        //"Tạo cấu hình chương trình"
        "Creating domain configuration"
        );
    // TODO: check dependency between system and domain modules to prioritise which 
    // modules to load first
    
    final String langCode = sucfg.getLanguageCode();
    
    /*v2.7.3: create any system modules that this application may use */
    List<List<Class>> moduleDescriptors = getSystemModuleDescriptors();
    Class[] group;
    // v2.6.4b: added null check
    if (moduleDescriptors != null) {
      // create system-specific labels
      Map<String,Label> sysLabelMap;
      
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);
        
        sysLabelMap = getModuleLabels(group); // v3.0
        
        sucfg.createModules(this, group, sysLabelMap, serialisedConfig);
      }
    }
    
    // domain-specific modules
    moduleDescriptors = getModuleDescriptors();
    if (moduleDescriptors != null) {
      // create domain-specific labels
      Map<String,Label> labelMap; // v3.0: = getDomainLabels();
      
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);
        
        labelMap = getModuleLabels(group); // v3.0
        
        sucfg.createModules(this, group, labelMap, serialisedConfig);
      }
    }
  }
  
  /**
   * @requires 
   *  moduleDescrClasses != null /\ langCode != null
   *  
   * @effects <pre> 
   *    load and return a {@link Map}<tt>(String,Label)</tt> from resource files of the 
   *    configured language of this, that are stored relative to 
   *    each module descriptor class in  <tt>moduleDescrClasses</tt>
   *    
   *    if no resource files are defined return <tt>null</tt>
   *    </pre>
   * @version 3.0
   */
  public Map<String, Label> getModuleLabels(Class[] moduleDescrClasses) {
    
    Map<String, Label> labelMap = new LinkedHashMap();

    // the specified language
    final Language lang = config.getLanguage();
    final String langCode = lang.getLanguageCode();
    
    // the label class is a sub-type of Label.class for the specified language code
    Class labelCls;
    //TODO: improved to support other languages
    if (lang == Language.Vietnamese) {
      labelCls = jda.modules.setup.init.lang.vi.Label.class;
    } else if (lang == Language.English) {
      labelCls = jda.modules.setup.init.lang.en.Label.class;
    } else
      throw new NotImplementedException("Label not supported for language: {0}", langCode);
      
    // the label file of the specified language
    /* v3.1: changed into ToolkitIO
    String sep = File.separator;
    String labelFile = "resources" + sep + "lang" + sep + langCode + sep + "Labels.properties";
    */
    final String propFileName = "Labels.properties";
    
    Properties labelProps;
    Enumeration<String> propNames;
    String propName;
    String propVal;
    Class<ModuleDescriptor> MD = ModuleDescriptor.class;
    ModuleDescriptor md;
    String moduleName;
    String labelNamePrefix, labelName;
    
    Label label;
    
    for (Class mdc : moduleDescrClasses) {
      md = (ModuleDescriptor) mdc.getAnnotation(MD);
      if (md != null) { // has module descriptor
        moduleName = md.name();
        labelNamePrefix = moduleName + "_"; // e.g: ModuleStudent_
        
        labelProps = //ToolkitIO.readPropertyFile(mdc, labelFile, "utf-8");
            ToolkitIO.readPropertyFile(mdc, "utf-8", "resources", "lang", langCode, propFileName);
        if (labelProps != null) {
          // has labels 
          // debug
          if (debug) {
            log(MessageCode.UNDEFINED, "Read labels:\n   Module name: {0} \n   Label file: {1}", moduleName, propFileName);
          }

          propNames = (Enumeration<String>) labelProps.propertyNames();
          while (propNames.hasMoreElements()) {
            propName = propNames.nextElement();
            propVal = labelProps.getProperty(propName);
            
            labelName = labelNamePrefix + propName;
            
            // create label object based on language code
            label = Label.createInstance(labelCls, propVal);
            
            labelMap.put(labelName, label);
          }
        } else {
          // debug
          if (debug) {
            log(MessageCode.UNDEFINED, "Labels NOT found:\n   Module name: {0} \n   Label file: {1}", moduleName, propFileName);
          }
        }
      }
    }
    
    if (labelMap.isEmpty())
      return null;
    else
      return labelMap;
  }

// v3.0  
//  /**
//   * @effects 
//   *  if label constant class != null
//   *    retrieve domain-specific labels
//   *  else
//   *    return null
//   * @deprecated as of v3.0, use {@link #getModuleLabels(Class[])} instead
//   */
//  protected Map<String,Label> getDomainLabels() throws 
//  NotFoundException {
//    Class constClass = config.getLabelConstantClassObject();
//        
//    if (constClass == null) {
//      return null;
//    }
//    
//    String lang = config.getLanguage().getLanguageCode();
//      
//    // load the suitable label constant class based on the language name
//    String simpleName = constClass.getSimpleName();;
//    String pkgName = constClass.getPackage().getName();
//    // the prefix before the language
//    String pkgNamePrefix = pkgName.substring(0, pkgName.lastIndexOf("."));
//    // the language part of the package name
//    String clsLang = pkgName.substring(pkgNamePrefix.length()+1);
//    String clsName; 
//    Class labelConstClass;
//    if (!clsLang.equalsIgnoreCase(lang)) {
//      // new class name
//      clsName = pkgNamePrefix + "." + lang + "." + simpleName;
//      try {
//        labelConstClass = Class.forName(clsName);
//      } catch (ClassNotFoundException e) {
//        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,
//            "Không tìm thấy lớp {0}", clsName);
//      }
//    } else {
//      labelConstClass = constClass;
//    }
//
//    // now load the labels from the class
//    Map<String,Label> labels = Toolkit.getConstantObjectsAsMap(labelConstClass,
//        Label.class);
//
//    if (labels == null)
//      throw new NotFoundException(NotFoundException.Code.LABELS_NOT_FOUND,
//          "Không tìm thấy cấu hình nhãn nào của chưowng trình");
//    
//    return labels;
//  }
  
  /**
   * @effects 
   *   delete all the system-typed resources used by the configuration schema
   */
  protected void deleteConfigurationSchema() throws DataSourceException {
    if (dodm == null) {
      initDODM();
    }

    log(MessageCode.UNDEFINED,
        //"Xoá cấu hình"
        "Deleting default configuration"
        );
    
    // create the setting schema and the main settings
    SetUpConfigBasic sufg = createSetUpConfigurationBasicInstance(); //new SetUpConfiguration(schema);
    
    //TODO: v2.7.4: this is not needed 
    //sufg.registerConfigurationSchema(this, false);

    sufg.deleteConfigurationSchema();
  }

  
  /**
   * @effects 
   *  remove the domain-specific module configuration (if any) 
   *  that was created using {@link #createDomainConfiguration(SetUpConfigBasic)}.  
   *  Throws DBException if fails to do so.
   */
  protected void clearDomainConfiguration(SetUpConfigBasic sucfg) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Xóa dữ liệu mô hình chương trình hiện tại"
        "Removing domain configuration"
        );
    //Class[] moduleCfgs = getModuleDescriptors();
//    sucfg.clearModules(moduleCfgs);
    
    /*v2.7.3: clear any system modules that are used by this application*/
    List<List<Class>> moduleDescriptors = getSystemModuleDescriptors();
    Class[] group;
    if (moduleDescriptors != null) {
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);
        sucfg.clearModules(group);
      }
    }
    
    moduleDescriptors = getModuleDescriptors();
    // v2.6.4b: added null check
    if (moduleDescriptors != null) {
      for (List<Class> mds : moduleDescriptors) {
        group = mds.toArray(new Class[mds.size()]);
        sucfg.clearModules(group);
      }
    }
  }
  
  /**
   * @effects create the structure of the data source schema(s) that are used to 
   *  store the objects of the domain classes of the application.
   *  
   *  <p>Throws DataSourceException if fails. 
   * @version 3.1
   */
  protected void createDomainSchema() throws DataSourceException {
    createDomainSchema(getModelClasses());
  }
  
  /**
   * @effects create the structure of the data source schema(s) that are used to 
   *  store the objects of the domain classes of the application.
   *  
   *  <p>Throws DataSourceException if fails. 
   * @version 5.4
   */
  public void createDomainSchema(Class[] domainClasses) throws DataSourceException {
    if (dodm == null) {
      initDODM();
    }

    log(MessageCode.UNDEFINED,
        "Creating domain schema"
        );
    
    // create the domain classes
//    Class[] domainClasses = getModelClasses();
    
    if (domainClasses != null) {
      boolean serialised = true; 
      boolean createIfNotExist = true;
      boolean read = false;
      registerClasses(domainClasses, serialised, createIfNotExist, read);
    }
  }
  
  /**
   * @effects delete the structure of the data source schema(s) that are used to 
   *  store the objects of the domain classes of the application (the schema(s) themselves
   *  are not deleted).  
   *  
   *  <p>Throws DataSourceException if fails.
   * @version 2.7.3
   */
  protected void deleteDomainSchema() throws NotPossibleException, DataSourceException {
    deleteDomainSchema(getDomainClasses());
  }
  
  /**
   * @effects delete the structure of the data source schema(s) that are used to 
   *  store the objects of the domain classes of the application (the schema(s) themselves
   *  are not deleted).  
   *  
   *  <p>Throws DataSourceException if fails.
   * @version 
   * - 2.7.3<br>
   * - 5.4
   */
  public void deleteDomainSchema(List<Class> domainClasses) throws NotPossibleException, DataSourceException {
    if (dodm == null) {
      initDODM();
    }
    
    log(MessageCode.UNDEFINED,
        "Deleting the domain schema..."
        );
    
    // delete the domain classes
//    List<Class> domainClasses = getDomainClasses();
    
    if (domainClasses != null) {
      /*v2.7.4: moved to SetUpConfig
        if (debug) log(MessageCode.UNDEFINED,
            "   Deleting constraints"
            );
        deleteDataSourceConstraints(domainClasses);
          
        if (debug) log(MessageCode.UNDEFINED,
            //"  Xóa các lớp"
            "   Deleting classes"
            );
        dodm.getDom().deleteClasses(domainClasses, true);
        */
      // create the setting schema and the main settings
      SetUpConfigBasic sufg = createSetUpConfigurationBasicInstance(); //new SetUpConfiguration(schema);
      
      sufg.deleteDataSourceSchema(domainClasses);
    } else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {"domainClasses = null"});
    }
  }
  
  /**
   * @effects
   *  return <b>all</b> domain classes of the application (this includes 
   *  those returned by both {@link #getModelClasses()} and {@link #getValueClasses()}); 
   *  or return <tt>null</tt> if no such classes are configured.
   * @version 2.7.3
   * 
   */
  private List<Class> getDomainClasses() {
    List<Class> domainClasses = new ArrayList<>();
    Class[] modelClasses = getModelClasses();
    if (modelClasses != null) {
      Collections.addAll(domainClasses, modelClasses);
    }
    
    /*v2.7.3: removed
    Class[] valueClasses = getValueClasses();
    if (valueClasses != null) {
      Collections.addAll(domainClasses, valueClasses);
    }
    */
    
    return (!domainClasses.isEmpty()) ? domainClasses : null;
  }

  /* v2.7.4: not used
  protected void deleteDataSourceConstraints(Collection<Class> classes) throws DataSourceException {
    List<String> consNames;
    for (Class c : classes) {
      consNames = dodm.getDom().loadDataSourceConstraints(c);
      if (consNames != null) {
        for (String cons : consNames) {
          if (debug) log(MessageCode.UNDEFINED,
              //"  ràng buộc {0}"
              "   constraint {0}"
              , cons);
          dodm.getDom().deleteDataSourceConstraint(c, cons);
        }
      }
    }
  }
  */
  
  /**
   * @effects delete the structure of the data source schema(s) that are used to 
   *  store the objects of the domain classes of the application (the schema(s) themselves
   *  are not deleted).  
   *  
   *  <p>Throws DataSourceException if fails.
   * @deprecated as of version 2.7.3
   */
  protected void deleteDomainSchemaByName() throws NotPossibleException, DataSourceException {
    if (dodm == null) {
      initDODM();
    }
    
    // get the schema name of the domain classes
    Class[] modelClasses = getModelClasses();
    if (modelClasses != null) {
      Collection<String> schemaNames = new Stack<>();
      String schemaName;
      DSMBasic dsm = dodm.getDsm();
      DOMBasic dom = dodm.getDom();
      for (Class c : modelClasses) {
        schemaName = dsm.getDomainSchema(c);
        if (schemaName == null)
          schemaName = DCSLConstants.DEFAULT_SCHEMA;
        
        if (!schemaNames.contains(schemaName)) {
          // a new schema -> delete 
          dom.deleteDomainSchema(schemaName); //getDBManager().dropAllTables();
          
          schemaNames.add(schemaName);
        }
      }
    } else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {"domainClasses = null"});
    }
  }
  
  /**
   * @effects drop all data tables of the application, 
   *  throws DBException if fails.
   */
  protected void deleteDomainData() throws NotPossibleException, DataSourceException {
    deleteDomainData(getDomainClasses());
  }
  
  /**
   * @effects drop all data tables of the application, 
   *  throws DBException if fails.
   */
  public void deleteDomainData(List<Class> domainClasses) throws NotPossibleException, DataSourceException {
    if (dodm == null) {
      initDODM();
    }
    
//    List<Class> domainClasses = getDomainClasses();
    
    if (domainClasses != null) {
      DOMBasic dom = dodm.getDom();
      boolean strict = true; // v3.0
      dom.deleteObjects(domainClasses, strict);
     }else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {"domainClasses = null"});
    }
  }

//  /**
//   * @requires
//   *  classes != null
//   * @effects 
//   *  
//   */
//  void deleteData(List<Class> classes) throws DataSourceException {
//    DSM dsm = schema.getDsm();
//    DOM dom = schema.getDom();
//    for (Class cls : classes) {
//      if (!dsm.isTransient(cls)) {
//        if (debug)
//          log(MessageCode.UNDEFINED, 
//              "Deleting data of {0}", cls.getSimpleName());
//        dom.deleteObjects(cls);
//      }
//    }
//  }
  
  /**
   * @effects 
   *  set up the domain-specific configuration
   * @throws DBException
   */
  //TODO: FIX: this method does not work correctly for CourseMan
//  protected void setUpDomainConfiguration() throws DBException {
//    if (schema == null) {
//      connectDB();
//    }
//
//    // register domain classes
//    registerDomainClasses(); 
//    
//    SetUpConfiguration sufg = new SetUpConfiguration(schema, config.getLanguage());
//    
//    // TODO: it seems unnecessary to have to load objects into memory before 
//    // we can remove them
//    // load configuration into memory
//    sufg.registerConfigurationSchema();
//    sufg.loadConfiguration();
//    
//    // clear 
//    clearDomainConfiguration(sufg);
//    
//    // create domain configuration
//    createDomainConfiguration(sufg);
//  }
  
//  /**
//   * This is invoked by {@link #createDomainConfiguration(SetUpConfiguration)}.
//   * 
//   * @effects 
//   *  register the <b>helper</tt> domain classes of an application to <tt>schema</tt>
//   */
//  protected Class[] registerHelperDomainClasses() throws DataSourceException, NotPossibleException, NotFoundException {
//    Class[] helperClasses = getHelperDomainClasses(); //getDomainClasses();
//    
//    if (helperClasses == null)
//      return null;
//
//    for (Class c: helperClasses) {
//      if (debug)
//        log(MessageCode.UNDEFINED,
//            "...{0}", c.getSimpleName());
//      schema.registerClass(c);
//    }
//    
//    return helperClasses;
//  }
  
  /**
   * This is invoked by {@link #createDomainConfiguration(SetUpConfigBasic)}.
   * 
   * @effects 
   *  register the domain-specific and system-related model classes that are used by 
   *  an application to <tt>schema</tt>
   */
  protected void registerDomainClasses() throws DataSourceException, NotPossibleException, NotFoundException {
    //v2.7.3 include this as part of the registration of the system and domain classes (below) 
    //registerHelperClasses();
    
    boolean createIfNotExist = true;
    boolean read = false;
    boolean serialised = true;  // v2.8
    
    // v2.7.3: register model classes of the system modules that are used by this application
    Class[] systemModelClasses = getSystemModelClasses();
    if (systemModelClasses != null) {
      log(MessageCode.UNDEFINED,
          //"Tạo mô hình chương trình"
          "Registering system model classes"
          );
      registerClasses(systemModelClasses, serialised, createIfNotExist, read);
    }
    
    Class[] domainClasses = getModelClasses(); //getDomainClasses();
    if (domainClasses == null)
      return ;//null;

    /*v2.7.3: this code is redundant by the code below 
    log(MessageCode.UNDEFINED,
        //"Đăng ký mô hình chương trình"
        "Registering domain classes"
        );    for (Class c: domainClasses) {
      if (c != null) {
        if (debug)
          log(MessageCode.UNDEFINED,
              "...{0}", c.getSimpleName());
        schema.registerClass(c);
      }
    }
    */

    log(MessageCode.UNDEFINED,
        //"Tạo mô hình chương trình"
        "Creating domain classes"
        );
    createIfNotExist = true;
    read = false;
    registerClasses(domainClasses, serialised, createIfNotExist, read);
    
    //return domainClasses;
  }
  
  /**
   * @effects 
   *  register the model classes of the module descriptors (together with their super-classes)
   * @version 2.7.4
   */
  public void registerClasses(Class[] moduleDescrClasses) throws DataSourceException {
    //DSMBasic dsm = dodm.getDsm();
    List<Class> modelClasses = new ArrayList<>();
    getModelClasses(
        //dsm, 
        moduleDescrClasses, modelClasses);
    
    boolean serialised = true;
    
    boolean createIfNotExist=true;
    boolean read = false;
    
    registerClasses(modelClasses.toArray(new Class[modelClasses.size()]), 
        serialised, // v2.8 
        createIfNotExist, read
        //true, false
        );
  }
  
  /**
   * @effects 
   *  <prep>
   *    register classes in DODM
   *    if serialised AND createIfNotExist
   *      create the class store of each class in classes that does not yet exist
   *    
   *    if serialised AND read
   *      read the domain objects (if any) of each class c in classes into c's object pool  
   *  </pre>
   * @version 
   *  2.6.4, 2.7.3
   *  <br>2.8
   */
  // v2.6.4.b
  public void registerClasses(Class[] classes, 
      boolean serialised, // v2.8 
      boolean createIfNotExist, boolean read) throws DataSourceException {
    if (serialised) {
      if (createIfNotExist) {
        // register first
        if (debug) log(MessageCode.UNDEFINED,"    Registering classes");
        dodm.registerClasses(classes);
        
        // then create
        if (debug) log(MessageCode.UNDEFINED,"    Creating classes");
        dodm.addClasses(classes, read);
      } else {
        // register only
        if (debug) log(MessageCode.UNDEFINED,"    Registering classes");
        dodm.addClasses(classes, false, read);
      }
    } else {
      // register only
      if (debug) log(MessageCode.UNDEFINED,"    Registering classes");
      dodm.registerClasses(classes);
    }
  }
  
  /*v2.7.3: replaced by a new API, which uses ModuleDef */
//  protected void registerEnumClasses() throws DataSourceException {
//    //register enum classes
//    Class[] enumClasses = getEnumClasses();
//    if (enumClasses != null) {
//      log(MessageCode.UNDEFINED,"Registering enum classes");
//      for (Class c : enumClasses) {
//        schema.registerEnumInterface(c);
//      }
//    }
//  }
//
//  protected Class[] getEnumClasses() {
//    return null;
//  }
  
  /* v2.7.3: removed 
  protected void registerValueClasses() throws DataSourceException {
    Class[] valueClasses = getValueClasses();
    if (valueClasses != null) {
      log(MessageCode.UNDEFINED,
          //"Tạo dữ liệu"
          "Create default values"
          );
      registerClasses(valueClasses, true, true);
    }
  }

  protected Class[] getValueClasses() {
    return null;
  }
  */

//  protected Class[] getHelperDomainClasses() {
//    return null;
//  }

  /**
   * A method to allow an application to flexibly load a sub-set of the application modules.
   * Applications need to override this method to return the domain classes of those modules
   * @effects 
   *  return the domain classes of the modules to load at application start-up, 
   *  or return <tt>null</tt> if no such classes were specified 
   */
  public Class[] getInputModelClasses() {
    return null;
  }
  
  /**
   * @effects 
   *  return an array, each element of which is a domain class, or return <tt>null</tt> if no such classes were 
   *  specified
   * @version
   *  - 3.2: made public
   */
  public abstract Class[] getModelClasses();

  /**
   * @effects 
   *  return the <b>domain-specific</b> modules, 
   *  or return <tt>null</tt> if no such modules were specified 
   * @version
   *  - 3.2: made public
   */
  public abstract List<List<Class>> getModuleDescriptors();

  /**
   * Sub-types need to override this method if they override the method {@link #getSystemModuleDescriptors()}.
   * 
   * @effects 
   *  return the model classes of the <b>non-default, system </b> modules 
   *  that are to be used with this domain application (i.e. the modules 
   *  defined by {@link #getSystemModuleDescriptors()}), or return <tt>null</tt> if no such classes were 
   *  specified
   * @version
   *  - 3.2: made public
   */
  public Class[] getSystemModelClasses() {
    return null;
  }
  
  /**
   * Sub-types need to override this method if they need to load system modules.
   * 
   * @effects 
   *  return the <b>non-default, system </b> modules that are to be used with this domain application, 
   *  or return <tt>null</tt> if no such modules were specified 
   * @version
   *  - 3.2: made public
   */
  public List<List<Class>> getSystemModuleDescriptors() {
    return null;
  }

  /**
   * This is a helper method for {@link #getModuleDescriptors()} to be used by sub-classes.
   *  
   * @requires 
   *  moduleDescriptors != null
   * @effects 
   *  for each array <tt>Class[]</tt> in <tt>moduleDescriptors</tt>
   *    turn it into a list and add this list to an aggregated list
   *  return the aggregated list 
   */
  protected List<List<Class>> getModuleDescriptors(Class[][] moduleDescriptors) {
    List<List<Class>> mdescrs = new ArrayList();
    List<Class> list;
    for (Class[] md : moduleDescriptors) {
      list = new ArrayList();
      Collections.addAll(list,md);
      mdescrs.add(list);
    }
    return mdescrs;
  }
  
  /**
   * @effects 
   *  return a class object that defines the domain-specific labels.
   */
  //protected abstract Class getDomainLabelConstantClass();
  
  // v2.8
//  /**
//   * @effects 
//   *  return an array of the domain classes of the specified module descriptors, 
//   *  together with their super- and ancestor domain classes (if any).
//   */
//  protected Class[] getModelClasses(Class[][] moduleDescriptors) {
//    return getModelClasses(dodm.getDsm(), moduleDescriptors);
//  }
  
  /**
   * @effects 
   *  return an array of the domain classes of the specified module descriptors, 
   *  together with their super- and ancestor domain classes (if any).
   */
  public static Class[] getModelClasses(
      //v2.8: DSMBasic dsm, 
      Class[][] moduleDescriptors) {
    List<Class> domainClasses = new ArrayList();

    for (Class[] mds : moduleDescriptors) {
      getModelClasses(
          //dsm, 
          mds, domainClasses);
    }
    
    return domainClasses.toArray(new Class[domainClasses.size()]);
  }

  /**
   * @modifies <tt>domainClasses</tt>
   * @effects 
   *  update <tt>domainClasses> with the domain classes of the specified module descriptors, 
   *  together with their super- and ancestor domain classes (if any).
   */
  public static void getModelClasses(
      // v2.8: DSMBasic dsm, 
      Class[] mds, List<Class> domainClasses) {
    ModuleDescriptor moduleDesc;
    final Class<ModuleDescriptor> moduleDescClass = ModuleDescriptor.class;
    Class domainCls;
    
    for (Class md : mds) {
      moduleDesc = (ModuleDescriptor) md.getAnnotation(moduleDescClass);
      if (moduleDesc == null) {
        throw new NotFoundException(NotFoundException.Code.MODULE_DESCRIPTOR_NOT_FOUND, 
            new Object[] {md.getName()});
      }
      domainCls = moduleDesc.modelDesc().model();
      
      /*v2.7.3: recursively find all the super domain classes as well  
      if (domainCls != MetaConstants.NullType && !domainClasses.contains(domainCls)) {
        domainClasses.add(domainCls);
      }
      */
      if (domainCls != CommonConstants.NullType){
        // process class hierarchy first (super classes need to be registered first!!)
        List<Class> clsHier = DSMBasic.getClassHierarchy(domainCls);
        if (clsHier != null) {
          boolean registerHier = false;
          for (Class c : clsHier) {
            if (DSMBasic.isDomainClass(c)) {
              // found a domain class in the hierarchy -> 
              // all super-classes are domain classes -> register them
              registerHier = true; break;
            }
          }
          
          
          if (registerHier) {
            for (Class c : clsHier) {
              if (!domainClasses.contains(c)) {
                domainClasses.add(c);
              }
            }              
          }
        }
        
        // now add the domain class
        if (!domainClasses.contains(domainCls))
          domainClasses.add(domainCls);
      }
    }
  }

  /**
   * @effects 
   *  return an array of the domain classes that contain those in the provided collection 
   *  together with any super- and ancestor domain classes of them.
   */
  public List<Class> getModelClasses(
      // v2.8: DSMBasic dsm, 
      Collection<Class> currClasses) {
    List<Class> domainClasses = new ArrayList();
    
    for (Class domainCls : currClasses) {
      // process class hierarchy first (super classes need to be registered
      // first!!)
      List<Class> clsHier = DSMBasic.getClassHierarchy(domainCls);
      if (clsHier != null) {
        boolean registerHier = false;
        for (Class c : clsHier) {
          if (DSMBasic.isDomainClass(c)) {
            // found a domain class in the hierarchy ->
            // all super-classes are domain classes -> register them
            registerHier = true;
            break;
          }
        }

        if (registerHier) {
          for (Class c : clsHier) {
            if (!domainClasses.contains(c)) {
              domainClasses.add(c);
            }
          }
        }
      }

      // now add the domain class
      if (!domainClasses.contains(domainCls))
        domainClasses.add(domainCls);
    }
    
    return domainClasses;
  }
  

  /**
   * @effects 
   *  load the minimum configuration needed to start the application
   * @version 2.7.1 
   */
  public void loadBaseConfiguration() throws NotPossibleException, DataSourceException {
    initDODM();
    
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Đăng ký mô hình"
          "Loading base configuration"
          );
    
    SetUpConfigBasic sufg = createSetUpConfigurationBasicInstance(); //new SetUpConfiguration(schema);

    sufg.registerConfigurationSchema(this, true);

    /* v2.7: not yet tested
    // load the application-specific configuration
    if (debug)
      System.out.println("Đọc cấu hình chương trình");

    loadApplicationConfiguration();

    // load security configuration
    if (config.getUseSecurity()) {
      if (debug)
        System.out.println("Đọc cấu hình bảo mật");

      loadSecurityConfiguration();
    }
    */
  }
  
  /**
   * @effects 
   *  if exists sub-classes of <tt>c</tt> among the domain classes of the application modules
   *    return them as a Collection
   *  else
   *    return null 
   */
  public Collection<Class> getSubClasses(Class c) {
    Class[] domainClasses = getModelClasses();
    Collection<Class> subs = new ArrayList<Class>();
    for (Class cls: domainClasses) {
      try {
        if (cls != c  // added this check    
            && cls.asSubclass(c) != null) {
          subs.add(cls);
        }
      } catch (ClassCastException e) {
        // ignore
      }
    }

    return (!subs.isEmpty()) ? subs : null;
  }
  
//  /**
//   * @requires 
//   *  configuration schema has been registered
//   * @effects 
//   *  load the main application module and all the application resources needed for this module.
//   *  return the main module or <tt>null</tt> if no such module is found.
//   * @version 2.7.1 
//   */
//  public ApplicationModule loadMainModule() throws NotPossibleException, DataSourceException {
//    // load application-wide configuration first
//    SetUpConfigBasic sufg = getSetUpConfiguration(); //new SetUpConfiguration(schema);
//
//    if (!sufg.isRegisteredConfigurationSchema())
//      throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_SCHEMA_NOT_REGISTERED, 
//          "Chưa đăng ký cấu hình chương trình");
//      
//    ApplicationModule module = sufg.loadMainModule();
//
//    return module;
//  }
  
//  /**
//   * @requires 
//   *  configuration schema has been registered
//   * @effects 
//   *  load the <b>functional</b> modules and all the application resources needed for them.
//   *  return the modules or <tt>null</tt> if no such modules are found.
//   * @version 2.7.1 
//   */
//  public Collection<ApplicationModule> loadFunctionalModules() throws NotPossibleException, DataSourceException {
//    // load application-wide configuration first
//    SetUpConfigBasic sufg = getSetUpConfiguration(); //new SetUpConfiguration(schema);
//
//    if (!sufg.isRegisteredConfigurationSchema())
//      throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_SCHEMA_NOT_REGISTERED, 
//          "Chưa đăng ký cấu hình chương trình");
//      
//    Collection<ApplicationModule> modules = sufg.loadFunctionalModules();
//
//    return modules;
//  }

  // v2.8: not used
//  /**
//   * @effects
//   *  load and return the Configuration object
//   *  
//   * @version 2.7.1
//   */
//  public Configuration loadApplicationConfiguration() throws NotPossibleException, DataSourceException {
//    
//    Class<Configuration> c = Configuration.class;
//    DOMBasic dom = dodm.getDom();
//    
//    if (!dom.isIdRangeInitialised(c)) // v2.8: added this check
//      //v2.8: dom.loadMetadata(c);
//      dom.retrieveMetadata(c);
//    
//    Oid cfgOid = dom.getLowestOid(c);
//    
//    /*v2.8: support memory-based config 
//    config = dom.loadObject(c, cfgOid);
//    */
//    config = dom.retrieveObject(c, cfgOid);
//    
//    /* alternative: 
//    //TODO: change the following if there are more than one Configurations
//    // there is only one configuration so we will search for a match in any value
//    String attribName = Configuration.AttributeName_appName;
//    Op op = Op.MATCH;
//    Object val = "%%";
//    config = schema.loadObject(c, attribName, op, val);
//    */
//    
//    return config;
//  }
  
  /**
   * @effects 
   *  load all security and non-security 
   *  configuration defined by <tt>schema</tt> from 
   *  the database;
   *  initialise {@link #config} with these.
   */
  public void loadConfiguration() throws NotFoundException, 
  NotPossibleException, DataSourceException {
    // load application-wide configuration first
    SetUpConfigBasic sufg = createSetUpConfigurationBasicInstance(); //new SetUpConfiguration(schema);
    
    sufg.loadConfiguration();

    //debug: System.out.println(schema.getDom().getObjects(ApplicationModule.class));
    
    // load domain-specific configuration
    loadApplicationConfigurationWithAssociations();
  }

  /**
   * @effects 
   *  load the application configuration from the data source and initialise {@link #config} with them.  
   */
  private void loadApplicationConfigurationWithAssociations() throws NotFoundException, NotPossibleException, DataSourceException {

    //TODO: this loadings seems redundant, already covered by loadConfiguration (above)
    Class ocls = Organisation.class;
    Class c = Configuration.class;
    try {
      retrieveObjectsWithAssociations(ocls);
    } catch (NotFoundException e) {
      // perhaps no organisation, ignore 
    }
    
    Collection<Configuration> configs = retrieveObjectsWithAssociations(c);
    
    config = configs.iterator().next();
  }

  /**
   * Unlike {@link #createApplicationConfiguration()}, which create a <tt>Configuration</tt> with all settings and 
   * use this to initialise {@link #config}, this method only create a <tt>Configuration</tt> with <b>minimum</b>
   * settings needed to connect to and access the data source. This method does not initialise {@link #config}.
   * 
   * @effects 
   *  create and return a <tt>Configuration</tt> containing only the <b>base</b> configuration settings (e.g. database connection settings)
   */
  public abstract Configuration createInitApplicationConfiguration();
  
  /**
   * Sub-classes must implement this method to initialise a <tt>Configuration</tt>
   * object for the application. 
   * 
   * @effects 
   *  initialise <tt>config</tt> with the domain-specific settings
   */
  public abstract void createApplicationConfiguration() throws NotPossibleException, NotFoundException;
  
  /**
   * @modifies config
   * @effects 
   *  create splash screen info and add to config
   * @version 2.7.4
   */
  protected void createSplashInfo(Configuration config, String logoFileName) {
    ImageIcon appLogo = GUIToolkit.getImageIconOptional(logoFileName, null);
    config.setAppLogo(appLogo);

    Company comp = Company.getInstance();
    
    config.setCompany(comp);
    
    SplashInfo splashInfo = new SplashInfo(config);
    config.setSplashInfo(splashInfo);    
  }
  
  protected void addClass(Class c) throws DataSourceException, NotPossibleException {
    dodm.addClass(c);
  }

  protected void addObject(Object o) throws DataSourceException, NotPossibleException {
    dodm.getDom().addObject(o);
  }

  /**
   * @requires 
   *  c is registered domain class 
   * @effects <pre>
   *  if exist pre-defined <tt>public static</tt> constant objects of type <tt>c</tt>
   *  that are defined in <tt>c</tt>
   *    store those objects to data source
   *  else
   *    do nothing</pre>
   */
  protected <T> void addConstantObjects(Class<T> c) throws NotPossibleException, DataSourceException {
    List<T> objs = Toolkit.getConstantObjects(c, c);
    if (objs != null) {
      for (T o : objs) {
        addObject(o);

        if (debug)
          log(MessageCode.UNDEFINED, "   -> {0}", o);
      }
    }
  }

  /**
   * @requires 
   *  c is registered domain class /\ dataCls contains static constant objects of type <tt>c</tt> 
   * @effects <pre>
   *  if exist pre-defined <tt>public static</tt> constant objects of type <tt>c</tt>
   *  that are defined in <tt>dataCls</tt>
   *    store those objects to data source
   *  else
   *    do nothing</pre>
   */
  protected <T> void addConstantObjects(Class<T> c, Class dataCls) throws NotPossibleException, DataSourceException {
    List<T> objs = Toolkit.getConstantObjects(dataCls, c);
    if (objs != null) {
      Collection oc;
      for (T o : objs) {
        addObject(o);  
        if (debug)
          log(MessageCode.UNDEFINED, "   -> {0}", o);
      }
    }    
  }
  
  protected Collection retrieveObjectsWithAssociations(Class c) 
      throws NotFoundException, NotPossibleException {
    Collection objects = null;
    try {
      // v2.7: should we use this instead? 
      //schema.loadObjectHierarchyWithAssociations(c);      
      objects = dodm.getDom().retrieveObjectsWithAssociations(c);
    } catch (DataSourceException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,  
          //"Không thể thực thi phương thức {0}.{1}", 
          new Object[] {"DomainSchema", "retrieveObjectsWithAssociations", c});
    }
    
    if (objects == null || objects.isEmpty())
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {c, ""});
    
    return objects;
  }

  /**
   * @requires application folder has a "bin" directory which contains two
   *           files: <br>
   *           - runscript_windows <br>
   *           - runscript_unix
   */
  protected void createRunScript() throws IOException {
    /**
     * a run-script has the form appName+ext where ext = .bat for WINDOWS and
     * .bash for others
     * 
     * v2.7.3: improved to use .vbs script wrapper over the .bat script so as to hide the batch console 
     */

    // copy the platform-specific run script to create it

    String scriptFileName = "run";
    // v3.0
    createRunScript(scriptFileName);
  }

  //v3.0
  protected void createRunScript(String scriptFileName) throws IOException {
    String defScript = config.getSetUpFolder() + SEP + DIR_BIN + SEP + scriptFileName;
    String ext;
    
    String os = System.getProperty("os.name").toLowerCase();
//  log(MessageCode.UNDEFINED,
//      //"Hệ điều hành: " + os
//      "Operating system: {0}", os
//      );
    
    if (os.indexOf("windows") > -1) {
      ext = ".bat";
      createMainRunScript(defScript, scriptFileName, ext);
      createRunScriptVbs(defScript, config.getAppName());
    } else {
      ext = ".bash";
      createMainRunScript(defScript, config.getAppName(), ext);
    }    
  }

  // v2.7.3
  private void createMainRunScript(String defScriptPath, String scriptFileName, String ext) throws IOException {
    String defScript = defScriptPath + ext;
    
    File defScriptFile = new File(defScript);

    String scriptFilePath = config.getAppFolder() + SEP + scriptFileName + ext;
    File scriptFile = new File(scriptFilePath);

    log(MessageCode.UNDEFINED,
        //"Tệp chạy nguồn: " + defScriptFile.getName()
        "Template main script: {0}", defScriptFile.getName()
        );
    log(MessageCode.UNDEFINED,
        //"Tệp chạy: " + scriptFile.getName()
        "Program main script: {0}", scriptFile.getName()
        );
    copyFile(defScriptFile, scriptFile);
  }

  // v2.7.3
  private void createRunScriptVbs(String defScriptPath, String scriptFileName) throws IOException {
    // requires 2 files: (1) run.vbs and (2) run.bat 
    String ext = ".vbs";
    String defScript = defScriptPath + ext;
    File defScriptFile = new File(defScript);
    String scriptFilePath = config.getAppFolder() + SEP + scriptFileName + ext;
    File scriptFile = new File(scriptFilePath);

    log(MessageCode.UNDEFINED,
        //"Tệp chạy nguồn: " + defScriptFile.getName()
        "Template VBS script: {0}", defScriptFile.getName()
        );
    log(MessageCode.UNDEFINED,
        //"Tệp chạy: " + scriptFile.getName()
        "Program VBS script: {0}", scriptFile.getName()
        );
    copyFile(defScriptFile, scriptFile);
  }

  /**
   * @effects 
   *  perform post-set up tasks (mostly involved copying resources to the application folder)
   */
  protected void postSetUp() throws IOException {
    log(MessageCode.UNDEFINED,"Post-setup ");

    // v3.1: copy application resources to the application's config folder
    String folderName = "resources";
    String configFolderPath = config.getAppSubDirPath(config.getConfigFolder());
    String targetFolderPath = configFolderPath; // + SEP + folderName;
    /*v3.1: use another method that supports jar-typed storage
    copyDir(SetUpBasic.class, folderName, targetFolderPath);
    */
    ToolkitIO.copyDir(SetUpBasic.class, folderName, targetFolderPath);
    
    OsmConfig osmConfig = config.getDodmConfig().getOsmConfig();
    // v3.0: added this check for JavaDb only
    //TODO: other file-based RDBMS may also need this support
    if (osmConfig.isDataSourceTypeJavaDb()) {
      // copy the data source folder to the application folder
      String dbName = osmConfig.getDataSourceName();
      copyDataSourceFolder(dbName);
    }
  }

  /**
   * @effects 
   *  copy the data source folder of the specified <tt>dataSourceName</tt> to the application folder
   *  
   * @version 3.1
   */
  private void copyDataSourceFolder(final String dataSourceName) throws IOException {
    // for sub-classes to customise
    File dbFolder = new File(config.getSetUpFolder() + SEP + dataSourceName); //config.getDbName());

    // copy the db folder to the application folder
    
    File subDir = null;
    
    if (dataSourceName.indexOf(SEP) > -1) {
      // create each sub-dir in the db path (if not exists)
      String[] subDirs = dataSourceName.split("\\"+SEP);
      StringBuffer newSubDir = new StringBuffer(config.getAppFolder());
      for (String subDirName : subDirs) {
        newSubDir.append(SEP).append(subDirName);
        subDir = new File(newSubDir.toString());
        if (!subDir.exists()) { // create subDir if not exists
          boolean ok = subDir.mkdir();
          if (!ok)
            throw new IOException("Failed to create folder: " + subDir.getAbsolutePath());
        }
      }
    } else {
      subDir = new File(config.getAppFolder() + SEP + dataSourceName);
    }
    
    // now ready to copy
    //File dest = new File(config.getAppFolder() + SEP + dbName);
    File dest = subDir;
    
    copyDir(dbFolder, dest);    
  }

  public String getAppName() {
    return config.getAppName();
  }
  
// v3.1: moved to ToolkitIO
//  /**
//   * <b>IMPORTANT</b> This method does not work if <tt>c</tt> is stored in a jar file. 
//   * In such case use {@link ToolkitIO#copyDir(Class, String, String)} instead.
//   * 
//   * @requires 
//   *  srcFolderName must be a single name (i.e. not containing any path elements) 
//   *  
//   * @effects
//   *  if exists a sub-directory named <tt>srcFolderName</tt> stored in the source directory 
//   *  of <tt>c</tt>
//   *    copy its entire content to <tt>targetPath</tt> (folder at <tt>targetPath</tt> is created
//   *    if not already exists)
//   *  ; else
//   *    do nothing
//   *  
//   *  <p>throws IOException if failed.
//   * @version 3.1
//   */
//  public void copyDir(Class c, String srcFolderName, String targetPath) throws IOException {
//    String srcFolderPath = ToolkitIO.getPath(c, srcFolderName);
//    if (srcFolderPath != null) {
//      File srcFolder = new File(srcFolderPath);
//      File targetFolder = new File(targetPath);
//      
//      copyDir(srcFolder, targetFolder);
//    }
//  }

  /**
   * @effects 
   *  copy the complete content directory tree of the source directory <tt>sd</tt>
   *  (except <tt>sd</tt> itself)
   *  to the target directory <tt>dd</tt>. 
   *  Target directory <tt>dd</tt> is created if not already exists; 
   *  target files with same name are overriden;  
   *  target sub-folders with same name are merged
   *   
   *  <p>throws IOException if failed
   */
  public void copyDir(File sd, File dd) throws IOException {
    log(MessageCode.UNDEFINED,"  Copying folder: {0} -> {1}", sd, dd);

    // create destination dir first
    if (!dd.exists()) {
      boolean ok = dd.mkdir();
      if (!ok)
        throw new IOException("Không thể tạo thư mục " + dd.getAbsolutePath());
    }

    File[] files = sd.listFiles();
    if (files != null) {
      File df;
      for (File f : files) {
        df = new File(dd + SEP + f.getName());
        if (f.isDirectory()) { // sub-directory
          // recursive
          copyDir(f, df);
        } else { // file
          copyFile(f, df);
        }
      }
    }
  }

  /**
   * @effects 
   *  Copy the file content captured in <tt>fins</tt> to <tt>dest</tt>
   */
  public void copyFile(InputStream fins, String dest) throws IOException {
    File destFile = new File(dest);
    
    // create destination file first
    if (!destFile.exists()) {
      boolean ok = destFile.createNewFile();
      if (!ok)
        throw new IOException("Không thể tạo file " + destFile.getAbsolutePath());
    }
    
    copyFile(fins, destFile);
  }
  
  /**
   * @requires 
   *  dest exists
   *  
   * @effects 
   *  Copy the file content captured in <tt>fins</tt> to <tt>dest</tt>
   */
  protected void copyFile(InputStream is, File dest) throws IOException {
    OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    byte[] data = new byte[1000];
    int count;
    while ((count = is.read(data, 0, 1000)) != -1) {
      out.write(data, 0, count);
    }
    out.flush();
    out.close();    
  }

  /**
   * @effects 
   *  if <tt>dest</tt> does not exist then create new
   *  copy content of <tt>src</tt> to <tt>dest</tt>, overriding if exists
   *  
   *  <p>throws IOException if failed
   */
  protected void copyFile(File src, File dest) throws IOException {
    // create destination file first
    if (!dest.exists()) {
      boolean ok = dest.createNewFile();
      if (!ok)
        throw new IOException("Không thể tạo file " + dest.getAbsolutePath());
    }
    
    /*v2.7.4: moved to method
    InputStream is = new FileInputStream(src);
    
    OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
    byte[] data = new byte[1000];
    int count;
    while ((count = is.read(data, 0, 1000)) != -1) {
      out.write(data, 0, count);
    }
    out.flush();
    out.close();
    */
    InputStream is = new FileInputStream(src);

    copyFile(is, dest);
    is.close();
  }
  
  protected void delDir(File dir) throws IOException {
    // delete all files and sub-dirs first
    File[] files = dir.listFiles();
    for (File f : files) {
      if (f.isDirectory()) { // sub-directory
        // recursive
        delDir(f);
      } else { // file
        f.delete();
      }
    }

    // then del the dir
    dir.delete();
  }

  /**
   * @effects 
   *  return this.schema
   */
  public DODMBasic getDODM() {
    return dodm;
  }
  
//  /**
//   * @effects 
//   *  If a domain user whose login=<tt>userName</tt> and whose password=<tt>password</tt> 
//   *  is found
//   *    return the object
//   *  else 
//   *    throw NotFoundException
//   *  
//   *  If an error occured in getting objects from database
//   *    throw NotPossibleException
//   */
//  public DomainUser getDomainUser(String userName, String password) 
//  throws NotFoundException, NotPossibleException {
//    final Expression.Op eq = Expression.Op.EQ;
//    Query q = new Query(new Expression(
//        "login", eq, userName, 
//        Expression.Type.Object
//        ));
//    q.add(new Expression(
//        "password", eq, password, 
//        Expression.Type.Object));
//    
//    final Class c = DomainUser.class;
//    
//    Collection<DomainUser> objects = dodm.getDom().getObjects(c, q);
//    if (objects == null)
//      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
//          "Không tìm thấy đối tượng {0}<{1}>",c,q);
//    
//    return objects.iterator().next();
//  }
  
  
  /**
   * @effects 
   *    print log message for <tt>code</tt>, where <tt>message</tt> takes the format specified by <tt>MessageFormat</tt>.
   * @version 
   *  - 3.0: improved to support state change listeners<br>
   *  - 5.1 code may be null
   */
  public void log(MessageCode code, String message, Object...data) {
    boolean hasStateChanged = hasStateChangedListeners();
    
    if (debug || loggingOn 
        || hasStateChanged  // v3.0
        ) {
      //TODO: convert message based on language
      if (data != null && data.length > 0) {
        Format fmt = new MessageFormat(message);
        message = fmt.format(data);
        //message = String.format(message, data);
      }
    }

    if (debug || loggingOn)
      System.out.println(message);

    if (hasStateChanged) {
      /* v3.0: use StringBuffer
      this.status = message;
      */
      if (status == null) status = new StringBuffer();
      status.append(message).append("\n");
      
      /*v3.0: use threaded
      fireStateChanged();
      */
      fireStateChanged(true);
    }
  }

//  /**
//   * <b>IMPORTANT</b> This method does not work if <tt>c</tt> is stored in a jar file. 
//   * In such case use {@link ToolkitIO#copyDir(Class, String, String)} instead.
//   * 
//   * @requires 
//   *  targetPath is an absolute path to a folder that exists
//   *  
//   * @effects 
//   *  if exists the sub-directory named <tt>srcDirName</tt> relative to the 
//   *    package directory of the class <tt>c</tt> 
//   *    copy all files from that sub-directory in to the target directory <tt>target</tt>
//   *  
//   *  <p>Throws NotPossibleException if failed for some reasons.
//   *  
//   * @version 2.7.4
//   */
//  public void copyFiles(Class c, String srcDirName, String targetPath) throws NotPossibleException {
//    /*v3.1: moved to ToolkitIO
//    InputStream fins;
//    String destFile = null;
//    
//    URL srcDirUrl = c.getResource(srcDirName);
//    if (srcDirUrl != null) {
//      // get all files and copy
//      File srcDir = new File(srcDirUrl.getPath());
//      File[] files = srcDir.listFiles();
//      if (files != null && files.length > 0) {
//        for (File file : files) {
//          if (file.isDirectory()) // skip directories
//            continue;
//          
//          try {
//            fins = new FileInputStream(file);//c.getResourceAsStream(dirName + File.separator + tempFile);
//            destFile = targetPath + File.separator + file.getName();
//            copyFile(fins, destFile);
//          } catch (IOException e) {
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_FILE, e, 
//                new Object[] {file, destFile});
//          }
//        }        
//      }
//    }
//    */
//    boolean skipSrcDir = true;
//    try {
//      ToolkitIO.copyDir(c, srcDirName, targetPath, skipSrcDir);
//    } catch (IOException e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COPY_DIR, e, 
//          new Object[] {srcDirName, targetPath});
//    }
//  }

  /**
   * @effects 
   *  if exists file <tt>srcDirName/fileName</tt> located in the class folder of <tt>c</tt>
   *    return an <tt>InputStream</tt> of the file
   *  
   *  <p>throws NotPossibleException if failed to do the above. 
   */
  public InputStream getFileAsStream(Class c, String srcDirName,
      String fileName) throws NotPossibleException {
    InputStream fins;
    String destFile = null;
    
    URL srcDirUrl = c.getResource(srcDirName);
    if (srcDirUrl != null) {
      // get all files and copy
      File srcDir = new File(srcDirUrl.getPath());
      File[] files = srcDir.listFiles();
      if (files != null && files.length > 0) {
        for (File file : files) {
          if (file.isDirectory()) // skip directories
            continue;
          
          if (file.getName().equals(fileName)) {
            // found 
            try {
              fins = new FileInputStream(file);
              return fins;
            } catch (FileNotFoundException e) {
              throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {file.getPath()});
            }
          }
        }
      }
    } else {
      throw new NotFoundException(NotFoundException.Code.FOLDER_NOT_FOUND, 
          new Object[] {c.getSimpleName() + "/" + srcDirName});      
    }

    // if we get here then file is not found
    throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, new Object[] {
        new Object[] {c.getSimpleName() + "/" + srcDirName + "/" + fileName}
    });
  }

  
  public String getStatus() {
    return (status != null) ? status.toString() : null;
  }

  public void setCommand(Cmd command) {
    this.command = command;
  }
  
  public Cmd getCommand() {
    return command;
  }

  
  /**
   * Return the application configuration object
   * @overview
   *  return <tt>config</tt> 
   */
  public Configuration getConfig() {
    return config;
  }
  
  /**
   * <b>IMPORTANT</b>: this method MUST ONLY USED by the SetUpProgram.
   * @effects 
   *  set this.config = config
   */
  public void setConfig(Configuration config) {
    this.config = config;
  }

  /**
   * @effects 
   *  clear any <b>derived</b> resources 
   *  (including DODM's) that have been used to run this so far.
   *  <p>Note: these exclude resources that were passed as input to create this (e.g. configuration)
   * @version 2.8
   */
  public void clear() {
    // v3.0: dodm = null;
    clearDODM();
    
    initialSuCfg = null;
    
    suCfg = null; // v4.0
    
    // v3.0
    runFireStateChanged=null;
    status = null;
  }

  /**
   * @effects 
   *  if dodm is not null
   *    nullify and remove this.dodm
   *  
   * @version 3.0
   */
  private void clearDODM() {
    //remove dodm from cache
    if (dodm != null) {
      dodm.close();
    
      // nullfy
      dodm = null;
    }
  }

  /**
   * @requires 
   *  moduleDescrCls is a valid module configuration class
   * @effects
   *  return {@link ModuleDescriptor} of <tt>moduleDescrCls</tt> or 
   *  
   *  throws NotFoundException if not found 
   */
  public ModuleDescriptor getModuleDescriptor(Class moduleDescrCls) throws NotFoundException {
    ModuleDescriptor moduleCfg = (ModuleDescriptor) 
        moduleDescrCls.getAnnotation(ModuleDescriptor.class);
    
    if (moduleCfg == null)
      throw new NotFoundException(NotFoundException.Code.MODULE_DESCRIPTOR_NOT_FOUND, 
          new Object[] {moduleDescrCls.getName()});
    
    return moduleCfg;
  }

  /**
   * @effects 
   *  if dodm = null
   *    return false
   *  else
   *    return true
   * @version 3.3
   */
  public boolean isDodmInit() {
    return dodm != null;
  }

  /**
   * v2.7.4: MUST NOT DO THIS!!!!
   * - cause NullPointerException when running the application
   */
//  @Override
//  public void finalize() {
//    if (dodm!=null)
//      dodm.close();
//  }
  
//  /**
//   * @effects 
//   *  register all classes needed to run an application that are NOT associated 
//   *  with any modules (these classes are registerd by the controllers instead).
//   * @version 2.7.3
//   */
//  public void registerHelperClasses() throws DataSourceException {
//    /* v2.7.3: get helper classes from the configuration or from the model classes and register them 
//    registerEnumClasses();
//    
//    // v2.7.3: removed 
//    // registerValueClasses();
//    
//    // v2.7.2
//    registerHelperDomainClasses();
//    */
//  }

  /**
   * @effects return <tt>Class[]</tt> array of all 
   *  <b>serialisable</tt> the domain classes of this application.
   */
//  protected  Class[] getDomainClasses() {
//    Object[][] moduleCfgs = getDomainConfiguration();
//    
//    if (moduleCfgs != null) {
//      List<Class> classes = new ArrayList();
//      Class c;
//      for (Object[] cfg : moduleCfgs) {
//        c = (Class) cfg[1];
//        if (c != null)
//          classes.add(c);
//      }
//      
//      return (Class[])classes.toArray(new Class[classes.size()]);
//    }
//
//    return null;
//  }
  
//  /**
//   * @effects 
//   *  return <b>all</b> domain classes
//   */
//  private Class[] getAllDomainClasses(){
//    // return all the serializable classes in module configuration
//    List<Class> classes = new ArrayList();
    
//    Class[] clsArr = getValueClasses();
//    if (clsArr != null) {
//      for (Class cls : clsArr) {
//        classes.add(cls);
//      }
//    }

//    Class[] clsArr = getDomainClasses();
//    if (clsArr != null) {
//      for (Class cls : clsArr) {
//        if (cls != null && !classes.contains(cls)) {
//          classes.add(cls);
//        }
//      }
//    }
//    
//    return (classes.isEmpty()) ? null : (Class[]) classes.toArray(new Class[classes.size()]);
//  }
}
