package jda.modules.dodm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dom.DOMFactory;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.dsm.DSMFactory;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.oql.QRM;

/**
 * @overview 
 *  Represents a domain-oriented data manager.
 *  
 * @author dmle
 * 
 * @version 
 * - 5.0: added support for {@link QRM}
 */
public class DODMBasic {
  private static final boolean debug = Toolkit.getDebug(DODMBasic.class);

  // v2.8
  private DODMConfig dodmConfig;
  
  // a data object manager
  private DOMBasic dom;
  
  // a domain schema manager
  private DSMBasic dsm;
  
  // the single instance of this class
  // v2.8: 
  protected static Map<DODMConfig,DODMBasic> instanceMap = new HashMap();
  
  /**
   * @effects 
   *  initialise this with an instance of DOM and DSM
   *  as specified in config  
   */
  public DODMBasic(Configuration config) throws NotPossibleException {
    //
    dodmConfig = config.getDodmConfig();
    
    dsm = DSMFactory.createDSM(dodmConfig);
    dom = DOMFactory.createDOM(dodmConfig, dsm);
    
    // v5.0
    //TODO: what about the case where there are multiple DODMBasic objects in #instanceMap
    QRM.createSingleInstance(this);
  }
  
  /**
   * @effects 
   *  if not exists instance of <tt>dodmCls</tt> whose config is <tt>config</tt>
   *    create it
   *  return the instance
   * @version 2.8
   */
  public static <T extends DODMBasic> T getInstance(Class<T> dodmCls, Configuration config) throws NotPossibleException {
    DODMConfig dodmCfg = config.getDodmConfig();
    DODMBasic instance = instanceMap.get(dodmCfg);
    
    if (instance == null) {
      try {
        // invoke the constructor to create object 
        instance = dodmCls.getConstructor(Configuration.class).newInstance(config);
        
        instanceMap.put(dodmCfg, instance);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
            new Object[] {dodmCls.getSimpleName(), config});
      }
    }
    return (T) instance;
  }
  
  /**
   * @effects 
   *  if not exists instance of <tt>config</tt>
   *    create it
   *  return the instance
   */
  public static DODMBasic getInstance(Configuration config) throws NotPossibleException {
    DODMConfig dodmCfg = config.getDodmConfig();
    /*v3.0: support pre-configured dodm type
    DODMBasic instance = instanceMap.get(dodmCfg);
    
    if (instance == null) {
      instance = new DODMBasic(config);
      instanceMap.put(dodmCfg, instance);
    }

    return instance;
    */
    Class<? extends DODMBasic> dodmType = dodmCfg.getDodmType();
    
    return getInstance(dodmType, config);
  }

//  /**
//   * @effects 
//   *  if <tt>dodm</tt> exists in the cache of this
//   *    remove it
//   *  else
//   *    do nothing
//   * @version 3.0
//   */
//  public static <T extends DODMBasic> void removeInstance(T dodm) {
//    Stack<DODMConfig> toRemove = new Stack();
//    for (Entry<DODMConfig,DODMBasic> e : instanceMap.entrySet()) {
//      if (e.getValue() == dodm) {
//        toRemove.push(e.getKey());
//      }
//    }
//    
//    if (!toRemove.isEmpty()) {
//      for (DODMConfig k : toRemove) instanceMap.remove(k);
//    }
//  }
  
//  /**
//   * @effects 
//   *  if this is configured with a data source
//   *    connect to it
//   *    <br>throws DataSourceException if failed
//   *  else
//   *    do nothing
//   * @version 3.0
//   * @throws DataSourceException 
//   */
//  public void connectToDataSource() throws DataSourceException {
//    if (isObjectSerialised()) {
//      dom.getOsm().connect();
//    }
//  }
  
  /**
   * @effects 
   *  return this.dom
   */
  public DOMBasic getDom() {
    return dom;
  }

  /**
   * @effects 
   *  return this.dsm
   */
  public DSMBasic getDsm() {
    return dsm;
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
    return dodmConfig.isObjectSerialisable();
  }

  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ c != null /\ c is a domain class (not an interface)</tt>
   * @effects 
   *  register <tt>c</tt> and all of its domain super- and ancestor classes (if any) 
   *  (if not yet done so).
   *    
   *  <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   * @version 2.7.3: also register referenced domain classes
   */
  public <T> void registerClassHierarchy(Class<T> c) throws NotPossibleException {
    // process hierarchy first
    List<Class> hier = dsm.getClassHierarchy(c);
    if (hier != null) {
      boolean toRegister = false;

      //TODO: v3.2c the two for loops below seem redundant -> improve
      for (Class h : hier) {
        if (!dsm.isRegistered(h) && dsm.isDomainClass(h)) {
          // found a domain class that has not been registered -> register
          // the hierarchy
          toRegister = true; break;
        }
      }
      
      if (toRegister) {
        for (Class h : hier) {
          registerClass(h);
        }
      }
    }
    
    // register c last
    registerClass(c);
  }
  
  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ classes != null </tt>
   * @effects 
   *  invoke {@link #registerClassHierarchy(Class)} for each class <tt>c</tt> in <tt>classes</tt>
   */  
  public void registerClasses(Class[] classes) throws NotPossibleException {
    // register classes and also all the referenced classes that are not listed therein
    // (these include all the enum classes and helper classes that are used as data types of the domain attributes)
    for (Class c : classes) {
      if (!dsm.isRegistered(c))
        registerClassHierarchy(c);
    }
  }

  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ c != null /\ c is a domain class (not an interface)</tt>
   * @effects 
   *  if <tt>c</tt> is not already registered
   *    registers <tt>c</tt> to <tt>this</tt> as a domain class.
   *    <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   *    
   *    <br>if <tt>c</tt> references other domain-oriented data types (through its the domain attributes) 
   *    register those (recursively) that have not yet been registered 
   * @version 
   *  - 3.0: support annotation (e.g. DomainConstraint) as a domain class 
   */
  public <T> void registerClass(Class<T> c) throws NotPossibleException {
    // register c to dsm
    
    if (debug)
      System.out.printf("DODM.registerClass: %s%n", c.getSimpleName());
    
    Collection<Class> refClasses = dsm.registerClass(c);
    if (refClasses != null) {
      HELPER: for (Class h : refClasses) {
        // only register h if it is not yet registered
        if (!dsm.isRegistered(h)) {
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

    // register c to dom
    dom.registerClass(c);
  }

  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ c != null </tt>
   * @effects 
   *  if <tt>c</tt> is not already registered
   *    registers <tt>c</tt> to <tt>this</tt> as an enumerated domain class.
   *    (the enum constants of <tt>c</tt> are also added to the object pool).
   *    
   *    <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   */
  public void registerEnumInterface(Class c) throws NotPossibleException {
    if (debug)
      System.out.printf("DODM.registerEnum: %s%n", c.getSimpleName());
    
    dsm.registerEnumInterface(c);
    dom.registerEnumInterface(c);
  }

  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ c != null </tt>
   * @effects
   *  if <tt>c</tt> is not already registered
   *    registers <tt>c</tt> to <tt>this</tt> as a domain class.
   *    (the object pool of <tt>c</tt> is initialised to be empty)
   *    
   *    <p>Throws <tt>NotPossibleException</tt> if <tt>c</tt> is not a proper
   *          domain class
   * @version 3.0
   * @deprecated NOT YET TESTED   
   */
  public void registerAnnotation(Class c) throws NotPossibleException {
    if (debug)
      System.out.printf("DODM.registerAnnotation: %s%n", c.getSimpleName());
    
    dsm.registerAnnotation(c);
    dom.registerAnnotation(c);
  }
  
  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ c != null </tt>
   * @effects 
   *  if c is registered in this.dsm
   *    return true
   *  else
   *    return false
   */
  public boolean isRegistered(Class c) {
    return dsm.isRegistered(c);
  }
  
  /**
   * @requires 
   *  <tt>dsm != null /\ dom != null /\ classes != null </tt>
   * @effects 
   *  equivalent to calling {@link #addClass(Class, boolean, boolean)} for each class <tt>c</tt> in <tt>classes</tt>
   */
  public void addClasses(Class[] classes, boolean createIfNotExist, boolean read) throws NotPossibleException, NotFoundException, DataSourceException {
    registerClasses(classes);
    
    for (Class c : classes)
      dom.addClass(c, createIfNotExist, read);
  }
  
  /**
   * @effects if <code>c</code> is registered in <code>this</code> and
   *          <code>isTransient(c) = false</code> if a data source element of
   *          <code>c</code> does not already exists then creates it.
   *          
   *          <p>if <tt>read = true</tt> reads the records from the data source 
   *          into <code>c.extents</code> (the object pool of <tt>c</tt>)
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
   *          <code>DataSourceException</code> if could not operate on
   *          the data source element of <code>c</code>.
   * @requires <code>c != null</code> and is a registered domain class
   */
  public void addClass(Class c, boolean createIfNotExist, boolean read) throws NotPossibleException, NotFoundException, DataSourceException {
    //dsm.registerClass(c);
    registerClass(c);
    dom.addClass(c, createIfNotExist, read);
  }

  /**
   * This method is a short-cut for {@link #addClass(Class, boolean, boolean)}, using the arguments: <tt>(c, true, true)</tt>.
   */
  public void addClass(Class c) throws NotPossibleException, NotFoundException, DataSourceException {
    dsm.registerClass(c);
    dom.addClass(c);
  }

  /**
   * This method is preferred to {@link #addClass(Class, boolean, boolean)} for setting up a set of related domain classes.
   * 
   * @requires 
   *  {@link #registerClasses(Class[]) have been invoked on <tt>classes</tt> 
   *  
   * @effects 
   *  add to this each domain class in <tt>classes</tt> together with the domain classes that they refer to (as specified by {@link #addClass(Class, boolean, boolean)})
   * 
   * @version 2.7.4: added support for referenced classes
   */
  public void addClasses(Class[] classes, boolean read) throws NotPossibleException, NotFoundException, DataSourceException {
    /* v2.7.4: remove this: force code to invoke first using DODM.registerClasses
    dsm.registerClasses(classes);
    dom.addClasses(classes, read);
    */
    // get all classes that need to be added (including the referenced)
    
    Collection<Class> allClasses = new ArrayList<>(classes.length);
    Collections.addAll(allClasses, classes);
    
    for (Class c : classes) {
      addDependencies(c, allClasses);
    }
    
    dom.addClasses(allClasses, read);
  }
  
  /**
   * @effects 
   *  recursively add to <tt>allClasses</tt> the <tt>c</tt>'s dependencies and the dependencies of these (and so on) that are NOT enum-typed  
   */
  private void addDependencies(Class c, Collection<Class> allClasses) {
    Collection<Class> dependencies = dsm.getClassDependencies(c);
    if (dependencies != null) {
      // process dependents that are not enum types and not yet processed: 
      //  recursively add their dependents 
      for (Class d : dependencies) {
        if (!d.isEnum() && !allClasses.contains(d)) {
          allClasses.add(d);
          addDependencies(d, allClasses);
        }
      }
    }
  }

  /**
   * @requires 
   *  dom != null /\ c is registered domain class 
   * @effects <pre>
   *  if exist pre-defined <tt>public static</tt> constant objects of type <tt>c</tt>
   *  that are defined in <tt>c</tt>
   *    add those objects to this
   *  else
   *    do nothing</pre>
   * @version 2.7.3
   */
  public <T> void addConstantObjects(Class<T> c) throws NotPossibleException, DataSourceException {
    List<T> objs = Toolkit.getConstantObjects(c, c);
    if (objs != null) {
      for (T o : objs) {
        dom.addObject(o);
      }
    }
  }
  
  /**
   * @requires classes != null
   * 
   * @effects 
   *  delete from the underlying data source the class stores of the domain classes (together with their dependencies, recursively) 
   *  in <tt>classes</tt>.
   *  
   *  <p>Throws DataSourceException if failed.  
   */
  public void deleteClasses(List<Class> classes) throws DataSourceException {
    // add all dependencies that have not been included in classes
    List<Class> allClasses = new ArrayList<>(classes);
    for (Class c : classes) {
      addDependencies(c, allClasses);
    }

    if (debug) 
      System.out.println("Deleting class store constraints");

    boolean objectSerialised = isObjectSerialised();

    if (objectSerialised) // v2.8: added this check
      deleteDataSourceConstraints(allClasses);
    
    if (debug) System.out.println("Deleting class stores");

    // v3.0
    boolean strict = false;
    
    dom.deleteClasses(allClasses, 
        objectSerialised,
        strict
        );
    
    // v2.8: delete class definitions 
    dsm.deleteClasses(allClasses);
  }
  
  /**
   * @requires 
   *  {@link #isObjectSerialised()} = true
   *  
   * @effects 
   *  delete from the underlying data sources the class store constraints of  the domain classes in <tt>classes</tt> 
   */
  private void deleteDataSourceConstraints(List<Class> classes) throws DataSourceException {
    
    List<String> consNames;
    for (Class c : classes) {
      consNames = dom.loadDataSourceConstraints(c);
      if (consNames != null) {
        for (String cons : consNames) {
          if (debug) System.out.println("   constraint " + cons);
          dom.deleteDataSourceConstraint(c, cons);
        }
      }
    }
  }
  
  /**
   * @effects 
   *  clear all resources used by this
   */
  public void close() {
    DSMFactory.removeInstance(dsm); // v3.0
    dsm = null;

    DOMFactory.removeInstance(dom); // v3.0
    //TODO: to remove the associated Osm instance of it is cached
    dom = null;
    
    // v2.8: instance=null;
    // remove this from cache
    instanceMap.remove(dodmConfig);
    
    // v3.0
    dodmConfig = null;
  }
}
