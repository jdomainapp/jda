package jda.modules.dodm.osm;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.osm.relational.DataSourceType;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  Represents the <b>Object Storage Manager</b> component of the {@link DODMBasic}.
 *  
 * @author dmle
 */
public abstract class OSM {
  private OsmConfig config;
  
  protected DOMBasic dom;

  
  public OSM(OsmConfig config, DOMBasic dom) {
    this.config = config;
    this.dom = dom;
  }

  protected OsmConfig getConfig() {
    return config;
  }

  public DOMBasic getDom() {
    return dom;
  }

  /**
   * @effects Returns a new <code>Connection</code> object to the data source
   *          specified in {@link #config}.
   *          
   * @modifies <code>this</code>
   */
  public abstract void connect() throws DataSourceException;
  
  /**
   * @effects 
   *  if a connection to data source is valid
   *    return true
   *  else
   *    return false
   */  
  public abstract boolean isConnected();
  
  /**
   * @effects closes the active connection to the underlying data source
   */
  public abstract void disconnect();
  
  /**
   * @effects 
   *  return the <tt>DataSourceType</tt> equivalence of <tt>javaType</tt>
   *  
   * @version 3.0
   */
  protected final DataSourceType getDataSourceTypeFor(Class<? extends DataSourceType> sqlTypeCls, Type javaType)
  throws NotFoundException {
    DataSourceType[] types = (DataSourceType[]) sqlTypeCls.getEnumConstants();
    
    for (DataSourceType type : types) {
      if (type.getMapping() == javaType) {
        return type;
      }
    }
    // no match
    
    //return null;
    throw new NotFoundException(NotFoundException.Code.DATA_SOURCE_TYPE_NOT_FOUND, new Object[] {javaType});
  }
  
  
  /**
   * @effects 
   *  return the class that defines the data source specific data types used for storing objects 
   * @version 3.0
   */
  protected abstract Class<? extends DataSourceType> getDataSourceTypeClass();
  
  /**
   * @effects 
   *  return the <tt>DataSourceFunction</tt>'s class specific for this 
   * @version 3.0
   */
  protected abstract Class<? extends DataSourceFunction> getDataSourceFunctionClass();
  
  /**
   * @effects 
   *  return the <tt>DataSourceFunction</tt> equivalence of <tt>Function</tt>
   *  
   * @version 3.0
   */
  protected final DataSourceFunction getDataSourceFunctionFor(Class<? extends DataSourceFunction> sqlFuncCls, Function func) 
  throws NotFoundException {
    DataSourceFunction[] sqlFuncs = (DataSourceFunction[]) sqlFuncCls.getEnumConstants();
    
    for (DataSourceFunction sqlFunc : sqlFuncs) {
      if (sqlFunc.getMapping() == func) {
        return sqlFunc;
      }
    }
    // no match
    //return null;
    throw new NotFoundException(NotFoundException.Code.DATA_SOURCE_FUNCTION_NOT_FOUND, new Object[] {func});
  }
  
  /**
   * @effects 
   *  return the <tt>DataSourceFunction</tt> equivalence of <tt>func</tt> as specified by  
   *  {@link #getDataSourceFunctionClass()}
   *  
   * @version 3.1
   */
  public final DataSourceFunction getDataSourceFunctionFor(Function func) {
    return getDataSourceFunctionFor(getDataSourceFunctionClass(), func);
  }
  
  /**
   * @effects Executes each schema-manipulation statement in <code>filePath</code> to
   *          create the schema in the underlying data source represented by this
   *          , throwing <code>DataSourceException</code> if an
   *          error occured.
   */
  public abstract void createSchemaFromFile(String filePath) throws DataSourceException;

  /**
   * @requires 
   *  osm != null /\ domain schema has been created
   *  
   * @effects Executes each object-manipulation statement in <code>filePath</code> to
   *          create records in the underlying data source represented by this
   *          , throwing <code>DataSourceException</code> if an
   *          error occurred.
   */
  public abstract void createObjectsFromFile(String filePath) throws DataSourceException;
  
  /**
   * @effects delete the store records of the domain objects of a class.
   * 
   * <p>Throws DataSourceException if failed to do so.
   */
  public abstract void deleteObjects(Class domainClass) throws DataSourceException;

  /**
   * @effects returns <code>true</code> if a class store named 
   *          <code>storeName</code> exists in the data source schema named
   *          <code>schema</code> or in the default schema if <tt>schema = null</tt>;
   *          else returns <code>false</code>
   */
  public abstract boolean exists(String schema, String storeName);

  /**
   * 
   * @effects 
   *  if the underlying data source schema named <tt>dsSchema</tt> exists
   *    list contents of all data stores in it
   *  else
   *    do nothing
   * @version 5.4
   */
  public abstract void printDataSourceSchema(String dsSchema);
  
  /**
   * @effects returns <code>true</code> if the data source schema with name
   *          <code>schema</code> has already been created, else returns
   *          <code>false</code>
   */
  public abstract boolean existsSchema(String schema);

  /**
   * @effects creates the data source schema named <code>name</code>
   */
  public abstract void createSchema(String name) throws DataSourceException;


  /**
   * @effects drops all class stores in the data source schema named <tt>schemaNamed</tt>; throws a
   *          <code>DataSourceException</code> if an error occured
   */
  public abstract void deleteDomainSchema(String schemaName) throws DataSourceException;

  /**
   * @requires <pre>
   *  for each entry e in dataSourceConstraints
   *    classStore(e.key) /\ e.getValue contains constraint statements on classStore(e.key)</pre>
   * @effects <pre> 
   *  for each entry e in dataSourceConstraints
   *    let storeName = e.key
   *    let storeCons = e.getValue
   *    alter classStore(storeName) adding the constraints in storeCons
   *  
   *  Throws DataSourceException if failed to add a constraint.
   *  </pre>
   */
  public abstract void createConstraints(java.util.Map<String,List<String>> dataSourceConstraints) throws DataSourceException;

  /**
   * @effects 
   *  remove the data source constraint of the class store associated to the domain class 
   *  <tt>c</tt>, whose name is <tt>name</tt>
   *  
   *  <p>Throws DataSourceException if failed to remove the constraint.
   */
  public abstract void dropDataSourceConstraint(Class c, String name) throws DataSourceException;

  /**
   * Create a new relational table from a domain class but leaving the constraints (e.g. FKs) till later.
   *
   * @modifies  
   *  tableConstraints
   * @effects Create a new domain class store whose structure is defined from
   *          the serialisable attributes of <code>domainClass</code>, else
   *          create a new domain class store from all the attributes of
   *          <code>domainClass</code>.
   *          
   *          <p>All the data source constraints are added to <tt>storeConstraints</tt>. 
   *        
   *        <p>Throws NotPossibleException if failed to create the store; 
   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
   */
  public abstract void createClassStoreWithoutConstraints(final Class domainClass, 
      final java.util.Map<String,List<String>> storeConstraints) throws DataSourceException, NotPossibleException, NotFoundException;

  /**
   * Create a new relational table from a domain class
   * 
   * @param domainClass
   *          a domain class
   * @effects Create a new domain class store whose structure is defined from
   *          the serialisable attributes of <code>domainClass</code>, else
   *          create a new relational table from all the attributes of
   *          <code>domainClass</code>.
   *        
   *        <p>Throws NotPossibleException if failed to create the store; 
   *  NotFoundException if <tt>domainClass</tt> is not a registered domain class or 
   *  required id domain attributes of the class(es) referenced by <tt>domainClass</tt> are not found.
   */
  public abstract void createClassStore(Class domainClass) throws DataSourceException, NotPossibleException, NotFoundException;

  /**
   * @effects remove the domain class store of a domain class together with its data
   *  Throws DataSourceException if failed to do so
   */
  public abstract void dropClassStore(Class domainClass) throws DataSourceException;

  /**
   * @effects 
   *  update store records of <tt>c</tt> that satisfies <tt>searchQuery</tt>
   *  using the expressions in <tt>updateQuery</tt>
   *  
   *  <p>Throws DataSourceException if failed to do so.
   *  
   * @requires the class store of <code>c</code> has been created
   */
  public abstract void updateObjects(Class you, Query<ObjectExpression> searchQuery,
      Query<ObjectExpression> updateQuery) throws DataSourceException;

  /**
   * @effects 
   *  if exists data source constraints (e.g. FKs) of <tt>c</tt>
   *    return a List of their names (in the definition order)
   *  else
   *    return null
   */
  public abstract List<String> readDataSourceConstraint(Class c);

  /**
   * @effects returns all objects of class <code>c</code> stored in the
   *          store records of <tt>c</tt>, or <code>null</code> if no objects exist
   *          
   *          <p>Throws <code>DataSourceException</code> if errors occurred in reading
   *          objects; NotPossibleException if fail to create object
   *           
   * @requires a class store was created for class <code>c</code>
   */
  public <T> List<T> readObjects(Class<T> domainCls) throws NotPossibleException, DataSourceException {
    // read all objects
    return readObjects(domainCls, -1);
  }
  
  /**
   * @effects returns <tt>num</tt> objects of class <code>c</code> stored in the
   *          store records of <tt>c</tt>, or <code>null</code> if no objects exist
   *          
   *          <p>Throws <code>DataSourceException</code> if errors occurred in reading
   *          objects; NotPossibleException if fail to create object
   *           
   * @requires a class store was created for class <code>c</code>
   */
  public abstract <T> List<T> readObjects(Class<T> c, int num) throws NotPossibleException, DataSourceException;



  /**
   * @requires 
   *  a class store of c has been created in the data source /\ 
   *  attrib is a valid attribute of c /\
   *  elements of derivedAttributes are valid attributes of c
   * 
   * @effects
   *  reads from the data source and returns a <tt>Map</tt> containing lowest and highest values of 
   *  the domain attribute <tt>attrib</tt> among the objects of c, group by the attributes 
   *  specified by <tt>derivedAttributes</tt>
   *   
   *  <p>throws DataSourceException if fails to read from data source;
   *  NotFoundException if no value range is found;
   *  IllegalArgumentException if no derived attributes were specified
   */
  public abstract java.util.Map<Tuple, Tuple2<Object,Object>> readValueRange(Class c, DAttr attrib, 
      DAttr[] derivedAttributes) 
      throws DataSourceException, NotFoundException;

  /**
   * @requires 
   *  a class store corresponding to class c has been created in the data source /\ 
   *  attrib is a valid attribute of c 
   * 
   * @effects
   *  reads from the data source and returns a Tuple2 containing lowest and highest values of 
   *  the domain attributes <tt>atrib</tt> among the objects of c
   *   
   *  <p>throws DataSourceException if fails to read from data source;
   *  NotFoundException if no value range is found
   */
  public abstract Tuple2<Object,Object> readValueRange(Class c, DAttr attrib) throws DataSourceException, NotFoundException;

  /**
   * This method differs from {@link #readValueRange(Class, DAttr)} in that it returns the 
   * actual domain class(es) that own the min and max values of the range. This is specifically needed for id-attributes
   * where the Oids need to be created with the correct sub-types that own them. 
   * 
   * @requires 
   *  a class store corresponding to class c has been created in the data source /\ 
   *  idAttrib is a valid <b>id</b> attribute of c /\ 
   *  if <tt>c</tt> has sub-types then these sub-types inherit the id-attribute of <tt>c</tt> 
   * 
   * @effects
   *  reads from the data source and returns a <tt>Tuple2(Tuple2,Tuple2)</tt> containing two pairs 
   *  <tt>(minCls,minVal), (maxCls,maxVal)</tt> where 
   *  <tt>minVal, maxVal</tt> are the lowest and highest values of <tt>idAttrib</tt> among the objects of <tt>c</tt>
   *  and <tt>minCls, maxCls</tt> are the actual domain classes of the objects containing <tt>minVal, maxVal</tt>.
   *  
   *  <p>Note: if <tt>c</tt> has sub-types then <tt>minCls, maxCls</tt> may be one of the sub-types, 
   *  else <tt>minCls, maxCls</tt> are the same as <tt>c</tt>  
   *   
   *  <p>throws DataSourceException if fails to read from data source;
   *  NotFoundException if no value range is found
   * @version 3.2
   */
  public abstract Tuple2<Tuple2<Class, Object>, Tuple2<Class, Object>> readIdValueRange(
      Class cls, DAttr idAttrib) throws DataSourceException, NotFoundException;
  
  /**
   * Store the data of a domain object into the class store created for its
   * class.
   * 
   * @effects inserts a class store record for a domain object <code>o</code> into the
   *          class store of the domain class <code>c</code>.
   *          
   *          <br>Throws DataSourceException if failed to do so.
   */
  public abstract void putObject(Class c, Object o) throws DataSourceException;

  /**
   * @effects updates the class store record of the domain object <code>o</code>
   *          in the class store of the domain class <code>c</code>
   *          
   *          <p>Throws <code>DataExceptionException</code> if an error occured.
   */
  public abstract void updateObject(Object o, Class c) throws DataSourceException;

  
  /**
   * @requires 
   *  c != null /\ c is a domain class /\ 
   *  id != null /\ id is a valid object id of c /\
   *  (<b>acyclicity</b>) the association graph of the domain objects involved is acyclic
   *  
   * @effects 
   *  completely load the object <tt>o</tt> of <tt>c</tt> or a sub-type of <tt>c</tt> whose id value is specified by <tt>oid</tt> and all 
   *  the domain objects associated to <tt>o</tt> (and so on (recursively)),
   *  return <tt>o</tt>
   *  
   *  <br>throws DataSourceException if fails to read objects from data source, 
   *  NotPossibleException if fail to create object
   */ 
  public abstract <T> T readObject(final Class<T> c, Oid oid) throws NotPossibleException, DataSourceException;
  
  /**
   * @effects 
   *  if exists object <tt>o</tt> in the class store of <tt>c</tt> whose <tt>Oid</tt> is <tt>id</tt>
   *    completely load the object <tt>o</tt> of <tt>c</tt> or a sub-type of <tt>c</tt> whose id value is specified by <tt>oid</tt> 
   *    <b>but without</b> loading any of the associated objects (these objects are assumed to 
   *    have been loaded and thus available for use directly in the relevant object pools), 
   *    return <tt>o</tt>; 
   *  else
   *    return <tt>null</tt>
   *    
   *  <p>throws DataSourceException if fails to load objects from data source, 
   *  NotPossibleException if fail to create object
   *  
   *  @version 3.0
   */
  public abstract <T> T reloadObject(Class<T> c, final Oid oid) throws NotPossibleException, DataSourceException;
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ oid != null
   *  /\ oid is a valid object id of an object of c /\
   *  fromAssocCls != null /\ fromLinkedAttrib != null /\ 
   *  fromAssocCls is associated to c via attribute fromLinkedAttrib  
   *   
   * @effects 
   * <pre>
   *  load from the data source object o of c whose id is oid and also load the objects associated to <tt>o</tt>
   *  (but exclude that which is associated via <tt>fromAssocCls.fromLinkedAttrib</tt>)
   *  
   *  throws NotFoundException if object is not found;  
   *  DBException if fails to load object(s) from data source.
   *  </pre>
   *  
   * @version 
   * - 2.7.4 <br>
   * - 3.1: support fromAssocOid 
   */
  public abstract <T> T readAssociatedObject(Class<T> c, Oid oid, Class fromAssocCls,
      Oid fromAssocOid, 
      DAttr fromLinkedAttrib) throws NotPossibleException,
      DataSourceException;

  /**
   * @effects delete class store record of the domain object <code>o</code> from
   *          the class store of the domain class <code>c</code>;
   *          
   *           <p>Throws
   *          <code>DataSourceException</code> if failed to do so.
   * @requires the class store of <code>c</code> contains a record for
   *           <code>o</code>
   */
  public abstract void deleteObject(Class c, Object o) throws DataSourceException;

  /**
   * @effects 
   *  delete class store records of <tt>c</tt> that satisfies <tt>searchQuery</tt> 
   *  
   *  <p>Throws DataSourceException if failed to do so.
   *  
   * @requires the class store of <code>c</code> has been created
   */
  public abstract void deleteObjects(Class c, Query<ObjectExpression> searchQuery) throws DataSourceException;
  
  /**
   * @requires 
   *  c is a registered domain class /\ o is an instance of c /\ subType is a sub-type of c 
   *  
   * @effects 
   *  transform <tt>o</tt> to become an object of <tt>subType</tt> by updating the underlying data store of <tt>subType</tt>.
   *  Throws DataSourceException if failed to update data source.
   *  
   *  <br><i>Note</i>: No actual subType object is created by this method and so client application that needs to access this object
   *  is required to reload objects from the data source.  
   *
   * @version 3.3
   */
  public abstract void transformObjectToASubtype(Class c, Object o, Class subType) throws DataSourceException;

  /**
   * @requires 
   *  c is a registered domain class /\ o is an instance of c /\ supType is a super-type of c 
   *  
   * @effects 
   *  transform <tt>o</tt> to become an object of <tt>supType</tt> by updating the underlying data store of <tt>c</tt> and <tt>supType</tt> (if needed).
   *  Throws DataSourceException if failed to update data source.
   *  
   *  <br><i>Note</i>: No actual <tt>supType</tt> object is created by this method and so client application that needs to access this object
   *  is required to reload objects from the data source.
   *    
   * @version 3.3 
   */
  public abstract void transformObjectToSupertype(Class c, Object o, Class supType) throws DataSourceException;
  
  /**
   * @requires 
   *   c is a domain class  /\ 
   *   linkedObjId is a valid object id /\ 
   *   assoc and targetAssoc are two ends of an association from c to linked object's class /\
   *   <tt>exps</tt> are defined over <tt>c</tt>
   *   
   * @effects 
   *  If exist in the class store of <tt>c</tt> the object ids that are of the objects linked to the domain object 
   *  identified by <tt>linkedObj</tt> via the specified association <tt>assoc</tt>  
   *  and, if <tt>exps.length > 0</tt>, then also satisfy <tt>exps</tt>
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  <br>throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   * @version 
   * - 3.0: support <tt>exps</tt><br>
   * - 3.2: change linkedObjId -> linkedObj
   */  
  public abstract Collection<Oid> readLinkedObjectIds(Class c,
      Tuple2<DAttr, DAssoc> assoc, 
      //Oid linkedObjId,
      Object linkedObj,
      Expression...exps)  
          throws DataSourceException, NotPossibleException;
  
  /**
   * @requires 
   *   c is a domain class  /\ 
   *   linkedObjId is a valid object id /\ 
   *   assoc and targetAssoc are two ends of an association from c to linked object's class
   *   
   * @effects 
   *  If exist object ids of <tt>c</tt> that are of the objects satisfying <tt>query</tt> (if specified) and being 
   *  linked to the domain object <tt>linkedObj</tt> via the specified association <tt>assoc</tt>
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  <br>throws NotPossibleException if id values are invalid or 
   *  DBException if fails to read ids from the data source.
   * @version 
   * - 3.1 <br>
   * - 3.2: use linkedObj instead of linkedObjId
   */
  public abstract Collection<Oid> readLinkedObjectIds(Class c,
      Tuple2<DAttr, DAssoc> assoc, 
      // Oid linkedObjId,
      Object linkedObj,
      Query query)
      throws NotPossibleException, DataSourceException;

  /**
   * @requires 
   *   c is a domain class  /\ 
   *   (query != null -> query is a valid Query over c)
   *   
   * @effects
   * <pre> 
   *  if query is not null 
   *    translate <tt>query</tt> into a source query and 
   *    execute this query to find all the object ids of c from the data source
   *    that satisfy it
   *  else 
   *    read all the object ids of c
   *    
   *  For a type-hierarchy, the object ids are created with the precise base class, 
   *  i.e. the class that defines the objects bearing the ids.
   *  
   *  If exist object ids matching the query 
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   *  </pre>   
   */
  public Collection<Oid> readObjectIds(final Class c, final Query query) 
  throws NotPossibleException, DataSourceException {
    boolean withSubtypes = true;

    return readObjectIds(c, 
        null, // aggregateFunc 
        query 
        ,withSubtypes
        //,null  // orderByClass
        );
  }

  /**
   * This method differs from {@link #readObjectIds(Class, Query)} in that it does not consider 
   * the sub-types of <tt>c</tt> (if any), and thus all the Oids retrieved are for objects of <tt>c</tt> only.
   * 
   * @requires 
   *   c is a domain class  /\ 
   *   (query != null -> query is a valid Query over c)
   *   
   * @effects
   * <pre> 
   *  if query is not null 
   *    translate <tt>query</tt> into a source query and 
   *    execute this query to find all the object ids of c from the data source
   *    that satisfy it
   *  else 
   *    read all the object ids of c
   *    
   *  If exist object ids matching the query 
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   *  </pre>   
   * @version 3.3 
   */
  public Collection<Oid> readObjectIdsWoutSubtypes(final Class c, final Query query) 
  throws NotPossibleException, DataSourceException {
    boolean withSubtypes = false;
    return readObjectIds(c, 
        null, // aggregateFunc 
        query 
        ,withSubtypes
        //,null  // order-by class
        );
  }
  
  /**
   * This is a more generic version of {@link #readObjectIds(Class, Query)} that supports order-by
   * 
   * @requires 
   *   c is a domain class  /\ 
   *   (query != null -> query is a valid Query over c)
   *   
   * @effects
   * <pre> 
   *  if query is not null 
   *    translate <tt>query</tt> into a source query and 
   *    execute this query to find all the object ids of c from the data source
   *    that satisfy it
   *  else 
   *    read all the object ids of c
   *    
   *  For a type-hierarchy, the object ids are created with the precise base class, 
   *  i.e. the class that defines the objects bearing the ids.
   *  
   *  If orderByClass != null 
   *    arrange the Oids by the key attribute(s) of those specified in orderByClass
   *  else
   *    arrange Oids by the natural order 
   *    
   *  If exist object ids matching the query 
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   *  </pre>   
   *  @version 3.3
   */
  public Collection<Oid> readObjectIds(final Class c, final Query query, final Class...orderByClass) 
  throws NotPossibleException, DataSourceException {
    boolean withSubtypes = true;

    return readObjectIds(c, null, query, withSubtypes ,orderByClass);
  }
  
  /**
   * @requires 
   *   c is a domain class  /\ 
   *   (query != null -> query is a valid Query over c) /\ 
   *   (aggregateFunc != null -> aggregateFunc is a valid aggregate function over the attributes
   *          of c)
   *   
   * @effects
   * <pre> 
   *  if query is not null 
   *    translate <tt>query</tt> into a source query with <tt>aggregateFunc</tt> (if specified)
   *    execute this query to find all the object ids of c from the data source
   *    that satisfy it
   *  else 
   *    read all the object ids of c
   *    
   *  if withSubTypes = true AND c has sub-types
   *    the object ids of the sub-type objects are created with the precise sub-type classes, 
   *    i.e. the class that defines the objects bearing the ids.
   *  
   *  If exist object ids matching the query 
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   *  </pre>   
   *  
   *  @example
   *    <pre>
   *    Example 1 (no query):
   *    =====================
   *    c = Student.class;
   *    aggregateFunc = null;
   *    query = null;
   *    -> get all the object ids of Student.class
   *    
   *    Example 2 (with query):
   *    ========================
   *    c = Student.class;
   *    aggregateFunc = null;
   *    query = "name = Le";
   *    -> get all the object ids of Student.class whose name is equal to "Le"
   *    
   *    Example 3 (with aggregate):
   *    ===========================
   *    c = Student.class;
   *    aggregateFunc = "min";
   *    query = "name = Le";
   *    -> get the min object id of Student.class whose name is equal to "Le"
   *    </pre>
   */
  public Collection<Oid> readObjectIds(final Class c, String aggregateFunc, final Query query) 
  throws NotPossibleException, DataSourceException {
    // v3.3: call shared method
    //Class orderByClass = null;
    boolean withSubtypes = true;

    return readObjectIds(c, aggregateFunc, query, withSubtypes
        //, orderByClass
        );
  }
  
  /**
   * This is a more generic version of {@link #readObjectIds(Class, String, Query)} that supports order-by
   * 
   * @requires 
   *   c is a domain class  /\ 
   *   (query != null -> query is a valid Query over c) /\ 
   *   (aggregateFunc != null -> aggregateFunc is a valid aggregate function over the attributes
   *          of c)
   *   
   * @effects
   * <pre> 
   *  if query is not null 
   *    translate <tt>query</tt> into a source query with <tt>aggregateFunc</tt> (if specified)
   *    execute this query to find all the object ids of c from the data source
   *    that satisfy it
   *  else 
   *    read all the object ids of c
   *    
   *  if withSubTypes = true AND c has sub-types
   *    the object ids of the sub-type objects are created with the precise sub-type classes, 
   *    i.e. the class that defines the objects bearing the ids.  
   *  
   *  If orderByClass != null 
   *    arrange the Oids by the key attributes of the class(es) specified in orderByClass
   *  else
   *    arrange Oids by the natural order 
   *      
   *  If exist object ids matching the query 
   *    return them as Collection
   *  else
   *    return null 
   *  
   *  throws NotPossibleException if id values are invalid or 
   *  DataSourceException if fails to read ids from the data source.
   *  </pre>   
   *  
   *  @example
   *    <pre>
   *    Example 1 (no query):
   *    =====================
   *    c = Student.class;
   *    aggregateFunc = null;
   *    query = null;
   *    -> get all the object ids of Student.class
   *    
   *    Example 2 (with query):
   *    ========================
   *    c = Student.class;
   *    aggregateFunc = null;
   *    query = "name = Le";
   *    -> get all the object ids of Student.class whose name is equal to "Le"
   *    
   *    Example 3 (with aggregate):
   *    ===========================
   *    c = Student.class;
   *    aggregateFunc = "min";
   *    query = "name = Le";
   *    -> get the min object id of Student.class whose name is equal to "Le"
   *    </pre>
   * @version 3.3
   */
  public abstract Collection<Oid> readObjectIds(final Class c, String aggregateFunc, final Query query, final boolean withSubTypes, final Class...orderByClass) 
  throws NotPossibleException, DataSourceException;
  
  /**
   * @requires
   *  c is a registered domain class /\ q is a query over c
   *  
   * @effects <pre>
   *  if exists in the underlying class store of <tt>c</tt> a record for <tt>r</tt> that  
   *  satisfies <tt>q</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   * 
   *  <p>throws DataSourceException if failed to perform query over the data source
   * @version 3.3 
   */
  public abstract boolean existObject(Class c, Query q) throws DataSourceException;

  /**
   * @effects 
   *  read from data source and return a Map<Oid,Object> of 
   *  the Oids and the values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt> 
   *  (in the same order as retrieved from the data source), 
   *  or return <tt>null</tt> if no such objects exist
   */
  public abstract Map<Oid, Object> readAttributeValuesWithOids(Class c,
      DAttr attrib);


  /**
   * @effects 
   *  read from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>.
   *  Arrange the values in the natural order. 
   *  <p>If no values are found then return <tt>null</tt>
   */
  public abstract Collection readAttributeValues(Class c, DAttr attrib);

  /**
   * @requires 
   *  c is a registered domain class /\ 
   *  attrib is a domain attribute of c /\ 
   *  query is a Select-type object query that is defined over <tt>c</tt> and performs a projection over <tt>attrib</tt>
   *    
   * @effects 
   *  load from data source and return a Collection of (raw) values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
   *  that satisfy <tt>query</tt>. 
   *  <br>Arrange the values in the natural order. 
   *  <p>If no values are found then return <tt>null</tt>
   * @version 3.3
   */
  public abstract Collection readAttributeValues(Class c, DAttr attrib, FlexiQuery query);
  
  /**
   * This is a more generic version of {@link #readAttributeValues(Class, DAttr)}
   * 
   * @requires
   *  if <tt>orderByKey = true</tt> then <b>all</b> key attributes of <tt>c</tt> must by owned by <tt>c</tt> 
   *     
   * @effects 
   *  read from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>.
   *  <br>If <tt>orderByKey = true</tt> then arrange the values in the order of the key attribute, else arrange them in the natural 
   *  order.
   *    
   *  <p>If no values are found then return <tt>null</tt>
   *  
   *  <p>Throws NotPossibleException if failed to read values
   * @version 3.3
   */
  public abstract Collection readAttributeValues(Class c, DAttr attrib, final boolean orderByKey) throws NotPossibleException;
  
  /**
   * This is the more general version of {@link #readAttributeValues(Class, DAttr)} in that 
   * it support multiple attributes. 
   * 
   * @requires
   *  if <tt>orderByKey = true</tt> then <b>all</b> key attributes of <tt>c</tt> must by owned by <tt>c</tt> 
   *   
   * @effects 
   *  read from data source and return a {@link Map} of values of the attributes in 
   *  <tt>attributes</tt> of the domain class <tt>c</tt>. 
   *  <br>If <tt>orderByKey = true</tt> then arrange the values in the order of the key attribute, else arrange them in the natural.
   *  
   *  <p>If no values are found return <tt>null</tt>
   *  
   *  <p>Throws NotPossibleException if failed to read values
   *  
   * @version 
   * - 3.1: created
   * <br>- 3.3: changed result type to Map(DAttr,Collection> and add parameter orderByKey 
   * 
   */
  public abstract Map<DAttr, Collection> readAttributeValueTuples(Class c, DAttr[] attributes, final boolean orderByKey) throws NotPossibleException;
  
  /**
   * This is the more general version of {@link #readAttributeValuesWithOids(Class, DAttr)} in that 
   * it support multiple attributes. 
   * 
   * @effects 
   *  read from data source and return an <b>ordered</b> {@link Map} of values of the id attributes and the attributes in 
   *  <tt>attributes</tt> of the domain class <tt>c</tt>, 
   *  or return <tt>null</tt> if no domain objects of <tt>c</tt> exist.
   *  
   *  <p>The keys of the result map are as follows: <tt>"Oid",attrib1,...,attribN</tt>,
   *  where "Oid" is mapped to {@link List}<tt>(Oid)</tt> containing the Oids, and 
   *  <tt>attribK</tt> are the domain attributes in <tt>attributes</tt> 
   *  
   *  <p>The entries of the resulted map are arranged in the same order as the above key sequence.
   *  
   * @version 3.1
   */
  public abstract Map<Object, List> readAttributeValueTuplesWithOids(Class c,
      DAttr[] attributes);
  
  /**
   * 
   * @effects 
   *  read from the data source the value of the attribute <tt>attrib</tt> 
   *  of the domain object identified by <tt>oid</tt> of the domain class <tt>c</tt>
   *  
   *  <p>Return <tt>null</tt> if the object with the specified id is not found OR the actual 
   *  attribute value is <tt>null</tt>
   *   
   * @example
   *  <pre>
   *  c = Student
   *  oid = Student(S2014)
   *  attrib = Student:sclass (value = SClass(id=2)) 
   *  
   *  -> result := SClass(id=2) 
   *  </pre>
   */
  public abstract Object readAttributeValue(Class c, Oid oid, DAttr attrib);
  

  /**
   * This is a more general version of {@link #readAttributeValue(Class, Oid, DAttr)}, which supports 
   * multiple attributes instead of one.
   * 
   * @effects 
   *  read from the class store of <tt>c</tt> values of the attributes in <tt>attribs</tt> 
   *  of the domain object identified by <tt>oid</tt> of <tt>c</tt>
   *  
   *  <p>Return <tt>null</tt> if the object with the specified id is not found OR the actual 
   *  attribute values are all <tt>null</tt>
   *  
   * @version 3.3
   */
  public abstract Map<DAttr, Object> readAttributeValues(Class c, Oid oid, Collection<DAttr> attribs);
  
  /**
   * @effects
   *  count and return the number of class store records of the domain class <tt>c</tt> 
   *  or return -1 if no records are found 
   *  
   *  <p>Throws DataSourceException if fails to do so
   */
  public abstract int readObjectCount(Class c) throws DataSourceException;

  /**
   * @requires 
   *  a class store of c has been created in the data source /\ 
   *  currId is a valid domain object id of c
   * 
   * @effects
   *  reads from the class store of <tt>c</tt> and returns the Oid of the domain object that  
   *  immediately precedes currId (in natural ordering); or null if no such Oid exists
   *   
   *  <p>throws DataSourceException if fails to read from data source;
   *  NotPossibleException if id values are invalid
   */
  public abstract Oid readIdFirstBefore(Class c, DAttr idAttrib, Oid currId)  
      throws DataSourceException, NotPossibleException;

  /**
   * @requires 
   *  a class store of class c has been created in the data source /\ 
   *  currId is a valid domain object id of c
   * 
   * @effects
   *  reads from the class store of <tt>c</tt> and returns the Oid of domain object that 
   *  immediately proceeds currId (in natural ordering); or null if no such Oid exists
   *   
   *  <p>throws DataSourceException if fails to read from data source;
   *  NotPossibleException if id values are invalid
   */
  public abstract Oid readIdFirstAfter(Class c, DAttr idAttrib, Oid currId)  
      throws DataSourceException, NotPossibleException;

  /**
   * @requires 
   *  cls != null /\ assoc != null /\  
   *  attrib is a valid attribute of cls /\
   *  linkedObj is a valid domain object  /\ linkedObjOid is the Oid of linkedObj
   *  
   * @effects
   *  load and return from the class store of <tt>c</tt> the number of objects that are linked to a given domain object 
   *  <tt>linkedObj</tt> via the attribute <tt>attrib</tt> (of <tt>cls</tt>).
   *  
   *  <p>Throws DataSourceException if fails to retrieve the information from the data source
   * 
   *  @example
   *  <pre>
   *  c = Enrolment
   *  attrib = Enrolment.student
   *  linkedObj = Student<id=S2014>
   *  
   *  -> read all Enrolment records whose student_id='S2014'
   *  </pre>   
   */
  public abstract int readAssociationLinkCount(Class cls, DAttr attrib,
      Object linkedObj, Oid linkedObjOid) throws DataSourceException;
  
  /**
   * @effects 
   *  print the store structure of <tt>c</tt> to the standard output
   *  if exist class store records of <tt>c</tt> 
   *    print them to the standard output
   */
  public abstract void print(Class c) throws DataSourceException;


  /**
   * @effects 
   *  return the name of the default schema of the underlying data source
   *  (i.e. the schema in which data source objects are created by default)
   *  
   * @version 3.0
   */
  public abstract String getDefaultSchema();
  
  /**
   * @effects 
   *  return the source-specific data source schema for the specified <tt>objSchema</tt>
   * @version 3.0 
   */
  public abstract String getDataSourceSchema(String objSchema);

  /**
   * @effects 
   *  return the source-specific data source schema for the object schema specified 
   *  by <tt>domainCls</tt>.
   * @version 3.0
   */
  public String getDataSourceSchema(DClass domainCls) {
    final String defaultSchema = DCSLConstants.DEFAULT_SCHEMA;
    String objectSchema = (domainCls == null) ? null : domainCls.schema();
    /*v3.0: move to method 
     dbSchema = dbSchema.toUpperCase();
     */
    String dbSchema;
    
    if (objectSchema == null || objectSchema.equals(defaultSchema))
      dbSchema = getDefaultSchema();  // use default data source schema
    else 
      dbSchema = getDataSourceSchema(objectSchema);
    
    return dbSchema;
  }
  
  // v2.7.3 congnv: add methods updateTable, updateAttribute, createTableMappings, generateMapping
  public abstract void updateDataSourceSchema(Class c, DAttr dc,
      int fieldIndex, String oldFieldName, Map<DAttr, Object> changedAttribVals)
      throws DataSourceException;
  
  // v2.7.3 congnv
  public abstract String getDataSourceType(DAttr dc);

  /**
   * This method is used to get additional debugging of a code segment, on top of the static {@link #debug} setting (if specified),  
   * that involves certain operations of this. 
   * First turn on debugging 
   * by calling the method with <tt>true</tt>; next call the operations as desired; 
   * and then turn off debugging by calling the method with <tt>false</tt>   
   * 
   * @effects 
   *  turn debug on or off for all tasks performed by <tt>this</tt> (which may invole a data source)
   * @version 3.1  
   */
  public abstract void setDebugOn(boolean tf);

  /**
   * @effects 
   *  if debug is turned on for the current class (i.e. this class or a current subtype)
   *    return true
   *  else
   *    return false 
   * @version 
   *  5.4
   */
  protected boolean isDebug() {
    return Toolkit.getDebug(this.getClass());
  }
}
