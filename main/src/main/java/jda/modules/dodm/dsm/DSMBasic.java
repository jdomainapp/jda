package jda.modules.dodm.dsm;

import static java.lang.annotation.ElementType.FIELD;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.filter.Filter;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DomainValueDesc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ChangeListener;

/**
 * @overview 
 *  Represents the <b>Domain Schema Manager</b> component of the {@link DODMBasic}.
 *  
 * @author dmle
 */
public class DSMBasic {
  private DODMConfig config;
  
  /**
   * The name of the domain schema. This is typically defined from the
   * application name and is also used to name of the underlying database.
   */
  private String name;

  /** maps a domain class to its domain attributes */
  // v5.0: improved to support more flexible field-dc mappings
  //private Map<Class, List<Field>> classDefs;
  private Map<Class, LinkedHashMap<? extends Member,DAttr>> classDefs;

  /**
   * maps a domain class it the domain attributes that are used to create the
   * relational table of the class. These include a subset of the attributes of that domain class 
   * used in {@link #classDefs} that have the <tt>serialisable</tt> property of the domain 
   * set to true, plus the id attributes of any super-classes of the domain class. 
   */
  //v5.0: changed to map Field-DAttr
  //private Map<Class, List<Field>> classSerialisableDefs;
  private Map<Class, LinkedHashMap<Field,DAttr>> classSerialisableDefs;

  /**
   * this map (derived from {@link #classDefs}) maps a domain class to its
   * domain constraints
   */
  //v5.0: replaced by classDefs
  // private Map<Class, List<DAttr>> classConstraints;
  
  /**
   * this map (derived from {@link #classDefs}) maps a domain class to those 
   * domain constraints of this class that define a deriving attribute (i.e. 
   * those whose {@link DAttr#derivedFrom()}<tt>.length > 0</tt>)
   * 
   * @version 2.6.4b
   */
  private Map<Class, List<DAttr>> classDerivingAttribs;
  
  /**
   * this maps a domain class to the 
   * domain constraints of those fields of this class that realise a reflexive association
   * 
   * <p><b>Note</b>: unlike other maps, this map is populated on demand each time operation {@link #getReflexiveDomainConstraints(Class, List, boolean)}
   * is called. Thus, this map is not guaranteed to be filled at any time during execution. 
   * 
   * @version 3.2
   */
  private Map<Class, List<DAttr>> classReflexiveAttribs;
  
  /***
   * this map (derived from {@link #classDefs}) maps a domain class to its
   * associations
   */
  private Map<Class, List<Tuple2<DAttr,DAssoc>>> classAssocs;
  
  /***
   * this map (derived from {@link #classDefs}) maps a domain class to a Collection 
   * of other classes (domain or enum) that it references (via at least of its domain attributes).
   * @version 2.7.4
   */
  private Map<Class, List<Class>> classDependencies;
  
  /***
   * this maps a domain class to its {@link DOpt}-annotated methods
   */
  private Map<Class,Map<DOpt.Type,Collection<Method>>> classMethods;

  /** maps a domain class to a Boolean value which is true/false based on whether or not the class 
   * is a reflexive class*/
  private Map<Class, Boolean> reflexiveClasses;

  /** maps a domain class to its change event object (e.g. used to inform listeners when 
   * a class is registered in this) 
   *  
   * @version 2.7.3
   * */
  private Map<Class, ChangeEvent> changeEvents;
  
  /**
   * maps a domain class to its change event listeners. The listeners of a
   * domain class are notified (passing the change event object of this class
   * taken from {@link #changeEvents} as an argument) when the class has been manipulated by this
   */
  private Map<LAName, List<ChangeListener>> listenerMap;

  /** maps domain schema names to domain schema objects */
//  private static Map<String, DSM> schemas = 
//      new LinkedHashMap<String, DSM>();

  // constants
  /** the domain constraint annotation class */
  public static final Class<DAttr> DC = DAttr.class;

  /** the association class*/
  public static final Class<DAssoc> AS = DAssoc.class;
  
  /** the class constraint annotation class */
  public static final Class<DClass> CC = DClass.class;

  private static final Class<ModuleDescriptor> MD = ModuleDescriptor.class;

  /** the UPDATE_OPERATION annotation class */
//  private static final Class<Update> UPDATE = Update.class;

  /** the METADATA annotation class */
  private static final Class<DOpt> METADATA = DOpt.class;

  public static final Class<AttrRef> MEMBER_REF = AttrRef.class;
  
  /** the default name, used when {@link #name} = null */
  public static final String DEFAULT_NAME = "domainapp";
  
  // v3.0
  public static final AssociationFilter AssocFilter = new AssociationFilter();


  /**
   * A shared {@link Filter} for {@link DAssoc} that excludes many-many associations. 
   * It is obtained by {@link #getExclManyManyAssocFilter()} (which creates this once) 
   * 
   * @version 3.3
   */
  private static Filter<DAssoc> exclManyManyAssocFilter;

  private static final boolean debug = Toolkit.getDebug(DSMBasic.class);
  private static final boolean loggingOn = Toolkit.getLoggingOn(DSMBasic.class);

  /**
   * This method is useful when we want to use DSM in the memory, without worrying 
   * too much about how objects of the class are stored. 
   * 
   * @effects 
   *  initialises this without a {@link DODMConfig}. 
   * @version 5.4.1
   */
  public DSMBasic() {
    this(null);
  }
  
  public DSMBasic(DODMConfig config) {
    this.config = config;
    
    classDefs = new LinkedHashMap<Class, LinkedHashMap<? extends Member,DAttr>>(); //v5.0 new LinkedHashMap<Class, List<Field>>();
    classSerialisableDefs = new LinkedHashMap<Class, LinkedHashMap<Field,DAttr>>();
  //v5.0 classConstraints = new LinkedHashMap<Class, List<DAttr>>();
    classReflexiveAttribs = new LinkedHashMap<Class, List<DAttr>>(); // v3.2
    classDerivingAttribs = new LinkedHashMap<Class, List<DAttr>>(); // v2.6.4b
    classAssocs = new LinkedHashMap<Class, List<Tuple2<DAttr,DAssoc>>>();
    classDependencies = new HashMap<Class, List<Class>>(); // v2.7.4
    classMethods = new LinkedHashMap<Class,Map<DOpt.Type,Collection<Method>>>();
    reflexiveClasses = new HashMap<Class,Boolean>();

    changeEvents = new LinkedHashMap<Class, ChangeEvent>();
    listenerMap = new LinkedHashMap<LAName, List<ChangeListener>>();

    if (config != null)
      this.name = config.getAppName();
    else 
      this.name = DEFAULT_NAME;
  }

  /**
   * Registers change listeners to the change events concerning the object pool
   * of a domain class.
   * 
   * @effects add listener <code>l</code> to the list of
   *          <code>ChangeListener</code>s of the change events concerning the
   *          <code>action</code>
   * @see #fireStateChanged(Class)
   */
  public void addChangeListener(LAName action, ChangeListener l) {
    List<ChangeListener> ls = listenerMap.get(action);
    if (ls == null) {
      ls = new ArrayList<ChangeListener>();
      listenerMap.put(action, ls);
    }
    ls.add(l);
  }

  /**
   * Notifies all change listeners registered to the change event of the domain
   * class <code>c</code>
   * 
   * @effects 
   *  inform <tt>ChangeListener</tt>s registered to listen to <tt>act</tt> about 
   *  the state change occured on <tt>c</tt>
   * 
   * @see #addChangeListener(Class, ChangeListener)
   */
  private void fireStateChanged(Class c, LAName act) {
    // notify the change listeners for the domain class c
    // using the change event of that class
    List<ChangeListener> ls = listenerMap.get(act);
    if (ls != null) {
      ChangeEvent ce = changeEvents.get(c);
      ChangeEventSource src = (ChangeEventSource) ce.getSource();
//      src.clear();
//      src.addAll(changedObjects);
      src.setChangeAction(act);
      
      for (ChangeListener l : ls) {
        l.stateChanged(ce);
      }
    }
  }
  
//  /**
//   * Note: method {@link #addClasses(Class[])} is preferred to this one.
//   * 
//   * @effects a short cut for {@link #addClass(Class, boolean)}, in which the
//   *          second argument is <code>true</code> if
//   *          <code>this.serialisable = true</code> or is <code>false</code> if
//   *          otherwise.
//   */
//  public void addClass(Class c) throws DataSourceException, NotPossibleException {
//    addClass(c);
//  }

  /**
   * @requires 
   *  <tt> c != null /\ c is a domain class (not an interface)</tt>
   * @effects 
   *  if <tt>c</tt> is not already registered
   *    registers <tt>c</tt> to <tt>this</tt> as a domain class.
   *    <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   *    
   *    <br>if <tt>c</tt> references other domain-oriented data types (through its the domain attributes) 
   *    register those (recursively) that have not yet been registered 
   * @version 5.4 
   */
  public <T> void registerClasses(Class<T> c) throws NotPossibleException {
    // register c to dsm
    
    if (debug)
      System.out.printf("DSM.registerClasses: %s%n", c.getSimpleName());
    
    Collection<Class> refClasses = registerClass(c);
    if (refClasses != null) {
      HELPER: for (Class h : refClasses) {
        // only register h if it is not yet registered
        if (!isRegistered(h)) {
          if (debug)
            System.out.printf("   [%s] -> ref: %s%n", c.getSimpleName(), h.getSimpleName());

          if (h.isEnum())
            registerEnumInterface(h);
          /*v3.0: not yet tested
          else if (h.isAnnotation()) // v3.0
            registerAnnotation(h);
            */
          else // recursive
            registerClassHierarchy(h);
        }
      }
    }
  }
  
  /**
   * @requires 
   *  <tt>c != null /\ c is a domain class (not an interface)</tt>
   * @effects 
   *  register <tt>c</tt> and all of its domain super- and ancestor classes (if any) 
   *  (if not yet done so).
   *    
   *  <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   * @version 5.4
   */
  public <T> void registerClassHierarchy(Class<T> c) throws NotPossibleException {
    // process hierarchy first
    List<Class> hier = getClassHierarchy(c);
    if (hier != null) {
      boolean toRegister = false;

      //TODO: v3.2c the two for loops below seem redundant -> improve
      for (Class h : hier) {
        if (!isRegistered(h) && isDomainClass(h)) {
          // found a domain class that has not been registered -> register
          // the hierarchy
          toRegister = true; break;
        }
      }
      
      if (toRegister) {
        for (Class h : hier) {
          registerClasses(h);
        }
      }
    }
    
    // register c last
    registerClasses(c);
  }
  
  /**
   * Use this method instead of {@link #addClass(Class)} if 
   * there are two-way associations between the domain classes that need to be added. 
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
  public void registerClasses(final Class[] classes)
      throws DataSourceException, NotPossibleException, NotFoundException {
    // to create if not exists
    // the table constraints to be added afterwards
    // create tables without constraints first
    for (Class c : classes) {
      if (!classDefs.containsKey(c)) {
        // register if not yet done so
        registerClass(c);
      }
    }
  }
  
//  /**
//   * Adds a domain class to this schema.
//   * 
//   * @effects if <code>c</code> is registered in <code>this</code> and
//   *          <code>isTransient(c) = false</code> if a relational table of
//   *          <code>c</code> does not already exists then creates it
//   *          
//   *          <p>if <tt>read = true</tt> reads the records from this
//   *          table into <code>this.classExt[c]</code>
//   * 
//   *          <p>
//   *          Creates a new <code>ChangeEvent</code> object for <code>c</code>
//   *          and adds it to <code>this.changeEvents</code>.
//   * 
//   *          <p>
//   *          Throws <code>NotPossibleException</code> if <code>c</code> is not
//   *          a domain class; 
//   *  NotFoundException if <tt>c</tt> the  
//   *  required id domain attributes of the class(es) referenced by <tt>c</tt> are not found; 
//   *          <code>DBException</code> if could not operate on
//   *          the relational table of <code>c</code>.
//   * @requires <code>c != null</code> and is a registered domain class
//   * @modifies <code>classExt,</code> and <code>database_table(c)</code>
//   * 
//   * @see #registerClass(Class)
//   */
//  public void addClass(final Class c)
//      throws DataSourceException, NotPossibleException, NotFoundException {
//    if (!classDefs.containsKey(c)) {
//      registerClass(c);
//    }
//  }

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
   *  
   * @effects <pre> 
   * if <code>ic</code> is an interface at least one of whose interface operations is defined 
   * with a suitable {@link DAttr} /\ there are <code>enum</code>
   * members of <tt>ic</tt>
   *  regiter <tt>ic</tt> in this
   * else 
   *  throws <code>NotPossibleException</code> 
   * </pre>
   * @requires <code>ic</code> has enum members and at least one member
   *           implements <code>ic</code>
   * 
   * @version 
   * - 5.0: changed to use {@link #classDefs} 
   */
  public void registerEnumInterface(Class ic) throws NotPossibleException {
    if (classDefs.containsKey(ic)) {
      return;
    }

    // extracts the domain constraint defined for the name() method
    // we use this as the id constraint
    DAttr idcons = null;
    Method m;
    try {
      if (ic.isEnum()) {
        m = ic.getMethod("getName", null);
        idcons = m.getAnnotation(DC);
      } else {
        m = ic.getMethod("name", null);
        idcons = m.getAnnotation(DC);
      }
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.CLASS_NOT_WELL_FORMED, e,
          new Object[] { ic} );
    }

    if (idcons != null) {
      //List<DAttr> constraints = new ArrayList();
      //constraints.add(idcons);
      //classConstraints.put(ic, constraints);
      LinkedHashMap<Member, DAttr> map = new LinkedHashMap<>();
      map.put(m, idcons);
      classDefs.put(ic, map);
    } else {
      throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND,
          new Object[] {ic} );
    }
  }

  /**
   * This method works similar to {@link #registerEnumInterface(Class)} because 
   * an annotation is also an interface.
   *  
   * @effects <pre> 
   *  if <code>ic</code> is an annotation at least one of whose interface operations 
   *  is defined with a suitable {@link DAttr} 
   *    register <tt>c</tt> in this
   *  else throws <code>NotPossibleException</code>
   *  </pre>
   * @version 3.0 
   * @deprecated NOT YET TESTED
   */
  public void registerAnnotation(Class c) {
    if (classDefs.containsKey(c)) {
      return;
    }

    Method[] methods = c.getMethods();
    
    // find methods with the suitable domain constraint tag
    //List<DAttr> constraints = new ArrayList();
    LinkedHashMap<Member,DAttr> constraintMap = new LinkedHashMap<>();
    DAttr dc;
    for (Method m : methods) {
      dc = m.getAnnotation(DC);
      if (dc != null) {
        // found one
        //constraints.add(dc);
        constraintMap.put(m, dc);
      }
    }

    if (!constraintMap.isEmpty()) {
      //classConstraints.put(c, constraints);
      classDefs.put(c, constraintMap);
    } else
      throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND,
      new Object[] { c });
  }
  
  /**
   * Use this method for <b>utility (non-domain)</b> classes (e.g. function) that donot have attributes, only methods. 
   * 
   * @effects 
   *  Register class <tt>c</tt> with only the methods. 
   * @version 2.7.4
   */
  public void registerClassMethods(Class c) {
    if (classDefs.containsKey(c)) {
      return;
    }
    
    registerMetadataAnnotatedMethods(c);
  }
  
  /**
   * This is basically a version of {@link #registerClass(Class)}} that silently handles
   * the not-well-formed exception.
   * 
   * @effects <pre>
   *  if <tt>c</tt> is well-formed 
   *    add <code>c</code> to <code>this</code> 
   *    if c is not registered AND refers to domain-oriented types
   *      return these as <tt>Collection</tt>
   *    else
   *      return <tt>null</tt> 
   *  else
   *    do nothing
   *    return null
   *  </pre>          
   * @requires <code>c != null</code> and is a domain class
   * @modifies <code>this.classDefs, classConstraints</code>
   * 
   * @version 3.4c
   */
  public Collection<Class> registerClassIfWellFormed(Class c) {
    try {
      return registerClass(c);
    } catch (NotPossibleException e) {
      // not well-formed
      return null;
    }
  }
  
  /**
   * @effects adds <code>c</code> to <code>this</code> or throws
   *          <code>NotPossibleException</code> if <code>c</code> is not a
   *          domain class.
   *          
   *          <br>if c is not registered AND refers to domain-oriented types
   *            return these as <tt>Collection</tt>
   *          else
   *          return <tt>null</tt> 
   *            
   * @requires <code>c != null</code> and is a domain class
   * @modifies <code>this.classDefs, classConstraints</code>
   * @version 
   * - 5.0: improved to use the new {@link #classDefs} design
   */
  public Collection<Class> registerClass(Class c) throws NotPossibleException {
    if (classDefs.containsKey(c)) {
      return null;
    }

    //System.out.printf("registering: %s%n", c.getSimpleName());
    
    // extract all domain attributes (including those of the parents and
    // ancestor through the generalisation hierarchy)
    //v5.0: List<Field> fields = getAttributes(c, DC, true, true, false);
    LinkedHashMap<Field,DAttr> fields = getAnnotatedFields(c, DC, true, true, false);
    if (fields == null) {
      throw new NotPossibleException(
          NotPossibleException.Code.CLASS_NOT_WELL_FORMED, new Object[] { c });
    }
    // v5.0: classDefs.put(c, fields);

    // extract the relational attributes of c and put into a separate map
    // these include the id-attributes of the domain super-class of c (if
    // any)
    // and all the declared domain attributes of c
    Class sup = getSuperClass(c);
    //List<Field> serialisableFields;

    // get the domain attributes of the class whose serialisable property is
    // true
    //v5.0: List myFields = getAttributes(c, DC, false, true, true);
    Map<Field,DAttr> myFields = getAnnotatedFields(c, DC, false, true, true);
    LinkedHashMap<Field,DAttr> serialisableFields = new LinkedHashMap<>();//new ArrayList<>();
    if (sup != null) {
      Map<Field,DAttr> ids = getIDAttributes(sup);
      if (ids != null) {
        serialisableFields.putAll(ids);//addAll(ids);
      }
      
      if (myFields != null){
        serialisableFields.putAll(myFields);
      }
    } else if (myFields != null) {
      serialisableFields.putAll(myFields);
    }
    classSerialisableDefs.put(c, serialisableFields);

    // add domain constraints of the domain attributes to a map
    /**v2.6: also cache any associations of the class (which include
     * also the associations inherited from the super class through its domain attributes) */
    
    //v5.0: List<DAttr> dcs = new ArrayList<DAttr>();
    //LinkedHashMap<Field, DAttr> fieldMap = new LinkedHashMap<>(); // v5.0
    List<DAttr> derivingDcs = new ArrayList<DAttr>();   // v2.6.4b
    List<Tuple2<DAttr,DAssoc>> assocs = new ArrayList<Tuple2<DAttr,DAssoc>>();
    
    // DAttr dc;
    // DAssoc assoc;
    
    // v2.7.3: support returning referenced types
    List<Class> refTypes = null;
    Class refType;
    for (Entry<Field,DAttr> e : fields.entrySet()) {
      Field f = e.getKey();
      DAttr dc = e.getValue();
      //dc = (DAttr) f.getAnnotation(DC);
      //v5.0: dcs.add(dc);
      
      //fieldMap.put(f, dc);
      
      // v2.6.4.b: add support for deriving attributes
      if (dc.derivedFrom().length > 0) {
        derivingDcs.add(dc);
      }
      
      DAssoc assoc = (DAssoc) f.getAnnotation(AS);
      if (assoc != null) {
        assocs.add(new Tuple2<DAttr,DAssoc>(dc,assoc));
      }
      
      // v2.7.3
      refType = f.getType();
      if (dc.type().isCollection() || refType.isEnum() || isDomainClass(refType)) {
        // non-primitive type
        if (refTypes == null) refTypes = new ArrayList<>(fields.size());
        if (dc.type().isCollection()) {
          // collection type: get the element type
          //System.out.printf("  ref type: %s%n", refType);
          refType = getGenericCollectionType(f);
          if (refType != null)
            refTypes.add(refType);
        } else {
          refTypes.add(refType);
        }
      }
    }
    //v5.0: classConstraints.put(c, dcs);
    classDefs.put(c, fields);
    
    classDerivingAttribs.put(c,derivingDcs);  // v2.6.4b
    
    if (!assocs.isEmpty()) {
      classAssocs.put(c, assocs);
    }
    
    // v2.6.4.a: initialise metadata-annotated methods
    registerMetadataAnnotatedMethods(c);
    
    // v2.7.3: initialise change event
    ChangeEventSource dsHelper = new ChangeEventSource(c);
    ChangeEvent ce = new ChangeEvent(dsHelper);
    changeEvents.put(c, ce);
    
    // fire registered event
    fireStateChanged(c, LAName.RegisterClass);
    
    // v2.7.4: caches referenced types
    classDependencies.put(c, refTypes);
    
    // v2.7.3
    return refTypes;
  }

  /**
   * @effects 
   *  find and register all <b>public</t> {@link DOpt}-annotated {@link Method}s of <tt>c</tt>
   *  to this
   */
  private void registerMetadataAnnotatedMethods(final Class c) {
    Method[] methods = c.getMethods();
    
    // find method with the suitable Metadata tag
    // v3.3: support repeatable 
    // Opt meta; 
    DOpt[] metaOpts;
    DOpt.Type metaType;
    for (Method m : methods) {
      /* v3.3: support repeatable annotations
      meta = m.getAnnotation(METADATA);
      if (meta != null) {
      */
      metaOpts = m.getAnnotationsByType(METADATA);

      if (metaOpts.length > 0) {
        for (DOpt meta : metaOpts) {
          // found one
          metaType = meta.type();
          Map<DOpt.Type,Collection<Method>> myMethods = classMethods.get(c);
          if (myMethods == null) {
            myMethods = new HashMap<DOpt.Type,Collection<Method>>();
            classMethods.put(c, myMethods);
          }
          
          Collection<Method> typedMethods = myMethods.get(metaType);
          if (typedMethods == null) {
            typedMethods = new ArrayList<Method>();
            myMethods.put(metaType, typedMethods);
          }
          
          typedMethods.add(m);
        }
      }
    }
  }
  
  /**
   * @effects 
   *  if <tt>c</tt> is configured as a domain class (<tt>c</tt> may not be registered in this)
   *    return true
   *  else
   *    return false
   *  @version 2.7.3
   */
  public static boolean isDomainClass(Class c) {
    return hasAttribute(c, DC);
  }

  /**
   * @requires schemaNames.length > 0
   * @effects 
   *  if <tt>c</tt> is a domain class configured to be in one of the input schemas
   *    return true
   *  else
   *    return false
   *    
   * @version 3.3
   */
  public static boolean isClassInSchema(Class c, String...schemaNames) {
    String schema = getActualDomainSchema(c);
    
    if (schema != null) {
      for (String s : schemaNames) {
        if (schema.equals(s)) {
          return true;
        }
      }
    }
    
     return false;
  }
  
  /**
   * @effects 
   *  if <tt>c</tt> is a module descriptor class (i.e. annotated with {@link ModuleDescriptor})
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public static boolean isModuleDescrClass(Class c) {
    ModuleDescriptor moduleCfg = (ModuleDescriptor) c.getAnnotation(MD);
    
    return moduleCfg != null;
  }

  /**
   * @requires 
   *  c != null
   * @effects 
   *  if c is registered in this
   *    return true
   *  else
   *    return false
   */
  public boolean isRegistered(Class c) {
    return classDefs.containsKey(c);
  }  

  /**
   * @effects 
   *  update the auto-generated values of attribute <tt>attrib</tt> of the domain class <tt>c</tt>, 
   *  using <tt>minVal</tt> and <tt>maxVal</tt>, which are derived from <tt>derivingVal</tt>
   *  
   *  <p>Throws NotFoundException if no suitable update method is found;
   *  NotPossibleException if fails to perform the update method
   */
  public void updateAutoGeneratedValue(Class c, DAttr attrib,
      Tuple derivingVal, Comparable minVal, Comparable maxVal) throws NotFoundException, NotPossibleException {
    // find the metadata update method of the class c
    Method m = findMetadataAnnotatedMethodWithNamePrefix(c, DOpt.Type.AutoAttributeValueSynchroniser, null);
    
    // invoke the method
    try {
      m.invoke(null, attrib, derivingVal, minVal, maxVal);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          e, "Không thể thực thi phương thức {0}.{1}({2})", c, "updateAutoGeneratedValue", "...");
    }
  }
  
  /**
   * @effects remove <code>c</code> from <code>this</code> and if
   *          <code>deleteFromDB = true</code> then also drop the database table
   *          of <code>c</code>
   * @modifies <code>classDefs, classConstraints, classExts, changeEvents, database_table(c)</code>
   */
  private void deleteClass(Class c) throws DataSourceException {
    classDefs.remove(c);

    classSerialisableDefs.remove(c);

    //v5.0 classConstraints.remove(c);

    classReflexiveAttribs.remove(c);
    
    classDerivingAttribs.remove(c);

    classAssocs.remove(c);
    
    classDependencies.remove(c);
    
    classMethods.remove(c);
    
    reflexiveClasses.remove(c);
    
    changeEvents.remove(c);
  }

  /**
   * @effects removes the domain classes in <code>classes</code> array from
   *          <code>this</code> and if <code>deleteFromDB=true</code> then also
   *          remove them from the database
   * @version 2.8
   *  - changed parameters: use List and remove boolean
   */
  public void deleteClasses(List<Class> classList)
      throws DataSourceException {
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

        deleteClass(c);
        if (debug)
          System.out.println("...ok");
      } catch (DataSourceException e) {
        // check number of attempts
        Integer attempts = retryMap.get(c);
        if (attempts != null && attempts >
          numClasses //classList.size()
        ) {
          // impossible to retry (something really wrong, e.g. class is
          // orphaned)
          // stop
          throw (e);
        } else {
          // record number of attempts
          if (attempts == null)
            attempts = 1;
          else
            attempts = attempts + 1;

          retryMap.put(c, attempts);
        }
        if (debug)
          System.out.println("...failed (to retry)");

        // e.printStackTrace();

        // perhaps caused by dependency, move table to the end of the list
        // to try again later
        classList.add(c);
      }
    }
  }

  /**
   * @effects 
   *  if there are domain classes in this
   *    return an Iterator of them
   *  else
   *    return null
   * @version 2.7.3
   */
  public Iterator<Class> getDomainClasses() {
    if (!classDefs.isEmpty()) {
      return classDefs.keySet().iterator();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  look up all non-object super and ancestor classes of <tt>c</tt> and 
   *  return them as <tt>List</tt>: 
   *  elements, excluding <tt>c</tt> are added from the <b>top down</b> (i.e. a super-class is added before its sub-class)
   *  
   *  <p>If no such classes are found return <tt>null</tt>.
   *  
   *  <p>Note: the classes in the hierarchy may not be registered in this
   */
  public static List<Class> getClassHierarchy(Class c) {
    List<Class> h = null;

    Class sup;
    Class curr = c;
    do {
      // v2.8: use static method 
      // sup = getSuperClass(curr, false);
      sup = getNonObjectSuperClass(curr);
    
      if (sup != null) {
        if (h == null) h = new ArrayList();
        h.add(0,sup);
        
        curr = sup;
      }
    } while (sup != null);
    
    return h;
  }
  
  /**
   * The part that extracts the dependencies in this method is exactly the same as {@link #registerClass(Class)}. 
   * 
   * @effects 
   *  if <tt>c</tt> references other domain classes
   *    return these as Collection
   *  else
   *    return null
   * @see {@link #registerClass(Class)}
   */
  public List<Class> getClassDependencies(Class c) {
    // if already processed class dependencies of c return them
    // otherwise keep the dependencies in buffer for re-use
    List<Class> dependencies = null;
    
    if (!classDependencies.containsKey(c)) {
      // not yet processed 
      //v5.0: List<Field> fields = getAttributes(c, DC, true, true, false);
      Map<Field,DAttr> fields = getAnnotatedFields(c, DC, true, true, false);
      if (fields != null) {
        // a proper domain class
        Class refType;
        DAttr dc;
        for (Field f : fields.keySet()) {
          dc = (DAttr) f.getAnnotation(DC);
          
          // v2.7.3
          refType = f.getType();
          if (dc.type().isCollection() || refType.isEnum() || isDomainClass(refType)) {
            // non-primitive type
            if (dependencies == null) dependencies = new ArrayList<>(fields.size());
            if (dc.type().isCollection()) {
              // collection type: get the element type
              //System.out.printf("  ref type: %s%n", refType);
              refType = getGenericCollectionType(f);
              if (refType != null)
                dependencies.add(refType);
            } else {
              dependencies.add(refType);
            }
          }
        }
      }
      
      classDependencies.put(c, dependencies);
    } else {
      // already processed (may be null)
      dependencies = classDependencies.get(c);
    }
    
    return dependencies;
  }
  
  /**
   * @effects 
   *  if attribute.format is supported
   *    return a Format object from it
   *  else
   *    throw NotImplementedException
   *  @version 2.7.3
   */
  public Format getAttributeFormat(DAttr attribute) throws NotImplementedException {
    DAttr.Format formatSpec;
    String formatString;
    Type type;
    
    formatSpec = attribute.format();
    type = attribute.type();
    
    if (formatSpec.isNull()) {
      formatString = null;
    } else {
      formatString = formatSpec.getFormatString();
    }
    
    if (formatString == CommonConstants.NullString) {
      // no format string
      formatString = null;
    }
    
    // use the data type to determine the value format
    Locale currentLocale = Locale.getDefault();
    Format format;

    if (type.isDate()) {
      if (formatString != null)
        format = new SimpleDateFormat(formatString, currentLocale);
      else
        format = new SimpleDateFormat();
      return format;
    } 
    else if (type.isInteger()) {
      // need to separate this for integer type
      //TODO test currency format
//        if (formatSpec.isCurrency()) {
//          format = NumberFormat.getCurrencyInstance();
//        } else {
        format = NumberFormat.getIntegerInstance();
//        }
        return format;
    } 
    else if (type.isNumeric()) {
      // other numeric type use number format
      //TODO: test currency format
//        if (formatSpec.isCurrency()) {
//          format = NumberFormat.getCurrencyInstance();
//        } else {
        format = NumberFormat.getNumberInstance();
//        }
        return format;
    }
    // add other cases here
    else {
      // not supported
      throw new NotImplementedException(NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED,  
          type.name());
    }
  }
  
  /**
   * @version 2.7.4
   * @effects if could not execute the <b>default
   *          constructor</b> of <tt>c</tt> throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>
   * @deprecated since v5.4: use ObjectFactory instead
   */
  @Deprecated
  public <T> T newInstance(Class<T> c) throws NotPossibleException {
    try {
      return c.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {c.getSimpleName(), ""});
    }
  }
  
  /**
   * @requires 
   *  valsMap != null /\ !valsMap.isEmpty()
   *  
   * @effects if no constructors of the domain class <code>c</code> exist for
   *          the arguments <code>attributeVals</code> throws
   *          <code>NotFoundException</code>, else if could not execute the
   *          constructor throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>, the values of whose domain
   *          attributes are initialised to the elements of
   *          <code>attributeVals</code>;
   * @version 
   * - 5.0: redesigned to be more flexible (makes use of {@link AttrRef})
   * @deprecated since v5.4: use ObjectFactory instead
   */
  @Deprecated
  public <T> T newInstance(Class<T> c, Map<DAttr, Object> valsMap) 
    throws NotFoundException, NotPossibleException {

    // first look in c for the constructor whose parameters are annotated with AttrRefs to the attributes 
    // in valsMap
    Constructor<T> co = findConstructorMethod(c, valsMap.keySet());
    
    Object[] valArr = null;
    
    if (co != null) {
      // found a constructor, use it 
      try {
        // create valArr according to the constructor's parameter ordering
        valArr = new Object[valsMap.size()];
        Parameter[] params = co.getParameters();
        Set<Entry<DAttr,Object>> entries = valsMap.entrySet();
        int idx = 0;
        for (Parameter param : params) {
          String attrRef = param.getAnnotation(AttrRef.class).value();
          Object val = null;
          for (Entry<DAttr, Object> e : entries) {  // find the value for param
            if (e.getKey().name().equals(attrRef)) {
              val = e.getValue(); break;
            }
          }
          valArr[idx++] = val;
        }
        T o = co.newInstance(valArr);
        
        return o;
      } catch (InstantiationException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
              e, new Object[] {c, "init", Arrays.toString(valArr)}
              );
      }
    } else {
      valArr = valsMap.values().toArray(new Object[0]);
      // no such constructor exists, fall back to the primitive method of using the values to 
      // find the best constructor that matches them
      return newInstance(c, valArr);
    }
  }
  
  /**
   * This is an OLD method. Uses {@link #newInstance(Class, Map)} instead. 
   * 
   * @requires 
   *  attributeVals != null
   *  
   * @effects if no constructors of the domain class <code>c</code> exist for
   *          the arguments <code>attributeVals</code> throws
   *          <code>NotFoundException</code>, else if could not execute the
   *          constructor throws <code>NotPossibleException</code>, else returns
   *          a new instance of <code>c</code>, the values of whose domain
   *          attributes are initialised to the elements of
   *          <code>attributeVals</code>;
   * @version 
   * - 3.3: made static
   * @deprecated since v5.4: use ObjectFactory instead
   */
  @Deprecated
  public static <T> T newInstance(Class<T> c, Object[] attributeVals)
      throws NotFoundException, NotPossibleException {

    T o = null;
    try {

      // create a new object using the default constructor method
      Constructor[] cons = c.getDeclaredConstructors();

      // find the constructor that has the same signature as the attributes
      // specified in values
      Constructor co = null;
      Class[] paramTypes;
      
      // v2.7.3: keep track of attribute types to use in exception message (if needed)
      Class[] attributeTypes = new Class[attributeVals.length];
      
      OUTER: for (int i = 0; i < cons.length; i++) {
        co = cons[i];
        paramTypes = co.getParameterTypes();
        if (paramTypes.length == attributeVals.length) {
          boolean match = true;
          CONS: for (int k = 0; k < paramTypes.length; k++) {
            Class type = paramTypes[k];
            Object obj = attributeVals[k];
            Class oc = (obj != null) ? obj.getClass() : null;
            attributeTypes[k] = oc; // v2.7.3
            
            // compare the object type with the parameter type
            if (obj == null)
              continue; // consider a match

            // v3.0: if (!type.equals(oc) && !isDecendant(oc, type)) {
            if (!isDecendant(oc, type)) {
              match = false;
              break CONS;
            }
          } // end CONS loop
          if (match) {
            // found the constructor
            break OUTER;
          }
        }
        co = null;
      } // end OUTER loop

      if (co == null) {
        throw new NotFoundException(
            NotFoundException.Code.CONSTRUCTOR_METHOD_NOT_FOUND,
            new Object[] {c,
            "\nArgs: " + Arrays.toString(attributeVals) + "\nTypes: " +  
            Arrays.toString(attributeTypes)});
      }

      // System.out.println("constructor: " + co);
      // create a new object
      o = (T) co.newInstance(attributeVals);
    } catch (InstantiationException e) {
      // e.printStackTrace();
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          // v3.3: "Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    } catch (IllegalAccessException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          // v3.3: "Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    } catch (InvocationTargetException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          // v3.3: e.getCause(),"Không thể thực thi phương thức: {0}({1})", c.getName(), ""
          e, new Object[] {c, "init", Arrays.toString(attributeVals)}
          );
    }

    return o;
  }

// v3.1: moved to DOMBasic
//  /**
//   * @requires 
//   *  cls != null /\ attrib != null /\ attrib is a domain attribute of cls /\ 
//   *  attrib.defaultValueFunction = true
//   * @effects 
//   *  if exists the specification of a default value of <tt>attrib</tt> in <tt>cls</tt>
//   *    execute it and return value
//   *  else
//   *    return null
//   *  @version 2.7.4
//   */
//  public Object getAttributeValueDefault(Class cls, DomainConstraint attrib) 
//      throws NotFoundException, NotPossibleException {
//    // use either defaultValue or defaultValueFunction
//    Object defVal = null;
//    if (!attrib.defaultValue().equals(MetaConstants.NullString)) {
//      //v3.1: need to convert value first 
//      Object rawVal = attrib.defaultValue();
//      //TODO: retrieve attribute's type if needed
//      Class attribDeclaredType = null;
//      try {
//        defVal = Toolkit.parseDomainValue(attrib, attribDeclaredType, rawVal);
//      } catch (ConstraintViolationException e) {
//        if (debug)
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
//            e, new Object[] {cls, "getAttributeValueDefault", attrib.name()});
//      }
//    } else if (attrib.defaultValueFunction()) {
//      // use function
//      Method m = null;
//      // find the default value function of the class c
//      try {
//        m = findMetadataAnnotatedMethod(cls, attrib.name(), Metadata.Type.MethodDefaultValueFunction);
//      } catch (NotFoundException e) {
//        // not found
//        if (debug) throw e;
//      }
//      
//      if (m != null) {
//        // invoke the method
//        try {
//          // m is a static method
//          defVal = m.invoke(null);
//        } catch (Exception e) {
//          // failed to perform function
//          if (debug) {
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
//                e, new Object[] {cls, "getAttributeValueDefault", attrib.name()});
//          }
//        }
//      }
//    }
//    
//    return defVal;
//  }

  /**
   * @effects 
   *  if exists {@link DomainValueDesc} defined for <tt>attrib</tt> of <tt>cls</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   *    
   * @version 3.2c
   * @see {@link #getDomainAttributeAnnotation(Class, Class, String)}
   */
  public DomainValueDesc getAttributeDefaultValueDesc(Class cls,
      DAttr attrib) {
    DomainValueDesc valueDesc = getDomainAttributeAnnotation(cls, DomainValueDesc.class, attrib.name());
    return valueDesc;
  }
  
  /**
   * @requires 
   *  cls != null /\ o != null /\ vals != null
   * @effects 
   *  return an ordered map of the values of the mutable attributes in the domain object <tt>o</tt> of the 
   *  domain class <tt>cls</tt> whose values are different from those 
   *    specified in <tt>vals</tt> (the order is same as attribute order in <tt>cls</tt>)
   *  
   *  <p>Throws NotFoundException if a domain attribute is not found. 
   */
  public LinkedHashMap<DAttr,Object> getAttributeValues(final Class cls, final Object o, 
      final LinkedHashMap<DAttr,Object> vals) throws NotFoundException {
    LinkedHashMap<DAttr,Object> oldVals = new LinkedHashMap();
    
    String fname = null;
    Field f = null;
    Object newVal, oldVal;
    DAttr dc;
    for (Entry<DAttr,Object> entry : vals.entrySet()) {
      dc = entry.getKey();
      // extract object values for the mutable attributes
      if (dc.mutable()) {
        newVal = entry.getValue();
        fname = dc.name();
        f = getDomainAttribute(cls, fname);
        
        // only record in the result if value has been changed 
        oldVal = getAttributeValue(f, o);
        if ((oldVal == null && newVal != null) ||
            (oldVal != null && !oldVal.equals(newVal))) {
          // changed
          oldVals.put(dc, oldVal); 
        }
      }
    }
    
    return oldVals.isEmpty() ? null : oldVals;
  }

  /**
   * @effects returns the <code>Object</code> value of the attribute
   *          <code>boundedAttribute</code> which is bounded to the attribute
   *          <code>attribute</code> of a domain object <code>o</code>.
   * 
   *          <p>
   *          Example: if
   * 
   *          <pre>
   * o = Student(1, &quot;Nguyen Van A&quot;, Class(&quot;2c09&quot;))
   * </pre>
   * 
   *          where
   * 
   *          <pre>
   * Student:id,name,class
   * </pre>
   * 
   *          and
   * 
   *          <pre>
   * Class:id
   * </pre>
   * 
   *          then the method returns <code>2c09</code> for
   *          <code>attribute="class"
   *           </code> and <code>boundedAttribute="id"</code>
   */
  public Object getBoundedAttributeValue(Object o, String attribute,
      String boundedAttribute) {
    Object attributeVal = getAttributeValue(o, attribute);

    if (attributeVal != null) {
      return getAttributeValue(attributeVal, boundedAttribute);
    }

    return null;
  }

  /**
   * This method works the same as {@link #getAttributeValue(Object, String)}. 
   * 
   * @effects returns the value of the domain attribute <code>attrib</code>
   *          of the object <code>obj</code>; throws
   *          <code>NotPossibleException</code> of an error occurred while
   *          getting the attribute value.
   * 
   * @requires <code>o.class</code> is a domain class and has a getter method
   *           named <code>"get"+ name</code> (first letter capitalised).
   */
  public Object getAttributeValue(Class c, Object obj, DAttr attrib)
      throws NotPossibleException {
    // find getter method for the attribute and invoke it
    String attribName = attrib.name();
    try {
      Method getter = findGetterMethod(attribName, c);
      if (getter != null)
        return getter.invoke(obj, null);
      else
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
            new Object[] {c, "getter_" + attribName});
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          new Object[] {obj, "getter_" + attribName, ""});
    }
  }
  
  /**
   * This method works the same as {@link #getAttributeValue(Object, String)} except that it supports
   * the use of a {@link Filter} to filter through the values 
   * 
   * @effects returns the value(s) of the domain attribute <code>attrib</code>
   *          of the object <code>obj</code> that satisfis <tt>valFilter</tt> (if specified)
   *          
   *          <br>If no value is found or no value satifies <tt>valFilter</tt> 
   *            return <tt>null</tt>
   *            
   *          <p>throws
   *          <code>NotPossibleException</code> of an error occurred while
   *          getting the attribute value.
   * 
   * @requires <code>o.class</code> is a domain class and has a getter method
   *           named <code>"get"+ name</code> (first letter capitalised).
   *           
   * @version 3.3
   */
  public Object getAttributeValue(Class c, Object obj, DAttr attrib, Filter valFilter)
      throws NotPossibleException {
    // find getter method for the attribute and invoke it
    String attribName = attrib.name();
    try {
      Method getter = findGetterMethod(attribName, c);
      if (getter != null) {
        Object val = getter.invoke(obj, null);
        
        if (val != null) {
          if (val instanceof Collection) {
            // value is a collection
            Collection col = (Collection) val;
            Collection filtered = col.getClass().newInstance();
            for (Object v : col) {
              if (valFilter.check(v)) {
                // satisfies filter
                filtered.add(v);
              }
            }
            
            return (filtered.isEmpty()) ? null : filtered;
          } else {
            // not a collection
            if (valFilter.check(val)) {
              return val;
            } else {
              return null;
            }
          }
        } else {
          // no value found
          return null;
        }
      }
      
      else
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
            new Object[] {c, "getter_" + attribName});
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          new Object[] {obj, "getter_" + attribName, ""});
    }
  }
  
  /**
   * @effects returns the value of the domain attribute <code>fieldName</code>
   *          of the object <code>obj</code>; throws
   *          <code>NotPossibleException</code> of an error occurred while
   *          getting the attribute value.
   * 
   * @requires <code>o.class</code> is a domain class and has a getter method
   *           named <code>"get"+ name</code> (first letter capitalised).
   */
  public Object getAttributeValue(Object obj, String fieldName)
      throws NotPossibleException {
    // find getter method for the field and invoke it
    try {
      Method getter = findGetterMethod(fieldName, obj.getClass());
      if (getter != null)
        return getter.invoke(obj, null);
      else
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
            "Không tìm thấy phương thức: {0}({1})", "getter_" + fieldName, "");
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          "Không thể thực thi phương thức: {0}.{1}({2})", obj, "getter_" + fieldName, "");
    }
  }

  /**
   * @effects returns the <code>Object</code> value of field <code>f</code> of
   *          the domain object <code>o</code>; throws
   *          <code>NotPossibleException</code> if an error occured; 
   *          NotFoundException if could not find a suitable getter method.
   * 
   * @requires a getter method for <code>f</code> is defined in <code>obj</code>
   */
  public Object getAttributeValue(Field f, Object obj)
      throws NotPossibleException, NotFoundException {
    // find getter method for the field and invoke it
    /*v2.7.4: fixed
    try {
      Method getter = findGetterMethod(f, obj.getClass());
      if (getter != null)
        return getter.invoke(obj, null);
      else
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND,
            "Không tìm thấy phương thức: {0}({1})", "getter_" + f.getName(), "");
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          "Không thể thực thi phương thức: {0}({1})", "getter_" + f.getName(),
          "");
    }
    */
    Method getter = findGetterMethod(f, obj.getClass());
    
    try {
      return getter.invoke(obj, null);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          "Không thể thực thi phương thức: {0}({1})", "getter_" + f.getName(),
          "");
    }
  }

  /**
   * @effects returns an <code>Object[]</code> array of the values of the domain
   *          attributes of the domain object <code>o</code> (in the order of
   *          the attribute declarations), or <code>null</code> if no attributes
   *          are found.
   * 
   * @requires <code>o.class</code> is a domain class and has a getter method
   *           named <code>"get"+ name</code> (first letter capitalised) for
   *           each of its domain attribute <code>name</code>.
   * 
   */
  public List getAttributeValues(Object o) throws NotPossibleException {
    Map<Field,DAttr> fields = getDomainAttributes(o.getClass());

    List vals = new ArrayList();
    if (fields != null) {
      for (Field f : fields.keySet()) {
        vals.add(getAttributeValue(f, o));
      }
    }

    return (vals != null) ? vals : null;
  }
  
  /**
   * A short-cut to {@link #getAttributeValuesAsMap(Object, String[])} with 
   * 2nd argument set to null. 
   * 
   * @effects
   *  read values of all domain attributes of <tt>obj</tt> and return them as <tt>Map(attribute,val)</tt>; 
   *  or return <tt>null</tt> if no domain attributes are found.
   *  
   *  <p>The entries in the returned Map are in the same order as the domain attributes.
   *  
   *  <p>Throws NotPossibleException if failed to obtain an attribute's value.
   * @version 5.4
   */
  public Map<DAttr,Object> getAttributeValuesAsMap(Object obj) throws NotPossibleException {
    return getAttributeValuesAsMap(obj, null);
  }
  
  /**
   * @requires 
   *  obj != null /\ 
   *  attribNames != null  -> attribNames contains valid attribute names of obj.class
   * 
   * @effects 
   *  read the values of all domain attributes of <tt>obj</tt> and return them as <tt>Map(attribute,val)</tt>; 
   *  or return <tt>null</tt> if no domain attributes are found.
   *  
   *  <p>If <code>attribNames != null</code> then only return entries for the matching attributes.
   *  
   *  <p>The entries in the returned Map are in the same order as the domain attributes.
   *  
   *  <p>Throws NotPossibleException if failed to obtain an attribute's value.
   *  
   * @version 2.7.4
   * - 3.0: added param attribNames 
   */
  public Map<DAttr,Object> getAttributeValuesAsMap(Object obj, String[] attribNames) throws NotPossibleException {
   LinkedHashMap<DAttr,Object> vals = new LinkedHashMap();
    
   Class c = obj.getClass();
   
   Map<Field,DAttr> fields = getDomainAttributes(c);

   if (fields != null) {
     DAttr attrib;
     Object val;
     String fieldName;
     boolean found;
     //for (Field field : fields) {
       //attrib = field.getAnnotation(DC);
     for (Entry<Field,DAttr> entry : fields.entrySet()) {
       Field field = entry.getKey();
       attrib = entry.getValue();
       fieldName = attrib.name();
       found = true;
       if (attribNames != null) {
         found = false;
         for (String attribName : attribNames) {
           if (attribName.equals(fieldName)) {
             // found
             found = true; break;
           }
         }
       }
       
       if (found) {
         try {
           val = getAttributeValue(field, obj);
           vals.put(attrib, val);
         } catch (NotFoundException e) {
           // no getter defined -> ignore
           log(e, "");
         }
       }
     }
   }

    return vals.isEmpty() ? null : vals;
  }
  
  /**
   * @requires 
   *  cls is a registered domain class /\ 
   *  attrib != null /\ attrib is a derived attribute 
   *  attribVal != null /\ attribVal is a valid value of attrib 
   *  
   * @effects
   *  parse <tt>attribVal</tt> into array <tt>Object[]</tt>, whose elements are values 
   *  of each deriving attribute of <tt>attrib</tt> of <tt>cls</tt> and following the 
   *  parsing rule specified in <tt>cls</tt>; return the result.
   *  
   *  <p>throws NotFoundException if parsing rule is not specified in <tt>cls</tt>;
   *  NotPossibleException if failed to parse value
   *  
   * @version 3.1
   */
  public Object[] parseDerivingAttributeValues(Class cls,
      DAttr attrib, Object attribVal) throws NotFoundException, NotPossibleException {
    String[] deriveFrom = attrib.derivedFrom();
    
    if (deriveFrom.length==0) {
      return null;
    }
    
    // ASSUME: parsing rule is defined in a static method of cls
    Method parseMethod = findMetadataAnnotatedMethod(cls, 
          DOpt.Type.DerivedAttributeParser, attrib);
    
    Object result = null;
    try {
      // look up deriving attributes
      DAttr[] derivingAttributes = new DAttr[deriveFrom.length];
      DAttr derAttrib; int index = 0;
      for (String derAttribName : deriveFrom) {
        derAttrib = getDomainConstraint(cls, derAttribName);
        derivingAttributes[index] = derAttrib;
        index++;
      }
      
      // invoke parser method
      result = parseMethod.invoke(null, attrib, attribVal, derivingAttributes);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
        new Object[] {cls.getSimpleName(), parseMethod.getName(), attribVal});
    }
    
    if (result == null)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PARSE_ATTRIBUTE_VALUE, 
          new Object[] {cls.getSimpleName(), attrib.name(), attribVal});
    
    if (result instanceof Object[]) {
      return (Object[]) result;
    } else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_RETURN_TYPE, 
          new Object[] {result.getClass(), Object[].class});
    }
  }
  
  /**
   * @effects returns the domain sub-classes of the domain class <code>c</code>
   *          or <code>null</code> if <code>c</code> does not have any
   *          sub-classes.
   */
  public Class[] getSubClasses(Class c) {
    Collection<Class> classes = classDefs.keySet();
    List<Class> subs = new ArrayList<Class>();
    for (Class cls : classes) {
      if (cls.getSuperclass().equals(c)) {
        subs.add(cls);
      }
    }

    if (!subs.isEmpty())
      return subs.toArray(new Class[subs.size()]);
    else
      return null;
  }

  /**
   * @effects
   *  if exists sub-classes that implement <tt>c</tt> or sub-interfaces of <tt>c</tt>
   *    return them as a Collection
   *  else
   *    return null
   */
  public Collection<Class> getImplementingSubClasses(Class c) {
    Collection<Class> classes = classDefs.keySet();
    List<Class> subs = new ArrayList<Class>();
    
    for (Class cls: classes) {
      try {
        if (cls.asSubclass(c) != null) {
          subs.add(cls);
        }
      } catch (ClassCastException e) {
        // ignore
      }
    }
    
    if (!subs.isEmpty())
      return subs;
    else
      return null;

  }
  
  /**
   * @effects if <code>c.super</code> is a domain class then return
   *          <code>c.super</code>, else returns <code>null</code>
   */
  public Class getSuperClass(Class c) {
    return getSuperClass(c, true);
  }

  /**
   * @effects 
   *  if exist a non-Object super class of c AND if <tt>registeredOnly = true</tt> and this class is registered in this 
   *    return it
   *  else
   *    return </tt>null</tt>
   */
  public Class getSuperClass(Class c, boolean registeredOnly) {
    Class sup = c.getSuperclass();

    if (sup != Object.class && 
        (!registeredOnly || classDefs.keySet().contains(sup))) {
      return sup;
    } else
      return null;
  }
  
  /**
   * @effects 
   *  if exist a non-Object super class of c AND if <tt>registeredOnly = true</tt> and this class is registered in this 
   *    return it
   *  else
   *    return </tt>null</tt>
   */
  public static Class getNonObjectSuperClass(Class c) {
    Class sup = c.getSuperclass();

    if (sup != Object.class) {
      return sup;
    } else
      return null;
  }
  
  /**
   * <b>Note:</b>The output of this method is used as the name of the class store (e.g. relational table name) 
   * in the data source  
   * 
   * @effects returns the standard domain name of a class <code>c</code>.
   * 
   */
  public String getDomainClassName(Class c) {
    /**
     * if class constraint is defined and the domain schema is specified 
     *  then use it together with the classes' simple name to construct the class name
     * else
     *  use the part of the class name after the "model." prefix (if any)
     *  together with the classes' simple name to construct the class name  
     */
    /**version 2.5.2: use class constraint first */
    DClass cc = (DClass) c.getAnnotation(CC);
    if (cc != null) {
      // has class constraint
      String schemaName = cc.schema();
      if (!schemaName.equals(DCSLConstants.DEFAULT_SCHEMA)) {
        return schemaName + "." + c.getSimpleName();
      } else {
        return c.getSimpleName();
      }
    } else {
      // no class constraint
      String[] names = parseClassName(c);
      if (names.length > 1) {
        return names[0] + "." + names[1];
      } else {
        return names[0];
      }
    }
  }

  /**
   * @effects 
   *  return the logical resource name of the domain class <tt>c</tt>
   */
  public String getResourceNameFor(Class c) {
    return getDomainClassName(c);
  }
  
  /**
   * @effects 
   *  return the logical resource name of the attribute <tt>c.attrib</tt>
   */
  public String getResourceNameFor(Class c, String attrib) {
    return getResourceNameFor(c)+"." + attrib;  
  }
  
  /**
   * @requires 
   *  c != null /\ c is a domain class
   *  
   * @effects 
   *  if c is a proper wrapper class
   *    return its base class
   *  else
   *    return <tt>null</tt>
   */
  public Class getWrappedBaseClass(Class c) {
    DClass cc = (DClass) c.getAnnotation(CC);
    if (cc != null) {
      Class baseCls = cc.wrapperOf();
      
      if (baseCls != CommonConstants.NullType) {
        return baseCls;
      }
    }
    
    return null;
  }
  
  /**
   * This differs from {@link #getDomainSchema(Class)} in that it returns the actual configured schema name of the input
   * domain class. The other method returns the "standardised" domain schema name. 
   * 
   * @effects 
   *  return the <b>actual</b> (configured) name of the domain schema to which the class <tt>c</tt> belongs
   *  or null if <tt>c</tt> belongs to the default schema.
   */
  public static String getActualDomainSchema(Class c) {
    DClass cc = (DClass) c.getAnnotation(CC);
    if (cc != null) {
      // has class constraint
      return cc.schema();
    } else {
      return null;
    }
  }
  
  /**
   * The domain schema of a class (if specified) is either written in the class constraint 
   * annotation of that class or the part of the package name after the model prefix
   * 
   * @effects 
   *  return the name of the domain schema to which the class <tt>c</tt> belongs
   *  or null if <tt>c</tt> belongs to the default schema.
   */
  public String getDomainSchema(Class c) {
    /**
     * if class constraint is defined and the domain schema is specified 
     *  then use it to construct the class name
     * else
     *  use the part of the class name after the "model." prefix
     *  to construct the class name  
     */
    DClass cc = (DClass) c.getAnnotation(CC);
    if (cc != null) {
      // has class constraint
      String schemaName = cc.schema();
      if (!schemaName.equals(DCSLConstants.DEFAULT_SCHEMA)) {
        return schemaName.toUpperCase(); //v2.7.3: schemaName;
      } else {
        return null;  // default schema
      }
    } else {
      // no class constraint
      String[] names = parseClassName(c);
      if (names.length > 1) {
        return names[0].toUpperCase(); //v2.7.3: names[0];
      } else {
        return null;
      }
    }
  }
  
  private String[] parseClassName(Class c) {
    String simpleName = c.getSimpleName();
    Package pkg = c.getPackage();
    String pkgName = pkg.getName(); // fully qualified
    int index = pkgName.indexOf("model.");
    String[] names;
    if (index > -1) {
      // has subpackage of model
      names = new String[2];
      String subName = pkgName.substring(index + 6);
      // replaces any dots in name by underscores
      names[0] = subName.replaceAll("\\.", "_");
      names[1] = simpleName;
    } else {
      names = new String[1];
      names[0] = simpleName;
    }
    return names;
  }

  /**
   * @effects 
   *  if c is a registered domain class AND is configured with a module-descriptor class m
   *    return m
   *  else
   *    return null
   */
  public static Class getModuleDescriptor(Class c) {
    DClass dm = (DClass) c.getAnnotation(CC);
    if (dm != null) {
      Class moduleDescrCls = dm.moduleDescriptor();
      if (moduleDescrCls != CommonConstants.NullType) {
        return moduleDescrCls;
      }
    }
    
    // not specified
    return null;
  }
  
  /**
   * @effects 
   *  if mcc is configured with a module-descriptor annotation dm
   *    return dm
   *  else
   *    return null
   */
  public static ModuleDescriptor getModuleDescriptorObject(Class mcc) {
    ModuleDescriptor dm = (ModuleDescriptor) mcc.getAnnotation(MD);
    
    return dm;
  }
  
  /**
   * @requires 
   *   <tt>mcc</tt> is a valid MCC (i.e. a {@link Class} attached with a valid {@link ModuleDescriptor})
   *    
   * @effects 
   *   return <tt>ModuleDesc(mcc).modelDesc.model</tt> or 
   *   throws NotPossibleException if <tt>mcc</tt> is not a valid MCC or is not configured with a domain class 
   * @version 5.1
   */
  public static Class getDomainClass(Class mcc) throws NotPossibleException {
    ModuleDescriptor md = getModuleDescriptorObject(mcc);
    
    Class c = null;
    if (md != null) {
      c = md.modelDesc().model();
      
      if (c.equals(CommonConstants.NullType)) c = null;
    } 
    
    if (c == null) {
      throw new NotPossibleException(NotPossibleException.Code.MODULE_NOT_WELL_FORMED, new Object[] {mcc.getSimpleName(), "domain class not found"}) ;
    } else {
      return c;
    }
  }
  
  /**
   * This works similar to {@link #getDomainAttributeAnnotation(Class, Class, String)}, except that 
   * it extracts annotations that are attached to class instead of to attributes.
   * 
   * @effects 
   *  if exists {@link Annotation} typed <tt>anType</tt> that is defined for <tt>cls</tt>
   *    return it
   *  else
   *    return null
   *
   * @version 3.3
   */
  public <T extends Annotation> T getDomainClassAnnotation(Class cls, Class<T> anType) {
    T an = (T) cls.getAnnotation(anType);
    
    if (an != null) {
      return an;
    } else {
      return null;
    }
  }
  
  /**
   * @effects displays the content of this schema out on the command line
   * @modifies <code>System.out</code>
   */
  // TODO: improves this
  public void listSchema() {
    System.out.println("CLASS DEFINITIONS");
    System.out.println("=========================================");
    System.out.println(classDefs);
//    System.out.println("CLASS CONSTRAINTS");
//    System.out.println("=========================================");
//    System.out.println(classConstraints);
//    System.out.println("CLASS OBJECTS");
//    System.out.println("=========================================");
//    System.out.println(classExts);
  }

  // //////// UTILITY methods /////
  /**
   * @effects returns a {@link Map} of the domain attributes of a domain
   *          class <code>c</code>.
   *          <p>
   *          If no fields are found then return <code>null</code>.
   *          
   *          <p>The attributes are returned in the order that they are declared in <tt>c</tt>.
   *          
   * @see #addClass(Class, boolean) (for how these attributes were extracted)
   * @version 
   * - 5.0: changed to return {@link Map}
   */
  public Map<Field,DAttr> getDomainAttributes(final Class c) {
    Map<Field,DAttr> a = (Map<Field,DAttr>) classDefs.get(c);
    if (a == null || a.isEmpty())
      return null;
    else
      return a;
  }

  /**
   * @effects returns the domain attributes of a domain class <code>c</code>
   *          whose domain constraint's auto is <code>true</code>, or
   *          <code>null</code> if no such attributes exist.
   */
  public Collection<DAttr> getAutoDomainAttributes(final Class c) {
//    List<Field> a = classDefs.get(c);
//    if (a == null || a.isEmpty()) {
//      return null;
//    } else {
//      // extract the auto attributes
//      List<Field> sa = new ArrayList();
//      DomainConstraint dc;
//      for (Field f : a) {
//        dc = f.getAnnotation(DC);
//        if (dc.auto()) {
//          sa.add(f);
//        }
//      }
//
//      if (!sa.isEmpty())
//        return sa;
//      else
//        return null;
//    }
    Collection<DAttr> a = getDomainConstraints(c); //classConstraints.get(c);
    if (a == null || a.isEmpty()) {
      return null;
    } else {
      // extract the auto attributes
      Collection<DAttr> sa = new ArrayList();
      for (DAttr dc : a) {
        if (dc.auto()) {
          sa.add(dc);
        }
      }

      if (!sa.isEmpty())
        return sa;
      else
        return null;
    }

  }
  
  /**
   * @effects returns <b>ALL</b> domain attributes of a domain class <code>c</code>
   *          whose domain constraint's serialisable is <code>true</code>, or
   *          <code>null</code> if no such attributes exist.
   *          
   *          <p>These include attributes declared in <tt>c</tt> and those inherited
   *          from the ancestor classes of <tt>c</tt> (if any).
   */
  public Map<Field,DAttr> getSerialisableDomainAttributes(final Class c) {
    Map<Field,DAttr> a = (Map<Field,DAttr>) classDefs.get(c);
    if (a == null || a.isEmpty()) {
      return null;
    } else {
      // extract the serialisable attributes
      Map<Field,DAttr> sa = new LinkedHashMap<>();
      DAttr dc;
      //for (Field f : a) {
      for(Entry<Field,DAttr> entry : a.entrySet()) {
        Field f = entry.getKey();
        dc = entry.getValue(); //f.getAnnotation(DC);
        if (dc.serialisable()) {
          //sa.add(f);
          sa.put(f, dc);
        }
      }

      if (!sa.isEmpty())
        return sa;
      else
        return null;
    }
  }

  /**
   * This method returns a sub-set of the result returned by {@link #getSerialisableDomainAttributes(Class)}, 
   * which includes only the attributes that are used in the class store of a domain class.
   * 
   * @effects returns a <code>Map</code> of the domain attributes of a domain
   *          class <code>c</code> that are used in the class store structure 
   *          (e.g. relational table columns) of <code>c</code> in the underlying data source.
   * 
   *          <p>
   *          If no fields are found then returns <code>null</code>.
   *          
   *          <p>The fields are returned in the same order as they are declared in <tt>c</tt>.
   *          
   * @see #registerClass(Class) (for how these attributes were extracted)
   * @version
   * - 5.0: changed to return {@link Map}
   */
  public Map<Field,DAttr> getSerialisableAttributes(final Class c) {
    LinkedHashMap<Field,DAttr> a = classSerialisableDefs.get(c);
    if (a == null || a.isEmpty())
      return null;
    else
      return a;
  }


  
  /**
   * @effects returns <code>true</code> if <code>c</code> has an auto id field,
   *          i.e. <code>DomainConstraint.id = true</code> for this field, else
   *          returns <code>false</code>.
   */
  public boolean hasAutoID(Class c) {
    List<DAttr> idc = this.getIDDomainConstraints(c);
    if (idc != null) {
      for (DAttr dc : idc) {
        if (dc.auto())
          return true;
      }
    }
    return false;
  }

  /**
   * @effects returns <code>true</code> if the attribute
   *          <code>attributeName</code> of the domain class <code>c</code> is
   *          an id attribute, i.e.
   *          <code>c.attribute.constraint.id() = true</code>, else returns
   *          <code>false</code>
   */
  public boolean isID(Class c, String attributeName) {
    return getDomainConstraint(c, attributeName).id();
  }

  /**
   * @effects returns <code>true</code> if the attribute
   *          <code>attributeName</code> of the domain class <code>c</code> is
   *          optional, i.e.
   *          <code>c.attribute.constraint.optional() = true</code>, else
   *          returns <code>false</code>
   */
  public boolean isOptional(Class c, String attributeName) {
    return getDomainConstraint(c, attributeName).optional();
  }


  /**
   * @effects 
   *  if the data type of the domain attribute specified by <tt>c.attrib</tt> is a sub-type of {@link Collection}
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  @version 3.0 
   */
  public boolean isCollectionTypedAttribute(Class c,
      DAttr attrib) {
    Field f = getDomainAttribute(c, attrib);
    
    Class type = f.getType();
    
    if (Collection.class.isAssignableFrom(type)) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * A variation of {@link #getGenericCollectionType(Field)}.
   * 
   * @requires 
   *  the specified attribute exists and has a generic collection type. 
   *  
   * @effects 
   *  return the generic type of the attribute of <tt>cls</tt> whose name is <tt>attributeName</tt>, 
   *  e.g. if the attribute's generic type is <tt>Collection&lt;Customer&gt;</tt> then 
   *  result = <tt>Customer</tt>; 
   *  or return <tt>null</tt> if type of the specified attribute is not a generic collection type.
   *  
   *  <p>Throws NotFoundException if the specified attribute does not exist.
   *  
   *  @version 5.1
   */
  public Class getGenericCollectionType(Class cls, String attributeName) throws NotFoundException {
    Field attribute = getDomainAttribute(cls, attributeName);
    
    return getGenericCollectionType(attribute);
  }
  
  /**
   * @effects 
   *  return the generic type of the specified attribute, 
   *  e.g. if <tt>attribute = Collection&lt;Customer&gt;</tt> then 
   *  result = <tt>Customer</tt>; 
   *  or return <tt>null</tt> if the collection type 
   *  uses a type variable (e.g. Collection&lt;T&gt;>)
   */
  public Class getGenericCollectionType(Field attribute) {
    /* v3.0: support the case where no parameterised type is specified
    ParameterizedType colType = (ParameterizedType) attribute.getGenericType();
    */
    java.lang.reflect.Type type = attribute.getGenericType();
    if (type instanceof ParameterizedType) {
      ParameterizedType colType = (ParameterizedType) type;
        
      java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
      
      java.lang.reflect.Type t = typeVars[0];
      
      if (t instanceof Class)
        return (Class) t;
      else
        // t is not a Class but a type variable (e.g. as in Collection<T>)
        return null;
    } else {
      return null;
    }
  }

  /**
   * @effects
   *  return the <tt>Field</tt> object of the domain class <tt>cls</tt>
   *  representing the collection-type attribute whose generic type is <tt>genType</tt>.
   *     
   *  <p>Throws NotPossibleException if <tt>cls</tt> does not have any domain attributes 
   *  or NotFoundException if no such attribute can be found.
   */
  /*v2.7.2: not used 
  private Field getCollectionAttributeByGenericType(Class cls, final Class genType) 
  throws NotFoundException {
    List<Field> attributes = getDomainAttributes(cls);
    if (attributes == null)
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, 
          "Không tìm thấy thuộc tính {0}.?<{1}>", cls, genType);
    
    DomainConstraint dc;
    Select filter;
    List vals = null;
    for (Field f : attributes) {
      dc = f.getAnnotation(DC);
      if (dc.type().isCollection()) {
        filter = dc.filter();
        if (filter.clazz().isAssignableFrom(genType)) {
          // found
          return f;
        }
      }
    }

    throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, 
        "Không tìm thấy thuộc tính {0}.?<{1}>", cls, genType);
  }
  */
  
  /**
   * @requires 
   *  cls != null /\ type != null
   * @effects 
   *  return the first <tt>Field</tt> object of the domain attribute
   *  of <tt>cls</tt> whose type is <tt>Collection&lt;type&gt;</tt>, or 
   *  null if no such attribute exist
   */
  /*v2.7.2: not used 
  private Field getCollectionAttributeByType(Class cls, Class type) {
    List<Field> attributes = getDomainAttributes(cls);
    
    DomainConstraint dc;
    ParameterizedType colType;
    
    if (attributes != null) {
      for (Field a : attributes) {
        dc = a.getAnnotation(DC);
        if (dc.type().isCollection()) {
          colType = (ParameterizedType) a.getGenericType();
          java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
          for (java.lang.reflect.Type typeVar : typeVars) {
            if (typeVar.equals(type)) {
              // found
              return a;
            }
          }
        }
      }
      
    }
    
    return null;
  }
  */
  
  /**
   * @effects 
   *  return a <tt>List</tt> containing the types that are used as generic 
   *  types of all the collecition-type attributes of <tt>cls</tt>.
   *  For example, <tt>getCollectionAttributeTypes(Customer.class) = {Order.class}</tt>
   *  since class <tt>Customer</tt> has this collection-type attribute 
   *  <tt>List<Order> orders</tt>; or null if no such attributes exist. 
   */
  /*v2.7.2: not used 
  public List<Class> getCollectionAttributeTypes(Class cls) {
    List<Field> attributes = getDomainAttributes(cls);
    
    List<Class> types = null;
    DomainConstraint dc;
    ParameterizedType colType;
    
    if (attributes != null) {
      types = new ArrayList();
      for (Field a : attributes) {
        dc = a.getAnnotation(DC);
        if (dc.type().isCollection()) {
          colType = (ParameterizedType) a.getGenericType();
          java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
          for (java.lang.reflect.Type typeVar : typeVars) {
            if (typeVar instanceof Class)
              types.add((Class) typeVar);
          }
        }
      }
      
      if (types.isEmpty())
        types = null;
    }
    
    return types;
  }
  */

// v3.2: removed (not used)  
//  /**
//   * Return all the attributes of the domain class <code>c</code>, whose
//   * annotation is of the type <code>annotatedClass</code> and if
//   * <code>recursive=true</code> then recursively similar attributes of the
//   * super and ancestor domain classes of <code>c</code>.
//   * 
//   * @effects invokes {@link #getAttributes(Class, Class, boolean, boolean)}
//   *          with the last argument is <code>true</code>.
//   * 
//   */
//  private List<Field> getAttributes(final Class c, final Class annotatedClass,
//      boolean recursive) {
//    return getAttributes(c, annotatedClass, recursive, true, true);
//  }


  /**
   * @requires  
   *  c is registered in this
   *  
   * @effects 
   *  if exists domain attributes of <tt>c</tt> (incl. <i>all</i> those inherited from the ancestor classes) 
   *  that are annotated with <tt>annotationType</tt>
   *    return a Collection of their <tt>DomainConstraint</tt>s (in the definition order)
   *  else
   *    return null  
   */
  public <A extends Annotation> Map<DAttr,A> getAnnotatedDomainAttributes(Class c,
      Class<A> annotationType) {
    Map<Field,DAttr> fields = getDomainAttributes(c);
    
    //Collection<DomainConstraint> attributes = new ArrayList();
    Map<DAttr,A> attributes = new LinkedHashMap<DAttr,A>();
    
    A attribAn;
    if (fields != null) {
      //for (Field f : fields) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        DAttr da = entry.getValue();
        attribAn = f.getAnnotation(annotationType);
        if (attribAn != null) {
          // found one: add the DomainConstraint annotation to the result
          attributes.put(//f.getAnnotation(DC)
              da
              ,attribAn);
        }
      }
    }
    
    return attributes.isEmpty() ? null : attributes;
  }
  
  /**
   * This method differs from {@link #getAnnotatedDomainAttributes(Class, Class)} in that it excludes any 'shadowed' attributes
   * of the ancestor classes (i.e. attributes that are have the same name as those found in <tt>c</tt>)
   * 
   * <p><b>IMPORTANT:</b>This method SHOULD ONLY be used in special classes (e.g. Module-configuration class) in which 
   * it is normal for a sub-type to have fields (e.g. title) that have the same name as some of those in the super-type. 
   * 
   * <p>In order to get attributes from domain classes, either 
   * use {@link #getDomainAttributes(Class)} or 
   * use {@link #getAnnotatedDomainAttributes(Class, Class)}
   * instead.
   * 
   * @requires  
   *  c is registered in this
   *  
   * @effects 
   *  if exists domain attributes of <tt>c</tt> (incl. those inherited from the ancestor classes that 
   *  <b>do not have the same name</b> as the attributes owned by <tt>c</tt>) 
   *  that are annotated with <tt>annotationType</tt>
   *    return a {@link Map} of their {@link DAttr}s (in the definition order)
   *  else
   *    return null  
   * @version 
   * - 3.3<br>
   * - 5.0: improved to support the new Field-DAttr mapping
   */
  public <A extends Annotation> Map<DAttr,A> getAnnotatedDomainAttributesNoDups(Class c,
      Class<A> annotationType) {
    /*v5.0
    List<Field> fields = getAnnotatedFieldsNoDups(c, annotationType);
    
    //Collection<DomainConstraint> attributes = new ArrayList();
    Map<DAttr,A> attributes = new LinkedHashMap<DAttr,A>();
    
    A attribAn;
    if (fields != null) {
      for (Field f : fields) {
        attribAn = f.getAnnotation(annotationType);
        attributes.put(//v5.0: f.getAnnotation(DC)
            //TODO: this may return null b/c f may not be a domain attribute
            this.getDomainConstraint(c,f)
            ,attribAn);
      }
    }
    */
    Map<Field,A> fields = getAnnotatedFieldsNoDups(c, annotationType);
    
    //Collection<DomainConstraint> attributes = new ArrayList();
    Map<DAttr,A> attributes = new LinkedHashMap<DAttr,A>();
    
    A attribAn;
    if (fields != null) {
      for (Entry<Field,A> entry : fields.entrySet()) {
        Field f = entry.getKey();
        attribAn = entry.getValue();
        attributes.put(//v5.0: f.getAnnotation(DC)
            //TODO: this may return null b/c f may not be a domain attribute
            this.getDomainConstraint(c,f)
            ,attribAn);
      }
    }
    return attributes.isEmpty() ? null : attributes;
  }
  
  /**
   * @effect 
   *  if exists domain attribute c.attribName /\ an annotation over this attribute
   *  of type <tt>A</tt>
   *    return this annotation object
   *  else
   *    return null
   * @version 2.7.2
   */
  public <A extends Annotation> A getDomainAttributeAnnotation(Class c,
      Class<A> annotationType, String attribName) {
    Map<Field,DAttr> fields = getDomainAttributes(c);
    
    A attribAn;
    if (fields != null) {
      String name;
      for (Field f : fields.keySet()) {
        name = f.getName();
        if (name.equals(attribName)) {
          // attribute exists -> check annotation
          attribAn = f.getAnnotation(annotationType);
          if (attribAn != null) {
            // annotation exists 
            return attribAn;
          }
        }
      }
    }
    
    return null;
  }
  
  /**
   * 
   * This method differs from the similar method {@link #getAnnotatedDomainAttributes(Class, Class)}
   * in that it does not require the specified class to be registered in this. 
   * 
   * @effects 
   * Return all the <b>serialisable</b> {@link Field}s of the class <code>c</code> and 
   * those of the parent and ancestor classes whose
   * annotation is of the type <code>annotatedClass</code>; 
   * or return <tt>null</tt> if no such attributes are found.
   * 
   * <p>The attributes are returned in the declaration order.
   */
  public Collection<Field> getAnnotatedSerialisableFields(final Class c, final Class annotatedClass) {
    //v5.0: return getAttributes(c, annotatedClass, true, false, true);
    LinkedHashMap<Field,Annotation> fieldMap = getAnnotatedFields(c, annotatedClass, true, false, true);
    if (fieldMap != null) {
      return fieldMap.keySet();
    } else {
      return null;
    }
  }
  
  /**
   * This method differs from the similar method {@link #getAnnotatedDomainAttributes(Class, Class)}
   * in that it filters those super-type attributes that have the same name as those in a sub-type 
   * (thus give preference to the sub-type's own fields) 
   *  
   * <p><b>IMPORTANT:</b>This method SHOULD ONLY be used in special classes (e.g. Module-configuration class) in which 
   * it is normal for a sub-type to have fields (e.g. title) that have the same name as some of those in the super-type. 
   * 
   * <p>In order to get attributes from domain classes, either 
   * use {@link #getDomainAttributes(Class)} or 
   * use {@link #getAnnotatedDomainAttributes(Class, Class)}
   * instead.
   * 
   * @effects 
   * Return all the <b>serialisable</b> attributes of the class <code>c</code> and 
   * those of the ancestor classes 
   * (with <b>preference</b> given to <tt>c</tt>'s attributes if they have same name as 
   * some found in ancestor classes),  
   * that, if <code>annotatedClass</code> is specified, whose annotation is of <code>annotatedClass</code>; 
   * or return <tt>null</tt> if no such attributes are found.
   *
   * @version
   * - 3.2: created<br>
   */
  public <T extends Annotation> Collection<Field> getAnnotatedSerialisableFieldsNoDups(Class c, final Class<T> annotatedClass) {
    Map<Field,T> fieldMap = getAnnotatedFieldsNoDups(c, annotatedClass, 
        true, // recursive 
        false, // registeredOnly
        true // useSerialisable
        );
    
    if (fieldMap != null) {
      return fieldMap.keySet();
    } else {
      return null;
    }
  }
  
  /**
   * This method differs from the similar method {@link #getAnnotatedDomainAttributes(Class, Class)}
   * in that it filters those super-type attributes that have the same name as those in a sub-type 
   * (thus give preference to the sub-type's own fields).
   * 
   * <p><b>IMPORTANT:</b>This method SHOULD ONLY be used in special classes (e.g. Module-configuration class) in which 
   * it is normal for a sub-type to have fields (e.g. title) that have the same name as some of those in the super-type. 
   * 
   * <p>In order to get attributes from domain classes, either 
   * use {@link #getDomainAttributes(Class)} or 
   * use {@link #getAnnotatedDomainAttributes(Class, Class)}
   * instead.
   * 
   * @effects 
   * Return <b>all</b> the fields of the class <code>c</code> and 
   * those of the ancestor classes 
   * (with <b>preference</b> given to <tt>c</tt>'s fields if they have same name as 
   * some found in ancestor classes),  
   * that, if <code>annotatedClass</code> is specified, whose annotation is of <code>annotatedClass</code>; 
   * or return <tt>null</tt> if no such attributes are found.
   *
   * @version
   * - 3.2: created
   */
  public <T extends Annotation> Map<Field,T> getAnnotatedFieldsNoDups(Class c, final Class<T> annotatedClass) {
    return getAnnotatedFieldsNoDups(c, annotatedClass,  
        true, // recursive 
        false, // registeredOnly 
        false // useSerialisable
        );
  }
  
  /**
   * This method differs from the similar method {@link #getAnnotatedDomainAttributes(Class, Class)}
   * in that it filters those super-type attributes that have the same name as those in a sub-type 
   * (thus give preference to the sub-type's own fields) 
   *  
   * <p><b>IMPORTANT:</b>This method SHOULD ONLY be used in special classes (e.g. Module-configuration class) in which 
   * it is normal for a sub-type to have fields (e.g. title) that have the same name as some of those in the super-type. 
   * 
   * <p>In order to get attributes from domain classes, either 
   * use {@link #getDomainAttributes(Class)} or 
   * use {@link #getAnnotatedDomainAttributes(Class, Class)}
   * instead.
   * 
   * @effects 
   * Return <b>all</b> annotated fields of the class <code>c</code> and 
   * those of the ancestor classes 
   * (with <b>preference</b> given to <tt>c</tt>'s fields if they have same name as 
   * some found in ancestor classes);  
   * or return <tt>null</tt> if no such fields are found.
   *
   * @version
   * - 3.2: created<br>
   * - 5.0: return {@link Collection}
   */
  public Collection<Field> getFieldsNoDuplicates(Class c) {
    Map<Field,Annotation> fieldMap = getAnnotatedFieldsNoDups(c, 
        null, // annotatedClass,  
        true, // recursive 
        false, // registeredOnly 
        false // useSerialisable
        );
    
    if (fieldMap != null) {
      return fieldMap.keySet();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if <tt>c</tt> has at least one attribute specified with <tt>annotatedClass</tt>
   *    return true
   *  else
   *    return false
   * @version 2.7.3
   */
  private static boolean hasAttribute(final Class c, final Class annotatedClass) {

    Field[] myFields = c.getDeclaredFields();

    if (myFields.length > 0) {
      if (annotatedClass == null) // has attributes
        return true;
      
      // check attributes with the specified annotation
      Annotation an;
      String n;
      Field f = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        n = f.getName();
        if (!n.startsWith("this$")) {
          // type = f.getType();

          // if annotation is specified then only return fields with
          // the given annotation
          if (annotatedClass != null) {
            an = f.getAnnotation(annotatedClass);
            if (an != null) {
              return true;
            }
          }
        }
      } // end 2nd for loop
    } // end if

    // does not have attribute
    return false;
  }
  
  /**
   * This method is used primarily for extracting domain attributes 
   * of domain classes. 
   * 
   * @effects returns a <code>LinkedHashMap</code> of <code>(Field,T)</code>s of the class
   *          <code>c</code> and if <code>recursive=true</code> then recursively
   *          those of the non-Object parent and ancestor classes of
   *          <code>c</code> (if any), that have the annotation
   *          <code>annotatedClass</code>, or <code>null</code> if no such
   *          fields exist.
   *          
   *          <p>If <tt>useSerialisable = true</tt> then only considers fields with {@link DAttr}.serialisable=true. 
   * @version
   * - 5.0: improved to return {@link LinkedHashMap} and to support {@link DAttr} extensions.
   */
  private <T extends Annotation> LinkedHashMap<Field,T> getAnnotatedFields(final Class c, final Class<T> annotatedClass,
      boolean recursive, boolean registeredOnly, boolean useSerialisable) {
    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    LinkedHashMap<Field,T> supFields = new LinkedHashMap<>();
    if (recursive) {
      Class superClass = getSuperClass(c, registeredOnly);
      if (superClass != null) {
        LinkedHashMap<Field,T> parentFields = getAnnotatedFields(superClass, annotatedClass, true
            , registeredOnly, useSerialisable);
        if (parentFields != null) {
          supFields.putAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();

    LinkedHashMap<Field,T> fields = new LinkedHashMap<>();
    if (!supFields.isEmpty()) {
      // v5.0: if there are DAttr extensions in c for fields in supFields
      // then use them
      if (annotatedClass == DC) { // looking for domain attributes, check DAttr extension...
        for (Entry<Field, T> e : supFields.entrySet()) {
          Field supField = e.getKey();
          T da = e.getValue();
          String fieldName = supField.getName();
          Method opt = getDAttrExtOperationIfExists(c, fieldName);
          if (opt != null) {
            // DAttr extension exists for supField
            da = (T) opt.getAnnotation(DC);
          }
          
          fields.put(supField, da);
        }
      } else {
        // not domain attributes: use all 
        fields.putAll(supFields);
      }
    }

    if (myFields.length > 0) {
      // Class type = null;
      String n;
      T an = null;
      //v3.1: boolean selected = false;
      Field f = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        n = f.getName();
        if (!n.startsWith("this$")) {
          // type = f.getType();

          // if annotation is specified then only return fields with
          // the given annotation
          if (annotatedClass != null) {
            an = f.getAnnotation(annotatedClass);
            if (an == null) {
              continue;
            }

            // if annotation class is DomainConstraint and useSerialisable =
            // true
            // then use this field only if its serialisable = true
            if (annotatedClass == DC && useSerialisable == true) {
              if (!((DAttr) an).serialisable())
                continue;
            }
          }

          //v5.0: fields.add(f);
          fields.put(f, an);
        }
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }

  /* v5.0: replaced 
  private List<Field> getAttributes(final Class c, final Class annotatedClass,
      boolean recursive, boolean registeredOnly, boolean useSerialisable) {
    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    List supFields = new ArrayList();
    if (recursive) {
      Class superClass = getSuperClass(c, registeredOnly);
      if (superClass != null) {
        List parentFields = getAttributes(superClass, annotatedClass, true
            , registeredOnly, useSerialisable);
        if (parentFields != null) {
          supFields.addAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();

    Annotation an;

    List fields = new ArrayList();
    if (!supFields.isEmpty()) {
      fields.addAll(supFields);
    }

    if (myFields.length > 0) {
      // Class type = null;
      String n;
      //v3.1: boolean selected = false;
      Field f = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        n = f.getName();
        if (!n.startsWith("this$")) {
          // type = f.getType();

          // if annotation is specified then only return fields with
          // the given annotation
          if (annotatedClass != null) {
            an = f.getAnnotation(annotatedClass);
            if (an == null) {
              continue;
            }

            // if annotation class is DomainConstraint and useSerialisable =
            // true
            // then use this field only if its serialisable = true
            if (annotatedClass == DC && useSerialisable == true) {
              if (!((DAttr) an).serialisable())
                continue;
            }
          }

          fields.add(f);
        }
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }
   */
  
  /**
   * @effects 
   *  If exists an {@link Method} of <tt>c</tt>
   *  that is annotated with <tt>{@link DAttr}.name = attribName</tt>
   *    return it
   *  else
   *    return null
   * @version 5.0
   */
  private Method getDAttrExtOperationIfExists(Class c, String attribName) {
    Method[] methods = c.getDeclaredMethods();
    
    if (methods.length > 0) {
      for (Method m : methods) {
        DAttr da = m.getAnnotation(DC);
        if (da != null && da.name().equals(attribName)) {
          // found one
          return m;
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * This method differs from {@link #getAnnotatedFields(Class, Class, boolean, boolean, boolean)} in 
   * that it filters the result such that no two {@link Field}s have the same name (due to inheritance). 
   * 
   * <p><b>IMPORTANT:</b>This method SHOULD ONLY be used in special classes (e.g. Module-configuration class) in which 
   * it is normal for a sub-type to have fields (e.g. title) that have the same name as some of those in the super-type. 
   * 
   * <p>In order to get attributes from domain classes, either 
   * use {@link #getDomainAttributes(Class)} or 
   * use {@link #getAnnotatedDomainAttributes(Class, Class)}
   * instead.
   * 
   * @effects returns a {@link Map} of {@link Field}s of the class
   *          <code>c</code> and if <code>recursive=true</code> then recursively
   *          those of the non-Object parent and ancestor classes of
   *          <code>c</code> (if any), that have the annotation
   *          <code>annotatedClass</code>, or <code>null</code> if no such
   *          fields exist.
   *          
   *          <p>If <tt>useSerialisable = true</tt> then only considers fields with {@link DAttr}.serialisable=true. 
   *          
   *          <p>Preference is given to <tt>Field</tt>s of <tt>c</tt> if they have the same 
   *          name as those found in an ancestor class; thus effectively resulting in no two fields having
   *          the same name in <tt>result</tt>
   *          
   *          <p>The fields are returned in the declaration order.
   * @version 
   * - 3.2: created<br>
   * - 5.0: improved to return {@link Map}
   */
  private <T extends Annotation> LinkedHashMap<Field,T> getAnnotatedFieldsNoDups(final Class c, final Class<T> annotatedClass,
      boolean recursive, boolean registeredOnly, boolean useSerialisable) {
    // add fields that are inherited from the parent first
    // this order must be the same as the order of the constructor arguments
    // used to create objects
    // of the class

    LinkedHashMap<Field,T> supFields = new LinkedHashMap<>();//new ArrayList();
    if (recursive) {
      Class superClass = getSuperClass(c, registeredOnly);
      if (superClass != null) {
        LinkedHashMap<Field,T> parentFields = getAnnotatedFieldsNoDups(superClass, annotatedClass, true
            , registeredOnly, useSerialisable);
        if (parentFields != null) {
          supFields.putAll(parentFields);
        }
      }
    }

    Field[] myFields = c.getDeclaredFields();

    LinkedHashMap<Field,T> fields = new LinkedHashMap<>();//new ArrayList();
    String sn, n;
    
    // first, process inherited fields (if any)
    if (!supFields.isEmpty()) {
      /*v3.2: additional filtering for duplicate
       * if there is another inherited field that has the same name then remove that field
      fields.addAll(supFields);
      */
      Field supField; T da;
      if (myFields.length > 0) {
        // to filter
        SUP: //v5.0: for (Field supField : supFields) {
        for(Entry<Field,T> entry : supFields.entrySet()) {
          supField = entry.getKey();
          da = entry.getValue();
          sn = supField.getName();
          for (Field f : myFields) {
            n = f.getName();
            if (sn.equals(n)) {
              // same name: skip this field
              continue SUP;
            }
          }
          
          // no duplicate: add
          // check for domain constraint extension
          if (annotatedClass == DC) { // looking for domain attributes, check DAttr extension...
            Method opt = getDAttrExtOperationIfExists(c, sn);
            if (opt != null) {
              // DAttr extension exists for supField
              da = (T) opt.getAnnotation(DC);
            }
          }
          
          fields.put(supField,da);
        } // end SUP
      } else {
        // check for domain constraint extension
        if (annotatedClass == DC) { // looking for domain attributes, check DAttr extension...
          for (Entry<Field, T> e : supFields.entrySet()) {
            supField = e.getKey();
            da = e.getValue();
            String fieldName = supField.getName();
            Method opt = getDAttrExtOperationIfExists(c, fieldName);
            if (opt != null) {
              // DAttr extension exists for supField
              da = (T) opt.getAnnotation(DC);
            }
            
            fields.put(supField, da);
          }
        } else {
          // not domain attributes: use all 
          fields.putAll(supFields);
        }
      }
    }

    // now process c's fields (if any)
    if (myFields.length > 0) {
      //v3.1: boolean selected = false;
      Field f = null;
      T an = null;
      for (int i = 0; i < myFields.length; i++) {
        f = myFields[i];
        n = f.getName();
        if (!n.startsWith("this$")) {
          // if annotation is specified then only return fields with
          // the given annotation
          if (annotatedClass != null) {
            an = f.getAnnotation(annotatedClass);
            if (an == null) {
              continue;
            }

            // if annotation class is DomainConstraint and useSerialisable =
            // true
            // then use this field only if its serialisable = true
            if (annotatedClass == DC && useSerialisable == true) {
              if (!((DAttr) an).serialisable())
                continue;
            }
          }

          fields.put(f,an);
        }
      } // end 2nd for loop
    } // end if

    return (!fields.isEmpty()) ? fields : null;
  }
  
  /**
   * @effects returns the id attribute(s) of the domain class <code>c</code>.
   * 
   *          <p>
   *          An id attribute is one whose <code>DomainConstraint</code>
   *          definition has <code>id = true</code>.
   * 
   *          <p>
   *          The id attribute(s) of a domain class include those declared in
   *          the class as well as those that it inherits from the domain super
   *          class.
   *          
   *          <p>The attributes are returned in the order that they are declared in <tt>c</tt>
   * @version 
   * - 5.0: changed to return {@link Map}
   */
  public Map<Field,DAttr> getIDAttributes(Class c) {
    // get the domain attribute
    Map<Field,DAttr> domainAttrs = (Map<Field,DAttr>) classDefs.get(c);

    Map<Field,DAttr> idAttributes = new LinkedHashMap<>();//new ArrayList();
    DAttr d = null;
    Field idField = null;
    for (Entry<Field,DAttr> entry : domainAttrs.entrySet()) {
      Field f = entry.getKey();
      //d = f.getAnnotation(DC);
      d = entry.getValue();
      if (d.id()) {
        // found
        idAttributes.put(f,d);
      }
    }

    if (!idAttributes.isEmpty()) {
      return idAttributes;
    } else {
      return null;
    }
  }

  /**
   * @effects returns the value(s) of the id attribute(s) of <code>o</code>;
   *          throws <code>NotFoundException</code> if <code>o</code> does not
   *          have any id attributes or <code>NotPossibleException</code> if
   *          could not get their value(s).
   * 
   * @requires <code>o</code> has id attribute(s), i.e. their
   *           <code>DomainConstraint.id</code> fields are set to
   *           <code>true</code>; and getter methods for them are defined.
   */
  public Object[] getIDAttributeValues(Object o) throws NotPossibleException,
      NotFoundException {
    // // get the id attribute of o
    // List<Field> domainAttrs = classDefs.get(o.getClass());
    //
    // DomainConstraint d;
    // List idVals = new ArrayList();
    // if (domainAttrs != null) {
    // // return the id attribute or null
    // for (Field f : domainAttrs) {
    // d = f.getAnnotation(DC);
    // if (d.id()) {
    // idVals.add(getAttributeValue(f, o));
    // }
    // }
    // }

    List idVals = new ArrayList();

    Collection<DAttr> domainAttrs = getDomainConstraints(o.getClass()); //classConstraints.get(o.getClass());

    if (domainAttrs != null) {
      for (DAttr d : domainAttrs) {
        if (d.id()) {
          // found
          idVals.add(getAttributeValue(o, d.name()));
        }
      }
    }

    if (!idVals.isEmpty()) {
      return idVals.toArray();
    } else {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND,
          "Không tìm thấy thuộc tính {0} trong lớp {1}", "", o.getClass());
    }
  }

  /**
   * @effects 
   *  return the value of the <b>first</tt> id-attribute of <tt>obj</tt>; or return <tt>null</tt> if 
   *  either no attribute is found or the value of that id-attribute is <tt>null</tt>
   * @version 2.7.4
   */
  public Object getIDAttributeValue(Object obj) {
    try {
      Object[] idVals = getIDAttributeValues(obj);
      return idVals[0];
    } catch (Exception e) {
      return null;
    }
  }
  
  /**
   * @effects returns the <code>DomainConstraint</code> object(s) of the id
   *          attribute(s) of the domain class <code>c</code>; 
   *          or null of no such attributes exist
   */
  public List<DAttr> getIDDomainConstraints(Class c) {
    // find id constraints from among the domain constraints of c
    Collection<DAttr> domainAttrs = getDomainConstraints(c); //classConstraints.get(c);

    List<DAttr> idCons = new ArrayList();
    if (domainAttrs != null) {
      for (DAttr d : domainAttrs) {
        if (d.id()) {
          // found
          idCons.add(d);
        }
      }
    }

    if (!idCons.isEmpty()) {
      return idCons;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if exists id domain attribute(s) of <tt>c</tt>
   *    return the <b>first</b> of such attribute
   *  else
   *    return <tt>null</tt>
   * @version 3.3
   */
  public DAttr getIDDomainConstraint(Class c) {
    Collection<DAttr> domainAttrs = getDomainConstraints(c); //classConstraints.get(c);

    if (domainAttrs != null) {
      for (DAttr d : domainAttrs) {
        if (d.id()) {
          // found
          return d;
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects returns the <code>DomainConstraint</code> object(s) of the id
   *          attribute(s) of the domain class <code>c</code>.
   * 
   * @deprecated use {@link #getIDDomainConstraints(Class)}
   */
  public DAttr[] getIDAttributeConstraints(Class c) {
    // get the id attribute
    // List<Field> domainAttrs = classDefs.get(c);
    //
    // List<DomainConstraint> idCons = new ArrayList();
    // DomainConstraint d = null;
    // Field idField = null;
    // if (domainAttrs != null) {
    // for (Field f : domainAttrs) {
    // d = f.getAnnotation(DC);
    // if (d.id()) {
    // // found
    // idCons.add(d);
    // }
    // }
    // }

    // find id constraints from among the domain constraints of c
    Collection<DAttr> domainAttrs = getDomainConstraints(c); //classConstraints.get(c);

    List<DAttr> idCons = new ArrayList();
    if (domainAttrs != null) {
      for (DAttr d : domainAttrs) {
        if (d.id()) {
          // found
          idCons.add(d);
        }
      }
    }

    if (!idCons.isEmpty()) {
      return (DAttr[]) idCons.toArray(new DAttr[idCons
          .size()]);
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>cls</tt> has candidate identifiers (Cids) then pick the first one in declaration order
   *  and return the attribute name(s) forming the identifier; 
   *  else return null 
   *  
   * @version 5.3 
   */
  public String[] getCandidateIdAttribNames(Class cls) {
    Map<Field, DAttr> attribMap = getDomainAttributes(cls);
    List<String> cids = null;
    
    if (attribMap != null) {
      String ccidName = null;
      for (Entry<Field, DAttr> e : attribMap.entrySet()) {
        AccessibleObject k = e.getKey();
        if (!(k instanceof Field)) {
          // could be a domain method: ignore
          continue;
        }
        
        Field f = (Field) k;
          
        DAttr da = e.getValue();
        if (da.cid()) {
          // single CID
          cids = new ArrayList<>();
          cids.add(f.getName());
          break;
        } else if (!da.ccid().equals(CommonConstants.NullString)) {
          // compound cid
          if (ccidName != null) { // a compound cid being process
            if (da.ccid().equals(ccidName)) {
              // part of the same compound cid
              cids.add(f.getName());
            } else {
              // a different ccid: ignore
            }
          } else { // first compound ccid: process
            ccidName = da.ccid();
            cids = new ArrayList<>();
            cids.add(f.getName());
          }
        }
      }
    }
    
    return (cids != null) ? cids.toArray(new String[cids.size()]) : null;
  }
  
  /**
   * @effects returns the <code>Collection</code> of domain constraints of the domain
   *          attributes of the domain class <code>c</code>.
   *   <p>The {@link DAttr}s are recorded in the order that they are declared in <tt>c</tt>
   */
  public Collection<DAttr> getDomainConstraints(Class c) {
    return //v5.0 classConstraints.get(c);
        classDefs.get(c).values();
  }

  /**
   * @effects returns {@link DAttr} of the domain attribute
   *          named <code>attributeName</code> of the domain class
   *          <code>c</code>, or throws <code>NotFoundException</code> if no such
   *          constraint exists.
   * @version 
   * - 5.0: changed to use the new Field-DAttr mapping
   */
  public DAttr getDomainConstraint(Class c, String attributeName)
      throws NotFoundException {
    /*
    Field a = getDomainAttribute(c, attributeName);
    DAttr dc = null;
    if (a != null) {
      dc = a.getAnnotation(DC);
    }
    if (dc == null)
      throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND, new Object[] {attributeName});
     */

    Map<Field,DAttr> fields = getDomainAttributes(c);

    if (fields != null) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        DAttr dc = entry.getValue();
        if (f.getName().equals(attributeName)) {
          return dc;
        }
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND, 
        new Object[] {c.getSimpleName()+"."+attributeName});
  }

  /**
   * @effects 
   *  return {@link DAttr} of the domain attribute
   *  of the domain class <code>c</code>, whose name is <code>attribName</code> and whose declared type 
   *  is assignment-compatible to <tt>compatibleToType</tt>, 
   *  or throws <code>NotFoundException</code> if no such attribute exists.
   *  
   * @version 
   * - 4.0<br>
   * - 5.0: changed to use the new Field-DAttr mapping
   */
  public DAttr getDomainConstraintIfCompatibleTo(Class c,
      String attribName, Class compatibleToType) {
    /* v5.0
    Field a = getDomainAttribute(c, attribName);
    DAttr dc = null;
    if (a != null && a.getType().isAssignableFrom(compatibleToType)) {
      dc = a.getAnnotation(DC);
    }

    if (dc == null)
      throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND, new Object[] {attribName});
    return dc;
    */
    Map<Field,DAttr> fields = getDomainAttributes(c);

    if (fields != null) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        DAttr dc = entry.getValue();
        if (f.getName().equals(attribName) && f.getType().isAssignableFrom(compatibleToType)) {
          return dc;
        }
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND, 
        new Object[] {attribName});
  }
  
  /**
   * @requires 
   *  attribField != null /\ attribField is owned by <tt>c</tt>
   *  
   * @effects returns <code>DomainConstraint</code> object attached to 
   *          <code>attributeField</code> of the owner domain class <tt>c</tt>, 
   *          throws <code>NotFoundException</code> if no such
   *          constraint exists.
   * @version 
   * - 3.2 <br>
   * - 5.0: changed to use the new Field-DAttr mapping
   */
  public DAttr getDomainConstraint(Class c, Field attribField)
      throws NotFoundException {
    /* v5.0:
    DAttr dc = null;
    if (attribField != null) {
      dc = attribField.getAnnotation(DC);
    }

    if (dc == null)
      throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND,
          new Object[] {attribField.getName()});
    
    return dc;
    */
    Map<Field,DAttr> fields = getDomainAttributes(c);

    if (fields != null) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        DAttr dc = entry.getValue();
        if (f.equals(attribField)) {
          return dc;
        }
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.CONSTRAINT_NOT_FOUND, 
        new Object[] {attribField.getName()});
  }
  
  /**
   * A domain class is reflexive if there exists one domain-type attribute of this class
   * whose type is the same as or a super-type of the class. For example, <tt>Employee:id,supervisor</tt> is reflexive because
   * <tt>Employee.supervisor</tt> has data type <tt>Employee</tt> (stating the rule that 
   * an employee has one supervisor). 
   * 
   * @requires
   *  <tt>if fields != null
   *    fields are the domain attributes of <tt>c</tt>
   * @effects 
   *    process the domain attributes of c (or <tt>fields</tt> if specified) and return the {@link DAttr} of those 
   *    that realise a reflexive association with c;
   *    <br>a field <tt>f</tt> realises a reflexive association if <tt>f.type = c \/ 
   *      (inheritable=true /\ f.type = c.super (or c.ancester))</tt>; 
   *    <br>if no reflexive attributes are found return <tt>null</tt>
   * @version 
   * - 3.2
   */  
  public List<DAttr> getReflexiveDomainConstraints(Class c, Map<Field,DAttr> fields, boolean inheritable) throws NotPossibleException {
    
    List<DAttr> refDcs = classReflexiveAttribs.get(c);
    
    if (refDcs == null) {
      // determine reflexivity and store for use later
      if (fields == null) {
        // read the fields of c if not specified
        fields = getDomainAttributes(c);
      }

      if (fields == null)
        throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED, new Object[] {c.getName()});

      DAttr dc;
      Type type;
      Class dataType;
      //for (Field f : fields) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        //dc = f.getAnnotation(DC);
        dc = entry.getValue();
        type = dc.type();
        if (type.isDomainType()) {
          dataType = f.getType();
          if (dataType == c || (inheritable && isSpecialised(c, dataType))) {
            // reflexive
            if (refDcs == null) refDcs = new ArrayList();
            refDcs.add(dc);
          }
        }
      }
      
      if (refDcs != null) classReflexiveAttribs.put(c, refDcs);
    }
    
    return refDcs;
  }
  
  /**
   * @effects 
   *  if exists attribute <tt>a</tt> of a domain class that is linked to 
   *  <tt>associatedClass.assocAttrib</tt>
   *    return <tt>a</tt>
   *  else
   *    return <tt>null</tt>
   */
  public DAttr getLinkedAttribute(Class associatedClass, DAttr assocAttrib) {
    try {
      Tuple2<DAttr, DAssoc> assocTuple = getTargetAssociation(associatedClass, assocAttrib);
      return assocTuple.getFirst();
    } catch (NotFoundException e) {
    }
    
    return null;
  }
  
  /**
   * @effects 
   *  return the actual data type of the <tt>Field</tt> of <tt>c</tt> whose 
   *  domain constraint is <tt>attrib</tt>; 
   *  or throws NotFoundException if no such attribute exists.
   * @version 
   * - 2.7.3 <br>
   * - 3.1: renamed
   * 
   */
  public Class getDomainClassFor(Class c, DAttr attrib) throws NotFoundException {
    Field f = getDomainAttribute(c, attrib);
    
    if (f != null) {
      return f.getType();
    } else {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {
          attrib.name(), c.getSimpleName()});
    }
  }
  
  /**
   * @effects returns the domain class used as the declared type of the
   *          attribute <code>attributeName</code> in the class <code>c</code> ,
   *          or throws <code>NotfoundException</code> if no such attribute
   *          exists.
   */
  public Class getDomainClassFor(Class c, String attributeName)
      throws NotFoundException {
    Field f = getDomainAttribute(c, attributeName);
    return f.getType();
  }

  /**
   * @effects returns the domain class whose simple name is <code>clsName</code>
   *          , or <code>null</code> if no such class exists.
   */
  public Class getDomainClassFor(String clsName) {
    for (Class c : classDefs.keySet()) {
      if (c.getSimpleName().equals(clsName)) {
        return c;
      }
    }

    return null;
  }

  /**
   * @effects 
   *  return the actual domain class, 
   *  which is either <tt>baseClass</tt> or a super- or ancestor class of 
   *  <tt>baseClass</tt>, in which the domain attribute represented by 
   *  dc is declared; or return <tt>null</tt> if <tt>dc</tt> is not 
   *  a valid domain constraint
   *  
   * @requires
   *  dc is a valid domain constraint of a domain attribute
   */
  public Class getDeclaringClass(Class baseClass, DAttr dc) throws NotPossibleException {
    // all the domain constraints associated to baseClass
    Map<Field,DAttr> fields = getDomainAttributes(baseClass);
    
    if (fields == null) {
      log(new NotPossibleException(NotPossibleException.Code.NULL_POINTER_EXCEPTION, new Object[] {baseClass+".fields", "no domain attributes"}), 
          "getDeclaringClass()", baseClass.getSimpleName() + " has no domain attributes");
    }
    
    //v5.0: for (Field f : fields) {
      //if (f.getAnnotation(DC).equals(dc)) {
    for (Entry<Field, DAttr> entry : fields.entrySet()) {
      Field f = entry.getKey();
      //DAttr da = entry.getValue();
      if (f.getName().equals(dc.name())) {
        return f.getDeclaringClass();
      }
    }
    
    return null;
  }
  
  // /**
  // * @effects returns <code>DomainConstraint</code> of the attribute of the
  // * domain class <code>c</code> or of the super (or ancestor) class of
  // * <code>c</code>, whose declared type is <code>type</code>; throws
  // * <code>NotFoundException</code> if no such field exists.
  // */
  // public DomainConstraint getDomainConstraint(Class c, Class type)
  // throws NotFoundException {
  // List<Field> fields = classDefs.get(c);
  //
  // if (fields != null) {
  // for (Field f : fields) {
  // if (f.getType() == type) {
  // return f.getAnnotation(DC);
  // }
  // }
  // }
  // throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND,
  // "Không tìm thấy thuộc tính {0} trong lớp {1}", "type=" + type,
  // c.getSimpleName());
  // }

  // /**
  // * @effects returns <code>Field</code> of the domain class <code>c</code> or
  // * of the super (or ancestor) class of <code>c</code>, whose declared
  // * type is <code>type</code>; throws <code>NotFoundException</code>
  // * if no such field exists.
  // */
  // public Field getDomainAttribute(Class c, Class type) throws
  // NotFoundException {
  // List<Field> fields = classDefs.get(c);
  //
  // if (fields != null) {
  // for (Field f : fields) {
  // if (f.getType() == type) {
  // return f;
  // }
  // }
  // }
  // throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND,
  // "Không tìm thấy thuộc tính {0} trong lớp {1}", "type=" + type,
  // c.getSimpleName());
  // }

  /**
   * @requires 
   *  cls != null /\ attribute != null /\ 
   *  attribute is a class attribute of <tt>cls</tt> /\ 
   *  a static setter method named setX() is defined in cls, where 
   *    X matches attribute 
   *    
   * @effects 
   *  update the value of class attribute <tt>cls.attribute</tt> to <tt>val</tt> based 
   *  on <tt>val</tt>.
   *  
   *  <p>Throws NotFoundException if no suitable update method is found;
   *  NotPossibleException if fails to perform method.
   */
  private void updateClassAttributeValue(Class cls, String attribute, Object val) 
    throws NotFoundException, NotPossibleException {
    String fname = (attribute.charAt(0) + "").toUpperCase() + attribute.substring(1);
    String mname = "set" + fname;

    // look up the first method that has the same name
    Method[] methods = cls.getMethods();
    if (methods != null) {
      boolean found = false;
      for (Method m : methods) {
        if (m.getName().equals(mname) && Modifier.isStatic(m.getModifiers())) {
          // found
          found = true;
          try {
            m.invoke(null, val);
            break;
          } catch (Exception e) {
            throw new NotPossibleException(
                NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, new Object[] {cls, mname});
          }
        }
      }
      
      if (!found) {
        throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
            new Object[] { cls, mname});
      }
      
    } else {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, new Object[] {cls, mname});
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
   * @effects 
   *  if exists association in <tt>c</tt> whose name is <tt>assocName</tt>
   *    return the association tuple (attribute,Association)
   *  else
   *    throws NotFoundException 
   */
  public Tuple2<DAttr, DAssoc> getAssociation(Class c,
      String assocName) throws NotFoundException {
    
    List<Tuple2<DAttr,DAssoc>> assocs = classAssocs.get(c);
    
    if (assocs == null)
      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, 
          new Object[] {assocName, "", c.getSimpleName()});
    
    for (Tuple2<DAttr,DAssoc> tuple : assocs) {
      if (tuple.getSecond().ascName().equals(assocName)) {
        // found association
        return tuple;
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND,
        new Object[] {assocName, "", c.getSimpleName()});
  }

  /**
   * @requires 
   *  c != null /\ attrib != null
   * @effects
   *  look up and return <tt>Association</tt> object that maps the domain attribute whose name is <tt>attribName</tt>
   *  of domain class <tt>c</tt> to an association; or 
   *  throws NotFoundException if no such association exists
   * 
   * @version 3.3
   */
  public DAssoc getAssociationObj(Class c, String attribName) throws NotFoundException {
    
    List<Tuple2<DAttr,DAssoc>> assocs = classAssocs.get(c);
    
    if (assocs == null)
      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, 
          new Object[] {"", attribName, c.getSimpleName()});

    DAttr attrib;
    for (Tuple2<DAttr,DAssoc> tuple : assocs) {
      attrib = tuple.getFirst();
      if (attrib.name().equals(attribName)) {
        // found association
        return tuple.getSecond();
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND,
        new Object[] {"", attribName, c.getSimpleName()});
  }
  
  /**
   * @requires 
   *  c != null /\ attrib != null
   * @effects
   *  look up and return a tuple that maps the domain attribute <tt>attrib</tt>
   *  of domain class <tt>c</tt> to an association; or 
   *  throws NotFoundException if no such association exists
   *    
   */
  public Tuple2<DAttr,DAssoc> getAssociation(Class c, DAttr attrib) throws NotFoundException {
    
    List<Tuple2<DAttr,DAssoc>> assocs = classAssocs.get(c);
    
    if (assocs == null)
      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, 
          new Object[] {"", attrib.name(), c.getSimpleName()});
    
    for (Tuple2<DAttr,DAssoc> tuple : assocs) {
      if (tuple.getFirst().equals(attrib)) {
        // found association
        return tuple;
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, 
        new Object[] {"", attrib.name(), c.getSimpleName()});
  }
  
  /**
   * @effects 
   *  if exists <tt>Association</tt> defined for the domain attribute represented by <tt>f</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  private DAssoc getAssociation(Field f) {
    return f.getAnnotation(AS);
  }
  
  /**
   * This method is more general than {@link #getAssociations(Class, AssocType, AssocEndType)}.
   * 
   * <br>Note: use {@link DSMBasic#AssocFilter} to define and re-use <tt>filter</tt>.
   * 
   * @requires 
   *  cls != null 
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples (dc,assoc) 
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute that satisfies <tt>filter</tt> (if it is satisfied); or 
   *  return <tt>null</tt> if no associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   * @version 
   *  - 3.0<br>
   *  - 3.1: pass attribute into the call of filter.check
   */
  public Map<DAttr, DAssoc> getAssociations(Class cls,
      Filter<DAssoc> filter) {
    LinkedHashMap<DAttr,DAssoc> assocMap = new LinkedHashMap();

    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(cls);
    
    if (tuples != null) {
      DAssoc assoc;
      DAttr attrib;
      if (filter != null) {
        // filter is specified -> check
        for (Tuple2<DAttr, DAssoc> tuple : tuples) {
          assoc = tuple.getSecond();
          attrib = tuple.getFirst();
          if (filter.check(assoc, attrib)) {  // v3.1: added attrib to the call
            assocMap.put(attrib, assoc);
          }
        }
      } else {
        // no filter: accept all
        for (Tuple2<DAttr, DAssoc> tuple : tuples) {
          assoc = tuple.getSecond();
          assocMap.put(tuple.getFirst(), assoc);
        }
      }
    }
    
    if (assocMap.isEmpty())
      return null;
    else
      return assocMap;
  }
  
  /**
   * @requires 
   *  cls != null 
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples <dc,assoc> 
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute; or 
   *  return null if no associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   */
  public Map<DAttr,DAssoc> getAssociations(Class cls) {
    return getAssociations(cls, null, null);
  }
  
  /**
   * @requires 
   *  cls != null  /\ associatedCls != null
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples (dc,assoc),  
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute, each of which 
   *  defines an association<tt>(cls, associatedCls)</tt>; or 
   *  return null if no associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   */
  public Map<DAttr, DAssoc> getAssociations(Class cls, Class associatedCls) {
    LinkedHashMap<DAttr,DAssoc> assocMap = new LinkedHashMap();

    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(cls);
    
    if (tuples != null) {
      DAssoc assoc;
      for (Tuple2<DAttr, DAssoc> tuple : tuples) {
        assoc = tuple.getSecond();
        if (assoc != null && assoc.associate().type() == associatedCls) {
          // found one
          assocMap.put(tuple.getFirst(), assoc);
        }
      }
    }
    
    if (assocMap.isEmpty())
      return null;
    else
      return assocMap;
  }

  /**
   * @requires 
   *  cls != null /\ assocType != null /\ endType != null 
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples <dc,assoc> 
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute such that 
   *  <tt>assoc.type = assocType /\ assoc.endType = endType</tt>; or 
   *  return null if no such associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   *  
   */
  public Map<DAttr,DAssoc> 
    getAssociations(Class cls, AssocType assocType,
      AssocEndType endType) {

    LinkedHashMap<DAttr,DAssoc> assocMap = new LinkedHashMap();

    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(cls);
    
    if (tuples != null) {
      DAssoc assoc;
      if (assocType != null && endType != null) {
        for (Tuple2<DAttr, DAssoc> tuple : tuples) {
          assoc = tuple.getSecond();
          if (assoc != null && assoc.ascType() == assocType
              && assoc.endType() == endType) {
            assocMap.put(tuple.getFirst(), assoc);
          }
        }
      } else {
        for (Tuple2<DAttr, DAssoc> tuple : tuples) {
          assoc = tuple.getSecond();
          assocMap.put(tuple.getFirst(), assoc);
        }
      }
    }
    
    if (assocMap.isEmpty())
      return null;
    else
      return assocMap;
  }
  

  /**
   * @requires 
   *  assocType != null
   *  
   * @effects
   *    if exists <tt>Association a(c,d)</tt> between <tt>c</tt> and some domain class <tt>d</tt> s.t. 
   *    <tt>a.associationType = assocType /\ 
   *          (myEndType == null || endType(c,a) = myEndType)</tt>
   *      return <tt>true</tt>
   *    else
   *      return <tt>false</tt>    
   * @version 2.7.3
   */
  public boolean hasAssociation(Class c, AssocType assocType, AssocEndType myEndType) {
    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(c);
    
    if (tuples != null) {
      DAssoc assoc;
      for (Tuple2<DAttr, DAssoc> tuple : tuples) {
        assoc = tuple.getSecond();
        if (assoc != null && 
            assoc.ascType() == assocType && 
            (myEndType == null || assoc.endType() == myEndType)) {
          // found
          return true;
        }
      }
    }
    
    // not found
    return false;
  }
  
  /**
   * @requires 
   *  cls != null 
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples (dc,assoc) 
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute such that 
   *  <tt>assoc.associate.determinant=true</tt> (i.e. associated class is 
   *  the determinant of the relationship); or 
   *  return null if no such associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   */
  public Map<DAttr,DAssoc> getAssociationsByDeterminant(Class cls) {

    LinkedHashMap<DAttr,DAssoc> assocMap = new LinkedHashMap();

    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(cls);
    
    if (tuples != null) {
      DAssoc assoc;
      for (Tuple2<DAttr, DAssoc> tuple : tuples) {
        assoc = tuple.getSecond();
        if (assoc.associate().determinant()) {
          // found one association 
          assocMap.put(tuple.getFirst(), assoc);
        }
      }
    }
    
    if (assocMap.isEmpty())
      return null;
    else
      return assocMap;
  }
  
  /**
   * @requires 
   *  cls != null 
   * @effects
   *  look up and return a <tt>Map</tt> of the tuples (dc,assoc) 
   *  where <tt>dc</tt> is the DomainConstraint of a domain attribute and 
   *  <tt>assoc</tt> is the association marker of this attribute such that 
   *  <tt>assoc.type = {@link AssocType#Many2Many</tt>; or 
   *  return null if no such associations exist.
   *  
   *  <p>The Associations are returned in the same order as they are defined in <tt>cls</tt>.
   *  
   * @version 3.2
   */
  public Map<DAttr,DAssoc> getManyToManyAssociations(Class cls) {

    LinkedHashMap<DAttr,DAssoc> assocMap = new LinkedHashMap();

    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(cls);
    
    if (tuples != null) {
      DAssoc assoc;
      for (Tuple2<DAttr, DAssoc> tuple : tuples) {
        assoc = tuple.getSecond();
        if (assoc.ascType().equals(AssocType.Many2Many)) {
          // found many-many association
          assocMap.put(tuple.getFirst(), assoc);
        }
      }
    }
    
    if (assocMap.isEmpty())
      return null;
    else
      return assocMap;
  }
  
  /**
   * @effects 
   *  return the target <tt>Association</tt> which is 
   *  defined in the associated class of <tt>ofAssociation</tt>
   *  together with the <tt>DomainConstraint</tt> of the domain attribute involved.
   *  
   *  <p>Throws NotFoundException if no such association exists.
   */
  public Tuple2<DAttr,DAssoc> getTargetAssociation(DAssoc ofAssociation) 
  throws NotFoundException {
    Class assocClass = ofAssociation.associate().type();
    String assocName = ofAssociation.ascName();
    AssocType assocType = ofAssociation.ascType();
    return getAssociation(assocClass, assocName, assocType);
  }

  /**
   * @effects  
   *  if exists the association tuple <tt>t = (a,assoc)</tt> that associates an attribute <tt>a</tt> of some domain class 
   *  to attribute <tt>c.attrib</tt>
   *    return <tt>t</tt>
   *  else
   *    return <tt>null</tt>
   *    
   *  <p>throws NotFoundException if no associations are found.
   */
  public Tuple2<DAttr,DAssoc> getTargetAssociation(Class c, DAttr attrib) 
  throws NotFoundException {
    Tuple2<DAttr, DAssoc> assocTuple = getAssociation(c, attrib);
    DAssoc assoc = assocTuple.getSecond();
  
    return getTargetAssociation(assoc);
  }
  
  /**
   * @requires 
   *  assocClass != null /\ assocName != null /\ assocType != null /\ 
   *  &lt;assocName,assocType&gt; uniquely identifies an association
   * @effects
   *  look up and return a tuple that maps the domain attribute and 
   *    the association of <tt>assocClass</tt>
   *    whose name is <tt>assocName</tt> and
   *    type is <tt>assocType</tt>; or 
   *  throws NotFoundException if no such association exists
   *    
   */
  public Tuple2<DAttr,DAssoc> getAssociation(Class assocClass, String assocName,
      AssocType assocType) throws NotFoundException {
    
    List<Tuple2<DAttr,DAssoc>> assocs = classAssocs.get(assocClass);
    
    if (assocs == null)
      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND,  new Object[] { assocName, assocType, assocClass});
    
    DAssoc assoc;
    for (Tuple2<DAttr,DAssoc> tuple : assocs) {
      assoc = tuple.getSecond();
      if (assoc.ascName().equals(assocName) && assoc.ascType() == assocType) {
        // found association
        return tuple;
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND,  new Object[] { assocName, assocType, assocClass});
  }

  /**
   * @requires 
   *  c0 != null /\ assocName != null /\ c1 != null 
   * @effects
   *  if exists <tt>Association a(c0.x,c1.y)</tt> defined in <tt>c0</tt> 
   *  whose name is <tt>assocName</tt>
   *    return the tuple (x,a)
   *  else
   *    return <tt>null</tt>; 
   *    
   */
  public Tuple2<DAttr,DAssoc> getAssociation(Class c0, String assocName,
      Class c1) {
    
    List<Tuple2<DAttr,DAssoc>> assocs = classAssocs.get(c0);
    
    if (assocs == null)
      return null;  // no associations
    
    DAssoc assoc;
    for (Tuple2<DAttr,DAssoc> tuple : assocs) {
      assoc = tuple.getSecond();
      if (assoc.ascName().equals(assocName) && assoc.associate().type() == c1) {
        // found association
        return tuple;
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  return {@link Tuple2} object containing names of the two link attributes in <tt>normAssocCls</tt>, 
   *  which normalise the many-many association <tt>manyAssoc</tt> of <tt>c</tt> 
   *  (one link attribute 'connects-to' <tt>c</tt>, while the other 'connects-to' the opposite side of <tt>c</tt> in <tt>manyAssoc</tt>)  
   *
   *  <p>throws NotFoundException if the required normalised associations are not found
   *  
   *  @version 3.3
   */
  public Tuple2<String, String> getLinkAttribsOfNormalisedAssocClass(Class c, DAssoc manyAssoc, Class normAttribType) 
  throws NotFoundException {

    // the first link attribute
    String normAttribName1 = manyAssoc.normAttrib();
    DAttr normAttrib1 = getDomainConstraint(c, normAttribName1);
    Tuple2<DAttr,DAssoc> normAssocTuple1 = getAssociation(c, normAttrib1);
    Tuple2<DAttr,DAssoc> linkAssocTuple1 = getTargetAssociation(normAssocTuple1.getSecond());
    String linkAttrib1 = linkAssocTuple1.getFirst().name();
    
    // the second link attrib
    Tuple2<DAttr,DAssoc> targetManyAssocTuple = getTargetAssociation(manyAssoc);
    Class manyAssocCls = manyAssoc.associate().type();
    String normAttribName2 = targetManyAssocTuple.getSecond().normAttrib();
    DAttr normAttrib2 = getDomainConstraint(manyAssocCls, normAttribName2);
    Tuple2<DAttr,DAssoc> normAssocTuple2 = getAssociation(manyAssocCls, normAttrib2);
    Tuple2<DAttr,DAssoc> linkAssocTuple2 = getTargetAssociation(normAssocTuple2.getSecond());
    String linkAttrib2 = linkAssocTuple2.getFirst().name();
    
    return Tuple2.newTuple2(linkAttrib1, linkAttrib2);
  }
  
  /**
   * This method is the reverse of {@link #isDeterminant(Class, DAttr)}.
   * @effects 
   *  if exists <tt>Association</tt> of the domain attribute <tt>c.attrib</tt>
   *  s.t. <tt>Association.associate</tt> is the determinant
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isDeterminedByAssociate(Class c, DAttr attrib) {
//    try {
//      Tuple2<DomainConstraint,Association> assocTuple = getAssociation(c, attrib);
//      Association assoc = assocTuple.getSecond();
//      return (assoc.type().equals(AssocType.One2One) && 
//          assoc.associate().determinant());
//    } catch (NotFoundException e) {
//      return false;
//    }
    return isDeterminedByAssociate(c, attrib, null);
  }
  
  /**
   * This method is the reverse of {@link #isDeterminant(Class, DAttr)}.
   * @effects 
   *  if exists <tt>Association a(c.attrib,d.attrib1)</tt> defined in <tt>c</tt> w.r.t some domain class <tt>d</tt> 
   *  s.t. <tt>d</tt> is the determinant and if <tt>assocClass = null \/ assocClass = d</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isDeterminedByAssociate(Class c, DAttr attrib, Class assocClass) {
    try {
      Tuple2<DAttr,DAssoc> assocTuple = getAssociation(c, attrib);
      DAssoc assoc = assocTuple.getSecond();
      Associate associate = assoc.associate();
      return (assoc.ascType().equals(AssocType.One2One) &&
          (assocClass == null || assocClass == associate.type()) && 
          associate.determinant());
    } catch (NotFoundException e) {
      return false;
    }
  }
  

  /**
   * This method is the reverse of {@link #isDeterminedByAssociate(Class, DAttr)}.
   * 
   * @effects 
   *  if exists <tt>Association a=(b.attrib1,c.attrib)</tt> defined in some domain class <tt>b</tt> w.r.t <tt>c</tt>
   *  s.t. <tt>c</tt> is the determinant
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isDeterminant(Class c, DAttr attrib) {
    try {
      Tuple2<DAttr,DAssoc> targetTuple = getTargetAssociation(c, attrib);
      DAssoc target = targetTuple.getSecond();
      return (target.ascType().equals(AssocType.One2One) && 
          target.associate().determinant());
    } catch (NotFoundException e) {
      return false;
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
   * @effects 
   *  if exists {@link Constructor} method of <tt>c</tt> whose parameters are annotated with 
   *  names of the attributes in <tt>attribs</tt>
   *    return it
   *  else
   *    return null
   *    
   * @version 5.0
   */
  private <T> Constructor<T> findConstructorMethod(Class<T> c, Collection<DAttr> attribs) {
    // find in c the constructor whose parameters reference these attributes
    Constructor[] constructors = c.getConstructors(); 
    
    if (constructors.length == 0) // no constructors
      return null;
    
    int numAttribs = attribs.size();
    Constructor theCons = null;
    for (Constructor cons : constructors) {
      Parameter[] params = cons.getParameters();
      if (params.length == numAttribs) {
        // possibly this one...
        boolean match = true;
        for (Parameter param : params) {
          AttrRef attrRef = param.getAnnotation(AttrRef.class);
          if (attrRef == null) {
            // param is not annotated with AttrRef -> not this constructor
            match = false;
            break;
          } else {
            boolean paramMatchAttrib = false;
            for (DAttr attrib : attribs) {
              if (attrib.name().equals(attrRef.value())) {
                // param references an attrib -> possibly this one
                paramMatchAttrib = true;
                break;
              }
            }
            
            if (!paramMatchAttrib) {
              // param does not reference any attribs -> not this constructor
              match = false;
              break;
            }
          }
//          else if (attribs.contains(attrRef.value())) {
//            // param references a required attrib -> possibly this one
//          } else {
//            // param does not references a required attrib -> not this constructor
//            match = false;
//            break;
//          }
        } // end params
        
        if (match) {
          // cons is a match
          theCons = cons;
          break;
        }
      }
    }
    
    // return result
    return theCons;
  }
  
  /**
   * @requires 
   *  <tt>attribs</tt> contains the domain constraints of the 
   *  domain attributes of <tt>cls</tt> /\
   *  at least one of these attributes is a required attribute (i.e. its {@link DAttr}<tt>.optional = false</tt>)
   *  
   * @effects 
   *  Find in <tt>cls</tt> the {@link Constructor} whose parameters are defined based on 
   *  the required domain attributes in <tt>attribs</tt>.
   *  If found then return the {@link Constructor} else return <tt>null</tt>.
   *  
   *  <p>The required constructor will have enough parameters to  
   *  reference the corresponding <i>noncollection-based</i> required attributes.
   *  
   *  @version 5.0 
   */
  public static Constructor findRequiredConstructor(Class cls,
      Collection<DAttr> attribs) {
    // extract from dcMap the required attributes
    Set<String> requiredAttribs = new HashSet<>();
    
    boolean hasRequiredColTypeAttrib = false;
    for(DAttr dc : attribs) {
      String attribName = dc.name();
      
      if (!dc.optional()) {
        if (dc.type().isCollection()) {
          hasRequiredColTypeAttrib = true;
        } else {          
          // a required attribute that is not collection-typed
          requiredAttribs.add(attribName);
        }
      }
    }
    
    if (requiredAttribs.isEmpty() && !hasRequiredColTypeAttrib) {
      // no required attributes -> no required constructor
      return null;
    }
        
    // find in cls the constructor whose parameters reference these attributes
    Constructor[] constructors = cls.getConstructors(); 
    
    if (constructors.length == 0) // no constructors
      return null;
    
    int numAttribs = requiredAttribs.size();
    Constructor requiredCons = null;
    for (Constructor cons : constructors) {
      Parameter[] params = cons.getParameters();
      if (params.length == numAttribs) {
        // possibly this one...
        boolean match = true;
        for (Parameter param : params) {
          AttrRef attrRef = param.getAnnotation(AttrRef.class);
          if (attrRef == null) {
            // param is not annotated with AttrRef -> not this constructor
            match = false;
            break;
          } else if (requiredAttribs.contains(attrRef.value())) {
            // param references a required attrib -> possibly this one
          } else {
            // param does not references a required attrib -> not this constructor
            match = false;
            break;
          }
        } // end params
        
        if (match) {
          // cons is a match
          requiredCons = cons;
          break;
        }
      }
    }
    
    // return result
    return requiredCons;
  }
  
  /**
   * @effects returns <code>Method</code> object of the class <code>c</code>,
   *          whose name is <code>"get" + f.getName()</code> (with first letter
   *          capitalised), or throws <code>NotFoundException</code> if no
   *          such method exists.
   * 
   */
  private Method findGetterMethod(Field f, Class c) throws SecurityException,
      NotFoundException {
//    String fname = f.getName();
//    return findGetterMethod(fname, c);
    return findGetterMethod(f.getName(), c);
  }

  /**
   * @effects returns <code>Method</code> object of the class <code>c</code>,
   *          whose name is <code>"get" + fieldName</code> (with first letter
   *          capitalised).
   *          
   *          <p>Throws <tt>NotFoundException</tt> if no
   *          such method exists; SecurityException if could not access the method
   * 
   */
  private static Method findGetterMethod(String fieldName, Class c)
      throws //SecurityException, 
      NotFoundException {
    
    fieldName = (fieldName.charAt(0) + "").toUpperCase()
        + fieldName.substring(1);
    String mname = "get" + fieldName;
    try {
      Method getter = c.getMethod(mname, null);
      return getter;
    } catch (NoSuchMethodException e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          e, new Object[] {c, mname});
    }
  }

  /**
   * @effects return <tt>Method</tt> object of the class <tt>c</tt>,
   *          whose name is <tt>methodName</tt>; 
   *          
   *          <p>Throws <tt>NotFoundException</tt> if no
   *          such method exists; NotPossibleException if could not access the method
   * 
   */
  public Method findMethod(String methodName, Class c, Class returnType) 
      throws NotPossibleException, NotFoundException {
    try {
      Method getter = c.getMethod(methodName, null);
      
      if (returnType != null) {
        Class rt = getter.getReturnType();
        if (rt == null || 
            !returnType.isAssignableFrom(rt)) {
          throw new NotFoundException(NotFoundException.Code.METHOD_WITH_RETURN_TYPE_NOT_FOUND, 
              "Không tìm thấy phương thức {0}.{1}(): {2}", c, methodName, returnType);
        }
      }
      return getter;
    } catch (NoSuchMethodException e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          e, "Không tìm thấy phương thức {0}.{1}()", c, methodName);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
          "Không thể thực thi phương thức {0}.{1}()", c.getSimpleName(), methodName);
    }
  }
  
  /**
   * @effects 
   *  find in class <tt>c</tt> a getter method for attribute <tt>attribName</tt>.
   *  <br>If found, execute method on object <tt>o</tt> and return the result, casted to the expected return type <tt>T</tt>.
   *  
   *  <p>Throws NotFoundException if method is not found, NotPossibleException if failed to execute the method.
   *  
   * @version 5.4.1
   */
  public static <T> T doGetterMethod(Class cls, Object o, String attribName, Class<T> returnType, Object...args) 
      throws NotPossibleException, NotFoundException {
    Method m = findGetterMethod(attribName, cls);
    
    try {
      return (T) m.invoke(o, args);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, new Object[] {o, "getter_" + attribName, ""});
    } 
  }
  
  /**
   * @effects 
   *  find in class <tt>c</tt> a setter method for attribute <tt>attribName</tt>.
   *  <br>If found, execute method on object <tt>o</tt>, using <tt>args</tt>, and return the result.
   *  
   *  <p>Throws NotFoundException if method is not found, NotPossibleException if failed to execute the method.
   *  
   * @version 2.8
   */
  public static void doSetterMethod(Class cls, Object o, String attribName, Object...args) 
      throws NotPossibleException, NotFoundException {
    Method m = findSetterMethod(cls, attribName);
    
    try {
      m.invoke(o, args);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, new Object[] {o, "setter_" + attribName, ""});
    } 
  }
  
  /**
   * @effects 
   *  invoke <tt>method</tt> on <tt>uponObj</tt> with <tt>args</tt>; 
   *  if <tt>method</tt> has a return-type then return the result, otherwise return <tt>null</tt>
   *  
   *  <p>throws NotPossibleException if fails to do so.
   */
  public Object doMethod(Class cls, Method method, Object uponObj, Object...args) throws NotPossibleException {
    try {
      Class rt = method.getReturnType();
      if (!rt.getName().equals("void")) {
        // has return type
        if (args != null && args.length > 0)
          return method.invoke(uponObj, args);
        else
          return method.invoke(uponObj, null);
      } else {
        // no return type
        if (args != null && args.length > 0)
          method.invoke(uponObj, args);
        else
          method.invoke(uponObj, null);
        return null;
      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,  
          "Không thể thực thi phương thức {0}<{1}>.{2}({3})", 
          cls.getSimpleName(), uponObj, method.getName(), 
          (args != null && args.length > 0) ? Arrays.toString(args) : "");
    }
  }
    
  /**
   * @effects returns <code>Method</code> object of the class <code>cls</code>,
   *          whose name is <code>"set" + f.getName()</code> (with first letter
   *          capitalised)
   */
  public static Method findSetterMethod(Field f, Class cls) throws SecurityException,
      NotFoundException {
    return findMethod(cls, f, "set");
  }

  /**
   * @effects returns <code>Method</code> object of the class <code>cls</code>,
   *          whose name is <code>"set" + attribName</code> (with first letter
   *          capitalised)
   */
  public static Method findSetterMethod(Class cls, String fieldName) 
      throws SecurityException, NotFoundException {
    
    fieldName = (fieldName.charAt(0) + "").toUpperCase()
        + fieldName.substring(1);
    String mname = "set" + fieldName;
    try {
      Method setter = cls.getMethod(mname, null);
      return setter;
    } catch (NoSuchMethodException e) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          e, new Object[] {cls.getSimpleName(), mname, ""});
    }
  }
  
  /**
   * This method <b>should ONLY</b> be used for adding a association link between domain objects.
   * For other cases, use {@link #findSetterMethod(Field, Class)} instead.
   *  
   * @effects returns <code>Method</code> object of the class <code>cls</code>,
   *          whose name is <code>"setNew" + f.getName()</code> (with first letter
   *          capitalised) 
   * @version 2.7.3
   */
  public Method findSetterNewMethod(Field f, Class cls) {
    return findMethod(cls, f, "setNew");
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

  /**
   * @effects returns <code>Method</code> object of the class <code>c</code>,
   *          whose name is <code>prefix + f.getName(f.getType)</code> (with the first letter
   *          capitalised).
   *          
   *          <p>Throws SecurityException if could not access the method, 
   *          NotFoundException if the method is not found.
   * 
   */
  private static Method findMethod(Class cls, Field f, String prefix) throws SecurityException,
      NotFoundException {
    String fname = f.getName();
    Class type = f.getType();
    fname = (fname.charAt(0) + "").toUpperCase() + fname.substring(1);
    String mname = prefix + fname;

    // first try the method with the same type
    try {
      Method m = cls.getMethod(mname, type);
  
      if (m == null) { // otherwise, find methods having the same name
        Method[] methods = cls.getMethods();
  
        for (int i = 0; i < methods.length; i++) {
          m = methods[i];
          if (m.getName().equals(mname)) {
            break;
          }
        }
      }
      return m;
    } catch (NoSuchMethodException e ) {
      throw new NotFoundException(NotFoundException.Code.METHOD_NOT_FOUND, 
          e, new Object[] {cls, mname});
    }
  }

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
   * @effects 
   *  find and return the first method of <tt>c</tt> that is annotated with <tt>methodType</tt> and 
   *  if <tt>namePrefix</tt> is specified then whose name starts with that prefix. 
   *  
   *  <p>throws NotFoundException if could not find the method.
   */
  public Method findMetadataAnnotatedMethodWithNamePrefix(Class c, DOpt.Type methodType, String namePrefix) throws NotFoundException {
    
    Map<DOpt.Type,Collection<Method>> methodMap = classMethods.get(c); 
    if (methodMap != null) {
      Collection<Method> methods = methodMap.get(methodType);
      if (methods != null) {
        if (namePrefix != null) {
          for (Method method: methods) {
            if (method.getName().startsWith(namePrefix)) {
              // found
              return method;
            }
          }
        } else {
          // return the first match
          return methods.iterator().next();
        }
      }
    }    
    // not found
    throw new NotFoundException(NotFoundException.Code.METHOD_ANNOTATED_NOT_FOUND, 
        // v3.3: "Không tìm thấy phương thức {0}#{1}({2}.{3})", new Object[] {c, namePrefix, METADATA.getSimpleName(), methodType});    
        new Object[] {c, namePrefix, methodType.name(), ""});
  }  
  
  /**
   * @effects 
   *  find and return the first method of <tt>c</tt> that is annotated with <tt>methodType</tt> and 
   *  if <tt>attrib != null</tt> then that refers to the attribute <tt>attrib</tt> 
   *  
   *  <p>throws NotFoundException if could not find the method.
   *  
   * @version 3.1
   */
  public Method findMetadataAnnotatedMethod(Class c, DOpt.Type methodType, DAttr attrib) throws NotFoundException {
    if (attrib != null)
      return findMetadataAnnotatedMethod(c, attrib.name(), methodType);
    else
      return findMetadataAnnotatedMethod(c, null, methodType);
  } 
  
  /**
   * This differs from {@link #findMetadataAnnotatedMethod(Class, jda.modules.dcsl.syntax.DOpt.Type, DAttr)} 
   * that it forces the exact matching of the method type to the attribute. 
   * 
   * @requires 
   *  attrib != null /\ methodType != null
   *  
   * @effects 
   *  find and return the <b>first</b> method of <tt>c</tt> that is annotated with <tt>methodType</tt> and 
   *  that refers to the attribute <tt>attrib</tt> 
   *  
   *  <p>throws NotFoundException if could not find the method.
   *  
   * @version 3.2
   */
  public Method findMetadataAnnotatedMethodWithAttribute(Class c,
      DOpt.Type methodType,
      DAttr attrib, Class...parameterTypes) {
    String attribName = attrib.name();
    Map<DOpt.Type,Collection<Method>> methodMap = classMethods.get(c); 
    
    if (methodMap != null) {
      //Collection<Method> methods = classMethods.get(c).get(methodType);
      Collection<Method> methods = methodMap.get(methodType);
      AttrRef attribRef;
      boolean matchParams = parameterTypes != null && parameterTypes.length > 0;
      if (methods != null) {
        for (Method method: methods) {
          // attrib name matching is performed first 
          attribRef = method.getAnnotation(MEMBER_REF);
          
          if (attribRef != null &&  
                attribRef.type() == FIELD &&
                attribRef.value().equals(attribName)) {
            // attrib name matching
            // now perform parameter matching (if specifed)
            if (!matchParams || typeArrayAssignCompatible(parameterTypes, method.getParameterTypes())) {
              // either param matching is not required or parameter matching is ok
              // match full -> return this one
              return method;
            }
          }
        } // end for
      }
    }
    
    // not found
    throw new NotFoundException(NotFoundException.Code.METHOD_ANNOTATED_NOT_FOUND, 
        // v3.3: "Không tìm thấy phương thức {0}.{1}#{2}({3})", 
        new Object[] {c, attribName, methodType.name(), Arrays.toString(parameterTypes)});
  }
  
  /**
   * @effects 
   *  look up in <tt>c</tt> and return all methods annotated with <tt>methodType</tt>. 
   *  
   *  <br>If no methods found, return <tt>null</tt>
   * 
   *  @version 2.7.4
   */
  public Collection<Method> findMetadataAnnotatedMethods(Class c, DOpt.Type methodType) {
    Map<DOpt.Type,Collection<Method>> methodMap = classMethods.get(c); 
    
    if (methodMap != null) {
      Collection<Method> methods = methodMap.get(methodType);
      
      return methods;
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  find and return the method of <tt>c</tt> that refers to attribute <tt>attribName</tt>, 
   *  is annotated with <tt>methodType</tt>, and  
   *  whose parameter types are <tt>parameterTypes</tt>.
   *  
   *  <p>If <tt>attribName = null</tt> then returns the first method matching the other criteria.
   *  
   *  <p>If <tt>attribName != null</tt> then returns the first method matching all criteria. 
   *  If no such methods can be 
   *  found and there is only one matching method then return that method; 
   *  otherwise throws NotFoundFoundException
   *  
   *  <p>throws NotFoundException if could not find the method.
   */
  private Method findMetadataAnnotatedMethod(Class c, String attribName, DOpt.Type methodType, 
      Class...parameterTypes) throws NotFoundException {
    
    Map<DOpt.Type,Collection<Method>> methodMap = classMethods.get(c); 
    
    if (methodMap != null) {
      //Collection<Method> methods = classMethods.get(c).get(methodType);
      Collection<Method> methods = methodMap.get(methodType);
      AttrRef attribRef;
      
      if (methods != null) {
        List<Method> matching = new ArrayList<Method>();
        boolean paramMatch;
        boolean attribMatch; 
        
        for (Method method: methods) {
          paramMatch = false;
          attribMatch = false;
          
          // (1) check parameter types
          if (
              /*v3.0: support sub-type matching
              Arrays.equals(parameterTypes,method.getParameterTypes())
               */
              typeArrayAssignCompatible(parameterTypes, method.getParameterTypes())
              ) {
            // a match
            paramMatch = true;
            if (attribName == null) {
              // no need to match name, return this one immediately
              return method;
            } else {
              // records this match for later processing
              matching.add(method);
            }
          } // end parameter matching
  
          // (2) check name matching
          /*v2.7.2: separate attribute matching from parameter matching here and combine them later
          if (paramMatch && attribName != null) {
            attribRef = method.getAnnotation(MEMBER_REF);
            
            if (attribRef != null &&  
                  attribRef.type() == FIELD &&
                  attribRef.name().equals(attribName)) {
              // match full -> return this one immediately
              return method;
            }
          }
          */
          if (attribName != null) {
            attribRef = method.getAnnotation(MEMBER_REF);
            
            if (attribRef != null &&  
                  attribRef.type() == FIELD &&
                  attribRef.value().equals(attribName)) {
              attribMatch = true;
            }
          } // end attribute matching
          
          if (attribMatch) {
            if (paramMatch) {
              // match full -> return this one immediately
              return method;
            } else {
              // records this match for later processing
              matching.add(method);
            }
          }
        } // end for
        
        // if we get here and there is only one matching then return the match, otherwise throws exception
        if (matching.size() == 1) {
          return matching.get(0);
        }
      }
    }
    
    // not found
    throw new NotFoundException(NotFoundException.Code.METHOD_ANNOTATED_NOT_FOUND, 
        // v3.3: "Không tìm thấy phương thức {0}.{1}#{2}({3})", 
        new Object[] {c, attribName, methodType.name(), Arrays.toString(parameterTypes)});    
  } 
  
  /**
   * @requires 
   *  typeArr1 != null /\ typeArr2 != null
   * @effects 
   *  if the types in <tt>typeArr1</tt> are assignment compatible to the corresponding types in 
   *    <tt>typeArr2</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   */
  private boolean typeArrayAssignCompatible(Class[] typeArr1, Class[] typeArr2) {
    if (typeArr1.length != typeArr2.length) {
      return false;
    }
    
    Class t2;
    int index = 0;
    for (Class t1 : typeArr1) {
      t2 = typeArr2[index];
      
      // v3.1: if (!t1.isAssignableFrom(t2)) {
      if (!t2.isAssignableFrom(t1)) {
        // not assignment compatible
        return false;
      }
      
      index++;
    }
    
    // all assignment compatibles
    return true;
  }

  /**
   * @requires 
   *  cls != null
   * @effects 
   *  return the <tt>Method</tt> object of <tt>cls</tt>,   
   *  whose name is the name of the <b>adder</b> method for the attribute <tt>attribName</tt>, and   
   *  whose parameter types are <tt>paramTypes</tt>
   *  
   *  <p>Throws NotFoundException if no such method exists.
   */
  public Method findAttributeValueAdderMethod(Class cls, String attribName, Class...paramTypes) 
  throws NotFoundException {
    //String valueClsName = valueCls.getSimpleName();
    //String namePrefix = "add" + valueClsName; 
    
    return findMetadataAnnotatedMethod(cls, attribName, DOpt.Type.LinkAdder, paramTypes);
  }
  
  /**
   * This method <b>should ONLY</b> be used to add <b>new</b> association links between domain objects.
   * For other cases, use {@link #findAttributeValueAdderMethod(Class, String, Class...)} instead. 
   * 
   * @requires 
   *  cls != null
   * @effects 
   *  return the <tt>Method</tt> object of <tt>cls</tt>,   
   *  whose name is the name of the <b>adder-new</b> method for the attribute <tt>attribName</tt>, and   
   *  whose parameter types are <tt>paramTypes</tt>
   *  
   *  <p>Throws NotFoundException if no such method exists.
   *  
   * @version 2.7.3
   */
  public Method findAttributeValueAdderNewMethod(Class cls, String attribName,
      Class...paramTypes) throws NotFoundException {
    return findMetadataAnnotatedMethod(cls, attribName, 
        DOpt.Type.LinkAdderNew, paramTypes);
  }
  
  /**
   * @requires 
   *  cls != null /\ valueCls != null
   * @effects 
   *  return <b>remover</b> (i.e. deleter) <tt>Method</tt> object for the attribute <tt>attribName</tt> 
   *  whose parameter types are <tt>paramTypes</tt> 
   *  
   *  <p>Throws NotFoundException if no such method exists.
   */
  public Method findAttributeValueRemoverMethod(Class cls, String attribName, Class...paramTypes) 
  throws NotFoundException {
//    String valueClsName = valueCls.getSimpleName();
//    String namePrefix = "remove" + valueClsName; 
    
    Method m = findMetadataAnnotatedMethod(cls, attribName, DOpt.Type.LinkRemover, paramTypes);
    
    return m;
  }
  
  /**
   * @requires 
   *  cls != null /\ valueCls != null
   * @effects 
   *  return <b>updater</b> <tt>Method</tt> object for the attribute <tt>attribName</tt> 
   *  whose parameter types are <tt>paramTypes</tt> 
   *  
   *  <p>Throws NotFoundException if no such method exists.
   */
  public Method findAttributeValueUpdaterMethod(Class cls, String attribName, Class...paramTypes) 
  throws NotFoundException {
    Method m = findMetadataAnnotatedMethod(cls, attribName, DOpt.Type.LinkUpdater, paramTypes);
    
    return m;
  }
  
  /**
   * @requires 
   *  cls != null /\ valueCls != null
   * @effects 
   *  return {@link Map} containing domain attributes and their associated <b>updater</b> <tt>Method</tt> object(s) 
   *  where the attributes are derived from <b>two or more</b>
   *  other attributes of <tt>cls</tt> and one of these attributes is the attribute <tt>dependOnAttrib</tt>.
   *  
   *  <p>return <tt>null</tt> if no such methods were found.
   * 
   * @example
   *  <pre>
   *  cls = Enrolment
   *  attributes: Enrolment:internalMark, examMark, finalMark
   *  deriving attribute: Enrolment:finalMark depends-on Enrolment:internalMark,examMark
   *  update method: Enrolment.updateFinalMark() references Enrolment.finalMark
   *  </pre>
   *  <p>Then:
   *  <pre>
   *  findAttributeValueDependentUpdaterMethod(cls,Enrolment:internalMark) = Map:{(Enrolment:internalMark,Method(Enrolment.updateFinalMark)}
   *  findAttributeValueDependentUpdaterMethod(cls, Enrolment:examMark) = Map:{(Enrolment:examMark,Method(Enrolment.updateFinalMark))}
   *  </pre>
   * @version 
   * - 2.6.4b <br>
   * - v3.1: change return type to Map
   */
  public Map<DAttr, Method> findAttributeValueMultiDependentUpdaterMethods(Class cls, DAttr dependOnAttrib) 
  throws NotFoundException {
    //Collection<Method> dependentMethods = new ArrayList<Method>();
    Map<DAttr,Method> dependentMethods = new HashMap();
    
    // find the deriving attributes that depend on two or more other attributes, one of which is dependOnAttrib
    Collection<DAttr> derivingAttribs = classDerivingAttribs.get(cls);
    
    if (!derivingAttribs.isEmpty()) {
      // there are deriving attributes
      String[] derivedFrom;
      String dependOnAttribName = dependOnAttrib.name();
      Method updateMethod;
      
      for (DAttr dervAttrib : derivingAttribs) {
        derivedFrom = dervAttrib.derivedFrom();
        if (derivedFrom.length > 1) { // depend on multiple attributes
          for (String name : derivedFrom) {
            if (name.equals(dependOnAttribName)) {  // one of those is dependOnAttribName
              // found one, find the value update method
              updateMethod = findMetadataAnnotatedMethod(cls, dervAttrib.name(), DOpt.Type.DerivedAttributeUpdater);
              if (updateMethod != null) {
                //dependentMethods.add(updateMethod);
                dependentMethods.put(dervAttrib, updateMethod);
              }
              break;
            }
          }
        }
      }
    }
    
    return (dependentMethods.isEmpty()) ? null : dependentMethods;
  }
  
  /**
   * @requires 
   *  c != null
   * @effects 
   *  return <b>refreshState</b> <tt>Method</tt> object for the class <tt>c</tt> 
   *  or <tt>null</tt> if no such method exists.
   */
  public Method findRefreshStateMethod(Class c) {
    try {
      Method m = findMetadataAnnotatedMethod(c, null, DOpt.Type.ObjectStateRefresher);
      
      return m;
    } catch (NotFoundException e) {
      return null;
    }
  }

  
  /**
   * @requires 
   *  attribute != null
   * @effects 
   *  return <tt>Method</tt> object representing the method 
   *  whose name is the name of the adder method defined in the <tt>Update</tt>
   *  annotation of the specified attribute, and whose argument types are <tt>parameterTypes</tt>
   *  
   *  <p>Throws NotFoundException if no such method exists.
   *  @deprecated as of version 2.6.4.b
   */
  /*v2.7.2: not used 
  private Method findAttributeValueAdderMethod(Class cls, Field attribute, Class...parameterTypes) 
  throws NotFoundException {
    Update update = attribute.getAnnotation(UPDATE);
    
    if (update == null)
      throw new NotFoundException(NotFoundException.Code.UPDATE_METHOD_NOT_FOUND, 
          "Không tìm thấy phương thức cập nhật dữ liệu cho thuộc tính " + attribute);
    
    String methodName = update.add();
    
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
  
  /**
   * @effects returns <code>Field</code> whose name is the field
   *          <code>name</code> of the class <code>c</code> or of the super (or
   *          ancestor) class of <code>c</code>; throws
   *          <code>NotFoundException</code> if no such field exists.
   */
  public Field getDomainAttribute(Class c, String name) throws NotFoundException {
    Collection<Field> fields = (Collection<Field>) classDefs.get(c).keySet();

    if (fields != null) {
      for (Field f : fields) {
        if (f.getName().equals(name)) {
          return f;
        }
      }
    }
    throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {name, c.getSimpleName()});
  }

  /**
   * @effects returns <code>Field</code> of the class <code>c</code> or of a super or ancestor class whose 
   * whose DomainConstraint is <tt>attrib</tt>; or null if no such attribute is found in <tt>c</tt>
   * @version 
   * - 5.0: changed to use attrib.name to match 
   * 
   */
  public Field getDomainAttribute(Class c, DAttr attrib) {
    Collection<Field> fields = (Collection<Field>) classDefs.get(c).keySet();

    if (fields != null) {
      for (Field f : fields) {
        //v5.0: if (f.getAnnotation(DC).equals(attrib)) {
        if (f.getName().equals(attrib.name())) { // found
          return f;
        }
      }
    }
  
    return null;
  }
  
  /**
   * @requires
   *  c != null /\ attributeType != null
   * @effects returns <tt>Field</tt> of the class <tt>c</tt> or 
   *  of the super (or ancestor) class of <tt>c</tt>, 
   *  whose declared type is <tt>attributeType</tt>; 
   *  
   *  <p>throws <tt>NotFoundException</tt> if no such field exists.
   */
  private Field getDomainAttributeByType(Class c, Class attributeType) throws NotFoundException {
    Collection<Field> fields = (Collection<Field>) classDefs.get(c).keySet();

    if (fields != null) {
      for (Field f : fields) {
        if (f.getType() == attributeType) {
          return f;
        }
      }
    }
    
    throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND,
        new Object[] {attributeType, c.getSimpleName()});
  }
  
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

  /**
   * @effects returns <code>DomainConstraint</code> objects of the annotated
   *          attributes of <code>c</code>, or <code>null</code> if no such
   *          constraints exist.
   * @deprecated same as {@link #getDomainConstraints(Class)}
   */
  public Collection<DAttr> getAttributeConstraints(final Class c) {
    return getDomainConstraints(c); //classConstraints.get(c);
  }

  /**
   * @effects returns <code>DomainConstraint</code> objects of the annotated
   *          attributes of the domain class named <code>clsName</code>, or
   *          <code>null</code> if no such constraints exist.
   * @version 2.1b
   */
  public Collection<DAttr> getAttributeConstraints(final Class c, 
      final String[] attributes)
      throws NullPointerException, NotFoundException {
    if (c == null)
      throw new NullPointerException("DomainSchema.getAttributeConstraints: domain class name is null");
    
    if (c == null)
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
          "Không tìm thấy lớp: {0}", c);

    Collection<DAttr> cons = getDomainConstraints(c); //getAttributeConstraints(c);

    // only return those matching the attributes (if specified)
    if (attributes != null) {
      List<DAttr> filterCons = new ArrayList();
      for (DAttr co : cons) {
        for (int i = 0; i < attributes.length; i++) {
          if (co.name().equals(attributes[i])) {
            filterCons.add(co);
          }
        }
      }
      return (!filterCons.isEmpty()) ? filterCons : null;
    } else {
      return cons;
    }
  }

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
   * This method uses both {@link #isDependedOn(Class, DAssoc)} and <b>is-determinant</b>.
   * 
   * @requires 
   *  c1 != null /\ assoc != null /\ 
   *  assoc is an <tt>Association</tt> that implements c1's end of  
   *  an association with another domain class
   *  
   * @effects 
   *  if c1 is <b>depended on</b> the domain class to which it is associated via <tt>assoc</tt>
   *  (i.e. <tt>assoc.dependsOn=true \/ assoc.associate.determinant = true</tt>)
   *    return true
   *  else 
   *    return false
   *  @version 2.7.4
   */
  public boolean isDependedOn(Class c1, DAssoc assoc) {
    boolean dependsOn = assoc.dependsOn();
    
    if (dependsOn)
      return true;
    
    return assoc.associate().determinant();
  }

  /**
   * @requires 
   *  o1 != null /\ o2 != null /\ linkAttribute != null /\ 
   *  linkAttribute is a domain attribute of o1.class that implements this class
   *  end of an association with o2.class
   * @effects 
   *  if o1.class depends on o2.class w.r.t link attribute <tt>linkAttribute</tt>
   *    return true
   *  else 
   *    return false 
   */
  public boolean isDependentOn(Object o1, DAttr linkAttribute, Object o2) {
    return isDependentOn(o1.getClass(), linkAttribute, o2.getClass());
  }  
  
  /**
   * @requires 
   *  c1 != null /\ c2 != null /\ linkAttribute != null /\ 
   *  linkAttribute is a domain attribute of c1 that implements its end of  
   *  an association to c2.
   *  
   * @effects 
   *  if c1 depends on c2 w.r.t. <tt>linkAttribute</tt>, 
   *    return true
   *  else 
   *    return false
   * @version 
   *  2.6.4.a changed to use {@link DAssoc#dependsOn()}
   *    
   */
//  *    i.e. there exists an M:1 or 1:1 association (c1,c2) whose link attribute of the c1's end is linkAttribute 
//  *    and the min cardinality of c2's end = 1.  
  public boolean isDependentOn(Class c1, DAttr linkAttribute, Class c2) {
    // associations of c1
    List<Tuple2<DAttr,DAssoc>> tuples = classAssocs.get(c1);
    
    if (tuples != null) {
      // check for association with c2
      DAttr dc;
      DAssoc assoc;
      for (Tuple2<DAttr,DAssoc> tuple : tuples) {
        dc = tuple.getFirst();
        assoc = tuple.getSecond();
        if (dc.equals(linkAttribute) && assoc.dependsOn()) {
          return true;
        }
      }
    }
    
    // not dependent
    return false;
  }
  
  /**
   * This method is another variant of {@link #isDependentOn(Class, String, Class)}.
   * 
   * @requires 
   *  c1 != null /\ assoc != null /\ 
   *  assoc is an <tt>Association</tt> that implements c1's end of  
   *  an association with another domain class
   *  
   * @effects 
   *  if c1 depends on the domain class to which it is associated via <tt>assoc</tt>
   *    return true
   *  else 
   *    return false
   * @version 2.6.4.a changed to use {@link DAssoc#dependsOn()}
   */
//  *    i.e. assoc is M:1 or 1:1 and 
//  *    the min cardinality of the other domain class' end of <tt>assoc</tt> is = 1.  
//  *    return true
  public boolean isDependentOn(Class c1, DAssoc assoc) {
    // check min card
    return assoc.dependsOn();
//    AssocType assocType;
//    AssocEndType endType;
//    AssocEnd associate;
//    assocType = assoc.type();
//    endType = assoc.endType();
//    associate = assoc.associate();
//    if (((assocType.equals(AssocType.One2Many) && endType.equals(AssocEndType.Many)) || 
//         (assocType.equals(AssocType.One2One)))) {
//      // found association 
//      // check min card
//      if (associate.cardMin() == 1) {
//        // dependent
//        return true;
//      } else {
//        return false;
//      }
//    } else {
//      // not dependent
//      return false;
//    }
  }
  
  /**
   * @requires c != null /\ classList != null
   * @effects 
   *  if c is one of the classes in classList
   *    return true
   *  else
   *    return false
   */
  private boolean isIn(Class c, Class[] classList) {
    for (Class cls : classList) {
      if (c == cls) {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * @effects if class <code>c1</code> is a descendent of class <code>c2</code>
   *          returns <code>true</code>, else returns <code>false</code>.
   * 
   * @version 
   *  - 3.0: fixed error in not recognising c2 when it is an interface type <br>
   *  - 3.3: made static
   */
  private static boolean isDecendant(Class c1, Class c2) {
    /* v3.0
    Class sup = c1.getSuperclass();

    if (sup == null)
      return false;

    if (sup == c2) {
      return true;
    } else if (sup != Object.class) {
      return isDecendant(sup, c2);
    } else {
      return false;
    }
    */
    return c2.isAssignableFrom(c1);
  }

  /**
   * @effects returns <code>true</code> if <code>c</code> is an abstract class.
   */
  public static boolean isAbstract(Class c) {
    return Modifier.isAbstract(c.getModifiers());
  }

  /**
   * @effects if <code>c != null && </code> is annotated with {@see
   *          ClassConstraint} with <code>serialisable = false</code> then
   *          returns <code>true</code>, else returns <code>false</code>
   */
  public static boolean isTransient(Class c) {
    if (c != null) {
      DClass cc = (DClass) c
          .getAnnotation(DClass.class);
      if (cc != null) {
        if (cc.serialisable() == false) {
          return true;
        }
      }

      return false;
    } else {
      return true;
    }
  }

  /**
   * @requires 
   *  <tt>c != null</tt>
   *  
   * @effects 
   *  if <tt>c</tt> is configured to have a constant object-pool
   *    return true
   *  else
   *    return false
   * @version 3.3
   */
  public static boolean isConstantObjectPool(Class c) {
    if (c != null) {
      DClass cc = (DClass) c.getAnnotation(DClass.class);
      if (cc != null) {
        return cc.objectPoolIsConstant();
      } else {
        return false; // default
      }
    } else {
      return false; // default
    }
  }

  /**
   * @effects 
   *  if <tt>cls</tt> is an {@link Enum}
   *    return true
   *  else
   *    return false
   * @version 3.3
   */
  public static boolean isEnum(Class cls) {
    if (cls == null)
      return false;
    
    return cls.isEnum();
  }

  /**
   * @effects if <code>c != null && </code> is annotated with {@see
   *          ClassConstraint} with <code>mutable = false</code> then returns
   *          <code>false</code>, else returns <code>true</code>
   */
  public static boolean isEditable(Class c) {
    if (c != null) {
      DClass cc = (DClass) c
          .getAnnotation(DClass.class);
      if (cc != null) {
        if (cc.mutable() == false) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * @effects if <code>c != null && </code> is annotated with {@see
   *          ClassConstraint} with <code>singleton = true</code> then return
   *          <code>true</code>, else return <code>false</code>
   */  
  public static boolean isSingleton(Class c) {
    if (c != null) {
      DClass cc = (DClass) c
          .getAnnotation(DClass.class);
      if (cc != null) {
        if (cc.singleton() == true) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }

  /**
   * A domain class is reflexive if there exists one domain-type attribute of this class
   * whose type is the same as the class. For example, <tt>Employee:id,supervisor</tt> is reflexive because
   * <tt>Employee.supervisor</tt> has data type <tt>Employee</tt> (stating the rule that 
   * an employee has one supervisor). 
   * 
   * @requires
   *  <tt>if fields != null
   *    fields are the domain attributes of <tt>c</tt>
   * @effects 
   *  if c has already been processed in terms of reflexivity
   *    return the value
   *  else
   *    process the domain attributes of c (or <tt>fields</tt> if specified) to determine its 
   *    reflexivity
   *    store the reflexivity value into this to use later   
   */  
  public boolean isReflexive(Class c, Map<Field,DAttr> fields) throws NotPossibleException {
    /* v3.2: redirect
    Boolean reflexive = reflexiveClasses.get(c);
    
    if (reflexive == null) {
      // determine reflexivity and store for use later
      reflexive = false;
      
      if (fields == null) {
        // read the fields of c if not specified
        fields = getDomainAttributes(c);
      }

      if (fields == null)
        throw new NotPossibleException(
            NotPossibleException.Code.CLASS_NOT_REGISTERED,
            "Lớp chưa được đăng ký {0}", c.getName());

      DomainConstraint dc;
      Type type;
      Class dataType;
      for (Field f : fields) {
        dc = f.getAnnotation(DC);
        type = dc.type();
        if (type.isDomainType()) {
          dataType = f.getType();
          if (dataType == c) {
            // reflexive
            reflexive = true;
            break;
          }
        }
      }
      
      reflexiveClasses.put(c,reflexive);
    }
    
    return reflexive;
    */
    return isReflexive(c, fields, false);
  }
  
  /**
   * A domain class is reflexive if there exists one domain-type attribute of this class
   * whose type is the same as or a super-type of the class. For example, <tt>Employee:id,supervisor</tt> is reflexive because
   * <tt>Employee.supervisor</tt> has data type <tt>Employee</tt> (stating the rule that 
   * an employee has one supervisor). 
   * 
   * @requires
   *  <tt>if fields != null
   *    fields are the domain attributes of <tt>c</tt>
   * @effects 
   *  if c has already been processed in terms of reflexivity
   *    return the value
   *  else
   *    process the domain attributes of c (or <tt>fields</tt> if specified) to determine its 
   *    reflexivity;
   *    <br>i.e. exists <tt>f in fields. f.type = c \/ 
   *      (inheritable=true /\ f.type = c.super (or c.ancester))</tt>
   *    <br>; store the reflexivity value into this to use later   
   * @version 
   * - 3.2: improved to support inheritable
   *  
   */  
  public boolean isReflexive(Class c, Map<Field,DAttr> fields, boolean inheritable) throws NotPossibleException {
    
    Boolean reflexive = reflexiveClasses.get(c);
    
    if (reflexive == null) {
      // determine reflexivity and store for use later
      reflexive = false;
      
      if (fields == null) {
        // read the fields of c if not specified
        fields = getDomainAttributes(c);
      }

      if (fields == null)
        throw new NotPossibleException(
            NotPossibleException.Code.CLASS_NOT_REGISTERED,
            new Object[] { c.getName()});

      DAttr dc;
      Type type;
      Class dataType;
      //for (Field f : fields) {
      for (Entry<Field,DAttr> entry : fields.entrySet()) {
        Field f = entry.getKey();
        //dc = f.getAnnotation(DC);
        dc = entry.getValue();
        type = dc.type();
        if (type.isDomainType()) {
          dataType = f.getType();
          if (dataType == c || (inheritable && isSpecialised(c, dataType))) {
            // reflexive
            reflexive = true;
            break;
          }
        }
      }
      
      reflexiveClasses.put(c,reflexive);
    }
    
    return reflexive;
  }
  
  /**
   * This method is better than {@link #isASubType(Class)} in that it supports hierarchy path.
   *  
   * @requires 
   *  c1, c2 are domain classes 
   *  
   * @effects 
   *  if c1 is a sub-type of c2 or c1 is a proper descendant of c2
   *    return true
   *  else
   *    return false 
   */
  public boolean isSpecialised(Class c1, Class c2) {
    Class sup = getSuperClass(c1, true);
    
    if (sup != null) {
      if (sup == c2)
        return true;
      else  // recursive: search up
        return isSpecialised(sup, c2);
    } else {
      // no super
      return false;
    }
  }

  /**
   * Note, use {@link #isSpecialised(Class, Class)} if needs to support inheritance up the hierarchy. 
   * 
   * @requires 
   *  c != null
   * @effects 
   *  return true if c is a sub-type of another domain class, 
   *  otherwise return false
   *  
   */
  public boolean isASubType(Class c) {
    return (getSuperClass(c) != null);
  }
  
  /**
   * @requires 
   *  c != null
   * @effects 
   *  if <tt>c</tt> is a language-aware domain class, i.e. specified with 
   *    <tt>{@link DClass#languageAware()}=true)</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean isLanguageAware(Class c) {
    if (c != null) {
      DClass cc = (DClass) c.getAnnotation(CC);
      if (cc != null) {
        if (cc.languageAware() == true) {
          return true;
        }
      }
      return false;
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if {@link #exclManyManyAssocFilter} is not initialised
   *    initialise it
   *  
   *  return {@link #exclManyManyAssocFilter}
   *  
   * @version v3.3
   */
  public static Filter<DAssoc> getExclManyManyAssocFilter() {
    if (exclManyManyAssocFilter == null) {
      exclManyManyAssocFilter = new Filter<DAssoc>() {
        @Override
        public boolean check(DAssoc o, Object... args) {
          if (o.ascType().equals(AssocType.Many2Many))
            return false; // exclude
          else
            return true;
        }
      };
    }
    
    return exclManyManyAssocFilter;
  }
  
  /**
   * Overrides the default method to release memory resources.
   */
  @Override
  public void finalize() throws Throwable {
    super.finalize();
    classDefs.clear();
    classSerialisableDefs.clear();
//    classExts.clear();
  //v5.0 classConstraints.clear();
    classReflexiveAttribs.clear();  // v3.2
    classDerivingAttribs.clear(); // v2.6.4b
    classAssocs.clear();
    classDependencies.clear();
    
    classDefs = null;
    classSerialisableDefs = null;
//    classExts = null;
  //v5.0 classConstraints = null;
    classAssocs = null;
    
    classDependencies = null;
    
    reflexiveClasses.clear();
    reflexiveClasses = null;
//    
//    if (dbt != null)
//      dbt.disconnect();
  }
}
