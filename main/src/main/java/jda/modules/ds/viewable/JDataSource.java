package jda.modules.ds.viewable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.datafields.JBoundedComponent;
import jda.mosa.view.assets.datafields.model.DisplayValueTuple;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ChangeListener;
import jda.util.events.ObjectUpdateData;

/**
 * @overview 
 *  Represents a data source that serves data values for bindable data fields.
 *  
 *  <p>A bounded data source is that that is created with a DomainSchema. Otherwise, it is unbounded.
 *  Operation <tt>isBounded()</tt> should be used to check whether a data source is bounded. 
 *  
 *  <p>It is a sub-type of ChangeListener so that it can listen for ChangeEvents concerning the data objects.
 *  
 *  <p>Sub-types must implement two methods <tt>isEmpty()</tt> and <tt>iterator()</tt> to enable access to 
 *  the underlying data. 
 *  
 * @author dmle
 */
public abstract class JDataSource implements ChangeListener {
  private List<JBoundedComponent> boundedComps;
  
  private ControllerBasic mainCtl; 
  
  private DODMBasic dodm;
  
  private Class domainClass;

  /**
   * maps an attribute of {@link #domainClass} to another <tt>Map</tt>, 
   * the entries of which map a value of the attribute to the Oid of 
   * the object containing that value.
   */
  private Map<DAttr, // bound attribute
                Map<Object,     // a bound attribute's value
                    Oid         // associated Oid
                    >> oidCache;
  
  /** maps an attribute of {@link #domainClass} to a <tt>boolean</tt> 
   *  which indicates whether the values of that attribute should be displayed 
   *  with the Oid of the objects.
   * */
  private Map<DAttr,Boolean> displayOidWithBoundValueMap;
  
  // derived (to speed up the use of id attributes)
  private List<DAttr> idAttribs;

  // derived attribute
  protected Boolean isEmpty; 
  
  /**
   * @version 2.7.2
   */
  public JDataSource(ControllerBasic mainCtl, DODMBasic dodm, Class domainCls) {
    this.mainCtl = mainCtl;
    this.dodm = dodm;
    this.domainClass = domainCls;
    boundedComps = new ArrayList<JBoundedComponent>();
    
    //displayOidWithBoundValueMap = new HashMap<DomainConstraint,Boolean>();
    
    if (domainClass != null)
      idAttribs = dodm.getDsm().getIDDomainConstraints(domainClass);
  }

  public JDataSource(DODMBasic dodm, Class domainCls) {
    this(null, dodm, domainCls);
  }

  public JDataSource() {
    this(null, null, null);
  }


//  /**
//   * @effects 
//   *  Connect to the data source represented by this and load the data values (if available)
//   *  
//   *  <br>Throws NotPossibleException if fails.
//   */
//  public void connect() throws NotPossibleException {
//    // default (for unbounded): do nothing
//    // Sub-types must override this if a real data source is used
//  }
//  
//  /**
//   * @effects 
//   *  if this has no data
//   *    return true
//   *  else
//   *    return false
//   */
//  public abstract boolean isEmpty();

  /**
  * @effects 
  *  Connect to the data source represented by this and load the data values (if available)
  *  
  *  <br>Throws NotPossibleException if fails.
  */
  public void connect() throws NotPossibleException {
    if (dodm != null) {
      // should already have been connected via the domain schema
      // check it
      /*v2.8: support memory-based data source 
      if (!dom.getDom().isConnectedToDataSource()) {
        // 
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONNECT_DB,
            "Không thể kết nối tới nguồn dữ liệu");
      }
      */
      DOMBasic dom = dodm.getDom();
      boolean objectSerialised = dodm.isObjectSerialised(); 
      if (objectSerialised && !dom.isConnectedToDataSource()) {
        // 
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CONNECT_DB);
      }
      
    }
    // sub-classes need to provide other means to connect here
  }
  
  /**
  * @effects 
  *  if this has no data
  *    return true
  *  else
  *    return false
  */
  public boolean isEmpty() {
    if (dodm != null && domainClass != null) {
      if (isEmpty == null) {
        // do this once: check if data source contains any objects of the domain class
//        DomainSchema schema = getDomainSchema();
//        Class domainClass = getDomainClass();
        try {
          int count = dodm.getDom().retrieveObjectCount(domainClass);
          isEmpty = (count < 1);
        } catch (Exception e) {
          // error, check again later
          return true;
        }
      }
    } else {
      // sub-classes need to provide other means to check this condition here
      isEmpty = true;
    }
    
    return isEmpty;
  }
  
  /**
   * An optional method for sub-types to implement
   * 
   * @effects 
   *  return an Iterator of objects in this.
   *  
   *  <p>This method returns <tt>null</tt> by default (if not implemented by the sub-types)
   */
  //public abstract Iterator iterator() throws NotPossibleException;  
  public Iterator iterator() throws NotPossibleException {
    throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
        new Object[] {this.getClass().getSimpleName()+".iterator()"});
  }
  
  /**
   * @effects 
   *  if <tt>this.mainCtl</tt> is specified
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  protected ControllerBasic getMainController() {
    return mainCtl;
  }
  
  /**
   * @effects 
   *  return the domain schema of this; or null if no such schema is specified
   */
  public DODMBasic getDodm() {
    return dodm;
  }

  /**
   * @effects 
   *  return the domain class of this; or null if no such class is specified
   */
  public Class getDomainClass() {
    return domainClass;
  }

  /**
   * @effects 
   *  if this is a bounded data source, 
   *  i.e. there exists a data field whose values are derived from this
   *    return true
   *  else
   *    return false   
   */
  public boolean isBounded() {
    return dodm != null;
  }

  /**
   * @effects 
   *  register <tt>boundAttrib</tt> into this as an attribute whose values need to be loaded
   *  with the corresponding <tt>Oid</tt>s.
   */
  public void setLoadOidFor(DAttr boundAttrib) {
    if (oidCache == null)
      oidCache = new HashMap<DAttr,Map<Object,Oid>>();
    
    Map<Object,Oid> cacheEntry = new HashMap<Object,Oid>();
    oidCache.put(boundAttrib, cacheEntry);
  }
  
  /**
   * @effects 
   *  if oid cache is initialised and has an entry for <tt>boundAttrib</tt>
   *    return true
   *  else
   *    return false
   */
  protected boolean isLoadOidFor(DAttr boundAttrib) {
    if (oidCache != null) {
      return (oidCache.get(boundAttrib) != null);
    }
    
    return false;
  }
  
  /**
   * @requires 
   *  boundAttrib != null /\ val != null
   * @effects <pre>
   *  if oid cache is initialised
   *    if exists a cached Oid for <tt>boundAttrib</tt> and its value <tt>val</tt>
   *      return the Oid
   *    else
   *      return null
   *  else
   *    return null</pre>
   */
  protected Oid getCachedOid(DAttr boundAttrib, Object val) {
    if (oidCache == null) {
      return null;
    } else {
      Map<Object,Oid> cacheEntry = oidCache.get(boundAttrib);
      if (cacheEntry != null) {
        return cacheEntry.get(val);
      } else {
        return null;
      }
    }
  }

  /**
   * @requires 
   *  boundAttrib != null /\ val != null /\ id != null
   * @effects <pre>
   *  if oid cache is initialised and exists a cached entry for <tt>boundAttrib</tt>
   *    adds <tt>id</tt> into this entry for value <tt>val</tt>
   *  else
   *    do nothing</pre>
   */
  protected void addOidToCache(DAttr boundAttrib, Object val, Oid id) {
    if (oidCache != null) {
      Map<Object,Oid> cacheEntry = oidCache.get(boundAttrib);
      if (cacheEntry != null) {
        cacheEntry.put(val, id);
      }
    }
  }

  /**
   * @requires 
   *  obj != null /\ attribute != null /\ 
   *  <code>o.class</code> is a domain class and has a getter method
   *           named <code>"get"+ name</code> (first letter capitalised).
   * @effects returns the value of the domain attribute <code>attribute</code>
   *          of the object <code>obj</code> in this; throws
   *          <code>NotPossibleException</code> of an error occurred while
   *          getting the attribute value.
   *          
   * @version 2.6.4.b
   *  return an instance of DisplayValueTuple if Oid is required to be displayed
   *  with the bounded value
   */
  public Object getBoundAttributeValue(Object obj,
      DAttr attribute) throws NotPossibleException {
    if (isBounded()) {
      //v2.6.4.b: 
      //return domainSchema.getAttributeValue(obj, attribute.name());
      Object attribVal = dodm.getDsm().getAttributeValue(obj, attribute.name());
      boolean displayOid = getDisplayOidWithBoundValue(attribute);
      if (displayOid) {
        // use display value tuple <id,val>
        //List<DomainConstraint> idAttribs = domainSchema.getIDDomainConstraints(domainClass);
        // Note: extract the id-attribute values directly from the obj, without
        // looking it up in the schema
        Object[] idVals = dodm.getDsm().getIDAttributeValues(obj);
        DisplayValueTuple vt = new DisplayValueTuple();
        for (int i = 0; i < idAttribs.size(); i++) {
          vt.addElement(idAttribs.get(i), idVals[i]);
        }
        
        // add bound value last
        vt.addElement(attribute, attribVal);
        
        return vt;
      } else {
        return attribVal;
      }
    } else
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {JDataSource.class,"getBoundAttributeValue",obj +","+attribute.name()});  
  }
  
  /**
   * @requires
   *  boundedVal != null
   * @effects 
   *  if exists Object o in this the value of whose boundedAttribute is equal to boundedVal
   *    return o
   *  else
   *    throw NotFoundException  
   */
  public Object reverseLookUp(DAttr boundAttribute,
      Object boundedVal) throws NotPossibleException, NotFoundException {
    if (!isBounded())
      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_NOT_BOUNDED, 
          new Object[] {
          JDataSource.class,"reverseLookUp",domainClass.getSimpleName()+"."+boundAttribute.name()+","+boundedVal, ""});

    if (isEmpty()) {
      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_IS_EMPTY, 
          new Object[] {JDataSource.class,"reverseLookUp",domainClass.getSimpleName()+"."+boundAttribute.name()+","+boundedVal});      
    }

    Iterator oit = iterator();
    Object o;
    while (oit.hasNext()) {
      o = oit.next();
      Object bv = getBoundAttributeValue(o, boundAttribute);
      if (bv != null && bv.equals(boundedVal)) {//bv == boundedVal) {
        return o;
      }
    }

    // not found
    throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
        new Object[] {domainClass.getSimpleName(), boundAttribute.name() +"="+boundedVal});
  }

  /**
   * @requires 
   *  boundAttribute != null
   *  
   * @effects
   *  return a Iterator of the data value objects of the boundAttribute of the objects 
   *  in this  
   *  
   * @version 
   *  2.7.2: support object-group permissions
   */
  public Iterator getBoundedValues(DAttr boundAttribute) throws NotPossibleException {
    if (!isBounded())
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {JDataSource.class,"getBoundValues",boundAttribute.name()});

    // make a new list
    Iterator oit = iterator();
    
    if (oit != null) {
      /* v2.7.2: 
       * If exists object group permission then filter the result
       */
      ControllerBasic mainCtl = getMainController();
      boolean hasObjectGroupPermission = false;
      
      if (mainCtl != null)
        hasObjectGroupPermission = mainCtl.hasObjectGroupPermission(domainClass);
      
      Collection boundValues = new ArrayList();
      Object o;
      Oid oid;
      boolean objectPermit;
      while (oit.hasNext()) {
        o = oit.next();
        // v2.7.2:
        if (hasObjectGroupPermission) {
          oid = dodm.getDom().lookUpObjectId(domainClass, o);
          objectPermit = mainCtl.getObjectPermission(domainClass, oid);
          if (objectPermit)
            boundValues.add(getBoundAttributeValue(o, boundAttribute));
        } else {
          boundValues.add(getBoundAttributeValue(o, boundAttribute));
        }
      }
  
      return boundValues.iterator();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if <tt>displayOidWithBoundValue = true</tt>
   *    mark <tt>boundAttrib</tt> to have its values displayed with the Oid of the objects
   *  else
   *    mark <tt>boundAttrib</tt> to have its values displayed by themselves (without the Oid)
   */
  public void setDisplayOidWithBoundValue(DAttr boundAttrib,
      boolean displayOidWithBoundValue) {
    if (displayOidWithBoundValueMap == null)
      displayOidWithBoundValueMap = new HashMap<DAttr,Boolean>();
    
    displayOidWithBoundValueMap.put(boundAttrib, displayOidWithBoundValue);
  }

  /**
   * @effects 
   *  if <tt>boundAttrib</tt> is marked to have its values displayed with the Oid of the objects
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   */
  public boolean getDisplayOidWithBoundValue(DAttr boundAttrib) {
    if (displayOidWithBoundValueMap == null)
      return false;
    
    Boolean result = 
        displayOidWithBoundValueMap.get(boundAttrib);
    
    if (result != null)
      return result;
    else
      return false;
  }
  

  /**
   * @effects 
   *  if boundAttrib is marked as requiring a specific type of display for its value
   *  (e.g. to display the Oid with each value) 
   *    return true  
   *  else
   *    return false
   */
  public boolean useDisplayValueTypeFor(DAttr boundAttrib) {
    // for now it is true if the bound value of the attribute requires to be displayed with Oid
    return getDisplayOidWithBoundValue(boundAttrib);
  }
  
  /**
   * @requires 
   *  {@link #useDisplayValueTypeFor(DAttr)} = true /\ 
   *  v != null
   * @effects 
   *  if <tt>v</tt> is not already in the correct type
   *    convert <tt>v</tt> into a display value type object that has the same data type as that 
   *    generated by {@link #getBoundAttributeValue(Object, DAttr)} and whose 
   *    bound attribute is <tt>boundAttrib</tt> and whose id-attribute(s) are those of <tt>this.domainClass</tt>
   *  else
   *    return <tt>v</tt> 
   */
  public Object parseDisplayValue(Object v, DAttr boundAttrib) {
    if (v instanceof DisplayValueTuple) {
      // no need to parse
      return v;
    }
    
    // convert v into DisplayValueTuple
    //List<DomainConstraint> idAttribs = domainSchema.getIDDomainConstraints(domainClass);
    DAttr[] attribs = new DAttr[idAttribs.size()+1];
    int i = 0;
    for (DAttr idAttrib : idAttribs) {
      attribs[i] = idAttrib;
      i++;
    }
    attribs[i] = boundAttrib;
    
    return DisplayValueTuple.parseString(v.toString(), attribs);
  }
  
  /**
   * This is invoked when the underlying set of domain objects were changed by the application (
   * e.g. some bounded attribute value was changed or when a domain object is added or removed).
   * When this happens, the data source needs to determine how to react to the event depending 
   * on the nature of the data source.
   *  
   * @effects 
   *  update the state of this (if needed) and 
   *  forward <tt>ce</tt> to all listeners 
   */
  @Override // ChangeListener
  public void stateChanged(ChangeEvent ce) {
    // if necessary handle the event here: update the id cache
    updateCache(ce);
    
    /*v3.1: move to method
    // forward event to all listeners
    for (JBoundedComponent l : boundedComps) {
      l.stateChanged(ce);
    }
    */
    forwardStateChangedToBoundedComponents(ce);
  }

  /**
   * @effects 
   *  informs all {@link JBoundedComponent} of this of the occurrence of <tt>ce</tt>
   */
  private void forwardStateChangedToBoundedComponents(ChangeEvent ce) {
    // forward event to all listeners
    for (JBoundedComponent l : boundedComps) {
      l.stateChanged(ce);
    }
  }

  /**
   * @effects 
   *  return the bounded components of this or <tt>null</tt> if no bounded components are specified
   * @version 2.7.4
   */
  protected final Iterator<JBoundedComponent> getBoundedComponents() {
    if (boundedComps != null) {
      return boundedComps.iterator();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   * if Oid cache is initialised
   *   update the Oid cache based on the action performed on the 
   *   domain objects contained in the ChangeEvent ce 
   */
  private void updateCache(ChangeEvent ce) {
    if (oidCache == null)
      return;
    
    ChangeEventSource ds = (ChangeEventSource) ce.getSource();

    /*
     *  process events: add, update and delete
     *  Note: the add event should generally also be of interest. However, its handling 
     *  is performed by a sub-type of this, which knows exactly which bounded value 
     *  is for the Oid
     */
    if (ds.isDelete()) {
      // if objects are in cache, remove them
      List objects = ds.getObjects();
      if (objects != null) { 
        Object removed = null;
        for (Object o : objects) {
          // remove o from all the entries that contain it
          for (Map<Object,Oid> entry : oidCache.values()) {
            for (Object c : entry.keySet()) {
              if (c == o) {
                // to remove this object
                removed = c;
                
                break;
              }
            }

            // remove object if found
            if (removed != null)
              entry.remove(removed);
            
            removed = null;
          }
        }
      }
    } else if (ds.isUpdate()) {
      // if an object is in cache and its Oid has been changed then 
      // we also need to update its Oid
      /*v2.7.2: suport ObjectUpdateData 
      Collection<DomainConstraint> affectedAttributes = (Collection<DomainConstraint>) ds.getEventData();
      */
      ObjectUpdateData data = (ObjectUpdateData) ds.getEventData();
      Collection<DAttr> affectedAttributes = data.getUpdatedAttribs();
      
      if (affectedAttributes != null) {
        boolean updateOid = false;
        for (DAttr attrib : affectedAttributes) {
          if (attrib.id()) {
            // an id attribute has been changed
            updateOid = true;
            break;
          }
        }
        
        if (updateOid) {
          List objects = ds.getObjects();
          if (objects != null) { 
            Oid newOid;
            for (Object o : objects) {
              // update Oid of o in all cache entries that contain it
              newOid = dodm.getDom().lookUpObjectId(domainClass, o);
              for (Map<Object,Oid> entry : oidCache.values()) {
                if (entry.containsKey(o)) {
                  entry.put(o, newOid);
                }
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * Register a bounded component.
   * 
   * @effects register <tt>boundedComp</tt> as being bounded to this
   */
  public void addBoundedComponent(JBoundedComponent boundedComp) {
    boundedComps.add(boundedComp);
  }

  /**
   * Overriding methods must invoke this first. 
   * 
   * @effects 
   *  clears all <b>buffer<b>-related resources used by this
   */
  public void clearBuffer() {
    if (oidCache != null) {
      // v2.6.4.b: clear the entries
      //oidCache.clear();
      //oidCache = null;
      if (oidCache != null) {
        Collection<DAttr> keySet = oidCache.keySet();
        Map m;
        for (DAttr k : keySet) {
          m = oidCache.get(k);
          m.clear();
        }
      }
    }
    
    // v2.7.2
    isEmpty = null;
    // default (for unbounded): do nothing
    // Sub-types must override this if a real data source is used    
  }

  /**
   * @requires 
   *  this is in a state suitable for re-runing {@link #iterator()} 
   *  
   * @effects 
   *  for each {@link JBoundedComponent} in this
   *    refresh its binding (to obtain the most up-to-date) data objects
   * @version 3.1
   */
  protected final void refreshBindings() {
    for (JBoundedComponent bcomp : boundedComps) {
      bcomp.refreshBinding();
    }
  }

  
  /**
   * @effects 
   *  clear the state of all the bounded components of this 
   */
  public void clearBindings() {
    for (JBoundedComponent bcomp : boundedComps) {
      bcomp.clearBinding();
    }
  }

  /**
   * @version 3.2
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName()+" (" + domainClass + ")";
  }
}
