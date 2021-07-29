package jda.modules.dodm.dom;

import static jda.modules.dodm.dsm.DSMBasic.isAbstract;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.filter.Filter;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.DomainIdable;
import jda.mosa.model.Oid;
import jda.util.ObjectComparator;

/**
 * @overview
 *  A sub-type of {@link DOMBasic} that supports a number of additional features:
 *  <ul>
 *    <li>Fully support memory-based configuration (i.e. when objects are only stored in pools and not in the data source)
 *    <li>Domain value validation: support <tt>unique</tt> constraint
 *   </ul>
 *  
 * @author dmle
 */
public class DOM extends DOMBasic {

  public DOM(DODMConfig config, DSMBasic dsm) {
    super(config, dsm);
  }

  /**
   * @effects 
   *  retrieve objects the values of the attribute <tt>attribName</tt> are <tt>op attribVal</tt> using 
   *  {@link #retrieveObjects(Class, jda.modules.oql.def.Query)}
   * @author congnv
   * @version 2.7.3
   */
  public <T> Map<Oid,T> retrieveObjectsMap(Class<T> c, String attribName, Op op, Object attribVal) throws DataSourceException, NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    Query q = new Query(new ObjectExpression(c, attrib, op, attribVal));
    Map<Oid,T> objects= retrieveObjects(c, q, null);
    
    return objects;
  }

  @Override
  protected void validateUniqueConstraint(Class cls, DAttr d,
      Object value) throws ConstraintViolationException {
    if (existAttributeValue(cls, d, value)) { 
      throw new ConstraintViolationException(
        ConstraintViolationException.Code.INVALID_VALUE_NOT_UNIQUE,
        new Object[] {d.name(), value}); 
    } 
  }

  @Override
  public <T> Map<Oid, T> retrieveObjects(Class<T> c,
      Tuple2<DAttr, DAssoc> assocTuple, Object linkedObj,
      Class linkedObjCls, Expression...exps) throws DataSourceException {
    if (isObjectSerialised()) {
      return super.retrieveObjects(c, assocTuple, linkedObj, linkedObjCls, exps);
    } else {
      // objects are not stored in data source
      
      // look up in c's object pool for objects that are linked to linkedObj via assocTuple
      // there is no need to update association links because objects loaded from pool 
      // alread have these
      
      DAttr attrib = assocTuple.getFirst();
      /* v3.0: support extra expressions
      return getObjectsMap(c, attrib, Op.EQ, linkedObj);
      */
      Query q = new Query();
      q.add(new ObjectExpression(c, attrib, Op.EQ, linkedObj));
      if (exps != null) {
        for (Expression exp : exps)
          q.add(exp);
      }
      
      return getObjectsMap(c, q);
    }
  }

  /**
   * @effects 
   *  if {@link #isObjectSerialised()} AND c is serialisable
   *    call {@link DOMBasic#retrieveObjects(Class, Query)}: retrieve objects from data source (if neeeded)
   *  else
   *    call {@link #getObjectsMap(Class, Query)}: retrieve objects directly from pool
   */
  @Override
  public <T> Map<Oid, T> retrieveObjects(Class<T> c, Query q, ObjectComparator comparator)
      throws DataSourceException, NotPossibleException {
    if (isObjectSerialised()
        //&& !getDsm().isTransient(c) // v3.0: added this check
        ) {
      return super.retrieveObjects(c, q, comparator);
    } else {
      // object is not stored in data source, get them directly from c's pool
      return getObjectsMap(c, q, comparator);
    }
  }

  @Override
  public Oid retrieveObjectId(Class c, DAttr[] idAttribs,
      Object[] idVals) throws NotFoundException, DataSourceException {
    // implementation is similar to supertype's method except for that part that 
    // uses osm
    /* v5.1 
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
    if (isObjectSerialised()) {
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
    } else {
      // object is not stored in data source
      return null;
    }
  }
  
  /**
   * @version 2.8
   *  implementation is similar to supertype's except for the part that use <tt>osm</tt>
   */
  @Override
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

        // v2.8: added this check
        if (osm != null) {
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
    }

    if (query == null) {
      return objects;
    } else {
      return getFilteredObjects(c, query);
    }
  }  
  
  /**
   * @effects
   *  call super-type's method and perform additional object state loading, including e.g. many-many state
   * @version 3.2
   */
  /* (non-Javadoc)
   * @see domainapp.basics.core.dodm.dom.DOMBasic#loadObject(java.lang.Class, domainapp.basics.model.Oid, java.util.List)
   */
  @Override
  public <T> T loadObject(Class<T> c, Oid oid,
      List<Tuple2<DAttr, DAssoc>> manyAssocs)
      throws DataSourceException, NotFoundException, NotPossibleException {
    T o = super.loadObject(c, oid, manyAssocs);
    
    // v3.2: if exist many-many associations in c then also load those associations
    // via their corresponding normaliser attributes
    // TODO: 
    // - if manyAssocs are specified then check within this
    // - for performance reason: somehow combine with loadDeterminantObjectsOf (of the super-type) ?
    loadManyToManyAssociatesOf(c, o, oid);
    
    return o;
  }

  /**
   * <b>NOTE</b>: The treatment of a many-to-many association is different from other associations (e.g. 
   * as performed via {@link #retrieveAssociatedObjects(Object, Class, Class, String, ObjectComparator, Expression...)})
   * in that the association links to the associate objects are derived via those of 
   * the <b>normaliser attribute</b> that normalises the association using a pair of one-many associations.   
   * 
   * @requires 
   *  c != null /\ o != null is a domain object of c /\ id != null 
   *   
   * @modifies o
   * @effects <pre> 
   *  if o has many-many associations with other classes 
   *    load the associate objects that are linked to <tt>o</tt> and update 
   *    <tt>o</tt> with association links to them
   *  else
   *    do nothing 
   *    </pre>
   *    
   *  throws NotFoundException if no normaliser attribute is specified or no suitable association 
   *    is defined for this attribute;  
   *  DataSourceException if fails to read object ids from the data source. 
   *    
   *  @version 
   *  - 3.2: created <br>
   *  - 3.3: added parameter manyAssocs
   */
  public <T> void loadManyToManyAssociatesOf(Class<T> c, T o, Oid id, DAssoc...manyAssocs) throws NotFoundException, DataSourceException {
    /*v3.3: support parameter manyAssocs
        Map<DAttr,DAssoc> assocs = dsm.getManyToManyAssociations(c);
      if (assocs != null) {
        // has many-many associations
        DAssoc assoc, normAssoc;
        Class assocCls; // the associate class on the opposite many-side
        DAttr normAttrib;  // normaliser attribute
        Tuple2<DAttr,DAssoc> normAssocTuple;  // association of normaliser attribute
        ASSOC: for (Entry<DAttr,DAssoc> e : assocs.entrySet()) {
          assoc = e.getValue();
          assocCls = assoc.associate().type();
  
          // only consider serialisable classes
          if (!DSMBasic.isTransient(assocCls)) {
            normAttrib = dsm.getDomainConstraint(c, assoc.normAttrib()); 
            normAssocTuple = dsm.getAssociation(
                //v3.3: wrong -> assocCls,
                c,
                normAttrib);
            normAssoc = normAssocTuple.getSecond();
  
            // retrieve associate objects that are linked via the normaliser attribute
            // ASSUME: link operations of the normaliser attribute also update the links to objects of assocCls
            retrieveAssociatedObjects(o, c, normAssoc.associate().type(), normAssoc.ascName());
          }
        }
      }
     */
    

    if (manyAssocs != null && manyAssocs.length > 0) {
      // only load for the specified associations
      ASSOC: for (DAssoc assoc : manyAssocs) {
        loadManyToManyAssociatesOfOne(c, o, id, assoc);
      } // end ASSOC
    } else {
      // load for all many associations
      Map<DAttr,DAssoc> assocs = dsm.getManyToManyAssociations(c);
      if (assocs != null) {
        // has many-many associations
        DAssoc assoc;
        ASSOC: for (Entry<DAttr,DAssoc> e : assocs.entrySet()) {
          assoc = e.getValue();
          loadManyToManyAssociatesOfOne(c, o, id, assoc);
        } // end ASSOC
      }
    }
  }
  
  /**
   * 
   * @requires 
   *  c != null /\ o != null is a domain object of c /\ id != null 
   *   
   * @modifies o
   * @effects <pre> 
   *  load the associate objects that are linked to <tt>o</tt> via association <tt>assoc</tt> and update 
   *  <tt>o</tt> with association links to them
   *    
   *  <p> throws NotFoundException if no normaliser attribute is specified or no suitable association 
   *    is defined for this attribute;  
   *  DataSourceException if fails to read from the data source. 
   *    
   *  @version 3.3
   */
  public <T> void loadManyToManyAssociatesOfOne(Class<T> c, T o, Oid id, DAssoc assoc) throws NotFoundException, DataSourceException {

    // the associate class on the opposite many-side
    Class assocCls = assoc.associate().type();

    DAssoc normAssoc;
    DAttr normAttrib;  // normaliser attribute
    Tuple2<DAttr,DAssoc> normAssocTuple;  // association of normaliser attribute
    
    // only consider serialisable classes
    if (!DSMBasic.isTransient(assocCls)) {
      normAttrib = dsm.getDomainConstraint(c, assoc.normAttrib()); 
      normAssocTuple = dsm.getAssociation(
          //v3.3: wrong -> assocCls,
          c,
          normAttrib);
      normAssoc = normAssocTuple.getSecond();
  
      // retrieve associate objects that are linked via the normaliser attribute
      // ASSUME: link operations of the normaliser attribute also update the links to objects of assocCls
      retrieveAssociatedObjects(o, c, normAssoc.associate().type(), normAssoc.ascName());
    }
  }
  
  /**
   * This method is needed because many-many associations are loaded 'lazily', i.e. they are loaded only 
   * for the object currently in question (via {@link #loadObject(Class, Oid)}) and not loaded for 
   * the associated objects of this object (these are loaded via {@link #loadAssociatedObject(Class, Oid, Class, Oid, DAttr)}. 
   * 
   * @requires 
   *  <tt>dodm</tt> is capable of loading many-many associations 
   *  
   * @effects 
   *  if value of the many-attribute of the many-many association named <tt>manyAssoc</tt> in <tt>o</tt> is <tt> null </tt>
   *    load associated objects via this association from the data source and add links to <tt>o</tt>
   *  else
   *    do nothing
   *     
   *  <p>Throws NotFoundException if association with the specified name is not found; 
   *  DataSourceException if failed to read from data source.
   *  
   * @version 3.3
   */
  public <T> void ensureObjectLoadedWithManyManyAssociates(Class<T> c, T o, String manyAssocName) throws NotFoundException, DataSourceException {
    Tuple2<DAttr, DAssoc> assocTuple = dsm.getAssociation(c, manyAssocName);
    
    if (assocTuple == null) {
      throw new NotFoundException(NotFoundException.Code.ASSOCIATION_NOT_FOUND, new Object[] {manyAssocName, AssocType.Many2Many, c});
    }
    
    DAttr manyAttrib = assocTuple.getFirst();
    DAssoc manyAssoc = assocTuple.getSecond();
    
    Object manyAttribVal = dsm.getAttributeValue(c, o, manyAttrib); // a Collection
    
    if (manyAttribVal == null) {
      // load many associaties
      Oid oid = lookUpObjectId(c, o);
      loadManyToManyAssociatesOfOne(c, o, oid, manyAssoc);
    }
  }
  
  @Override
  public <T> T reloadObject(Class<T> c, final Oid oid) throws NotPossibleException, NotFoundException, DataSourceException {
    // read from data source
    T o = (T) osm.reloadObject(c, oid);

    if (o == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Không tìm thấy đối tượng {0}<{1}>", c, oid); 
    }
    
    // v2.6.4b: store Oid into the object if it is an instance of DomainIdable
    if (o instanceof DomainIdable) {
      ((DomainIdable)o).setOid(oid);
    }
    
    // replace the existing object in pool (must use o.class not c here)
    Object old = addObject(o.getClass(), o, oid, false);

    // update the associated objects to use o instead of the old version
    // Note: this only updates the memory links not in the data source b/c o is only reloaded (not updated)
    updateAssociateLinksOnReload(old, o, o.getClass());
    
    /* no need to be concerned with other objects that may be referencing this reloaded object
     * those objects need to be refreshed (by the user) if they are to use the reloaded object 
     * 
    // v2.7.2: if o has determinant 1:1 association with other classes (i.e.
    // o.class is associated to another class X where X is the determinant of the association)
    // then load objects of those classes that are associated to o
    loadDeterminantObjectsOf(c, o, oid, null, null);
    */
    
    return o;
  }

//  /**
//   * @effects <pre>
//   * if attrib.serialisable = true
//   *  invoke super.loadAttributeValues
//   * else if attrib.deriveFrom.length > 0
//   *  load and return the set of attribute value tuples for the attributes in attrib.deriveFrom 
//   * else
//   *  return throw NotPossibleException(invalid attribute)   
//   *  </pre> 
//   * @version 3.2 
//   */
//  /* (non-Javadoc)
//   * @see domainapp.basics.core.dodm.dom.DOMBasic#loadAttributeValues(java.lang.Class, domainapp.basics.model.meta.DAttr)
//   */
//  @Override
//  public Collection loadAttributeValues(Class c, DAttr attrib) 
//      throws NotFoundException, NotPossibleException {
//    
//    if (attrib.serialisable()) {
//      return super.loadAttributeValues(c, attrib);
//    } else {
//      String[] deriveFrom = attrib.derivedFrom();
//      DSMBasic dsm = getDsm();
//      if (deriveFrom.length == 0) {
//        // not a derived attribute: invalid bound attribute
//        throw new NotPossibleException(NotPossibleException.Code.INVALID_BOUND_ATTRIBUTE, 
//            new Object[] {c.getSimpleName(), attrib});
//      } else {
//        // a derived attribute
//        DAttr[] derAttributes = new DAttr[deriveFrom.length];
//        
//        // v3.1: support a special case where attrib is derived from exactly one other attribute 
//        // and this attribute's data type is an Enum
////        if (boundAttribute.type().isDomainType()) {
////          Class domainType = dsm.getDomainClassFor(cls, boundAttribute);
////          if (domainType.isEnum()) {
////            // enum data type: values are the enum's constants
////            boundValues = new ArrayList();
////            Collections.addAll(boundValues,domainType.getEnumConstants());
////          } else {
////            // not an enum: load values from data source
////            boundValues = dom.loadAttributeValues(cls, boundAttribute);
////          }
////        } else {
////          // not a domain type: load values from data source
////          boundValues = dom.loadAttributeValues(cls, boundAttribute);
////        }
////        boolean deriveFromEnum = false;
//        
//        DAttr derAttrib;
//        for (int i = 0; i < deriveFrom.length; i++) {
//          derAttrib = dsm.getDomainConstraint(c, deriveFrom[i]);
//          derAttributes[i] = derAttrib;
//          
//          // v3.3: support 1-level recursive derived attribute def
//          
//        }
//        
//        Map<DAttr,List> dvalsMap = osm.readAttributeValueTuples(c, derAttributes);
//        
//        if (dvalsMap == null) {
////          throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_VALUES_NOT_FOUND,
////              new Object[] {c.getSimpleName(), attrib.name()});
//          return null;
//        }
//        
//        // compute the derived attributes
//        Object[] dvalArr;
//        Object value;
//        Collection values = new ArrayList();
//        int numDvalCols = dvalsMap.size();
//        Method valGenMethod = null;
//        Collection<List> dvalCols = dvalsMap.values();
//        int vindex = 0, colIndex;
//        OUTER: while (true) {  // loop until no more value tuples found
//          dvalArr = new Object[numDvalCols];
//          colIndex = 0;
//          for (List dvalCol : dvalCols) {
//            
//            if (vindex >= dvalCol.size()) {
//              // no more value tuples
//              break OUTER;
//            }
//            
//            dvalArr[colIndex] = dvalCol.get(vindex);
//            colIndex++;
//          }
//          
//          if (valGenMethod == null)
//            valGenMethod = dsm.findMetadataAnnotatedMethod(c, 
//                DOpt.Type.DerivedAttributeGetter, attrib);
//          
//          try {
//            value = valGenMethod.invoke(null, new Object[] {dvalArr});
//          } catch (Exception e) {
//            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
//                new Object[] {c.getSimpleName(), valGenMethod.getName(), dvalArr});
//          }
//          
//          values.add(value);
//          
//          vindex++;
//        } // end OUTER
//        
//        return values;
//      } // end else
//    } // end else
//  }

  /**
   * @requires
   *  if <tt>attrib</tt> is derived from other attributes then <b>all</b> key attributes of <tt>c</tt> must by owned by <tt>c</tt> 
   *   
   * @effects <pre>
   * if attrib.serialisable = true
   *  invoke super.loadAttributeValues
   * else if attrib.deriveFrom.length > 0
   *  load and return the set of attribute value tuples for the attributes in attrib.deriveFrom 
   * else
   *  return throw NotPossibleException(invalid attribute)   
   *  </pre> 
   * @version 
   * - 3.3: improved to support 2-level derived attribute def, i.e. a derived attribute that is in turn derived from 
   *        another serialisable, domain-typed attribute 
   */
  /* (non-Javadoc)
   * @see domainapp.basics.core.dodm.dom.DOMBasic#loadAttributeValues(java.lang.Class, domainapp.basics.model.meta.DAttr)
   */
  @Override
  public Collection loadAttributeValues(Class c, final DAttr attrib) 
      throws NotFoundException, NotPossibleException {
    
    if (attrib.serialisable()) {
      return super.loadAttributeValues(c, attrib);
    } else {
      String[] srcAttribNames = attrib.derivedFrom();
      DSMBasic dsm = getDsm();
      if (srcAttribNames.length == 0) {
        // not a derived attribute: invalid bound attribute
        throw new NotPossibleException(NotPossibleException.Code.INVALID_BOUND_ATTRIBUTE, 
            new Object[] {c.getSimpleName(), attrib.name()});
      } else {
        // a derived attribute
        //DAttr[] normalSrcAttributes = new DAttr[srcAttributes.length];
        List<DAttr> normalSrcAttributes = new ArrayList<DAttr>();
        
        // sequential values map for all source attributes: maps attrib -> List of values of that attribute
        // where attrib is in derAttributes
        Map<DAttr,Collection> dvalsMap = null;

//        // val map for domain-typed source attributes
//        Map<DAttr,Collection> refValsMap = null;

        // map for the source attribute of a derived attribute that itself is a derived attribute:
        // source attribute -> List of derived attributes that are derived from it
        Map<DAttr,Collection<DAttr>> refDAttribMap = null;
        
        Map<DAttr, Collection> mergedValsMap = null;

        DAttr srcAttrib, refSrcAttrib;
        Collection vals;
        final boolean orderByKey = true;
        for (String srcAttribName : srcAttribNames) {
          srcAttrib = dsm.getDomainConstraint(c, srcAttribName);
          // v3.3: support 1-level recursive derived attribute def
          
          if (srcAttrib.type().isDomainType()) {
            if (!srcAttrib.serialisable()) {
              // source attribute must be serialisable!
              throw new NotPossibleException(NotPossibleException.Code.INVALID_DERIVED_SOURCE_ATTRIBUTE, 
                  new Object[] {c.getSimpleName(), attrib.name(), srcAttrib.name()});
            }
            
            // srcAttrib: domain-typed attribute (e.g. SubjectBySemester.semester -> Semester)
            // load the referenced Oids of the referenced domain class
            vals = super.loadAttributeValues(c, srcAttrib, orderByKey); // recursive
            
            if (vals != null) {
              if (mergedValsMap == null) mergedValsMap = new HashMap<>();

              mergedValsMap.put(srcAttrib, vals);
            }
          } else if (srcAttrib.serialisable()) {
            // serialisable but not a domain-typed
            normalSrcAttributes.add(srcAttrib);
          } else if (srcAttrib.derivedFrom().length == 1) {
            // recursive derived attrib def: this level can only derive from one *serialisable, domain-typed* attribute 
            refSrcAttrib = dsm.getDomainConstraint(c, srcAttrib.derivedFrom()[0]);
            
            if (!refSrcAttrib.serialisable() || !refSrcAttrib.type().isDomainType()) { 
              // not valid attrib
              throw new NotPossibleException(NotPossibleException.Code.INVALID_DERIVED_SOURCE_ATTRIBUTE, 
                  new Object[] {c.getSimpleName(), srcAttrib.name(), refSrcAttrib.name()});
            }
            
            // valid attribute
            if (refDAttribMap == null) refDAttribMap = new HashMap<>();
            CollectionToolkit.updateCollectionBasedMap(refDAttribMap, refSrcAttrib, srcAttrib);
            //refDAttribMap.put(srcAttrib, refSrcAttrib); // value to be read later  
          } else {
            // not valid attrib
            throw new NotPossibleException(NotPossibleException.Code.INVALID_DERIVED_SOURCE_ATTRIBUTE, 
                new Object[] {c.getSimpleName(), attrib.name(), srcAttribName});
          }
        }
        
        /* merge values retrieved for refDAttribMap and normalSrcAttributes (if any)
         */
        if (refDAttribMap != null) {
          // there are recursive def
          /* read values of each refDAttribMap
               Note: two or more attributes may be derived from the same domain-typed attribute
           */
          Map<DAttr,Collection<Object>> refDvalsMap = loadRefDerivedAttributeValues(c, refDAttribMap);
          
          // values of normal derived attributes
          Map<DAttr,Collection> derAttributeValsMap = osm.readAttributeValueTuples(c, normalSrcAttributes.toArray(new DAttr[normalSrcAttributes.size()]), orderByKey);
          
          // if there are values found then merge refDvalsMap and derAttributeValsMap into to dvalsMap
          // (keeping the same original order of the derived attribute in deriveFrom
          // @modifies derAttributeValsMap
          if (refDvalsMap != null) {
            // has derived attributes
            if (mergedValsMap == null) mergedValsMap = new HashMap<>();
            mergedValsMap.putAll(refDvalsMap);
          }
          
          if (derAttributeValsMap != null) {
            // has values for normal source attributes
            if (mergedValsMap == null) mergedValsMap = new HashMap<>();
            mergedValsMap.putAll(derAttributeValsMap);  // merge
          }
        } else {
          // no recursive def
          if (!normalSrcAttributes.isEmpty()) {
            Map<DAttr, Collection> derAttributeValsMap = osm.readAttributeValueTuples(c, normalSrcAttributes.toArray(new DAttr[normalSrcAttributes.size()]), orderByKey);
            if (derAttributeValsMap != null) {
              if (mergedValsMap == null) {
                mergedValsMap = derAttributeValsMap;
              } else {
                mergedValsMap.putAll(derAttributeValsMap);
              }
            }
          }
        }
        
        if (mergedValsMap != null) {
          dvalsMap = new LinkedHashMap<>();
          // orderly put derived attribute values into dvalsMap 
          Set<DAttr> attrKeys = mergedValsMap.keySet();
          for (String derivedAttribName : srcAttribNames) {
            for (DAttr akey : attrKeys) {
              if (akey.name().equals(derivedAttribName)) {
                dvalsMap.put(akey, mergedValsMap.get(akey));
                break;
              }
            }
          }
        }

        if (dvalsMap == null) {
          // no values found at all!
          return null;
        }
        
        // compute the derived attributes from dvalsMap
        Object value;
        Collection values = new ArrayList();
        int numDvalCols = dvalsMap.size();
        Object[] dvalArr = new Object[numDvalCols];
        Object[] genMethodArg = new Object[1];
        Method valGenMethod = null;
        Collection<Collection> dvalCols = dvalsMap.values();
        int vindex = 0, colIndex;
        OUTER: while (true) {  // loop until no more value tuples found
          colIndex = 0;
          for (Collection dvalCol : dvalCols) {
            
            if (vindex >= dvalCol.size()) {
              // no more value tuples
              break OUTER;
            }
            
            dvalArr[colIndex] = CollectionToolkit.getElementAt(dvalCol, vindex); //dvalCol.get(vindex);
            colIndex++;
          }
          
          if (valGenMethod == null)
            valGenMethod = dsm.findMetadataAnnotatedMethod(c, DOpt.Type.DerivedAttributeGetter, attrib);
          
          try {
            genMethodArg[0] = dvalArr;
            value = valGenMethod.invoke(null, genMethodArg);
          } catch (Exception e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
                new Object[] {c.getSimpleName(), valGenMethod.getName(), dvalArr});
          }
          
          values.add(value);
          
          vindex++;
        } // end OUTER
        
        return values;
      } // end else
    } // end else
  }
  
//  /**
//   * @requires 
//   *  c is a valid domain class 
//   *  <br> /\ attrib is a valid domain-typed domain attribute of <tt>c</tt> 
//   *   
//   * @effects 
//   *   load from the data source values of <tt>c.attrib</tt>
//   *   <br>If values are found then return them as {@link Collection}, else return <tt>null</tt> 
//   *    
//   * @version 3.3
//   */
//  private Collection loadDomainTypedAttribValues(Class c, DAttr attrib) {
//    return null;
//  }

  /**
   * @requires 
   *  all attributes in <tt>refDAttribMap</tt> are of <tt>c</tt> 
   *  <br> /\ attributes in <tt>refDAttribMap.keys</tt> are serialisable, domain-typed attributes 
   *  <br> /\ for each <tt>(s,C) in refDAttribMap </tt>and for each <tt>(s,a), a in C</tt>: exists attribute a' in d.type s.t. a'.name=a.name</tt> 
   *  
   * @effects 
   *  Load from the data source values of each (derived) domain attribute <tt>a</tt> of <tt>c</tt> in 
   *  <tt>refDAttribMap.values</tt>, which is derived from the corresponding domain-typed (source) attribute <tt>s</tt> of <tt>c</tt>  
   *  in <tt>refDAttribMap.keys</tt>.
   *  
   *  <br>The values, which are put into a {@link List}, are exactly those of the domain attribute of the same name in the declared 
   *  type of <tt>s</tt> in the objects that are linked to those of <tt>c</tt> 
   *  
   *  <p>Return the loaded values in <tt>Map(DAttr,List)</tt>: attribute -&gt; List of values; or return <tt>null</tt>
   *  if no values are found
   *  
   *  <p>Throws NotPossibleException if any of the pre-conditions is not true 
   *
   *  @example <pre>
   *    c = SubjectBySemester:subject,code,year,semester
   *        where code.deriveFrom=["subject"] and subject.type = Subject
   *    refDAttribMap = Map:(subject -> Collection{code})
   *    
   *     -> result = Map:(code -> Collection:["IPG", "PPL", "SEG", ...])
   *  
   *  </pre>
   *  
   * @version 3.3
   */
  public Map<DAttr, Collection<Object>> loadRefDerivedAttributeValues(Class c, Map<DAttr, Collection<DAttr>> refDAttribMap) throws NotPossibleException, NotFoundException {
    /*
     let result be Map<DAttr,List>
     for each entry (aSrc,Collection<DAttr> aDer) in refDAttribMap
        load values of c.aSrc
            (these are Oids of the referenced objects)
        let c' = aSrc.type
        for each value oid 
          use oid to read from c' class store the values v1,...,vn for the d1,...,dn in aDer 
          add (di,vi)s into result
      return result
     */
    DAttr srcAttrib;
    Collection<DAttr> derAttribs;
    Map<DAttr, Collection<Object>> result = new HashMap<>();
    Tuple2<DAttr,DAssoc> assocTuple; 
    DAssoc assoc;
    Class refType;
    Collection<DAttr> refTypeAttribs; // the attributes of refType that have the same names as those in refDAttribMap
    Query refQuery = new Query();
    Collection<Oid> refOids;
    Map<DAttr,Object> refAttribValues;
    Class[] joinClasses;
    for (Entry<DAttr, Collection<DAttr>> e : refDAttribMap.entrySet()) {
      srcAttrib = e.getKey();
      derAttribs = e.getValue();
      
      // create the join query from c to the srcAttrib.type
      assocTuple = dsm.getAssociation(c, srcAttrib);
      assoc = assocTuple.getSecond();
      refType = assoc.associate().type();
      
      // get the attributes of refType that have the same name as derAttribs
      refTypeAttribs = new ArrayList<>();
      for (DAttr derAttrib : derAttribs) {
        refTypeAttribs.add(dsm.getDomainConstraint(refType, derAttrib.name()));
      }
      
      if (!refQuery.isEmpty()) refQuery.removeAll();
      
      // order join classes s.t the many-side is first
      if (assoc.endType().equals(AssocEndType.Many)) {  
        joinClasses = new Class[] {c, refType};
      } else {
        joinClasses = new Class[] {refType, c};
      }
      
      refQuery.add(QueryToolKit.createJoinExpression(dsm, joinClasses, new String[] {assoc.ascName()}));
      
      try {
        Class orderByKey = c; // order by c's key attribute
        refOids = retrieveObjectOids(refType, refQuery, orderByKey);
        if (refOids != null) {
          for (Oid refOid : refOids) {
            // use oid to read from refType's class store the values v1,...,vn for the d1,...,dn in derAttribs
            refAttribValues = loadMultipleAttributeValues(refType, refOid, refTypeAttribs);
            for (Entry<DAttr,Object> refValEntry : refAttribValues.entrySet()) {
              CollectionToolkit.updateCollectionBasedMap(result, refValEntry.getKey(), refValEntry.getValue());
            }
          }
        }
      } catch (DataSourceException e1) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e1, new Object[]{""});
      }
    }
    
    if (result.isEmpty())
        return null;
    else
      return result;
  }

  /**
   * @effects 
   *   load and return values of <tt>attribs</tt> of <tt>c</tt> in the object whose Oid is <tt>oid</tt>, 
   *   values are mapped to the corresponding attributes in the result
   *    
   * @version 3.3
   */
  public Map<DAttr, Object> loadMultipleAttributeValues(Class c, Oid oid, Collection<DAttr> attribs) {
    return osm.readAttributeValues(c, oid, attribs);
  }

  @Override
  public Map<Oid, Object> loadAttributeValuesWithOids(Class c,
      final DAttr attrib) throws NotFoundException, NotPossibleException {
    if (attrib.serialisable()) {
      return super.loadAttributeValuesWithOids(c, attrib);
    } else {
      String[] deriveFrom = attrib.derivedFrom();
      DSMBasic dsm = getDsm();
      if (deriveFrom.length == 0) {
        // not a derived attribute: invalid bound attribute
        throw new NotPossibleException(NotPossibleException.Code.INVALID_BOUND_ATTRIBUTE, 
            new Object[] {c.getSimpleName(), attrib});
      } else {
        // a derived attribute
        DAttr[] derAttributes = new DAttr[deriveFrom.length];
        for (int i = 0; i < deriveFrom.length; i++)
          derAttributes[i] = dsm.getDomainConstraint(c, deriveFrom[i]);
        Map<Object,List> dvalsMap = osm.readAttributeValueTuplesWithOids(c, derAttributes);
        
        if (dvalsMap == null) {
//          throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_VALUES_NOT_FOUND,
//              new Object[] {c.getSimpleName(), attrib.name()});
          return null;
        }
        
        // compute the derived attributes 
        Object[] dvalArr;
        Object value;
        Map<Oid,Object> values = new LinkedHashMap();
        Method valGenMethod = null;
        
        Collection<List> dvalCols = dvalsMap.values();
        // Oids are the first entry of the map
        List<Oid> oids = dvalCols.iterator().next();
        Oid id;

        int numDvalCols = dvalCols.size()-1;
        int numValues = oids.size();
        
        int vindex = 0, colIndex;
        OUTER: while (true) {  // loop until no more value tuples found
          if (vindex >= numValues) {
            // no more value tuples
            break OUTER;
          }
          
          // the Oid of this value
          id = oids.get(vindex);
          
          // the values array needed to compute
          dvalArr = new Object[numDvalCols];
          colIndex = 0;
          for (List dvalCol : dvalCols) {
            if (colIndex >= 1) {
              // deriving attribute values start from the second entry
              dvalArr[colIndex-1] = dvalCol.get(vindex);
            }
            colIndex++;
          }
          
          if (valGenMethod == null)
            valGenMethod = dsm.findMetadataAnnotatedMethod(c, 
                DOpt.Type.DerivedAttributeGetter, attrib);
          
          try {
            value = valGenMethod.invoke(null, new Object[] {dvalArr});
          } catch (Exception e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
                new Object[] {c.getSimpleName(), valGenMethod.getName(), dvalArr});
          }
          
          values.put(id, value);
          
          vindex++;
        } // end OUTER
        return values;
      } // end else
    } // end else
  }

  /**
   * @requires 
   *  c is a domain class /\ o is an object of c
   *  
   * @modifies o and associated objects of o (if any)
   * 
   * @effects 
   * <pre>
   *  if exists associations in c
   *    for each association (a,assoc) in c
   *      let v = o.a
   *      set o.a = null
   *      
   *      if v is Collection
   *        for each u in v
   *          update u to remove its association link to o
   *      else
   *        update v to remove its association link to o
   * </pre>
   *    
   * @version 3.3
   */
  public void removeAssociationLinks(Class c, Object o) throws NotPossibleException {
    Filter exclManyManyAssocFilter = DSMBasic.getExclManyManyAssocFilter();
    Map<DAttr, DAssoc> assocs = dsm.getAssociations(c, exclManyManyAssocFilter);
    
    if (assocs != null) {
      DAttr attrib;
      Object attribVal;
      Collection vals;
      DAssoc assoc;
      for (Entry<DAttr, DAssoc> e : assocs.entrySet()) {
        attrib = e.getKey();
        assoc = e.getValue();
        
        try {
          attribVal = dsm.getAttributeValue(c, o, attrib);
        } catch (NotPossibleException ex) {
          // perhaps getter method is not yet defined: ignore
          attribVal = null;
        }
        
        if (attribVal != null) {
          if (attribVal instanceof Collection) {
            // many side
            vals = (Collection) attribVal;
            
            for (Object v : vals) {
              updateAssociateToRemoveLink(v, o, attrib);
            }
          } else {
            // one side
            updateAssociateToRemoveLink(attribVal, o, attrib);
          }
          
          setAttributeValue(o, attrib.name(), null);
        }
      }
    }
  }

  /**
   * @requires 
   *  c is a registered domain class /\ o is an instance of c /\ subType is a sub-type of c /\ 
   *  {@link #isObjectSerialised()} 
   *  
   * @effects 
   *  transform <tt>o</tt> to become an object of <tt>subType</tt> by updating the underlying data store of <tt>subType</tt>.
   *  <br>Throws DataSourceException if failed to update data source.
   *  
   *  <br><i>Note</i>: No actual subType object is created by this method and so client application that needs to access this object
   *  is required to reload objects from the data source.  
   *  
   * @version 3.3
   */
  public void transformObjectToASubType(Class c, Object o, Class subType) throws DataSourceException {
    if (!isObjectSerialised()) {
      // only works if serialisation is used
      return;
    }

    osm.transformObjectToASubtype(c, o, subType);
  }

  /**
   * @requires 
   *  c is a registered domain class /\ o is an instance of c /\ supType is a super-type of c /\ 
   *  {@link #isObjectSerialised()}  
   *  
   * @effects 
   *  transform <tt>o</tt> to become an object of <tt>supType</tt> by updating the underlying data store of <tt>c</tt> and <tt>supType</tt> (if needed).
   *  <br>Throws DataSourceException if failed to update data source.
   *  
   *  <br><i>Note</i>: No actual <tt>supType</tt> object is created by this method and so client application that needs to access this object
   *  is required to reload objects from the data source.  
   *  
   * @version 3.3
   */
  public void transformObjectToSuperType(Class c, Object o, Class supType) throws DataSourceException {
    if (!isObjectSerialised()) {
      // only works if serialisation is used
      return;
    }

    osm.transformObjectToSupertype(c, o, supType);
  }

  // Use mappings: not yet tested
//@Override
//public void addClasses(final Collection<Class> classes, boolean read)
//    throws DataSourceException, NotPossibleException, NotFoundException {
//  DSM dsm = (DSM) super.getDsm();
//  
//  // to create if not exists
//  boolean createIfNotExist = true;
//  
//  // the table constraints to be added afterwards
//  Map<String,List<String>> tableConstraints = new LinkedHashMap<String,List<String>>();
//  
//  // create tables without constraints first
//  for (Class c : classes) {
//    if (!classExts.containsKey(c)) {
//      // register if not yet done so
//      /*v2.7.4: throw Exception here to force calling code to register class first 
//      registerClass(c);
//      */
//      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_REGISTERED,
//          new Object[] {c.getName()});
//    }
//    
//    // load the class objects (if any)
//    DomainClass cc = (DomainClass) c.getAnnotation(DSMBasic.CC);
//    boolean isTransient = (cc != null && cc.serialisable()==false);
//    if (!isTransient && osm != null) {
//      String dbSchema = (cc == null) ? MetaConstants.DEFAULT_SCHEMA : cc.schema();
//      dbSchema = dbSchema.toUpperCase();
//      String tableName = c.getSimpleName();
//
//      boolean exist = osm.exists(dbSchema, tableName);
//
//      if (createIfNotExist && !exist) {
//        // if a table of this class not exists then create
//        osm.createClassStoreWithoutConstraints(c, tableConstraints);
//        
//        /*
//        // v2.7.3 congnv: mapping - generate & store mapping
//        Collection<Mapping> mappings = dsm.generateMappings(c);
//        addObjects(mappings);
//        */
//      } else if (exist && read) {
//        /*
//        // v2.7.3 congnv: mapping - update table
//        updateMappings(c);
//        */
//        
//        // clear existing objects (if any)
//        Collection objects = getObjects(c);
//        if (!objects.isEmpty()) {
//          objects.clear();
//        }
//
//        // load its objects
//        retrieveObjectsWithAssociations(c);
//      }
//    }
//
//    // prepare a state change event that will be raised when class objects
//    // are changed
//    if (!changeEvents.containsKey(c)) { // v2.7.3: added this check
//      ChangeEventSource dsHelper = new ChangeEventSource(c);
//      ChangeEvent ce = new ChangeEvent(dsHelper);
//      changeEvents.put(c, ce);      
//    }
//  }
//  
//  // insert table constraints afterwards
//  if (osm != null && !tableConstraints.isEmpty()) {
//    osm.createConstraints(tableConstraints);
//  }
//}
  
  // Use mappings: not yet tested
//  // v2.7.3 congnv
//  /**
//   * update mappings associated with class c
//   * @effects
//   * <pre>
//   * foreach fields f of class c
//   *  (1) update mappings
//   *  get mapping m of f from the database
//   *  if mapping is not already exists
//   *    create new mapping & save
//   *  else if mapping changed
//   *    update mapping
//   *  (2) update table structure
//   * </pre>
//   */
//  //TODO: not yet tested
//  public void updateMappings(Class c) throws DataSourceException {
//    DSM dsm = (DSM) super.getDsm();
//
//    // get the declared fields of this class
//    // TODO: get all domain attributes (support for Type.Domain)
//    List fields = dsm.getDomainAttributes(c);
//    String tableName = dsm.getDomainClassName(c);
//    
//    // load mappings for this class
//    Map<Oid, Mapping> mappings = retrieveObjectsMap(Mapping.class, "mappingId", Op.MATCH, tableName+"_%");
//
//    if (fields == null) {
//      // delete existing mapping if exists
//      Mapping m;
//      if (mappings != null) {
//        for (Oid oid : mappings.keySet()) {
//          m = mappings.get(oid);
//          
//          deleteObject(m, oid, c);
//        } 
//      }
//    } else {
//      // update
//        Field f = null;
//        DomainConstraint dc;
//        Mapping mapping;
//        Map<DomainConstraint,Object> changedAttribVals;
//        
//        String oldFieldName;
//        for (int i = 0; i < fields.size(); i++) {
//            f = (Field) fields.get(i);
//            dc = f.getAnnotation(DSMBasic.DC);
//            
//            // ignore collection type
//            if(dc.type().isCollection()) continue;
//            
//            // perform 2 steps
//            // get mapping
//            mapping = (Mapping) lookUpObjectByID(Mapping.class, tableName+"_"+i);
//            
//            if (mapping == null) {
//              // create new mapping
//              mapping = dsm.generateMapping(tableName, dc, i);
//              addObject(mapping);
//              
//              // update schema
//              osm.updateDataSourceSchema(c, dc, i, null, null);
//            } else {
//              changedAttribVals = getMappingChange(mapping, c, dc, i);
//              // update mapping changes
//              if (changedAttribVals != null) {
//                // update schema
//                oldFieldName = mapping.getFieldName();
//                
//                osm.updateDataSourceSchema(c, dc, i, oldFieldName, changedAttribVals);
//                
//                // update object
//                updateObject(mapping, changedAttribVals);
//              }
//            }
//        }
//    }
//  }

  // Use mappings: not yet tested
//  // v2.7.3 congnv
//  /**
//   * determine changes to the mapping of dc in c then return changes as Map
//   * @effects 
//   * <pre>
//   *  for each attribute a in Mapping.attributes 
//   *    if a.value is changed
//   *      put <a.domainconstraint,value> into map  
//   *  return map
//   * </pre>
//   */
//  //TODO: not yet tested
//  public Map<DomainConstraint,Object> getMappingChange(Mapping mapping, Class c, DomainConstraint dc, int fieldIndex) {
//  Map<DomainConstraint, Object> vals = new HashMap<>();
//    DSM dsm = (DSM) super.getDsm();
//
//  // get the declared fields of this class
//    List fields = dsm.getRelationalAttributes(Mapping.class);
//    
//    if (fields == null)
//        throw new NotFoundException(NotFoundException.Code.ATTRIBUTES_NOT_FOUND,
//            "Không tìm thấy thuộc tính dữ liệu nào của lớp: {0}", Mapping.class);
//    
//    Field f = null;
//    DomainConstraint cc;
//    Object oldVal = null;
//    Object newVal = null;
//    Mapping mappingNew = null;
//    String className = dsm.getDomainClassName(c);
//    
//    for (int i = 0; i < fields.size(); i++) {
//        f = (Field) fields.get(i);
//        cc = f.getAnnotation(DSMBasic.DC);
//        mappingNew = dsm.generateMapping(className, dc, fieldIndex);
//        
//        // only detect changes if this field is mutable
//        if (cc.mutable()) {
//          // check that value has been changed
//          oldVal = dsm.getAttributeValue(f, mapping);
//          newVal = dsm.getAttributeValue(f, mappingNew);
//          
//          if ((oldVal == null && newVal != null) ||
//              (oldVal != null && !oldVal.equals(newVal))) {
//            // changed
//            vals.put(cc, newVal);
//          }
//        }
//    }
//
//    return (vals.isEmpty() ? null : vals);
//  }
}
