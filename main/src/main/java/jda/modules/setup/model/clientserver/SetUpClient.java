package jda.modules.setup.model.clientserver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.util.SwTk;

/**
 * @overview
 *  A sub-type of {@link SetUp} that is used specifically for setting up a client host in a 
 *  client-server configuration. 
 *  
 *  <p>A key difference between a client and server in such configuration is that it requires  
 *  the key settings that make up the <b>initial</b> {@link Configuration} to be stored in a file.
 *  At launch-time, the client will read this file to determine how to connect to the server.
 *  
 * @author dmle
 * 
 * @version 
 * - 3.3: change from abstract to normal class
 */
public // v3.3: abstract 
  class SetUpClient extends SetUp {

  private SetUp su;

  /**
   * @effects 
   *  initialise this with an instance of the base set-up class specified by {@link #getBaseSetUpClass()}
   *  
   *  <p>throws NotPossibleException if failed.
   */
  public SetUpClient() throws NotPossibleException {
    super();
    Class<? extends SetUp> baseSetUpCls = getBaseSetUpClass();
    
    if (baseSetUpCls == null) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT_SETUP_CLASS, 
          new Object[] {"null"});
    }
    
    
    try {
      su = baseSetUpCls.newInstance();
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {baseSetUpCls.getName()});
    }
  }
  
  /**
   * @effects 
   *  return the base set-up class whose configuration is used for running this, or 
   *  return null if no such class is specified
   * @version 3.0
   */
  public // v3.3: abstract 
    Class<? extends SetUp> getBaseSetUpClass() {
    // v3.3: by default use the the set-up class specified in system property (if specified)  
    Class<? extends SetUp> setUpCls = SwTk.getSetUpClassFromProperty();
    
    if (setUpCls != null) {
      return setUpCls;
    } else {
      return null;
    }
  }
  
  @Override
  public Configuration createInitApplicationConfiguration() {
    // basically same as the server, except for the application name
    Configuration config = su.createInitApplicationConfiguration();
    
    /*v3.1: use the same settings as the main to avoid a bug in not detecting the application 
     * folder in the main configuration that is loaded from the data source  
    String appName = config.getAppName()+"Client";
    
    config.setAppName(appName);
    config.setAppFolder(ApplicationToolKit.getWellKnownAppFolder(appName));
    
     */
    return config;
  }
  
  /**
   * @effects 
   *  initialise <tt>config</tt> to the <b>initial</b><tt>Configuration</tt>.
   *   
   * @version 2.8
   */
  @Override
  public void createApplicationConfiguration() {
    // create just the initial configuration
    config = createInitApplicationConfiguration();
  }
  
  /**
   * @effects 
   *  initialise <tt>DODM</tt> instance <b>without</b> starting the Java Db server process.
   */
  @Override
  public DODMBasic initDODM() throws DataSourceException {
    return super.initDODM(false);
  }

  /**
   * @effects  <pre>
   * performs a sub-set of the setup-tasks, which 
        EXCLUDES the followings:
          setUpConfiguration
          setUpSecurity
          postSetUpDB
       INCLUDES the followings:
          store (initial) Configuration to file {@link SetUp#CONFIG_CLIENT_FILE_NAME}
   *  </pre> 
   */
  @Override
  protected void runSetUp(boolean serialisedConfig) throws IOException,
      DataSourceException {
    
    /* subset (1) */
    printSettings();

    // init dodm
    initDODM();
    
    // test connection to db
    //getDODM().connectToDataSource();

    /* INCLUDES: store initial configuration to file (must do this before preSetUp so that it can be copied over) */
    String clientCfgFile = getConfigFilePath(SetUp.CONFIG_CLIENT_FILE_NAME);
    saveConfigurationToFile(clientCfgFile);

    // copy resources to application's folder
    preSetUp();
    
    // v3.1: perform post-setup tasks of the modules which involve copying resource files
    // to the application folder
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();
    
    // system modules
    List<List<Class>> moduleDescrLists = su.getSystemModuleDescriptors();
    if (moduleDescrLists != null) {
      for (List<Class> sysModuleDescrs : moduleDescrLists) {
        postSetUpModule(sufg, sysModuleDescrs);
      }
    }
    
    // domain modules 
    moduleDescrLists = su.getModuleDescriptors();
    if (moduleDescrLists != null) {
      for (List<Class> sysModuleDescrs : moduleDescrLists) {
        postSetUpModule(sufg, sysModuleDescrs);
      }
    }
    // END v3.1
    
    /* subset (2) */
    createRunScript();

    postSetUp();
  }

  @Override
  protected void createRunScript() throws IOException {
    String scriptFileName = "runClient";
    createRunScript(scriptFileName);
  }

  /**
   * @effects 
   *  invoke super AND
   *  copy <tt>config</tt> sub-folder over
   */
  @Override
  protected void preSetUp() throws IOException {
    super.preSetUp();
    
    // copy config dir over
    File configFolder = new File(getConfigFolderPath());
    
    File configFolderApp = new File(getConfigFolderAppPath());
    
    copyDir(configFolder, configFolderApp);
  }

  /**
   * @effects 
   *  load <tt>Configuration</tt> from file {@link SetUp#CONFIG_CLIENT_FILE_NAME}
   */
  @Override
  protected Configuration loadInitApplicationConfiguration() {
    String clientCfgFile = getConfigFilePath(SetUp.CONFIG_CLIENT_FILE_NAME);
    Configuration config = readConfigurationFromFile(clientCfgFile);
    
    return config;
  }
}
