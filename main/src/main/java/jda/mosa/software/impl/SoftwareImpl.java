package jda.mosa.software.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.javadb.JavaDbOSM;
import jda.modules.dodm.osm.javadb.JavaDbOSMBasic;
import jda.modules.dodm.osm.relational.RelationalOSMBasic;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.model.Oid;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;

/**
 * @overview 
 *  A domain-driven software that directly uses {@link SoftwareAio} and the domain model API
 *  to manipulate (CRUD) the domain model and its instances.
 *  A domain model instance consists of a set of domain models and its links.
 *  
 *  <p>This software uses the default relational database (usually JavaDb)
 *  for storing the domain model and its objects.
 *  
 * @version 4.0
 */
public abstract class SoftwareImpl {
  private SoftwareAio sw;
  
  /**
   * @effects 
   *  initialise this with the specified software configuration class <tt>swCfgCls</tt>
   *  and the set up class <tt>SetUpGen</tt>
   */
  protected SoftwareImpl(Class swCfgCls) throws NotPossibleException {
    method("init()");
    
    // run with in-memory configuration 
    sw = SoftwareFactory.createStandardSoftwareAio(swCfgCls);
  }

  /**
   * @effects 
   *  initialise this with the specified software configuration class <tt>swCfgCls</tt>
   *  and the set up class <tt>setUpCls</tt>
   * @version 
   */
  protected SoftwareImpl(Class sysCls, Class<? extends SetUpGen> setUpCls) {
    method("init()");
    
    // run with in-memory configuration 
    sw = SoftwareFactory.createStandardSoftwareAio(sysCls, setUpCls);
  }

  public SoftwareAio getSwObject() {
    return sw;
  }
  
  public DODMBasic getDODM() {
    return sw.getSu().getDODM();
  }
  
  public DSMBasic getDsm() {
    return getDODM().getDsm();
  }

  public DOMBasic getDom() {
    return getDODM().getDom();
  }

  /**
   * @effects 
   *  Initialise DODM and create the default application configuration.
   *  Throws NotPossibleException if failed.
   */
  public void configure() throws NotPossibleException {
    // configure the database
    Cmd cmd = Cmd.Configure;
    try {
      sw.exec(new String[] {cmd.getName()});
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, e, new String[] {cmd.getName()});
    }
  }
  
  /**
   * @effects 
   *  Delete application configuration
   *  
   *  <p>Throws ApplicationRuntimeException if an error occured.
   * 
   * @version 
   */
  public void deleteConfig() throws Exception {
    sw.exec(new String[] {Cmd.DeleteConfig.getName()});    
  }
  
  /**
   * @effects 
   *  initialise DODM or throws NotPossibleException if failed.
   */
  public void init() throws NotPossibleException {
    try {
      sw.getSu().initDODM();
    } catch (DataSourceException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, e, new String[] {"Initialise DODM"});
    }    
  }
  
  public void registerConfigurationSchema(SetUpBasic su) throws Exception {
    Cmd cmd = Cmd.RegisterConfigurationSchema;
    
    su.run(cmd, null);
  }
  
//  public Class[] getConfigurationSchema() {
//    return SetUpConfig.getConfigurationClasses();
//  }

  public void registerDomainModel(SetUpBasic su) throws Exception {
    Cmd cmd = Cmd.CreateDomainSchema;
    su.run(cmd, null);
  }
  
  /**
   * @effects 
   *  Create domain schema of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws DataSourceException if an error occured.
   * 
   * @version 
   */
  public void registerDomainModel(Class[] domainClasses) throws DataSourceException {
    getSwObject().getSu().createDomainSchema(domainClasses);
  }
  
  /**
   * @effects 
   *  if model name is defined for cls
   *    return it
   *  else
   *    return null
   */
  public String getDomainModelName(Class cls) {
    return getDsm().getDomainSchema(cls);
  }
  
  /**
   * @effects 
   *  Delete domain data of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws DataSourceException if an error occured.
   * 
   * @version 
   */
  public void deleteDomainData(Class[] domainClasses) throws DataSourceException {
    getSwObject().getSu().deleteDomainData(Arrays.asList(domainClasses));
  }
  
  /**
   * @effects 
   *  Delete domain schema of the domain classes specified in <tt>domainClasses</tt>.
   *  
   *  <p>Throws DataSourceException if an error occured.
   * 
   * @version 
   */
  public void deleteDomainModel(Class[] domainClasses) 
      throws DataSourceException {
    method(String.format("deleteDomainModel(%d classes)",domainClasses.length));
    getSwObject().getSu().deleteDomainSchema(Arrays.asList(domainClasses));
  }

  /**
   * @effects 
   *  Delete domain model of the system
   *  
   *  <p>Throws DataSourceException if an error occured.
   * 
   * @version 
   */
  public void deleteDomainModel(String modelName) 
      throws DataSourceException {
    method(String.format("deleteDomainModel(%s)", modelName));
    Toolkit.addDebug(
        RelationalOSMBasic.class.getSimpleName(),
        JavaDbOSM.class.getSimpleName(),
        JavaDbOSMBasic.class.getSimpleName()
        );
    
    getDom().deleteDomainSchema(modelName);
  }
  
  public abstract void run(Object...args) throws Exception;

//  /**
//   * Override this to customise whether to use embedded or client/server data base
//   */
//  protected boolean isEmbedded() {
//    return true;
//  }

//  /**
//   * @effects
//   *  create a new Configuration that uses <b>embedded JavaDb</b> database
//   */
//  protected Configuration initEmbeddedJavaDbConfiguration(String appName, String dataSourceName) {
//    Configuration config = ApplicationToolKit.createSimpleConfigurationInstance(appName, dataSourceName);
//    return config;
//  }
//
//  /**
//   * @effects
//   *  create a new Configuration that uses <b>client/server</b> database 
//   */
//  protected Configuration initClientServerConfiguration(String appName, String dataSourceName) {
//    //create a new Configuration that uses <b>client/server JavaDb</b> database running at <tt>localhost:1527</tt>
//    
//    String clientUrl = "//localhost/"+dataSourceName;
//    String serverUrl = "//localhost";
//    
//    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientServerConfig("derby", clientUrl, serverUrl);
//
//    Configuration config = ApplicationToolKit.createInitApplicationConfiguration(appName, osmConfig);
//    return config;
//  }

  //
  // @BeforeClass
  // public static void getInstance() throws NotPossibleException {
  // instance = new TestDBMainBasic();
  // }
  
//  protected void initDODM(Configuration config) {
//    /*v2.7.4: support client/server*/
//    // if config is JavaDb client/server then start db server
//    OsmConfig osmConfig = config.getDodmConfig().getOsmConfig();
//    if (osmConfig instanceof OsmClientServerConfig 
//        && osmConfig.isDataSourceTypeJavaDb() // v3.0
//        ) {
//      OsmClientServerConfig osmClientServerCfg = (OsmClientServerConfig)  osmConfig;
//      startOsmJavaDbServer(osmClientServerCfg);
//    }
//    
//    schema = DODMBasic.getInstance(config);    
//  }

//  /**
//   * @effects 
//   *  start a JavaDb server (if port is available)
//   */
//  private void startOsmJavaDbServer(OsmClientServerConfig osmCfg) throws NotPossibleException {
//    ProtocolSpec serverProt = osmCfg.getServerProtocolSpec();
//    
//    if (serverProt == null) {
//      throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
//    }
//    
//    JavaDbServer dbServer = new JavaDbServer(serverProt);
//    
//    if (!dbServer.isPortAvailable()) {
//      // a server is already running by another JVM
//      //throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_USE_PORT, 
//      //    new Object[] {serverProt.getPort()});
//    } else {
//      dbServer.start();
//    }
//  }

//  protected String getDataSourceName() {
//    return defaultDataSourceName;
//  }
//  
//  protected String getAppName() {
//    return defaultAppName;
//  }
  
//  protected abstract void initClasses();

//  /**
//   * @requires
//   *  domainClasses != null
//   *  
//   * @throws DataSourceException
//   */
//  protected void initSchemas() throws DataSourceException {
//    // if there are domain classes defined in the sub-packages of the model package then 
//    // we must create the database schemas for these sub-packages
//    registerDataSourceSchemas(schema, domainClasses);
//  }
  
  /**
   * @effects 
   *  create a data source schema for each domain schemas that are specified in each class in <tt>classes</tt> 
   */
  public void registerDataSourceSchemas(Class[] classes) throws DataSourceException {
    // if there are domain classes defined in the sub-packages of the model package then 
    // we must create the database schemas for these sub-packages
    Stack<String> schemas = new Stack();
    //String[] clsNames;
    String schemaName;
    
    DODMBasic dodm = getDODM();
    
    boolean created;
    DSMBasic dsm = dodm.getDsm();
    for (Class c : classes) {
      schemaName = dsm.getDomainSchema(c);
      if (schemaName != null && !schemas.contains(schemaName)) {
        schemas.push(schemaName);
        created = dodm.getDom().addSchemaIfNotExist(schemaName);
        System.out.println(((created) ? "Created schema: " : "Registered schema: ") + schemaName);
      }
    }
  }
  
  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
//  public void initData() throws DataSourceException {
//    // only invoked once for all test cases
//    method("initData()");
//
//    defaultInitData();
//  }

//  /**
//   * @requires 
//   *  c is registered
//   * @effects
//   *  for each <tt>c</tt>'s constant object that is defined in <tt>c</tt>
//   *    add them to this.data
//   */
//  public <T> void initConstantObjects(Class<T> c) {
//    List<T> objs = Toolkit.getConstantObjects(c, c);
//    
//    if (objs != null) {
//      Map<Class,Collection> data = getData();
//      data.put(c, objs);
//    }
//  }
  
  
//  protected abstract void defaultInitData();

  /**
   * 
   * @effects 
   *  register c to the domain model (without reading its objects from the data source) 
   */
  public <T> void addClass(Class<T> c) throws DataSourceException {
    method("addClass()");
    addClass(c, true, false);
  }
  
//  public <T> void addClassAndCreate(Class<T> c) throws DataSourceException {
//    // must add abstract classes first!
//    System.out.println("addClassAndCreate()");
//    addClass(c, true, false);
//  }

  public <T> void addClass(Class<T> c, boolean createIfNotExist, boolean read)
      throws DataSourceException {
    method("addClass(" + c.getSimpleName() + ")");
    DODMBasic dodm = getDODM();

    dodm.addClass(c, createIfNotExist, read);
  }

  // add or register classes to the schema
  public void addClasses(Collection<Class> domainClasses) throws DataSourceException {
    method("addClasses()");

//    DODMBasic dodm = getDODM();
//    
//    for (Class c : domainClasses) {
//      System.out.printf("    Registering %s%n",c.getSimpleName());
//      dodm.registerClass(c);
//    }
//    
//    addClasses(domainClasses);
    addClasses((Class[]) domainClasses.toArray());
  }

  // v2.7.2
  /**
   * @effects 
   *  add <tt>classes</tt> to DODM.
   *  This will also recursively add all classes that are referenced directly or 
   *  indirectly by each specified <tt>classes</tt> to DODM.
   *  
   *  <p>Throws DataSourceException if failed.
   */
  public void addClasses(Class... classes) throws 
    NotPossibleException, NotFoundException, DataSourceException {
    method("addClasses()");

    DODMBasic dodm = getDODM();
    
    /* v2.7.4
    dodm.addClasses(classes, false);
    */
    dodm.registerClasses(classes);
    
    dodm.addClasses(classes, false);
  }
  
//  public void registerEnumClasses(Class...classes) throws DataSourceException {
//    DODMBasic dodm = getDODM();
//    for (Class c : classes) {
//      dodm.registerEnumInterface(c);
//    }
//  }
  
  /**
   * @effects 
   *  if c is currently registered into the model manager
   *    return true
   *  else
   *    return false
   */
  public boolean isRegistered(Class c) {
    return getDom().isRegistered(c);
  }
  
  /**
   * @effects 
   *  if c has been materialised in the data source
   *    return true
   *  else
   *    return false
   */
  public boolean isMaterialised(Class c) {
    boolean isMat = getDom().isCreatedInDataSource(c);
    return isMat;
  }
  
  public <T> void registerClass(Class<T> c) throws DataSourceException {
//    method("registerClass("+c+")");
//    DomainSchema schema = getDomainSchema();
//    //dodm.getDom().registerClass(c);
//    if (!dodm.getDom().isCreatedInDataSource(c)) {
//      dodm.getDom().addClass(c, true, false);
//    } else {
//      dodm.getDom().registerClass(c);
//    }
    registerClass(c, true);
  }

  public <T> void registerClass(Class<T> c, boolean create) throws DataSourceException {
    method("registerClass("+c+")");
    DODMBasic dodm = getDODM();
    //dodm.getDom().registerClass(c);
    
    // v2.8
    boolean objectSerialised = dodm.isObjectSerialised();
    
    if (objectSerialised &&   // v2.8: added this check 
        create) {
      if (!dodm.getDom().isCreatedInDataSource(c)) {
        dodm.addClass(c, true, false);
      } else {
        dodm.registerClass(c);
      }
    } else {
      dodm.registerClass(c);
    }
  }

  //public void registerClasses() throws DataSourceException {
  //  registerClasses(domainClasses);
  //}
  
  public void registerClasses(Class[] classes) throws DataSourceException {
    method("registerClass()");
  
    DODMBasic dodm = getDODM();
    
    for (Class c : classes) {
      System.out.printf("    Registering %s%n",c.getSimpleName());
      dodm.registerClass(c);
    }
  }

  /**
   * @requires
   *  domainClasses != null
   */
  public void removeClassAndDeleteFromDB(Class...domainClasses) throws DataSourceException {
    method("removeClassAndDeleteFromDB()");
    
    DODMBasic dodm = getDODM();
    
    /*v2.7.2: use the new API 
    // loop in reverse order b/c of the dependencies
    for (int i = domainClasses.length - 1; i >= 0; i--) {
      Class c = domainClasses[i];
      removeClassAndDeleteFromDB(c);
    }*/

      System.out.println("  Deleting constraints");
      deleteDataSourceConstraints(domainClasses);
      
      System.out.println("  Deleting domain classes");
      List toDelete = new ArrayList();
      Collections.addAll(toDelete, domainClasses);
      boolean strict=true;
      //dodm.getDom().deleteClasses(domainClasses, true);
      dodm.getDom().deleteClasses(toDelete, true, strict);
  }

  // v2.6.4.b
  public void deleteDataSourceConstraints(Class... classes) throws DataSourceException {
    List<String> consNames;
    DODMBasic dodm = getDODM();
    for (Class c : classes) {
      consNames = dodm.getDom().loadDataSourceConstraints(c);
      if (consNames != null) {
        for (String cons : consNames) {
          System.out.println("  constraints " + cons);
          dodm.getDom().deleteDataSourceConstraint(c, cons);
        }
      }
    }
  }
  
  public <T> void removeClassAndDeleteFromDB(Class<T> c) {
    method(String.format("removeClassAndDeleteFromDB(%s)%n", c.getSimpleName()));
    
    DODMBasic dodm = getDODM();
    
    // loop in reverse order b/c of the dependencies
    System.out.println("  Class " + c);
    try {
      dodm.getDom().deleteClass(c, true);
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }
  
  public void removeClass(Class...domainClasses) throws DataSourceException {
    method("removeClass()");
    DODMBasic dodm = getDODM();
    for (Class c : domainClasses) {
      dodm.getDom().deleteClass(c, false);
    }
  }
  
  public <T> void createObjects(Class<T> c, Collection<T> objects) throws DataSourceException {
    method(String.format("createObjects()%n"));
    DODMBasic dodm = getDODM();
    
    System.out.println("Creating objects for: " + c.getSimpleName());
    for (Object o : objects) {
      dodm.getDom().addObject(o);
    }
  }

  public <T> void createObjects(Class<T> c, Collection<T> objects, Integer maxCount) throws DataSourceException {
    method(String.format("createObjects(%s,%s)%n",c, maxCount+""));

    DODMBasic dodm = getDODM();
//    Map<Class,Collection> data = getData();
    
//    Collection objects = data.get(c);
    int sz = objects.size();
    int num = (maxCount != null) ? Math.min(maxCount, sz) : sz;
    
    System.out.println("Creating objects for: " + c.getSimpleName());
    Object o;
    Iterator it = objects.iterator();
    for (int i = 0; i < num; i++) {
      o = it.next();
      dodm.getDom().addObject(o);
    }
  }
  
//  public void createObjects(Class c) throws DataSourceException {
//    createObjects(c, null);
//  }
  
  public <T> void addObject(Class<T> c, T o) throws DataSourceException {
    method(String.format("addObject(%s, %s)%n",c, o));

    DODMBasic dodm = getDODM();
    
    dodm.getDom().addObject(o);
  }
  
//  public <T> void addObject(T o) throws DataSourceException {
//    System.out.printf("addObject(%s, %s)%n",c, o);
//
//    DODMBasic dodm = getDODM();
//    dodm.getDom().addObject(o);
//  }
  
  public <T> Collection<T> getObjects(Class<T> c) {
    DODMBasic dodm = getDODM();
    return dodm.getDom().getObjects(c);      
  }
  
  public void deleteObjects(Class...domainClasses) throws DataSourceException {
    method("deleteObjects()");
    // reversed order
    for (int i = domainClasses.length - 1; i >= 0; i--) {
      Class c = domainClasses[i];
      //System.out.println("Deleting objects of " + c);
      //dodm.getDom().deleteObjects(c);
      deleteObjects(c,true);
    }
  }

  public <T> void deleteObjects(Class<T> c, boolean fromDb) throws DataSourceException {
    method(String.format("deleteObjects(%s,fromDb=%b)%n",c,fromDb));
    
    DODMBasic dodm = getDODM();
    dodm.getDom().deleteObjects(c, fromDb);
  }
  
  public <T> void deleteObject(T o, Class<T> c) throws DataSourceException {
    //ducmle: 20220704: getDODM().getDom().deleteObject(o, c);
    Oid oid = getDODM().getDom().lookUpObjectId(c, o);
    getDODM().getDom().deleteObject(o, oid, c);
  }

  public <T> void updateObjectComplete(T o, Object[] vals) throws NotPossibleException, DataSourceException {
    getDODM().getDom().updateObjectComplete(o, vals);
  }
  
  public <T> void updateObject(Class<T> c, T o) throws NotPossibleException, NotFoundException, DataSourceException {
    DOMBasic dom = getDom();
    dom.updateObject(o, null);
  }

  /**
   * @effects 
   * 
   */
  public <T> void updateObject(Class<T> c, T object, String[] attribs, Object[] vals) throws NotPossibleException, NotFoundException, DataSourceException {
    Map<DAttr,Object> valMap = new HashMap<>();
    DSMBasic dsm = getDsm();
    for (int i = 0; i < attribs.length; i++) {
      String attrib = attribs[i];
      DAttr attr = dsm.getDomainConstraint(c, attrib);
      Object val = vals[i];
      valMap.put(attr,  val);
    }
    
    getDom().updateObject(object, valMap);
  }
  
  public <T> T retrieveObject(Class<T> c, String attrib, Op op,
      String val) throws NotFoundException, DataSourceException {
    method(String.format("retrieveObject(%s: %s %s %s)", c.getSimpleName(), attrib, op, val));

    DOMBasic dom = getDom();
    
    return dom.retrieveObject(c, attrib, op, val);
  }

  /**
   * @effects 
   */
  public <T> T retrieveObjectById(Class<T> cls, Object id) throws NotFoundException, DataSourceException {
    return retrieveObject(cls, "id", Op.EQ, id +"");
  }
  
  public <T> Collection<T> retrieveObjects(Class<T> c, String attrib, Op op,
      String val) throws NotFoundException, DataSourceException {
    method(String.format("retrieveObjects(%s: %s %s %s)", c.getSimpleName(), attrib, op, val));

    DOMBasic dom = getDom();
    
    return dom.retrieveObjects(c, attrib, op, val);
  }
  
  /**
   * @requires {@link #addClasses()} has been run
   */
  public void loadObjects(Class...domainClasses) throws DataSourceException {
    method("loadObjects()");
    for (Class c : domainClasses) {
      loadObjects(c);
    }
  }
  
//  /**
//   * @requires 
//   *  domainClasses != null
//   *  
//   * @throws DataSourceException
//   */
//  public void loadAllObjectsFromSource(Class...domainClasses) throws DataSourceException {
//    method("loadAllObjects()");
//    for (Class c : domainClasses) {
//      loadObjectsFromSource(c);
//    }
//    
//    // for each class that has collection-type attributes, load the referenced
//    // objects
//    DODMBasic dodm = getDODM();
//    for (Class c : domainClasses) {
//      dodm.getDom().retrieveAssociatedObjects(c);
//    }  
//  }
  
  /**
   * @requires {@link #addClasses()} has been run
   */
  public <T> void loadObjects(Class<T> c) throws DataSourceException {
    method(String.format("loadObjects(%s)%n", c.getSimpleName()));
    DODMBasic dodm = getDODM();
    
    Collection<T> objs = dodm.getDom().
        retrieveObjectsWithAssociations(c);
//    return objs;
  }

//  public <T> Collection<T> loadObjectsFromSource(Class<T> c) throws DataSourceException {
//    System.out.println("Loading objects from source " + c.getSimpleName());
//    DODMBasic dodm = getDODM();
//    
//    Collection<T> objs = (Collection<T>) dodm.getDom().retrieveObjectsWithAssociations(c);
//    return objs;
//  }

  public <T> void loadAssociatedObjects(T o) throws NotFoundException, NotPossibleException {
    getDom().retrieveAssociatedObjects(o);
  }
  
  /**
   * @effects 
   * 
   */
  public void loadAndPrintObjects(Class...classes) throws DataSourceException {
    for (Class c : classes) {
      loadObjects(c);
      printObjectPool(c); 
    }
  }
  
  public <T> T getObject(Class<T> cls, Object[] obids)
      throws NotFoundException, NotPossibleException {
    return (T) getDODM().getDom().lookUpObjectByID(cls, obids);
  }

  // /// Methods to manipulate application settings
  public void addSettings(String lang) throws DataSourceException {
    method("addSettings()");
    //OSM osm = null;
    //osm = dodm.getDom().getOsm();
    DOMBasic dom = getDODM().getDom();

    if (dom != null) {
      URL fileURL = null;

      // insert file
      String fileName = JavaDbOSMBasic.SQL_POPULATE_TABLES + "_" + lang + ".sql";
      fileURL = SoftwareImpl.class.getResource(fileName);

      if (fileURL == null)
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, new String[] {fileName});

      String filePath = fileURL.getPath();
      // general tasks: preparation
      dom.createObjectsFromFile(filePath);
    }
  }

  public void createSettingTables(String lang) throws DataSourceException {
    method("createSettingTables()");
//    OSM osm = null;
//    osm = dodm.getDom().getOsm();
    DOMBasic dom = getDODM().getDom();

    if (dom != null) {
      URL fileURL = null;

      // insert file
      String fileName = JavaDbOSMBasic.SQL_CREATE_TABLES + "_" + lang + ".sql";
      fileURL = SoftwareImpl.class.getResource(fileName);

      if (fileURL == null)
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, new 
            String[] {fileName});

      String filePath = fileURL.getPath();
      // general tasks: preparation
      dom.createSchemaFromFile(filePath);
    }
  }

  // getter methods
//  public Map<Class, Collection> getData() {
//    return data;
//  }
  
  public <T> T getObject(Collection<T> col, int index) {
    int i = 0;
    Iterator<T> it = col.iterator();
    while (i < index) {
      it.next();
      i++;
    }
    return it.next();
  }
  
  public <T> T getRandomObject(Collection<T> objs) {
    int sz = objs.size();
    int random = (int) (sz * Math.random());
    
    return getObject(objs, random);
  }

  public <T> T getMiddleObject(Collection<T> objs) {
    int sz = objs.size();
    int mid = (int) (sz/2);
    
    return getObject(objs, mid);
  }
  
  protected void method(String name) {
    System.out.printf(String.format("%n(+) %s.%s%n", this.getClass().getSimpleName(), name));
  }

  //  protected boolean isAbstract(Class c) {
//    return getDODM().getDsm().isAbstract(c);
//  }
  
  public void printDomainModel(boolean displayFqn) {
    method("printDomainModel()");
    DOMBasic dom = getDom();
    dom.listSchema(displayFqn);
  }
  
  public void printMaterialisedDomainModel(String modelName) {
    method(String.format("printMaterialisedDomainModel(%s)", modelName));
    DOMBasic dom = getDom();
    dom.listMaterialisedSchema(modelName);
  }
  
  public void printObjectDB(Collection<Class> domainClasses) {
    method("printObjectDB()");
    DODMBasic dodm = getDODM();
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
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

  public void printObjectDB(Class...classes) throws DataSourceException {
    method("printObjectDB()");
    DODMBasic dodm = getDODM();
    DOMBasic dom = dodm.getDom();
    DSMBasic dsm = dodm.getDsm();
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
  
//  /**
//   * @requires 
//   *  {@link #loadObjects()}
//   */
//  public void printDataMemory() throws DataSourceException {
//    printDataMemory(domainClasses);
//  }

  /**
   * @requires 
   *  {@link #loadObjects()}
   */
  public void printObjectPool(Class...domainClasses) throws DataSourceException {
    method("printObjectPool()");
    
    DODMBasic dodm = getDODM();
    
    for (Class cls : domainClasses) {
      // System.out.println("Class " + cls);
      dodm.getDom().listData(cls, false);
    }
  }
  
  public void printObjectPool(Class domainClass) throws DataSourceException {
    method("printObjectPool()");
    
    DODMBasic dodm = getDODM();
    
    dodm.getDom().listData(domainClass, false);
  }
  
  public void printObjects(Class c, Collection objects) {
    int count = (objects != null) ? objects.size() : 0;
    
    method(String.format("printObjects(%s: %d (objects))%n",c.getSimpleName(), count));
    if (objects != null && !objects.isEmpty()) {
      for (Object o : objects) {
        System.out.printf("...%s%n",o);
      }
    } 
//    else {
//      System.out.println("No objects found");
//    }
  }
}
