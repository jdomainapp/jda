package jda.test.dodm;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.junit.AfterClass;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.dodm.osm.javadb.JavaDbOSMBasic;
import jda.modules.javadbserver.model.JavaDbServer;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.OsmClientServerConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpConfig;
import jda.util.SwTk;

/**
 * @overview
 *  The <b>abstract</b> top-level test class for all basic DODM-related operations. 
 *  
 * @author dmle
 *
 */
public abstract class DODMBasicTester {
  public static enum Language {
    /** Vietnamese */
    vi, //
    /** English (Default) */
    en, //
  }

  protected Map<Class, Collection> data;

  protected Class[] domainClasses;

  protected DODMBasic schema;

  protected static DODMBasicTester instance;

  private static final String defaultDataSourceName = "data/DomainAppTest";

  private static final String defaultAppName = "DomainApp";

  protected DODMBasicTester() throws NotPossibleException {
    if (instance == null) {
      method("init()");
      String dataSourceName = getDataSourceName();
      String appName = getAppName();
      if (dataSourceName == null) {
        dataSourceName = defaultDataSourceName;
      }
      
      if (appName ==null) {
        appName = defaultAppName;
      }
      
      // v2.7.3: use a configuration to initialise DODM
      //Configuration config = ApplicationToolKit.getSimpleConfigurationInstance(appName, dataSourceName);
      Configuration config;
      if (isEmbedded()) {
        System.out.println("Using Embedded config");
        config = initEmbeddedJavaDbConfiguration(appName, dataSourceName);
      } else {  
        System.out.println("Using client/server config");
        config = initClientServerConfiguration(appName, dataSourceName);
      }
      
      printf("Initialising a DODM (possibly to data source %s)%n", dataSourceName);
      
      //schema = DODMBasic.getInstance(config);//getInstance(name);
      initDODM(config);
      
      initClasses();

      if (domainClasses != null) {
        try {
          initSchemas();
        } catch (DataSourceException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e, new Object[] {"init", "", ""});
        }
      }

      data = new LinkedHashMap<Class, Collection>();
      
      instance = this;
    }
  }

  /**
   * Override this to customise whether to use embedded or client/server data base
   */
  protected boolean isEmbedded() {
    return true;
  }

  /**
   * @effects
   *  create a new Configuration that uses <b>embedded JavaDb</b> database
   */
  protected Configuration initEmbeddedJavaDbConfiguration(String appName, String dataSourceName) {
    Configuration config = SwTk.createSimpleConfigurationInstance(appName, dataSourceName);
    return config;
  }

  /**
   * @effects
   *  create a new Configuration that uses <b>client/server</b> database 
   */
  protected Configuration initClientServerConfiguration(String appName, String dataSourceName) {
    //create a new Configuration that uses <b>client/server JavaDb</b> database running at <tt>localhost:1527</tt>
    
    String clientUrl = "//localhost/"+dataSourceName;
    String serverUrl = "//localhost";
    
    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientServerConfig("derby", clientUrl, serverUrl);

    Configuration config = SwTk.createInitApplicationConfiguration(appName, osmConfig);
    return config;
  }

  //
  // @BeforeClass
  // public static void getInstance() throws NotPossibleException {
  // instance = new TestDBMainBasic();
  // }
  
  protected void initDODM(Configuration config) {
    /*v2.7.4: support client/server*/
    // if config is JavaDb client/server then start db server
    OsmConfig osmConfig = config.getDodmConfig().getOsmConfig();
    if (osmConfig instanceof OsmClientServerConfig 
        && osmConfig.isDataSourceTypeJavaDb() // v3.0
        ) {
      OsmClientServerConfig osmClientServerCfg = (OsmClientServerConfig)  osmConfig;
      startOsmJavaDbServer(osmClientServerCfg);
    }
    
    schema = DODMBasic.getInstance(config);    
  }

  /**
   * @effects 
   *  start a JavaDb server (if port is available)
   */
  private void startOsmJavaDbServer(OsmClientServerConfig osmCfg) throws NotPossibleException {
    ProtocolSpec serverProt = osmCfg.getServerProtocolSpec();
    
    if (serverProt == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
    }
    
    JavaDbServer dbServer = new JavaDbServer(serverProt);
    
    if (!dbServer.isPortAvailable()) {
      // a server is already running by another JVM
      //throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_USE_PORT, 
      //    new Object[] {serverProt.getPort()});
    } else {
      dbServer.start();
    }
  }

  protected String getDataSourceName() {
    return defaultDataSourceName;
  }
  
  protected String getAppName() {
    return defaultAppName;
  }
  
  protected abstract void initClasses();

  /**
   * @requires
   *  domainClasses != null
   *  
   * @throws DataSourceException
   */
  protected void initSchemas() throws DataSourceException {
    // if there are domain classes defined in the sub-packages of the model package then 
    // we must create the database schemas for these sub-packages
    registerDataSourceSchemas(schema, domainClasses);
  }
  
  /**
   * @effects 
   *  create (if not already) the data source schema(s) that are specified in <tt>classes</tt> 
   *  
   * @throws DataSourceException
   */
  public void registerDataSourceSchemas(DODMBasic schema, Class[] classes) throws DataSourceException {
    // if there are domain classes defined in the sub-packages of the model package then 
    // we must create the database schemas for these sub-packages
    Stack<String> schemas = new Stack();
    //String[] clsNames;
    String schemaName;
    
    boolean created;
    DSMBasic dsm = schema.getDsm();
    for (Class c : classes) {
      schemaName = dsm.getDomainSchema(c);
      if (schemaName != null && !schemas.contains(schemaName)) {
        schemas.push(schemaName);
        created = schema.getDom().addSchemaIfNotExist(schemaName);
        System.out.println(((created) ? "Created schema: " : "Registered schema: ") + schemaName);
      }
    }
  }
  
  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
  public void initData() throws DataSourceException {
    // only invoked once for all test cases
    method("initData()");

    defaultInitData();
  }

  /**
   * @requires 
   *  c is registered
   * @effects
   *  for each <tt>c</tt>'s constant object that is defined in <tt>c</tt>
   *    add them to this.data
   */
  public <T> void initConstantObjects(Class<T> c) {
    List<T> objs = Toolkit.getConstantObjects(c, c);
    
    if (objs != null) {
      Map<Class,Collection> data = instance.getData();
      data.put(c, objs);
    }
  }
  
  
  protected abstract void defaultInitData();

  
  public void addClassAndCreate() throws DataSourceException {
    // must add abstract classes first!
    System.out.println("addClassAndCreate()");
    for (Class c : domainClasses) {
      addClass(c, true, false);
    }
  }

  public void addClass(Class c, boolean create, boolean read)
      throws DataSourceException {
    System.out.println("addClass(" + c.getSimpleName() + ")");
    DODMBasic schema = instance.getDODM();

    schema.addClass(c, create, read);
  }
  
  public void registerClasses() throws DataSourceException {
//    method("registerClass()");
//
//    DomainSchema schema = instance.getDomainSchema();
//    
//    for (Class c : domainClasses) {
//      System.out.printf("    Registering %s%n",c.getSimpleName());
//      schema.getDom().registerClass(c);
//    }
    registerClasses(domainClasses);
  }

  public void registerClasses(Class[] classes) throws DataSourceException {
    method("registerClass()");

    DODMBasic schema = instance.getDODM();
    
    for (Class c : classes) {
      System.out.printf("    Registering %s%n",c.getSimpleName());
      schema.registerClass(c);
    }
  }
  
  // add or register classes to the schema
  public void addClasses() throws DataSourceException {
    method("addClasses()");

    DODMBasic schema = instance.getDODM();
    
    for (Class c : domainClasses) {
      System.out.printf("    Registering %s%n",c.getSimpleName());
      schema.registerClass(c);
    }
    
    /**v2.7.2: use the new API  
    try {
      for (Class c : domainClasses) {
        if (!schema.getDom().isCreatedInDataSource(c)) {
          System.out.printf("Creating %s in data source and in schema.getDom()...%n", c.getSimpleName());
          schema.getDom().addClass(c, true, false);
        } else {
          System.out.printf("%s already created, registering in the schema.getDom()...%n", c.getSimpleName());
          schema.getDom().registerClass(c);
        }
      }
    } catch (DBException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
          e, "");
    }
    */
    addClasses(domainClasses);
  }

  // v2.7.2
  /**
   * @effects 
   *  register and add <tt>classes</tt> to DODM
   */
  public void addClasses(Class[] classes) throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    
    /* v2.7.4
    schema.addClasses(classes, false);
    */
    schema.registerClasses(classes);
    
    schema.addClasses(classes, false);
  }
  
  public void registerEnumClasses(Class[] classes) throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    for (Class c : classes) {
      schema.registerEnumInterface(c);
    }
  }
  
  public void registerClass(Class c) throws DataSourceException {
//    method("registerClass("+c+")");
//    DomainSchema schema = instance.getDomainSchema();
//    //schema.getDom().registerClass(c);
//    if (!schema.getDom().isCreatedInDataSource(c)) {
//      schema.getDom().addClass(c, true, false);
//    } else {
//      schema.getDom().registerClass(c);
//    }
    registerClass(c, true);
  }

  public void registerClass(Class c, boolean create) throws DataSourceException {
    method("registerClass("+c+")");
    DODMBasic schema = instance.getDODM();
    //schema.getDom().registerClass(c);
    
    // v2.8
    boolean objectSerialised = schema.isObjectSerialised();
    
    if (objectSerialised &&   // v2.8: added this check 
        create) {
      if (!schema.getDom().isCreatedInDataSource(c)) {
        schema.addClass(c, true, false);
      } else {
        schema.registerClass(c);
      }
    } else {
      schema.registerClass(c);
    }
  }
  
  // register classes to the schema and read their objects 
  public void addClass() throws DataSourceException {
    method("addClass()");
    for (Class c : domainClasses) {
//      if (isAbstract(c))
//        addClass(c, false, false);
//      else
      addClass(c, false, true);
    }
  }

  public void createObjects() throws DataSourceException {
    System.out.printf("createObjects()%n");

    DODMBasic schema = instance.getDODM();
    
    Map<Class,Collection> data = instance.getData();

    Class c;
    Collection objects;
    for (Entry<Class, Collection> e : data.entrySet()) {
      c = e.getKey();
      objects = e.getValue();
      System.out.println("Creating objects for: " + c.getSimpleName());
      for (Object o : objects) {
        schema.getDom().addObject(o);
      }
    }
  }

  public void createObjects(Class c, Integer maxCount) throws DataSourceException {
    System.out.printf("createObjects(%s,%s)%n",c, maxCount+"");

    DODMBasic schema = instance.getDODM();
    Map<Class,Collection> data = instance.getData();
    
    Collection objects = data.get(c);
    int sz = objects.size();
    int num = (maxCount != null) ? Math.min(maxCount, sz) : sz;
    
    System.out.println("Creating objects for: " + c.getSimpleName());
    Object o;
    Iterator it = objects.iterator();
    for (int i = 0; i < num; i++) {
      o = it.next();
      schema.getDom().addObject(o);
    }
  }
  
  public void createObjects(Class c) throws DataSourceException {
    createObjects(c, null);
//    System.out.printf("createObjects(%s)%n",c);
//
//    DomainSchema schema = instance.getDomainSchema();
//    Map<Class,Collection> data = instance.getData();
//    
//    Collection objects = data.get(c);
//    System.out.println("Creating objects for: " + c.getSimpleName());
//    for (Object o : objects) {
//      schema.getDom().addObject(o);
//    }
  }
  
  public void createObject(Class c, Object o) throws DataSourceException {
    System.out.printf("addObject(%s, %s)%n",c, o);

    DODMBasic schema = instance.getDODM();
    
    schema.getDom().addObject(o);
  }
  
  public <T> void addObject(T o) throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    schema.getDom().addObject(o);
  }
  
  public <T> Collection<T> getObjects(Class<T> c) {
    DODMBasic schema = instance.getDODM();
    return schema.getDom().getObjects(c);      
  }
  
  public void deleteObjects() throws DataSourceException {
    System.out.println("deleteObjects()");
    // reversed order
    for (int i = domainClasses.length - 1; i >= 0; i--) {
      Class c = domainClasses[i];
      //System.out.println("Deleting objects of " + c);
      //schema.getDom().deleteObjects(c);
      deleteObjects(c,true);
    }
  }

  public void deleteObjects(Class c, boolean fromDb) throws DataSourceException {
    System.out.printf("deleteObjects(%s,fromDb=%b)%n",c,fromDb);
    
    DODMBasic schema = instance.getDODM();
    schema.getDom().deleteObjects(c, fromDb);
  }
  
  public void deleteObject(Object o, Class c) throws DataSourceException {
    instance.getDODM().getDom().deleteObject(o, c);
  }

  public void updateObjectComplete(Object o, Object[] vals) throws NotPossibleException, DataSourceException {
    instance.getDODM().getDom().updateObjectComplete(o, vals);
  }
  
  protected <T> void updateObject(Class<T> c, T o) throws NotPossibleException, NotFoundException, DataSourceException {
    DOMBasic dom = instance.getDom();
    dom.updateObject(o, null);
  }

  protected <T> T retrieveObject(Class<T> c, String attrib, Op op,
      String val) throws NotFoundException, DataSourceException {
    DOMBasic dom = instance.getDom();
    
    return dom.retrieveObject(c, attrib, op, val);
  }

  /**
   * @requires {@link #addClasses()} has been run
   */
  public void loadObjects() throws DataSourceException {
    method("loadObjects()");
    for (Class c : domainClasses) {
      //if (!isAbstract(c)) {
      loadObjects(c);
      // } else {
      // data.put(c, new ArrayList());
      // }
    }
  }
  
  /**
   * @requires 
   *  domainClasses != null
   *  
   * @throws DataSourceException
   */
  public void loadAllObjectsFromSource() throws DataSourceException {
    method("loadAllObjects()");
    for (Class c : domainClasses) {
      loadObjectsFromSource(c);
    }
    
    // for each class that has collection-type attributes, load the referenced
    // objects
    DODMBasic schema = instance.getDODM();
    for (Class c : domainClasses) {
      schema.getDom().retrieveAssociatedObjects(c);
    }  
  }
  
  /**
   * @requires {@link #addClasses()} has been run
   */
  public void loadObjects(Class c) throws DataSourceException {
//    System.out.println("Loading class " + c.getSimpleName());
//    DomainSchema schema = instance.getDomainSchema();
//    
//    Collection objs = schema.getDom().loadObjectsWithAssociations(c);
    Collection objs = loadObjectsFromSource(c);
    instance.getData().put(c, objs);
  }

  public <T> Collection<T> loadObjectsFromSource(Class<T> c) throws DataSourceException {
    System.out.println("Loading class " + c.getSimpleName());
    DODMBasic schema = instance.getDODM();
    
    Collection<T> objs = (Collection<T>) schema.getDom().retrieveObjectsWithAssociations(c);
    return objs;
  }

  public <T> void loadAssociatedObjects(T o) throws NotFoundException, NotPossibleException {
    instance.getDom().retrieveAssociatedObjects(o);
  }
  
  protected Object getObject(Class cls, Object[] obids)
      throws NotFoundException, NotPossibleException {
    return instance.getDODM().getDom().lookUpObjectByID(cls, obids);
  }



  /**
   * @requires
   *  domainClasses != null
   */
  public void removeClassAndDeleteFromDB() throws DataSourceException {
    System.out.println("removeClassAndDeleteFromDB()");
    
    DODMBasic schema = instance.getDODM();
    
    /*v2.7.2: use the new API 
    // loop in reverse order b/c of the dependencies
    for (int i = domainClasses.length - 1; i >= 0; i--) {
      Class c = domainClasses[i];
      removeClassAndDeleteFromDB(c);
    }*/

      System.out.println("  Xóa các ràng buộc");
      deleteDataSourceConstraints(domainClasses);
      
      System.out.println("  Xóa các lớp");
      List toDelete = new ArrayList();
      Collections.addAll(toDelete, domainClasses);
      boolean strict=true;
      //schema.getDom().deleteClasses(domainClasses, true);
      schema.getDom().deleteClasses(toDelete, true, strict);
  }

  // v2.6.4.b
  protected void deleteDataSourceConstraints(Class[] classes) throws DataSourceException {
    List<String> consNames;
    for (Class c : classes) {
      consNames = schema.getDom().loadDataSourceConstraints(c);
      if (consNames != null) {
        for (String cons : consNames) {
          System.out.println("  ràng buộc " + cons);
          schema.getDom().deleteDataSourceConstraint(c, cons);
        }
      }
    }
  }
  
  public void removeClassAndDeleteFromDB(Class c) {
    System.out.printf("removeClassAndDeleteFromDB(%s)%n", c.getSimpleName());
    
    DODMBasic schema = instance.getDODM();
    
    // loop in reverse order b/c of the dependencies
    System.out.println("  Class " + c);
    try {
      schema.getDom().deleteClass(c, true);
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }
  
  public void removeClass() throws DataSourceException {
    System.out.println("removeClass()");
    for (Class c : domainClasses) {
      schema.getDom().deleteClass(c, false);
    }
  }

  public void printDataDB() {
    System.out.println("printDataDB()");
    DOMBasic dom = schema.getDom();
    DSMBasic dsm = schema.getDsm();
    Throwable cause;
    for (Class cls : domainClasses) {
      if (!dsm.isTransient(cls)) {
        try {
          dom.listData(cls, true);
        } catch (Exception e) {
          cause = e.getCause();
          System.err.println("Class: "+cls+
              "\n  "+ e.getMessage()+
              "\n  " + ((cause != null) ? "Cause: " + cause.getMessage() : "")
              );
        }
      }
    }
  }

  public void printDataDB(Class[] classes) throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    DOMBasic dom = schema.getDom();
    DSMBasic dsm = schema.getDsm();
    Throwable cause;
    for (Class cls : classes) {
      if (!dsm.isTransient(cls)) {
        //dom.listData(cls, true);
        try {
          dom.listData(cls, true);
        } catch (Exception e) {
          cause = e.getCause();
          System.err.println("Class: "+cls+
              "\n  "+ e.getMessage()+
              "\n  " + ((cause != null) ? "Cause: " + cause.getMessage() : "")
              );
        }
      }
    }
  }
  
  /**
   * @requires 
   *  {@link #loadObjects()}
   */
  public void printDataMemory() throws DataSourceException {
    printDataMemory(domainClasses);
//    System.out.println("printDataMemory()");
//    
//    DomainSchema schema = instance.getDomainSchema();
//    
//    for (Class cls : domainClasses) {
//      // System.out.println("Class " + cls);
//      schema.getDom().listData(cls, false);
//    }
  }

  /**
   * @requires 
   *  {@link #loadObjects()}
   */
  public void printDataMemory(Class[] domainClasses) throws DataSourceException {
    System.out.println("printDataMemory()");
    
    DODMBasic schema = instance.getDODM();
    
    for (Class cls : domainClasses) {
      // System.out.println("Class " + cls);
      schema.getDom().listData(cls, false);
    }
  }
  
  public void printDataMemory(Class domainClass) throws DataSourceException {
    System.out.printf("printDataMemory(%s)%n", domainClass.getSimpleName());
    
    DODMBasic schema = instance.getDODM();
    
    schema.getDom().listData(domainClass, false);
  }
  
  // /// Methods to manipulate application settings
  public void addSettings(String lang) throws DataSourceException {
    System.out.println("addSettings()");
    //OSM osm = null;
    //osm = schema.getDom().getOsm();
    DOMBasic dom = schema.getDom();

    if (dom != null) {
      URL fileURL = null;

      // insert file
      String fileName = JavaDbOSMBasic.SQL_POPULATE_TABLES + "_" + lang + ".sql";
      fileURL = DODMBasicTester.class.getResource(fileName);

      if (fileURL == null)
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND,
            "Không tìm thấy tệp (file): {0}", fileName);

      String filePath = fileURL.getPath();
      // general tasks: preparation
      dom.createObjectsFromFile(filePath);
    }
  }

  public void createSettingTables(String lang) throws DataSourceException {
    System.out.println("createSettingTables()");
//    OSM osm = null;
//    osm = schema.getDom().getOsm();
    DOMBasic dom = schema.getDom();

    if (dom != null) {
      URL fileURL = null;

      // insert file
      String fileName = JavaDbOSMBasic.SQL_CREATE_TABLES + "_" + lang + ".sql";
      fileURL = DODMBasicTester.class.getResource(fileName);

      if (fileURL == null)
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND,
            "Không tìm thấy tệp (file): {0}", fileName);

      String filePath = fileURL.getPath();
      // general tasks: preparation
      dom.createSchemaFromFile(filePath);
    }
  }

  @AfterClass
  public static void shutdown() {
    // schema.getDom().finalize();
  }

  protected void method(String name) {
    System.out.println(this.getClass().getSimpleName() + "." + name);
  }

  protected boolean isAbstract(Class c) {
    return schema.getDsm().isAbstract(c);
  }
  
  // getter methods
  public Map<Class, Collection> getData() {
    return data;
  }
  
  public DODMBasic getDODM() {
    return schema;
  }
  
  public DSMBasic getDsm() {
    return instance.getDODM().getDsm();
  }

  public DOMBasic getDom() {
    return instance.getDODM().getDom();
  }

  public <T> T getObject(Collection<T> col, int index) {
    int i = 0;
    Iterator<T> it = col.iterator();
    while (i < index) {
      it.next();
      i++;
    }
    return it.next();
  }
  
  protected <T> T getRandomObject(Collection<T> objs) {
    int sz = objs.size();
    int random = (int) (sz * Math.random());
    
    return getObject(objs, random);
  }

  protected <T> T getMiddleObject(Collection<T> objs) {
    int sz = objs.size();
    int mid = (int) (sz/2);
    
    return getObject(objs, mid);
  }
  
  protected void printObjects(Class c, Collection objects) {
    int count = (objects != null) ? objects.size() : 0;
    
    System.out.printf("%n%s: %d (objects)%n",c.getSimpleName(), count);
    if (objects != null && !objects.isEmpty()) {
      for (Object o : objects) {
        System.out.printf("...%s%n",o);
      }
    } 
//    else {
//      System.out.println("No objects found");
//    }
  }

  public void registerConfigurationSchema(SetUpBasic su) throws Exception {
    Cmd cmd = Cmd.RegisterConfigurationSchema;
    
    su.run(cmd, null);
  }
  
  public Class[] getConfigurationSchema() {
    return SetUpConfig.getConfigurationClasses();
  }

  public void registerDomainSchema(SetUpBasic su) throws Exception {
    Cmd cmd = Cmd.CreateDomainSchema;
    
    su.run(cmd, null);
  }
  
  /**
   * @effects 
   *  short cut for {@link System#out}.printf
   */
  public void printf(String formatSpecifier, Object...args) {
    System.out.printf(formatSpecifier, args);
  }
}
