package jda.modules.setup.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.dodm.DODM;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.ds.SimpleDataFileLoader;
import jda.modules.javadbserver.model.JavaDbServer;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.OsmClientServerConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.module.Module;
import jda.util.SwTk;

/**
 * Represents the 'Set-Up' class, responsible for setting up an application.
 * 
 * <p>
 * Typical set-up tasks include:
 * <ol>
 * <li>Set up the application folder
 * <li>Set up the database and its tables
 * <li>Set up the default configuration settings (if any)
 * <li>Create the application run script
 * </ol>
 * 
 * @author dmle
 * 
 */
public abstract class SetUp extends SetUpBasic {
  
  protected static final String CONFIG_CLIENT_FILE_NAME = "init-client.cfg";
  protected static final String CONFIG_SERVER_FILE_NAME = "init-server.cfg";
  
  public static final String CONFIG_FOLDER_NAME = "config";

  // v2.7.3: 
  private JavaDbServer dbServer;
  
  // v2.7.2:
  private SetUpSecurity sus;
  
  /** the initial configuration needed to get the application started. It is initialised by the result of the method 
   * {@link #loadInitApplicationConfiguration()} and typically includes 
   * settings that specify the data source connection, which are used by {@link #initDODM(boolean)}
   * to initialise a {@link DODMBasic} instance.
   * 
   * <p>In certain types of application (e.g. client/server), the initial configuration of the client 
   * has a more important role of containing client-specific configuration data (e.g. application folder
   * path), which are needed for each client to run. For these types of application, {@link #initConfig}
   * needs to be merged with {@link #config} to create the client-specific configuration.
   * 
   * @version 3.1
   */
  private Configuration initConfig;

  
//  /* (non-Javadoc)
//   * @see domainapp.basics.setup.SetUpBasic#getAppName()
//   */
//  /**
//   * @effects 
//   * 
//   * @version 4.0 
//   */
//  @Override
//  public String getAppName() {
//    Configuration config = getConfig();
//    if (config != null) {
//      return config.getAppName();
//    } else {
//      throw new NotPossibleException(NotPossibleException.Code.NULL_POINTER_EXCEPTION, new Object[] {Configuration.class.getSimpleName(), ": an object is required but not specified"});
//    }
//  }

  
//  /* (non-Javadoc)
//   * @see domainapp.basics.setup.SetUpBasic#getConfig()
//   */
//  /**
//   * @effects 
//   * 
//   * @version 4.0
//   */
//  @Override
//  public Configuration getConfig() {
//    if (config != null)
//      return config;
//    else
//      return initConfig;
//  }


  protected void runSetUp(boolean serialisedConfig) throws IOException, DataSourceException {
    runAllConfig();
    
    // set up basic security resources
    setUpSecurity(serialisedConfig);

    // for sub-classes to add other routines
    runAllFinalise(); 
  }
  
  /**
   * @effects 
   *  Starts a JavaDB server running on a host/port specified in <tt>osmCfg</tt>
   *  
   *  <p>If there is already a server running at the specified host/port then do nothing
   *   
   *  <p>Throws NotPossibleException if no server protocol is specified or failed to start the server
   */
  private void startOsmJavaDbServer(OsmClientServerConfig osmCfg) throws NotPossibleException {
    ProtocolSpec serverProt = osmCfg.getServerProtocolSpec();
    
    if (serverProt == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
    }
    
    if (dbServer == null) {
      dbServer = new JavaDbServer(serverProt);
      
      if (!dbServer.isPortAvailable()) {
        // a server is already running by another JVM
        //throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_USE_PORT, 
        //    new Object[] {serverProt.getPort()});
      } else {
        dbServer.start();
      }
    } else if (!dbServer.isRunning()) {
      dbServer.start();
    }
  }
  
  /**
   * @effects 
   *  Starts a JavaDB server running on a host/port specified in the initial application configuration
   *  
   *  <p>If there is already a server running at the specified host/port then do nothing
   *   
   *  <p>Throws NotPossibleException if no server protocol is specified or failed to start the server
   */
  private void startOsmJavaDbServer() throws NotPossibleException {
    if (debug)
      log(MessageCode.UNDEFINED,
          "Starting JavaDb server"
          );
    
    Configuration initCfg = createInitApplicationConfiguration();
    OsmClientServerConfig osmCfg = (OsmClientServerConfig) initCfg.getDodmConfig().getOsmConfig();
    
    startOsmJavaDbServer(osmCfg);
  }
  
  /**
   * @effects 
   *  If there is a JavaDB server running on a host/port specified in the initial application configuration
   *    stop it 
   *  else 
   *    do nothing
   *   
   *  <p>Throws NotPossibleException if no server protocol is specified or failed to start the server
   */
  private void stopOsmJavaDbServer() throws NotPossibleException {
    if (debug)
      log(MessageCode.UNDEFINED,
          "Stopping JavaDb server"
          );
    
//    // if there is a dbServer instance running then use it; otherwise create a new instance
//    if (dbServer == null) {
//      Configuration initCfg = createInitApplicationConfiguration();
//      
//      OsmClientServerConfig osmCfg = (OsmClientServerConfig) initCfg.getDodmConfig().getOsmConfig();
//      
//      ProtocolSpec serverProt = osmCfg.getServerProtocolSpec();
//      
//      if (serverProt == null) {
//        throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
//      }
//      
//      dbServer = new JavaDbServer(serverProt);
//    }
    
    if (dbServer != null && dbServer.isRunning())
      dbServer.stop();
  }

  
  /* (non-Javadoc)
   * @see domainapp.basics.setup.SetUpBasic#registerConfigurationSchema()
   */
  /**
   * @effects 
   *  Similar to {@link SetUpBasic#registerConfigurationSchema()}, except that it also 
   *  supports the serialised-configuration option.
   *  
   * @version 4.0
   */
  @Override
  public void registerConfigurationSchema() throws DataSourceException {
    // support serialisedConfig option
    log(MessageCode.REGISTER_CONFIGURATION,
        //"Đăng ký cấu hình"
        "Registering configuration..."
        );
     
     if (!isDodmInit()) {
       initDODM();
     }

     boolean serialisedConfig = isSerialisedConfiguration();
     boolean createIfNotExist = true;
     
     SetUpConfigBasic sufg = createSetUpConfigurationInstance();
     
     sufg.registerConfigurationSchema(this, serialisedConfig, createIfNotExist);
  }


  /**
   * @requires 
   *  configuration schema has been registered (by calling {@link #registerConfigurationSchema()}).
   * 
   * @effects 
   *  create only the base configuration (i.e. without the domain module configurations) 
   *  
   * @version 4.0
   */
  public void createBaseConfiguration() throws DataSourceException,
      NotPossibleException {
    log(MessageCode.CREATE_CONFIGURATION,
        //"Cài đặt cấu hình"
        "Creating the base configuration"
        );
    
    if (!isDodmInit()) {
      initDODM();
    }

    final boolean serialisedConfig = isSerialisedConfiguration();
    
    /**
     * Default configuration
     */
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();    

    // delete first
    if (serialisedConfig)
      sufg.clearConfigurationSchema();
    
    // then create
    sufg.createConfiguration(this, serialisedConfig);
  }
  
  /**
   * @effects 
   *  similar to {@link SetUpBasic#createConfiguration()}, except that it also supports the serialised option 
   *  
   * @version 2.8
   */
  @Override
  protected void createConfiguration(boolean serialised) throws DataSourceException,
      NotPossibleException {
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
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();
    
    boolean createIfNotExist = true;
    
    sufg.registerConfigurationSchema(this, 
        serialised, 
        createIfNotExist
        );

    if (serialised)
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
    
    setIsConfigured(true);
  }

  /**
   * Unlike {@link #createConfiguration(boolean)}, which creates the module configurations of the entire application, this method 
   * creates the actual {@link Module} object for one specified module.
   * 
   * @effects 
   *  create and return {@link ApplicationModule} created from <tt>moduleCfgCls</tt>    
   * 
   * @requires 
   *  the underlying configuration schema has been created 
   *     
   * @version 4.0
   */
  public ApplicationModule createModuleConfig(Class moduleCfgCls) throws DataSourceException,
      NotPossibleException {
    return createModuleConfigs(moduleCfgCls).iterator().next();
  }

  /**
   * @effects
   *  if there are dependent (i.e. child) modules defined (recursively) in <tt>moduleCfgClasses</tt> 
   *    return them all as <tt>Collection</tt>
   *  else
   *    return null 
   * 
   * @version 4.0
   *
   */
  public List<Class> getDependencyModules(Class...moduleCfgClasses) {
    if (moduleCfgClasses == null) return null;
    
    List<Class> dependsOn = new ArrayList<>();
    
    for (Class moduleCfgCls : moduleCfgClasses) {
      getDependencyModules(moduleCfgCls, dependsOn);
    }
    
    if (dependsOn.isEmpty())
      return null;
    else
      return dependsOn;
  }
  
  /**
   * @modifies dependsOn
   * @effects 
   *  if there are dependent (i.e. child) modules defined (recursively) in <tt>moduleCfgCls</tt> 
   *    add them all to <tt>dependsOn</tt>
   *  else
   *    do nothing
   * @version  4.0
   */
  private void getDependencyModules(Class moduleCfgCls,
      Collection<Class> dependsOn) {
    ModuleDescriptor md = (ModuleDescriptor) moduleCfgCls.getAnnotation(ModuleDescriptor.class);
    
    if (md != null) {
      Class[] dependsOnArr = md.childModules();
      if (dependsOnArr.length > 0) {
        // has dependencies: add those not already contained in dependsOn
        for (Class dependOnMd : dependsOnArr) {
          if (!dependsOn.contains(dependOnMd)) {
            dependsOn.add(dependOnMd);
            
            // recursively find the dependencies in dependOnMd 
            getDependencyModules(dependOnMd, dependsOn);
          }
        }
      }
    }
  }

  /**
   * This method differs from {@link #createConfiguration(boolean)} in that 
   * it only creates the actual {@link ApplicationModule} objects for some specified modules (not all modules of the application),
   * 
   * @effects 
   *  create and return {@link ApplicationModule}[] created from <tt>moduleCfgClasses</tt> 
   * 
   * @requires 
   *  moduleCfgClasses != null /\ the underlying configuration schema has been created 
   *     
   * @version 4.0
   */
  public Collection<ApplicationModule> createModuleConfigs(Class...moduleCfgClasses) throws DataSourceException,
      NotPossibleException {
    if (moduleCfgClasses == null)
      return null;
    
    log(MessageCode.CREATE_CONFIGURATION,
        //"Cài đặt cấu hình"
        "Creating configuration for the specified modules and their dependencies (if any)..."
        );

    // initialise DODM if needed
    if (!isDodmInit()) {
      initDODM();
    }

    boolean serialisedConfig = isSerialisedConfiguration();
    
    // register all the related domain classes
    registerDomainClasses(moduleCfgClasses); 

    // create the module configuration objects
    Map<String,Label> labelMap = getModuleLabels(moduleCfgClasses);
    SetUpConfigBasic sufg = createSetUpConfigurationInstance();
    Collection<ApplicationModule> moduleCfgs = sufg.createModules(this, moduleCfgClasses, labelMap, serialisedConfig);

    return moduleCfgs;
  }
  
  /**
   * @requires 
   *  moduleCfgClasses != null
   *  
   * @effects 
   *  Register the domain classes of the modules specified in <tt>moduleCfgClasses</tt>.
   *  Create the class stores of these classes (if needed).  
   *
   *   
   * @version 4.0 
   */
  public void registerDomainClasses(Class...moduleCfgClasses) throws DataSourceException {
    if (moduleCfgClasses == null)
      return;
    
    log(MessageCode.UNDEFINED,
        //"Tạo mô hình chương trình"
        "Registering domain classes of the specified modules..."
        );
    
    List<Class> domainClasses = new ArrayList<>();
    getModelClasses(moduleCfgClasses, domainClasses);
    
    if (!domainClasses.isEmpty()) { 
      boolean serialised = true;
      boolean createIfNotExist = true;
      boolean read = false;
      registerClasses(domainClasses.toArray(new Class[domainClasses.size()]), serialised, createIfNotExist, read);
    }
  }

  @Override
  public Class[] getModelClasses() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<List<Class>> getModuleDescriptors() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void runExtended(Cmd cmd, String[] args) throws DataSourceException, IOException {
    boolean serialisedConfig = isSerialisedConfiguration();
    
    if (cmd == Cmd.StartJavaDbServer) {
      // starts java db server
      startOsmJavaDbServer();
    } else if (cmd == Cmd.StopJavaDbServer) {
      // stops java db server
      stopOsmJavaDbServer();
    } else if (cmd == Cmd.PostSetUpModule) {
      postSetUpModule(args);
    }
    else if (cmd == Cmd.ConfigureSecurity) {
      createSecurityConfiguration(serialisedConfig);
    } else if (cmd == Cmd.ConfigureDomainSecurity) { // v3.3
      String[] classNames = Toolkit.subArray(args, 1);
      createDomainSecurityConfiguration(serialisedConfig, classNames);
    }
    else if (cmd == Cmd.SetUpSecurity) {
      setUpSecurity(serialisedConfig);
    } else if (cmd == Cmd.RegisterSecuritySchema) {  // v3.3
      registerSecuritySchema(serialisedConfig);
    } else if (cmd == Cmd.DeleteSecuritySchema && serialisedConfig) {
      deleteSecuritySchema();
    } else if (cmd == Cmd.DeleteSecurityConfiguration && serialisedConfig) {
      deleteSecurityConfiguration();
    } else if (cmd == Cmd.SaveInitClientConfiguration) {
      String cfgFile = getConfigFilePath(SetUp.CONFIG_CLIENT_FILE_NAME);
      saveConfigurationToFile(cfgFile);
    } else if (cmd == Cmd.SaveInitServerConfiguration) {
      String cfgFile = getConfigFilePath(SetUp.CONFIG_SERVER_FILE_NAME);
      saveConfigurationToFile(cfgFile);
    } else if (cmd == Cmd.CreateDomainDataSet) { // v3.3
      String[] classNames = Toolkit.subArray(args, 1);
      createDomainDataSet(classNames);
    } else if (cmd == Cmd.CreateBasicDomainData) { // v3.2c
      createBasicDomainData();
    } else if (cmd == Cmd.CreateDemoDomainData) { // v3.2c
      createDemoDomainData();
    }
    else {
      log(MessageCode.ERR_COMMAND_NOT_EXECUTABLE, "Command not executable: ",cmd);
    }
  }

  /**
   * @requires 
   *  args != null /\ args.length=1 /\ args[0] is a valid module descriptor class name 
   * @effects 
   *  if the module specified by <tt>args[0]</tt> has post-setup command 
   *    perform it
   *  else
   *    do nothing
   *  
   * <p>throws NotPossibleException, NotFoundException if failed
   */
  protected void postSetUpModule(String[] args) throws NotPossibleException, NotFoundException {
    String moduleCfgName = getArg(args, 1); 
    
    if (moduleCfgName == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_INPUT_MODULE);
    }
    
    // load the specified module
    Class moduleDescCls;
    try {
      moduleDescCls = Class.forName(moduleCfgName);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, 
          new Object[] {moduleCfgName}); 
    }
    
    // run post-set up of the specified module
    SetUpConfigBasic suCfg = createSetUpConfigurationInstance();
    
    suCfg.postSetUpModule(this, moduleDescCls);
  }

  /**
   * @effects 
   *  use <tt>suCfg</tt> to perform post-setup commands (if any) that are specified by 
   *  each of the module descriptor classes in <tt>moduleDescClasses</tt>
   *  
   * <p>throws NotPossibleException if failed.
   * 
   * @version 3.1
   */
  protected void postSetUpModule(SetUpConfigBasic suCfg, List<Class> moduleDescClasses) throws NotPossibleException {
    for (Class moduleDescCls : moduleDescClasses) {
      suCfg.postSetUpModule(this, moduleDescCls);
    }
  }
  
  /**
   * This method is typically used by {@link #postSetUpDB()} to load pre-defined domain objects from CSV files.
   *  
   * @effects 
   *  read and add data objects from external CSV files that are specified by the descriptor classes <tt>classes</tt>
   *  
   * @version 3.2 
   */
  @SuppressWarnings("unchecked")
  public void importObjectsFromCSVFile(Class[] classes) throws DataSourceException, NotPossibleException {
    if (dodm == null) {
      initDODM();
    }

    // populate value data
    log(MessageCode.UNDEFINED, 
        //"Cài đặt dữ liệu khởi tạo...."
        "Initialising program data"
        );
    
    //final DSMBasic dsm = dodm.getDsm();
    
    SimpleDataFileLoader l;
    Class c;
    String filePath = null;
    for (Class<SimpleDataFileLoader> cd : classes) {
      try {
        filePath = null;
        
        l = cd.newInstance();
        c = l.getDomainClass();
        filePath = l.getFilePath();
        
        if (debug)
          log(MessageCode.UNDEFINED, 
            "  Class: {0} -> data class: {1}", c.getSimpleName(), cd.getSimpleName() 
            );
        
        //addClass(c);
        if (!dodm.isRegistered(c)) {
          dodm.registerClassHierarchy(c);
        }
      
        ((DODM)dodm).importObjectsFromCsvFile(c, filePath);
      } catch (Exception e) {
        //e.printStackTrace();
        ControllerBasic.displayIndependentError(e);
        if (!(e instanceof DataSourceException)) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {filePath} );
        }
      }
    }
  }
  
  /**
   * @effects 
   *  initialise a suitable <tt>DODM</tt> from the initial application configuration.
   *  
   *  <p>Throws DataSourceException if failed.
   */
  @Override
  public DODMBasic initDODM() throws DataSourceException {
    // v2.8: use shared method 
    return initDODM(true);
  }
  
  /**
   * @effects
   *  initialise an <tt>DODM</tt> from the initial application configuration and  
   *  if <tt>startDBServerIfNeeded = true</tt> Java Db has not been started then 
   *    start a server process 
   *  
   *  <p>Throws DataSourceException if failed. 
   *    
   * @version 2.8
   */
  protected DODMBasic initDODM(boolean startDBServerIfNeeded) {
    if (debug)
    log(MessageCode.INIT_DODM,
        //"Kết nối dữ liệu " + getDBName()
        "Initialising data manager component"
        );
  
    // v2.8: support isSerialisedConfig option
    boolean serialisedConfig = isSerialisedConfiguration();
    
    // in addition, check if no application modules require access to the data source
    // i.e. their domain classes are either not specified or non-serialisable
    boolean serialisedData = isSerialisedData();

    /*v3.1: make initConfig a field 
    Configuration initConfig;
    */
    
    if (config != null)
      initConfig = config;
    else
      initConfig = 
        //v2.8: createInitApplicationConfiguration();
        loadInitApplicationConfiguration();
    
    // disable object serialisation if both config and data are not serialised
    //TODO: make this more flexible (e.g. separate between DODM instance for config and for data)
    if (!serialisedConfig && !serialisedData) {
      initConfig.getDodmConfig().setObjectSerialisable(false);
    } else {
      // add support for java db server: start one if not already
      OsmConfig osmCfg = initConfig.getDodmConfig().getOsmConfig();
      if (startDBServerIfNeeded && 
          //v3.0: (osmCfg instanceof OsmClientServerConfig)
          SwTk.isDefaultClientServerConfig(osmCfg)
          && dbServer==null) {
        OsmClientServerConfig clientServerCfg = (OsmClientServerConfig) osmCfg;
        startOsmJavaDbServer(clientServerCfg);
      }
    }
    
    // create dodm instance (this is needed whether or not serialisation is used)
    dodm = DODM.getInstance(initConfig, dbServer);
    
    return dodm;
  }

  /**
   * @effects 
   *  if the domain classes of the configured modules are either not specified or non-serialisable
   *    return <tt>false</tt>
   *  else
   *    return <tt>true</tt>
   */
  private boolean isSerialisedData() {
    Class[] systemClasses = getSystemModelClasses();
    
    if (systemClasses != null) {
      for (Class c : systemClasses) {
        if (!DSMBasic.isTransient(c)) {
          // found a serialised class -> return true immediately
          return true;
        }
      }
    }
    
    Class[] domainClasses = getModelClasses();

    if (domainClasses != null) {
      for (Class c : domainClasses) {
        if (!DSMBasic.isTransient(c)) {
          // found a serialised class -> return true immediately
          return true;
        }
      }
    }
    
    // no serialised classes were found
    return false;
  }

  /**
   * @effects 
   *  load all security and non-security configuration from the data source
   * @version 
   * - 3.1: improved to merge settings of initial configuration (if needed)
   *   (needed to support client/server applications)
   */
  public void loadConfiguration() throws NotFoundException, 
  NotPossibleException, DataSourceException {
    super.loadConfiguration();
    
    // v3.1: merge settings of initial configuration if necessary (needed to support client/server applications)
    if (initConfig != null && initConfig != config) {
      // to merge
      mergeConfig(initConfig, config);
    }
    
    // load security configuration
    if (config.getUseSecurity())
      loadSecurityConfiguration();
  }

  /**
   * @requires 
   *  initConfig != null /\ initConfig != config
   *  
   * @effects 
   *  merge non-null values of mutable attributes of <tt>initConfig</tt> into <tt>config</tt>, s.t. 
   *  attributes in <tt>config</tt> are overridden.
   */
  private void mergeConfig(Configuration initConfig, Configuration config) {
    // TODO: generalise this merge to consider all mutable attributes
    // for now: merge common attributes needed by client/server application
    
    // application folder:
    String initAppFolder = initConfig.getAppFolder();
    String appFolder = config.getAppFolder();
    if (initAppFolder != null && !initAppFolder.equals(appFolder)) {
      config.setAppFolder(initAppFolder);
    }
    
    // OSM config:
    OsmConfig initOsmConfig = initConfig.getDodmConfig().getOsmConfig();
    OsmConfig osmConfig = config.getDodmConfig().getOsmConfig();
    if (initOsmConfig != null && !initOsmConfig.equalsByConstruction(osmConfig)) {
      config.getDodmConfig().setOsmConfig(initOsmConfig);
    }
  }

  @Override
  public Configuration createInitApplicationConfiguration() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void createApplicationConfiguration() throws NotPossibleException,
      NotFoundException {
    // TODO Auto-generated method stub
    
  }

  private SetUpSecurity getSetUpSecurity() {
    SetUpConfigBasic sufg = createSetUpConfigurationBasicInstance();
    
    if (sus == null) {
      sus = new SetUpSecurity(dodm);
    }
    
    return sus;
  }
  
  protected void setUpSecurity(boolean serialisedConfig) throws DataSourceException {
    if (config.getUseSecurity()) {
      if (dodm == null) {
        initDODM();
      }
      
      SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
      
      sus.setUpSecurity(serialisedConfig);
      
      // create domain-specific settings
      createDomainSecurityConfiguration(sus, serialisedConfig);
    }
  }
  
  /**
   * @effects 
   *  Create the security schema (without creating the objects)
   *  
   * @version 3.3
   */
  protected void registerSecuritySchema(boolean serialisedConfig) throws DataSourceException, IOException {
    if (dodm == null) {
      initDODM();
    }
    
    SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
    
    sus.registerSecuritySchema(serialisedConfig);
  }
  
  /**
   * 
   * @effects 
   *  clear existing security configuration (if any), 
   *  create system security configuration, 
   *  create domain-specific security configuration
   */
  protected void createSecurityConfiguration(boolean serialisedConfig) throws DataSourceException, IOException {
    if (dodm == null) {
      initDODM();
    }
    
    SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
    
    sus.registerSecuritySchema(serialisedConfig);

    if (serialisedConfig) {
      // delete exising security configuration
      sus.clearSecuritySchema();
    } else {
      // create all module configuration to use in the security configuration (below)
      if (!isConfigured())
        run(Cmd.Configure, null);
    }

    sus.createSecurityConfiguration(serialisedConfig);
    
    // create domain-specific settings
    createDomainSecurityConfiguration(sus, serialisedConfig);    
  }

  /**
   * @effects 
   *    create all domain-specific security configuration
   */
  protected void createDomainSecurityConfiguration(SetUpSecurity suSec, boolean serialisedConfig) 
  throws DataSourceException {
    // sub-classes should override
  }

  /**
   * @requires 
   * {@link #createSecurityConfigurationFor(Class)} is implemented
   * 
   * @effects 
   *  if <tt>args</tt> contains security class names
   *    only create the security data related to those classes
   *  else 
   *    create all domain-specific security configuration
   * @version 
   * - 3.3: improved to support security classes specified via input argument    
   */
  protected void createDomainSecurityConfiguration(boolean serialisedConfig, String[] args) 
  throws DataSourceException, NotFoundException {
    if (dodm == null) {
      initDODM();
    }
    
    SetUpSecurity sus = getSetUpSecurity();
    
    sus.registerSecuritySchema(serialisedConfig);
    
    if (args != null && args.length > 0) {
      
      
      Class c;
      for (String arg : args) {
        c = Toolkit.loadClass(arg);
        
        createSecurityConfigurationFor(sus, c, serialisedConfig);
      }
      
    } else {
      // no args
      createDomainSecurityConfiguration(sus, serialisedConfig);
    }
  }
  
  /**
   * To be implemented by sub-types
   * @effects 
   *    Use the pre-defined security set-up data for <tt>c</tt> to create its configuration in the application data source  
   * @version 3.3
   * @param sus 
   */
  protected void createSecurityConfigurationFor(SetUpSecurity sus, Class c, boolean serialisedConfig) throws DataSourceException {
    // to be implemented by sub-types
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        new Object[] {getClass().getSimpleName(), "createSecurityConfigurationFor("+c.getSimpleName()+")"});
  }

  /**
   * @requires 
   *  {@link #isSerialisedConfiguration()} = true
   * @effects 
   *  delete the security-related classes
   */
  protected void deleteSecuritySchema() throws DataSourceException {
    //v3.3: if (config.getUseSecurity()) {
      if (dodm == null) {
        initDODM();
      }
      
      SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
      
      sus.deleteSecuritySchema();
//    } else {
//      log(MessageCode.UNDEFINED, "Security is NOT enabled! Please enable security to run this command.");
//    }
  }
  
  /**
   * @requires 
   *  {@link #isSerialisedConfiguration()} = true
   * @effects 
   *  delete the security-related objects
   */
  protected void deleteSecurityConfiguration() throws DataSourceException {
    //v3.3: if (config.getUseSecurity()) {
      if (dodm == null) {
        initDODM();
      }
      
      SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
      
      sus.clearSecuritySchema();
    //}
  }

  /**
   * @requires 
   *  dodm != null
   */
  public void loadSecurityConfiguration() throws DataSourceException {
    SetUpSecurity sus = getSetUpSecurity(); //v2.7.2: new SetUpSecurity(schema);
    boolean serialisedConfig = true;
    sus.registerSecuritySchema(serialisedConfig);
    sus.loadConfiguration();
  }

  /**
   * @requires 
   *  config != null
   * @effects 
   *  Serialise <tt>this.config</tt> to a file (the file is created if it does not already exist)
   *  
   *  <p>Throws NotFoundException if file is not found; NotPossibleException if failed to serialise to file.
   * @version 2.8
   */
  protected void saveConfigurationToFile(String outputFilePath) throws NotFoundException, NotPossibleException {
    //String filePath = System.getProperty("user.dir") + File.separator + "config.cfg";
    ToolkitIO.writeObject(outputFilePath, config);
    
    log(MessageCode.UNDEFINED, "Configuration is saved to file: {0}", outputFilePath);
  }
  
  /**
   * @effects 
   *  If file <tt>filePath<tt> exists and contains a valid <tt>Configuration</tt>
   *    read and return it
   *  else
   *    throws NotFoundException if file does not exist; NotPossibleException if failed to read from file
   * @version 2.8
   */
  protected Configuration readConfigurationFromFile(String filePath) {
    Configuration config = ToolkitIO.readObject(filePath, Configuration.class);
    
    if (debug)
      log(MessageCode.UNDEFINED, "Configuration is read from file: {0}", filePath);
    
    
    return config;
  }
  

  /**
   * @requires 
   *  config != null
   *  
   * @effects 
   *  return the absolute path to file <tt>configFileName</tt>, which is stored in the directory 
   *  returned by {@link #getConfigFolderPath()}.
   *  
   *  <p>throws NotFoundException if path to file is not found.
   */
  protected String getConfigFilePath(String configFileName) throws NotFoundException {
    // file is stored in the config folder
    String configFolder = getConfigFolderPath();
    
    File folder = new File(configFolder);
    ToolkitIO.createFolderIfNotExists(folder);
    
    String filePath = folder.getPath() + File.separator + configFileName;
    
    if (debug)
      System.out.println("config file: " + filePath);
    
    return filePath;
        
  }
  /**
   * <b>IMPORTANT</b>: This method 
   * can only be invoked either by set-up or by the application script file in the application folder. 
   * 
   * <p>Unlike other application sub-folders, the <tt>config</tt> folder is 
   * not defined in <tt>Configuration</tt> because it must be available at launch-time, before configuration 
   * is read.  
   *  
   * @requires 
   *  current working directory is the root of an application set-up path
   *  
   * @effects
   *  return the path to the folder <tt>config</tt>, which is relative to the <b>current working directory</b> 
   */
  protected String getConfigFolderPath() throws NotFoundException {
    return getSystemProperty("user.dir") + File.separator + CONFIG_FOLDER_NAME;
  }
  
  /**
   * @requires 
   *  config != null
   * @effects 
   *  return the absolute path to the <tt>config</tt> sub-folder in the application-folder 
   */
  protected String getConfigFolderAppPath() {
    return config.getConfigFolderPath();
  }

  /**
   * @effects 
   *  if there are specified data-file-loader classes in classNames
   *    call {@link #importObjectsFromCSVFile(Class[])} on them
   *  else
   *    do nothing
   * @version 3.3
   */
  protected void createDomainDataSet(String[] classNames) throws NotPossibleException, NotFoundException, DataSourceException {
    if (classNames == null || classNames.length == 0) {
      log(MessageCode.UNDEFINED, "Data-file-loader classes are required but not specified!");
      return;
    }
      
    
    Class c;
    Class[] fileLoaderClasses = new Class[classNames.length];
    
    int i = 0;
    for (String className : classNames) {
      c = Toolkit.loadClass(className);
      fileLoaderClasses[i++] = c;
    }
    
    importObjectsFromCSVFile(fileLoaderClasses);
  }
  
  /**
   * @effects 
   *  create <b>basic</b> domain data needed to run the application.
   *  
   * @version 3.2c
   * 
   * @throws DataSourceException
   */
  protected void createBasicDomainData() throws DataSourceException {
    // TODO: for sub-classes to implement
  }

  /**
   * @effects 
   *  create <b>test</b> domain data needed to run a working demonstration of the application.
   *  
   * @version 3.2c
   * 
   * @throws DataSourceException
   */
  protected void createDemoDomainData() throws DataSourceException {
    // TODO: for sub-classes to implement    
  }

  /**
   * @requires 
   *  this has been initialised with initial configuration (by calling {@link #createInitApplicationConfiguration()})
   *  
   * @effects 
   *  if exists in the underlying data source the software represented by this
   *    return true
   *  else
   *    return false 
   * @version 4.0
   */
  public boolean existsSoftware() throws DataSourceException {
    if (!isDodmInit()) {
      initDODM();
    }
    
    DODMBasic dodm = getDODM();
    DOMBasic dom = dodm.getDom();
    
    return dom.existObject(Configuration.class, Configuration.AttributeName_appName, getAppName());
  }
}
