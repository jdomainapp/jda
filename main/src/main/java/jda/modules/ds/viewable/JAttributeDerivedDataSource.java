package jda.modules.ds.viewable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.model.Oid;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ObjectUpdateData;

/**
 * @overview 
 *  A sub-type of {@link JDataSource} that is specifically used to present the (run-time) objects
 *  contained in the value of a collection-typed domain attribute as a data source for manipulation.
 *  
 *  <p>This data source is used with the {@link DAttr#sourceAttribute()} setting.
 *   
 * @author dmle
 * 
 * @version 
 * - 3.0: created<br>
 * - 3.1: support source attribute query data source query
 *
 */
public class JAttributeDerivedDataSource  extends JDataSource {
  
  private DAttr sourceAttrib;
  private DataController dataController;
  
  /**
   * objects of the attribute {@link #sourceAttrib} of the current object in {@link #dataController}
   */
  private Collection objectBuffer;

  public JAttributeDerivedDataSource(DataController dataController, DODMBasic dodm, 
      DAttr sourceAttrib, Class domainCls) {
    super(dodm, domainCls);
    this.dataController = dataController;
    this.sourceAttrib = sourceAttrib;
  }

  @Override
  public Iterator iterator() throws NotPossibleException {
    // create an Iterator over the object (to conform to the standard API) 
    // which provides access to the object
    if (objectBuffer == null) {
      objectBuffer = loadObjects();
    } 

    if (objectBuffer != null)
      return objectBuffer.iterator();
    else
      return null;
  }
  
  @Override
  public boolean isEmpty() {
    return (objectBuffer == null);
  }
  
  /**
   * @effects 
   *  read the objects of the source attribute 
   *  if succeeded
   *    return the objects
   *  else
   *    return <tt>null</tt>
   *    
   *  <p>Throws NotPossibleException if failed to load the object.
   */
  protected Collection loadObjects() throws NotPossibleException {
    DSMBasic dsm = getDodm().getDsm();
    
    /** v3.1: support sourceAttrib that is specified with a source query: load its values from data source using source query
    // the run-time object of the domain class
    Object rtObj = dataController.getCurrentObject();
    
    if (rtObj != null) {
      Collection objs = (Collection) schema.getAttributeValue(rtObj, sourceAttrib.name()); 
    
      return objs;
    } else {
      return null;
    }
    */
    Class dataCls = getDomainClass(); // the domain class whose objects are contained in this data source
    Class defCls = dataController.getDomainClass(); // the domain class that provides definition for the query (if any)
    DODMBasic dodm = getDodm();
    
    if (sourceAttrib.sourceQuery()) {
      // a collection-typed source attribute specified with a SIMPLE source query
      // load the objects from data source that satisfy the query
      DOMBasic dom = dodm.getDom();
      // retrieve the source query definition 
      QueryDef queryDesc = dsm.getDomainAttributeAnnotation(defCls, QueryDef.class, sourceAttrib.name());
      
      if (queryDesc == null) {
        // no query descriptor found
        throw new NotPossibleException(NotPossibleException.Code.NO_OBJECT_QUERY_DESCRIPTOR, 
            new Object[] {defCls.getSimpleName()+"."+sourceAttrib.name()});
      }
      
      Query query = QueryToolKit.createQuery(dsm, queryDesc);
      try {
        Map<Oid,Object> result = dom.retrieveObjects(dataCls, query);
        
        if (result != null) {
          return result.values();
        } else {
          return null;
        }
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, 
            new Object[] {dataCls.getSimpleName()+": từ nguồn "+defCls.getSimpleName()+"."+sourceAttrib.name()});
      }
    } else if (sourceAttrib.sourceQueryHandler()) {
      // a collection-typed source attribute specified with a COMPLEX source query handler function
      // find and execute the handler function 
      Method m = null;
      // find the default value function of the class c
      try {
        m = dsm.findMetadataAnnotatedMethod(defCls, DOpt.Type.SourceQueryHandler, sourceAttrib);
      } catch (NotFoundException e) {
        // not found
        throw new NotPossibleException(NotPossibleException.Code.NO_OBJECT_QUERY_HANDLER_FUNCTION, e, 
            new Object[] {dataCls.getSimpleName()+": từ nguồn "+defCls.getSimpleName()+"."+sourceAttrib.name()});
      }
      
      // invoke the method
      try {
        // m is a static method: 
        // m must take DOMBasic and an array of arguments, which map the derived attributes
        // (i.e. attributes whose values are used as input for the query) to the actual 
        // values of these attributes set on the data fields
        Object[] args = null;
        String[] derivedFrom = sourceAttrib.derivedFrom();
        if (derivedFrom.length > 0) {
          args = new Object[derivedFrom.length];
          DAttr derivedAttrib;
          int index = 0; Object val;
          for (String derivedAttribName : derivedFrom) {
            derivedAttrib = dsm.getDomainConstraint(defCls, derivedAttribName);
            val = dataController.getDataFieldValue(derivedAttrib);
            args[index] = val;
            index++;
          }
        }
        
        // execute the method and return the result if succeeded
        Collection result = (Collection) m.invoke(null, dodm, args);
        
        //System.out.println(this.getClass().getSimpleName()+": result: " + result);

        return result;
      } catch (Exception e) {
        // failed to perform function
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
            e, new Object[] {defCls, DOpt.Type.SourceQueryHandler, sourceAttrib.name()});
      }
    } else {
      // normal collection-typed attribute: use the attribute value
      // the run-time object of the domain class
      Object rtObj = dataController.getCurrentObject();
      
      if (rtObj != null) {
        Collection objs = (Collection) dsm.getAttributeValue(rtObj, sourceAttrib.name()); 
      
        return objs;
      } else {
        return null;
      }
    }
  }
  
  @Override
  public void stateChanged(ChangeEvent ce) {
    // nothing to be done to the state of this here
    // ask super to process further
    super.stateChanged(ce);
    
    ChangeEventSource ds = (ChangeEventSource) ce.getSource();
    
    boolean toRefreshBinding = ds.isAddNew() || ds.isDelete(); 
    
    if (!toRefreshBinding && ds.isUpdate()) {
      // only update if source attribute is one of the affected
      ObjectUpdateData data = (ObjectUpdateData) ds.getEventData();
      Collection<DAttr> affectedAttributes = data.getUpdatedAttribs();
      
      toRefreshBinding = (affectedAttributes != null && affectedAttributes.contains(sourceAttrib));
    }

    // debug
    //System.out.println(this.getClass().getSimpleName()+".stateChanged: action = "+ds.getAct()+"; source attribute = " + sourceAttrib.name() + "; to refresh = " + toRefreshBinding);

    if (toRefreshBinding) {
      /* force bounded data fields to reload: 
       * this step is particular to this data source because its object set is ONLY A SUBSET which is 
       * typically specified by a query, and so it is generally impossible to determine what to 
       * do with the changes embodied in the state change event.
       * For example, suppose the event is raised because a new object is created but the problem is 
       * that we cannot know if this object is suited for storage in this data source unless we evaluate
       * the query again the object. This is the not much different from evaluating the query again to obtain objects  
       */
      refreshBindings();
    }
  }
  
//  /**
//   * @effects 
//   *  return the domain attribute of the data field that is bounded to this OR
//   *  throw NotPossibleException if this is not the case.
//   * @version 2.7.4
//   */
//  protected DomainConstraint getBoundedAttribute() throws NotPossibleException {
//    if (boundedAttrib == null) {
//      Iterator<JBoundedComponent> bcomps = getBoundedComponents();
//      if (bcomps != null) { 
//        // just one bounded component
//        JBoundedComponent bcomp = bcomps.next();
//        boundedAttrib = bcomp.getDomainConstraint(); //bcomp.getBoundConstraint();
//      }
//
//      if (boundedAttrib == null) {
//        throw new NotPossibleException(NotPossibleException.Code.NO_BOUND_ATTRIBUTES);
//      }
//    }
//    
//    return boundedAttrib;
//  }

  @Override
  public void clearBuffer() {
    super.clearBuffer();
    //NOTE: do not clear objectBuffer!!!
    objectBuffer=null;
  }
  
}
