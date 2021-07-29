package jda.modules.dodm.dom;

import static jda.modules.dodm.dsm.DSMBasic.AS;
import static jda.modules.dodm.dsm.DSMBasic.CC;
import static jda.modules.dodm.dsm.DSMBasic.DC;
import static jda.modules.dodm.dsm.DSMBasic.isAbstract;
import static jda.modules.dodm.dsm.DSMBasic.isTransient;

import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.common.expression.Op;
import jda.modules.common.filter.Filter;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.Associate;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Include;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dsm.AssociationFilter;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.ds.IdObjectMap;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.IdExpression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.DomainIdable;
import jda.mosa.model.Oid;
import jda.util.ObjectComparator;
import jda.util.ObjectMapSorter;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ChangeListener;
import jda.util.events.ObjectUpdateData;

/**
 * @overview 
 *  Represents the <b>Data Object Manager</b> component of the {@link DODMBasic}.
 *  
 * @author dmle
 * 
 */
@DClass(serialisable=false) // v5.0
public class DOMBasic {
  
//  /* v5.0: id attribute */
//  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
//  private int id;
//  private static int idCounter = 0;
//  // end 5.0
  
  //private Configuration config;
  private DODMConfig config;
  protected DSMBasic dsm;
  
  /** maps a domain class and its pool of objects 
   * @version 2.6.4.a: changed value to a LinkedHashMap
   * */
  //private Map<Class, List> classExts;
  protected Map<Class, IdObjectMap<Oid,Object>> classExts;

  /** maps a domain class to its change event object */
  protected Map<Class, ChangeEvent> changeEvents;

  /**
   * maps a domain class to its change event listeners. The listeners of a
   * domain class are notified (passing the change event object of this class
   * taken from {@link #changeEvents} as an argument) when an object of the class has
   * been created or changed.
   */
  private Map<Class, List<ChangeListener>> listenerMap;

  /**
   * maps the PK value of a domain object to the object itself. This map, which
   * is only used for objects that have auto-generated identifier attributes,
   * helps ensure that only one instance of these objects is available in
   * memory.
   * */
  private Map<String, Object> recToObjectMap;

  //private JavaDbOSM osm;
  protected OSM osm;
  
  private boolean oldDebug; // v3.1
  protected static boolean debug = Toolkit.getDebug(DOMBasic.class);
  
  private static final boolean loggingOn = Toolkit.getLoggingOn(DOMBasic.class);

  /** maps domain schema names to domain schema objects */
  private static Map<String, DSMBasic> schemas = 
      new LinkedHashMap<String, DSMBasic>();
  
  public DOMBasic(DODMConfig config, DSMBasic dsm) {
    
    this.config = config;
    this.dsm = dsm;
    
    classExts = new LinkedHashMap<Class, IdObjectMap<Oid,Object>>();
    recToObjectMap = new LinkedHashMap<String, Object>();

    changeEvents = new LinkedHashMap<Class, ChangeEvent>();
    listenerMap = new LinkedHashMap<Class, List<ChangeListener>>();

    boolean serialisable = config.isObjectSerialisable();
    
    if (serialisable) {
      osm = OSMFactory.getOsmInstance(
          config.getOsmType(), 
          config.getOsmConfig(), 
          this);
    }
  }

  public DSMBasic getDsm() {
    return dsm;
  }

  public OSM getOsm() {
    return osm;
  }
  
  /**
   * @effects 
   *  if this is configured to store objects into a data source
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   *  @version 2.8
   */
  public boolean isObjectSerialised() {
    return config.isObjectSerialisable();
  }
  
//  /**
//   * @effects 
//   *  return the data source manager object
//   */
//  public OSM getOsm() {
//    return osm;
//  }

//  /**
//   * @effects 
//   *  if dbt != null
//   *    return the name of the data source represented by dbt
//   *  else
//   *    return null; 
//   */
//  public String getDataSourceName() {
//    if (osm != null)
//      return osm.getDataSourceName();
//    else
//      return null;
//  }
  
  /**
   * @effects 
   *  if a data source object is created for the domain class c
   *    return true
   *  else
   *    return false
   */
  public boolean isCreatedInDataSource(Class c) {
    DClass cc = (DClass) c.getAnnotation(CC);
    boolean isTransient = (cc != null && cc.serialisable()==false);
    boolean exist;
    if (!isTransient && osm != null) {
      /*v3.0: use method
      String dbSchema = (cc == null) ? MetaConstants.DEFAULT_SCHEMA : cc.schema();
      dbSchema = dbSchema.toUpperCase();
      */
      String dbSchema = osm.getDataSourceSchema(cc);
      
      String tableName = c.getSimpleName();
  
      exist = osm.exists(dbSchema, tableName);
      
    } else {
      exist = false;
    }
    
    return exist;
  }
  
  /**
   * @effects 
   *  if this is associated to a data source manager and this manager
   *  is connected to a data source
   *    return true
   *  else
   *    return false
   */
  public boolean isConnectedToDataSource() {
    return osm != null && osm.isConnected();
  }

  /**
   * @requires 
   *  osm != null
   * @effects Executes each schema-manipulation statement in <code>filePath</code> to
   *          create the schema in the underlying data source represented by this
   *          , throwing <code>DataSourceException</code> if an
   *          error occured.
   */
  public void createSchemaFromFile(String filePath) throws DataSourceException {
    if (osm != null) {
      osm.createSchemaFromFile(filePath);
    }
  }

  /**
   * @requires 
   *  osm != null /\ domain schema has been created
   *  
   * @effects Executes each object-manipulation statement in <code>filePath</code> to
   *          create records in the underlying data source represented by this
   *          , throwing <code>DataSourceException</code> if an
   *          error occurred.
   */
  public void createObjectsFromFile(String filePath) throws DataSourceException {
    if (osm != null) {
      osm.createObjectsFromFile(filePath);
    }
  }
  
  /**
   * Registers change listeners to the change events concerning the object pool
   * of a domain class.
   * 
   * @effects add listener <code>l</code> to the list of
   *          <code>ChangeListener</code>s of the change events concerning the
   *          objects of the domain class <code>c</code>, and (recursively) of
   *          those concerning the objects of the sub-classes of this class, and
   *          so on.
   * @see #fireStateChanged(Class)
   */
  public void addChangeListener(Class c, ChangeListener l) {
    List<ChangeListener> ls = listenerMap.get(c);
    if (ls == null) {
      ls = new ArrayList<ChangeListener>();
      listenerMap.put(c, ls);
    }
    ls.add(l);

    // registers the same listener for events raised by the sub-classes of c
    // (because objects of these sub-classes are also objects of c)
    Class[] subs = dsm.getSubClasses(c);
    if (subs != null) {
      for (Class sub : subs) {
        addChangeListener(sub, l);
      }
    }
  }

  /**
   * Notifies all change listeners registered to the change event of the domain
   * class <code>c</code>
   * 
   * @effects 
   *  update <tt>ChangeEventSource</tt> object associated to the domain class <tt>c</tt>
   *  with <tt>changedObjects</tt> and the action <tt>act</tt> and 
   *  invoke stateChanged() of the <tt>ChangeListener</tt>s
   *  registered to <tt>c</tt>.
   * 
   * @see #addChangeListener(Class, ChangeListener)
   */
  protected void fireStateChanged(Class c, Collection changedObjects, LAName act) {
    // notify the change listeners for the domain class c
    // using the change event of that class
    List<ChangeListener> ls = listenerMap.get(c);
    if (ls != null) {
      ChangeEvent ce = changeEvents.get(c);
      ChangeEventSource src = (ChangeEventSource) ce.getSource();
      // pass the changed objects to the event source
      src.clear();
      src.addAll(changedObjects);
      src.setChangeAction(act);
      
      for (ChangeListener l : ls) {
        l.stateChanged(ce);
      }
    }
  }

  /**
   * A version of {@link #fireStateChanged(Class, List)} in which the second
   * argument is a single object instead of a <code>List</code>.
   */
  private void fireStateChanged(Class c, Object changedObject, LAName act) {
    // notify the change listeners for the domain class c
    // using the change event of that class
//    List<ChangeListener> ls = listenerMap.get(c);
//    if (ls != null) {
//      ChangeEvent ce = changeEvents.get(c);
//      ChangeEventSource src = (ChangeEventSource) ce.getSource();
//      // pass the changed objects to the event source
//      src.clear();
//      src.add(changedObject);
//      src.setChangeAction(act);
//
//      for (ChangeListener l : ls) {
//        l.stateChanged(ce);
//      }
//    }
    fireStateChanged(c, changedObject, act, null);
  }

  /**
   * A version of {@link #fireStateChanged(Class, List)} which supports 
   * the specification of additional data associated to the event
   */
  private void fireStateChanged(Class c, Object changedObject, LAName act, Object data) {
    // notify the change listeners for the domain class c
    // using the change event of that class
    List<ChangeListener> ls = listenerMap.get(c);
    if (ls != null) {
      ChangeEvent ce = changeEvents.get(c);
      ChangeEventSource src = (ChangeEventSource) ce.getSource();
      // pass the changed objects to the event source
      src.clear();
      src.add(changedObject);
      if (data != null)
        src.setEventData(data);
      src.setChangeAction(act);

      for (ChangeListener l : ls) {
        l.stateChanged(ce);
      }
    }
  }
  
  /**
   * @effects <pre>
   *  if the schema named <code>name</code> does not exist in the data source
   *    create it
   *    if succeeded return <tt>true</tt>, else return <tt>false</tt>
   *  else
   *    return false </pre>
   */
  public boolean addSchemaIfNotExist(String name) throws DataSourceException {
    if (osm != null) {
      if (!osm.existsSchema(name)) {
        osm.createSchema(name);
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Note: method {@link #addClasses(Class[], boolean)} is preferred to this one.
   * 
   * @effects a short cut for {@link #addClass(Class, boolean)}, in which the
   *          second argument is <code>true</code> if
   *          <code>this.serialisable = true</code> or is <code>false</code> if
   *          otherwise.
   */
  public void addClass(Class c) throws DataSourceException, NotPossibleException {
    if (osm != null) {
      addClass(c, true, true);
    } else {
      addClass(c, false, false);
    }
  }

  /**
   * Use this method instead of {@link #addClass(Class, boolean, boolean)} if 
   * there are associations between the domain classes to be added. 
   * 
   * <p>In general, this method is preferred.
   *  
   * @effects
   * <pre> 
   *  for each domain class <tt>c in classes</tt>
   *    registers <code>c</code> in <code>this</code> if not already done so 
   *    if <code>isTransient(c) = false</code> and a relational table of
   *          <code>c</code> does not already exists then creates it
   *          
   *    <p>if <tt>read = true</tt> reads the records from this
   *          table into <code>this.classExt[c]</code>
   *          
   *          <p>
   *          Creates a new <code>ChangeEvent</code> object for <code>c</code>
   *          and adds it to <code>this.changeEvents</code>.
   * 
   *          <p>
   *          Throws <code>NotPossibleException</code> if <code>c</code> is not
   *          a domain class; 
   *  NotFoundException if <tt>c</tt> the  
   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found; 
   *          <code>DBException</code> if could not operate on
   *          the relational table of <code>c</code>.
   *          </pre>
   */
  public void addClasses(
      // v2.7.4: final Class[] classes,
      final Collection<Class> classes,
      boolean read)
      throws DataSourceException, NotPossibleException, NotFoundException {
    // to create if not exists
    boolean createIfNotExist = true;
    
    // the table constraints to be added afterwards
    Map<String,List<String>> tableConstraints = new LinkedHashMap<String,List<String>>();
    
    // v3.0: keep track of the db schemas to create
    Stack<String> schemas = new Stack();
    
    // create tables without constraints first
    for (Class c : classes) {
      if (!classExts.containsKey(c)) {
        // register if not yet done so
        /*v2.7.4: throw Exception here to force calling code to register class first 
        registerClass(c);
        */
        throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED,
            new Object[] {c.getName()});
      }
      
      // load the class objects (if any)
      DClass cc = (DClass) c.getAnnotation(DSMBasic.CC);
      boolean isTransient = (cc != null && cc.serialisable()==false);
      
      if (!isTransient && osm != null) {
        String dbSchema = osm.getDataSourceSchema(cc);
        
        /* v3.0: create dbSchema if not a default and not exist*/
        if (dbSchema != null) {
          if (!schemas.contains(dbSchema)) {
            schemas.push(dbSchema);
            addSchemaIfNotExist(dbSchema);
          }
        }
        
        // create table
        String tableName = c.getSimpleName();

        boolean exist = osm.exists(dbSchema, tableName);

        if (createIfNotExist && !exist) {
          // if a table of this class not exists then create
          osm.createClassStoreWithoutConstraints(c, tableConstraints);
          
          /* v2.7.3 congnv: mapping - generate & store mapping
          Collection<Mapping> mappings = dsm.generateMappings(c);
          addObjects(mappings);
          */
        } else if (exist && read) {
          /* v2.7.3 congnv: mapping - update table
          updateMappings(c);
          */
          
          // clear existing objects (if any)
          Collection objects = getObjects(c);
          if (!objects.isEmpty()) {
            objects.clear();
          }

          // load its objects
          retrieveObjectsWithAssociations(c);
        }
      }

      // prepare a state change event that will be raised when class objects
      // are changed
      if (!changeEvents.containsKey(c)) { // v2.7.3: added this check
        ChangeEventSource dsHelper = new ChangeEventSource(c);
        ChangeEvent ce = new ChangeEvent(dsHelper);
        changeEvents.put(c, ce);      
      }
    }
    
    // insert table constraints afterwards
    if (osm != null && !tableConstraints.isEmpty()) {
      osm.createConstraints(tableConstraints);
    }
  }
  
  /**
   * Adds a domain class to this schema.
   * 
   * @effects if <code>c</code> is registered in <code>this</code> and
   *          <code>isTransient(c) = false</code> if a relational table of
   *          <code>c</code> does not already exists then creates it
   *          
   *          <p>if <tt>read = true</tt> reads the records from this
   *          table into <code>this.classExt[c]</code>
   * 
   *          <p>
   *          Creates a new <code>ChangeEvent</code> object for <code>c</code>
   *          and adds it to <code>this.changeEvents</code>.
   * 
   *          <p>
   *          Throws <code>NotPossibleException</code> if <code>c</code> is not
   *          a domain class; 
   *  NotFoundException if <tt>c</tt> the  
   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found; 
   *          <code>DBException</code> if could not operate on
   *          the relational table of <code>c</code>.
   * @requires <code>c != null</code> and is a registered domain class
   * @modifies <code>classExt,</code> and <code>database_table(c)</code>
   * 
   * @see #registerClass(Class)
   */
  public void addClass(final Class c, boolean create, boolean read)
      throws DataSourceException, NotPossibleException, NotFoundException {
    if (!classExts.containsKey(c)) {
      registerClass(c);
    }
    
    // load the class objects (if any)
    DClass cc = (DClass) c.getAnnotation(DSMBasic.CC);
    boolean isTransient = (cc != null && cc.serialisable()==false);

    if (!isTransient && osm != null) {
      //final String defaultSchema = osm.getDefaultSchema(); //v3.0: MetaConstants.DEFAULT_SCHEMA.toUpperCase();
      //String objectSchema = (cc == null) ? defaultSchema : cc.schema();
      /*v3.0: move to method 
       dbSchema = dbSchema.toUpperCase();
       */
      String dbSchema = osm.getDataSourceSchema(cc);
      
      /* v3.0: create dbSchema if not a default and not exist*/
      if (dbSchema != null) {
        addSchemaIfNotExist(dbSchema);
      }
      
      // create table
      String tableName = c.getSimpleName();

      boolean exist = osm.exists(dbSchema, tableName);

      if (create && !exist) {
        // if a table of this class not exists then create
        osm.createClassStore(c);
      } else if (exist && read) {
        // clear existing objects (if any)
        Collection objects = getObjects(c);
        if (!objects.isEmpty()) {
          objects.clear();
        }

        // load its objects
        retrieveObjectsWithAssociations(c);
      }
    }
  }

  /**
   * @effects adds <code>c</code> to <code>this</code> or throws
   *          <code>NotPossibleException</code> if <code>c</code> is not a
   *          domain class.
   * @requires <code>c != null</code> and is a domain class
   * @modifies <code>this.classDefs, classConstraints</code>
   */
  public void registerClass(Class c) throws NotPossibleException {
    if (classExts.containsKey(c)) {
      return;
    }

    // the object pool
    IdObjectMap<Oid,Object> objects = new IdObjectMap<Oid,Object>();
    classExts.put(c, objects);
    

    // prepare a state change event that will be raised when class objects
    // are changed
    ChangeEventSource dsHelper = new ChangeEventSource(c);
    ChangeEvent ce = new ChangeEvent(dsHelper);
    changeEvents.put(c, ce);
  }

  /**
   * This method works similar in spirit to {@link #registerClass(Class)} in
   * that it registers an interface class to the domain schema. However, it
   * applies only to <b>enum-type interfaces</b>. An enum-type interface is an
   * interface that exists only for the purpose of defining constant values in
   * the form of one or more (static) enum members. The enum members must
   * implement the interface.
   * 
   * <p>
   * For example, the following <code>Action</code> interface is an enum-type interface that defines various
   * types of application actions.
   * 
   * <pre>
   * public interface Action {
   *   public static enum AppAction implements Action {
   *     Close, //
   *     Report, //
   *     Exit; //
   *     
   *     public String getName() {
   *      return name();
   *     }
   *   }
   * 
   *   public static enum DataAction implements Action {
   *     Open, New, Refresh, //
   *     Create, Update, Cancel, Reset, //
   *     Delete, //
   *     Next, Last, Previous, First, //
   *     Search, ClearSearch, //
   *     Export, //
   *     
   *     public String getName() {
   *      return name();
   *     }
   *   }
   * 
   *   public String name();
   * }
   * </pre>
   * 
   * <p>Two important design features of the enum-type interface must be observed. The enum members must implement the interface and  
   * must implement the method <code>getName()</code> which simply invokes the method <code>name()</code> to return <code>String</code>. 
   * In the above example, the three enum members <code>AppAction, DataAction, </code> and <code>TableAction</code> have <code>implements Action</code> 
   * in their declaration and that they each define the method <code>getName</code>.  
   * 
   * <br> Note that the <code>Action.name()</code> method declaration is not essential for the purpose of registration into the domain schema. 
   * It exists merely for the purpose of using <code>Action</code> as the super-type for all the declared enum constants
   * in the application code.  
   * 
   * <p>The only thing that needs to happen when registering an enum-type interface is to add all the enum constants to 
   * the object pool. However, unlike {@link #registerClass(Class)}, this objects pool is not stored into the database. 
   * They exist only in memory so that the can later be retrieved by the {@link #getObjects(Class)} method. 
   * This by the way is the recommended way of retrieving the objects of an enum-type interface via the domain schema 
   * (method {@link #readObjects(Class) clearly will not work).
   *  
   * @effects if <code>ic</code> is an interface and there are <code>enum</code>
   *          members of this interface then puts all the enum constants into
   *          the object pool of <code>ic</code>; throws
   *          <code>NotPossibleException</code> if <code>ic</code> does not have
   *          any enum members or no enum members implement <code>ic</code>
   * @requires <code>ic</code> has enum members and at least one member
   *           implements <code>ic</code>
   */
  public void registerEnumInterface(Class ic) throws NotPossibleException {
    if (classExts.containsKey(ic)) {
      return;
    }
    
    // the object pool
    IdObjectMap<Oid,Object> objects = new IdObjectMap<Oid,Object>();
    classExts.put(ic, objects);

    Class m;
    Object[] vals;
    boolean hasEnum = false;
    /**
     * First, if there are enum members that implements ic then for each member
     * adds their constants to the object pool of ic
     * 
     */
    /*v3.1: support inclusion filter
    Class[] members = ic.getClasses();
    if (members.length > 0) {
      for (int i = 0; i < members.length; i++) {
        m = members[i];
        if (m.isEnum() && ic.isAssignableFrom(m)) {
          if (!hasEnum)
            hasEnum = true;
          vals = m.getEnumConstants();
          for (Object v : vals) {
            try {
              addObject(ic, v, false);
            } catch (DataSourceException e) {
              // should not happen
            }
          }
        }
      } // end for
    }

    // Ic could be an enum, if so register its constants
    if (ic.isEnum()) {
      vals = ic.getEnumConstants();
      if (!hasEnum)
        hasEnum = true;
      for (Object v : vals) {
        try {
          addObject(ic, v, false);
        } catch (DataSourceException e) {
          // should not happen
        }
      }
    }
    */
    Class[] members = ic.getClasses();
    if (members.length > 0) {
      for (int i = 0; i < members.length; i++) {
        m = members[i];
        if (m.isEnum() && ic.isAssignableFrom(m)) {
          if (!hasEnum)
            hasEnum = true;
          registerEnumConstants(m);
        }
      } // end for
    }

    // Ic could be an enum, if so register its constants
    if (ic.isEnum()) {
      if (!hasEnum)
        hasEnum = true;
      registerEnumConstants(ic);
    }

    if (!hasEnum) {
      throw new NotPossibleException(
          NotPossibleException.Code.CLASS_NOT_WELL_FORMED,
          "Lớp không được định nghĩa đúng: {0}", ic);
    }
  }
  
  /**
   * @effects 
   *  if <tt>c</tt> is defined with {@link Include} 
   *    register only the constants specified by it
   *  else
   *    register all constants 
   *  @version 3.1
   */
  private void registerEnumConstants(Class c) throws NotFoundException {
    
    Include include = (Include) c.getAnnotation(Include.class);
    if (include != null) {
      // has inclusion
      String[] members = include.members();
      Object v;
      
      for (String member : members) {
        try {
          v = Enum.valueOf(c, member);
          addObject(c, v, false);
        } catch (DataSourceException e) {
          // should not happen
        } catch (Exception e) { // member not found
          throw new NotFoundException(NotFoundException.Code.CONSTANT_NOT_FOUND, e,  
              new Object[] {member, c.getSimpleName()});
        }
      }
    } else {
      // no inclusion: register all
      Object[] vals = c.getEnumConstants();
      for (Object v : vals) {
        try {
          addObject(c, v, false);
        } catch (DataSourceException e) {
          // should not happen
        }
      }   
    } 
  }

  /**
   * @effects 
   *  initialise object pool of <tt>c</tt> to empty
   * @version 3.0
   * @deprecated NOT YET TESTED
   */
  public void registerAnnotation(Class c) {
    if (classExts.containsKey(c)) {
      return;
    }
    
    // the object pool
    IdObjectMap<Oid,Object> objects = new IdObjectMap<Oid,Object>();
    classExts.put(c, objects);
  }

  
  /**
   * @effects 
   *  if c is registered in this
   *    return true
   *  else
   *    return false
   */
  public boolean isRegistered(Class c) {
    return (classExts.containsKey(c));
  }
  
  /**
   * @requires 
   *  c is a valid domain class in this
   * @effects 
   *  if <tt>c</tt> is serialisable
   *    re-load metadata of <tt>c</tt> from data source
   *  else
   *    update the metadata of <tt>c</tt> based on the current state of its object pool
   * @version 2.8
   *  updated to support memory-based object store
   */
  private void updateMetadata(Class c) throws NotPossibleException, DataSourceException {
    
    boolean objectSerialised = isObjectSerialised();  // v2.8
    
    if (!isTransient(c)
        && objectSerialised // v2.8: added this check
        ) {
      // serialisable
      loadMetadata(c);
    } else {
      // non serialisable
      IdObjectMap<Oid,Object> objPool = classExts.get(c);
      if (objPool.isEmpty()) {
        objPool.clear();
      } else {
        // update min or max
        Oid minId = objPool.getMinId();
        Oid maxId = objPool.getMaxId();
        if (minId == null) {
          minId = objPool.findFirstId();
          objPool.setMinId(minId);
        } else {
          maxId = objPool.findLastId();
          objPool.setMaxId(maxId);
        }
      }
    }
  }
  
  /**
   * @requires 
   *  c is a valid domain class in this
   * @effects 
   *  if <tt>c</tt> is serialisable /\ {@link #isObjectSerialised()} = true
   *    re-load metadata of <tt>c</tt> from data source
   *  else
   *    update the metadata of <tt>c</tt> based on the current state of its object pool
   * @version 2.8
   */
  public void retrieveMetadata(Class c) throws NotPossibleException, DataSourceException {
    
    boolean objectSerialised = isObjectSerialised();  // v2.8

    /*v3.1: support the case in which c is a sub-type: 
     * must also load meatadata of the super-type (b/c c inherits attributes from super class) */
    Class sup = dsm.getSuperClass(c);
    if (sup != null) {
      retrieveMetadata(sup);
    }
    
    if (!isTransient(c)
        && objectSerialised // v2.8: added this check
        ) {
      // serialisable
      // initialise id pool range from the state of the object pool AND 
      // other auto-generated attributes
      loadMetadata(c);
    } else {
      // non serialisable: initialise id pool range from the state of the object pool
      // (this does not include the initialisation of other auto-generated attributes)
      IdObjectMap<Oid,Object> objPool = classExts.get(c);
      if (objPool.isEmpty()) {
        objPool.clear();
      } else {
        // update min or max
        Oid minId = objPool.getMinId();
        Oid maxId = objPool.getMaxId();
        if (minId == null) {
          minId = objPool.findFirstId();
          objPool.setMinId(minId);
        } else {
          maxId = objPool.findLastId();
          objPool.setMaxId(maxId);
        }
      }
    }
  }
  
  /**
   * @requires 
   *  cls is a domain class registered in this 
   *  /\ {@link #isObjectSerialised()} = true
   *  
   * @modifies cls
   * @effects 
   *  load (from the data source, <i>if necessary</i>) the metadata of <tt>cls</tt> from the storage and 
   *  invoke the relevant update methods of <tt>cls</tt> 
   *  passing in the metadata to update.
   *  
   *  <p>Throws DBException if fails to load metadata from the data source;
   *  NotPossibleException if fails to update <tt>cls</tt> with the metadata
   * @version 
   *  2.8: changed to private 
   */
  //public 
  private void loadMetadata(Class cls) throws DataSourceException, NotPossibleException {
    
    /*
     * This is a general pseudocode for updating class attributes  
     * ---------------------------------------------------------------------
     * for each class attribute a of cls
     *  read its annotation to know which domain attribute (e.g. b) of cls that a is used to define
     *  query storage for the max value v of b
     *  init u
     *  if v.type != a.type
     *    parse v to obtain u whose type matches a.type (using a parse method of cls)
     *  else 
     *    u = v
     *  set cls.a = u (using an update method of cls)
     *  
     *  e.g.: cls = Student, a = currNum 
     *          -> b = id, v = "S2013" (by invoking dbt.readMaxValue(Student.class,"id")), 
     *             u = 2013 (by invoking Student.parseCurrNum("S2013")) 
     *          -> cls.a = 2013 (by invoking setCurrNum(2013)) 
     */
    
    // process auto, possibly deriving, attributes
    Collection<DAttr> autoAttribs = dsm.getAutoDomainAttributes(cls);
    
    // Two (Class,Object) pairs, in each pair: the class and the value that it owns
    Tuple2<Tuple2<Class,Object>, Tuple2<Class,Object>> idVals; 
    
    Tuple2<Object,Object> autoValRange;
    if (autoAttribs != null) {
      String[] derived;
      
      for (DAttr autoAttrib : autoAttribs) {
        // only interested in serialisable attributes
        if (!autoAttrib.serialisable()) {
          continue;
        }
        
        derived = autoAttrib.derivedFrom();
        
        if (!Arrays.equals(derived, CommonConstants.EmptyArray)) {
          // load min-max values from db
          // get all the derived attributes
          int numDerived = derived.length;
          DAttr[] derivedAttribs = new DAttr[numDerived];
          for (int i = 0; i < numDerived; i++) {
            derivedAttribs[i] = dsm.getDomainConstraint(cls, derived[i]);
          }

          try {
            // get the min-max value ranges of autoAttrib group by the derived attributes
            Map<Tuple, Tuple2<Object,Object>> vals = osm.readValueRange(cls, autoAttrib, derivedAttribs);
            
            // update
            Object partMinVal, partMaxVal;
            Comparable pmv, pxv, mv = null, xv = null;
            Tuple derivingVal;
            for (Entry<Tuple, Tuple2<Object,Object>> entry : vals.entrySet()) {
              derivingVal = entry.getKey(); 
              autoValRange = entry.getValue();
              
              // v3.3: handle the case autoValRange = null
              if (autoValRange == null) continue;
              
              partMinVal = autoValRange.getFirst();
              partMaxVal = autoValRange.getSecond();
              
              if (!(partMinVal instanceof Comparable))
                throw new NotPossibleException(
                  NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                    new Object[] {cls.getSimpleName(), autoAttrib.name(), partMinVal});
              
              pmv = (Comparable) partMinVal;
    
              if (!(partMaxVal instanceof Comparable))
                throw new NotPossibleException(
                  NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                  new Object[] {cls.getSimpleName(), autoAttrib.name(), partMaxVal});
                
              pxv = (Comparable) partMaxVal;
  
              // update the class metadata
              updateAutoGeneratedValue(cls, autoAttrib, derivingVal, pmv, pxv);
  
              // update the aggregated min, max values
              if (mv == null || mv.compareTo(pmv) > 0)
                mv = pmv;
  
              if (xv == null || xv.compareTo(pxv) < 0)
                xv = pxv;
            } // end for: vals
            
            if (autoAttrib.id()) { // also the id attribute
              // update the object pool's id range
              /*v3.2: ASSUME in this case minCls and maxCls are same as cls
              updateObjectPoolIdRange(cls, autoAttrib, mv, xv);
              */
              //TODO: do we also need to find minCls and maxCls for this case?
              updateObjectPoolIdRange(cls, autoAttrib, cls, mv, cls, xv);
            }
          } catch (NotFoundException e) {
            // ignore 
            log(e, "loadMetadata");
          }
        } else {
          // no derived attributes
          try {
            /*v3.2: support id-specific value range reader 
            idVals = osm.readValueRange(cls, autoAttrib);
            Object minVal = idVals.getFirst();
            Object maxVal = idVals.getSecond();
            
            if (!(minVal instanceof Comparable))
              throw new NotPossibleException(
                NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                "Mã đối tượng không hợp lệ {0}<{1}>:{2} (cần kiều Comparable)",
                cls.getSimpleName(), autoAttrib.name(), minVal);
  
            Comparable mv = (Comparable) minVal;
            
            if (!(maxVal instanceof Comparable))
              throw new NotPossibleException(
                  NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                  "Mã đối tượng không hợp lệ {0}<{1}>:{2} (cần kiều Comparable)",
                  cls.getSimpleName(), autoAttrib.name(), maxVal);
            
            Comparable xv = (Comparable) maxVal;
          
            // update the class metadata
            updateAutoGeneratedValue(cls, autoAttrib, null, mv, xv);
            
            if (autoAttrib.id()) { // also the id attribute
              // update the object pool's id range
              updateObjectPoolIdRange(cls, autoAttrib, mv, xv);
            } */
            if (!autoAttrib.id()) {
              // non-id auto attribute
              autoValRange = osm.readValueRange(cls, autoAttrib);
              Object minVal = autoValRange.getFirst();
              Object maxVal = autoValRange.getSecond();
              
              if (!(minVal instanceof Comparable))
                throw new NotPossibleException(
                  NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                  new Object[] {cls.getSimpleName(), autoAttrib.name(), minVal});
    
              Comparable mv = (Comparable) minVal;
              
              if (!(maxVal instanceof Comparable))
                throw new NotPossibleException(
                    NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                    new Object[] {cls.getSimpleName(), autoAttrib.name(), maxVal});
              
              Comparable xv = (Comparable) maxVal;
            
              // update the class metadata
              updateAutoGeneratedValue(cls, autoAttrib, null, mv, xv);
            } else {
              // id, auto attrib: use a more specialised reader that returns also the sub-type that owns
              // the value (if any)
              // update the object pool's id range
              idVals = osm.readIdValueRange(cls, autoAttrib);
              Tuple2<Class,Object> minValRange = idVals.getFirst();
              Tuple2<Class,Object> maxValRange = idVals.getSecond();
              Class minCls = minValRange.getFirst();
              Object minVal = minValRange.getSecond();
              Class maxCls = maxValRange.getFirst();
              Object maxVal = maxValRange.getSecond();
              
              if (!(minVal instanceof Comparable))
                throw new NotPossibleException(
                  NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                  new Object[] {cls.getSimpleName(), autoAttrib.name(), minVal});
    
              Comparable mv = (Comparable) minVal;
              
              if (!(maxVal instanceof Comparable))
                throw new NotPossibleException(
                    NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                    new Object[] {cls.getSimpleName(), autoAttrib.name(), maxVal});
              
              Comparable xv = (Comparable) maxVal;
            
              // update the class metadata
              updateAutoGeneratedValue(cls, autoAttrib, null, mv, xv);
              
              // this is specific for id-attribute
              /* v3.2: pass also the classes that owns the values 
              updateObjectPoolIdRange(cls, autoAttrib, mv, xv);
              */
              updateObjectPoolIdRange(cls, autoAttrib, minCls, mv, maxCls, xv);
            }
          } catch (NotFoundException e) {
            // ignore 
            log(e, "loadMetadata");
          }
        }
      } // end for
    } // end auto-attributes
    
    // process non-auto, non-deriving id attributes
    Collection<DAttr> idAttributes = dsm.getIDDomainConstraints(cls);
    
    if (idAttributes != null) {
      for (DAttr idAttrib: idAttributes) {
        // ignore auto-attributes because already processed above
        if (idAttrib.auto())
          continue;
        
        // non-auto attributes, just read the value range
        try {
          /*v3.2: support id-specific value range reader
          idVals = osm.readValueRange(cls, idAttrib);
          Object minVal = idVals.getFirst();
          Object maxVal = idVals.getSecond();
           */
          idVals = osm.readIdValueRange(cls, idAttrib);
          Tuple2<Class,Object> minValRange = idVals.getFirst();
          Tuple2<Class,Object> maxValRange = idVals.getSecond();
          Class minCls = minValRange.getFirst();
          Object minVal = minValRange.getSecond();
          Class maxCls = maxValRange.getFirst();
          Object maxVal = maxValRange.getSecond();
          
          if (!(minVal instanceof Comparable))
            throw new NotPossibleException(
              NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
              new Object[] {cls.getSimpleName(), idAttrib.name(), minVal});
  
          Comparable mv = (Comparable) minVal;
          
          if (!(maxVal instanceof Comparable))
            throw new NotPossibleException(
                NotPossibleException.Code.INVALID_OBJECT_ID_TYPE,
                new Object[] {cls.getSimpleName(), idAttrib.name(), maxVal});
          
          Comparable xv = (Comparable) maxVal;
        
          // update the object pool's id range
          // this is specific for id-attribute
          /* v3.2: pass also the classes that owns the values 
          updateObjectPoolIdRange(cls, idAttrib, mv, xv);
          */
          updateObjectPoolIdRange(cls, idAttrib, minCls, mv, maxCls, xv);
        } catch (NotFoundException e) {
          // ignore 
          log(e, "loadMetadata");
        }
      }
    } else {
      // no id attributes
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
          "Lớp cấu hình chưa đầy đủ {0}", cls.getSimpleName());
    }
  }

  /**
   * @effects 
   *  update the min and max Oids of the attribute <tt>attrib</tt> of the object pool of domain class <tt>c</tt>
   *  to <tt>minVal</tt> and <tt>maxVal</tt>, respectively
   *  
   *  @version 
   *  - 3.2: added minCls, maxCls
   */
  private void updateObjectPoolIdRange(Class c, DAttr attrib,
      Class minCls, Comparable minVal, Class maxCls, Comparable maxVal) {
    // TODO: assume that this is the ONLY id attribute used in the
    // object pool of c

    /*v3.2: use minCls, maxCls to create oid
     * Note: minCls, maxCls may be the sub-type of or same as c
    Oid minId = genObjectId(c, attrib, (Comparable) minVal);
    Oid maxId = genObjectId(c, attrib, (Comparable) maxVal);
    */
    Oid minId = genObjectId(minCls, attrib, (Comparable) minVal);
    Oid maxId = genObjectId(maxCls, attrib, (Comparable) maxVal);
    
    // update id range in the object pool of cls
    IdObjectMap objMap = classExts.get(c);
    objMap.setMinId(minId);
    objMap.setMaxId(maxId);
  }

  /**
   * @effects 
   *  update the auto-generated values of attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
   *  using <tt>minVal</tt> and <tt>maxVal</tt>, which are derived from <tt>derivingVal</tt>
   *  
   *  <p>Throws NotFoundException if no suitable update method is found;
   *  NotPossibleException if fails to perform the update method
   */
  private void updateAutoGeneratedValue(Class c, DAttr attrib,
      Tuple derivingVal, Comparable minVal, Comparable maxVal) throws NotFoundException, NotPossibleException {
    // find the metadata update method of the class c
    Method m = dsm.findMetadataAnnotatedMethodWithNamePrefix(c, DOpt.Type.AutoAttributeValueSynchroniser, null);
    
    // invoke the method
    try {
      m.invoke(null, attrib, derivingVal, minVal, maxVal);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          e, "Không thể thực thi phương thức {0}.{1}({2})", c, "updateAutoGeneratedValue", "...");
    }
  }
  
  /**
   * @effects 
   *  if id-range of c has been initialised
   *    return true
   *  return false
   */
  public boolean isIdRangeInitialised(Class c) {
    IdObjectMap objectMap = classExts.get(c);
  
    return objectMap.isIdRangeInitialised(); 
  }
  
  
  /**
   * @requires 
   *  {@link #isObjectSerialised()} = true
   *  
   * @effects 
   *  if exists data source constraints (e.g. FKs) of <tt>c</tt>
   *    return a List of their names (in the definition order)
   *  else
   *    return null
   * @version 
   *  2.6.4.b: read FK constraints only. 
   */
  public List<String> loadDataSourceConstraints(Class c) {
    return osm.readDataSourceConstraint(c);
  }

  /**
   * @effects 
   *  remove the data source constraint of the table associated to the domain class 
   *  <tt>c</tt>, whose name is <tt>name</tt>
   *  
   *  <p>Throws DBException if failed to remove the constraint. 
   */
  public void deleteDataSourceConstraint(Class c, String consName) throws DataSourceException {
    osm.dropDataSourceConstraint(c, consName);
  }

  /**
   * @effects  
   *  delete the data source structure of the schema named <tt>schemaName</tt> (but 
   *  keep the schema)
   */
  public void deleteDomainSchema(String schemaName) throws DataSourceException {
    if (osm != null && osm.isConnected()) {
      //TODO: generalise this
      osm.deleteDomainSchema(schemaName);
    }
  }
  
  /**
   * @effects remove <code>c</code> from <code>this</code> and if
   *          <code>deleteFromDB = true</code> then also drop the database table
   *          of <code>c</code>
   * @modifies <code>classDefs, classConstraints, classExts, changeEvents, database_table(c)</code>
   */
  public void deleteClass(Class c, boolean deleteFromDB) throws DataSourceException {
//    classDefs.remove(c);
//
//    classRelDefs.remove(c);
//
//    classConstraints.remove(c);
//
//    classAssocs.remove(c);
    
    classExts.remove(c);

    changeEvents.remove(c);

    listenerMap.remove(c);

    if (osm != null) {
      if (deleteFromDB) {
        // try {
        DClass cc = (DClass) c.getAnnotation(CC);
        /*v3.0: use source-specific method
        String dbSchema = (cc == null) ? MetaConstants.DEFAULT_SCHEMA : cc.schema();
        dbSchema = dbSchema.toUpperCase();
        */
        String dbSchema = osm.getDataSourceSchema(cc);
        
        String tableName = c.getSimpleName();
        if (osm.exists(dbSchema, tableName)) {
          osm.dropClassStore(c);
        }
      }
    }
  }

  // v3.0
//  /**
//   * @effects removes the domain classes in <code>classes</code> array from
//   *          <code>this</code> and if <code>deleteFromDB=true</code> then also
//   *          remove them from the database.
//   * @deprecated as of v2.7.3 (use {@link #deleteClasses(List, boolean)} instead)
//   */
//  public void deleteClasses(Class[] classes, boolean deleteFromDB)
//      throws DataSourceException {
//    if (!deleteFromDB) {
//      for (Class c : classes) {
//        try {
//          deleteClass(c, false);
//        } catch (DataSourceException e) {
//          // should not happen
//        }
//      }
//    } else {
//      List<Class> classList = new ArrayList();
//      Collections.addAll(classList, classes);
//      Class c;
//      if (debug)
//        System.out.println("To remove " + classList.size()
//            + " classes from the database");
//
//      Map<Class, Integer> retryMap = new LinkedHashMap();
//
//      int numClasses = classList.size();
//      while (classList.size() > 0) {
//        c = classList.remove(0);
//        try {
//          if (debug)
//            System.out.println("deleting class " + c);
//
//          deleteClass(c, true);
//          if (debug)
//            System.out.println("...ok");
//        } catch (DataSourceException e) {
//          // check number of attempts
//          Integer attempts = retryMap.get(c);
//          if (attempts != null && attempts >
//            numClasses //classList.size()
//          ) {
//            // impossible to retry (something really wrong, e.g. class is
//            // orphaned)
//            // stop
//            throw (e);
//          } else {
//            // record number of attempts
//            if (attempts == null)
//              attempts = 1;
//            else
//              attempts = attempts + 1;
//
//            retryMap.put(c, attempts);
//          }
//          if (debug)
//            System.out.println("...failed (to retry)");
//
//          // e.printStackTrace();
//
//          // perhaps caused by dependency, move table to the end of the list
//          // to try again later
//          classList.add(c);
//        }
//      }
//    }
//  }

  /**
   * @effects removes the domain classes in <code>classes</code> array from
   *          <code>this</code> and if <code>deleteFromDB=true</code> then also
   *          remove them from the database.
   */  
  public void deleteClasses(Class[] classes, boolean deleteFromDB
      , boolean strict // v3.0
      ) throws DataSourceException {
    deleteClasses(Arrays.asList(classes), deleteFromDB, strict);
  }

  /**
   * @modifies classList  
   * 
   * @effects removes the domain classes in <code>classes</code> array from
   *          <code>this</code> and if <code>deleteFromDB=true</code> then also
   *          remove them from the database.
   */  
  public void deleteClasses(List<Class> classList, boolean deleteFromDB
      , boolean strict // v3.0
      )
      throws DataSourceException {
    if (!deleteFromDB) {
      for (Class c : classList) {
        try {
          deleteClass(c, false);
        } catch (DataSourceException e) {
          // should not happen
        }
      }
    } else {
//      List<Class> classList = new ArrayList();
//      Collections.addAll(classList, classes);
      Class c;
      if (debug)
        System.out.println("To remove " + classList.size()
            + " classes from the database");

      Map<Class, Integer> retryMap = new LinkedHashMap();

      int numClasses = classList.size();
      while (classList.size() > 0) {
        c = classList.remove(0);
        try {
          if (debug)
            System.out.println("deleting class " + c);

          deleteClass(c, true);
          if (debug)
            System.out.println("...ok");
        } catch (DataSourceException e) {
          // check number of attempts
          Integer attempts = retryMap.get(c);
          if (attempts != null && attempts >
            numClasses //classList.size()
          ) {
//            // impossible to retry (something really wrong, e.g. class is
//            // orphaned)
//            // stop
//            throw (e);
            /*v3.0: 
             * if strict = false
             *  print error instead of throwing exception
             * 
             */
            if (strict) {
              throw (e);
            } else {
              System.err.printf("Failed to delete class: %s. Due to: %n%s%n", c, e.getMessage());
              if (debug) e.printStackTrace(); // v3.2: for debugging purposes 
            }
          } else {
            // record number of attempts
            if (attempts == null)
              attempts = 1;
            else
              attempts = attempts + 1;

            retryMap.put(c, attempts);
            
            if (debug)
              System.out.println("...failed (to retry)");

            // e.printStackTrace();

            // perhaps caused by dependency, move table to the end of the list
            // to try again later
            classList.add(c);
          }
        }
      }
    }
  }

// v3.0
//  /**
//   * @effects removes the domain objects of <code>classes</code> array from
//   *          <code>this</code> and from the database.
//   * @deprecated as of v2.7.3 (use {@link #deleteObjects(Collection)} instead)
//   */
//  public void deleteObjects(Class[] classes, 
//      boolean strict // v3.0
//      ) throws DataSourceException {
//    List<Class> classList = new ArrayList();
//    Collections.addAll(classList, classes);
//    /*v3.0
//    Class c;
//    if (debug)
//      System.out.println("To remove objects of " + classList.size()
//          + " classes from the database");
//
//    Map<Class, Integer> retryMap = new LinkedHashMap();
//
//    while (classList.size() > 0) {
//      c = classList.remove(0);
//      try {
//        if (debug)
//          System.out.println("deleting class " + c);
//
//        deleteObjects(c);
//        if (debug)
//          System.out.println("...ok");
//      } catch (DataSourceException e) {
//        // check number of attempts
//        Integer attempts = retryMap.get(c);
//        if (attempts != null && attempts > classList.size()) {
//          // impossible to retry (something really wrong, e.g. class is
//          // orphaned)
//          // stop
//          throw (e);
//        } else {
//          // record number of attempts
//          if (attempts == null)
//            attempts = 1;
//          else
//            attempts = attempts + 1;
//
//          retryMap.put(c, attempts);
//        }
//        if (debug)
//          System.out.println("...failed (to retry)");
//
//        // e.printStackTrace();
//
//        // perhaps caused by dependency, move table to the end of the list
//        // to try again later
//        classList.add(c);
//      }
//    }
//    */
//    deleteObjects(classList, strict);
//  }

  /**
   * @param strict 
   * @modifies classes
   * @effects removes the domain objects of <code>classes</code> array from
   *          <code>this</code> and from the database.
   */
  public void deleteObjects(List<Class> classes, 
      boolean strict // v3.0
      ) throws DataSourceException {
//    List<Class> classList = new ArrayList();
//    Collections.addAll(classList, classes);
    Class c;
    if (debug)
      System.out.println("To remove objects of " + classes.size()
          + " classes from the database");

    Map<Class, Integer> retryMap = new LinkedHashMap();

    while (classes.size() > 0) {
      c = classes.remove(0);
      try {
        if (debug)
          System.out.println("deleting class " + c);

        deleteObjects(c);
        if (debug)
          System.out.println("...ok");
      } catch (DataSourceException e) {
        // check number of attempts
        Integer attempts = retryMap.get(c);
        if (attempts != null && attempts > classes.size()) {
          // impossible to retry (something really wrong, e.g. class is
          // orphaned)
          // stop
          /*v3.0: 
           * if strict = false
           *  print error instead of throwing exception
           * 
           */
          if (strict) {
            throw (e);
          } else {
            System.err.printf("Failed to delete objects of class: %s. Due to: %n%s", c, e.getMessage());            
          }
        } else {
          // record number of attempts
          if (attempts == null)
            attempts = 1;
          else
            attempts = attempts + 1;

          retryMap.put(c, attempts);
          
          if (debug)
            System.out.println("...failed (to retry)");

          // e.printStackTrace();

          // perhaps caused by dependency, move table to the end of the list
          // to try again later
          classes.add(c);
        }
      }
    }
  }
  
  /**
   * @requires 
   *  objects != null
   * @effects
   *  add each object in <tt>objects</tt> into this and if a data source is specified
   *  then also store it to the data source 
   */
  public <T> void addObjects(Collection<T> objects) throws DataSourceException {
    for (T o : objects) {
      addObject(o);
    }
  }
  

  /**
   * Create a new object in the object pool of a its domain class and 
   * (optionally) a data source record from a domain object in the
   * underlying database table of its domain class.
   * 
   * @effects Adds <code>o</code> to <code>this</code> and if
   *          <code>isTransient(c) = false AND serialised=true</code> creates a database record of
   *          <code>o</code> in the table that was created from
   *          <code>o.class</code>.
   * 
   *          <p>
   *          If the super-class of <code>o.class</code> is also a domain class
   *          then adds <code>o</code> to this class. Repeats this until the
   *          super-class is <code>Object</code>.
   * 
   *          <p>
   *          If failed to insert record into the database then throws
   *          <code>DataSourceException</code>
   *          
   *          <p>If all succeeded fires the state change event
   *          associated to <code>o</code>'s class; return the <tt>Oid</tt> of <tt>o</tt>
   *          
   * @requires <code>o.class</code> is a registered domain class /\ 
   *    the object pool of o.class has been initialised 
   * @version 2.8
   */
  public Oid addObject(Object o, boolean serialised) throws DataSourceException {
    Class c = o.getClass();

    if (serialised) { // added this check
      // further check on serialisability of c 
      serialised = !isTransient(c);
    }

    Oid oid = addObject(c, o, serialised);

    // notify listeners
    fireStateChanged(c, o, LAName.New);
    
    return oid;
  }
  
  /**
   * @requires 
   *  o.class and all associated classes are registered in this
   *  
   * @effects 
   *  add to the object pool of <tt>o.class</tt> 
   *    and (recursively) add the associate objects of <tt>o</tt> to respectively object pools
   *  <p>if <tt>serialised = true</tt> then also store these objects to the data source
   *  
   *  <p>Throws NotPossibleException if failed to add an object to pool; DataSourceException if 
   *  failed to store object to data source
   *   
   * @version 2.8
   *   
   */
  public void addObjectWithAssociates(Object o, boolean serialised) throws NotPossibleException, DataSourceException {
    Stack added = new Stack();
    addObjectWithAssociates(o, added, serialised);
  }

  /**
   * @requires 
   *  o.class and all associated classes are registered in this
   *  
   * @effects 
   *  add to the object pool of <tt>o.class</tt> 
   *  and (recursively) add the associate objects of <tt>o</tt> to respectively object pools, 
   *  that are not in <tt>added</tt>
   *    add each newly added object to <tt>added</tt>
   *    
   *  <p>if <tt>serialised = true</tt> then also store these objects to the data source
   *  
   *  <p>Throws NotPossibleException if failed to add an object to pool; DataSourceException if 
   *  failed to store object to data source
   *   
   * @version 2.8
   *   
   */
  private void addObjectWithAssociates(Object o, Stack added, boolean serialised) throws NotPossibleException, DataSourceException {
    // add o 
    addObject(o, serialised);
    added.push(o);
    
    // if o has associated objects then add them
    Class c = o.getClass();
    Collection<Associate> associates = getAssociates(o, c);
    if (associates != null) {
      Object a;
      for (Associate associate : associates) {
        a = associate.getAssociateObj();
        if (!added.contains(a)) {
          // recursive
          addObjectWithAssociates(a, added, serialised);
        }
      }
    }
  }
  
  /**
   * Create a new database record from a domain object and inserts it into the
   * underlying database table of its domain class.
   * 
   * @effects Adds <code>o</code> to <code>this</code> and if
   *          <code>isTransient(c) = false</code> creates a database record of
   *          <code>o</code> in the table that was created from
   *          <code>o.class</code>.
   * 
   *          <p>
   *          If the super-class of <code>o.class</code> is also a domain class
   *          then adds <code>o</code> to this class. Repeats this until the
   *          super-class is <code>Object</code>.
   * 
   *          <p>
   *          If failed to insert record into the database then throws
   *          <code>DataSourceException</code>
   *          
   *          <p>If all succeeded fires the state change event
   *          associated to <code>o</code>'s class; return the <tt>Oid</tt> of <tt>o</tt>
   * @requires <code>o.class</code> is a registered domain class /\ 
   *    the object pool of o.class has been initialised 
   */
  public Oid addObject(Object o) throws DataSourceException {
    /*v2.8: moved to method
    Class c = o.getClass();

    boolean serialised = !isTransient(c);

    Oid oid = addObject(c, o, serialised);

    // notify listeners
    fireStateChanged(c, o, LAName.New);
    
    return oid;
    */
    return addObject(o, true);
  }

  /**
   * @effects if <code>serialised=true</code> then inserts <code>o</code> into
   *          the database table of <code>o.class</code>.
   * 
   *          <p>
   *          invokes <code>addObject(o.class,o)</code>
   */
  // private void addObject(Object o, boolean serialised) throws DBException {
  // // try to add to db first
  // if (serialised && dbt != null)
  // dbt.putObject(o);
  //
  // Class c = o.getClass();
  // addObject(c, o);
  // }

  /**
   * @effects adds domain object <code>o</code> to the extent of the
   *          domain class <code>c</code> and if <code>serialised=true</code>
   *          then inserts <code>o</code> into the database table of
   *          <code>c</code>.
   * 
   *          <p>
   *          Repeats the above for the domain super- and ancestor classes of
   *          <code>c</code> (if any).
   * @requires <code>c</code> is either the (actual) domain class of
   *           <code>o</code> or is the super- or ancester domain class of the
   *           domain class of <code>o</code>
   */
  protected Oid addObject(Class c, Object o, boolean serialised)
      throws DataSourceException {
    Oid oid = genObjectId(c, o);
    
    // v2.6.4b: store Oid into the object if it is an instance of DomainIdable
    if (o instanceof DomainIdable) {
      ((DomainIdable)o).setOid(oid);
    }

    addObject(c, o, oid, serialised);
    return oid;
//    // first, add the object to the the super class (if any)
//    // and so on recursively
//    Class sup = getSuperClass(c);
//    if (sup != null) {
//      addObject(sup, o, serialised);
//    }
//
//    // now add o to c, trying db first
//    if (serialised && dbt != null)
//      dbt.putObject(c, o);
//
//    //List objects = classExts.get(c);
//    //objects.add(o);
//    Map<Oid,Object> objects = classExts.get(c);
//    Oid oid = getObjectId(c, o);
//    objects.put(oid, o);
  }

  /**
   * @effects adds domain object <tt>o</tt> to the extent of the
   *          domain class <tt>c</tt> and if <tt>serialised=true</tt>
   *          then inserts <tt>o</tt> into the database table of
   *          <tt>c</tt>.
   * 
   *          <p>
   *          Repeats the above for the domain super- and ancestor classes of
   *          <tt>c</tt> (if any).
   *          
   *          <p>Return the old object that was previously associated to id in the pool.
   *          
   * @requires <tt>c</tt> is either the (actual) domain class of
   *           <tt>o</tt> or is the super- or ancester domain class of the
   *           domain class of <tt>o</tt>
   */
  protected Object addObject(Class c, Object o, Oid id, boolean serialised)
      throws DataSourceException {
    // first, add the object to the the super class (if any)
    // and so on recursively
    Class sup = dsm.getSuperClass(c);
    if (sup != null) {
      addObject(sup, o, id, serialised);
    }

    // now add o to c, trying db first
    if (serialised && osm != null)
      osm.putObject(c, o);

    Map<Oid,Object> objects = classExts.get(c);
    Object old = objects.put(id, o);
    
    return old;
  }
  

  /**
   * This method improves from {@link #createObject(Class, Object[])} in that it supports the 
   * mapping between parameter values and attributes.
   * 
   * @effects if invokes <code>o = newInstance(c,valsMap)</code> succeeds then
   *          invokes <code>addObject(o)</code> and returns <code>o</code>.
   * 
   *          <p>
   *          If no suitable constructor of <code>c</code> exists throws
   *          <code>NotFoundException</code>, else if could not create a new
   *          instance of <code>c</code> using the constructor throws
   *          <code>NotPossibleException</code>, else if failed to add the new
   *          object to the data source throws <code>DataSourceException</code>.
   * 
   * @requires <code>c != null</code>, <code>vals != null</code>
   * 
   * @version 5.0
   */
  public <T> Tuple2<Oid, T> createObject(Class<T> c, Map<DAttr, Object> valsMap) throws DataSourceException,
    NotFoundException, NotPossibleException {
    T o;
    if (valsMap == null || valsMap.isEmpty()) {
      o = dsm.newInstance(c);
    } else {
      o = dsm.newInstance(c, valsMap);
    }

    // add object to list
    Oid id = addObject(o);

    return new Tuple2<Oid,T>(id,o);
  }
  
  /**
   * A method to create a new object of a domain class from some data values and
   * adds it to <code>this</code>.
   * 
   * @effects if invokes <code>o = newInstance(c,vals)</code> succeeds then
   *          invokes <code>addObject(o)</code> and returns <code>o</code>.
   * 
   *          <p>
   *          If no suitable constructor of <code>c</code> exists throws
   *          <code>NotFoundException</code>, else if could not create a new
   *          instance of <code>c</code> using the constructor throws
   *          <code>NotPossibleException</code>, else if failed to add the new
   *          object to the database throws <code>DBException</code>.
   * 
   * @requires <code>c != null</code>, <code>vals != null</code>
   */
  public Tuple2<Oid,Object> createObject(Class c, Object[] vals) throws DataSourceException,
      NotFoundException, NotPossibleException {
    /* v2.8: added support for null arguments
    Object o = dsm.newInstance(c, vals);
     */
    Object o;
    if (vals == null) {
      o = dsm.newInstance(c);
    } else {
      o = dsm.newInstance(c, vals);
    }

    // add object to list
    Oid id = addObject(o);

    return new Tuple2<Oid,Object>(id,o);
  }


//  /**
//   * @effects 
//   *  init oldMap as Map<DomainConstraint,Object> to hold old values of the 
//   *  attributes whose values will be updated using attributeVals
//   *  
//   *  if <code>attributeVals != null</code> 
//   *    for each entry <dc,value> in attributeVals
//   *      if value is different from attribute's existing value
//   *        update attribute using value
//   *        add <attribute,value> to oldMap 
//   * 
//   *  <code>isTransient(o.class) = false</code> then updates
//   *  <code>o</code> in the database table of <code>o.class</code> and
//   *  in all the tables of the super and ancester domain classes of this
//   *  class.
//   *  
//   *  return oldMap
//   *  <p>
//   *  Throws <tt>NotFoundException</tt> if fails to retrieve domain attributes, 
//   *  <tt>NotPossibleException</tt> if fails to change attribute value, 
//   *  <tt>DBException</tt> if fails to update the object in the database.
//   * 
//   * @requires o is a domain object whose class contains attributes specified 
//   *  in attributeVals /\ o != null
//   *  
//   * @deprecated this method is not being used, do not use it.
//   */
//  public LinkedHashMap<DomainConstraint,Object> updateObjectOLD(Object o, Map<DomainConstraint,Object> attributeVals)
//      throws DBException, NotPossibleException, NotFoundException {
//    
//    final Class cls = o.getClass();
//
//    LinkedHashMap<DomainConstraint,Object> oldVals = new LinkedHashMap();
//    
//    if (attributeVals != null) {
//      // first update o with the new attribute values
//      // get the domain attributes of obj
//
//      String fname = null;
//      Field f = null;
//      Object newVal = null;
//      Object oldVal = null;
//      DomainConstraint dc;
//      try {
//        for (Entry<DomainConstraint,Object> e : attributeVals.entrySet()) {
//          dc = e.getKey();
//          // only update if this field is mutable
//          if (dc.mutable()) {
//            newVal = e.getValue();
//            fname = dc.name();
//            f = getDomainAttribute(cls, fname);
//            
//            // check that value has been changed before invoking setter
//            oldVal = getAttributeValue(f, o);
//            if ((oldVal == null && newVal != null) ||
//                (oldVal != null && !oldVal.equals(newVal))) {
//              // changed
//              oldVals.put(dc, oldVal);
//              setAttributeValue(o, f, newVal); 
//            }
//          }
//        }
//      } catch (Exception e) {
//        throw new NotPossibleException(
//            NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
//            "Không thể thực thi phương thức: {0}.{1}({2},{3})", o, "updateObject", 
//            fname, newVal);
//      }
//    }
//
//    // update the related database table(s)
//    // TODO: use oldVals to update object in database
//    // IMPORTANT: this cannot be done at the moment because 
//    //  vals are used differently by different controllers 
//    if (!isTransient(cls))
//      updateObjectIn(o, cls);
//    
//    return (oldVals.isEmpty()) ? null : oldVals;
//  }

  /**
   * @effects 
   *  update database records of <tt>c</tt> that satisfies <tt>searchExp</tt>
   *  using the expressions in <tt>updateQuery</tt>
   *   
   * @requires the database table of <code>c</code> has been created
   */
  public void updateObjects(Class c, Query<ObjectExpression> searchQuery,
      Query<ObjectExpression> updateQuery) throws DataSourceException {
    if (osm != null) {
      osm.updateObjects(c, searchQuery, updateQuery);
    }
  }
  
  /**
   * 
   * @requires 
   *  attributeVals != null /\ 
   *  o is a domain object whose class contains attributes specified 
   *  in attributeVals /\ o != null 
   * 
   * @effects 
   *   for each entry <dc,value> in attributeVals
   *     update the value of the attribute in o specified by dc using value
   * 
   *  <p>if <code>isTransient(o.class) = false</code> then updates
   *  <code>o</code> in the database table of <code>o.class</code> and
   *  in all the tables of the super and ancester domain classes of this
   *  class.
   *  
   *  <p>
   *  Throws <tt>NotFoundException</tt> if fails to retrieve domain attributes, 
   *  <tt>NotPossibleException</tt> if fails to change attribute value, 
   *  <tt>DBException</tt> if fails to update the object in the database.
   * @version 2.6.1, v2.6.4b  
   */  
  public void updateObject(Object o, 
      Map<DAttr,Object> attributeVals)
      throws DataSourceException, NotPossibleException, NotFoundException {
    updateObject(o, null, attributeVals, true);
  }
  
  /**
   * 
   * @requires 
   *  attributeVals != null /\ attributeVals contain <b>new</b> (updated) values of the updated attributes /\ 
   *  affectedVals != null /\ affectedVals contain <b>old</b> values of the updated attributes /\ 
   *  o is a domain object whose class contains attributes specified in attributeVals /\ o != null 
   * 
   * @modifies attribVals, affectedVals 
   *  
   * @effects 
   *   for each entry (dc,value) in attributeVals
   *     update the value of the attribute in o specified by dc using value
   * 
   *   <p>if there are derived attributes that depend on multiple deriving attributes, one of which is an attribute 
   *   in <tt>attributeVals</tt>
   *    update each of those derived attributes and place its new and old values into <tt>attributeVals, affectedVals</tt> (resp.)
   *   
   *  <p>if <code>isTransient(o.class) = false</code> then updates
   *  <code>o</code> in the database table of <code>o.class</code> and
   *  in all the tables of the super and ancester domain classes of this
   *  class.
   *  
   *  <p>
   *  Throws <tt>NotFoundException</tt> if fails to retrieve domain attributes, 
   *  <tt>NotPossibleException</tt> if fails to change attribute value, 
   *  <tt>DBException</tt> if fails to update the object in the database;
   *  ConstraintViolationException if an updated value is invalid
   *  
   * @version 
   * v2.7.2 <br>
   * v2.7.4: support ConstraintViolationException <br>
   * v3.1: support adding derived attribute values into mappings
   */  
  public void updateObject(Object o, 
      Map<DAttr,Object> affectedVals,
      Map<DAttr,Object> attributeVals, boolean saveChanges)
      throws DataSourceException, NotPossibleException, NotFoundException,
      ConstraintViolationException  // v2.7.4
  {
    final Class cls = o.getClass();

    if (attributeVals != null) {
      // invoke setters to update o with the new attribute values
      String attribName = null;
      Field f = null;
      Object newVal = null;
      //Object oldVal = null;
      DAttr attrib;

      /*v3.1: changed to map
      Collection<Method> updateDerivingValueMethods;
      Collection<Method> updateMethods = new ArrayList<Method>();
      */
      Map<DAttr,Method> updateDerivingValueMethods;
      Map<DAttr,Method> updateMethods = new HashMap();
      
      try {
        
        /* TODO: v2.7.2: check if id-attribute(s) were among the attributeVals 
        Map<DomainConstraint,Object> idChangedMap = null;
        */
        
        for (Entry<DAttr,Object> e : attributeVals.entrySet()) {
          attrib = e.getKey();
          // only update if this field is mutable
          if (attrib.mutable()) {
            newVal = e.getValue();
            attribName = attrib.name();
            f = dsm.getDomainAttribute(cls, attribName);
            
            /*v3.1: find methods together with the associated derived attributes
            // v2.6.4b: if there are update methods of deriving attributes that depend on this attribute
            // and these methods have not been recorded then record them here to be invoked later
            updateDerivingValueMethods = dsm.findAttributeValueMultiDependentUpdaterMethods(cls, attrib);
            if (updateDerivingValueMethods != null) {
              for (Method m : updateDerivingValueMethods) if (!updateMethods.contains(m)) updateMethods.add(m);
            }
            */
            updateDerivingValueMethods = dsm.findAttributeValueMultiDependentUpdaterMethods(cls, attrib);
            if (updateDerivingValueMethods != null) {
              for (Entry<DAttr,Method> em : updateDerivingValueMethods.entrySet()) {
                if (!updateMethods.containsKey(em.getKey())) updateMethods.put(em.getKey(), em.getValue());
              }
            }
            
            /* TODO: v2.7.2: support id attribute
            if (attrib.id()) {
              if (idChangedMap == null) idChangedMap = new LinkedHashMap<>();
              idChangedMap.put(attrib,newVal);
            }
            */
            
            // v2.7.2: disallow id-attribute change for now
            if (attrib.id()) {
              throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
                  "Tính năng không được hỗ trợ: {0}", "Domain id attribute update");
            }
            
            setAttributeValue(o, f, newVal);
          }
        }
        
        // v2.6.4b: if there are deriving attributes that depend on >= 2 other attributes and 
        // at least one of these attributes were among the affected attributes above
        // then invoke the update methods on those attributes here (if these were defined)
        if (!updateMethods.isEmpty()) {
          /*v3.1: add derived attribute values to mappings
           for (Method m : updateMethods) {
            m.invoke(o, null);
           }
          */
          Method m; Object oldDerivedVal, newDerivedVal;
          for (Entry<DAttr,Method> e : updateMethods.entrySet()) {
            attrib = e.getKey();
            m = e.getValue();
            oldDerivedVal = m.invoke(o);
            
            if (oldDerivedVal != null) { // TODO: should we enforce that old value is returned?
              // derived attribute value was changed: record it in attributeVals and affectedVals
              newDerivedVal = dsm.getAttributeValue(o.getClass(), o, attrib);
              attributeVals.put(attrib, newDerivedVal);
              affectedVals.put(attrib, oldDerivedVal);
            }
          }          
        }
      } catch (Exception e) {
        /* TODO: 
         * o's state may be inconsistent because some attributes were updated 
         * while others (e.g. deriving attributes) were not  
         */
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
            //"Không thể thực thi phương thức: {0}.{1}({2},{3})", 
            new Object[] {o, "updateObject", attribName + "," + newVal});
      }
    }
    
    // update the related database table(s)
    // TODO: only update the columns corresponding to the attributes listed in attributeVals AND any 
    // deriving attributes from them 
    if (saveChanges &&  // v2.6.4b: added this check 
        !isTransient(cls))
      updateObjectIn(o, cls);
    
    /* v2.7.2: TODO: update id attribute change
    if (idChangedMap != null) {
      // update the object's Oid in the object pool and in the object (if specified)
      updateObjectOid(o, idChangedMap);
    }
    */
    
    /**
     *  notify listeners
     *  TODO: for now, we only do this if the affected attributes were specified 
     *  Generally speaking, such information is needed to allow event handlers to correctly handle the object update.   
     */
    if (attributeVals != null) {
      // v2.7.2: support ObjectUpdateData
      //fireStateChanged(cls, o, LAName.Update, attributeVals.keySet());
      Object dat = null;
      if (affectedVals != null) {
        dat = new ObjectUpdateData(attributeVals.keySet(), 
           attributeVals.values(),
           affectedVals.values());
      } 
      fireStateChanged(cls, o, LAName.Update, dat);
    }
  }
  
  /**
   * 
   * @requires 
   *  attributeVals != null /\ 
   *  o is a domain object whose class contains attributes specified 
   *  in attributeVals /\ o != null 
   * 
   * @effects 
   *   for each entry <dc,value> in attributeVals
   *     update the value of the attribute in o specified by dc using value
   * 
   *   <p>if <code>isTransient(o.class) = false /\ saveChanges=true</code> then updates
   *  <code>o</code> in the database table of <code>o.class</code> and
   *  in all the tables of the super and ancester domain classes of this
   *  class.
   *  
   *  <p>
   *  Throws <tt>NotFoundException</tt> if fails to retrieve domain attributes, 
   *  <tt>NotPossibleException</tt> if fails to change attribute value, 
   *  <tt>DBException</tt> if fails to update the object in the database.
   * @version 2.6.1, 2.6.4b  
   */  
  public void updateObject(Object o, 
      Map<DAttr,Object> attributeVals, boolean saveChanges)
      throws DataSourceException, NotPossibleException, NotFoundException {
    updateObject(o, null, attributeVals, saveChanges);
  }

  /**
   * @requires 
   *  o != null /\ o is registered in object pool /\ idValMap != null
   * @effects 
   *  update <tt>o.oid</tt> in the object pool and in <tt>o</tt> (if needed) using the new  
   *  values specified in <tt>idValMap</tt>
   *  
   *  <p><b>CAUTION</b>: this method is dangerous!!!
   */
  private void updateObjectOid(Object o, Map<DAttr,Object> idValMap) {
    Class c = o.getClass();

    // the current id
    Oid currId = lookUpObjectId(c, o);
    
    for (Entry<DAttr,Object> e : idValMap.entrySet()) {
      currId.setIdAttributeValue(e.getKey(), (Comparable) e.getValue());
    }
  }
  
  /**
   * @effects updates the database table of the domain class <code>c</code> and
   *          those of the super- and ancestor classes of <code>c</code> (if
   *          any) with the attribute values of the domain object <code>o</code>
   *          , or throws <code>DBException</code> if an error occured.
   * 
   * @requires <code>c</code> is a domain class and is assignment compatible to
   *           <code>o</code>
   */
  private void updateObjectIn(Object o, Class c) throws DataSourceException {
    // first, update the super classes
    Class sup = dsm.getSuperClass(c);
    if (sup != null) {
      updateObjectIn(o, sup);
    }

    // then, update o in c
    if (osm != null)
      osm.updateObject(o, c);
  }

  /**
   * This method is used to obtain the most up-to-date
   * state of a domain object (e.g. before displaying it on an object form). 
   * This is necessary especially
   * when the object has derived attributes, whose values are computed from states of other objects
   * but changes to these objects' state have not yet been effected on the object.   
   *  
   * @requires 
   *  o != null /\ c is a domain class in this /\ o.class = c
   * @effects <pre>
   *  if can refresh state of o
   *    refresh state of o
   *    if state change should be stored 
   *      return true
   *    else
   *      return false  
   *  else
   *    do nothing
   *    
   *  <br>Throws NotImplementedException if c does not support a method for refreshing state of its objects.
   *  </pre>
   *  
   * @version 
   * - 2.7.4 <br>
   * - 3.2: improved to return boolean to indicate whether state change should be saved
   */  
  public boolean refreshObjectState(Object o, Class c) throws NotImplementedException {
    Method m = dsm.findRefreshStateMethod(c);
    
    if (m != null) {
      // can refresh via a method
      try {
        /* v3.2: support state change update flag returned from invocation
         m.invoke(o, null);
         */
        Object result = m.invoke(o, null);
        if (result != null && (result instanceof Boolean)) {
          return ((Boolean) result);
        } else {
          return false;
        }
      } catch (Exception e) {
        // ignore
        // v3.2: not interested
        return false;
      }
    } else {
      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, new Object[] {c.getSimpleName(), "refreshState"});
    }
  }
  

  
  /**
   * @effects if exists in the memory extent of a domain class <code>c</code> an
   *          object the <tt>Oid</tt> of which is equal to
   *          <code>objId</code> then return it, else returns <code>null</code>
   * 
   * @requires <code>c != null && objId != null && c</code> is a domain class
   *           <code> && c</code> is in <code>this</code>
   */
  public <T> T lookUpObject(Class<T> c, Oid objId) {

    // first look up using c
    Map<Oid,Object> objects = classExts.get(c);

    if (objects != null) {
      Oid oid;
      T o;
      for (Entry<Oid,Object> entry: objects.entrySet()) {
        // extract the id value of each object
        oid = entry.getKey();
        o = (T) entry.getValue();
        if (oid.equals(objId)) {
          return o;
        }
      }
    }

    // if we get here then no objects found matching c, perhaps it is abtract
    // try the sub-classes
    T o;
    Class[] subClasses = dsm.getSubClasses(c);
    if (subClasses != null) {
      for (Class sub : subClasses) {
        o = (T) lookUpObject(sub, objId);
        if (o != null) {
          return o;
        }
      }
    }

    return null;
  }
  
  /**
   * If only the <tt>Oid</tt> is needed then use {@link #lookUpObjectId(Class, DAttr[], Object[])} instead. 
   * 
   * @requires 
   *  c is a registered domain class /\ 
   *  idAttribs is a non-empty array of id-attributes of c /\ 
   *  idVals.length = idAttribs.length
   *  
   * @effects 
   *  look up and return a pair <tt>Entry<Oid,Object)</tt> of the object of 
   *  <tt>c</tt> matching the specified id attribute(s) and value(s); 
   *  or return <tt>null</tt> if no such object is found.
   */
  public Entry<Oid,Object> lookUpObject(Class c, DAttr[] idAttribs, 
      Object[] idVals) {
    Collection<Entry<Oid,Object>> objs = classExts.get(c).entrySet();
    
    Oid oid;
    for (Entry<Oid,Object> e : objs) {
      oid = e.getKey();
      if (oid.equals(idAttribs, idVals)) {
        // match
        return e;
      }
    }

    // if we get here then no objects found matching c, perhaps it is abtract
    // try the sub-classes
    Entry<Oid,Object> e;
    Class[] subClasses = dsm.getSubClasses(c);
    if (subClasses != null) {
      for (Class sub : subClasses) {
        e = lookUpObject(sub, idAttribs, idVals);
        if (e != null) {
          return e;
        }
      }
    }

    return null;
  }

  /**
   * @effects if exists in the memory extent of a domain class <code>c</code> an
   *          object the value of whose id attribute is equal to
   *          <code>idVal</code> then return it, else return <code>null</code>
   * 
   * @requires <code>c != null && idVal != null && c</code> is a domain class
   *           <code> && c</code> is in <code>this</code>
   */
  public Object lookUpObjectByID(Class c, Object idVal) {
    // TODO: improve this by not using Object[] array argument
    return lookUpObjectByID(c, new Object[] {idVal});
  }

  /**
   * @effects if exists in the memory extent of a domain class <code>c</code> an
   *          object the values of whose id attributes equal to
   *          <code>idVals</code> then return it, else returns <code>null</code>
   * 
   * @requires <code>c != null && idVals != null && c</code> is a domain class
   *           <code> && c</code> is in <code>this</code>
   */
  public Tuple2<Oid,Object> lookUpObjectById(Class c, Object[] idVals) {
    // first look up using c
    Map<Oid,Object> objects = classExts.get(c);//List objects = classExts.get(c);

    if (objects != null) {
      Oid oid;
      Object o;
      for (Entry<Oid,Object> entry : objects.entrySet()) {
        oid = entry.getKey();
        o = entry.getValue();
        //v2.7.2: if (Arrays.equals(oid.getIdValues(), idVals)) {
        if (oid.equals(idVals)) {
          return new Tuple2<Oid,Object>(oid,o);
        }
      }
    }

    // if we get here then no objects found matching c, perhaps it is abtract
    // try the sub-classes
    Tuple2<Oid,Object> t;
    Class[] subClasses = dsm.getSubClasses(c);
    if (subClasses != null) {
      for (Class sub : subClasses) {
        t = lookUpObjectById(sub, idVals);
        if (t != null) {
          return t;
        }
      }
    }

    return null;
  }  

  /**
   * @effects if exists in the memory extent of a domain class <code>c</code> an
   *          object the values of whose id attributes equal to
   *          <code>idVals</code> then return it, else returns <code>null</code>
   * 
   * @requires <code>c != null && idVals != null && c</code> is a domain class
   *           <code> && c</code> is in <code>this</code>
   */
  public Object lookUpObjectByID(Class c, Object[] idVals) {
    // TODO is there a way of checking if c is abstract

    // first look up using c
    Map<Oid,Object> objects = classExts.get(c);//List objects = classExts.get(c);

    if (objects != null) {
      Oid oid;
      Object o;
      for (Entry<Oid,Object> entry : objects.entrySet()) {
        oid = entry.getKey();
        o = entry.getValue();
        // v2.7.2: if (Arrays.equals(oid.getIdValues(), idVals)) {
        if (oid.equals(idVals)) {
          return o;
        }
      }
    }

    // if we get here then no objects found matching c, perhaps it is abtract
    // try the sub-classes
    Object o;
    Class[] subClasses = dsm.getSubClasses(c);
    if (subClasses != null) {
      for (Class sub : subClasses) {
        o = lookUpObjectByID(sub, idVals);
        if (o != null) {
          return o;
        }
      }
    }

    return null;
  }  

  /**
   * @requires 
   *  objects != null
   * @effects 
   *  if objects != null
   *    find object o in objects whose id attribute values match <tt>idVals</tt>
   *    if found
   *      return o
   *    else
   *      return null
   *  else
   *    return null
   */
  public Object lookUpObjectByID(List objects, Object[] idVals) {
    if (objects != null) {
      Object[] idvs;
      for (Object o : objects) {
        // extract the id value of each object
        try {
          idvs = dsm.getIDAttributeValues(o);
          if (Arrays.equals(idvs, idVals)) {
            return o;
          }
        } catch (NotPossibleException e) {
          // something wrong, but ignore
        }
      }
    }
    
    return null;
  }
  
  /**
   * @effects if exists a domain object <code>o</code> in <code>this</code>
   *          whose record-map key is of the form
   *          <code>tableName(rids[0],rids[1],...)</code>, where
   *          <code>tableName</code> is the name of the database table of the
   *          domain class <code>c</code> and <code>rids[]</code> are the PK
   *          values of the table record from which <code>o</code> was created
   *          then returns <code>o</code>, else returns <code>null</code>.
   * 
   *          <p>
   *          Note: Use this method only when <code>c</code> has an
   *          auto-generated id attribute, i.e.
   *          <code>DomainConstraint.auto=true</code> for this attribute.
   * @see #putObjectByRecID(Class, Object, Object[])
   */
  public Object lookUpObjectByRecID(Class c, Object[] rIds) {
    String tableName = dsm.getDomainClassName(c);
    StringBuffer key = new StringBuffer(tableName);
    key.append("(");
    for (int i = 0; i < rIds.length; i++) {
      Object idVal = rIds[i];
      key.append(idVal);
      if (i < rIds.length - 1)
        key.append(",");
    }
    key.append(")");

    Object o = recToObjectMap.get(key.toString());
    if (o == null) {
      // perhaps c is abstract, try its sub-classes
      Class[] subs1 = dsm.getSubClasses(c);
      if (subs1 != null) {
        for (Class sub : subs1) {
          o = lookUpObjectByRecID(sub, rIds);
          if (o != null) {
            return o;
          }
        }
      }
    }

    return o;
  }

  /**
   * @effects puts a domain object <code>o</code> in a record-to-object map of
   *          <code>this</code>, the key of which is
   *          <code>tableName(rids[0],...)</code>, where <code>tableName</code>
   *          is the name of the database table of <code>c</code> and
   *          <code>rids</code> are the PK values of the table record from which
   *          <code>o</code> was created.
   * 
   *          p> Note: Use this method only when <code>c</code> has an
   *          auto-generated id attribute, i.e.
   *          <code>DomainConstraint.auto=true</code> for this attribute.
   * 
   * @see #lookUpObjectByRecID(Class, Object[])
   */
  public void putObjectByRecID(Class c, Object o, Object[] rids) {
    String tableName = dsm.getDomainClassName(c);
    StringBuffer key = new StringBuffer(tableName);
    key.append("(");
    for (int i = 0; i < rids.length; i++) {
      Object idVal = rids[i];
      key.append(idVal);
      if (i < rids.length - 1)
        key.append(",");
    }
    key.append(")");

    // if (debug)
    // System.out.println("(recID,obj) = (" + key + "," + o +")");

    recToObjectMap.put(key.toString(), o);
  }

  /**
   * @effects 
   *  if exists in the data store of <code>cls</code> an object the value of 
   *  whose default id attribute equals <code>id</code>
   *    return it
   *  else
   *    return null
   * @version 5.4
   * @throws DataSourceException 
   * @throws NotFoundException 
   * 
   */
  public <T> T retrieveObjectByDefaultId(Class<T> cls, Object id) throws NotFoundException, DataSourceException {
    Collection<T> objs = retrieveObjects(cls, 
        DCSLConstants.ATTRIB_ID_DEFAULT_NAME, Op.EQ, id);
    
    if (objs != null) {
      return objs.iterator().next();
    } else {
      return null;
    }
  }
  
  
  /**
   * This method is a short-cut for {@link #retrieveObjects(Class, Query, ObjectComparator)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attribName</tt> is the name of a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute named <tt>attribName</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return them
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attribName</tt> does not match any of the domain attributes of <tt>c</tt> 
   */
  public <T> Collection<T> retrieveObjects(Class<T> c, String attribName, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Map<Oid,T> objects= retrieveObjects(c, q, 
        null  // v3.0
        );
    
    if (objects != null){
      return (Collection<T>) objects.values();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  call {@link #retrieveObjects(Class, Query, ObjectComparator)} with <tt>(c, null, null)</tt>
   *  
   * @version 3.0
   */
  public <T> Map<Oid, T> retrieveObjects(Class<T> c) throws NotPossibleException, DataSourceException {
    Query q = null;
    ObjectComparator comparator = null;
    return retrieveObjects(c, q, comparator);
  }
  
  /**
   * This is a short-cut for {@link #retrieveObjects(Class, Query, ObjectComparator)} with the second arg
   * set to <tt>null</tt>.
   * 
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of <b>all</b> objects of the domain class 
   *  <tt>c</tt> that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>throws NotPossibleException if object id values are invalid;
   *  DataSourceException if fails to read object ids from the data source.  
   *   
   * @version 3.0
   */
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, ObjectComparator comparator) throws NotPossibleException, DataSourceException {
    Query q = null;
    return retrieveObjects(c, q, 
        comparator  // v3.0
        );
  }

  /**
   * @effects 
   *  call {@link #retrieveObjects(Class, Query, ObjectComparator)} with <tt>(c, q, null)</tt>
   * @version 3.0
   */
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, Query q) throws DataSourceException, NotPossibleException {
    ObjectComparator comparator = null;
    return retrieveObjects(c, q, comparator);
  }
  

  /**
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of objects of the domain class 
   *  <tt>c</tt> that satisfy query <tt>q</tt> (if it is specified) AND that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>If <tt>q = null</tt> then return all domain objects of <tt>c</tt>.
   *  
   *  <p>if <tt>comparator != null</tt> then return objects sorted by the comparator
   *   
   *  <p>throws NotPossibleException if failed to generate data source query;
   *  DataSourceException if fails to read from the data source. 
   *  
   * @version 
   * - 3.0: support sorting via comparator
   */
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, Query q, ObjectComparator comparator) throws DataSourceException, NotPossibleException {
    Collection<Oid> oids = osm.readObjectIds(c, q);
    
    if (oids != null) {
      // v3.3: use shared method
      return retrieveObjects(c, oids, comparator);
//      // v3.0: support sorting: if comparator is specified then sort objects first, then add them to objMap
//      ObjectMapSorter sorter = null;
//      if (comparator != null)
//        sorter = new ObjectMapSorter(comparator);
//      
//      LinkedHashMap<Oid,T> m = new LinkedHashMap<>();
//      
//      T o;
//      for (Oid id : oids) {
//        // check in object pool first
//        o = lookUpObject(c, id);
//        if (o == null) {
//          o = loadObject(c, id, null);
//        }
//        
//        // update result
//        /*v3.0: support sorting
//        m.put(id, o);   
//        */
//        if (sorter != null)
//          sorter.put(id,o);
//        else
//          m.put(id, o);
//      }
//      
//      /*v3.0: support sorting */
//      if (sorter != null) {
//        sorter.copyTo(m);
//      }
//      
//      return m;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of objects of the domain class 
   *  <tt>c</tt> that satisfy query <tt>q</tt> (if it is specified) AND that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>If <tt>q = null</tt> then return all domain objects of <tt>c</tt>.
   *  
   *  <p>if <tt>orderBy != null</tt> then order the result objects by the id-values of the objects of the <tt>orderBy</tt> class(es) 
   *  
   *  <p>throws NotPossibleException if failed to generate data source query;
   *  DataSourceException if fails to read from the data source. 
   *  
   * @version 3.3
   */
  public <T> Map<Oid, T> retrieveObjectsWithOrderBy(Class<T> c, Query q, Class...orderBy) throws DataSourceException, NotPossibleException {
    Collection<Oid> oids = osm.readObjectIds(c, q, orderBy);
    
    if (oids != null) {
      ObjectComparator comparator = null;
      return retrieveObjects(c, oids, comparator);
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of objects of the domain class 
   *  <tt>c</tt> that satisfy query <tt>q</tt> (if it is specified) AND that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>If <tt>q = null</tt> then return all domain objects of <tt>c</tt>.
   *  
   *  <p>if <tt>orderBy != null</tt> then order the result objects by the id-values of the objects of the <tt>orderBy</tt> class 
   *  
   *  <p>if <tt>comparator != null</tt> then return objects sorted by the comparator
   *   
   *  <p>throws NotPossibleException if failed to generate data source query;
   *  DataSourceException if fails to read from the data source. 
   *  
   * @version 3.3
   * 
   * @note: can support multile order-by classes (see {@link #retrieveObjectsWithOrderBy(Class, Query, Class...)})
   * 
   */
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, Query q, Class orderBy, ObjectComparator comparator) throws DataSourceException, NotPossibleException {
    Collection<Oid> oids = osm.readObjectIds(c, q, orderBy);
    
    if (oids != null) {
      return retrieveObjects(c, oids, comparator);
    } else {
      return null;
    }
  }
  
  /**
   * This method differs from {@link #retrieveObjects(Class, Query)} in that it does not consider the sub-type objects of <tt>c</tt> (if any).
   * This is particularly used in cases where query <tt>q</tt> is complex and does not concern information of the sub-types.  
   * 
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of objects of the domain class 
   *  <tt>c</tt> that satisfy query <tt>q</tt> (if it is specified) AND that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>If <tt>q = null</tt> then return all domain objects of <tt>c</tt>.
   *  
   *  <p>throws NotPossibleException if object id values are invalid;
   *  DataSourceException if fails to read object ids from the data source.   
   * @version 3.3
   */
  public <T> Map<Oid, T> retrieveObjectsWoutSubTypes(Class<T> c, Query q) throws DataSourceException, NotPossibleException {
    ObjectComparator comparator = null;
    return retrieveObjectsWoutSubTypes(c, q, comparator);
  }
  
  /**
   * This method differs from {@link #retrieveObjects(Class, Query, ObjectComparator)} in that it does not consider the sub-type objects of <tt>c</tt> (if any).
   * This is particularly used in cases where query <tt>q</tt> is complex and does not concern information of the sub-types.  
   * 
   * @effects 
   *  load (from data source <b>if necessary</b>) and return a Map<Oid,Object> of objects of the domain class 
   *  <tt>c</tt> that satisfy query <tt>q</tt> (if it is specified) AND that are not already contained in <tt>c</tt>'s object pool; 
   *  or return <tt>null</tt> if no such objects are found.
   *  
   *  <p>If <tt>q = null</tt> then return all domain objects of <tt>c</tt>.
   *  
   *  <p>if <tt>comparator != null</tt> then return objects sorted by the comparator 
   *  
   *  <p>throws NotPossibleException if object id values are invalid;
   *  DataSourceException if fails to read object ids from the data source.   
   *
   * @version 3.3
   */
  public <T> Map<Oid, T> retrieveObjectsWoutSubTypes(Class<T> c, Query q, ObjectComparator comparator) throws DataSourceException, NotPossibleException {
    Collection<Oid> oids = osm.readObjectIdsWoutSubtypes(c, q);
    
    if (oids != null) {
      return retrieveObjects(c, oids, comparator);
    } else {
      return null;
    }
  }
  
  /**
   * @requires 
   *  c is a registered domain class /\ size(oids) > 0
   * @effects 
   *  retrieve (from the data source <b>if necessary</b>) the domain objects of <tt>c</tt> whose ids are <tt>oids</tt>
   *  and return them in a Map (whose keys are the ids).
   *  
   *  <p>If <tt>comparator</tt> is specified then 
   *      objects are sorted by value as defined by <tt>comparator</tt>;
   *    else objects are placed in the same order the ids in <tt>oids</tt> 
   *  
   *  <p>Throws NotFoundException if object is not found; DBException if fails to load object(s) from data source.
   * @version 
   *  -3.0: improved to support sorting 
   */
  // v3.0: public <T> Map<Oid, T> retrieveObjects(Class<T> c, Collection<Oid> oids) throws DataSourceException, NotFoundException {
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, Collection<Oid> oids, ObjectComparator comparator) 
      throws DataSourceException, NotFoundException {
    LinkedHashMap<Oid,T> objMap = new LinkedHashMap<>();
    T o;
    
    // v3.0: support sorting: if comparator is specified then sort objects first, then add them to objMap
    ObjectMapSorter sorter = null;
    if (comparator != null)
      sorter = new ObjectMapSorter(comparator);
      
    
    //v3.3: no need 
    // boolean objectSerialised = isObjectSerialised();  // v2.8
    
    for (Oid id : oids) {
      // v2.7.3: check in object pool first
      o = lookUpObject(c, id);
      if (o == null) {
        //if (objectSerialised)
        o = loadObject(c, id);
        //else
        //  o = retrieveObject(c, id);
      }
      
      /*v3.0: support sorting
      objMap.put(id, o);
      */
      if (sorter != null)
        sorter.put(id,o);
      else
        objMap.put(id, o);
    }
    
    /*v3.0: support sorting */
    if (sorter != null) {
      sorter.copyTo(objMap);
    }
    
    return objMap;
  }

  /**
   * Loads the domain objects that are associated to the objects of a domain
   * class <tt>c</tt>.
   * 
   * <p>
   * Example:
   * 
   * <pre>
   *  c = Customer.class
   *  attribute Customer.enrolments is List<Enrolment>
   *  then load all Enrolment objects (if not already done so) 
   *  and for each Customer object o
   *    enrols = List<Enrolment> containing all enrolments of o
   *    set o.enrolments = enrols
   * </pre>
   * 
   * @effects <pre>
   *   if there are no objects of c
   *    do nothing
   *   else
   *    for each domain object <tt>o</tt> of the domain class <tt>c</tt> 
   *      for each 1:M association a(c,d), where d is some domain class 
   *        load objects of d (if not done so) that participate in the association a
   *        set the value of the attribute of c in o that implements a to those objects of d
   *        that are linked to o via a
   *   </pre>
   * 
   * @requires <tt>c</tt> is a domain class registered in <tt>this</tt>
   */
  public void retrieveAssociatedObjects(Class c) throws NotFoundException, NotPossibleException {
    Collection objects = getObjects(c);

    if (objects == null || objects.isEmpty())
      return;

    // get the 1:M associations 
    final AssocType type = AssocType.One2Many;
    Map<DAttr,DAssoc> manyAssocs = dsm.getAssociations(c, type, AssocEndType.One);
    
    if (manyAssocs == null)
      return;

    DAssoc assoc;
    DAttr dc;
    String attribute, targetAttribute;
    int linkCount;
    Class assocClass; 
    String assocName;
    Tuple2<DAttr,DAssoc> tuple;
    
    Query q;
    final Op eq = Op.EQ;
    Collection refObjects;

    for (Object o : objects) {
      for (Entry<DAttr,DAssoc> entry : manyAssocs.entrySet()) {
        dc = entry.getKey();
        assoc = entry.getValue();
        
        attribute = dc.name();
        /**v2.5.4: only proceed if link attribute has not been loaded */
        linkCount = getAssociationLinkCountFromPool(c, attribute, o);
        
        //attributeVal = (Collection) getAttributeValue(o, attribute);
        //if (attributeVal != null && !attributeVal.isEmpty())
        if (linkCount > 0)
          continue; // skip

        // find the target link attribute
        assocClass = assoc.associate().type();
        tuple = dsm.getTargetAssociation(assoc);
        targetAttribute = tuple.getFirst().name();
        q = new Query();
        q.add(new Expression(targetAttribute, eq, o));
        try {
          refObjects = retrieveObjectsWithAssociations(assocClass, q);
          if (refObjects != null) {
            updateAssociateLink(o, attribute, refObjects);
          }
        } catch (DataSourceException e) {
          // something wrong, ignore
        }
      } // end inner for
    } // end outer for
  }
  
  /**
   * Loads the domain objects that are referred to by the specified domain object 
   * through its 1:M associations.
   * 
   * <p>Example: 
   * <pre>
   *  c = Customer.class
   *  attribute Customer.enrolments is List<Enrolment>
   *  given a Customer object o
   *    enrols = List<Enrolment> containing all enrolments of o
   *    o.enrolments = enrols 
   * </pre>
   * 
   * <p>The way that this method load objects is similar to {@link #retrieveAssociatedObjects(Class)}.
   * 
   * @effects <pre>
   *  let c = o.class
   *  for each 1:M association a(c,d), where d is some domain class 
   *    load objects of d (if not done so) that participate in the association a
   *    set the value of the attribute of c in o that implements a to those objects 
   * </pre>
   * 
   * @requires <code>o.class</code> is a domain class registered in <code>this</code>
   *  which has at least one collection-type attributes.
   */
  public void retrieveAssociatedObjects(final Object o) throws NotFoundException, NotPossibleException {
    // get the 1:M associations
    
    final AssocType type = AssocType.One2Many;
    final Class c = o.getClass();
    
    Map<DAttr,DAssoc> manyAssocs = dsm.getAssociations(c, type, AssocEndType.One);
    
    if (manyAssocs == null)
      return;

    DAssoc assoc;
    DAttr dc, targetAttrib;
    String attribute, targetAttributeName;
    int linkCount;
    Class assocClass; 
    String assocName;
    Tuple2<DAttr,DAssoc> tuple;
    
    Query q;
    final Op eq = Op.EQ;
    Collection refObjects;

    for (Entry<DAttr, DAssoc> entry : manyAssocs.entrySet()) {
      dc = entry.getKey();
      assoc = entry.getValue();

      attribute = dc.name();
      /*v3.0: support non-serialisable and virtual associations  
      // v2.5.4: only proceed if link attribute has not been loaded 
      linkCount = getAssociationLinkCountFromPool(c, attribute, o);

      if (linkCount > 0)
        // loaded -> skip
        continue;

      // find the target link attribute
      assocClass = assoc.associate().type();
      tuple = dsm.getTargetAssociation(assoc);
      */
      try {
        tuple = dsm.getTargetAssociation(assoc);
        
        targetAttrib = tuple.getFirst();
        
        if (targetAttrib.serialisable()==false) {
          // non-serialisable association -> skip
          continue;
        }
      } catch (NotFoundException e) {
        // no target association (i.e. virtual association) -> skip 
        continue ;
      }
      
      targetAttributeName = targetAttrib.name();

      // v2.5.4: only proceed if link attribute has not been loaded 
      linkCount = getAssociationLinkCountFromPool(c, attribute, o);

      if (linkCount > 0)
        // loaded -> skip
        continue;

      // find the target link attribute
      assocClass = assoc.associate().type();

      q = new Query();
      q.add(new Expression(targetAttributeName, eq, o));
      try {
        refObjects = retrieveObjectsWithAssociations(assocClass, q);
        if (refObjects != null) {
          updateAssociateLink(o, attribute, refObjects);
        }
      } catch (DataSourceException e) {
        // something wrong, ignore
      }
    } // end inner for
  }
  
  /**
   * This is similar to {@link #retrieveAssociatedObjects(Object, Class, Class, String)} except that it retrieves only   
   * <b>the first</b> associated object.
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls
   *   
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> 
   *    load (from the data source, if necessary) <b>the first</b> of such objects 
   *    add association link from linkedObj to this object
   *    
   *  <br>return <b>the first</b> object or return null if no such objects exist</pre>
   * @version 
   *  - 3.4
   */
  public <T> T retrieveAssociatedObject(Object linkedObj, Class linkedObjCls, Class<T> c, String assocName) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);
    
    if (assocTuple != null) {
      //TODO: improve to read the first id
      Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj 
          //, exps
      );
      
      if (oids != null) {
        Oid first = oids.iterator().next();
        return retrieveObject(first, c, assocTuple, linkedObj);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * This is a short-cut for {@link #retrieveObjects(Class, Tuple2, Object, Class)}
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls
   *   
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> 
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   *  - 3.2
   */
  public <T> Map<Oid,T> retrieveAssociatedObjects(Object linkedObj, Class linkedObjCls, Class<T> c, String assocName) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);
    
    if (assocTuple != null) {
      Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj 
          //, exps
      );
      
      return retrieveObjects(oids, c, assocTuple, linkedObj, 
          //v3.2: comparator
          null);
    } else {
      return null;
    }
  }
  

  /**
   * An extended version of {@link #retrieveAssociatedObjects(Object, Class, Class, String)} that supports {@link ObjectComparator} for 
   * sorting the result.
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls /\ 
   *  sorter != null -> sorter.attrib belongs to <tt>c</tt>
   *  
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> 
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   *  - 3.2
   */
  public <T> Map<Oid,T> retrieveAssociatedObjects(Object linkedObj, Class linkedObjCls, Class<T> c, String assocName, 
      ObjectComparator comparator, Expression...exps) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);
    
    if (assocTuple != null) {
      Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj, exps);

      return retrieveObjects(oids, c, assocTuple, linkedObj, comparator);
    } else {
      return null;
    }
  }
  

  /**
   * This method is to retrieve <b>associated</b> objects.
   * 
   * <p>It is a short-cut for {@link #retrieveObjects(Class, Tuple2, Object, Class)}.
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls /\
   *  <tt>query</tt> is defined over <tt>c</tt>
   *   
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> and, if <tt>query != null</tt>, then also satisfy <tt>query</tt> 
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   *  - 3.1
   */
  public <T> Map<Oid,T> retrieveObjects(Class<T> c, String assocName,
      Object linkedObj, Class linkedObjCls, Query query) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);
    
    if (assocTuple != null) {
      //return retrieveObjects(c, assocTuple, linkedObj, linkedObjCls, query);
      Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj, query);
      
      return retrieveObjects(oids, c,  assocTuple, linkedObj,
          //v3.2: comparator
          null
          );
    } else {
      return null;
    }
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assoc != null /\ assoc defines c's end of an association with linkedObjCls /\ 
   *  {@link #isObjectSerialised()} = true /\ 
   *  <tt>query</tt> is defined over <tt>c</tt>
   *  
   *   
   * @effects <pre>
   *  if exists domain objects of c that satisfy <tt>query</tt> (if it is specified) and are associated 
   *  to linkedObj via the association defined by assocTuple 
   *  (i.e. if association=(c.a,linkedObjCls.b) then o satisfies if equals(o.a,linkedObj)) 
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   * - 3.1 <br>
   * - 3.2: use linkedObj instead of linkedObjId 
   * @deprecated as of v3.2
   */
  public <T> Map<Oid,T> retrieveObjects(Class<T> c, Tuple2<DAttr,DAssoc> 
    assocTuple, Object linkedObj, Class linkedObjCls, Query query) throws DataSourceException {
    
    /* v3.2: use linkedObj instead of its Oid
    Oid linkedObjId = lookUpObjectId(linkedObjCls, linkedObj);
    
    // load object ids
    Collection <Oid> oids = osm.readObjectIds(c, assocTuple, linkedObjId, query);
    */
    
    // load object ids
    Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj, query);
    
    /*v3.2: moved to shared method 
    // the domain attribute of c's end of the association
    Association assoc = assocTuple.getSecond();
    Tuple2<DomainConstraint,Association> yourAssoc = dsm.getTargetAssociation(assoc);
    DomainConstraint linkedAttrib = (yourAssoc != null) ? yourAssoc.getFirst() : null;
    
    if (oids != null) {
      Map<Oid,T> m = new LinkedHashMap<>();
      T o;
      for (Oid id : oids) {
        // check in object pool first
        o = lookUpObject(c, id);
        if (o == null) {
          o = loadObject(c, id, null);
          
          // if association is defined on linkedObjCls then update linkedObj to add association link to o
          if (linkedAttrib != null) {
            updateAssociateLink(linkedObj, linkedAttrib, o);
          }
        }
        
        // update result
        m.put(id, o);        
      }
      
      return m;
    } else {
      return null;
    } */
    
    return retrieveObjects(oids, c,  assocTuple, linkedObj,
        //v3.2: comparator
        null
        );
  }
  
  /**
   * This is a short-cut for {@link #retrieveObjects(Class, Tuple2, Object, Class)}.
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls /\
   *  <tt>exps</tt> are defined over <tt>c</tt>
   *   
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> and, if <tt>exps.length > 0</tt>, then also satisfy <tt>exps</tt> 
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   *  - 2.7.4
   *  <br>-3.0: support <tt>exps</tt>
   *  <br>-3.2: remove deprecation
   * @deprecated as of v3.2, use {@link #retrieveAssociatedObjects(Object, Class, Class, String)} instead
   */
  public <T> Map<Oid,T> retrieveObjects(Class<T> c, String assocName,
      Object linkedObj, Class linkedObjCls, Expression...exps) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);
    
    if (assocTuple != null) {
      return retrieveObjects(c, assocTuple, linkedObj, linkedObjCls, exps);
    } else {
      return null;
    }
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assoc != null /\ assoc defines c's end of an association with linkedObjCls /\ 
   *  {@link #isObjectSerialised()} = true /\ 
   *  <tt>exps</tt> are defined over <tt>c</tt>
   *  
   *   
   * @effects <pre>
   *  if exists domain objects of c that are associated 
   *  to linkedObj via the association defined by assocTuple 
   *  (i.e. if association=(c.a,linkedObjCls.b) then o satisfies if equals(o.a,linkedObj)) 
   *  and, if <tt>exps.length > 0</tt>, then also satisfy <tt>exps</tt>  
   *    load (from the data source, if necessary) these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the loaded objects as Map or return null if no such objects exist</pre>
   * @version 
   * - 2.7.4 updated
   * <br>- 3.0: support <tt>exps</tt>
   * @deprecated as of v3.2, use {@link #retrieveAssociatedObjects(Object, Class, Class, String, ObjectComparator, Expression...)} instead.
   */
  public <T> Map<Oid,T> retrieveObjects(Class<T> c, Tuple2<DAttr,DAssoc> 
    assocTuple, Object linkedObj, Class linkedObjCls, Expression...exps) throws DataSourceException {
    
    /* v3.2: use linkedObj instead of its Oid
    Oid linkedObjId = lookUpObjectId(linkedObjCls, linkedObj);
    
    // load object ids
    Collection <Oid> oids = osm.readObjectIds(c, assocTuple, linkedObjId, exps);
    */
    // load object ids
    Collection <Oid> oids = osm.readLinkedObjectIds(c, assocTuple, linkedObj, exps);
    
    /*v3.2: moved to shared method
    // the domain attribute of c's end of the association
    Association assoc = assocTuple.getSecond();
    Tuple2<DomainConstraint,Association> yourAssoc = dsm.getTargetAssociation(assoc);
    DomainConstraint linkedAttrib = (yourAssoc != null) ? yourAssoc.getFirst() : null;
    
    if (oids != null) {
      Map<Oid,T> m = new LinkedHashMap<>();
      T o;
      for (Oid id : oids) {
        // check in object pool first
        o = lookUpObject(c, id);
        if (o == null) {
          o = loadObject(c, id, null);
          
          // if association is defined on linkedObjCls then update linkedObj to add association link to o
          if (linkedAttrib != null) {
            updateAssociateLink(linkedObj, linkedAttrib, o);
          }
        }
        
        // update result
        m.put(id, o);        
      }
      
      return m;
    } else {
      return null;
    }
    */
    
    return retrieveObjects(oids, c, assocTuple, linkedObj, 
        //v3.2: comparator
        null);
  }

  /**
   * @effects 
   *  retrieve (from data source if needed) objects of <tt>c</tt> that are linked to <tt>linkedObj</tt> via 
   *  association <tt>assocTuple</tt>; add association links from <tt>linkedObj</tt> to those objects.
   *  
   *  <p>if <tt>comparator</tt> is specified then sort the retrieved objects using it.
   *   
   * @version 3.2 
   */
  private <T> Map<Oid,T> retrieveObjects(Collection<Oid> oids, Class<T> c, Tuple2<DAttr,DAssoc> 
    assocTuple, Object linkedObj, ObjectComparator comparator) throws NotFoundException, NotPossibleException, DataSourceException  {

    // the domain attribute of c's end of the association
    DAssoc assoc = assocTuple.getSecond();
    Tuple2<DAttr,DAssoc> yourAssoc = dsm.getTargetAssociation(assoc);
    DAttr linkedAttrib = (yourAssoc != null) ? yourAssoc.getFirst() : null;
    
    if (oids != null) {
      // support sorting: if comparator is specified then sort objects first, then add them to objMap
      ObjectMapSorter sorter = null;
      if (comparator != null)
        sorter = new ObjectMapSorter(comparator);
      
      LinkedHashMap<Oid,T> m = new LinkedHashMap<>();
      T o;
      for (Oid id : oids) {
        // check in object pool first
        o = lookUpObject(c, id);
        if (o == null) {
          o = loadObject(c, id, null);
          
          /*moved this out of if block, because o might have been retrieved by another process (not related to this)
          // if association is defined on linkedObjCls then update linkedObj to add association link to o
          if (linkedAttrib != null) {
            updateAssociateLink(linkedObj, linkedAttrib, o);
          }
          */
        }

        // if association is defined on linkedObjCls then update linkedObj to add association link to o
        if (linkedAttrib != null) {
          updateAssociateLink(linkedObj, linkedAttrib, o);
        }

        // update result
        // m.put(id, o);
        if (sorter != null)
          sorter.put(id,o);
        else
          m.put(id, o);
      }
      
      if (sorter != null) {
        sorter.copyTo(m);
      }
      
      return m;
    } else {
      return null;
    }
  }
  
  /**
   * This is a single-object version of {@link #retrieveObjects(Collection, Class, Tuple2, Object, ObjectComparator)}.
   * @effects 
   *  retrieve (from data source if needed) the object of <tt>c</tt> whose id is <tt>id</tt> that is linked to <tt>linkedObj</tt> via 
   *  association <tt>assocTuple</tt>; add association link from <tt>linkedObj</tt> to this object.
   *   
   * @version 3.4 
   */
  private <T> T retrieveObject(Oid id, Class<T> c, Tuple2<DAttr,DAssoc> 
    assocTuple, Object linkedObj) throws NotFoundException, NotPossibleException, DataSourceException  {

    // the domain attribute of c's end of the association
    DAssoc assoc = assocTuple.getSecond();
    Tuple2<DAttr,DAssoc> yourAssoc = dsm.getTargetAssociation(assoc);
    DAttr linkedAttrib = (yourAssoc != null) ? yourAssoc.getFirst() : null;
    
    // check in object pool first
    T o = lookUpObject(c, id);
    if (o == null) {
      o = loadObject(c, id, null);
      
      /*moved this out of if block, because o might have been retrieved by another process (not related to this)
      // if association is defined on linkedObjCls then update linkedObj to add association link to o
      if (linkedAttrib != null) {
        updateAssociateLink(linkedObj, linkedAttrib, o);
      }
      */
    }

    // if association is defined on linkedObjCls then update linkedObj to add association link to o
    if (linkedAttrib != null) {
      updateAssociateLink(linkedObj, linkedAttrib, o);
    }
    
    return o;
  }
  
//  /**
//   * Loads the domain objects that are referred to by the specified domain object via 
//   * a given 1:M association 
//   * 
//   * @requires <code>myCls</code> is a domain class registered in <code>this</code> /\ 
//   * myRefAttrib is a domain attribute of <tt>myCls</tt>  
//   * 
//   * @effects <pre>
//   *  let c = myCls, d = youCls
//   *  let a(c,myRefAttrib,d,yourRefAttrib) be the 1-M association between c and d (d is at the MANY end)
//   *  load objects of d (if not done so) that participate in the association a
//   *  update the association links from those objects to o  
//   * </pre>
//   * 
//   * @example 
//   * <pre>
//   *  c = Customer.class
//   *  attribute Customer.enrolments is List<Enrolment>
//   *  given a Customer object o
//   *    enrols = List<Enrolment> containing all enrolments of o
//   *    o.enrolments = enrols 
//   * </pre>
//   * 
//   * <p>The way that this method load objects is similar to {@link #retrieveAssociateOid(Class, Oid, DomainConstraint, Class)}.
//   * 
//   *  @version 3.0
//   */
//  public <T> Collection<T> retrieveAssociatedObjects(Class myCls, Object o,
//      String myRefAttrib, Class<T> youCls
//      ) throws NotFoundException, NotPossibleException {
//    
//    return retrieveAssociatedObjects(myCls, o, myRefAttrib, youCls, null, null, null);
//  }
//  
//  /**
//   * Loads the domain objects that are referred to by the specified domain object via 
//   * a given 1:M association and satisfy a value-based expression. 
//   * 
//   * @requires <code>myCls</code> is a domain class registered in <code>this</code> /\ 
//   * myRefAttrib is a domain attribute of <tt>myCls</tt> /\ 
//   * yourAttribName != null -> op != null
//   * 
//   * @effects <pre>
//   *  let c = myCls, d = youCls
//   *  let a(c,myRefAttrib,d,yourRefAttrib) be the 1-M association between c and d (d is at the MANY end)
//   *  load objects of d (if not done so) that participate in the association a and,  
//   *    if yourAttribName is not null, the values of whose domain attribute named yourAttribName satisfy (x op val)
//   *  update the association links from those objects to o  
//   * </pre>
//   * 
//   * @example 
//   * <pre>
//   *  c = Customer.class
//   *  attribute Customer.enrolments is List<Enrolment>
//   *  given a Customer object o
//   *    enrols = List<Enrolment> containing all enrolments of o
//   *    o.enrolments = enrols 
//   * </pre>
//   * 
//   * <p>The way that this method load objects is similar to {@link #retrieveAssociateOid(Class, Oid, DomainConstraint, Class)}.
//   * 
//   *  @version 3.0
//   */
//  public <T> Collection<T> retrieveAssociatedObjects(Class myCls, Object o,
//      String myRefAttrib, Class<T> youCls,
//      String yourAttribName, Op op, Object val
//      ) throws NotFoundException, NotPossibleException {
//    
//    final AssocType type = AssocType.One2Many;
//    final Class c = o.getClass();
//    
//    Map<DomainConstraint,Association> myManyAssocs = dsm.getAssociations(c, type, AssocEndType.One);
//    
//    if (myManyAssocs == null)
//      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, new Object[] {
//          c.getSimpleName(), type, ""
//      });
//
//    Association assoc;
//    DomainConstraint dc, targetAttrib;
//    String attribute; //, targetAttributeName;
//    int linkCount;
//    Class<T> assocClass; 
//    String assocName;
//    Tuple2<DomainConstraint,Association> tuple;
//    
//    Query q;
//    final Expression.Op eq = Expression.Op.EQ;
//    Collection<T> yourObjects = null;
//
//    ObjectExpression yourAttribExp = null;
//    if (yourAttribName != null) {
//      DomainConstraint yourAttrib = dsm.getDomainConstraint(youCls, yourAttribName);
//      yourAttribExp = new ObjectExpression(youCls, yourAttrib, op, val);
//    }
//    
//    for (Entry<DomainConstraint, Association> entry : myManyAssocs.entrySet()) {
//      dc = entry.getKey();
//      attribute = dc.name();
//
//      if (attribute.equals(myRefAttrib)) {
//        // found the association a(c,d)
//      
//        assoc = entry.getValue();
//  
//        tuple = dsm.getTargetAssociation(assoc);
//        
//        targetAttrib = tuple.getFirst();
//        
//        // v2.5.4: only proceed if link attribute has not been loaded 
//        linkCount = getAssociationLinkCountFromPool(c, attribute, o);
//  
//        if (linkCount <= 0) {
//          // find the target link attribute
//          assocClass = assoc.associate().type();
//    
//          q = new Query();
//          q.add(//new Expression(targetAttributeName, eq, o)
//              new ObjectExpression(youCls, targetAttrib, eq, o)
//              );
//          
//          // if target attribute expression is specified then use it 
//          if (yourAttribExp != null) {
//            q.add(yourAttribExp);
//          }
//          
//          try {
//            yourObjects = retrieveObjectsWithAssociations(assocClass, q);
//            if (yourObjects != null) {
//              updateAssociateLink(o, attribute, yourObjects);
//            }
//          } catch (DataSourceException e) {
//            // something wrong, ignore
//          }
//        }
//      }
//    } // end for
//    
//    return yourObjects;
//  }
  
//  /**
//   * Loads the domain objects that are associated to a specified domain object 
//   * through its 1:M associations and transitively so (if necessary).
//   * 
//   * <p>Example: 
//   * <pre>
//   *  c = Customer.class
//   *  attribute Customer.enrolments is List<Enrolment>
//   *  given a Customer object o
//   *    enrols = List<Enrolment> containing all enrolments of o
//   *    o.enrolments = enrols 
//   * </pre>
//   * 
//   * <p>The way that this method load objects is similar to {@link #loadAssociatedObjects(Class)}, 
//   * except that it supports transitive loading of objects
//   * 
//   * @effects <pre>
//   *  let c = o.class
//   *  for each 1:M association a(c,d), where d is some domain class 
//   *    load objects of d (if not done so) that participate in the association a
//   *    set the value of the attribute of c in o that implements a to those objects
//   *    if deep is specified
//   *      (recursive) load the objects associated to each of those objects  
//   * </pre>
//   * 
//   * @requires <code>o.class</code> is a domain class registered in <code>this</code>
//   *  which has at least one collection-type attributes.
//   */
//  public void loadAssociatedObjects(final Object o, boolean deep) throws NotFoundException, NotPossibleException {
//    // get the 1:M associations
//    
//    final AssocType type = AssocType.One2Many;
//    final Class c = o.getClass();
//    
//    Map<DomainConstraint,Association> manyAssocs = getAssociations(c, type, AssocEndType.One);
//    
//    if (manyAssocs == null)
//      return;
//
//    Association assoc;
//    DomainConstraint dc;
//    String attribute, targetAttribute;
//    int linkCount;
//    Class assocClass; 
//    String assocName;
//    Tuple2<DomainConstraint,Association> tuple;
//    
//    Query q;
//    final Expression.Op eq = Expression.Op.EQ;
//    Collection refObjects;
//
//    for (Entry<DomainConstraint, Association> entry : manyAssocs.entrySet()) {
//      dc = entry.getKey();
//      assoc = entry.getValue();
//
//      attribute = dc.name();
//      /** v2.5.4: only proceed if link attribute has not been loaded */
//      linkCount = getAssociationLinkCountFromPool(c, attribute, o);
//
//      if (linkCount > 0)
//        // loaded -> skip
//        continue;
//
//      // find the target link attribute
//      assocClass = assoc.associate().type();
//      tuple = getTargetAssociation(assoc);
//      targetAttribute = tuple.getFirst().name();
//      q = new Query();
//      q.add(new Expression(targetAttribute, eq, o));
//      try {
//        refObjects = loadObjectsWithAssociations(assocClass, q);
//        if (refObjects != null) {
//          updateAssociateLink(o, attribute, refObjects);
//          
//          // v2.7.2: perform transitive loading if deep was specified
//          if (deep) {
//            for (Object refObj : refObjects) {
//              loadAllAssociations(refObj, true);
//            }
//          }
//        }
//      } catch (DBException e) {
//        // something wrong, ignore
//      }
//    } // end inner for
//  }

//  /**
//   * Load objects of a domain class satisfying a query with <b>all</b> associated objects, 
//   * and recursively those associated to these objects and so on.
//   * 
//   * @effects <pre>
//   *  if exists domain objects of <tt>c</tt> that satisfy query <tt>query</tt>
//   *    load (if not done so) and return those objects. All the associated objects
//   *    to these objects are also loaded (and so on transitively).
//   *    
//   *    (if query is <tt>null</tt> then return all objects of <tt>c</tt>)
//   *  else
//   *    return <tt>null</tt>
//   *  </pre>
//   *  
//   *  Throws DBException if fails to process data in the data source.
//   *  
//   * @requires 
//   *  c is a registered domain class
//   */
//  public <T> Collection<T> loadObjectsWithAllAssociations(Class<T> c, Query query)
//      throws DBException {
//    // first load objects with 1:1 or M:1 associations
//    Collection<T> objects = (Collection<T>) loadObjectsWithAssociations(c, query);
//    
//    // then load those associated via 1:M associations and at the same time 
//    // upload each object of c to point to those
//    if (objects != null && !objects.isEmpty()) {
//      boolean deep = true;
//      for (T o : objects) {
//        loadAssociatedObjects(o, deep);
//      }
//      
//      return objects;
//    }
//
//    return null;
//  }
  
  /**
   * Load objects of a domain class satisfying a query with an option to 
   * also load the objects of the associated domain classes (called referenced objects).
   * 
   * @effects
   *  invoke {@link #retrieveObjectsWithAssociations(Class, Query, Stack, boolean)}
   */
  protected Collection retrieveObjectsWithAssociations(Class c, Query query, boolean loadReferenced)
      throws DataSourceException {
    // use a class stack to avoid loop
    Stack<Class> visited = new Stack();
    visited.push(c);
    return retrieveObjectsWithAssociations(c, query, visited, loadReferenced);
  }


  /**
   * Load the domain objects of a given class (optionally) constrained by a
   * query and, if <b>referenced domain types</b> (i.e. those that are associated to the 
   * given class via a <b>1:X</b> associations with the X-side being on the class) 
   * have not been loaded then also load
   * them. Thus, this method will cause the loading of the domain objects of the
   * related classes.
   * 
   * @effects 
   *    if <code>query != null</code> then 
   *      returns a <code>List</code> of
   *          the domain objects of the domain class <code>c</code> that satisfy
   *          <code>query</code>
   *    else 
   *      returns a <code>List</code> of all the
   *          domain objects of <code>c</code>. 
   *          If these objects have not been loaded then load them from the 
   *          database table of <code>c</code> and, 
   *          if successful, fire the change event of <code>c</code>.
   * 
   *          <p>
   *          If <code>loadReferenced=true</code> then also load all the objects
   *          of the referenced types.
   * 
   * @requires <code>c</code> has been registered in <code>this</code>
   * @modifies <code>this.classExts.get(c)</code>
   */
  protected Collection retrieveObjectsWithAssociations(Class c, Query query, Stack<Class> visited,
      boolean loadReferenced) throws DataSourceException {
    Collection objects = getObjects(c);
    
    // v2.6.4.b
    Map<Class,Tuple2<DAttr,DAttr>> linkLater = new HashMap<Class,Tuple2<DAttr,DAttr>>();

    if (objects == null || objects.isEmpty()) { 
      // not loaded or no objects
      // check referenced types first
      if (loadReferenced) {
        final Map<Field,DAttr> fields = dsm.getSerialisableDomainAttributes(c);

        Field f = null;
        DAttr dc;
        Type type;
        Class domainType;

        // v2.6.4.b: 
        DAttr linkedAttrib;
        
        if (debug)
          System.out.printf("Class: %s%n", c.getSimpleName());
        
        Collection<Entry<Field,DAttr>> fieldEntries = fields.entrySet();
        int i = -1;
        FIELD: // v5.0: for (int i = 0; i < fields.size(); i++) {
          for (Entry<Field,DAttr> entry : fieldEntries) {
          /*f = (Field) fields.get(i);
          dc = f.getAnnotation(DC);
          */
          i++;
          f = entry.getKey();
          dc = entry.getValue();
          type = dc.type();
          if (type.isDomainType()) {
            domainType = f.getType();
            // load all the objects of this type (if not already)
            if (visited.contains(domainType)) {
              // visited --> ignore
              continue FIELD;
            } 
            // v2.6.1: ignore if domain type is not registered
            /*
            else {
              visited.push(domainType);
            }
            */
            if (!dsm.isRegistered(domainType)) {
              continue FIELD;
            }
            // END v2.6.1
            
            // load objects of domainType
            visited.push(domainType);

            if (debug)
              System.out.println("...referencing: "
                  + domainType.getSimpleName());
            
            Collection objs = getObjects(domainType);
            
            //TODO: fix NullPointerException if domainType is an enum-type
            if (objs == null || objs.isEmpty()) {
              // not yet loaded
              retrieveObjectsWithAssociations(domainType, null, visited, true);
            } else if (isAbstract(domainType)) {
              // check to make sure that all the sub-classes (if any)
              // have also been loaded
              Class[] subs = dsm.getSubClasses(domainType);
              if (subs != null) { // load sub-classes
                for (Class sub : subs) {
                  retrieveObjectsWithAssociations(sub, null, visited, true);//loadReferenced);
                }
              }
            }
            
            /* v2.6.4.b: add support for 1:1 association with determinant:
             * if domainType is determined by c in a 1:1 association then record domain type
             * so that their objects can be updated to point to c's objects later
             */
            if (dsm.isDeterminant(c,dc)) {
              linkedAttrib = dsm.getLinkedAttribute(c, dc);
              if (linkedAttrib != null) linkLater.put(domainType, 
                  new Tuple2<DAttr,DAttr>(dc,linkedAttrib));
            }
          } // end if domainType
        } // end for(fields) loop
      }
    }

    // now load objects of c
    /**
     * TODO: Performance: <br>
     * - can we improve the first step to load only the objects satisfying
     * objectExp from the database? <br>
     * - cache loaded objects for a given objectExp and return them
     * 
     * there are two main cases:
     * 
     * <pre>
     * if c is abstract and c has sub-classes
     *        load all sub-classes of c
     *      else
     *        load c (see below)
     * </pre>
     * 
     * <p>
     * To load objects of c:<br>
     * 
     * <pre>
     * init objects = all the objects of c in this
     * if (objects == null)
     *   load them from db table of c
     * 
     * if (objectExp == null) 
     *   return objects
     * else
     *   filteredObjects = new List
     *   for (Object o: objects)
     *     if o satisfies objectExp
     *       add o to filteredObjects
     *   return filteredObjects
     * </pre>
     */
    if (objects.isEmpty()) {
      if (isAbstract(c)) {
      //Class[] subs = getSubClasses(c);
      //if (subs != null && isAbstract(c)) { // load sub-classes
        Class[] subs = dsm.getSubClasses(c);
        if (subs != null) {
          for (Class sub : subs) {
            retrieveObjectsWithAssociations(sub, null, loadReferenced);
          }
          
          // try getting the subclass objects after load
          objects = getObjects(c);
          
          if (!objects.isEmpty()) {
            // notify listeners
            fireStateChanged(c, objects, LAName.New);
          }
        }
      } else { // load c
        if (debug)
          System.out.println("loading from db: " + c.getSimpleName());

        List dbObjects = osm.readObjects(c);
        if (dbObjects != null) {
          // must use this loop to update the object tree
          for (Object o : dbObjects) {
            /** v2.5.3 
             * should we also load the referenced objects as well?
             * (it may be a performance overhead though)  
             */ 
            addObject(o.getClass(), o, false); // serialised = false
          }

          // v2.6.4.b: if there are linked objects to be updated then update them
          if (!linkLater.isEmpty()) {
            for (Entry<Class,Tuple2<DAttr,DAttr>> e : linkLater.entrySet()) {
              updateReferencedObjects(c, e.getValue().getFirst(), dbObjects, 
                  e.getKey(), e.getValue().getSecond());
            }
          }
          
          // notify listeners
          fireStateChanged(c, objects, LAName.New);
        }
      }
    }

    if (query == null) {
      return objects;
    } else {
      return getFilteredObjects(c, query);
    }
  }

  // v2.6.4.b: update referenced objects that are determined by an associate in a 1:1 association
  protected void updateReferencedObjects(Class c, DAttr attrib, Collection objects, 
      Class refClass, DAttr linkedAttrib) {
    Object refObj;
    String linkedAttribName = linkedAttrib.name();
    if (debug)
      System.out.printf("updating objects of %s, determined by %s%n", refClass.getSimpleName(), c.getSimpleName());

    for (Object o : objects) {
      refObj = dsm.getAttributeValue(o, attrib.name());
      if (refObj != null) {
        // update refObj with the reverse link to o
        setAttributeValue(refObj, linkedAttribName, o);
      } else {
        if (debug)
          log(null, "updateReferenceObjects", "Object required but is null");
      }
    }
  }
  
  /**
   * @effects 
   *  if the domain objects of <tt>c</tt> and its sub-types (if any) have not been loaded
   *    load them (together with all the associated objects if any) from the data source
   *  
   *  <p>return the domain objects as <tt>Collection</tt>
   *  
   *  <p>Throws DBException if fails to load from the data source 
   */
  public Collection retrieveObjectHierarchyWithAssociations(Class c) throws DataSourceException {
    if (isAbstract(c)) {
      // c is an abstract super-class
      return retrieveObjectsWithAssociations(c);
    } else {
      // c is not abstract, load c and those of its sub-types (recursively)
      Collection result = new ArrayList(), objects, subTypeObjects;
      
      objects = retrieveObjectsWithAssociations(c);
      
      if (!objects.isEmpty()) result.addAll(objects);
      //if (objects == null) objects = new ArrayList();
      
      // see if c has sub-types
      Class[] subTypes = dsm.getSubClasses(c);
      if (subTypes != null) {
        for (Class s : subTypes) {
          // loading objects of sub-types
          subTypeObjects = retrieveObjectsWithAssociations(s);
          if (!subTypeObjects.isEmpty()) {
            // add sub-type objects to result
            result.addAll(subTypeObjects);
          }
        }
      }
      
      return result;
    }
  }
  
  /**
   * Load all objects of a domain class and also load the objects of 
   * the associated domain classes.
   * 
   * @effects returns all domain objects of a class <code>c</code>.
   * @see #retrieveObjectsWithAssociations(Class, Query, boolean)
   */
  public Collection retrieveObjectsWithAssociations(Class c) throws DataSourceException {
    return retrieveObjectsWithAssociations(c, null, true);
  }

  /**
   * @effects Load all objects of a domain class <tt>c</tt> satisfying a query. Also load the objects of 
   * the associated domain classes and those of the classes associated to these, and so on.
   * 
   * @requires <code>c</code> has been registered in <code>this</code>
   * @modifies <code>this.classExts.get(c)</code>
   */
  public Collection retrieveObjectsWithAssociations(Class c, Query query) throws DataSourceException {
    return retrieveObjectsWithAssociations(c, query, true);
  }

  
  /**
   * @requires
   *  c is a registered domain class.
   *  
   * @effects 
   *  remove all objects of <tt>c</tt> from memory (including those in the 
   *  extents of the super classes of c if any)
   *  remove the class extent of <tt>c</tt>.
   *  
   *  <p>Throws NotPossibleException if fails to do so. 
   */
  public void unloadObjects(Class c) throws NotPossibleException {
    // remove objects of c from memory
    try {
      deleteObjects(c, false);
    } catch (DataSourceException e) {
      // should not happen
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, 
          e, new Object[] {e.getMessage()});
    }
  }
  
  /**
   * This method <b>does not</b> read the associated objects and as such assume that they 
   * have already been imported. Applications must make sure that that this is the case. 
   * 
   * @modifies {@link #classExts}
   * @effects 
   *  read the domain objects of <tt>cls</tt> from the data source represented by <tt>osm</tt> and  
   *  add them to the object pool of <tt>cls</tt> in this.
   *  
   *  <p>If <tt>cls</tt> is serialisable then also store the objects into the data source.
   *  
   *  <p>Inform listeners of the new objects. 
   *  
   *  <p>Throws DataSourceException if fails to load objects.
   *  
   *  @version 2.7.3
   */
  public void importObjects(OSM osm, Class cls) throws DataSourceException {
    List objs = osm.readObjects(cls);
    if (objs != null) {
      // must use this loop to update the object tree
      for (Object o : objs) {
        addObject(o.getClass(), o, true); // serialised
      }

      // notify listeners
      fireStateChanged(cls, objs, LAName.New);
    }
  }
  
  /**
   * @requires 
   *  cls != null /\ attrib != null /\ attrib is a domain attribute of cls /\ 
   *  attrib.defaultValueFunction = true
   * @effects 
   *  if exists the specification of a default value of <tt>attrib</tt> in <tt>cls</tt>
   *    execute it and return value
   *  else
   *    return null
   *  @version 
   *  - 2.7.4 <br>
   *  - 3.1: moved from DSM <br>
   *  - 3.2: fixed to used a new metadata-search method
   */
  public Object getAttributeValueDefault(Class cls, DAttr attrib) 
      throws NotFoundException, NotPossibleException {
    // use either defaultValue or defaultValueFunction
    Object defVal = null;
    if (!attrib.defaultValue().equals(CommonConstants.NullString)) {
      //v3.1: need to convert value first 
      Object rawVal = attrib.defaultValue();
      //TODO: retrieve attribute's type if needed
      Class attribDeclaredType = null;
      try {
        defVal = DODMToolkit.parseDomainValue(attrib, attribDeclaredType, rawVal);
      } catch (ConstraintViolationException e) {
        if (debug)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            e, new Object[] {cls, "getAttributeValueDefault", attrib.name()});
      }
    } else if (attrib.defaultValueFunction()) {
      // use function
      Method m = null;
      // find the default value function of the class c
      try {
        /*v3.2: create a new method 
        m = dsm.findMetadataAnnotatedMethod(cls, Metadata.Type.MethodDefaultValueFunction, attrib);
        */
        m = dsm.findMetadataAnnotatedMethodWithAttribute(cls, DOpt.Type.DefaultValueFunction, attrib);
      } catch (NotFoundException e) {
        // not found
        if (debug) throw e;
      }
      
      if (m != null) {
        // invoke the method
        try {
          // m is a static method: 
          // m must either: (1) have no args or (2) has DOMBasic as the only arg
          if (m.getParameterTypes().length == 0)
            defVal = m.invoke(null);
          else
            defVal = m.invoke(null, this);
        } catch (Exception e) {
          // failed to perform function
          if (debug) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
                e, new Object[] {cls, "getAttributeValueDefault", attrib.name()});
          }
        }
      }
    }
    
    return defVal;
  }
  
  /**
   * This method is a special case of {@link #getAttributeValues(Class, DAttr)} where 
   * the attribute is the id-attribute.
   * 
   * @requires 
   *  c is a registered domain class in this 
   * @effects <pre>
   *  if exists objects of <tt>c</tt> in the object pool
   *    return {@link Collection} of the <i>first</i> id-attribute values of those objects; 
   *    or return <tt>null</tt> if <tt>c</tt> has no id-attributes
   *  else
   *    return <tt>null</tt>
   *    </pre>
   * @version 3.1 
   */
  public Collection getIdAttributeValues(Class c) {
    List<DAttr> idAttribs = dsm.getIDDomainConstraints(c);
    
    if (idAttribs != null) {
      DAttr firstIdAttrib = idAttribs.get(0);
      
      return getAttributeValues(c, firstIdAttrib);
    }
    
    return null;
  }

  /**
   * @requires 
   *  c is a registered domain class in this /\ 
   *  attrib is a domain attribute of c
   *    
   * @effects 
   *  retrieve from <tt>c</tt>'s object pool a <tt>Collection</tt> of values of the attribute <tt>attrib</tt> of 
   *  the <tt>c</tt>, 
   *  or return <tt>null</tt> if no objects exist
   */
  public Collection getAttributeValues(Class c, DAttr attrib) {
    Collection vals;
    
    Collection objects = getObjects(c);
    if (objects == null) {
      // no objects
      return null;
    } else {
      vals = new ArrayList();
      Object val;
      for (Object o : objects) {
        val = dsm.getAttributeValue(c, o, attrib);
        if (val != null) {
          vals.add(val);
        }
      }
      
      return (vals.isEmpty()) ? null : vals;
    }
  }
  
  /**
   * @requires 
   *  c is a registered domain class in this /\ 
   *  attrib is a domain attribute of c
   *  /\ {@link #isObjectSerialised()} = true
   *    
   * @effects 
   *  load from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>.
   *  Arrange the values in the natural order. 
   *  <p>If no values are found then return <tt>null</tt>
   */
  public Collection loadAttributeValues(Class c, DAttr attrib) {
    return osm.readAttributeValues(c, attrib);
  }

  /**
   * @requires 
   *  c is a registered domain class in this /\ 
   *  attrib is a domain attribute of c /\ 
   *  query is a Select-type object query that is defined over <tt>c</tt> and performs a projection over <tt>attrib</tt>
   *  /\ {@link #isObjectSerialised()} = true
   *    
   * @effects 
   *  load from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
   *  that satisfy <tt>query</tt>. 
   *  <br>Arrange the values in the natural order. 
   *  <p>If no values are found then return <tt>null</tt>
   * @version 3.3
   */
  public Collection loadAttributeValues(Class c, DAttr attrib, FlexiQuery query) {
    return osm.readAttributeValues(c, attrib, query);
  }
  
  /**
   * @requires 
   *  c is a registered domain class in this /\ 
   *  attrib is a domain attribute of c
   *  /\ {@link #isObjectSerialised()} = true
   *    
   * @effects 
   *  load from data source and return a Collection of values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt>.
   *  <br>If <tt>orderByKey = true</tt> then arrange the values in the order of the key attribute, else arrange them in the natural 
   *  order.
   *    
   *  <p>If no values are found then return <tt>null</tt>
   * @version 3.3
   */
  public Collection loadAttributeValues(Class c, DAttr attrib, final boolean orderByKey) {
    return osm.readAttributeValues(c, attrib, orderByKey);
  }
  
  /**
   * @requires 
   *  c is a registered domain class in this /\ 
   *  attrib is a domain attribute of c
   *  /\ {@link #isObjectSerialised()} = true
   *  
   * @effects 
   *  load from data source and return a Map<Oid,Object> of 
   *  the Oids and the values of the attribute <tt>attrib</tt> of the domain class <tt>c</tt> 
   *  (in the same order as retrieved from the data source), 
   *  or return <tt>null</tt> if no such objects exist
   */
  public Map<Oid,Object> loadAttributeValuesWithOids(Class c, DAttr attrib) {
    return osm.readAttributeValuesWithOids(c, attrib);
  }
  
  /**
   * @requires 
   *  {@link #isObjectSerialised()} = true
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
  private Object loadAttributeValue(Class c, Oid oid, DAttr attrib) {
    return osm.readAttributeValue(c, oid, attrib);
  }
  
  /**
   * @requires 
   *  c != null /\ attrib != null /\ val != null /\ 
   *  attrib is a domain attribute of c
   *  
   * @effects 
   *  if exists a domain object o of c s.t equals(o.atrib.value,val)  
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   * 
   * @version 2.7.4
   */
  public boolean existAttributeValue(Class c, DAttr attrib, Object val) {
    /* 
     * create and execute query over (c, attrib,val) to retrieve Oids
     * if result is empty
     *  return false
     * else
     *  return true
     */
    Op op = Op.EQ;
    Query<ObjectExpression> q = new Query<>(new ObjectExpression(c, attrib, op, val));
    
    OSM osm = getOsm();
    
    Collection<Oid> oids = null;
    try {
      oids = osm.readObjectIds(c, q);
    } catch (Exception e) {
      // ignore
    }
    
    if (oids == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * @effects
   *  count and return the number of domain objects of <tt>c</tt> in the data source; 
   *  return -1 if no objects are found.
   *   
   *  <p>Throws DBException if failed to process data in the data source
   * @version 
   *  2.8: rename "load..." to "retrieve..." to support memory-based configuration
   */
  public int retrieveObjectCount(Class c) throws DataSourceException {
    /*v2.8: support memory-based config 
    return osm.readObjectCount(c);
    */
    if (isObjectSerialised()) {
      return osm.readObjectCount(c);
    } else {
      return getObjectCount(c);
    }
  }
  
  /**
   * @effects
   *  if exists <tt>Oid</tt>s in the data source satisfies <tt>query<tt> 
   *    return their count
   *  else 
   *    return 0
   */
  public int loadObjectCount(Class c, Query query) {
    int count;
    
    try {
      Collection<Oid> oids = retrieveObjectOids(c, query);
      if (oids != null) {
        return oids.size();
      } else {
        return 0;
      }
    } catch (DataSourceException e) {
      return 0;
    }
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ currId != null
   *  /\ currId is a valid object id of an object of c 
   *  /\ object whose id immediately precedes currId (in natural ordering) has not previously been loaded
   *   
   * @effects 
   * <pre>
   *  load object o of c whose id immediately <b>precedes</b> currId (in natural ordering)
   *  ; or null if no such Oid exists
   *      
   *  throws DBException if fails to load object(s) from data source; 
   *  NotPossibleException if fails to create object id from data source
   *  </pre>
   */
  public Object retrieveObjectFirstBefore(Class c, Oid currId) throws DataSourceException, NotPossibleException  {
    // load Oid from data source
    Oid oid = retrieveIdFirstBefore(c, currId);
    
    Object o = null;
    
    /* v2.8: added support for memory-based config
    if (oid != null) {
      // load object
      o = loadObject(c, oid);
    } */
    if (oid != null) {
      if (isObjectSerialised()) {
        // load object
        o = loadObject(c, oid);
      } else {  // v2.8
        o = retrieveObject(c, currId);
      }
    }
    
    return o;
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ currId != null
   *  /\ currId is a valid object id of an object of c 
   *  /\ object whose id immediately proceeds currId (in natural ordering) has not previously been loaded
   *   
   * @effects 
   * <pre>
   *  load object o of c whose id immediately <b>proceeds</b> currId (in natural ordering)
   *  ; or null if no such Oid exists
   *      
   *  throws DBException if fails to load object(s) from data source; 
   *  NotPossibleException if fails to create object id from data source
   *  </pre>
   */
  public Object retrieveObjectFirstAfter(Class c, Oid currId) throws DataSourceException, NotPossibleException {
    // load object id from data source
    Oid oid = retrieveIdFirstAfter(c, currId);

    Object o = null;
    
    /*v2.8: support memory-based config
    if (oid != null) {
      // load object
      o = loadObject(c, oid);
    } */
    if (oid != null) {
      if (isObjectSerialised()) {
        // load object
        o = loadObject(c, oid);
      } else {  // v2.8
        o = retrieveObject(c, currId);
      }
    }
    
    return o;
  }

  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ oid != null
   *  /\ oid is a valid object id of an object of c
   *  <br> /\ {@link #isObjectSerialised()} = true
   *   
   * @effects 
   * <pre>
   *  load from the data source object o of c whose id is oid
   *      
   *  throws NotFoundException if object is not found;  
   *  DataSourceException if fails to load object(s) from data source.
   *  </pre>
   */
  public <T> T loadObject(Class<T> c, Oid id) throws DataSourceException, NotFoundException {
    return (T) loadObject(c, id, null);
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ oid != null
   *  /\ oid is a valid object id of an object of c /\ 
   *  (manyAssocs != null -> manyAssocs contains 1:M associations of c)
   *  <br> /\ {@link #isObjectSerialised()} = true
   *   
   * @effects 
   * <pre>
   *  load from the data source object o of c whose id is oid and also <b>all</b> 
   *  the associated objects of <tt>o</tt> (and their associated objects, recursively)
   *  </pre>
   *  
   * @throws NotFoundException if object is not found  
   * @throws NotPossibleException if failed to create object from the data source record
   * @throws DataSourceException if failed to read record from the data source
   */
  public <T> T loadObject(Class<T> c, Oid oid, 
      //TODO: this parameter is not being used
      List<Tuple2<DAttr,DAssoc>> manyAssocs) 
  throws DataSourceException, NotFoundException, NotPossibleException {

    // read from data source
    T o = (T) osm.readObject(c, oid);

    if (o == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Không tìm thấy đối tượng {0}<{1}>", c, oid); 
    }
    
    // v2.6.4b: store Oid into the object if it is an instance of DomainIdable
    if (o instanceof DomainIdable) {
      ((DomainIdable)o).setOid(oid);
    }
    
    // add object to pool (must use o.class not c here)
    addObject(o.getClass(), o, oid, false);

    // v2.7.2: if o has determinant 1:1 association with other classes (i.e.
    // o.class is associated to another class X where X is the determinant of the association)
    // then load objects of those classes that are associated to o
    loadDeterminantObjectsOf(c, o, oid, null, null);
    
    // v3.2: if exist many-many associations in c then also load those associations
    // via their corresponding normaliser attributes
    // TODO: 
    // - if manyAssocs are specified then check within this
    // - for performance reason: somehow combine with loadDeterminantObjectsOf (above) ?
    // loadManyToManyAssociatesOf(c, o, oid);
    
    //FIXME: this is currently not used (included in readObject() above)
    // load linked objects of the specified associations
    /*v2.7.4: not used
    if (manyAssocs != null) {
      Tuple2<DomainConstraint,Association> targetAssoc;
      Association myEnd;
      DomainConstraint attrib;
      Class assocClass;
      List associates;
      for (Tuple2<DomainConstraint, Association> myAssoc : manyAssocs) {
        attrib = myAssoc.getFirst();
        myEnd = myAssoc.getSecond();
        targetAssoc = dsm.getTargetAssociation(myEnd);
        assocClass = myEnd.associate().type();
        // load associates via this association
        associates = retrieveObjects(assocClass, targetAssoc, oid);
        // set associates to o.attribute
        updateAssociateLink(o, attrib.name(), associates);
      }
    }
    */
    
    return o;
  }

  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ oid != null
   *  /\ oid is a valid object id of an object of c /\
   *  fromAssocCls != null /\ fromLinkedAttrib != null /\ 
   *  fromAssocCls is associated to c via attribute fromLinkedAttrib /\ 
   *  {@link #isObjectSerialised()} = true  
   *   
   * @effects 
   * <pre>
   *  load from the data source object o of c whose id is oid and who has an association link 
   *  with another domain object of type <tt>fromAssocCls</tt> via attribute <tt>fromAssocCls.fromLinkedAttrib</tt>; 
   *  also load the objects associated to <tt>o</tt> (and so on recursively)
   *  </pre>
   * 
   * @throws NotFoundException if object is not found  
   * @throws NotPossibleException if failed to create object from the data source record
   * @throws DataSourceException if failed to read record from the data source
   *   
   * @version 
   * - 2.7.4 <br>
   * - 3.1: support fromAssocOid 
   */
  public <T> T loadAssociatedObject(Class<T> c, 
      Oid oid, 
      Class fromAssocCls,
      Oid fromAssocOid, // v3.1
      DAttr fromLinkedAttrib) 
  throws DataSourceException, NotFoundException, NotPossibleException {

    // not in pool, read from data source but exclude that which is associated via <tt>fromAssocCls.fromLinkedAttrib</tt>)
    T o = (T) osm.readAssociatedObject(c, oid, fromAssocCls, 
        fromAssocOid, // v3.1
        fromLinkedAttrib);

    if (o == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Không tìm thấy đối tượng {0}<{1}>", c, oid); 
    }
    
    // v2.6.4b: store Oid into the object if it is an instance of DomainIdable
    if (o instanceof DomainIdable) {
      ((DomainIdable)o).setOid(oid);
    }
    
    // add object to pool (must use o.class not c here)
    addObject(o.getClass(), o, oid, false);

    // v2.7.2: if o has determinant 1:1 association with other classes (i.e.
    // o.class is associated to another class X where X is the determinant of the association)
    // then load objects of those classes that are associated to o
    loadDeterminantObjectsOf(c, o, oid, fromAssocCls, fromLinkedAttrib);
    
    return o;
  }
  
  /**
   * @requires 
   *  c != null /\ o != null is a domain object of c /\ id != null
   *  excludeAssocCls != null -> (excludeLinkedAttrib != null /\ excludeLinkedAttrib is an attrribute of excludeAssocCls)
   *   
   * @modifies o
   * @effects <pre> 
   *  if o has determinant 1:1 association with other classes (i.e.
   *   o.class is associated to class X where X is the determinant of the association)
   *    load objects of those classes that are associated to o and update o accordingly
   *  else
   *    do nothing 
   *    </pre>
   *    
   *  throws NotFoundException if fail to look up the relevant domain attributes of the associated of <tt>c</tt>; 
   *  DBException if fails to read object ids from the data source. 
   *    
   *  @version 2.7.4: improved to support exclusion
   */
  private <T> void loadDeterminantObjectsOf(Class<T> c, T o, Oid id, 
      Class excludeAssocCls, DAttr excludeLinkedAttrib) throws NotFoundException, DataSourceException {
    Map<DAttr,DAssoc> deterAssocs = dsm.getAssociationsByDeterminant(c);
    if (deterAssocs != null) {
      // has determinant associations
      DAttr attrib, assocAttrib;
      DAssoc assoc;
      Class assocCls;
      Object assocObj;
      Op op = Op.EQ;
      Tuple2<DAttr,DAssoc> targetAssocTuple;
      ASSOC: for (Entry<DAttr,DAssoc> e : deterAssocs.entrySet()) {
        attrib = e.getKey(); 
        assoc = e.getValue();
        assocCls = assoc.associate().type();
        
        // only consider serialisable classes
        if (!isTransient(assocCls)) {
          targetAssocTuple = dsm.getTargetAssociation(assoc);
          if (targetAssocTuple != null) {
            assocAttrib = targetAssocTuple.getFirst();

            // v2.7.4: support exclusion
            if (assocCls == excludeAssocCls && excludeLinkedAttrib != null && assocAttrib.equals(assocAttrib)) {
              // to be excluded -> skip
              continue ASSOC;
            }

            if (assocAttrib.serialisable()) { // only load object linked via a serialisable attribute 
              if (debug)
                log(null, "loadDeterminantObjectsOf", "  loading object of determinant assoc class: " + assocCls.getSimpleName());
              
              assocObj = retrieveObject(assocCls, assocAttrib, op, o);
              if (assocObj != null) {
                // update o: add link to assocObj
                setAttributeValue(o, attrib.name(), assocObj);
              }
            }
          } else {
            // no target association defined -> ignore & log
            if (debug)
              log(null, "loadDeterminantObjectsOf", "  no target association for class: " + c.getSimpleName(), " attribute: " + attrib.name());
          }
        }
      }
    }
  }
  
//  /**
//   * <b>NOTE</b>: The treatment of a many-to-many association is different from other associations (e.g. 
//   * as performed via {@link #retrieveAssociatedObjects(Object, Class, Class, String, ObjectComparator, Expression...)})
//   * in that the association links to the associate objects are derived via those of 
//   * the <b>normaliser attribute</b> that normalises the association using a pair of one-many associations.   
//   * 
//   * @requires 
//   *  c != null /\ o != null is a domain object of c /\ id != null 
//   *   
//   * @modifies o
//   * @effects <pre> 
//   *  if o has many-many associations with other classes 
//   *    load the associate objects that are linked to <tt>o</tt> and update 
//   *    <tt>o</tt> with association links to them
//   *  else
//   *    do nothing 
//   *    </pre>
//   *    
//   *  throws NotFoundException if no normaliser attribute is specified or no suitable association 
//   *    is defined for this attribute;  
//   *  DataSourceException if fails to read object ids from the data source. 
//   *    
//   *  @version 3.2
//   */
//  private <T> void loadManyToManyAssociatesOf(Class<T> c, T o, Oid id) throws DataSourceException {
//    Map<DAttr,DAssoc> assocs = dsm.getManyToManyAssociations(c);
//    if (assocs != null) {
//      // has many-many associations
//      DAssoc assoc, normAssoc;
//      Class assocCls; // the associate class on the opposite many-side
//      DAttr normAttrib;  // normaliser attribute
//      Tuple2<DAttr,DAssoc> normAssocTuple;  // association of normaliser attribute
//      ASSOC: for (Entry<DAttr,DAssoc> e : assocs.entrySet()) {
//        assoc = e.getValue();
//        assocCls = assoc.associate().type();
//
//        // only consider serialisable classes
//        if (!isTransient(assocCls)) {
//          normAttrib = dsm.getDomainConstraint(c, assoc.normAttrib()); 
//          normAssocTuple = dsm.getAssociation(assocCls, normAttrib);
//          normAssoc = normAssocTuple.getSecond();
//
//          // retrieve associate objects that are linked via the normaliser attribute
//          // ASSUME: link operations of the normaliser attribute also update the links to objects of assocCls
//          retrieveAssociatedObjects(o, c, normAssoc.associate().type(), normAssoc.ascName());
//        }
//      }
//    }
//  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ oid != null
   *  /\ oid is a valid object id of an object of c
   *   
   * @effects 
   * <pre>
   *  load (from the data source if necessary) object o of c whose id is oid
   *      
   *  throws NotFoundException if object is not found;  
   *  DBException if fails to load object(s) from data source.
   *  </pre>
   * @version 
   *  2.7.3: created <br>
   *  2.8: improved to support memory-based configuration
   */
  public <T> T retrieveObject(Class<T> c, Oid id) throws DataSourceException, NotFoundException {
    // look up first
    T o = lookUpObject(c, id);
    
    boolean objectSerialised = isObjectSerialised();  // v2.8
    
    if (o == null
        && objectSerialised   // v2.8: added this check
        )
      o = loadObject(c, id, null);
    
    return o;
  }

  /**
   * This is an extension of {@link #retrieveObject(Class, String, Op, Object)} to support 
   * multiple attributes.
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attribNames</tt> are names of a valid domain attribute of <tt>c</tt> /\
   *  <tt>attribNames != null /\ ops != null /\ attribVals != null</tt> 
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the values of attributes named <tt>attribNames</tt> satisfy 
   *  <tt>op v</tt>, for all <tt>op in ops, v in attribVals</tt>
   *    return such first object
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if some attribute name does not match any of the domain attributes of <tt>c</tt> 
   *  
   * @version 2.8
   */
  public <T> T retrieveObject(Class<T> c,
      String[] attributeNames, Op[] ops, Object[] attribVals) throws DataSourceException, NotFoundException {
    Query q = new Query();
    DAttr attrib;
    ObjectExpression exp;
    int i = 0;
    for (String attribName : attributeNames) {
      attrib = dsm.getDomainConstraint(c, attribName);
      exp = new ObjectExpression(c, attrib, ops[i], attribVals[i]);
      q.add(exp);
      i++;
    }
    
    Map<Oid,T> objects= retrieveObjects(c, q, 
        null  // v3.0
        );
    
    if (objects != null){
      return (T) objects.entrySet().iterator().next().getValue();
    } else {
      return null;
    }
  }

  /**
   * This method is a short-cut for {@link #retrieveObjects(Class, Query, ObjectComparator)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attribName</tt> is the name of a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute named <tt>attribName</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return such first object
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attribName</tt> does not match any of the domain attributes of <tt>c</tt> 
   */
  public <T> T retrieveObject(Class<T> c, String attribName, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    return retrieveObject(c, attrib, op, attribVal);
//    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
//    Map<Oid,Object> objects= loadObjects(c, q);
//    
//    if (objects != null){
//      return (T) objects.entrySet().iterator().next().getValue();
//    } else {
//      return null;
//    }
  }
 
  /**
   * This method is similar to {@link #retrieveObject(Class, String, Op, Object)}, which is 
   * a short-cut for {@link #retrieveObjects(Class, Query, ObjectComparator)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attrib</tt> is a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute <tt>attrib</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return such first object
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attrib</tt> does not match any of the domain attributes of <tt>c</tt>; 
   *  DBException if fails to read object ids from the data source. 
   */  
  public <T> T retrieveObject(Class<T> c, DAttr attrib, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Map<Oid,T> objects= retrieveObjects(c, q, 
        null  // v3.0
        );
    
    if (objects != null){
      return (T) objects.entrySet().iterator().next().getValue();
    } else {
      return null;
    }
  }
  
  /**
   * This is a more generic version of {@link #retrieveObject(Class, String, Op, Object)} in that it takes 
   * a query as input.
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>q</tt> is a valid query over <tt>c</tt> 
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the state of each of which satisfies <tt>q</tt>
   *    return the <b>first</b> of such objects
   *  else
   *    return null
   *    
   *  <p>throws NotPossibleException if failed to generate data source query; 
   *  DataSourceException if fails to read from the data source. 
   *  
   * @version 3.3
   */
  public <T> T retrieveObject(Class<T> c, Query q) throws NotPossibleException, DataSourceException {
    Map<Oid,T> objects= retrieveObjects(c, q, 
        null  // v3.0
        );
    
    if (objects != null){
      return (T) objects.entrySet().iterator().next().getValue();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists object <tt>o</tt> in the class store of <tt>c</tt> whose <tt>Oid</tt> is <tt>id</tt>
   *    load <tt>o</tt> into <tt>c</tt>'s object pool (replacing any existing object of the same <tt>id</tt>)
   *    but <b>without</b> loading any of the associated objects (these objects are assumed to 
   *    have been loaded and thus available for use directly in the relevant object pools), 
   *    return <tt>o</tt>; 
   *  else
   *    throws NotFoundException
   *    
   * @throws NotFoundException if object is not found OR an associated object is not found  
   * @throws NotPossibleException if failed to create object from the data source record
   * @throws DataSourceException if failed to read record from the data source
   *   
   *  @version 3.0
   */
  public <T> T reloadObject(Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
    // for sub-types to implement
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        DOMBasic.class.getSimpleName(), "");
  }
  
  /**
   * <b>IMPORTANT</b>: Use this method only to initialise the object browser. 
   * DONOT use the returned Oid to create the object tuple, because the actual min Oid 
   * in the pool (although equal to this returned min Oid) may be a different object!!!
   * 
   * @requires 
   *  c != null /\ c is a domain class registered in this
   *   
   * @effects 
   * <pre>
   *  if lowest-id of the domain objects of c is not defined
   *    load it from data source
   *  
   *  return lowest-id of c
   *   
   *  throws NotFoundException if no domain objects of c are defined;  
   *  DBException if fails to load object(s) from data source.
   *  </pre>
   */
  public Oid getLowestOid(Class c) throws DataSourceException, NotFoundException {
    IdObjectMap objectMap = classExts.get(c);
    
    if (!objectMap.isIdRangeInitialised()) {
      // read both min and max from db 
      loadMetadata(c);
    }

    Oid min = objectMap.getMinId();
    
    if (min == null)
      throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
          "Không tìm thấy khoảng mã dữ liệu cho {0}", c);
    
    return min;
  }

  /**
   * <b>IMPORTANT</b>: Use this method only to initialise the object browser. 
   * DONOT use the returned Oid to create the object tuple, because the actual max Oid 
   * in the pool (although equal to this returned max Oid) may be a different object!!!
   * 
   * @requires 
   *  c != null /\ c is a domain class registered in this
   *   
   * @effects 
   * <pre>
   *  if highest-id of the domain objects of c is not defined
   *    load it from data source
   *  
   *  return highest-id of c
   *   
   *  throws NotFoundException if no domain objects of c are defined;  
   *  DBException if fails to load object(s) from data source.
   *  </pre>
   */
  public Oid getHighestOid(Class c) throws DataSourceException, NotFoundException {
    IdObjectMap objectMap = classExts.get(c);
    
    if (!objectMap.isIdRangeInitialised()) {
      // read both min and max from db 
      loadMetadata(c);
    }

    Oid max = objectMap.getMaxId();
    
    if (max == null)
      throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
          "Không tìm thấy khoảng mã dữ liệu cho {0}", c);

    return max;
  }

//  /**
//   * @effects 
//   *  if id-range of object pool of c has not been initialised
//   *    init them from data source
//   *  else
//   *    if min != null
//   *      set id-range.minId = min
//   *    if max != null
//   *      set id-range.maxId = max
//   *      
//   * @requires 
//   *  c is a registered domain class
//   */
//  private void updatePoolIdRange(Class c, Oid min, Oid max) throws DBException {
//    IdObjectMap objectMap  = classExts.get(c);
//    
//    if (!objectMap.isIdRangeInitialised()) {
//      loadMetadata(c);
//    } else {    
//      if (min != null)
//        objectMap.setMinId(min);
//      
//      if (max != null)
//        objectMap.setMaxId(max);
//    }
//  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ currId != null
   *  /\ currId is a valid object id of an object of c 
   *   
   * @effects 
   * <pre>
   *  load (from the data souce, <i>if necessary</i>) and return the Oid of c that immediately <b>precedes</b> currId (in natural ordering)
   *  ; or null if no such Oid exists
   *  
   *  throws DBException if fails to load data from data source;
   *  NotPossibleException if id values are invalid
   *  </pre>
   * @version 2.8
   *  renamed from load... to retrieve...
   */
  public Oid retrieveIdFirstBefore(Class c, Oid currId) throws DataSourceException, NotPossibleException  {
    // load object from data source
    //ASSUME: single id attribute
    DAttr idAttrib = currId.getIdAttribute(0);
    
    //Oid oid = dbt.readIdFirstBefore(c, idAttrib, currId);
    // if c is serialisable then read from the data source
    // else get the previous id in the object pool (because all objects are stored in the buffer)
    Oid oid;
    boolean objectSerialised = isObjectSerialised();  // v2.8
    
    if (!isTransient(c)
        && objectSerialised // v2.8: added this check
        ) {
      // serialisable
      oid = osm.readIdFirstBefore(c, idAttrib, currId);
    } else {
      // not serialisable
      IdObjectMap objPool = classExts.get(c);
      oid = objPool.previousId(currId);
    }
    
    return oid;
  }
  


  /**
   * @requires 
   *  c != null /\ c is a domain class registered in this /\ currId != null
   *  /\ currId is a valid object id of an object of c 
   *   
   * @effects 
   * <pre>
   *  load (from the data souce, <i>if necessary</i>) and return the Oid of c that immediately <b>proceeds</b> currId (in natural ordering) 
   *  ; or null if no such Oid exists
   *  
   *  throws DBException if fails to load data from data source;
   *  NotPossibleException if id values are invalid
   *  </pre>
   * @version 2.8
   *  renamed from load... to retrieve...
   */
  public Oid retrieveIdFirstAfter(Class c, Oid currId) throws DataSourceException, NotPossibleException {
    // load object from data source
    
    //ASSUME: single id attribute
    DAttr idAttrib = currId.getIdAttribute(0);
    
    // if c is serialisable then read from the data source
    // else get the next id in the object pool (because all objects are stored in the buffer)
    Oid oid;
    
    boolean objectSerialised = isObjectSerialised();  // v2.8
    
    if (!isTransient(c)
        && objectSerialised // v2.8: added this check
        ) {
      // serialisable
      oid = osm.readIdFirstAfter(c, idAttrib, currId);
    } else {
      // not serialisable
      IdObjectMap objPool = classExts.get(c);
      oid = objPool.nextId(currId);
    }
    
    return oid;
  }
  
  /**
   * @effects  
   *  return the Oid of an object <tt>o</tt> of <tt>c</tt> that is the determinant in 
   *  an association to some object whose <tt>Oid</tt> is <tt>linkedId</tt> 
   *  via the associative attribute <tt>c.refAttrib</tt>
   *  ;
   *  return <tt>null</tt> if no such Oid is found.
   * 
   *  @requires 
   *    c is the determinant of the association to the domain class of the object whose Oid is <tt>linkedId</tt> 
   *    
   * @version 3.0
   */
  public Oid retrieveDeterminantAssociateOid(Class c,
      DAttr refAttrib, Oid linkedId) throws NotPossibleException, DataSourceException {
    Query q = new Query<IdExpression>(new IdExpression(c, refAttrib, linkedId));
    Collection<Oid> ids = retrieveObjectOids(c, q);
    
    if (ids != null) {
      return ids.iterator().next();
    } else {
      return null;
    }
  }
  
  /**
   * 
   * @effects 
   *  load (from the data source, <i>if necessary</i>) the Oid of <tt>youCls</tt>
   *  that is referred to by the object of <tt>myCls</tt> 
   *  identified <tt>oid</tt> through the attribute <tt>myRefAttrib</tt>
   *  
   *  <p>Return <tt>null</tt> if the object with the specified id is not found OR the referenced 
   *  attribute value is <tt>null</tt>
   *    
   * @example
   *  <pre>
   *  myCls = Student
   *  oid = Student(S2014)
   *  myRefAttrib = Student:sclass (value = SClass(id=2)) 
   *  youCls = SClass
   *  
   *  -> result := Oid(SClass(id=2)) 
   *  </pre>
   *  
   *  @version 
   *  2.8: (1) improved to support memory-based config; (2) renamed "load..." to "retrieve..."
   */
  public Oid retrieveAssociateOid(Class myCls, Oid oid,
      DAttr myRefAttrib, Class youCls) {
    // TODO: assumes Comparable
    /*v2.8: 
    Comparable val = (Comparable) loadAttributeValue(myCls, oid, myRefAttrib);
    */
    
    Comparable val;
    if (isObjectSerialised()) {
      val = (Comparable) loadAttributeValue(myCls, oid, myRefAttrib);      
    } else {
      Object o = lookUpObject(myCls, oid);
      val = (Comparable) dsm.getAttributeValue(myCls, o, myRefAttrib);
    }
    
    if (val == null)
      return null;
    
    Oid assocOid = new Oid(youCls);
    List<DAttr> idAttribs = dsm.getIDDomainConstraints(youCls);
    
    if (idAttribs.size() > 1) {
      // not supported
      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
          "Tính năng hiện không được hỗ trợ: {0}", "Composite domain id attributes");
    }
    
    DAttr idAttrib = idAttribs.get(0);
    
    assocOid.addIdValue(idAttrib, val);
    
    return assocOid;
  }
  
  /**
   * @requires 
   *  c is a registered domain class /\ 
   *  idAttribs is a non-empty array of id-attributes of c /\ 
   *  idVals.length = idAttribs.length
   *  
   * @effects 
   * <pre>
   *  look up and return the object id of an object of 
   *  c matching the specified id attribute(s) and value(s)  
   *  
   *  if it is not loaded 
   *      load and register it into the pool of c
   *  
   *  throws NotFoundException if no matching object id is found; 
   *  DBException if id values are not valid
   *  </pre>
   * @version 
   * - 3.?<br>
   * - 5.1: improved to invoke {@link #lookUpObjectId(Class, DAttr[], Object[])} first.
   */
  public Oid retrieveObjectId(Class c, DAttr[] idAttribs, 
      Object[] idVals) throws NotFoundException, DataSourceException {
    /* v5.1: 
    Map<Oid,Object> objs = classExts.get(c);
    Collection<Oid> oids = objs.keySet(); 
        
    for (Oid oid: oids) {
      if (oid.equals(idAttribs, idVals)) {
        // match
        return oid;
      }
    }
    */
    Oid oid = lookUpObjectId(c, idAttribs, idVals);
    if (oid != null) {
      return oid;
    }
    // end v5.1
    
    // not found, try to load from db
    Query q = new Query();
    for (int i = 0; i < idAttribs.length; i++) {
      q.add(
          /*v2.6.4.b: changed to ObjectExpression
          new Expression(idAttribs[i].name(), Op.EQ, idVals[i])
          */
          new ObjectExpression(c, idAttribs[i], Op.EQ, idVals[i])
          );      
    }
    
    Collection<Oid> loids = osm.readObjectIds(c, q);
    
    if (loids == null) {
      // could not find the object id
      throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
          "Không tìm thấy mã đối tượng {0}<{1}>", c.getSimpleName(), Arrays.toString(idVals));
    } else {
      return loids.iterator().next();
    }
  }

  /**
   * A convenient method for invoking {@link #retrieveObjectOids(Class, Query)} to retrieve a single {@link Oid}.
   * 
   * @effects 
   *  if exists domain objects of <tt>c</tt> that satisfies query <tt>q</tt>, return <b>the first<b> of such object 
   *  that is found, else return <tt>null</tt>.
   *  
   *  <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source.
   *  
   * @version 3.4
   */
  public Oid retrieveObjectOid(Class c, Query q)throws DataSourceException, NotPossibleException {
    Collection<Oid> oids = retrieveObjectOids(c, q);
    
    if (oids != null) {
      return oids.iterator().next();
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  Load and return a Collection<Oid> of the domain objects of <tt>c</tt> that satisfies
   *  query <tt>q</tt>.
   *  
   *  <p>if c's extent is empty or no such objects exist
   *    return null
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source.
   * 
   * @requires  
   *  c is a registered domain class in this /\ 
   *  q is a valid non-join Query over c or join Query over c and the classes associated to c
   * @version 
   *  2.8: (1) improved to support memory-based config, (2) rename "load..." to "retrieve..." 
   */
  public Collection<Oid> retrieveObjectOids(Class c, Query q) throws DataSourceException, NotPossibleException {
    /* v2.8:
    return osm.readObjectIds(c, q);
    */
    if (isObjectSerialised()) {
      return osm.readObjectIds(c, q);
    } else {
      Map<Oid,Object> objs = getObjectsMap(c, q);
      if (objs != null) 
        return objs.keySet();
      else
        return null;
    }
  }
  
  /**
   * This is a generic version of {@link #retrieveObjectOids(Class, Query)} that supports order-by
   * 
   * @effects 
   *  Load and return a Collection<Oid> of the domain objects of <tt>c</tt> that satisfies
   *  query <tt>q</tt>.
   *  
   *  <br>If <tt>orderByClass != null</tt> then arrange the Oids in the order of the key attribute of the class specified 
   *  by <tt>orderByClass</tt>, else arrange the Oids in natural order. 
   *   
   *  <p>If c's extent is empty or no such objects exist then return null
   * 
   * <p> throws NotPossibleException if id values are invalid, DataSourceException if fails to read ids from the data source.
   *  
   * @requires  
   *  c is a registered domain class in this /\ 
   *  q is a valid non-join Query over c or join Query over c and the classes associated to c
   * @version 3.3 
   * 
   * @note: can support multile order-by classes (see {@link #retrieveObjectsWithOrderBy(Class, Query, Class...)})
   */
  public Collection<Oid> retrieveObjectOids(Class c, Query q, Class orderByClass) throws DataSourceException, NotPossibleException {
    if (isObjectSerialised()) {
      return osm.readObjectIds(c, q, orderByClass);
    } else {
      //TODO: support orderByClass ???
      Map<Oid,Object> objs = getObjectsMap(c, q);
      if (objs != null) 
        return objs.keySet();
      else
        return null;
    }
  }
  
  /**
   * @requires
   *  c is a registered domain class /\ attrib is a valid attribute of c
   *  
   * @effects <pre>
   *  if exists in the underlying class store of <tt>c</tt> a record for <tt>r</tt> s.t 
   *  <tt>r[attrib] = v</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   *  
   *  <p>throws NotPossibleException if failed.
   *  
   * @version 4.0   
   */
  public boolean existObject(Class c, String attrib, Object val) throws NotPossibleException {
    if (isObjectSerialised()) {
      Query q = new Query();
      DAttr attr = dsm.getDomainConstraint(c, attrib);
      q.add(new ObjectExpression(c, attr, Op.EQ, val));      
      
      try {
        return osm.existObject(c, q);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {e.getMessage()});
      }
    } else {
      // should we support the object pool? 
      return false;
    }
  }
  
  /**
   * @requires
   *  c is a registered domain class /\ attribs.length > 0 /\ vals.length > 0
   *  
   * @effects <pre>
   *  if exists in the underlying class store of <tt>c</tt> a record for <tt>r</tt> s.t 
   *  <tt>r[a] = v</tt> for all <tt>a in attribs, v in vals</tt> (respectively)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   *  
   *  <p>throws NotPossibleException if failed
   *  
   * @version 3.3   
   */
  public boolean existObject(Class c, DAttr[] attribs, Object[] vals) throws NotPossibleException {
    if (isObjectSerialised()) {
      Query q = new Query();
      for (int i = 0; i < attribs.length; i++) {
        q.add(new ObjectExpression(c, attribs[i], Op.EQ, vals[i]));      
      }
      
      try {
        return osm.existObject(c, q);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {e.getMessage()});
      }
    } else {
      // should we support the object pool? 
      return false;
    }
  }
  
  /**
   * @requires
   *  c is a registered domain class /\ q is a valid query over <tt>c</tt>
   *  
   * @effects <pre>
   *  if exists in the underlying class store of <tt>c</tt> a record that satisfies <tt>q</tt> 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  </pre>
   *  
   *  <p>throws NotPossibleException if failed
   *  
   * @version 3.3   
   */
  public boolean existObject(Class c, Query q) throws NotPossibleException {
    if (isObjectSerialised()) {
      try {
        return osm.existObject(c, q);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {e.getMessage()});
      }
    } else {
      // should we support the object pool? 
      return false;
    }
  }
  
  /**
   * @effects 
   *  if exist in the object pool of <tt>c</tt> an object 'equal' to <tt>o</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.0
   */
  public boolean existObject(Class c, Object o) {
    return lookUpObjectId(c, o) != null;
  }

//  /**
//   * An object loading policy
//   */
//  public static enum LoadPolicy {
//    /** do not load the associated objects*/
//    ASSOCIATE_LINK_NONE,
//    /** load only the directly associated objects*/
//    ASSOCIATE_LINK_DIRECT,
//    /** load the associated objects, those associated to these, and so on (transitively)*/
//    ASSOCIATE_LINK_TRANSITIVE,
//  }
//  
//  /**
//   * @effects load all objects of a domain class satisfying a query and also load the objects of 
//   * the <b>directly</b> associated domain classes.
//   * 
//   * <p>if exist such objects
//   *  return them as a Collection
//   * else
//   *  return null
//   * 
//   * @requires <code>c</code> has been registered in <code>this</code>
//   * @modifies <code>this.classExts.get(c)</code>
//   * 
//   * @version 2.6.4.a
//   */
//  //TODO: NOT COMPLETED YET! 
//  public Collection loadObjects(Class c, Query query, LoadPolicy loadPolicy) throws DBException {
////    if (objects.isEmpty()) {
//    Collection newObjects = null;
//
//      if (isAbstract(c)) {
//        // load objects of sub-classes that satisfy query 
//        Class[] subs = getSubClasses(c);
//        if (subs != null) {
//          for (Class sub : subs) {
//            Collection subObjs = loadObjects(sub, query, loadPolicy);
//            if (subObjs != null) {
//              if (newObjects == null) newObjects = new LinkedList();
//              newObjects.addAll(subObjs);
//            }
//          }
//          
//          // try getting the subclass objects after load
//          //objects = getObjects(c);
//          
//          if (!newObjects.isEmpty()) {
//            // notify listeners
//            fireStateChanged(c, newObjects, LAName.New);
//          }
//        }
//      } else { // c is not a abstract class
//        if (debug)
//          System.out.println("loading from db: " + c.getSimpleName());
//
//        // find any objects in the pool that satisfy query
//        Collection pooledObjects = getObjects(c, query); 
//        
//        // load from database c's objects that satisfy query and not already in the object pool
//        //TODO: fix this
//        //newObjects = dbt.readObjects(c, pooledObjects, query, loadPolicy);
//        
//        if (newObjects != null) {
//          // must use this loop to update the object tree
//          Class actualCls;
//          for (Object o : newObjects) {
//            actualCls = o.getClass();
//            addObject(actualCls, o, false); // serialised = false
//          }
//
//          // notify listeners
//          fireStateChanged(c, newObjects, LAName.New);
//        }
//      }
////    }
//
//      return newObjects;
////    if (query == null) {
////      return objects;
////    } else {
////      return getFilteredObjects(c, query);
////    }
//  }



  /**
   * @effects returns the <code>List</code> of all the domain objects of the
   *          domain class <code>c</code> in the object pool or <code>null</code> 
   *          if if no objects can be found.
   *          <p>
   *          Throws <code>NotPossibleException</code> if <tt>c</tt> is not registered in this
   * 
   */
  public Collection getObjects(Class c) throws NotFoundException,
      NotPossibleException {
    return getObjects(c, null);
  }

  /**
   * @effects
   *  if object pool of c in this is empty
   *    return null
   *  else 
   *    return an <tt>Iterator</tt> of the object pool of <tt>c</tt>, 
   *    each element of which is an <tt>Entry<Oid,Object></tt>,
   *  
   */
  public Iterator<Entry<Oid, Object>> getObjectIterator(Class c) {
    if (isEmptyExtent(c))
      return null;
    else
      return classExts.get(c).entrySet().iterator();
  }

  /**
   * @effects 
   *  return the object of <tt>c</tt> whose id is <tt>id</tt> or <tt>null</tt> if no such object is found
   * @version 3.0:
   *  change header to use generic
   */
  // v3.0: public Object getObject(Class c, Oid id) {
  public <T> T getObject(Class<T> c, Oid id) {
    IdObjectMap<Oid,Object> objs = classExts.get(c);
    if (objs == null) {
      return null;
    } else {
      return (T) objs.get(id);
    }
  }
  
  /**
   * This method is a short-cut for {@link #getObjects(Class, Query)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attribName</tt> is the name of a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute named <tt>attribName</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return such first object
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attribName</tt> does not match any of the domain attributes of <tt>c</tt>; 
   *  NotPossibleException if <tt>c</tt> is not a valid domain class 
   */
  public <T> T getObject(Class<T> c, String attribName, Op op, Object attribVal) throws NotPossibleException, NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Collection objects= getObjects(c, q);
    
    if (objects != null) {
      return (T) objects.iterator().next();
    } else {
      return null;
    }
  }

  /**
   * This method is a short-cut for {@link #getObjects(Class, DomainConstraint, Op, Object))} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attribName</tt> is the name of a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute named <tt>attribName</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return <b>all</b> such objects
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attribName</tt> does not match any of the domain attributes of <tt>c</tt> 
   */
  public <T> Collection<T> getObjects(Class<T> c, String attribName, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    /*v2.8: use method 
    //System.out.println(getObjects(c));
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Collection objects= getObjects(c, q);
    
    if (objects != null) {
      return (Collection<T>) objects;
    } else {
      return null;
    }
    */
    return getObjects(c, attrib, op, attribVal);
  }
  
  /**
   * This method is a short-cut for {@link #getObjects(Class, Query)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attrib</tt> is a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute <tt>attrib</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return <b>all</b> such objects
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attrib</tt> does not match any of the domain attributes of <tt>c</tt>
   *  
   * @version 2.8
   */
  public <T> Collection<T> getObjects(Class<T> c, DAttr attrib, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    //System.out.println(getObjects(c));
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Collection objects= getObjects(c, q);
    
    if (objects != null) {
      return (Collection<T>) objects;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if there are objects of c already loaded in this
   *    return the number of such objects
   *  else if no objects of c loaded or c does not have any objects
   *    return 0
   *  else (e.g. fails to count objects of c for some reasons)
   *    return -1
   */
  public int getObjectCount(Class c) {
    try {
      Collection objs = getObjects(c);
      if (objs != null) {
        return objs.size();
      } else {
        return 0;
      }
    } catch (Exception e) {
      return -1;
    }
  }
  
  /**
   * @effects 
   *  if there are no objects of domain class c in this 
   *    return true
   *  else
   *    return false
   */
  public boolean isEmptyExtent(Class c) {
    return (getObjectCount(c) <= 0);
  }
  
  /**
   * A specialised version of {@link #getObjects(Class, Query)} which works for the associated objects
   * of a given object.
   * 
   * @requires 
   *  c != null /\ c is a domain class in this /\ 
   *  linkedObj != null /\  linkedObjCls = linkedObj.class /\ 
   *  assocName != null /\ assocName is the name of c's end of an association with linkedObjCls
   *   
   * @effects <pre>
   *  if exists in the object pool of c that are associated 
   *  to linkedObj via the association named <tt>assocName</tt> 
   *    <b>retrieve</b> these objects 
   *    add association linkes from linkedObj to these objects
   *    
   *  <br>return the retrieved objects as Map or return null if no such objects (or no association 
   *  named <tt>assocName</tt>) exist.</pre>
   * @version 
   *  - 5.1
   */
  public <T> Map<Oid,T> getAssociatedObjects(Object linkedObj, Class linkedObjCls, Class<T> c, String assocName) throws DataSourceException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(c, assocName, linkedObjCls);

    Map<Oid,T> result = null;

    if (assocTuple != null) {
      Collection<Entry<Oid,Object>> objs = classExts.get(c).entrySet();
      final String attribName = assocTuple.getFirst().name();
      
      for (Entry<Oid,Object> e : objs) {
        Oid oid = e.getKey();
        T obj = (T) e.getValue();
        Object attribVal = dsm.getAttributeValue(obj, attribName);
        if (attribVal != null && attribVal == linkedObj) {
          // found one
          if (result == null) result = new LinkedHashMap<>();
          result.put(oid, obj);
        }
      }

      // if we get here then no objects found matching c, perhaps it is abtract
      // try the sub-classes
      Entry<Oid,Object> e;
      Class[] subClasses = dsm.getSubClasses(c);
      if (subClasses != null) {
        for (Class sub : subClasses) {
          result = getAssociatedObjects(linkedObj, linkedObjCls, sub, assocName);
          if (result != null) {
            return result;
          }
        }
      }
    } 
    
    return result;
  }
  
  /**
   * @effects returns the <code>List</code> of the domain objects of the domain
   *          class <code>c</code> in the object pool that satisfy an object expression
   *          <code>exp</code>, or <code>null</code> if no objects can be found.
   * 
   *          <p>
   *          Throws <code>NotFoundException</code> if an attribute does not
   *          exist for an expression or <code>NotPossibleException</code> if 
   *          <tt>c</tt> is not registered in this OR an
   *          error occurred while retrieving the value of an attribute that
   *          matches an expression.
   */
  public Collection getObjects(Class c, Query query) throws NotFoundException,
      NotPossibleException {
    // return classExts.get(c);
    // v2.7.3: added this check 
    if (!isRegistered(c)) 
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED, c);
    
    if (query == null) {
      // TODO: to return null if empty (? but this may affect other operations who rely on the collection being returned 
      // even if it is empty)
      return classExts.get(c).values(); 
//      Collection objs = classExts.get(c).values();
//      if (objs == null || objs.isEmpty())
//        return null;
//      else
//        return objs;
    } else {
      Collection filtered = getFilteredObjects(c, query);
      if (filtered != null) {
        return filtered;
      } else {
        return null;
      }
    }
  }

  /**
   * @effects returns a <code>List</code> of the domain objects of the class
   *          <code>c</code> that satisfy the <code>Query</code>
   *          <code>query</code>; throws <code>NotFoundException</code> if an
   *          attribute does not exist for an expression or
   *          <code>NotPossibleException</code> if an error occurred while
   *          retrieving the value of an attribute that matches an expression.
   * 
   * @requires <code>getObjects(c) != null</code>
   * @see #getObjects(Class)
   */
  protected Collection getFilteredObjects(Class c, Query query)
      throws NotFoundException, NotPossibleException {
    //Object attributeVal;
    Collection objects = getObjects(c);
    Collection filteredObjects = new ArrayList();
    try {
      for (Object o : objects) {
        if (query.eval(dsm, o))
          filteredObjects.add(o);
      }
    } catch (ConcurrentModificationException e) {
      System.err.printf("Error in DomainSchema.getFilteredObject(class={%s},query={%s})%n %s", c, query, e.getClass());
      e.printStackTrace();
    }
    return (!filteredObjects.isEmpty()) ? filteredObjects : null;
  }

  /**
   * @effects 
   *  <pre>
   *    if exists domain objects of c in objCol that satisfy query
   *      return a Map of them (with the object ids as keys), which contains objects in 
   *      the same order as they appear in objCol
   *    else 
   *      return null
   *      
   *    Throws NotPossibleException if failed to evaluate query
   * </pre>
   * 
   * @requires c != null /\ objCol != null /\ query != null /\ 
   * objCol is not modified during call 
   */
  public <T> Map<Oid,T> getFilteredObjectsFrom(Class<T> c, Collection<T> objCol, Query query) 
      throws NotPossibleException {
    //Object attributeVal;
    Map<Oid,T> filteredObjects = new LinkedHashMap<>();
//    try {
    Oid id;
    for (T o : objCol) {
      if (query.eval(dsm, o)) {
        // found a match
        id = lookUpObjectId(c, o);
        filteredObjects.put(id, o);
      }
    }
//    } catch (ConcurrentModificationException e) {
//      System.err.printf("DomainSchema.getFilteredObject(class={%s},query={%s})%n %s", c, query, e.getClass());
//      e.printStackTrace();
//    }
    return (!filteredObjects.isEmpty()) ? filteredObjects : null;
  }

  /**
   * @requires c != null /\ c is registered in this
   * @effects 
   *  retrieve and return from <tt>c</tt>'s object pool the {@link Oid}s of objects satisfy <tt>query</tt>
   *  or of all objects if <tt>query is null</tt>; or return <tt>null</tt> if no such objects are found
   * @version 3.0
   */
  public <T> Collection<Oid> getObjectOids(Class<T> c, Query query) {
    if (!isRegistered(c))
      return null;
    
    Map<Oid,T> objects = (Map<Oid,T>) classExts.get(c);
    
    Collection<Oid> filteredOids = new ArrayList();
    try {
      Oid id;
      T o;
      for (Entry<Oid,T>  e: objects.entrySet()) {
        id = e.getKey();
        o = e.getValue();
        if (query == null || query.eval(dsm, o))
          filteredOids.add(id);
      }
    } catch (ConcurrentModificationException e) {
      System.err.printf("Error in DomainSchema.getFilteredObject(class={%s},query={%s})%n %s", c, query, e.getClass());
      e.printStackTrace();
    }
    
    return (!filteredOids.isEmpty()) ? filteredOids : null;
  }
  
  /**
   * This method is a short-cut for {@link #getObjects(Class, Query)} where the specific search criteria for 
   * the objects can be specified via one attribute.  
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class /\ 
   *  <tt>attrib</tt> is a valid domain attribute of <tt>c</tt>
   *  
   * @effects 
   *  if exists objects of <tt>c</tt> the value of attribute <tt>attrib</tt> satisfies 
   *  <tt>op attribVal</tt>
   *    return <b>all</b> such objects
   *  else
   *    return null
   *    
   *  <p>throws NotFoundException if <tt>attrib</tt> does not match any of the domain attributes of <tt>c</tt>
   *  
   * @version 2.8
   */
  public <T> Map<Oid,T> getObjectsMap(Class<T> c, DAttr attrib, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    //System.out.println(getObjects(c));
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    return getObjectsMap(c, q);
  }
  
  /**
   * @effects
   *  if <tt>c</tt> is not registered 
   *    throws NotPossibleException
   *  else 
   *    get from <tt>c</tt>'s object pool those that satisfy <tt>query</tt> (if specified) and 
   *    return a <tt>Map</tt> of these, whose keys are the objects' ids;
   *  OR return <tt>null</tt> if no such objects exist.
   *  
   * @version 
   *  - 2.8<br>
   *  - 3.0: support sorting
   */
  public <T> Map<Oid,T> getObjectsMap(Class<T> c, Query query) throws NotPossibleException {
    /*v3.0: support sorting
    if (!isRegistered(c)) 
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED, c);
    
    if (query == null)
      return (Map<Oid,T>) classExts.get(c); 
    else {
      return getFilteredObjectsMap(c, query);
    }
    */
    return getObjectsMap(c, query, null);
  }
  
  /**
   * @effects
   *  if <tt>c</tt> is not registered 
   *    throws NotPossibleException
   *  else 
   *    get from <tt>c</tt>'s object pool those that satisfy <tt>query</tt> (if specified) and 
   *    return a <tt>Map</tt> of these, whose keys are the objects' ids and that are sorted by <tt>comparator</tt> (if specified);
   *  OR return <tt>null</tt> if no such objects exist.
   *  
   * @version 
   *  - 2.8<br>
   *  - 3.0: support sorting
   */
  public <T> Map<Oid,T> getObjectsMap(Class<T> c, Query query, ObjectComparator comparator) throws NotPossibleException {
    if (!isRegistered(c)) 
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED, c);
    
    Map<Oid,T> objects = (Map<Oid,T>) classExts.get(c);

    LinkedHashMap<Oid,T> filteredObjects = new LinkedHashMap<>();
    Oid id;
    T o;
    
    // v3.0: support sorting: if comparator is specified then sort objects first, then add them to objMap
    ObjectMapSorter sorter = null;
    if (comparator != null)
      sorter = new ObjectMapSorter(comparator);

    try {
      for (Entry<Oid,T>  e: objects.entrySet()) {
        id = e.getKey();
        o = e.getValue();
      
        if (query == null || query.eval(dsm, o)) {
          // o satisfies condition(s)
          if (sorter != null)
            sorter.put(id,o);
          else
            filteredObjects.put(id, o);
        }
      }
    } catch (ConcurrentModificationException e) {
      System.err.printf("Error in DomainSchema.getFilteredObject(class={%s},query={%s})%n %s", c, query, e.getClass());
      e.printStackTrace();
    }
    
    /*v3.0: support sorting */
    if (sorter != null) {
      sorter.copyTo(filteredObjects);
    }
    
    return (!filteredObjects.isEmpty()) ? filteredObjects : null;
  }
  
  // v3.0: moved into getObjectsMap above
//  /**
//   * @requires 
//   *  c is registered in this /\ query != null
//   *  
//   * @effects 
//   *  get from <tt>c</tt>'s object pool those that satisfy <tt>query</tt> and 
//   *  return a <tt>Map</tt> of these, whose keys are the objects' ids;
//   *  OR return <tt>null</tt> if no such objects exist.
//   *  
//   * @version 2.8
//   */
//  protected <T> Map<Oid,T> getFilteredObjectsMap(Class<T> c, Query query)
//      throws NotFoundException, NotPossibleException {
//    Map<Oid,T> objects = (Map<Oid,T>) classExts.get(c);
//    Map<Oid,T> filteredObjects = new LinkedHashMap<>();
//    try {
//      T o;
//      for (Entry<Oid,T>  e: objects.entrySet()) {
//        o = e.getValue();
//        if (query.eval(dsm, o))
//          filteredObjects.put(e.getKey(), o);
//      }
//    } catch (ConcurrentModificationException e) {
//      System.err.printf("Error in DomainSchema.getFilteredObject(class={%s},query={%s})%n %s", c, query, e.getClass());
//      e.printStackTrace();
//    }
//    return (!filteredObjects.isEmpty()) ? filteredObjects : null;
//  }
  
  /**
   * @effects delete all domain objects from the memory extent of the domain
   *          class <code>c</code>, and from those of all the non-Object
   *          super-classes of <code>c</code>. If
   *          <code>isTransient(c) = false</code> then also delete
   *          <code>objects</code> from the database table of <code>c</code>
   *          (but keeps <code>c</code> in <code>this</code>).
   */
//  public void deleteObjects(Class c) throws DBException {
//    // first try to remove from the db
//    if (dbt != null && !isTransient(c))
//      dbt.delete(c);
//
//    // remove objects of c from the super classes
//    List objects = classExts.get(c);
//    if (!objects.isEmpty()) {
//      // remove objects of c from all its super classes (if any)
//      Class sup = getSuperClass(c);
//      if (sup != null) {
//        deleteObjectsFrom(objects, sup);
//      }
//    }
//    objects.clear();
//
//    // notify listeners
//    fireStateChanged(c, objects);
//  }
  public void deleteObjects(Class c) throws DataSourceException {
    deleteObjects(c,true);
  }
  
  /**
   * @effects delete all domain objects from the memory extent of the domain
   *          class <code>c</code>, and from those of all the non-Object
   *          super-classes of <code>c</code>. If
   *          <code>fromDb = true</code> then also delete
   *          <code>objects</code> from the database table of <code>c</code>
   *          (but keeps <code>c</code> registered in <code>this</code>).
   */
  public void deleteObjects(Class c, boolean fromDB) throws DataSourceException {
    // first try to remove from the db
    if (fromDB && osm != null && !isTransient(c))
      osm.deleteObjects(c);

    // remove objects of c from the super classes
  //List objects = classExts.get(c);
    Map<Oid,Object> objects = classExts.get(c);
    
    if (objects != null) {  // v2.7.3: added this check
      if (!objects.isEmpty()) {
        // remove objects of c from all its super classes (if any)
        Class sup = dsm.getSuperClass(c);
        if (sup != null) {
          deleteObjectsFrom(objects, sup, fromDB);
        }
      }
      objects.clear();
      
      // notify listeners
      fireStateChanged(c, objects.values(), LAName.Delete);
    }

  }
  
  /**
   * @effects if <code>c</code> is not <code>Object</code> then deletes the
   *          domain objects <code>objects</code> from <code>c</code>, and
   *          recursively from the super and ancestor domain classes of
   *          <code>c</code> (if any).
   */
  private void deleteObjectsFrom(final Map<Oid,Object> objects, Class c, boolean fromDB)
      throws DataSourceException {

    //List cObjects = classExts.get(c);
    Map<Oid, Object> cObjects = classExts.get(c);
    
    Oid oid;
    Object o;
    boolean idStateObsolete = false;
    
    for (Entry<Oid,Object> entry: objects.entrySet()) {
      oid = entry.getKey();
      o = entry.getValue();
      if (fromDB && osm != null && !isTransient(c))
        osm.deleteObject(c, o);

      //cObjects.remove(o);
      try {
        cObjects.remove(oid);
      } catch (ObsoleteStateSignal e) {
        if (fromDB) // only cares if removing object from db
          idStateObsolete = true;
      }
    }

    // reload metadata if id state is obsolete because of the removals
    if (idStateObsolete)
      updateMetadata(c); //loadMetadata(c);
    
    Class sup = dsm.getSuperClass(c);
    if (sup != null)
      deleteObjectsFrom(objects, sup, fromDB);
  }
  
//  /**
//   * @effects if <code>c</code> is not <code>Object</code> then deletes the
//   *          domain objects <code>objects</code> from <code>c</code>, and
//   *          recursively from the super and ancestor domain classes of
//   *          <code>c</code> (if any).
//   */
//  private void deleteObjectsFrom(final List objects, Class c, boolean fromDB)
//      throws DBException {
//
//    List cObjects = classExts.get(c);
//    for (Object o : objects) {
//      if (fromDB && dbt != null && !isTransient(c))
//        dbt.deleteObject(c, o);
//
//      cObjects.remove(o);
//    }
//
//    Class sup = getSuperClass(c);
//    if (sup != null)
//      deleteObjectsFrom(objects, sup, fromDB);
//  }

  /**
   * @effects 
   *  if exist in the <b>object pool</b> of the domain class <tt>c</tt> an object <tt>o</tt> the values of whose identifier attributes are
   *  <tt>idVals</tt> 
   *    remove <code>o</code> from the object pool of, and if c is serialisable then also from the data store of, <code>c</code> and recursively from those of the super and
   *    ancestor domain classes of <code>c</code>. Throws
   *    <code>DataSourceException</code> if failed to remove object from the database.
   *  else
   *    do nothing
   */
  public void deleteObject(Class c, Object[] idVals) throws DataSourceException {
    Object obj = lookUpObjectByID(c, idVals);
    if (obj != null)
      deleteObject(obj, c);
  }

  /**
   * This method only delete objects that are found in the <b>object pool</b>
   * 
   * @effects 
   *    if exist in the <b>object pool</b> of the domain class <tt>c</tt> objects satisfying query <tt>q</tt>
   *      remove them from the object pool of, and if c is serialisable then also from the data store of, <tt>c</tt> and recursively from those of the super and
   *      ancestor domain classes of <tt>c</tt> in <tt>this</tt>. 
   *      Throws <tt>DataSourceException</tt> if failed to remove object from the
   *      database.
   *      
   *      <br>Return the removed objects.
   *    else
   *      do nothing
   */
  public Collection deleteObjects(Class c, Query q) throws DataSourceException {
    Collection objects = getObjects(c, q);
    if (objects != null) {
      for (Object o : objects) 
        deleteObject(o, c);
    }
    
    return objects;
  }
  
  /**
   * This method differs from {@link #deleteObjects(Class, Query)} in that if the domain class <tt>c</tt> is serialisable 
   * then it deletes <b>All</b> objects in the data store of <tt>c</tt> that satisfy the query.
   * 
   * @effects <pre> 
   *    if <tt>c</tt> is serialisable 
   *      delete from the <b>object pool and data store</b> of <tt>c</tt> the objects satisfying <tt>q</tt>, 
   *      and recursively remove them from those of the super and
   *      ancestor domain classes of <tt>c</tt> in <tt>this</tt>.
   *    else  
   *      delete from the <b>object pool</b> of <tt>c</tt> the objects satisfying <tt>q</tt>, 
   *      and recursively remove them from those of the super and
   *      ancestor domain classes of <tt>c</tt> in <tt>this</tt>.
   *    
   *    <br>Return the removed objects.
   *    </pre>
   *    <p>Throws <tt>DataSourceException</tt> if failed to remove object from the
   *      database.
   *    
   * @version 3.3
   */
  public void deleteObjectsDS(Class c, Query q) throws DataSourceException {
    if (osm != null && !isTransient(c)) {
      // delete in the data store
      osm.deleteObjects(c, q);
    } else {
      // delete only in the object pool
      deleteObjects(c, q);
    }
  }

  /**
   * @effects removes <code>o</code> from the object pool of, and if c is serialisable then also the data store of, <code>c</code> and recursively from
   *          those of the super and ancestor domain classes of <code>c</code>.
   *          Throws <code>DataSourceException</code> if failed to remove object from
   *          the database.
   * @requires <code>o!=null</code> and <code>o</code> is an object of
   *           <code>c</code>
   * @deprecated as of 2.6.4.a (use {@link #deleteObject(Object, Oid, Class)} instead)
   */
  public void deleteObject(Object o, Class c) throws DataSourceException {
 // delete o from c first
    if (osm != null && !isTransient(c)) {
      // delete from database
      osm.deleteObject(c, o);
    }
    
    Map<Oid,Object> objs = classExts.get(c);
    Oid oid = genObjectId(c, o);
    /* v3.3: handle thrown signal
    objs.remove(oid);
    */
    try {
      objs.remove(oid);
    } catch (ObsoleteStateSignal e) {
      // id-range is in an invalid state -> reload
      updateMetadata(c);
    }
    
    // notify listeners
    fireStateChanged(c, o, LAName.Delete);

    // then from all the super and ancestor domain classes of c (if any)
    Class sup = dsm.getSuperClass(c);
    if (sup != null) {
      deleteObject(o, sup);
    }
  }
  
  /**
   * @effects removes <code>o</code> whose id is <tt>oid</tt> 
   *          from the object pool of, and if c is serialisable then also the data store of, <code>c</code> and recursively from
   *          those of the super and ancestor domain classes of <code>c</code>.
   *          Throws <code>DataSourceException</code> if failed to remove object from
   *          the database.
   * @requires <code>o!=null</code> and <code>o</code> is an object of
   *           <code>c</code>
   */
  public void deleteObject(Object o, Oid oid, Class c) throws DataSourceException {
    // delete o from c first
    if (osm != null && !isTransient(c)) {
      // delete from database
      osm.deleteObject(c, o);
    }
    
    //List objs = classExts.get(c);
    //objs.remove(o);
    Map<Oid,Object> objPool = classExts.get(c);
    //Oid oid = getObjectId(c, o);
    try {
      objPool.remove(oid);
    } catch (ObsoleteStateSignal e) {
      // id-range is in an invalid state -> reload
      //loadMetadata(c);
      updateMetadata(c);
    }
    
    // notify listeners
    fireStateChanged(c, o, LAName.Delete);

    // then from all the super and ancestor domain classes of c (if any)
    Class sup = dsm.getSuperClass(c);
    if (sup != null) {
      deleteObject(o, oid, sup);
    }
  }
  
  /**
   * @effects displays the content of this schema out on the command line
   * @modifies <code>System.out</code>
   */
  public void listSchema() {
//    System.out.println("CLASS DEFINITIONS");
//    System.out.println("=========================================");
//    System.out.println(classDefs);
//    System.out.println("CLASS CONSTRAINTS");
//    System.out.println("=========================================");
//    System.out.println(classConstraints);
    System.out.println("Domain model instances");
    System.out.println("=========================================");
//    System.out.println(classExts);
    for (Entry<Class, IdObjectMap<Oid, Object>> e : classExts.entrySet()) {
      Class cls = e.getKey();
      boolean isMaterialised = isCreatedInDataSource(cls);
      System.out.printf("-> %s: %n...isMaterialised?: %b%n...Object pool: %s%n(FQN: %s)%n",
          cls.getSimpleName(),
          isMaterialised,
          e.getValue().values(),
          cls
          );
    }
  }

  public void listSchema(boolean displayFqn) {
  //  System.out.println("CLASS DEFINITIONS");
  //  System.out.println("=========================================");
  //  System.out.println(classDefs);
  //  System.out.println("CLASS CONSTRAINTS");
  //  System.out.println("=========================================");
  //  System.out.println(classConstraints);
    System.out.println("Domain model instances");
    System.out.println("=========================================");
  //  System.out.println(classExts);
    String format = "-> %s: %n...isMaterialised?: %b%n...Object pool: %s%n";
    if (displayFqn) {
      format += "(FQN: %s)%n";
    } 
    for (Entry<Class, IdObjectMap<Oid, Object>> e : classExts.entrySet()) {
      Class cls = e.getKey();
      boolean isMaterialised = isCreatedInDataSource(cls);
      System.out.printf(format,
          cls.getSimpleName(),
          isMaterialised,
          e.getValue().values(),
          cls
          );
    }
  }
  
  /**
   * 
   * @effects 
   *  if the underlying data source schema named <tt>dsSchema</tt> exists
   *    list contents of all data stores in it
   *  else
   *    do nothing
   * @version 5.4
   */
  public void listMaterialisedSchema(String dsSchema) {
    osm.printDataSourceSchema(dsSchema);
  }
  
  /**
   * Displays the data objects of this schema stored in the database on the
   * command line.
   * 
   * @modifies <code>System.out</code>
   */
  public void listData(Class c, boolean fromDB) throws DataSourceException {
    if (fromDB) {
      if (osm != null)
        osm.print(c);
    } else {
      System.out.println("Class: " + c.getName());
      IdObjectMap<Oid, Object> ext = classExts.get(c);
      Collection objects = (ext != null) ? ext.values() : null;
      if (objects == null || objects.isEmpty()) {
        System.out.println("(empty)");
      } else {
        for (Object o : objects) {
          System.out.println("   " + o);
        }
      }
    }
  }

  /**
   * @requires 
   *  c is a registered domain class /\ 
   *  idAttrib is an id-attribute of c /\ 
   *  idVal is the value of idAttrib of an object
   *  
   * @effects 
   *  generate and return the <tt>Oid</tt> of a (not yet loaded) object of 
   *  <tt>c</tt> whose id attribute is <tt>idAttrib</tt> and value is <tt>idVal</tt>  
   */
  public Oid genObjectId(Class c, DAttr idAttrib, 
      Comparable idVal) {
    Oid oid = new Oid(c);
    oid.addIdValue(idAttrib, idVal);
    
    return oid;
  }
  
  /**
   * @effects 
   *  generate and return an <tt>Oid</tt> from the value(s) of the id attribute(s) of <tt>o</tt>
   *  
   *  <br>throws <tt>NotFoundException</tt> if <tt>o</tt> does not
   *  have any id attributes or <tt>NotPossibleException</tt> if
   *          could not get their value(s) or id values are not valid.
   *          
   * @requires <tt>o</tt> has id attribute(s), i.e. their
   *           <tt>DomainConstraint.id</tt> fields are set to
   *           <tt>true</tt>; and getter methods for them are defined.
   */
  public Oid genObjectId(Class c, Object o) throws 
  NotFoundException, NotPossibleException {
    Collection<DAttr> domainAttrs = dsm.getDomainConstraints(c); //classConstraints.get(c);

    Oid oid = new Oid(c);
    boolean hasId = false;
    Object idVal;
    if (domainAttrs != null) {
      for (DAttr d : domainAttrs) {
        if (d.id()) {
          // found
          if (!hasId) hasId = true;
          //idVals.add(getAttributeValue(o, d.name()));
          idVal = dsm.getAttributeValue(o, d.name());
          
          if (!(idVal instanceof Comparable))
            throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
                new Object[] {c.getSimpleName(),o,idVal});
          
          oid.addIdValue(d, (Comparable)idVal);
        }
      }
    }

    if (hasId) {
      return oid;
    } else {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_ID_NOT_FOUND,
          "Không tìm thấy thuộc tính dạng mã trong lớp {1}", "", o.getClass());
    }
  }

  /**
   * @requires 
   *  cls != null /\ cls is a domain class in this /\ 
   *  o != null /\ o is a domain object of cls
   * @effects 
   *  return the object id of o that is kept in the object pool of cls
   *  or return <tt>null</tt> if the object pool is empty or object is not yet placed in the object pool
   * 
   * @version 
   *  2.6.4b added support for domain classes that implement the DomainIdable interface
   */
  public Oid lookUpObjectId(Class cls, Object o) {
    // v2.6.4b: if o is an instance of DomainIdable, get its id directly
    if (o instanceof DomainIdable) {
      return ((DomainIdable)o).getOid();
    }
    
    Map<Oid,Object> objects = classExts.get(cls);
    
    if (objects != null && !objects.isEmpty()) {
      for (Entry<Oid,Object> entry : objects.entrySet()) {
        if (entry.getValue() == o) {
          return entry.getKey();
        }
      }
    }
    
    return null;
//    throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NOT_FOUND, 
//        "Không tìm thấy mã đối tượng {0}<{1}>", cls.getSimpleName(), o);
  }

  /**
   * @requires 
   *  cls != null /\ cls is a domain class in this /\ 
   *  o != null /\ o is a domain object of cls
   * @effects 
   *  return the object id of o
   * 
   * @version 
   *  2.6.4b added support for domain classes that implement the DomainIdable interface
   */
  public Oid lookUpObjectId(Class cls, DomainIdable o) {
    return o.getOid();
  }
  
  /**
   * @requires 
   *  c is a registered domain class /\ 
   *  idAttribs is a non-empty array of id-attributes of c /\ 
   *  idVals.length = idAttribs.length
   *  
   * @effects 
   *  look up and return the <tt>Oid</tt> of an object of 
   *  <tt>c</tt> matching the specified id attribute(s) and value(s); or
   *  return <tt>null</tt> if no such id is found.
   */
  public Oid lookUpObjectId(Class c, DAttr[] idAttribs, 
      Object[] idVals) {
    Map<Oid,Object> objs = classExts.get(c);
    Collection<Oid> oids = objs.keySet(); 
        
    for (Oid oid: oids) {
      if (oid.equals(idAttribs, idVals)) {
        // match
        return oid;
      }
    }
    
    // perhaps the id belongs to one of the sub-classes
    Oid oid;
    Class[] subClasses = dsm.getSubClasses(c);
    if (subClasses != null) {
      for (Class sub : subClasses) {
        oid = lookUpObjectId(sub, idAttribs, idVals);
        if (oid != null) {
          return oid;
        }
      }
    }

    return null;
  }


  /**
   * @requires targetObj != null /\ attributeType != null
   * 
   * @effects update the value of the linked collection-type attribute of object <tt>targetObj</tt>
   *          whose generic collection type is 
   *          <tt>attributeType</tt> with the new value
   *          <tt>val</tt>; return true if if <tt>targetObj</tt>'s state was changed 
   * 
   *          Throws NotFoundException if attribute is not found;
   *          NotPossibleException if fails to update the attribute
   */
  /*v2.7.2: not used
  public boolean updateCollectionAttributeValue(Object targetObj, Class attributeType,
      Object val) throws NotFoundException, NotPossibleException {
    final Class c = targetObj.getClass();
    Field attribute = getCollectionAttributeByGenericType(c, attributeType);
    
    return updateAssociateLink(targetObj, attribute, val);
  }
  */
  
  /**
   * @requires
   *  obj != null /\ cls != null /\ linkedCls != null /\ linkedObj != null /\ 
   *  obj is an instance of cls /\ 
   *  attrib is in cls /\ attrib implements the association between cls and linkedCls /\ 
   *  linkedObj is an instance of linkedCls 
   *  
   * @effects
   *  update the state of obj whose class is cls caused by linkedObj,
   *    whose class is linkedCls  
   *  if obj was updated
   *    return true
   *  else
   *    return false
   *  
   *  <p>Example: 
   *  Given:
   *  obj=Customer<1>, cls = Customer.class, 
   *  linkedObj=Order<1>
   *  
   *  update the state of obj (e.g. value of attribute Customer.balance) based 
   *  on the (updated) state Order<1> and return true if succeeds or false if otherwise
   */
  public boolean updateAssociateOnUpdate(Object obj, Class cls, 
      DAttr attrib,  // v2.7.2
      Class linkedCls, 
      Object linkedObj) {
    Method updater = null;
    try {
      /*v2.6.4.b:
      Field linkAttributeObj = getCollectionAttributeByType(cls, linkedCls);
      
      if (linkAttributeObj != null) {
        updater = findAttributeValueUpdaterMethod(cls, linkAttributeObj, linkedCls);
        
        if (updater != null) {
          Object updated = updater.invoke(obj, linkedObj);

          if (updated != null && updated instanceof Boolean)
            return (Boolean) updated;
          else
            return false;
        }
      } else {
        // attribute not found
        // should not happen
        log(null, "updateObjectByLink",  
            "updater:"+updater, 
            "targetObject:"+obj,
            "value:"+linkedObj,
            "Link attribute of type Collection<"+linkedCls+"> not found in " + cls);
      }
      */
      /*v2.7.2: added linked attribute
      Field attribute = getCollectionAttributeByType(cls, linkedCls);

      String attribName = attribute.getAnnotation(DC).name();
      */
      String attribName = attrib.name();

      updater = dsm.findAttributeValueUpdaterMethod(cls, attribName, linkedCls);
      
      Object updated = updater.invoke(obj, linkedObj);

      if (updated != null && updated instanceof Boolean)
        return (Boolean) updated;
      else
        return false;
    } catch (Exception e) {
      // fail to invoke updater method
      log(e, "updateAssociateOnUpdate",  
          "updater:"+updater, 
          "targetObject:"+obj,
          "value:"+linkedObj);
    }
    
    return false;
  }
  
  /**
   * @deprecated use {@link #updateObject(Object, Map)} instead
   * 
   * @effects if <code>attributeVals != null</code> then sets the domain
   *          attribute ith of <code>o</code> to the element ith of
   *          <code>attributeVals</code>, and if
   *          <code>isTransient(o.class) = false</code> then updates
   *          <code>o</code> in the database table of <code>o.class</code> and
   *          in all the tables of the super and ancester domain classes of this
   *          class.
   * 
   *          <p>
   *          Throws <code>NotPossibleException</code> if could not locate or
   *          could not invoke the corresponding setter method of an attribute.
   *          Throws <code>DBException</code> if errors in updating the object
   *          in the database table.
   * 
   * @requires elements of <code>attributeVals</code> are in the same order as
   *           the declaration order of the domain attributes of
   *           <code>obj</code> and suitable setter methods for these attributes
   *           exist.
   * 
   * @see #getAttributes(Class, Class, boolean)
   */
  public void updateObjectComplete(Object o, Object[] attributeVals)
      throws DataSourceException, NotPossibleException {

    final Class cls = o.getClass();

    if (attributeVals != null) {
      // first update o with the new attribute values
      // get the domain attributes of obj
      Map<Field,DAttr> fields = dsm.getDomainAttributes(cls); // classDefs.get(cls);

      Field f = null;
      Method setter;
      //DAttr dc;
      Object val = null;
      // TODO: (below) keep track of which attributes were changed so that we
      // only
      // update those attributes in the relevant database table(s)
      //try {
        int index = 0, i = -1;
        //for (int i = 0; i < fields.size(); i++) {
        for (Entry<Field,DAttr> entry : fields.entrySet()) {
          try {
            f = entry.getKey(); DAttr dc = entry.getValue();
            //f = (Field) fields.get(i);
            i++;
            //dc = (DAttr) f.getAnnotation(DC);
            // only update if this field is mutable
            if (dc.mutable()) {
              val = attributeVals[index++];
              setter = dsm.findSetterMethod(f, cls);
              setter.invoke(o, val);
            }
          } catch (Exception e) {
            throw new NotPossibleException(
                NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
                //"Không thể thực thi phương thức: {0}.{1}({2},{3})", 
                new Object[] {o,
                "updateObject", ((f != null) ? f.getName() : null), val});
          }
        }
//      } catch (Exception e) {
//        throw new NotPossibleException(
//            NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
//            //"Không thể thực thi phương thức: {0}.{1}({2},{3})", 
//            new Object[] {o,
//            "updateObject", ((f != null) ? f.getName() : null), val});
//      }
    }

    // update the related database table(s)
    // TODO: if oldVals is not empty then update only the changed attributes
    if (!isTransient(cls))
      updateObjectIn(o, cls);
  }
  
  /**
   * This method works exactly the same as {@link #addAssociateLink(Object, DAttr, Object)} 
   * except that the association ends are swapped.
   *  
   * @effects 
   *  update <tt>associate</tt> to record a new association link to <tt>uptObject</tt> that is 
   *  linked via the attribute <tt>attrib</tt> of <tt>uptObject.class</tt>.
   *  
   *  <p>If <tt>associate</tt> was changed
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   *  
   *  <p>Throws NotFoundException if could not find the corresponding association between 
   *  the two domain classes
   */
  public boolean addLinkToAssociate(Object uptObject, DAttr attrib, Object associate) 
  throws NotFoundException
  {
    Class ucls = uptObject.getClass();
    Class aCls = associate.getClass();
    
    // update 
    // find the attribute a1 of associate whtribute-update method on <tt>associate</tt> passing in <tt>updateObj</tt> as argumentose type is ucls
    /*v2.7.2: to use association 
    Field refAttrib = getCollectionAttributeByType(aCls, ucls);
    */
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getTargetAssociation(ucls, attrib);
    DAttr yourAttrib = assocTuple.getFirst();
    Field refAttrib = dsm.getDomainAttribute(aCls, yourAttrib); 
        
    /* v2.7.2 
    if (refAttrib != null) {
      return updateAssociateLink(associate, refAttrib, uptObject);
    } else {
      return false;
    }
    */
    
    /*v2.7.3: use addAssociationLink 
    return updateAssociateLink(associate, refAttrib, uptObject);
    */
    return addAssociateLink(associate, refAttrib, uptObject);
  }
  
  /**
   * @effects 
   *  invoke {@link #addAssociateLink(Object, Field, Object)}, where 
   *  <tt>Field</tt> is the domain field of <tt>targetObj.class</tt> whose domain constraint
   *  is <tt>attrib</tt>
   */
  public boolean addAssociateLink(Object targetObj,
      DAttr attrib, Object val) {
    final Class c = targetObj.getClass();
    Field attribute = dsm.getDomainAttribute(c, attrib);
    
    return addAssociateLink(targetObj, attribute, val);
  }
  
  /**
   * Updates the value of an attribute of a domain object to add a new association link 
   * to another domain object. This method supports
   * both normal and collection-type attributes. This is more general than the
   * {@link #setAttributeValue(Object, String, Object)} method.
   * 
   * <p>This method works similar to {@link #updateAssociateLink(Object, String, Object)} except 
   * that uses a different type of adder method to add the link. This type of adder method 
   * supports a desirable side-effect of updating the state of the target object (e.g. values of the 
   * derived attributes).
   * 
   * @effects if <code>attributeName</code> is a normal attribute then invokes
   *            the setter method of this attribute passing in <code>val</code> as
   *            an argument, 
   *          else 
   *            invokes adder method of this attribute to add 
   *            <code>val</code> to the collection.
   * @requires 
   *    targetObj != null /\ attributeName != null /\ 
   *    suitable setter or adder method for <code>attributeName</code> is
   *           defined in <code>targetObj.class</code>
   * @version 2.7.3
   */
  public boolean addAssociateLink(Object targetObj, String attributeName,
      Object val) throws NotFoundException, NotPossibleException {
    final Class c = targetObj.getClass();
    Field attribute = dsm.getDomainAttribute(c, attributeName);
    return addAssociateLink(targetObj, attribute, val);
  }
  
  /**
   * This method works similar to {@link #updateAssociateLink(Object, Field, Object)} except 
   * that uses a different type of adder method to add the link. This type of adder method 
   * supports a desirable side-effect of updating the state of the target object (e.g. values of the 
   * derived attributes).
   * 
   * @requires 
   *  targetObj != null /\ attribute != null /\ 
   *  suitable setter or adder method for <code>attribute</code> is
   *           defined in <code>targetObj.class</code>
   * @effects if <code>attribute</code> is a normal attribute then invokes
   *            the <b>setter-new</b> method of this attribute passing in <code>val</code> as
   *            an argument, 
   *          else 
   *            invokes <b>adder-new</b> method of this attribute to add 
   *            <code>val</code> to the collection.
   *            
   *            <p>Throws NotFoundException if could not find the suitable method mentioned above, 
   *            NotPossibleException if failed to perform the method
   * @example
   * <b>Example 1:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.city (typed: City)
   * val = City("Hanoi") 
   * -> invoke Customer("a").setNewCity(val)
   * </pre>
   * 
   * <b>Example 2:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.orders (typed: List<Order>)
   * val = Order(1) 
   * -> invoke Customer("a").addNewCustomerOrder(val)
   * </pre>
   * 
   * <b>Example 3:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.orders (typed: List<Order>)
   * val = [Order(1),Order(2)] 
   * -> invoke Customer("a").addNewCustomerOrder(val)
   * </pre>
   * 
   * @version 2.7.3 
   */
  private boolean addAssociateLink(Object targetObj, Field attribute,
      Object val) throws NotFoundException, 
      NotPossibleException 
  {
    final Class c = targetObj.getClass();
    DAttr dc = attribute.getAnnotation(DC);
    
    String attribName = dc.name();
    
    if (dc.type().isCollection()) {
      // collection-type attribute, use adder method
      Method adder;
      // two cases of adder method depending whether or not val
      // is a collection

      if (val instanceof Collection) {
        // add val's elements to the existing collection on the target object
        // ASSUME: interface type of val.type is used as parameter type
        Class colType;
        if (val instanceof List) {
          colType = List.class;
        } else if (val instanceof Set) {
          colType = Set.class;
        } 
        // add other cases here
        else {
          colType = Collection.class;
        }
        adder = dsm.findAttributeValueAdderNewMethod(c, attribName, colType);
      } else {
        // a val to the existing collection on the target object
        Class elementType = dsm.getGenericCollectionType(attribute);
        adder = dsm.findAttributeValueAdderNewMethod(c, attribName, elementType);
      }
      try {
        Object output = adder.invoke(targetObj, val);
        if (output != null && output instanceof Boolean) {
          return ((Boolean)output);
        } else {
          return false;
        }
      } catch (Exception e) {
        // should not happen
        log(e, "addAssociateLink",  
            "adder:"+adder, 
            "targetObject:"+targetObj,
            "value:"+val);
        return false;
      }
    } else {
      // non-collection-type attribute, use setter-new method
      try {
        setNewAssociationLink(targetObj, attribute, val);
      } catch (NotPossibleException e) {
        // should not happen
        log(e, "addAssociateLink",  
            "setter method", 
            "targetObject:"+targetObj,
            "value:"+val);
        return false;
      }
      return true;
    }
  }
  
  /**
   * @requires targetObj != null /\ attributeType != null
   * 
   * @effects update the value of the attribute <tt>attrib</tt> of object <tt>targetObj</tt>
   *          with the value <tt>val</tt>; return true if if <tt>targetObj</tt>'s state was changed 
   * 
   *          Throws NotFoundException if attribute is not found;
   *          NotPossibleException if fails to update the attribute
   */
  public boolean updateAssociateLink(Object targetObj, 
      /*v2.7.2: use attrib 
      Class attributeType,
       */
      DAttr attrib,  
      Object val) throws NotFoundException, NotPossibleException {
    final Class c = targetObj.getClass();
    
    /*v2.7.2
    Field attribute = getDomainAttributeByType(c, attributeType);
     */
    Field attribute = dsm.getDomainAttribute(c, attrib);
    
    return updateAssociateLink(targetObj, attribute, val);
  }
  
  /**
   * Updates the value of an attribute of a domain object. This method supports
   * both normal and collection-type attributes. This is more general than the
   * {@link #setAttributeValue(Object, String, Object)} method.
   * 
   * @effects if <code>attributeName</code> is a normal attribute then invokes
   *            the setter method of this attribute passing in <code>val</code> as
   *            an argument, 
   *          else 
   *            invokes adder method of this attribute to add 
   *            <code>val</code> to the collection.
   * @requires 
   *    targetObj != null /\ attributeName != null /\ 
   *    suitable setter or adder method for <code>attributeName</code> is
   *           defined in <code>targetObj.class</code>
   * @see #setAttributeValue(Object, String, Object)
   */
  public boolean updateAssociateLink(Object targetObj, String attributeName,
      Object val) throws NotFoundException, NotPossibleException {
    final Class c = targetObj.getClass();
    Field attribute = dsm.getDomainAttribute(c, attributeName);
    return updateAssociateLink(targetObj, attribute, val);
  }
  
  /**
   * @requires 
   *  targetObj != null /\ attribute != null /\ 
   *  suitable setter or adder method for <code>attribute</code> is
   *           defined in <code>targetObj.class</code>
   * @effects if <code>attribute</code> is a normal attribute then invokes
   *            the setter method of this attribute passing in <code>val</code> as
   *            an argument, 
   *          else 
   *            invokes adder method of this attribute to add 
   *            <code>val</code> to the collection.
   *            
   *            <p>Throws NotFoundException if could not find the suitable method mentioned above, 
   *            NotPossibleException if failed to perform the method
   * @example
   * <b>Example 1:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.city (typed: City)
   * val = City("Hanoi") 
   * -> invoke Customer("a").setCity(val)
   * </pre>
   * 
   * <b>Example 2:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.orders (typed: List<Order>)
   * val = Order(1) 
   * -> invoke Customer("a").addCustomerOrder(val)
   * </pre>
   * 
   * <b>Example 3:</b>
   * <pre>
   * targetObj = Customer("a")
   * attribute = Customer.orders (typed: List<Order>)
   * val = [Order(1),Order(2)] 
   * -> invoke Customer("a").addCustomerOrder(val)
   * </pre>
   */
  private boolean updateAssociateLink(Object targetObj, Field attribute,
      Object val) throws NotFoundException, 
      NotPossibleException 
  {
    final Class c = targetObj.getClass();
    DAttr dc = attribute.getAnnotation(DC);
    
    // v2.6.4.b: 
    String attribName = dc.name();
    
    // v2.7.4: fix changed
    boolean changed = false;
    
    if (dc.type().isCollection()) {
      // collection-type attribute, use adder method
      Method adder;
      // two cases of adder method depending whether or not val
      // is a collection

      /*v2.7.2: moved down
      // v2.6.4.b:
      Class elementType = getGenericCollectionType(attribute);
      */
      if (val instanceof Collection) {
        // add val's elements to the existing collection on the target object
        // ASSUME: interface type of val.type is used as parameter type
        Class colType;
        if (val instanceof List) {
          colType = List.class;
        } else if (val instanceof Set) {
          colType = Set.class;
        } 
        // add other cases here
        else {
          colType = Collection.class;
        }
        /* v2.6.4.b
        adder = findAttributeValueAdderMethod(c, attribute, colType);
        */
        adder = dsm.findAttributeValueAdderMethod(c, attribName, colType);
        //v3.3: support also use of Collection as parameter type        
        //        try {
        //          // try specific collection type first 
        //          adder = dsm.findAttributeValueAdderMethod(c, attribName, colType);
        //        } catch (NotFoundException e) {
        //          // try using Collection as parameter type
        //          adder = dsm.findAttributeValueAdderMethod(c, attribName, Collection.class);
        //        }
      } else {
        // a val to the existing collection on the target object
        /* v2.6.4b:
        Class elementType = getGenericCollectionType(attribute);
        adder = findAttributeValueAdderMethod(c, attribute, elementType);
        */
        Class elementType = dsm.getGenericCollectionType(attribute);
        adder = dsm.findAttributeValueAdderMethod(c, attribName, elementType);
      }
      try {
        Object output = adder.invoke(targetObj, val);
        if (output != null && output instanceof Boolean) {
          //return ((Boolean)output);
          changed = ((Boolean)output);
        } else {
          //return false;
          changed = false;
        }
      } catch (Exception e) {
        // should not happen
        log(e, "updateAssociateLink",  
            "adder:"+adder, 
            "targetObject:"+targetObj,
            "value:"+val);
        //return false;
        changed = false;
      }
    } else {
      // non-collection-type attribute, use setter method
      try {
        /*v2.7.3: use a better name 
        setAttributeValue(targetObj, attribute, val);
        */
        changed = setAssociationLink(targetObj, attribute, val);
      } catch (NotPossibleException e) {
        // should not happen
        log(e, "updateAssociateLink",  
            "setter method", 
            "targetObject:"+targetObj,
            "value:"+val);
        changed = false;
        //return false;
      }
      //return true;
    }
    
    return changed;
  }
    
  /**
   * @effects 
   *  invoke {@link #setAttributeValue(Object, Field, Object)}
   *  <br>if targetObj is changed
   *    return true
   *  else
   *    return false
   *  
   */
  private boolean setAssociationLink(Object targetObj, Field attribute, Object val) {
    return setAttributeValue(targetObj, attribute, val);
  }

  
  /**
   * This method is equivalent to performing:
   *  {@link #updateAssociateToRemoveLink(Object, DomainConstraint), followed by 
   *  {@link #updateAssociateLink(Object, DAttr, Object)}  for each 
   *  association of <tt>c</tt> with regards to <tt>old</tt>
   * 
   * @effects <pre>
   *    for each object o associated to oldObj via a 1-1 or 1-M association
   *      replace reference(s) to oldObj in o by reloadedObj
   *      
   *    if failed to update an associated object log the error message
   *    </pre>
   * @version 3.0
   */
  protected void updateAssociateLinksOnReload(Object oldObj, Object reloadedObj, Class c) {
    AssociationFilter assocFilter = DSMBasic.AssocFilter;
    assocFilter.reset();  // IMPORTANT: must do this because filter is shared
    assocFilter.addAssociationTypeSpec(AssocType.One2Many, AssocEndType.Many);
    assocFilter.addAssociationTypeSpec(AssocType.One2One, AssocEndType.One);
    
    // retrieve the desired linked associates
    Collection<Associate> associates = getAssociates(oldObj, c, assocFilter);
    
    if (associates != null) {
      for (Associate a : associates) {
        try {
          updateAssociateToReplaceLink(a, oldObj, reloadedObj);
        } catch (NotPossibleException | NotFoundException e) {
          // failed to update this associate
          log(e, "updateAssociateLinksOnReload", "Reloaded obj: " + reloadedObj, "Association: " + a);
        }
      }
    }
    
    // reset filter
    assocFilter.reset();
  }

  /**
   * @requires 
   *  a != null /\ o != null
   *  
   * @effects 
   *  update the associated obj represented by <tt>a</tt> to <b>replace</b> the association link 
   *  to <tt>oldObj</tt> by a link to <tt>newObj</tt>.
   *  
   *  <p>if associated obj is changed return <tt>true</tt>, else return <tt>false</tt>
   *  
   *  <p>throws NotPossibleException if failed to update, NotFoundException if could not find the 
   *  suitable method to perform update
   *  
   * @version 3.0
   * @TODO improve (if needed) to use attribute-value-replacer method instead of
   *        changing the value directly!!!
   */
  private boolean updateAssociateToReplaceLink(Associate a, Object oldObj, Object newObj) 
      throws NotPossibleException, NotFoundException {
    // the associated object
    Object assocObj = a.getAssociateObj();
    
    // linked attribute of assocObj
    DAttr yourAttrib = a.getMyEndAttribute(); 
    
    // association end of assocObj
    DAssoc yourAssoc = a.getMyEndAssociation();
    
    AssocEndType yourEnd = yourAssoc.endType();
    final Class yourCls = assocObj.getClass();
    if (yourAssoc.ascType() == AssocType.One2Many && 
        yourEnd == AssocEndType.One) {
      // yourAssoc is 1:M
      // TODO: improve (if needed) to use attribute-value-replacer method instead of 
      // changing the value directly!!!
//      Method deleter = dsm.findAttributeValueRemoverMethod(yourCls, yourAttrib.name(), myCls);
//      
//      try {
//        deleter.invoke(associate, deletedObj);
//        return true;
//      } catch (Exception e) {
//        // something wrong internally, report it
//        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
//            e,  "Không thể thực hiện phương thức {0}.{1}({2})", yourCls.getSimpleName(), 
//            deleter.getName(), deletedObj);
//      }
      // for now, get the attribute value out and change it directly
      Collection val = (Collection) dsm.getAttributeValue(yourCls, assocObj, yourAttrib);
      Collection oldObjCol = new ArrayList();
      oldObjCol.add(oldObj);
      boolean removed = val.removeAll(oldObjCol); // remove
      if (removed) { // important to check this because val may not yet contain oldObj
        val.add(newObj);  // add
      }
      return true;
    } else if (yourAssoc.ascType().equals(AssocType.One2One)) {
      // yourAssoc is 1:1 
      // invoke setter method of associate to set value to newObj
      setAttributeValue(assocObj, yourAttrib.name(), newObj);
      return true;
    } else {
      // not one of the allowed cases -> do nothing
      return false;
    }      
  }

  /**
   * @effects 
   *  Log throwable <tt>e</tt>
   */
  private void log(Throwable e, String methodName, Object...data) {
    //TODO: log to file or something
    if (debug || loggingOn) {
      PrintStream out = System.out; //System.err;
      out.printf("%s%s.%s:%n", (e != null) ? "WARNING in ":"", this.getClass().getSimpleName(), methodName);
      if (data.length > 0) {
        for (Object d : data) {
          out.println("   " + d);
        }
      }
  
      if (e != null) {
        if (debug)
          e.printStackTrace();
        else
          System.out.println("   Details: " + e.getMessage());
      } 
    }
  }

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
  public void setDebugOn(boolean tf) {
    if (tf && !debug) {
      // keep old debug value
      oldDebug = false; // = debug
      debug = true;
    } else if (!tf && debug != oldDebug) {
      // reset
      debug = oldDebug;
    }
    
    // also update osm debug if it is used
    if (isObjectSerialised()) {
      osm.setDebugOn(tf);
    }
  }

  /**
   * @effects 
   *  print <tt>mesg</tt> and call {@link #setDebugOn(boolean)}(tf)
   * @version 3.3
   */
  public void setDebugOn(boolean tf, String mesg) {
    System.out.println(mesg);
    
    setDebugOn(tf);
  }
  
//  /**
//   * Updates the value of an attribute of a domain object. This method supports
//   * both normal and collection-type attributes. This is more general than the
//   * {@link #setAttributeValue(Object, String, Object)} method.
//   * 
//   * @deprecated use {@link #updateAttributeValue(Object, String, Object)} instead
//   * 
//   * @effects if <code>attributeName</code> is a normal attribute then invokes
//   *            the setter method of this attribute passing in <code>val</code> as
//   *            an argument, 
//   *          else invokes getter method of this attribute and adds
//   *          <code>val</code> to the collection.
//   * @requires suitable setter or getter method of <code>attributeName</code> is
//   *           defined in <code>targetObj.class</code> and in the case of
//   *           collection-type attribute the getter method must return a {@see
//   *           Collection} object.
//   * @see #setAttributeValue(Object, String, Object)
//   */
//  public void updateAttributeValueOld(Object targetObj, String attributeName,
//      Object val) throws NotFoundException, NotPossibleException {
//    final Class c = targetObj.getClass();
//    Field attribute = getAttribute(c, attributeName);
//    DomainConstraint dc = attribute.getAnnotation(DC);
//
//    if (dc.type().isCollection()) {
//      Object oval = getAttributeValue(targetObj, attributeName);
//
//      if (oval == null) {
//        // creates a new collection (the actual type is compatible with the
//        // field type)
//        Class type = attribute.getType();
//        if (type.isInterface()) {
//          if (List.class.isAssignableFrom(type)) {
//            oval = new ArrayList();
//          } else if (Set.class.isAssignableFrom(type)) {
//            oval = new HashSet();
//          } else {
//            throw new NotImplementedException(
//                NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//                "Tính năng hiện đang không được hỗ trợ: {0}", "type:" + type);
//          }
//        } else {
//          try {
//            oval = type.newInstance();
//          } catch (Exception e) {
//            throw new NotPossibleException(
//                NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
//                "Không thể thực hiện phương thức: {0}({1})",
//                type.getSimpleName());
//          }
//        }
//        // set value
//        setAttributeValue(targetObj, attributeName, oval);
//      } else if (!(oval instanceof Collection)) {
//        throw new NotPossibleException(
//            NotPossibleException.Code.CLASS_NOT_WELL_FORMED,
//            "Lớp không được định nghĩa đúng: {0}", c);
//      }
//
//      Collection col = (Collection) oval;
//
//      if (val instanceof Collection)
//        col.addAll((Collection) val);
//      else
//        col.add(val);
//    } else {
//      setAttributeValue(targetObj, attributeName, val);
//    }
//  }

  /**
   * @requires 
   *  myObj != null /\ myCls != null
   * @effects 
   *  find in the object pools of the domain classes associated to <tt>myCls</tt>
   *  the objects that are linked to <tt>myObj</tt> via associations with property <tt>Association(...,derivedFrom=true)</tt>.
   *  
   *  <br>Return a Collection<Associate> of all of them or return <tt>null</tt> if no objects are found
   * @version 2.7.4
   */
  public Collection<Associate> getAssociatesDerivedFrom(Object myObj, Class myCls) {

Collection<Associate> associates = new ArrayList();
    
    Map<DAttr,DAssoc> associations = dsm.getAssociations(myCls);
    
    // associates obj are values of all the non-primitive-typed domain attributes
    if (associations != null) {
      DAttr attrib, yourAttrib;
      Associate asso;
      Class yourCls;
      Tuple2<DAttr,DAssoc> yourAssocTuple;
      DAssoc assoc, yourAssoc;
      boolean dependent;
      for (Entry<DAttr,DAssoc> e: associations.entrySet()) {
        try {
          attrib = e.getKey();
          assoc = e.getValue();
          
          yourCls = assoc.associate().type();
          // get the other end of the association 
          yourAssocTuple = dsm.getTargetAssociation(assoc); 
          yourAttrib = yourAssocTuple.getFirst();
          yourAssoc = yourAssocTuple.getSecond();
          
          // filter association with property Association(...,derivedFrom=true)
          if (yourAssoc.derivedFrom()) {
            // TODO: assume no Many-To-Many associations
            dependent = dsm.isDependentOn(yourCls, yourAssoc); 
  
            // if associate is a collection-type then find in the object-pool 
            // all the objects that are not yet added in associate (because they have not 
            // be browsed to)
            Object associate;
            if (attrib.type().isCollection()) {
              /* find all those matching myObj in the object pool of yourCls
               * Note: NOT necessary to look up for these in the data source because
               * they are not of interest to the caller of this method
               */
              Query q = new Query();
              q.add(new ObjectExpression(yourCls, yourAttrib, Op.EQ, myObj));
              Collection allObjs = getObjects(yourCls, q);
              associate = allObjs;
            } else {
              associate = dsm.getAttributeValue(myObj, attrib.name());
            }
  
            if (associate == null) {
              // skip 
              continue;
            }
  
            asso = new Associate(
                associate,
                yourAssoc,
                yourAttrib,
                assoc, 
                attrib, 
                dependent);
  
            associates.add(asso);
          }
        } catch (Exception  ex) {
          // log error and continue
          log(ex,"getAssociatesDerivedFrom");
        }
      }
    }
    
    return associates.isEmpty() ? null : associates;
  }
  
  /**
   * @requires 
   *  myObj != null /\ myCls != null
   * @effects 
   *  find in the object pools of the domain classes associated to <tt>myCls</tt>
   *  the objects that are linked to <tt>myObj</tt>.
   *  Return a Collection<Associate> of all of them or return <tt>null</tt> if no objects are found 
   */
  public Collection<Associate> getAssociates(Object myObj, Class myCls) {
    /*v3.0: use new method
    Collection<Associate> associates = new ArrayList();
    
    Map<DomainConstraint,Association> associations = dsm.getAssociations(myCls);
    
    // associates obj are values of all the non-primitive-typed domain attributes
    if (associations != null) {
      DomainConstraint attrib, yourAttrib;
      Associate asso;
      Class yourCls;
      Tuple2<DomainConstraint,Association> yourAssocTuple;
      Association assoc, yourAssoc;
      boolean dependent;
      for (Entry<DomainConstraint,Association> e: associations.entrySet()) {
        try {
          attrib = e.getKey();
          assoc = e.getValue();
          
          yourCls = assoc.associate().type();
          // get the other end of the association 
          yourAssocTuple = dsm.getTargetAssociation(assoc); 
          yourAttrib = yourAssocTuple.getFirst();
          yourAssoc = yourAssocTuple.getSecond();
          // TODO: assume no Many-To-Many associations
          dependent = dsm.isDependentOn(yourCls, yourAssoc); 

          // if associate is a collection-type then find in the object-pool 
          // all the objects that are not yet added in associate (because they have not 
          // be browsed to)
          Object associate;
          if (attrib.type().isCollection()) {
            //find all those matching myObj in the object pool of yourCls
           //Note: NOT necessary to look up for these in the data source because
            //they are not of interest to the caller of this method
            
            Query q = new Query();
            q.add(new ObjectExpression(yourCls, yourAttrib, Op.EQ, myObj));
            Collection allObjs = getObjects(yourCls, q);
            associate = allObjs;
          } else {
            associate = dsm.getAttributeValue(myObj, attrib.name());
          }

          if (associate == null) {
            // skip 
            continue;
          }

          asso = new Associate(
              associate,
              yourAssoc,
              yourAttrib,
              assoc, 
              attrib, 
              dependent);

          associates.add(asso);
        } catch (Exception  ex) {
          // log error and continue
          log(ex,"getAssociates");
        }
      }
    }
    
    return associates.isEmpty() ? null : associates;
      */
    
    return getAssociates(myObj, myCls, null);
  }
  
  /**
   * @requires 
   *  myObj != null /\ myCls != null
   * @effects 
   *  find in the object pools of the domain classes associated to <tt>myCls</tt> via associations of <tt>myCls</tt> that 
   *  satisfy <tt>filter</tt> (if it is specified), the objects that are linked to <tt>myObj</tt>.
   *  Return a Collection<Associate> of all of them or return <tt>null</tt> if no objects are found
   * @version 3.0 
   */
  public Collection<Associate> getAssociates(Object myObj, Class myCls, Filter<DAssoc> filter) {
    Collection<Associate> associates = new ArrayList();
    
    Map<DAttr,DAssoc> associations = dsm.getAssociations(myCls, filter);
    
    // associates obj are values of all the non-primitive-typed domain attributes
    if (associations != null) {
      DAttr attrib, yourAttrib;
      Associate asso;
      Class yourCls;
      Tuple2<DAttr,DAssoc> yourAssocTuple;
      DAssoc assoc, yourAssoc;
      boolean dependent;
      for (Entry<DAttr,DAssoc> e: associations.entrySet()) {
        try {
          attrib = e.getKey();
          assoc = e.getValue();
          
          yourCls = assoc.associate().type();
          // get the other end of the association 
          yourAssocTuple = dsm.getTargetAssociation(assoc); 
          yourAttrib = yourAssocTuple.getFirst();
          yourAssoc = yourAssocTuple.getSecond();
          // TODO: assume no Many-To-Many associations
          dependent = dsm.isDependentOn(yourCls, yourAssoc); 

          // if associate is a collection-type then find in the object-pool 
          // all the objects that are not yet added in associate (because they have not 
          // be browsed to)
          Object associate;
          if (attrib.type().isCollection()) {
            /* find all those matching myObj in the object pool of yourCls
             * Note: NOT necessary to look up for these in the data source because
             * they are not of interest to the caller of this method
             */
            Query q = new Query();
            q.add(new ObjectExpression(yourCls, yourAttrib, Op.EQ, myObj));
            Collection allObjs = getObjects(yourCls, q);
            associate = allObjs;
          } else {
            associate = dsm.getAttributeValue(myObj, attrib.name());
          }

          if (associate == null) {
            // skip 
            continue;
          }

          asso = new Associate(
              associate,
              yourAssoc,
              yourAttrib,
              assoc, 
              attrib, 
              dependent);

          associates.add(asso);
        } catch (Exception  ex) {
          // log error and continue
          log(ex,"getAssociates");
        }
      }
    }
    
    return associates.isEmpty() ? null : associates;
  }
  
  /**
   * @requires 
   *  obj != null /\ cls != null
   * @effects 
   *  find all <b>non-null</b> objects that are linked to <tt>obj</tt> via some associations
   *  and return them as <tt>Collection<Associate></tt> or null if no such objects exist
   */
  public Collection<Associate> getLinkedAssociates(Object obj, Class cls) {
    List<Associate> associates = new ArrayList();
    
    Map<Field,DAttr> attributes = dsm.getDomainAttributes(cls);
    
    // associates obj are values of all the non-primitive-typed domain attributes
    if (attributes != null) {
      DAttr myAttrib, yourAttrib;
      String name;
      Associate asso;
      Object associateObj;
      Class associateCls;
      DAssoc assoc, yourAssoc;
      Tuple2<DAttr,DAssoc> yourTuple;
      boolean dependent;
      //for (Field field : attributes) {
      for (Entry<Field,DAttr> entry : attributes.entrySet()) {
        Field field = entry.getKey();
        myAttrib = entry.getValue();
        //myAttrib = field.getAnnotation(DC);
        assoc = field.getAnnotation(AS);
        try {
          //if (type.isDomainType() || type.isCollection()) {
          if (assoc != null) {
            name = myAttrib.name();
            //type = dc.type();
            associateObj = dsm.getAttributeValue(obj, name);
            if (associateObj == null) {
              // skip 
              continue;
            }

            associateCls = assoc.associate().type();
            // get the other end of the association 
            
            //yourAssoc = getTargetAssociation(assoc).getSecond();
            yourTuple = dsm.getTargetAssociation(assoc);
            yourAttrib = yourTuple.getFirst();
            yourAssoc = yourTuple.getSecond();
            // TODO: assume no Many-To-Many associations
            dependent = dsm.isDependentOn(associateCls, yourAssoc); //isDependentOn(associateCls, cls);

            asso = new Associate(
                associateObj, 
                yourAssoc,
                yourAttrib,
                assoc, 
                myAttrib, 
                dependent);

            associates.add(asso);
          } // end if 
        } catch (Exception  e) {
          // log error and continue
          log(e,"getAssociates");
        }
      }
    }
    
    return associates;
  }

  /**
   * This method uses a combination of three methods: {@link #getAssociationLinkCount(Class, DAttr, Object)},  
   * {@link #loadAssociationLinkCount(Class, DAttr, Object, Oid)}, and {@link #setAssociationLinkCount(Class, DAttr, Object, int)}
   * to achieve the effect. 
   * 
   * @modifies <tt>o</tt>
   * @effects 
   *  if <tt>o</tt> (of <tt>c</tt>) has not been loaded with link count to the associated objects via the association named <tt>assocName</tt> (of <tt>c</tt>)
   *    load it and update <tt>o</tt>
   *  else
   *    do nothing
   *    
   *  <p>return the link count.
   *  
   *  <p>throws NotFoundException if association named <tt>assocName</tt> is not found; 
   *  NotPossibleException if fails to retrieve the information from the data source or fail to 
   *  set link count into <tt>o</tt>
   *  
   * @version 3.3
   */
  public int ensureObjectHasLinkCount(Class c, Object o, String assocName) throws NotFoundException, NotPossibleException {
    Tuple2<DAttr, DAssoc> assocTuple = dsm.getAssociation(c, assocName);
    
    DAttr linkAttrib = assocTuple.getFirst();
    
    int linkCount = getAssociationLinkCount(c, linkAttrib, o);
    
    if (linkCount <= 0) {
      // o either has not been loaded with link count or does not have any links
      // try to load
      Oid oid = lookUpObjectId(c, o);

      DAssoc assoc = assocTuple.getSecond();
      Class assocCls = assoc.associate().type();
      DAttr targetAttrib = dsm.getTargetAssociation(assoc).getFirst();
      
      try {
        linkCount = loadAssociationLinkCount(assocCls, targetAttrib, o, oid);
      } catch (DataSourceException e) {
        // failed to load
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e,  
            new Object[] {""});
      }
      
      // cache link count into object
      if (linkCount > 0) {
        setAssociationLinkCount(c, linkAttrib, o, linkCount);
      }
    }
    
    return linkCount;
  }
  
  /**
   * @requires 
   *  cls != null /\ assoc != null /\  
   *  attrib is a valid attribute of cls /\
   *  linkedObj is a valid domain object /\ 
   *  {@link #isObjectSerialised()} = true
   *    
   * @effects
   *  load and return from the data source the number of objects  
   *  of the domain class <tt>cls</tt> that are linked to a given domain object 
   *  <tt>linkedObj</tt> via the attribute <tt>attrib</tt> (of <tt>cls</tt>).
   *  
   *  <p>Throws DataSourceException if fails to retrieve the information from the data source
   *  
   * @example
   *  <pre>
   *  cls = Enrolment
   *  attrib = Enrolment.student
   *  linkedObj = Student(S2014), linkedObjOid = Oid(Student:id=S2014)
   *  link-count update method: Student.setEnrolmentCount()
   *  </pre>
   */
  public int loadAssociationLinkCount(Class cls, DAttr attrib, 
      Object linkedObj, Oid linkedObjOid) throws DataSourceException {
    return osm.readAssociationLinkCount(cls, attrib, linkedObj, linkedObjOid);
  }
  
  /**
   * @requires 
   *  cls != null /\ assoc != null /\  obj != null /\ 
   *  assoc is a valid association in cls /\ 
   *  obj is a domain object of <tt>cls</tt>
   * @effects
   *  if <tt>cls</tt> contains a suitable link-count getter method for <tt>attrib</tt> 
   *    invoke that method upon <tt>obj</tt> to obtain the current link count
   *  else
   *    return -1 
   *  
   *  <p>Throws NotFoundException if could not find the link-count getter method;
   *  NotPossibleException if fails to perform the method
   *  
   *  @example
   *  <pre>
   *  cls = Student
   *  obj = Student<S2014>
   *  attrib = Student.enrolments
   *  link-count getter method: Student.getEnrolmentsCount()
   *  </pre>  
   */
  public int getAssociationLinkCount(Class cls,
      DAttr attrib, Object obj) throws NotFoundException, NotPossibleException {
    String attribName = attrib.name();
    attribName = (attribName.charAt(0) + "").toUpperCase()
        + attribName.substring(1);
    
    // method name (e.g. getCustomerCount)
    String namePrefix = "get" + attribName; // + "Count";
    
    Method m = dsm.findMetadataAnnotatedMethodWithNamePrefix(cls, DOpt.Type.LinkCountGetter, namePrefix);
    
    return (Integer) dsm.doMethod(cls, m, obj);
  }

  /**
   * @return 
   * @requires 
   *  cls != null /\ assoc != null /\  obj != null /\ 
   *  assoc is a valid association in cls /\ 
   *  obj is a domain object of <tt>cls</tt>
   * @effects <pre>
   *  if <tt>cls</tt> contains a suitable link-count setter method for <tt>attrib</tt> 
   *    invoke that method upon <tt>obj</tt> to set the current link count 
   *    if result is Boolean (indicating that the associated view should be updated)
   *      return result 
   *    else return <tt>false</tt> 
   *  else
   *    do nothing </pre>
   *  
   *  <p>Throws NotFoundException if could not find the link-count setter method;
   *  NotPossibleException if fails to perform the method
   *  
   *  @example
   *  <pre>
   *  cls = Student
   *  obj = Student<S2014>
   *  attrib = Student.enrolments
   *  link-count getter method: Student.setEnrolmentsCount()
   *  </pre>     
   * @version 
   * - 3.1: change return type to boolean  
   */
  public boolean setAssociationLinkCount(Class cls,
      DAttr attrib, Object obj, int linkCount) 
  throws NotFoundException, NotPossibleException {
    String attribName = attrib.name();
    attribName = (attribName.charAt(0) + "").toUpperCase()
        + attribName.substring(1);
    
    // method name prefix (e.g. setCustomer...)
    String namePrefix = "set" + attribName; // + "Count";
    
    Method m = dsm.findMetadataAnnotatedMethodWithNamePrefix(cls, DOpt.Type.LinkCountSetter, namePrefix);
    
    Object result = dsm.doMethod(cls, m, obj, linkCount);
    
    if (result != null && result instanceof Boolean) {
      return (Boolean) result;
    } else 
      return false;
  }
  
  /**
   * @requires 
   *  cls != null /\ assoc != null /\  obj != null /\ 
   *  assoc is a valid association in cls /\ 
   *  obj is a domain object of <tt>cls</tt>
   * @effects
   *  look up the link attribute <tt>a</tt> of <tt>cls</tt> that is marked with 
   *    the association <tt>assoc</tt> and 
   *  invoke a suitable getter method of <tt>a</tt> upon <tt>obj</tt>
   *    to determine the number of objects that are linked to <tt>obj</tt> via 
   *     <tt>a</tt>   
   *  
   *  <p>Throws NotFoundException if no association <tt>assoc</tt> or attribute <tt>a</tt>
   *  or a suitable getter method for <tt>a</tt> exists in <tt>cls</tt>; or throws  
   *  NotPossibleException if fails to invoke the getter method.
   */
  public int getAssociationLinkCountFromPool(Class cls,
      String attribute, Object obj) throws NotFoundException, NotPossibleException {
    Field attrib = dsm.getDomainAttribute(cls, attribute);
    return getLinkCount(attrib, cls, obj);
  }

  /**
   * @requires 
   *  obj != null /\ a != null /\ cls != null /\ a is a valid domain attribute of cls
   *  /\ method getXCount(): Integer is defined in cls (where <tt>X=a.getName()</tt>
   *  and with the first letter capitalised)
   *   
   * @effects 
   *  return the number of domain objects <b>currently loaded in the object pool</b>
   *  that are linked to the domain object 
   *  <tt>obj</tt> of the domain class <tt>cls</tt> via the attribute 
   *  <tt>a</tt> of this class.
   *   
   *  <br>If succeeds
   *    return the result as Integer
   *  
   *  <br>Throws NotFoundException if no method with such name is defined in <tt>cls</tt>;
   *  NotPossibleException if fails to perform the method.  
   */
  private int getLinkCount(Field a, Class cls, Object obj) throws NotFoundException, NotPossibleException {
    // look up and invoke method obj.getXCount, where X=a.getName()
     // and with the first letter capitalised.
    String attribName = a.getName();
    attribName = (attribName.charAt(0) + "").toUpperCase()
        + attribName.substring(1);
    
    // method name (e.g. getCustomerCount)
    String mname = "get" + attribName + "Count";

    Method method = dsm.findMethod(mname, cls, Integer.class);

    try {
      Object output = method.invoke(obj, null);
      return (Integer) output;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,  
          "Không thể thực thi phương thức {0}<{1}>.{2}()", cls.getSimpleName(), obj, mname);
    }
  }
  
  /**
   * This method is used when <tt>obj</tt> is created in the system. 
   * 
   * @effects <pre>
   *    for each domain attribute a of obj.class, s.t name(a) neq excludeAttribute
   *      set domainObj = value(obj,a) 
   *      look up domain attribute a1 of domainObj.cls, s.t. type(a1) = obj.class
   *      add/set obj to value(domainObj,a1)
   * </pre>
   * @requires obj != null
   */
  /* v2.7.2: not used
  //TODO: pass in role information to further generalise this method
  public void updateAssociatesOnCreate(Object obj, DomainConstraint excludeAttribute) 
  throws NotFoundException, NotPossibleException 
  {
    Object domainObj;
    DomainConstraint dc;
    final Class cls = obj.getClass();

    // loop through all domain-type attributes of obj, whose name 
    // is not the same as the exclude attribute
    List<Field> attributes = getDomainAttributes(cls);
    //String name;
    if (attributes != null) {
      for (Field field : attributes) {
        dc = field.getAnnotation(DC);
        //name = dc.name();
        if (dc == excludeAttribute) // ignore 
          continue;
        
        if (dc.type().isDomainType()) {
          domainObj = getAttributeValue(obj, dc.name());

          if (domainObj != null) {
            // update 
            // find the attribute a1 of domainObj whose type is cls
            Field refAttrib = getCollectionAttributeByType(domainObj.getClass(), cls);
            
            if (refAttrib != null) {
              updateAssociateLink(domainObj, refAttrib, obj);
            }
//            if (refAttribs != null) {
//              // NOTE: assume one attribute match
//              if (refAttribs.size() > 1)
//                throw new NotPossibleException(
//                    NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
//                    "Không thể thực thi phương thức {0}.{1}()", "DomainSchema",
//                    "updateReferringAttributeValue");
//  
//              String a1 = refAttribs.get(0);
//              updateAttributeValue(domainObj, a1, obj);
//            }
          }
        }
      }
    }
  }
  */
  
//  /**
//   * This method is used when <tt>deletedObj</tt> was deleted, to update
//   * a specific object to which it is associated. 
//   * 
//   * @requires deletedObj != null /\ associatedObj != null /\ 
//   *  there exists a 1:M association link between associatedObj and deletedObj
//   * 
//   * @effects 
//   * <pre>
//   *      look up domain attribute a1 in associatedObj whose 
//   *        generic type = deletedObj.class
//   *      remove deletedObj from the value of a1
//   *      if deletedObj does not depend on domainObj
//   *        unlink domainObj from deletedObj
//   *      add domainObj into result array
//   *    return true if associatedObj was changed 
//   * </pre>
//   * 
//   * @deprecated this method is not being used and seems obsolete. Do not use it.
//   * 
//   */
//  public boolean updateManyAssociateOnDelete(Object deletedObj, Object associatedObj) 
//      throws NotFoundException, NotPossibleException {
//    boolean changed = updateOneToManyAssociateOnDelete(associatedObj, deletedObj);
//    
//    /*
//     * if deletedObj does not depend on domainObj
//     *  unlink domainObj from deletedObj
//     * Why? because dependent object needs to be deleted instead of unlinked 
//     * and this deletion is performed by the controller, not by this schema 
//     */
//    if (!isDependentOn(deletedObj, associatedObj))
//      setAttributeValue(deletedObj, name, null);
//
//    return changed;
//  }
  
  /**
   * This method is used when <tt>deletedObj</tt> was deleted to update the  
   * associate of this object 
   * 
   * @requires deletedObj != null /\ linkAttribute != null /\ linkAttrib is an attribute
   * of deletedObj.class
   * 
   * @effects 
   * <pre>
   *      let domainObj = value of linkAttribute in deletedObj
   *      let a1 = domain attribute of domainObj.class that implements the link to deletedObj
   *      update value of a1 to remove deletedObj
   *      if deletedObj does not depend on domainObj
   *        unlink domainObj from deletedObj
   *        
   *      if domainObj is changed
   *        return true
   *      else
   *        return false
   * </pre>
   * 
   */
  public boolean updateAssociateToRemoveLink(Object deletedObj, 
      DAttr linkAttribute) 
      throws NotFoundException, NotPossibleException {
    Object associatedObj;
    final Class deletedCls = deletedObj.getClass();

    /*v2.7.2: shortened 
    // loop through all domain objects referred to by uptObject, whose type
    // is not the same as the parent's type
    List<Field> attributes = getDomainAttributes(deletedCls);
    DomainConstraint dc;
    String name;
    boolean parentChanged = false;
    if (attributes != null) {
      for (Field field : attributes) {
        dc = field.getAnnotation(DC);
        name = dc.name();
        //TODO use association 
        
        if (dc == linkAttribute && dc.type().isDomainType()) {
          associatedObj = getAttributeValue(deletedObj, name);

          // domain object may be null
          if (associatedObj != null) {
            parentChanged = updateAssociateToRemoveLink(associatedObj, deletedObj, linkAttribute);
//            
//            if deletedObj does not depend on domainObj
//             unlink domainObj from deletedObj
//            Why? because dependent object needs to be deleted instead of unlinked 
//            and this deletion is performed by the controller, not by this schema 
//            
            if (!isDependentOn(deletedObj, linkAttribute, associatedObj))
              setAttributeValue(deletedObj, name, null);

          } // end if
          
          break;
        }
      }
      */
    
    String name = linkAttribute.name();
    associatedObj = dsm.getAttributeValue(deletedObj, name);
    boolean associateChanged = false;

    // domain object may be null
    if (associatedObj != null) {
      associateChanged = updateAssociateToRemoveLink(associatedObj, deletedObj, linkAttribute);
//      
//      if deletedObj does not depend on domainObj
//       unlink domainObj from deletedObj
//      Why? because dependent object needs to be deleted instead of unlinked 
//      and this deletion is performed by the controller, not by this schema 
//      
      if (!dsm.isDependentOn(deletedObj, linkAttribute, associatedObj))
        setAttributeValue(deletedObj, name, null);

    } // end if

    return associateChanged;
  }

  /**
   * @requires 
   *  targetObj != null
   * @effects 
   *  find the collection-type attribute in the class of targetObj whose
   *    generic type is deletedObjType
   *  if attribute exists
   *    look up the delete method of the attribute
   *    invoke the delete method on targetObj passing deletedObj as argument
   *    return true
   *  else
   *    return false
   *    
   *  e.g. 
   *  <pre>
   *  If targetObj = Student<1>, deletedObj = Enrolment<1>
   *  then
   *    collection-type attribute = Student.enrolments (List<Enrolment>)
   *    delete method = deleteEnrolment (as defined in the Update annotation of the attribute)
   *  </pre>
   * 
   * @deprecated as of v 2.7.2, use {@link #updateAssociateToRemoveLink(Object, Object, DomainConstraint)} instead. 
   */  
  /*v2.7.2: not used 
  public boolean updateOneToManyAssociateOnDelete(Object targetObj, Class deletedObjType, Object deletedObj) throws 
  NotPossibleException {
    Class targetCls = targetObj.getClass();
    try {
      Field attribute = getCollectionAttributeByGenericType(targetCls, deletedObjType);
      String attribName = attribute.getAnnotation(DC).name();
      Method deleter = findAttributeValueRemoverMethod(targetCls, attribName, deletedObjType);
      
      try {
        Object updated = deleter.invoke(targetObj, deletedObj);
        if (updated != null && updated instanceof Boolean) {
          return (Boolean) updated;
        } else {
          return false;
        }
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            e, "Không thể thực thi phương thức {0}.{1}({2})", targetCls, deleter.getName(), deletedObj);
      }
    } catch (NotFoundException e) {
      // attribute not found -> ignore
      log(e, "DomainSchema.updateOneToManyAssociateOnDelete");
      return false;
    }
  }
  */
  
  /**
   * @requires 
   *  associate != null /\ linkAttrib is an attribute of deletedObj.class
   *  
   * @effects <pre>
   * find the correct association between associate.class and deletedObj.class that is via the attribute 
   * linkAttrib of deletedObj.class
   *   if association is 1:M
   *      invoke deleter method of associate to remove deletedObj from the association
   *   else
   *      invoke setter method of associate to set value to null 
   *  
   *  if associate is updated
   *    return true
   *  else
   *    return false
   *    
   * <p>Throws NotPossibleException if failed to update
   * 
   *  <p>e.g. 
   *  If associate = Student<1>, deletedObj = Enrolment<1>
   *  then
   *    collection-type attribute = Student.enrolments (List<Enrolment>)
   *    delete method = deleteEnrolment (as defined in the Update annotation of the attribute)
   *  </pre>
   */
  public boolean updateAssociateToRemoveLink(Object associate, Object deletedObj, 
      DAttr linkAttrib // v2.7.2
      ) throws NotPossibleException {
    // invoke delete method on the domainObj to remove
    Class yourCls = associate.getClass();
    final Class myCls = deletedObj.getClass();
    
    /*v2.7.2: fixed to support 1-1 associations as well
    return updateOneToManyAssociateOnDelete(associate, deletedCls, deletedObj);
     */
    /*
     * find the correct association between associate.class and deletedObj.class that is via linkAttrib
     *   if association is 1:M
     *      invoke deleter method of associate to remove deletedObj from the association
     *   else
     *      invoke setter method of associate to set value to null 
     */
    try {
//      Tuple2<DomainConstraint,Association> myAssocTuple = getAssociation(myCls, linkAttrib);
//      Association myAssoc = myAssocTuple.getSecond();
      Tuple2<DAttr,DAssoc> yourAssocTuple = dsm.getTargetAssociation(myCls, linkAttrib);
      DAttr yourAttrib = yourAssocTuple.getFirst();
      DAssoc yourAssoc = yourAssocTuple.getSecond();
      AssocEndType yourEnd = yourAssoc.endType();
      
      if (yourAssoc.ascType().equals(AssocType.One2Many) && 
          yourEnd.equals(AssocEndType.One)) {
        // yourAssoc is 1:M
        // invoke deleter method of associate to remove deletedObj from the association
        Method deleter = dsm.findAttributeValueRemoverMethod(yourCls, yourAttrib.name(), myCls);
        
        try {
          deleter.invoke(associate, deletedObj);
          return true;
        } catch (Exception e) {
          // something wrong internally, report it
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
              e,  "Không thể thực hiện phương thức {0}.{1}({2})", yourCls.getSimpleName(), 
              deleter.getName(), deletedObj);
        }
      } else if (yourAssoc.ascType().equals(AssocType.One2One)) {
        // yourAssoc is 1:1 
        // invoke setter method of associate to set value to null
        setAttributeValue(associate, yourAttrib.name(), null);
        return true;
      } else {
        // not one of the allowed cases -> do nothing
        return false;
      }
    } catch (NotFoundException e) {
      // association or method is not defined -> ignored
      return false;
    }
  }
  
  /**
  * @requires <tt>
   *  associate != null /\ 
   *    yourAssoc = association of associate.class w.r.t deletedObj.class that is via 
   *    attribute yourAttrib </tt> 
   *  
   * @effects <pre>
   *   if yourAssoc is 1:M
   *      invoke deleter method of associate to remove deletedObj from the association
   *   else
   *      invoke setter method of associate to set value to null 
   *  
   *  if associate is updated
   *    return true
   *  else
   *    return false
   *    
   * <p>Throws NotPossibleException if failed to update
   * 
   *  <p>e.g. 
   *  If associate = Student<1>, deletedObj = Enrolment<1>
   *  then
   *    collection-type attribute = Student.enrolments (List<Enrolment>)
   *    delete method = deleteEnrolment (as defined in the Update annotation of the attribute)
   *  </pre>
   *   
   * @version 3.0
   */
  private boolean updateAssociateToRemoveLink(Object deletedObj, Object associate, 
      DAssoc yourAssoc, DAttr yourAttrib) throws NotPossibleException {
    AssocEndType yourEnd = yourAssoc.endType();
    Class yourCls = associate.getClass();
    final Class myCls = deletedObj.getClass();
    
    if (yourAssoc.ascType().equals(AssocType.One2Many) && 
        yourEnd.equals(AssocEndType.One)) {
      // yourAssoc is 1:M
      // invoke deleter method of associate to remove deletedObj from the association
      Method deleter = dsm.findAttributeValueRemoverMethod(yourCls, yourAttrib.name(), myCls);
      
      try {
        deleter.invoke(associate, deletedObj);
        return true;
      } catch (Exception e) {
        // something wrong internally, report it
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
            e,  "Không thể thực hiện phương thức {0}.{1}({2})", yourCls.getSimpleName(), 
            deleter.getName(), deletedObj);
      }
    } else if (yourAssoc.ascType().equals(AssocType.One2One)) {
      // yourAssoc is 1:1 
      // invoke setter method of associate to set value to null
      setAttributeValue(associate, yourAttrib.name(), null);
      return true;
    } else {
      // not one of the allowed cases -> do nothing
      return false;
    }  
  }
  
//  /**
//   * @requires deletedParent != null
//   * @effects add to dependents the associated objects that depend on deletedParent 
//   * and return dependents or null if no dependent objects are found
//   *          <p>
//   *          e.g.
//   * 
//   *          <pre>
//   *  If deletedParent = Student<1> AND Enrolment depends on Student
//   *  then
//   *    <tt>dependents</tt> contains all Enrolment objects referred to by Student<1>
//   *    
//   * </pre>
//   * @pseudocode <pre>
//   *  set parentType = class of deletedParent
//   *  set affectedChildren = empty collection
//   *  for each domain attribute a of parentType
//   *   if a.type is Domain or Collection
//   *    set childType = declared/generic type of a
//   *    set v = value of a in deletedParent
//   *    if childType depends on parentType or an ancester of parent type 
//   *    if a.type = Domain
//   *        add v to dependents
//   *    else // Collection (v is a collection)
//   *        add v.elements to dependents
//   * </pre>
//   * 
//   * @deprecated this method is not being used and seems obsolete. Do not use it.
//   */
//  public Collection updateDependentAssociatesOnDelete(Object deletedParent) throws DBException {
//    // set parentType = class of deletedParent
//    final Class parentType = deletedParent.getClass();
//    
//    List<Field> attribs = getDomainAttributes(parentType); 
//    
//    Collection dependents = new LinkedList();
//    
//    if (attribs == null) {
//      // should not happen
//      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
//          "Lớp {0} không được định nghĩa đúng", parentType);
//    }
//
//    DomainConstraint dc;
//    Type type;
//    Class childType;
//    Object v;
//    // for each domain attribute a of parentType
//    for (Field a : attribs) {
//      dc = a.getAnnotation(DC);
//      type = dc.type();
//      //  if a.type is Domain or Collection
//      if (type.isDomainType() || type.isCollection()) {
//        //    set v = value of a in deletedParent
//        v = getAttributeValue(a, deletedParent);
//
//        if (type.isDomainType()) {
//          // domain type
//          // set childType = declared type of a
//          childType = a.getType();
//          // if childType depends on parentType or an ancester of parent type
//          if (isDependentOn(childType,parentType)) {
//            dependents.add(v);
//          }
//        } else {
//          // collection type
//          //    set childType = generic type of a
//          childType = getGenericCollectionType(a);
//          //    if childType depends on parentType or an ancester of parent type
//          Collection col = (Collection) v;
//          if (isDependentOn(childType,parentType)) {
//            dependents.addAll(col);
//          }
//        }
//      } // end if 
//    } // end for
//    
//    return (dependents.isEmpty()) ? null : dependents;
//  }
  
//  /**
//   * Removes a value of an attribute of a domain object. This method supports
//   * collection-type attributes only. For normal attributes, use method
//   * {@link #updateAttributeValue(Object, String, Object)} instead.
//   * 
//   * @effects if <code>attributeName</code> is a collection-type attribute then
//   *          invokes the getter method of this attribute and removes
//   *          <code>val</code> from the collection.
//   * @requires suitable getter method of <code>attributeName</code> is defined
//   *           in <code>targetObj.class</code> and must return a {@see
//   *           Collection} object.
//   */
//  public void removeAttributeValue(Object targetObj, String attributeName,
//      Object val) {
//    final Class c = targetObj.getClass();
//    DomainConstraint dc = getDomainConstraint(c, attributeName);
//
//    if (dc.type().isCollection()) {
//      Object oval = getAttributeValue(targetObj, attributeName);
//
//      if (oval != null) {
//        if (!(oval instanceof Collection))
//          throw new NotPossibleException(
//              NotPossibleException.Code.CLASS_NOT_WELL_FORMED,
//              "Lớp không được định nghĩa đúng: {0}", c);
//
//        Collection col = (Collection) oval;
//        col.remove(val);
//      }
//    }
//  }

  /**
   * @effects initialises the value of attribute <tt>attributeName</tt> of
   *          the domain object <tt>o</tt> to the new value <tt>val</tt>.
   *          <br>if the state of <tt>o</tt> is changed
   *                return true
   *              else 
   *                return false
   *          
   *          <p>Throws <tt>NotFoundException</tt> if attribute is not found
   *          or <tt>NotPossibleException</tt> if either the attribute is
   *          not mutable or fail to invoke its setter method.
   * 
   * @requires <tt>o</tt> is a domain object,
   *           <tt>attributeName != null</tt> and is a mutable domain
   *           attribute of <tt>o.class</tt>.
   */
  public boolean setAttributeValue(Object o, String attributeName, Object val)
      throws NotFoundException, NotPossibleException {
    Class c = o.getClass();
    Field f = dsm.getDomainAttribute(c, attributeName);

    if (f == null) {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND,
         new Object[] {attributeName, c});
    }

    return setAttributeValue(o, f, val);
//    DomainConstraint dc = (DomainConstraint) f.getAnnotation(DC);
//    // only update if this field is mutable
//    if (dc.mutable()) {
//      try {
//        Method setter = findSetterMethod(f, c);
//        setter.invoke(o, val);
//      } catch (Exception e) {
//        throw new NotPossibleException(
//            NotPossibleException.Code.FAIL_TO_PERFORM, e,
//            "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue",
//            o, "");
//      }
//    } else {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
//          "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue", o,
//          attributeName + ".mutable=false");
//    }
  }

  /**
   * @requires 
   *  f != null
   * @effects initialises the value of attribute <tt>f</tt> of
   *          the domain object <tt>o</tt> to the new value <tt>val</tt>.
   *          <br>if the state of <tt>o</tt> is changed
   *                return true
   *              else 
   *                return false
   *          
   *          <p>Throws <tt>NotFoundException</tt> if no suitable setter method is found
   *          for the specified attribute or <tt>NotPossibleException</tt> if fails to invoke the setter method, 
   *          <tt>ConstraintViolationException</tt> if value is invalid
   */
  private boolean setAttributeValue(Object o, Field f, Object val)
      throws NotFoundException, NotPossibleException,
      ConstraintViolationException  // v2.7.4
  {
    
    Class c = o.getClass();

    DAttr dc = (DAttr) f.getAnnotation(DC);
    // only update if this field is mutable
    if (dc.mutable()) {
      try {
        Method setter = dsm.findSetterMethod(f, c);
        Object changed = setter.invoke(o, val);
        
        if (changed != null && changed instanceof Boolean) {
          return (Boolean)changed;
        } else {
          return false;
        }
      } 
      /*v2.7.2: let NotFoundException through 
      catch (Exception e) 
      */
      catch (SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) 
      {
        /*v2.7.4: support ConstraintViolationException 
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PERFORM, e,
            "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue"+"_"+f.getName(),
            o + "," + ((val!=null)?val.getClass().getSimpleName()+"("+val+")": val), "");
            */
        // check if ConstraintViolationException was the cause, if so then throw it....
        if (e instanceof InvocationTargetException) {
          InvocationTargetException ite = (InvocationTargetException) e;
          Throwable cause = ite.getCause();
          if (cause instanceof ConstraintViolationException) {
            throw (ConstraintViolationException) cause;
          }
        }
        
        // all other cases -> NotPossibleException
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PERFORM, e,
            "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue"+"_"+f.getName(),
            o + "," + ((val!=null)?val.getClass().getSimpleName()+"("+val+")": val), "");
      }
    } else {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          "Không thể thực thi thao tác: {0}({1}) {2}", 
          // v3.1: "setAttributeValue", o,f.getName() + ".mutable=false");
          "setAttributeValue"+"_"+f.getName(),
          o + "," + ((val!=null)?val.getClass().getSimpleName()+"("+val+")": val), "mutable=false");
    }
  }
  
  /**
   * 
   * @requires 
   *  f != null
   * @effects
   *  find and invoke <tt>setNewX()</tt> method of attribute <tt>f</tt> of <tt>o.class</tt>, upon 
   *  <tt>o</tt>, passing <tt>val</tt> in as argument. 
   *  <br>if the state of <tt>o</tt> is changed
   *        return true
   *      else 
   *        return false
   *  
   *  <p>Throws <tt>NotFoundException</tt> if no suitable method is found
   *  for the specified attribute or <tt>NotPossibleException</tt> if fails to invoke the method.
   */
  private boolean setNewAssociationLink(Object o, Field f, Object val)
      throws NotFoundException, NotPossibleException {
    
    Class c = o.getClass();

    DAttr dc = (DAttr) f.getAnnotation(DC);
    // only update if this field is mutable
    if (dc.mutable()) {
      try {
        Method setter = dsm.findSetterNewMethod(f, c);
        Object changed = setter.invoke(o, val);
        
        if (changed != null && changed instanceof Boolean) {
          return (Boolean) changed; // v2.7.4: true
        } else {
          return false;
        }
      } 
      /*v2.7.2: let NotFoundException through 
      catch (Exception e) 
      */
      catch (SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) 
      {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PERFORM, e,
            "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue"+"_"+f.getName(),
            o + "," + ((val!=null)?val.getClass().getSimpleName()+"("+val+")": val), "");
      }
    } else {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM,
          "Không thể thực thi thao tác: {0}({1}) {2}", "setAttributeValue", o,
          f.getName() + ".mutable=false");
    }
  }

//  /**
//   * @effects returns <code>Method</code> object of the class <code>c</code>,
//   *          whose name is <code>"add" + f.getName()</code> (with the first letter
//   *          capitalised).
//   *          
//   *          <p>Throws SecurityException if could not access the method, 
//   *          NotFoundException if the method is not found.
//   * 
//   */
//  private Method findAdderMethod(Field f, Class cls) throws SecurityException,
//      NotFoundException {
//    return findMethod(cls, f, "add");
//  }

  

//  /**
//   * @effects 
//   *  find and return a method annotated with Metadata.AutoGeneratedValue
//   *  
//   *  <p>throws NotFoundException if could not find the method.
//   */
//  private Method findAutoGeneratedValueMethod(Class c) throws NotFoundException {
//    Method[] methods = c.getMethods();
//    
//    // find method with the suitable Metadata tag
//    Metadata meta;
//    for (Method m : methods) {
//      meta = m.getAnnotation(METADATA);
//      if (meta != null && meta.type() == Metadata.Type.MethodUpdateAutoGeneratedValue) {
//        // found it
//        return m;
//      }
//    }
//    
//    // not found
//    throw new NotFoundException(NotFoundException.Code.METHOD_ANNOTATED_NOT_FOUND, 
//        "Không tìm thấy phương thức {0}#{1}({2})", c, METADATA.getSimpleName(), Metadata.Type.MethodUpdateAutoGeneratedValue);
//  }
  
  
  
  /**
   * @requires 
   *  attribute != null
   * @effects 
   *  return <tt>Method</tt> object representing the method 
   *  whose name is the name of the deleter method defined in the <tt>Update</tt>
   *  annotation of the specified attribute, and whose argument types are <tt>parameterTypes</tt>
   *  
   *  <p>Throws NotFoundException if no such method exists.
   *  @deprecated as of version 2.6.4.b  
   */
  /*v2.7.2: not used 
  private Method findAttributeValueDeleterMethod(Class cls, Field attribute, Class...parameterTypes) 
  throws NotFoundException {
    Update update = attribute.getAnnotation(UPDATE);
    
    if (update == null)
      throw new NotFoundException(NotFoundException.Code.UPDATE_METHOD_NOT_FOUND, 
          "Không tìm thấy phương thức cập nhật dữ liệu cho thuộc tính " + attribute);
    
    String methodName = update.delete();
    
    try {
      Method m = cls.getMethod(methodName, parameterTypes);
      return m;
    } catch (Exception e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          "Không tìm thấy phương thức {0}.{1}({2})", cls, methodName, Arrays.toString(parameterTypes));
    }
  }
  */
  
  /**
   * @requires 
   *  attribute != null
   * @effects 
   *  return <tt>Method</tt> object representing the method 
   *  whose name is the name of the updater method defined in the <tt>Update</tt>
   *  annotation of the specified attribute, and whose argument types are <tt>parameterTypes</tt>
   *  
   *  <p>Throws NotFoundException if no such method exists.
   *  @deprecated as of version 2.6.4.b  
   */
  /*v2.7.2: not used 
  private Method findAttributeValueUpdaterMethod(Class cls, Field attribute, Class...parameterTypes) 
  throws NotFoundException {
    Update update = attribute.getAnnotation(UPDATE);
    
    if (update == null)
      throw new NotFoundException(NotFoundException.Code.UPDATE_METHOD_NOT_FOUND, 
          "Không tìm thấy phương thức cập nhật dữ liệu cho thuộc tính " + attribute);
    
    String methodName = update.update();
    
    if (methodName == Update.NotDefined)
      // update method not defined
      return null;
    
    try {
      Method m = cls.getMethod(methodName, parameterTypes);
      return m;
    } catch (Exception e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          "Không tìm thấy phương thức {0}.{1}({2})", cls, methodName, Arrays.toString(parameterTypes));
    }
  }
  */
  
  
  
  // /**
  // * @effects if there exists a <code>Field</code> of <code>c</code> whose
  // declared type equals <code>type</code>
  // * then returns it, else returns <code>null</code>
  // */
  // private Field getAttribute(Class c, Class type) {
  // List<Field> fields = classDefs.get(c);
  //
  // if (fields != null) {
  // for (Field f : fields) {
  // if (f.getType().equals(type)) {
  // return f;
  // }
  // }
  // }
  //
  // return null;
  // }

  

  // public List<DomainConstraint> getAttributeConstraints(final String[]
  // filter)
  // throws NotFoundException {
  // String clsName = filter[0];
  // Class c = getDomainClassFor(filter[0]);
  //
  // if (c == null)
  // throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
  // "Không tìm thấy lớp: {0}", clsName);
  //
  // List<DomainConstraint> cons = getAttributeConstraints(c);
  //
  // // only return those matching the filter (if specified)
  // if (filter.length > 1) {
  // List<DomainConstraint> filterCons = new ArrayList();
  // for (DomainConstraint co : cons) {
  // for (int i = 1; i < filter.length; i++) {
  // if (co.name().equals(filter[i])) {
  // filterCons.add(co);
  // }
  // }
  // }
  // return (!filterCons.isEmpty()) ? filterCons : null;
  // } else {
  // return cons;
  // }
  // }

//  /**
//   * Parse the {@see DomainConstraint}'s filter field.
//   * 
//   * <p>
//   * The filter has the following form: <br>
//   * 
//   * <pre>
//   * Class-name[:attribute*]
//   * </pre>
//   * 
//   * <p>
//   * For example: the filter <code>Student</code> specifies just the
//   * <code>Student</code> class. On the other hand, the filter
//   * <code>Student:id,name</code> specifies the <code>Student</code> class, with
//   * two attributes </code>id</code> and <code>name</code>. The second example
//   * is used to specify domain constraints of report-type attributes to state
//   * that only certain attributes of a domain class appear in the report output.
//   * 
//   * @effects returns an array of the elements in the string <code>filter</code>
//   *          , e.g. if <code>filter=Student</code> then returns the array
//   * 
//   *          <pre>
//   * { &quot;Student&quot; }
//   * </pre>
//   * 
//   *          ; if <code>filter=Student:id,name</code> then returns the array
//   * 
//   *          <pre>
//   * { &quot;Student&quot;, &quot;id&quot;, &quot;name&quot; }
//   * </pre>
//   * 
//   */
//  public String[] parseConstraintFilter(String filter) {
//    Stack<String> elements = new Stack();
//    final String sep = ":";
//    final String sep2 = ",";
//    int ind = filter.indexOf(sep);
//    if (ind > -1) {
//      elements.push(filter.substring(0, ind));
//      String[] attribs = filter.substring(ind + 1).split(sep2);
//      Collections.addAll(elements, attribs);
//    } else {
//      elements.push(filter);
//    }
//
//    return elements.toArray(new String[elements.size()]);
//  }

//  /**
//   * @deprecated to be removed
//   * @version 
//   *  2.6.4b: make deprecated
//   */
//  public List<DomainConstraint> getReportConstraints(final Class c,
//      ReportTag.Name tagName) {
//    final Class tagClass = ReportTag.class;
//
//    List<Field> fields = getAttributes(c, tagClass, true);
//
//    List<DomainConstraint> consts = new ArrayList();
//    if (fields != null) {
//      ReportTag t;
//      DomainConstraint dc;
//      for (Field f : fields) {
//        t = f.getAnnotation(ReportTag.class);
//        // filter by the tag name
//        if (t.name().equals(tagName)) { // a match
//          // get the domain constraint
//          dc = f.getAnnotation(DC);
//          //dc = getAttributeConstraint(f);
//          if (dc != null) {
//            consts.add(dc);
//          } else {
//            // internal error: domain constraint expected but not found
//            throw new InternalError("");
//          }
//        }
//      }
//    }
//
//    if (!consts.isEmpty()) {
//      return consts;
//    } else {
//      return null;
//    }
//  }

//  /**
//   * @requires <tt>f</tt> has a DomainConstraint
//   * @effects <pre>
//   *          if f is a referenced field
//   *            returns DomainConstraint of the field to which f refers; 
//   *            throws NotPossibleException if the referenced field is not 
//   *            defined correctly, NotFoundException if the referenced 
//   *            field does not exist
//   *          else
//   *            returns DomainConstraint of f
//   *          </pre>
//   */
//  private DomainConstraint getAttributeConstraint(Field f) throws 
//    NotPossibleException, NotFoundException {
//    DomainConstraint dc = f.getAnnotation(DC);
//    
//    if (!dc.reference().clazz().equals(DomainConstraint.Null)) {
//      // get the domain constraint of the target attribute
//      String clsName = dc.reference().clazz();
//      String attribute = dc.reference().attribute();
//      Class cls = getDomainClassFor(clsName);
//      if (cls == null)
//        throw new NotPossibleException(
//            NotPossibleException.Code.CLASS_NOT_REGISTERED,
//            "Lớp chưa được đăng kí: {0}", clsName);
//      Field tf = getAttribute(cls, attribute);
//      /**@version 2.1b: add support for reference field*/
//
//      dc = (DomainConstraint) tf.getAnnotation(DC);
//      if (dc == null)
//        throw new NotPossibleException(
//            NotPossibleException.Code.FIELD_NOT_WELL_FORMED,
//            "Thuộc tính không được định nghĩa đúng: {0}", tf);        
//    }
//
//    return dc;
//  }
  
  /**
   * @effects 
   *  if <tt>d</tt> is not null
   *    if <code>value</code> satisfies <code>d</code> 
   *      returns the validated object
   *      else throws <code>ConstraintViolationException</code>
   *  else
   *    return value
   */
  public Object validateDomainValue(Class cls, DAttr d, Object value)
      throws ConstraintViolationException {

    Object val = value;

    // validate object against the domain constraint
    // validate optional constraint
    if (d != null) {
      if (!d.optional() && val == null) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED,
            //"{0}: Dữ liệu nhập không đúng: {1}", d.name(), value
            new Object[] {d.name(), value}
            );
      }

      if (value == null) {
        return null;
      }
      
      // validate other standard constraints (excluding unique)
      val = validateOtherConstraints(d, value);
      
      // v2.7.4: validate unique constraint
      if (d.unique())
        validateUniqueConstraint(cls, d, val);
    } // end null check

    // if we get here then ok
    return val;
  }
  
  /**
   * @requires 
   *  cls != null /\ attrib != null
   * @effects 
   *  if attrib is valid
   *    if value satisfies the domain constraint of attrib of cls
   *      return the validated object
   *    else throws ConstraintViolationException
   *  else
   *    return value
   *    
   *  <p>Throws NotFoundException if attrib is not found in cls
   */  
  public Object validateDomainValue(Class cls, String attrib, Object value) 
      throws ConstraintViolationException, NotFoundException {
    DAttr attribute = dsm.getDomainConstraint(cls, attrib);
    
    return validateDomainValue(cls, attribute, value);
  }

  /**
   * @effects 
   *  validate <tt>v</tt> based on the constraints in <tt>attrib</tt> and the referenced 
   *  attribute <tt>refAttrib</tt> (i.e. FK in relational model terminology);
   *  <br>if succeeded 
   *    return the validated value
   *  else
   *    throws ConstraintViolationException 
   * @version 2.7.3  
   */
  public Object validateDomainRefValue(DAttr attrib,
      DAttr refAttrib, Object value) throws ConstraintViolationException {
    // validate optionality based on attrib (not refAttrib)

    if (!attrib.optional() && value == null) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED,
          //"{0}: Dữ liệu nhập không đúng: {1}", attrib.name(), value
          new Object[] {attrib.name(), value}
          );
    }

    if (value == null) {
      return null;
    }
    
    // rest is as usual
    return validateOtherConstraints(refAttrib, value);
  }
  
  /**
   * @requires 
   *  attrib != null
   * @effects   
   *  validate value against <b>non-optional</b> constraints of <tt>attrib</tt>
   *  
   *  <br>if succeeded 
   *    return the validated value
   *  else
   *    throws ConstraintViolationException
   * @version 2.7.3
   */
  private Object validateOtherConstraints(DAttr attrib, Object value) throws ConstraintViolationException {
    Object val = value;

    String attribName = attrib.name();
    
    // validate length constraint
    Type type = attrib.type();

    // if (type.equals(Type.String)) {
    if (type.isString()) {
      String valStr = value.toString();
      
      if (attrib.length() > 0) {
        if (valStr.length() > attrib.length())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_ATTRIBUTE_LENGTH, 
              //"Dữ liệu nhập không đúng: {0}", valStr
              new Object[] {attribName, valStr, valStr.length(), attrib.length()});
      }
      
      // v2.7.3: support char type
      if (type.isChar()) {
        val = valStr.charAt(0); 
      } else {
        val = valStr;
      }
    }

    if (type.isNumeric()) {
      // parse into number if necessary
      if (!(value instanceof Number)) {
        try {
          if (type.equals(Type.Integer)) {
            val = Integer.parseInt(value.toString());
          } else if (type.equals(Type.Long)) {
            val = Long.parseLong(value.toString());
          } else if (type.equals(Type.Float)) {
            val = Float.parseFloat(value.toString());
          } else if (type.equals(Type.Double)) {
            val = Double.parseDouble(value.toString());
          }
          // v2.7.2
          else if (type.equals(Type.BigInteger)) {
            val = new BigInteger(val.toString());
          }
        } catch (NumberFormatException e) {
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_NUMERIC_VALUE,
              //"Dữ liệu nhập không đúng: {0}", value
              new Object[] {attribName, value}
              );
        }
      } else {
        // already a number, see if it matches the specified type. If not then
        // convert
        if (type.equals(Type.Integer) && !(value instanceof Integer)) {
          val = ((Number) value).intValue();
        } else if (type.equals(Type.Long) && !(value instanceof Long)) {
          val = ((Number) value).longValue();
        } else if (type.equals(Type.Float) && !(value instanceof Float)) {
          val = ((Number) value).floatValue();
        } else if (type.equals(Type.Double) && !(value instanceof Double)) {
          val = ((Number) value).doubleValue();
        } // v2.7.2
        else if (type.equals(Type.BigInteger) && !(value instanceof BigInteger)) {
          val = new BigInteger(value.toString());
        }
      }

      // validate min and max constraints
      if (attrib.min() != Double.NEGATIVE_INFINITY) {
        if (((Number) val).doubleValue() < attrib.min())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE_LOWER_THAN_MIN,
              //"Dữ liệu nhập không đúng: {0}", val
              new Object[] {attribName, val, attrib.min()}
              );
      }

      if (attrib.max() != Double.POSITIVE_INFINITY) {
        if (((Number) val).doubleValue() > attrib.max())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE_HIGHER_THAN_MAX,
              //"Dữ liệu nhập không đúng: {0}", val
              new Object[] {attribName, val, attrib.max()}
              );
      }
    } // end numeric check

    if (type.isBoolean() && !(value instanceof Boolean)) {
      try {
        val = Boolean.parseBoolean(value.toString());
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_BOOLEAN_VALUE,
            //"Dữ liệu nhập không đúng: {0}", value
            new Object[] {attribName, value}
            );
      }
    } // end boolean check

    if (type.isColor() && !(value.getClass().equals("java.awt.Color"))) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_COLOR_VALUE,
          //"Dữ liệu nhập không đúng: {0}", value
          new Object[] {attribName, value}
          );
    } // end color check

    if (type.isFont() && !(value.getClass().equals("java.awt.Font"))) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_FONT_VALUE,
          //"Dữ liệu nhập không đúng: {0}", value
          new Object[] {attribName, value}
          );
    } // end font check
    
    // v2.7.3: support date type where value is NOT already a date
    if (type.isDate() && !(value instanceof Date)) {
      // try to convert it to date, throws exeception if failed
      DateFormat format = (DateFormat) dsm.getAttributeFormat(attrib);
      try {
        val = (Date) format.parseObject(value.toString());
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_DATE_VALUE, 
            //value
            new Object[] {attribName, value}
            );
      }
    }
    
    return val;
  }

  /**
   * @effects 
   *  if d.unique()=true AND exist <tt>value</tt> of attribute <tt>d</tt> of <tt>c</tt>
   *    throws ConstraintViolationException
   *  else
   *    do nothing
   *  @version 2.7.4
   */
  protected void validateUniqueConstraint(Class cls, DAttr d,
      Object value) throws ConstraintViolationException {
    // for sub-types to implement
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        new Object[] {DOMBasic.class.getSimpleName(), "validateUniqueConstraint"});
  }

  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>delete</b> action. It invokes
   * {@link #validateCardinalityConstraint(Class, String, Object, LAName)} passing in 
   * <tt>LAName.Delete</tt> as the value of the last parameter.  
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj != null
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a link is deleted from it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateCardinalityConstraintOnDelete(
      Class cls, 
      DAttr attribute, 
      Object toObj, Oid toObjOid, int currentLinkCount) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    validateCardinalityConstraint(cls, attribute, toObj, toObjOid, currentLinkCount, LAName.Delete);
  }

  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>delete</b> action. It invokes
   * {@link #validateCardinalityConstraint(Class, String, Object, LAName)} passing in 
   * <tt>LAName.Delete</tt> as the value of the last parameter.  
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj != null
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a link is deleted from it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateCardinalityConstraintOnDelete(
      Class cls, 
      DAttr attribute, 
      Object toObj, int currentLinkCount) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    validateCardinalityConstraint(cls, attribute, toObj, null, currentLinkCount, LAName.Delete);
  }
  
  /**
   *  @requires cls != null /\ assocName != null 
   *  @effects 
   *    if deleting an existing link to the associate of cls in the association named assocName 
   *    violates its cardinality constraint
   *      throws ConstraintViolationException
   *     
   *  @example
   *    cls = Enrolment.class, assocName="std-has-enrols", currentLinkCount=1; 
   *    validateCardinalityConstraintOnDelete(cls, assocName, currentLinkCount) → 
   *        ConstraintViolationException

   *    Throws NotFoundException if the association can not be found. 
   */
  public void validateCardinalityConstraintOnDelete(
      Class cls, String assocName, int currentLinkCount) throws ConstraintViolationException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(cls, assocName);
    
    validateCardinalityConstraints(
        cls, assocTuple.getSecond(), currentLinkCount, LAName.Delete);
  }
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>create new</b> action. It invokes
   * {@link #validateCardinalityConstraint(Class, String, Object, LAName)} passing in 
   * <tt>LAName.Create</tt> as the value of the last parameter.  
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj /\ null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a new link is created for it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateCardinalityConstraintOnCreate(
      Class cls, 
      DAttr attribute, 
      Object toObj, Oid toObjOid, int currentLinkCount) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    validateCardinalityConstraint(cls, attribute, toObj, toObjOid, currentLinkCount, LAName.Create);
  }
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>create new</b> action. It invokes
   * {@link #validateCardinalityConstraint(Class, String, Object, LAName)} passing in 
   * <tt>LAName.Create</tt> as the value of the last parameter.  
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj /\ null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a new link is created for it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateCardinalityConstraintOnCreate(
      Class cls, 
      DAttr attribute, 
      Object toObj, int currentLinkCount) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    
    validateCardinalityConstraint(cls, attribute, toObj, null, currentLinkCount, LAName.Create);
  }
  
  /**
   *  @requires cls != null /\ assocName != null
   *  @effects 
   *    if a new link to the associate of cls in the association named assocName 
   *    violates its cardinality constraint
   *      throws ConstraintViolationException
   *     
   *  @example
   *    cls = Enrolment.class, assocName="std-has-enrols", currentLinkCount=30; 
   *    validateCardinalityConstraintOnCreate(cls, assocName, currentLinkCount) → 
   *        ConstraintViolationException

   *    Throws NotFoundException if the association can not be found. 
   */
  public void validateCardinalityConstraintOnCreate(
      Class cls, String assocName, int currentLinkCount) throws ConstraintViolationException {
    Tuple2<DAttr,DAssoc> assocTuple = dsm.getAssociation(cls, assocName);
    
    validateCardinalityConstraints(
        cls, assocTuple.getSecond(), currentLinkCount, LAName.Create);
  }
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class.  
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj /\ null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if action <tt>objectAction</tt> is performed on it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  private void validateCardinalityConstraint(
      Class cls, 
      DAttr attribute, 
      Object toObj, Oid toObjOid, int currentLinkCount,
      LAName objectAction) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    
    /*v2.7.2: use all associations, not just 1:M 
    Map<DomainConstraint,Association> manyAssociations = getAssociations(cls, 
        AssocType.One2Many, AssocEndType.Many);
        */
    Map<DAttr,DAssoc> manyAssociations = dsm.getAssociations(cls);
    
    if (manyAssociations != null) {
      DAttr linkAttribute;
      DAssoc myAssociation;
      for (Entry<DAttr,DAssoc> assocEntry: manyAssociations.entrySet()) {
        linkAttribute = assocEntry.getKey();
        if (linkAttribute == attribute) {
          // found the attribute and its association
          myAssociation = assocEntry.getValue();
          
          validateCardinalityConstraints(
              cls, attribute,
              myAssociation, toObj, toObjOid, 
              currentLinkCount, objectAction);
          
          break;
        }
      }
    }
  }
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific association</b>
   * between two domain classes.  
   * 
   * @requires association != null /\ assocObj != null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          <tt>myAssociation</tt> that links to <tt>assocObj</tt> is not violated
   *          if action <tt>objectAction</tt> is performed on an association.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   * @example
   *           
   */
  private void validateCardinalityConstraints(
      Class myClass, 
      DAttr attrib,
      DAssoc myAssociation, 
      Object assocObj, Oid assocObjOid, int currentLinkCount,
      LAName objectAction) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException {
    Class assocClass;
    DAssoc targetAssociation;
    //int currentLinkCount, 
    int minCard, maxCard;
    Tuple2<DAttr,DAssoc> tuple;
    DAttr targetAttrib;
    
    assocClass = myAssociation.associate().type();
    tuple = dsm.getTargetAssociation(myAssociation); 
    targetAttrib = tuple.getFirst();
    targetAssociation = tuple.getSecond();
    
    if (currentLinkCount < 0) {
      /*
       * use the association to determine the link attribute on v's side
       * and invoke a suitable getter method of this attribute on v to
       * obtain the number of objects of this.cls that are currently
       * linked to it
       */
      boolean cacheLinkCount = true; // whether or not to cache link count into the object
      try {
        currentLinkCount = getAssociationLinkCount(assocClass,
            targetAttrib, assocObj);
      } catch (Exception e) {
        // not possible to cache 
        cacheLinkCount = false;
      }
      
      // v2.7.2: only load link count if attrib is serialisable
      if (currentLinkCount <= 0 && isConnectedToDataSource()
          && attrib.serialisable()  // v2.7.2
          ) {
        // load link count from db 
        try {
          if (assocObjOid == null) {
            // get the Oid
            assocObjOid = lookUpObjectId(assocClass, assocObj);
            if (assocObjOid == null) {
              // generate one
              assocObjOid = genObjectId(assocClass, assocObj);
            }
          }
          currentLinkCount = loadAssociationLinkCount(myClass, attrib, assocObj, assocObjOid);
        } catch (DataSourceException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e,  
              new Object[] {""});
        }
        
        if (cacheLinkCount) {
          // cache link count into object
          try {
            setAssociationLinkCount(assocClass, targetAttrib, assocObj, currentLinkCount);
          } catch (Exception e) {
            // fails to cache
          }
        }
      }
    } // end get link count
    
    /*
     * if (this number = max-card and max-card is not '*' and
     * objectAction = Create) OR (this number = min-card and
     * objectAction = Delete) throws ConstraintViolationException else
     * do nothing
     */
    minCard = targetAssociation.associate().cardMin();
    maxCard = targetAssociation.associate().cardMax();
    if ((maxCard != DCSLConstants.CARD_MORE && currentLinkCount+1 > maxCard && objectAction == LAName.Create)
        || (currentLinkCount-1 < minCard && objectAction == LAName.Delete)) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
          new Object[] {objectAction, assocObj, targetAssociation.ascName(), minCard, maxCard, currentLinkCount});
    }
  }
  
  /**
   * @requires association != null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects 
   *  if performing action <tt>objectAction</tt> on the specified association <tt>assoc</tt>, 
   *  whose current link count is <tt>currentLinkCount</tt> 
   *  causing its cardinality constraint to violate 
   *    throws ConstraintViolationException
   *          
   *  @version 3.0
   */
  private void validateCardinalityConstraints(Class cls,
      DAssoc assoc, int currentLinkCount,
      LAName objectAction) throws ConstraintViolationException {
    /*
     * if (this number = max-card and max-card is not '*' and
     * objectAction = Create) OR (this number = min-card and
     * objectAction = Delete) throws ConstraintViolationException else
     * do nothing
     */
    int minCard = assoc.associate().cardMin();
    int maxCard = assoc.associate().cardMax();
    if ((maxCard != DCSLConstants.CARD_MORE && currentLinkCount+1 > maxCard && objectAction == LAName.Create)
        || (currentLinkCount-1 < minCard && objectAction == LAName.Delete)) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
          new Object[] {objectAction, "", assoc.ascName(), minCard, maxCard, currentLinkCount});
    }    
  }
  
//  /**
//   * This method is used to validate the cardinality constraint of <b>all associations
//   * attached to some attributes</b> of a domain class. 
//   *   
//   * This method basically performs two validations: 
//   * (1) {@link #validateCardinalityConstraints(Class, Object, LAName.Delete)} 
//   *    which is performed over the domain attribute values of the domain object
//   * (2) {@link #validateCardinalityConstraints(Map, LAName.Create)}
//   *    which performed directly over the new values of a domain object
//   * 
//   * @requires 
//   *  cls != null /\ cls is a domain class /\ 
//   *  dobj != null /\ dobj is a valid domain object of cls /\ 
//   *  vals != null
//   * @effects 
//   *  check that the cardinality constraints of any 1:M associations between 
//   *  other domain classes and <tt>cls</tt> are not violated if an  
//   *  association link is added to them because of <tt>newVals</tt> and so are  
//   *  those between the same domain classes w.r.t <tt>dobj</tt>. If so
//   *  do nothing, else throw ConstraintViolationException. 
//   */
//  public void validateCardinalityConstraintsOnUpdate(
//      Class cls, 
//      Object dobj, 
//      LinkedHashMap<DomainConstraint,Object> newVals) throws ConstraintViolationException, NotFoundException, 
//      NotPossibleException {
//    /* 
//     * determine all 1:M associations of this.cls in which the end-type of this.cls
//     * is M
//     */
//    Map<DomainConstraint,Association> manyAssociations = getAssociations(cls, 
//        AssocType.One2Many, AssocEndType.Many);
//    
//    if (manyAssociations != null) {
//      DomainConstraint linkAttribute;
//      Object valueObj;
//      Association myAssociation;
//      //TODO: determine this link count for each association
//      int currentLinkCount = -1;
//      
//      for (Entry<DomainConstraint,Association> assocEntry: manyAssociations.entrySet()) {
//        linkAttribute = assocEntry.getKey();
//        myAssociation = assocEntry.getValue();
//        // (1) check constraint against the existing link attribute value of dobj
//        valueObj = getAttributeValue(dobj, linkAttribute.name());
//        if (valueObj != null) {
//          validateCardinalityConstraints(myAssociation, valueObj, currentLinkCount, LAName.Delete);
//        }
//        
//        // (2) check constraint against the new value of the same attribute in newVals
//        valueObj = newVals.get(linkAttribute);
//        if (valueObj != null) {
//          validateCardinalityConstraints(myAssociation, valueObj, currentLinkCount, LAName.Create);
//        }
//      }
//    }
//  }

  /**
   * This method is used to validate the cardinality constraints of <b>all associations
   * attached to a domain class</b>.
   *  
   * 
   * <p>This method is very similar to {@link #validateCardinalityConstraints(Map, LAName)}
   * except that it is performed directly over the domain attribute values of a domain object.
   * 
   * @requires 
   *  cls != null /\ cls is a domain class /\ 
   *  obj != null /\ obj is a valid domain object of cls /\ 
   *  objectAction != null
   * @effects 
   *  check that the cardinality constraints of any 1:M associations between 
   *  other domain classes and <tt>cls</tt> are not violated if an  
   *  association link is added/removed (depending on <tt>objectAction</tt>)
   *  to them w.r.t <tt>obj</tt>. If so
   *  do nothing, else throw ConstraintViolationException. 
   */
  public void validateCardinalityConstraints(
      Class cls, 
      Object obj, 
      LAName objectAction) throws ConstraintViolationException, NotFoundException, 
      NotPossibleException {
    /* 
     * determine all 1:M associations of this.cls in which the end-type of this.cls
     * is M
     */
    Map<DAttr,DAssoc> manyAssociations = dsm.getAssociations(cls, 
        AssocType.One2Many, AssocEndType.Many);
    
    if (manyAssociations != null) {
      DAttr linkAttribute;
      Object valueObj;
      Oid valueObjOid;
      Class assocClass;
      DAssoc myAssociation, yourAssociation;
      int minCard, maxCard;
      int currentLinkCount = -1;
      //Object obj = (Object) dobj;
      for (Entry<DAttr,DAssoc> assocEntry: manyAssociations.entrySet()) {
        linkAttribute = assocEntry.getKey();
        myAssociation = assocEntry.getValue();
        valueObj = dsm.getAttributeValue(obj, linkAttribute.name());
        valueObjOid = lookUpObjectId(valueObj.getClass(), valueObj);
        if (valueObj != null) {
          validateCardinalityConstraints(cls, linkAttribute, 
              myAssociation, valueObj, valueObjOid, currentLinkCount, objectAction);
        } // end for
      }
    }
  }
  
  /**
   * 
   * @requires 
   *  vals != null /\ objectAction != null
   *  
   * @effects <pre>
   *  for each domain class <tt>c</tt> involved in <tt>vals</tt> that is not <tt>excludeClass</tt>
   *    if performing <tt>objectAction</tt> on the association between <tt>cls</tt> and <tt>c</tt>
   *    violates the association's cardinality constraints 
   *      throw ConstraintViolationException
   *    else
   *      do nothing </pre>
   */
  public void validateCardinalityConstraints(
      Class cls,
      Map<DAttr,Object> vals,
      LAName objectAction, Class excludeClass) throws ConstraintViolationException, NotFoundException, 
      NotPossibleException {
    /* 
     * determine all associations of this.cls
     */
    Map<DAttr,DAssoc> myAssociations = 
        //v2.7.3: dsm.getAssociations(cls,AssocType.One2Many, AssocEndType.Many);
        dsm.getAssociations(cls);
    
    if (myAssociations != null) {
      /*
       *  match the associates to the value objects in vals using the names of the 
       *  link attributes of this.cls
       *  then for each matching value object v
       *    use the association to determine the link attribute on v's side
       *      and invoke a suitable getter method of this attribute on v 
       *      to obtain the number of objects of this.cls that are currently linked to it
       *    if (this number = max-card and objectAction = Create) OR
       *       (this number = min-card and objectAction = Delete)
       *       throws ConstraintViolationException 
       *    else 
       *      do nothing
       */
      DAttr linkAttribute;
      Object valueObj;
      Oid valueObjOid;
      DAssoc myAssociation;
      int currentLinkCount = -1, minCard, maxCard;
      for (Entry<DAttr,DAssoc> assocEntry: myAssociations.entrySet()) {
        linkAttribute = assocEntry.getKey();
        myAssociation = assocEntry.getValue();
        
        if (excludeClass != null && myAssociation.associate().type() == excludeClass) {
          continue; // skip
        }
        
        valueObj = vals.get(linkAttribute);
        valueObjOid = lookUpObjectId(valueObj.getClass(), valueObj);
        if (valueObj != null) {
          // a matching value object
          validateCardinalityConstraints(
              cls, linkAttribute, 
              myAssociation, 
              valueObj, valueObjOid, currentLinkCount, objectAction);
        } 
      }
    }
  }

  
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @version 5.0
   */
  @Override
  public String toString() {
    return "DOMBasic (" + config + ")";
  }

  /**
   * Overrides the default method to release memory resources.
   */
  public void finalize() throws Throwable {
    super.finalize();
//    classDefs.clear();
//    classRelDefs.clear();
//    classExts.clear();
//    classConstraints.clear();
//    classDerivingAttribs.clear(); // v2.6.4b
//    classAssocs.clear();
//    classDefs = null;
//    classRelDefs = null;
    classExts = null;
//    classConstraints = null;
//    classAssocs = null;
//    reflexiveClasses.clear();
//    reflexiveClasses = null;
    
    if (osm != null)
      osm.disconnect();
  }
}
