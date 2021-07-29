package jda.modules.ds.viewable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.datafields.model.DisplayValueTuple;

/**
 * @overview 
 *  An implementation of {@see JDefaultDataSource} that requires the 
 *  <b>bound attribute to be serialisable</b>, 
 *  i.e. its values can be loaded directly from the underlying data source, 
 *  and that uses an object cache for storing look-up results.  
 *  
 * @author dmle
 */
public class JAdaptiveDataSource extends JDefaultDataSource {
  
  private final static boolean debug = Toolkit.getDebug(JAdaptiveDataSource.class);
  
  public JAdaptiveDataSource(ControllerBasic mainCtl, DODMBasic dodm, Class domainClass) {
    super(mainCtl, dodm, domainClass);
  }

  /* v2.6.4.b: support display of Oid with bound values
   * v2.7.2: support objectgroup-based permission on the bounded objects 
   */
  @Override
  public Iterator getBoundedValues(DAttr boundAttribute) throws NotPossibleException {
    if (!isBounded())
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {JDataSource.class,"getBoundValues",boundAttribute.name()});

    // load bounded values directly from the data source
    final DODMBasic dodm = getDodm();
    DOMBasic dom = dodm.getDom();

    final Class cls = getDomainClass();
    
    boolean loadOidFor = isLoadOidFor(boundAttribute);
    boolean displayOid = getDisplayOidWithBoundValue(boundAttribute);
    
    Collection boundValues;
    
    /*v2.8: added support for memory-based configuration */
    boolean objectSerialised = dodm.isObjectSerialised();
    
    if (objectSerialised) {
      if (loadOidFor) {
        // load bound values with associated Oids
        Map<Oid,Object> idVals = dom.loadAttributeValuesWithOids(cls, boundAttribute);
        
        /* v2.7.2: object-group permission is only checked if the loadOid option is on
         * If exists object group permission then filter the results based on the Oids.
         */
        ControllerBasic mainCtl = getMainController();
        boolean hasObjectGroupPermission = mainCtl.hasObjectGroupPermission(cls);
        
        if (hasObjectGroupPermission) {
          /*v3.1: moved to Security 
          idVals = filterByObjectPermission(idVals);
          */
          idVals = mainCtl.getSecurity().filterObjectsByPermission(cls, idVals);
        }
        
        // v2.6.4.b: support display value tuple
        //boundValues = idVals.values();
        if (idVals == null) {
          boundValues = null;
        } else {
          if (displayOid) { 
            // display Oid with the values
            DisplayValueTuple vt;
            Oid id;
            Object val;
            boundValues = new ArrayList();
            for (Entry<Oid,Object> idVal : idVals.entrySet()) {
              // use display value tuple (id,...,val)
              vt = new DisplayValueTuple();
              id = idVal.getKey();
              val = idVal.getValue();
              for (int i = 0; i < id.size(); i++) {
                vt.addElement(id.getIdAttribute(i), id.getIdValue(i));
              }
              
              // add bound value last
              vt.addElement(boundAttribute, val);
  
              // add to result
              boundValues.add(vt);
              
              // v2.6.4.b: add to cache
              addOidToCache(boundAttribute, vt, idVal.getKey());
            }
          } else {
            // not display Oid, just use the values
            boundValues = idVals.values();
            
            // put val-ids to cache
            for (Entry<Oid,Object> idVal : idVals.entrySet()) {
              addOidToCache(boundAttribute, idVal.getValue(), idVal.getKey());
            }
          }
        }
      } else {
        // just load the bound values
        boundValues = dom.loadAttributeValues(cls, boundAttribute);
      }
    } else {
      // object not serialised: just get the attribute values from pool
      boundValues = dom.getAttributeValues(cls, boundAttribute);
    }
    
    return boundValues != null ? boundValues.iterator() : null;
  }
  
  @Override
  public Object reverseLookUp(DAttr boundAttribute,
      Object boundedVal) throws NotPossibleException, NotFoundException {
    if (debug) System.out.printf(this.getClass().getSimpleName()+".reverseLookUp(%s,%s)%n", boundAttribute.name(), boundedVal);

    final Map<Object,Object> objCache = getObjectCache();

    DODMBasic dodm = getDodm();
    DOMBasic dom = dodm.getDom();
    boolean objectSerialised = dodm.isObjectSerialised();
    
    Class cls = getDomainClass();
    
    if (!isBounded())
      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_NOT_BOUNDED, 
          new Object[] {JDataSource.class,"reverseLookUp",cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal});

    if (isEmpty()) {
      throw new NotPossibleException(NotPossibleException.Code.DATA_SOURCE_IS_EMPTY, 
          new Object[] {JDataSource.class,"reverseLookUp",cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal});      
    }

    boolean displayOid = getDisplayOidWithBoundValue(boundAttribute);
    
    // return object from cache if found

    /*v2.6.4b: supports a new cache key scheme
     */
    Object t; // cache key
    if (displayOid) {
      t = boundedVal;
    } else {
      t = Tuple2.newTuple2(boundAttribute, boundedVal);
    }
    
    Object o = objCache.get(t);
    if (o == null) {
      if (debug) System.out.printf("  not in object cache (key: %s) %n", t);
      
      /*
       *  if Oid for boundVal is available for this attribute then use it to load object
       *  otherwise load object directly using boundVal
       */
      //TODO: this cannot support duplicate boundedVal
      /*
       * v2.6.4.b: add support for the new cache key scheme
      Oid oid = getCachedOid(boundAttribute, boundedVal);
       */
      Object oidKey;
      Oid oid;
      if (displayOid) // use the same object cache key (which is the display value tuple) to look up Oid 
        oidKey = t;
      else  // use the bounded value to look up
        oidKey = boundedVal;
      
      oid = getCachedOid(boundAttribute, oidKey);
      
      if (oid != null) {
        if (debug) System.out.printf("  in Oid cache (key: %s): %s%n", oidKey, oid);

        // use this oid to load object
        o = dom.getObject(cls, oid);
        if (o == null
            && objectSerialised // v2.8: added this check
            ) {
          try {
            o = dom.loadObject(cls, oid);
          } catch (DataSourceException e) {
            // not found
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e,
                new Object[] {cls.getSimpleName()+"."+oid+": "+boundAttribute.name()+"="+boundedVal});
            
          }          
        }
        
        // v2.8: added
        if (o == null) {
          throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, cls, oid);
        }
      } else {
        if (debug) System.out.printf("  not in Oid cache (key: %s)%n", oidKey);

        // load the object matching the bounded value directly from the data source
        /*
         * v2.6.4b: support display value tuple
         */
        if (displayOid) {
          // Oid is actually already loaded in the object pool, but not yet added to the oid cache
          // -> extract the Oid value and uses it to look up in the object pool
          DisplayValueTuple vt = (DisplayValueTuple) boundedVal;
          int numElements = vt.size();
          DAttr[] idAttribs = new DAttr[numElements-1];
          Object[] idVals = new Object[numElements-1];
          for (int i = 0; i < numElements-1; i++) {
            idAttribs[i] = vt.getDomainConstraint(i);
            idVals[i] = vt.getElement(i);
          }
          
          Entry<Oid,Object> to = dom.lookUpObject(cls, idAttribs, idVals);
          if (to == null) {
            // should not happen
            throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
                new Object[] {cls.getSimpleName(), Arrays.toString(idVals)});
          }
          
          oid = to.getKey();
          o = to.getValue();
        } else {
          // Oid is not yet loaded in the pool load it (with the object from the data source)          
          Query query = new Query();
          Expression exp = new ObjectExpression(cls, boundAttribute, Op.EQ, boundedVal);
          query.add(exp);

          Map<Oid,Object> objs = null;
          try {
            objs = dom.retrieveObjects(cls, query);
          } catch (DataSourceException e) {
            // not found
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
                new Object[] {cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal});
            
          }
          
          if (objs == null) {
            // not found
            throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
                new Object[] {cls.getSimpleName(), boundAttribute.name() +"="+boundedVal});
          }
          
          if (objs.size() > 1)
            throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
                new Object[] {JDataSource.class + ".reverseLookUp:" + cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal +
                ": giá trị không duy nhất"});
          
          //o = objs.values().iterator().next();          
          Entry<Oid,Object> e = objs.entrySet().iterator().next();
          oid = e.getKey();
          o = e.getValue();
          
          // debug
//          if (debug)
//            System.out.println("  loaded object: " + o);
        }
        
        // update the oid cache
        addOidToCache(boundAttribute, boundedVal, oid);
      }
      
      // cache object
      objCache.put(t, o);
    } else {
      if (debug) System.out.printf("  in object cache (key: %s): %s%n", t, o);
    }
    
    return o;
  }
}
