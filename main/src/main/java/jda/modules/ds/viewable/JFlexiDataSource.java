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
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.Query;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.datafields.model.DisplayValueTuple;

/**
 * @overview 
 *  An implementation of {@see JDefaultDataSource} that <b>DOES NOT</b> require the 
 *  bound attribute to be serialisable. For example, bound attribute can be 
 *  a derived attribute, whose values are computed from other (serialisable) attribute(s).
 *  
 * @author dmle
 */
public class JFlexiDataSource extends JDefaultDataSource {
  
  private final static boolean debug = Toolkit.getDebug(JFlexiDataSource.class);

// v3.1: for use with a new feature but not yet works  
//  /**
//   * Maps bound attributes of {@link #domainClass} whose declared types are {@link Enum} to the 
//   * data types. If a bound attribute's type is not an Enum then maps it to <tt>null</tt>
//   * 
//   * <p>This map is used to cache the mappings to speed up the check during data source operations 
//   */
//  private Map<DomainConstraint,Class> enumTypeAttribMap;
  
  public JFlexiDataSource(ControllerBasic mainCtl, DODMBasic dodm, Class domainClass) {
    super(mainCtl, dodm, domainClass);
  }

  @Override 
  public Iterator getBoundedValues(DAttr boundAttribute) throws NotPossibleException {
    if (!isBounded())
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          new Object[] {JFlexiDataSource.class, "getBoundValues",boundAttribute.name()});

    // load bounded values directly from the data source
    final DODMBasic dodm = getDodm();
    DOMBasic domObj = dodm.getDom();
    //DSMBasic dsm = dodm.getDsm();
    
    if (!(domObj instanceof DOM)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_DOM_TYPE, new Object[] {domObj.getClass()});
    }
    
    DOM dom = (DOM) domObj;
    final Class cls = getDomainClass();
    
    boolean loadOidFor = isLoadOidFor(boundAttribute);
    boolean displayOid = getDisplayOidWithBoundValue(boundAttribute);
    
    Collection boundValues = null;
    
    /*v2.8: added support for memory-based configuration */
    boolean objectSerialised = dodm.isObjectSerialised();
    
    if (objectSerialised) {
      // objects are serialised into a data source
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
        // do not load Oid with attribute values: load only the values
        boundValues = dom.loadAttributeValues(cls, boundAttribute);
//        /*v3.2: support load by reference, when boundAttribute is a non-serialisable derived attribute of a domain-type attribute
//         * e.g. 
//         *  boundAttribute: SubjectBySemester.code (serialisable=false)
//         *  derived from: SubjectBySemester.subject (domain Type: Subject)
//         *  
//         *  Subject.code: a domain attribute that is referred to by the above
//         *  
//         * in this case, we actually load attribute values from Subject.code instead.
//         * 
//         */
//        boolean loadByRef = false;
//        if (boundAttribute.serialisable()==false && boundAttribute.derivedFrom().length==1) {
//          String derivedAttribName = boundAttribute.derivedFrom()[0];
//          DomainConstraint derivedAttrib = dsm.getDomainConstraint(cls, derivedAttribName);
//          if (derivedAttrib.type().isDomainType()) {
//            // derived from a domain-typed attribute
//            Class refDomainType = dsm.getDomainClassFor(cls, derivedAttrib);
//            // load values from the attribute in refDomainType that has the same name as boundAttribute
//            DomainConstraint refAttrib = dsm.getDomainConstraint(refDomainType, boundAttribute.name());
//            boundValues = dom.loadAttributeValues(refDomainType, refAttrib);
//            
//            loadByRef = true;
//          }
//        }
//        
//        if (loadByRef == false) // not load by reference
//          boundValues = dom.loadAttributeValues(cls, boundAttribute);
        
        /*v3.1: this code is to support enum-type attribute BUT it is not yet working properly
            - support the case where boundAttribute's type is an enum; in which case no need to load values 
              only to retrieve the enum constants 
        Class enumType = getEnumTypeOrNot(boundAttribute);
        if (enumType != null) {
          // enum data type: values are the id attribute values of the enum's constants
          boundValues = dom.getIdAttributeValues(enumType);
        } else {
          // not a domain type: load values from data source
          boundValues = dom.loadAttributeValues(cls, boundAttribute);
        }
        */
      }
    } else {
      // objects are not serialised: just get the attribute values from pool
      boundValues = dom.getAttributeValues(cls, boundAttribute);
    }
    
    return boundValues != null ? boundValues.iterator() : null;
  }

// v3.1: added but not used  
//  /**
//   * @effects 
//   *  if <tt>boundAttribute</tt>'s declared type in {@link #domainClass} is an {@link Enum}
//   *    return the declared type
//   *  else
//   *    return <tt>null</tt> 
//   */
//  protected Class getEnumTypeOrNot(DomainConstraint boundAttribute) {
//    if (enumTypeAttribMap == null)
//      enumTypeAttribMap = new HashMap();
//    
//    Class enumTypeOrNot = null;
//    
//    if (!enumTypeAttribMap.containsKey(boundAttribute)) {
//      // not yet registered in the map
//      if (boundAttribute.type().isDomainType()) {
//        DSMBasic dsm = getDodm().getDsm();
//        Class cls = getDomainClass();
//        Class domainType = dsm.getDomainClassFor(cls, boundAttribute);
//        if (domainType.isEnum()) {
//          enumTypeOrNot = domainType;
//        }
//      }
//      enumTypeAttribMap.put(boundAttribute, enumTypeOrNot);
//    } else {
//      // already registered in the map (could be mapped to null)
//      enumTypeOrNot = enumTypeAttribMap.get(boundAttribute);
//    }
//    
//    return enumTypeOrNot;
//  }

  @Override
  public Object reverseLookUp(DAttr boundAttribute,
      Object boundedVal) throws NotPossibleException, NotFoundException {
    if (debug) System.out.printf(this.getClass().getSimpleName()+".reverseLookUp(%s,%s)%n", boundAttribute.name(), boundedVal);

    final Map<Object,Object> objCache = getObjectCache();

    DODMBasic dodm = getDodm();
    DOMBasic domObj = dodm.getDom();
    
    if (!(domObj instanceof DOM)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_DOM_TYPE, new Object[] {domObj.getClass()});
    }
    
    DOM dom = (DOM) domObj;
    
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
            throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {cls.getSimpleName(), Arrays.toString(idVals)});
          }
          
          oid = to.getKey();
          o = to.getValue();
        } else {
          // Oid is not yet loaded in the pool load it (with the object from the data source)          
          /* v3.1: support non-serialisable, derived bound attribute
           *  - add multiple expressions, one for each deriving attribute
           *  - if boundAttribute's data type is an enum then retrieve it directly from the object pool
           */
          /*v3.1: this code is to support enum-type attribute BUT it is not yet working properly
          Class enumType = getEnumTypeOrNot(boundAttribute);
          if (enumType != null) {
            // an enum-type: requires 2 steps:
            // - retrieve enum bounded val from the pool of enumType
            // - then use enumBoundedVal as boundedVal to retrieve object of cls (below)
            Object enumBoundedVal = dom.lookUpObjectByID(enumType, boundedVal);
            
            boundedVal = enumBoundedVal;
          }*/

          /*
           * v3.2: support non-serialisable, non-derived bound attribute whose value is obtained directly from the object pool 
           * without having to query the data source
          Map<Oid,Object> objs = null;
          Query query = getLookUpQuery(cls, boundAttribute, boundedVal);
          try {
            objs = dom.retrieveObjects(cls, query);
          } catch (DataSourceException e) {
            // not found
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
                "Lỗi thao tác trên nguồn dữ liệu đối với {0}",
                cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal);
            
          }
           */
          Map<Oid,Object> objs = null;
          if (!boundAttribute.serialisable() && boundAttribute.derivedFrom().length==0) {
            // non-serialisable, non-derived bound attribute: retrieve directly from pool
            try {
              objs = dom.getObjectsMap(cls, boundAttribute, Op.EQ, boundedVal);
            } catch (DataSourceException e) {
              // not found
              throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e,
                  new Object[] {cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal});
              
            }
          } else {
            // process as before (retrieve from data source) 
            Query query = getLookUpQuery(cls, boundAttribute, boundedVal);
            try {
              objs = dom.retrieveObjects(cls, query);
            } catch (DataSourceException e) {
              // not found
              throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e,
                  new Object[] {cls.getSimpleName()+"."+boundAttribute.name()+"="+boundedVal});
              
            }
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

  /**
   * @requires 
   *  attrib != null /\ attribVal != null
   * @effects 
   *  return a data source {@link Query} whose expressions are defined over <tt>attrib</tt> and 
   *  <tt>attribVal</tt> of <tt>cls</tt>
   *  
   *  <p>If <tt>attrib</tt> is non-serialisable, derived attribute then the expressions are defined
   *  over the deriving attributes.
   *  
   *  <p>throws NotPossibleException if <tt>attrib</tt> is neither serialisable and nor derived, or 
   *  if failed to parse the derived attribute value (when required); 
   *  throws NotFoundException if a deriving attribute is not found
   *  
   * @version 
   * - 3.1 <br>
   * - 3.3: improved to support 2-level derived attribute
   */
  protected Query getLookUpQuery(Class cls, DAttr attrib,
      Object attribVal) throws NotPossibleException, NotFoundException {
    Query query = new Query();
    boolean serialisable = attrib.serialisable();
    
    Expression exp;
    if (serialisable) {
      // serialisable attribute
      exp = new ObjectExpression(cls, attrib, Op.EQ, attribVal);
      query.add(exp);
    } else {
      // non-serialisable attribute
      String[] deriveFrom = attrib.derivedFrom();
      if (deriveFrom.length == 0) {
        // not derived
        throw new NotPossibleException(NotPossibleException.Code.INVALID_BOUND_ATTRIBUTE, 
            new Object[] {cls.getSimpleName(), attrib.name()});
      } else {
        // derived attrib: attribVal is formed from values of the deriving attributes
        // parse attribVal and use the result to create query expressions
        DSMBasic dsm = getDodm().getDsm();
        Object[] derivingAttribVals = dsm.parseDerivingAttributeValues(cls, attrib, attribVal);
        DAttr derAttrib;
        int index = 0;
        for (Object derVal : derivingAttribVals) {
          derAttrib = dsm.getDomainConstraint(cls, deriveFrom[index]);
          /*v3.3: support 2-level derived attribute (i.e. derived attribute is derived from another serialisable attribute) 
            exp = new ObjectExpression(cls, derAttrib, Expression.Op.EQ, derVal);
           */
          if (derAttrib.derivedFrom().length > 0) {
            if (derAttrib.derivedFrom().length > 1) {
              // illegal: must be derived from exactly one source attribute
              throw new NotPossibleException(NotPossibleException.Code.INVALID_DERIVED_ATTRIBUTE,
                  new Object[] {cls.getSimpleName(), derAttrib.name(), Arrays.toString(derAttrib.derivedFrom())});
            }
            
            // 2-level derived attribute: the level-2 source attribute must be serialisable and domain-typed
            exp = getSingleSourceDerivedAttributeQueryExpression(cls, derAttrib, derVal);
          } else if (derAttrib.type().isDomainType() && !DSMBasic.isEnum(dsm.getDomainClassFor(cls, derAttrib))) { // v3.3: added this case 
            // derAttrib is a domain-typed but Not an enum-type: need to create a join expression with value constraint (similar to the 
            // getSingleSourceDerivedAttributeQueryExpression but is applied to derAttrib)
            exp = getDomainTypedAttributeQueryExpression(cls, derAttrib, attrib.name(), derVal);
          } else {
            // none of the above: process normally
            exp = new ObjectExpression(cls, derAttrib, Op.EQ, derVal);
          }
          // end v3.3
          
          query.add(exp);
          index++;
        }
      }
    }
    
    return query;
  }

  /**
   * @requires 
   *  <tt>cls.srcAttrib</tt> is a domain-typed attribute /\ 
   *  <tt>refType = type(cls.srcAttrib)</tt> an attribute that is named <tt>targetAttribName</tt>  
   * 
   * @effects 
   *  create and return a suitable query {@link Expression} for <tt>cls.srcAttrib</tt> such that involes a join 
   *  between <tt>cls</tt> and the declared type of <tt>srcAttrib</tt> (i.e. <tt>refType = type(cls.srcAttrib)</tt>) and 
   *  a value constraint on <tt>refType.targetAttribName EQ attribVal</tt>
   *  
   * @version 3.3
   */
  private Expression getDomainTypedAttributeQueryExpression(Class cls, DAttr srcAttrib, String targetAttribName, Object targetAttribVal) {
    DSMBasic dsm = getDodm().getDsm();

    // create the join query from cls to the srcAttrib.type
    Tuple2<DAttr, DAssoc> assocTuple = dsm.getAssociation(cls, srcAttrib);
    DAssoc assoc = assocTuple.getSecond();
    Class refType = assoc.associate().type();
    
    // order join classes s.t the many-side is first
    Class[] joinClasses;
    if (assoc.endType().equals(AssocEndType.Many)) {  
      joinClasses = new Class[] {cls, refType};
    } else {
      joinClasses = new Class[] {refType, cls};
    }

    ObjectJoinExpression exp = QueryToolKit.createJoinExpressionWithValueConstraint(dsm, joinClasses, 
        new String[] {assoc.ascName()}, 
        targetAttribName, Op.EQ, targetAttribVal);
    
    return exp;
  }

  /**
   * @requires 
   *  <tt>cls.derAttrib</tt> is a derived attribute that is derived from exactly one source attribute and that this source
   *  attribute is serialisable and has a domain type  
   *  
   * @effects 
   *  return {@link ObjectJoinExpression} over <tt>cls.derAttrib</tt> which joins <tt>cls</tt> with the domain type that is 
   *  referenced by the attribute and is constrained by <tt>attribVal</tt>
   *  
   * @version 3.3
   */
  private ObjectJoinExpression getSingleSourceDerivedAttributeQueryExpression(Class cls, DAttr derAttrib, Object attribVal) throws NotPossibleException, NotFoundException {
    String srcAttribName = derAttrib.derivedFrom()[0];
    
    DSMBasic dsm = getDodm().getDsm();

    DAttr srcAttrib = dsm.getDomainConstraint(cls, srcAttribName);
    
    if (!srcAttrib.serialisable() || !srcAttrib.type().isDomainType()) { 
      // not valid attrib
      throw new NotPossibleException(NotPossibleException.Code.INVALID_DERIVED_SOURCE_ATTRIBUTE, 
          new Object[] {cls.getSimpleName(), srcAttrib.name()});
    }
    
    // TODO: call getDomainTypedAttributeQueryExpression on srcAttrib
    // create the join query from cls to the srcAttrib.type
    Tuple2<DAttr, DAssoc> assocTuple = dsm.getAssociation(cls, srcAttrib);
    DAssoc assoc = assocTuple.getSecond();
    Class refType = assoc.associate().type();
    
    // order join classes s.t the many-side is first
    Class[] joinClasses;
    if (assoc.endType().equals(AssocEndType.Many)) {  
      joinClasses = new Class[] {cls, refType};
    } else {
      joinClasses = new Class[] {refType, cls};
    }

    // attribute of refType that has the same name as derAttrib
    // this attribute is used to create the value constraint for the join expression
    String refAttribName = derAttrib.name();
    
    ObjectJoinExpression exp = QueryToolKit.createJoinExpressionWithValueConstraint(dsm, joinClasses, new String[] {assoc.ascName()}, 
        refAttribName, Op.EQ, attribVal);
    
    return exp;
  }
}
