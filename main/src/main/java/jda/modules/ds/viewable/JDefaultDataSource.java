package jda.modules.ds.viewable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;

/**
 * @overview 
 *  An <tt>abstract</tt> sub-type of {@see JDataSource} that specifies the default behaviour needed 
 *  for the bindable data fields. 
 *  
 *  <p>It is bound to a {@see DataController} and 
 *  further does not require all objects to be loaded up-front. Instead
 *  these objects are loaded on-demand and are cached for later use.
 *  
 *  <p>When the domain objects are modified by the application (e.g. when the bounded attribute values 
 *  are changed or when new objects are created), these changes are reflected in the data source. 
 *  
 *  <p>This data source supports an additional configurable option of loading the {@link Oid} of the bounded
 *  object that provides each value of the bound attribute. The {@link Oid} is paired with its corresponding bounded value
 *  so that both can be displayed to the user on the data field.  
 *  
 *  <p>This data source is used as a helper for a {@see DataController} to 
 *  provide bindable data fields access to the domain objects.
 *  
 *  <p>Sub-types must implement the abstract methods.
 *  
 * @version 3.1
 * 
 * @author dmle
 */
public abstract class JDefaultDataSource extends JDataSource {
  
  /**
   *  a cache used for caching objects loaded from data source
   *  it maps: <br><tt>(attribute,attribVal) -> object </tt>
   *  <br>where: <ul> 
   *      <li><tt>attribute</tt>: a bound attribute of the domain class specified in this,
   *      <li><tt>attribVal</tt>: a value of that attribute, 
   *      <li><tt>object</tt>: the corresponding domain object
   *  </ul>  
   */
  /*v2.6.4b: changed first-level key to Object so that 
   * it can support two types of keys used: Tuple2 or DisplayValueTuple
   private Map<Tuple2<DomainConstraint,Object>,  // (boundAttribute,boundValue)
                  Object                        // Object
                  > objCache;
   */ 
  private Map<Object,Object> objCache;  // key: either a Tuple2(boundAttribute,boundValue) or DisplayValueTuple

  // the data controller
  //private DataController dctl;

  /*v2.7.2: moved up to JDataSource
  // derived attribute
  private Boolean isEmpty; 
  */
  
  private final static boolean debug = Toolkit.getDebug(JDefaultDataSource.class);
  
  public JDefaultDataSource(ControllerBasic mainCtl, DODMBasic dodm, Class domainClass) {
    super(mainCtl, dodm, domainClass);
    objCache = new LinkedHashMap();
  }

  
  // for sub-types to implement
  @Override
  public abstract Iterator getBoundedValues(DAttr boundAttribute) throws NotPossibleException;
  
  // for sub-types to implement
  @Override
  public abstract Object reverseLookUp(DAttr boundAttribute,
      Object boundedVal) throws NotPossibleException, NotFoundException;
  
//  /* v2.6.4.b: support display of Oid with bound values
//   * v2.7.2: support objectgroup-based permission on the bounded objects 
//   */
//  @Override
//  public Iterator getBoundedValues(DomainConstraint boundAttribute) throws NotPossibleException {
//    if (!isBounded())
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
//          "Không thể thực hiện phương thức {0}.{1}({2})",JDataSource.class,"getBoundValues",boundAttribute.name());
//
//    // load bounded values directly from the data source
//    final DODMBasic dodm = getDodm();
//    DOMBasic dom = dodm.getDom();
//
//    final Class cls = getDomainClass();
//    
//    boolean loadOidFor = isLoadOidFor(boundAttribute);
//    boolean displayOid = getDisplayOidWithBoundValue(boundAttribute);
//    
//    Collection boundValues;
//    
//    /*v2.8: added support for memory-based configuration */
//    boolean objectSerialised = dodm.isObjectSerialised();
//    
//    if (objectSerialised) {
//      if (loadOidFor) {
//        // load bound values with associated Oids
//        Map<Oid,Object> idVals = dom.loadAttributeValuesWithOids(cls, boundAttribute);
//        
//        /* v2.7.2: object-group permission is only checked if the loadOid option is on
//         * If exists object group permission then filter the results based on the Oids.
//         */
//        ControllerBasic mainCtl = getMainController();
//        boolean hasObjectGroupPermission = mainCtl.hasObjectGroupPermission(cls);
//        
//        if (hasObjectGroupPermission) {
//          idVals = filterByObjectPermission(idVals);
//        }
//        
//        // v2.6.4.b: support display value tuple
//        //boundValues = idVals.values();
//        if (idVals == null) {
//          boundValues = null;
//        } else {
//          if (displayOid) { 
//            // display Oid with the values
//            DisplayValueTuple vt;
//            Oid id;
//            Object val;
//            boundValues = new ArrayList();
//            for (Entry<Oid,Object> idVal : idVals.entrySet()) {
//              // use display value tuple (id,...,val)
//              vt = new DisplayValueTuple();
//              id = idVal.getKey();
//              val = idVal.getValue();
//              for (int i = 0; i < id.size(); i++) {
//                vt.addElement(id.getIdAttribute(i), id.getIdValue(i));
//              }
//              
//              // add bound value last
//              vt.addElement(boundAttribute, val);
//  
//              // add to result
//              boundValues.add(vt);
//              
//              // v2.6.4.b: add to cache
//              addOidToCache(boundAttribute, vt, idVal.getKey());
//            }
//          } else {
//            // not display Oid, just use the values
//            boundValues = idVals.values();
//            
//            // put val-ids to cache
//            for (Entry<Oid,Object> idVal : idVals.entrySet()) {
//              addOidToCache(boundAttribute, idVal.getValue(), idVal.getKey());
//            }
//          }
//        }
//      } else {
//        // just load the bound values
//        boundValues = dom.loadAttributeValues(cls, boundAttribute);
//      }
//    } else {
//      // object not serialised: just get the attribute values from pool
//      boundValues = dom.getAttributeValues(cls, boundAttribute);
//    }
//    
//    return boundValues != null ? boundValues.iterator() : null;
//  }

  // v3.1: moved to Security
//  /**
//   * @effects 
//   *  return a sub-set of <tt>idVals</tt> filtering out those for which the 
//   *  current user does not have permissions.
//   */
//  protected Map<Oid,Object> filterByObjectPermission(Map<Oid,Object> idVals) {
//    Class cls = getDomainClass();
//    ControllerBasic mainCtl = getMainController();
//    
//    Map<Oid,Object> filteredIdVals = null;
//    try {
//      filteredIdVals = idVals.getClass().newInstance();
//    } catch (Exception ex) {
//      // should not happen
//      return idVals;
//    }
//    
//    Oid id;
//    for (Entry<Oid,Object> e : idVals.entrySet()) {
//      id = e.getKey();
//      if (mainCtl.getObjectPermission(cls, id)) {
//        // allowed
//        filteredIdVals.put(id, e.getValue());
//      }
//    }
//    
//    return filteredIdVals;  
//  }
  
//  @Override
//  public Object reverseLookUp(DomainConstraint boundAttribute,
//      Object boundedVal) throws NotPossibleException, NotFoundException {
//    if (debug) System.out.printf("JAdaptiveDataSource.reverseLookUp(%s,%s)%n", boundAttribute.name(), boundedVal);
//
//    DODMBasic dodm = getDodm();
//    DOMBasic dom = dodm.getDom();
//    boolean objectSerialised = dodm.isObjectSerialised();
//    
//    Class cls = getDomainClass();
//    
//    if (!isBounded())
//      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_NOT_BOUNDED, 
//          "Không thể thực hiện phương thức {0}.{1}({2}): Không có liên kết tới nguồn dữ liệu",
//          JDataSource.class,"reverseLookUp",cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal);
//
//    if (isEmpty()) {
//      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_IS_EMPTY, 
//          "Không thể thực hiện phương thức {0}.{1}({2}): Không có dữ liệu nào từ nguồn",
//          JDataSource.class,"reverseLookUp",cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal);      
//    }
//
//    boolean displayOid = getDisplayOidWithBoundValue(boundAttribute);
//    /*v2.6.4b
//    // if displayOid is true then extract the actual bounded value
//    Object boundedVal;
//    if (displayOid) {
//      DisplayValueTuple vt = (DisplayValueTuple) boundedValue;
//      boundedVal = vt.getLastElement();
//    } else {
//      boundedVal = boundedValue;
//    }
//   */
//    
//    // return object from cache if found
//
//    /*v2.6.4b: supports a new cache key scheme
//    Tuple2<DomainConstraint,Object> t = Tuple2.newTuple2(boundAttribute, boundedVal);
//     */
//    Object t; // cache key
//    if (displayOid) {
//      t = boundedVal;
//    } else {
//      t = Tuple2.newTuple2(boundAttribute, boundedVal);
//    }
//    
//    Object o = objCache.get(t);
//    if (o == null) {
//      if (debug) System.out.printf("  not in object cache (key: %s) %n", t);
//      
//      /*
//       *  if Oid for boundVal is available for this attribute then use it to load object
//       *  otherwise load object directly using boundVal
//       */
//      //TODO: this cannot support duplicate boundedVal
//      /*
//       * v2.6.4.b: add support for the new cache key scheme
//      Oid oid = getCachedOid(boundAttribute, boundedVal);
//       */
//      Object oidKey;
//      Oid oid;
//      if (displayOid) // use the same object cache key (which is the display value tuple) to look up Oid 
//        oidKey = t;
//      else  // use the bounded value to look up
//        oidKey = boundedVal;
//      
//      oid = getCachedOid(boundAttribute, oidKey);
//      
//      if (oid != null) {
//        if (debug) System.out.printf("  in Oid cache (key: %s): %s%n", oidKey, oid);
//
//        // use this oid to load object
//        o = dom.getObject(cls, oid);
//        if (o == null
//            && objectSerialised // v2.8: added this check
//            ) {
//          try {
//            o = dom.loadObject(cls, oid);
//          } catch (DataSourceException e) {
//            // not found
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
//                "Lỗi thao tác trên nguồn dữ liệu đối với {0}",
//                cls.getSimpleName()+"."+oid+": "+boundAttribute.name()+"="+boundedVal);
//            
//          }          
//        }
//        
//        // v2.8: added
//        if (o == null) {
//          throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, cls, oid);
//        }
//      } else {
//        if (debug) System.out.printf("  not in Oid cache (key: %s)%n", oidKey);
//
//        // load the object matching the bounded value directly from the data source
//        /*
//         * v2.6.4b: support display value tuple
//         */
//        if (displayOid) {
//          // Oid is actually already loaded in the object pool, but not yet added to the oid cache
//          // -> extract the Oid value and uses it to look up in the object pool
//          DisplayValueTuple vt = (DisplayValueTuple) boundedVal;
//          int numElements = vt.size();
//          DomainConstraint[] idAttribs = new DomainConstraint[numElements-1];
//          Object[] idVals = new Object[numElements-1];
//          for (int i = 0; i < numElements-1; i++) {
//            idAttribs[i] = vt.getDomainConstraint(i);
//            idVals[i] = vt.getElement(i);
//          }
//          
//          Entry<Oid,Object> to = dom.lookUpObject(cls, idAttribs, idVals);
//          if (to == null) {
//            // should not happen
//            throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
//                "Không tìm thấy đối tượng {0}<{1}>", new Object[] {cls.getSimpleName(), Arrays.toString(idVals)});
//          }
//          
//          oid = to.getKey();
//          o = to.getValue();
//        } else {
//          // Oid is not yet loaded in the pool load it (with the object from the data source)          
//          Query query = new Query();
//          Expression exp = new ObjectExpression(cls, boundAttribute, Expression.Op.EQ, boundedVal);
//          query.add(exp);
//
//          Map<Oid,Object> objs = null;
//          try {
//            objs = dom.retrieveObjects(cls, query);
//          } catch (DataSourceException e) {
//            // not found
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
//                "Lỗi thao tác trên nguồn dữ liệu đối với {0}",
//                cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal);
//            
//          }
//          
//          if (objs == null) {
//            // not found
//            throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
//                "Không tìm thấy đối tượng {0}<{1}>", new Object[] {cls.getSimpleName(), boundAttribute.name() +"="+boundedVal});
//          }
//          
//          if (objs.size() > 1)
//            throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
//                "Không hỗ trợ tính năng {0}: {1}", new Object[] {JDataSource.class + ".reverseLookUp:" + cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal, 
//                " giá trị không duy nhất"});
//          
//          //o = objs.values().iterator().next();          
//          Entry<Oid,Object> e = objs.entrySet().iterator().next();
//          oid = e.getKey();
//          o = e.getValue();
//          
//          // debug
////          if (debug)
////            System.out.println("  JAdaptiveDataSource: Loaded object " + o);
//        }
//        
//        // update the oid cache
//        addOidToCache(boundAttribute, boundedVal, oid);
//      }
//      
//      // cache object
//      objCache.put(t, o);
//    } else {
//      if (debug) System.out.printf("  in object cache (key: %s): %s%n", t, o);
//    }
//    
//    return o;
//  }
  
  @Override
  public void stateChanged(ChangeEvent ce) {
    // if necessary handle the event here: update the object cache
    updateObjectCache(ce);
    
    resetOnStateChanged(ce);
    
    // must do this to process further
    super.stateChanged(ce);
  }
  
  /**
   * @effects 
   * if object cache is initialised
   *   update the object cache based on the action performed on the 
   *   domain objects contained in the ChangeEvent ce 
   */
  private void updateObjectCache(ChangeEvent ce) {
    if (objCache == null || objCache.isEmpty())
      return;
    
    ChangeEventSource ds = (ChangeEventSource) ce.getSource();

    // interested in one event: delete
    if (ds.isDelete()) {
      // if objects are in cache, remove them
      List objects = ds.getObjects();
      if (objects != null) { 
        /* v2.6.4b
        Stack<Entry<Tuple2<DomainConstraint,Object>,Object>> removedStack = new Stack();
         */
        Stack<Entry<Object,Object>> removedStack = new Stack();
        
        Object c;
        for (Object o : objects) {
          // remove o from all the entries that contain it
          /*v2.6.4b:
          //for (Entry<Tuple2<DomainConstraint,Object>,Object> entry : objCache.entrySet()) {
           */
          for (Entry<Object,Object> entry : objCache.entrySet()) {
            c = entry.getValue();
            if (c == o) {
              // to remove this entry
              removedStack.push(entry);
            }
          }
          
          // remove entries if found
          if (!removedStack.isEmpty()) {
            /*v2.6.4b:
            for (Entry<Tuple2<DomainConstraint,Object>,Object> entry : removedStack)
            */
            for (Entry<Object,Object> entry : removedStack)
              objCache.remove(entry.getKey());
          }
          
          removedStack.clear();
        }
      }
    } else if (ds.isUpdate()) {
      // there is no need to do anything here because object is stored in the object pool 
    }
  }
  
  /**
   * @effects 
   *  reset this on {@link #stateChanged(ChangeEvent)}
   */
  private void resetOnStateChanged(ChangeEvent ce) {
    if (isEmpty != null) {
      ChangeEventSource ds = (ChangeEventSource) ce.getSource();
      if (ds.isAddNew() && isEmpty == true) {
        isEmpty = false;
      } else if (ds.isDelete()) { // isEmpty = false
        //v2.6.4b: reset isEmpty to null to force reload
        isEmpty = null;
      }
    }
  }
  
  /**
   * @effects 
   *  return {@link objCache}
   */
  protected Map<Object,Object> getObjectCache() {
    return objCache;
  }
  
  @Override
  public void clearBuffer() {
    super.clearBuffer();
    // clear cache
    objCache.clear();
    // v2.7.2: isEmpty = null;
  }
}
