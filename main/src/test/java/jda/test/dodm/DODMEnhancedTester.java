package jda.test.dodm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of {@link DODMBasicTester} that supports a number of additional
 *  DODM-related operations, especially those concerning the use of {@link Oid} and 
 *  the manipulation of the object pool (e.g. id-range, getting first or last object, etc.)
 *  using this id.
 *  
 * @author dmle
 *
 */
public class DODMEnhancedTester 
//extends TestDBBasic 
extends DODMBasicTester 
{
  public DODMEnhancedTester() throws NotPossibleException {
    super();
  }

  @Override
  protected void initClasses() {
//    // the addition-order of domain classes must carefully observed to honour
//    // their dependencies!
//    domainClasses = new Class[] { //
//        City.class,
//        SClass.class, // 
//        Student.class, //
//    };
  }
  
  @Override
  protected void defaultInitData() {
    // TODO Auto-generated method stub
    
  }
  
  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
//  public void initData() throws DataSourceException {
//    // only invoked once for all test cases
//    method("initData()");
//
//    initCities();
//  }

  /**
   * @requires
   * {@link #addClass(cls)}
   */
  public void retrieveMetadata(Class cls) throws NotPossibleException, DataSourceException {
    instance.getDom().retrieveMetadata(cls);
  }
  
  /**
   * @requires
   * {@link #addClasses()}
   */
  public Tuple2<Oid,Oid> getOidRange(Class cls) throws DataSourceException, NotFoundException {
    method("getOidRange()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Oid min = dom.getLowestOid(cls);
    Oid max = dom.getHighestOid(cls);
    
    return new Tuple2<Oid,Oid>(min,max);
  }
  
  /**
   * @throws DataSourceException 
   * @throws NotFoundException 
   * @requires
   * {@link #registerClass(Class)} (<- cls)
   * {@link #loadObjects(Class)} (<- cls)
   */
  public <T> Tuple2<Oid,T> getFirstObject(Class<T> cls) throws NotFoundException, DataSourceException {
    method("getFirstObject()");
    
    /*v3.0: use object iterator instead!!! 
    Oid min = instance.getDODM().getDom().getLowestOid(cls);
    
    T o = retrieveObject(cls, min);
    
    //System.out.printf("First object of %s: %s%n", cls, o);
    
    return new Tuple2<Oid,T>(min,o);
    */
    
    Oid min = instance.getDODM().getDom().getLowestOid(cls);

    DOMBasic dom = instance.getDom();
    Iterator<Entry<Oid,Object>> it = dom.getObjectIterator(cls);
    
    if (it != null) {
      while (it.hasNext()) {
        Entry<Oid,Object> e = it.next();
        if (e.getKey().equals(min)) {
          // found min tuple
          Tuple2<Oid,T> tuple = new Tuple2(e.getKey(), (T) e.getValue());
          return tuple;
        }
      }
      
      // should not get here
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {cls.getSimpleName(), min});
      
    } else {
      return null;
    }
  }

  /**
   * @throws DataSourceException 
   * @throws NotFoundException 
   * @requires
   * {@link #registerClass(Class)} (<- cls)
   * {@link #loadObjects(Class)} (<- cls)
   * 
   */
  public <T> Tuple2<Oid,T> getLastObject(Class<T> cls) throws NotFoundException, DataSourceException {
    method("getLastObject()");

    /*v3.0: use object iterator instead!!! 

    Oid max = instance.getDODM().getDom().getHighestOid(cls);
    
    T o = retrieveObject(cls, max);
    
    //System.out.printf("Last object of %s: %s%n", cls, o);
    
    return new Tuple2<Oid,T>(max,o);
    */
    DOMBasic dom = instance.getDom();
    
    Oid max = dom.getHighestOid(cls);

    Iterator<Entry<Oid,Object>> it = dom.getObjectIterator(cls);
    
    if (it != null) {
      while (it.hasNext()) {
        Entry<Oid,Object> e = it.next();
        if (e.getKey().equals(max)) {
          // found min tuple
          Tuple2<Oid,T> tuple = new Tuple2(e.getKey(), (T) e.getValue());
          return tuple;
        }
      }
      
      // should not get here
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {cls.getSimpleName(), max});
      
    } else {
      return null;
    }
  }
  
  /**
   * @requires
   * {@link #addClasses()}
   *  Object whose id immediately precedes currId has not been loaded
   */
  public Oid getIdFirstBefore(Class cls, Oid currId) throws DataSourceException, NotPossibleException {
    method("getIdFirstBefore()");
    
    DOMBasic dom = instance.getDom();
    return dom.retrieveIdFirstBefore(cls, currId);
  }
  
  /**
   * @requires
   * {@link #addClasses()}
   *  Object whose id immediately precedes currId has not been loaded
   */
  public Object getObjectFirstBefore(Class cls, Oid currId) throws DataSourceException, NotPossibleException {
    method("getObjectFirstBefore()");
    
    DOMBasic dom = instance.getDom();
    Object o = dom.retrieveObjectFirstBefore(cls, currId);
    
    //System.out.printf("Object of %s immediately precedes %s: %s%n", cls, currId, o);
    return o;
  }

  /**
   * @requires
   * {@link #addClasses()}
   *  Object whose id immediately proceeds currId has not been loaded
   */
  public Oid getIdFirstAfter(Class cls, Oid currId) throws DataSourceException, NotPossibleException {
    method("getIdFirstAfter()");
    
    DOMBasic dom = instance.getDom();
    return dom.retrieveIdFirstAfter(cls, currId);
  }
  
  /**
   * @requires
   * {@link #addClasses()}
   *  Object whose id immediately proceeds currId has not been loaded
   */
  public Object getObjectFirstAfter(Class cls, Oid currId) throws DataSourceException, NotPossibleException{
    method("getObjectFirstAfter()");
    
    DOMBasic dom = instance.getDom();
    Object o = dom.retrieveObjectFirstAfter(cls, currId);
    
    return o;
    //System.out.printf("Object of %s immediately proceeds %s: %s%n", cls, currId, o);
  }

  /**
   * @requires
   *  {@link #registerClass(Class)} (<- <tt>cls</tt>) /\
   *  id is a valid Oid of cls
   */
  public <T> T retrieveObject(Class<T> cls, Oid id) throws DataSourceException, NotFoundException {
    DODMBasic dodm = instance.getDODM(); 
    
    T o = (T) dodm.getDom().getObject(cls, id);
    
    if (o == null)
      o = (T) dodm.getDom().loadObject(cls, id);
    
    return o;
  }

  public Map<Oid,Object> loadObjectsWithOid(Class cls) throws NotPossibleException, DataSourceException {
    return loadObjectsWithOid(cls, null);
  }
  
  public Map<Oid,Object> loadObjectsWithOid(Class cls, Query query) throws NotPossibleException, DataSourceException {
    System.out.printf("loadObjectsWithOid(%s,%n  %s)%n", cls.getSimpleName(),query);
    
    DODMBasic schema = instance.getDODM();
    
    return schema.getDom().retrieveObjects(cls, query);
  }
  
  /**
   * @effects 
   *  returns a collection containing <tt>numObjs</tt> randomly-chosen objects of c
   *  or null if c's extent is empty
   *  
   * @requires  
   *  c's objects have been loaded
   */
  public <T> Collection<Tuple2<Oid, T>> getRandomObjects(Class<T> c, int numObjs) throws IllegalArgumentException, DataSourceException, IllegalArgumentException {
    // get the random objects
    DODMBasic schema = instance.getDODM();
    
    int countObjs = schema.getDom().getObjectCount(c);
    Iterator<Entry<Oid,Object>> objects;
    
    if (countObjs == 0)
      throw new IllegalArgumentException("Invalid number of objects (expecting <= " + countObjs+ "): " + numObjs);

    int rand;
    Collection<Tuple2<Oid,T>> randObjects = new ArrayList(numObjs);
    
    Tuple2<Oid,T> tuple;
    for (int i = 1; i <= Math.min(numObjs,countObjs); i++) {
      objects = schema.getDom().getObjectIterator(c);
      
      // get a random index
      do {
        rand = (int) (Math.random()*countObjs);
        tuple = (Tuple2<Oid,T>) getObject(objects, rand);
      } while (randObjects.contains(tuple));
      
      randObjects.add(tuple);
    }
    
    return randObjects;
  }
  
  public void deleteObject(Class c, Object o, Oid oid) throws DataSourceException {
    schema.getDom().deleteObject(o, oid, c);
  }

  public Tuple2<Oid, Object> deleteRandom(Class c) throws DataSourceException {
    // get a random object
    int countObjs = schema.getDom().getObjectCount(c);
    Iterator<Entry<Oid,Object>> objects = schema.getDom().getObjectIterator(c);
    
    int rand = (int) (Math.random()*countObjs);
    Tuple2<Oid,Object> tuple = getObject(objects, rand);
    
    // delete the object
    Oid oid = tuple.getFirst();
    Object o = tuple.getSecond();
    deleteObject(c, o, oid);
    
    return tuple;
  }
  
  public <T> Tuple2<Oid,T> getObject(Iterator<Entry<Oid,T>> mapIt, int index) {
    int i = 0;
    while (i < index) {
      mapIt.next();
      i++;
    }
  
    Entry<Oid,T> entry = mapIt.next();
    
    Tuple2<Oid,T> tuple = new Tuple2<Oid,T>(entry.getKey(), entry.getValue());
    return tuple;
  }

  /**
   * @effects 
   *  return a <tt>Tuple2</tt> of a randomly choosen object of <tt>c</tt> in its object pool
   *  or return <tt>null</tt> if the pool is empty  
   */
  public <T> Tuple2<Oid, T> getRandomObject(Class<T> c) {
    DODMBasic schema = instance.getDODM();
    
    int countObjs = schema.getDom().getObjectCount(c);
    
    if (countObjs == 0) // pool is empty
      return null;

    Iterator<Entry<Oid,Object>> objects = schema.getDom().getObjectIterator(c);
    
    Tuple2<Oid,T> tuple;
    // get a random index
    int rand = (int) (Math.random()*countObjs);
    tuple = (Tuple2<Oid,T>) getObject(objects, rand);
    
    return tuple;
  }

  /**
   * @effects 
   *  return the object of <tt>c</tt> whose id is <tt>id</tt> or <tt>null</tt> if no such object is found
   */
  public <T> T getObject(Class<T> c, Oid id) {
    DODMBasic dodm = instance.getDODM(); 
    DOMBasic dom = dodm.getDom();
    
    return dom.getObject(c, id);
  }

  /**
   * @effects 
   *  if exists in object pool of <tt>c</tt> an entry <tt>(id,o)</tt>
   *    return <tt>id</tt>
   *  else
   *    return <tt>null</tt>
   *  @version 3.0
   */
  public <T> Oid getObjectId(Class<T> c, T o) {
    DODMBasic dodm = instance.getDODM(); 
    DOMBasic dom = dodm.getDom();
    
    return dom.lookUpObjectId(c, o);
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
   * @throws NotFoundException if an associated object is not found  
   * @throws NotPossibleException if failed to create object from the data source record
   * @throws DataSourceException if failed to read record from the data source
   * 
   * @version 3.0
   */
  public <T> T reloadObjectWithOid(Class<T> c, Oid id) throws NotPossibleException, NotFoundException, DataSourceException {
    DODMBasic dodm = instance.getDODM(); 
    DOMBasic dom = dodm.getDom();
    
    T o = dom.reloadObject(c, id);
    
    return o;
  }
  
}
