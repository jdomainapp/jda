package jda.modules.report.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.modules.report.model.OutputFilter;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of <tt>ReportController</tt> that represents controlers for parameterised 
 *  reports. A parameterised report is a report that needs 
 *  the user to input some data before it can be run.
 *  
 * @author dmle
 * 
 * @version 2.7.4
 */
public abstract class ParameterisedReportController<C> extends ReportController {
  
  private Map<DAttr,Input> inputAttributes;

  protected Node<RunComponent> refreshStartNode;
  protected Node<RunComponent> refreshStopNode;

  /* v3.1: support multiple outputs (moved to parent)
  // v2.7.4
  private OutputFilter outputFilter;
  */
  public ParameterisedReportController(DODMBasic schema, ApplicationModule module,
    Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }

  @Override
  public void initReport()  throws NotFoundException, DataSourceException {
    Class reportCls = getDomainClass();

    DODMBasic dodm = getDodm();
    
    inputAttributes = dodm.getDsm().getAnnotatedDomainAttributes(reportCls, INPUT); 
    
    /*v3.3: allow no formal input attribute defs (still has input though)  
    if (inputAttributes == null || 
          inputAttributes.isEmpty())
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, 
          new Object[] {"Input", reportCls.getSimpleName()});
    */
    
    super.initReport();
  }
  
  @Override
  protected void initRunTree() throws NotPossibleException {
    setRestartPolicy(
        RestartPolicy.None
        );
    
    setProperty("show.message.popup", Boolean.FALSE);
    DataController dctl = getRootDataController();    
    final Class reportClass = Report.class;
    
    /*
     * RUN TREE:
     *  initReport (single)                  [n]
     *  |--showGUI
     *  |--newObject (single)
     *  |--dctl.states:<Created | Updated>   [m1]
     *  |----dctl.clearChildren
     *  |----dctl.getCurrentObject           [refreshStartNode]
     *  |------doReport                      [refreshStopNode]
     *  |------restart
     *    
     */
    // add a node that runs once for all the subsequent runs of
    // this controller to initialise the resources
    RunComponent comp = new RunComponent(this, MethodName.initReport.name(), null);
    comp.setSingleRun(true);
    Node n = init(comp);

    // show GUI node
    comp = new RunComponent(this, MethodName.showGUI.name(), null);
    n = add(comp,n);

    // add create new object component (run once) 
    comp = new RunComponent(dctl,MethodName.newObject.name(), null);
    comp.setSingleRun(true);
    add(comp, n);

    // add a component to wait for the user to create or update object
    // this is repeated for each run
    comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    Node m1 = add(comp, n);

    // v2.6.4b: added a node to clear the children
    comp = new RunComponent(dctl, MethodName.clearChildren.name(), null);
    add(comp,m1);

    // add a node to obtain the created object
    comp = new RunComponent(dctl, MethodName.getCurrentObject.name(), null);
    refreshStartNode = add(comp,m1);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doReport.name(), new Class[] {reportClass});
    refreshStopNode = add(comp,refreshStartNode);

    // restart when finished 
    comp = new RunComponent(this, MethodName.restart.name(), null);
    add(comp,refreshStartNode);
  }

  @Override
  protected boolean updateReport(Report report) throws DataSourceException,
      NotFoundException {
    
    DODMBasic dodm = getDodm();

    /* v3.3: improved to support the case that report does not have any INPUT attributes
     * which requires that the report filter does the work instead (this case is used for 
     * complex report)
    Query<ObjectExpression> q = new Query<ObjectExpression>();
    Class refClass = null;

    refClass = generateReportQuery(q, report);
    
    // run search on the data container responsible for the domain class specified 
    // in the query
    if (q.isEmpty())  // no query terms
      q = null;
    
    Collection res = dodm.getDom().retrieveObjectOids(refClass, q);
    
    boolean hasResult = false;
      
    if (res != null) {
      // if there is a result filter then use it
      // support the use of multiple output filters for multiple outputs,
      Collection<DAttr> outputAttribs = getOutputAttributes();
      OutputFilter filter;
      Collection<Oid> filteredRes;
      
      // debug
      //System.out.printf("%s.updateReport()...%n", this);
      
      for (DAttr outputAttrib: outputAttribs) {
        filter = getOutputFilter(outputAttrib);
        if (filter != null) {
          filteredRes = filter.filter(this, res, report);
          
          if (filteredRes != null) {
            if (!hasResult) hasResult = true;
            setResult(outputAttrib, filteredRes);
          }
        } else {
          // assumes: only one of these cases occurs
          if (!hasResult) hasResult = true;
          setResult(outputAttrib, res);
        }
      }
    }
     */

    boolean hasResult = false;

    if (inputAttributes != null && !inputAttributes.isEmpty()) {
      Collection res = null;
      Query<ObjectExpression> q = new Query<ObjectExpression>();
      Class refClass = null;

      refClass = generateReportQuery(q, report);
      
      // run search on the data container responsible for the domain class specified 
      // in the query
      if (q.isEmpty())  // no query terms
        q = null;
      
      res = dodm.getDom().retrieveObjectOids(refClass, q);
      
      if (res != null) {
        // if there is a result filter then use it to filter res
        // support the use of multiple output filters for multiple outputs,
        Collection<DAttr> outputAttribs = getOutputAttributes();
        OutputFilter filter;
        Collection<Oid> filteredRes;
        
        // debug
        //System.out.printf("%s.updateReport()...%n", this);
        
        for (DAttr outputAttrib: outputAttribs) {
          filter = getOutputFilter(outputAttrib);
          if (filter != null) {
            filteredRes = filter.filter(this, res, report);
            
            if (filteredRes != null) {
              if (!hasResult) hasResult = true;
              setResult(outputAttrib, filteredRes);
            }
          } else {
            // assumes: only one of these cases occurs
            if (!hasResult) hasResult = true;
            setResult(outputAttrib, res);
          }
        }
      }
    } else {
      // no input attributes: rely on filter
      Collection<DAttr> outputAttribs = getOutputAttributes();
      OutputFilter filter;
      Collection<Oid> filteredRes;
      
      // debug
      //System.out.printf("%s.updateReport()...%n", this);
      
      for (DAttr outputAttrib: outputAttribs) {
        filter = getOutputFilter(outputAttrib);
        if (filter != null) {
          filteredRes = filter.filter(this, null, report);
          
          if (filteredRes != null) {
            if (!hasResult) hasResult = true;
            setResult(outputAttrib, filteredRes);
          }
        } else {
          // assumes: only one of these cases occurs
          //if (!hasResult) hasResult = true;
          setResult(outputAttrib, null);
        }
      }
    }
    
    if (hasResult) {
      //setResult(res);
      
      // prepare the result (if needed) to ensure the correct viewing behaviour
      /*
       * v3.1:
       * support multiple outputs: use result map (above) to display result for each output 
       */
      prepareResultForDisplay();
      
      // display data on the data container responsible for the output
      /*
       * v3.1:
       * support multiple outputs: use result map (above) to display result for each output 
       */
      displayResult(report);
      
      return true;
    } else {
      // no result found
      return false;
    }
  }

  /**
   * @modifies q
   * @effects 
   *  generate query <tt>Expression</tt>s from <tt>report</tt>'s input attributes and 
   *  add them to <tt>q</tt>.
   *  
   *  <br>Return the domain class that will be used to execute <tt>q</tt>. This is one of the 
   *  classes specified in <tt>report</tt>'s input attributes.  
   */
  protected abstract Class generateReportQuery(Query<ObjectExpression> q, Report report);

  /**
   * @effects 
   * clear the current report object and re-run it
   */
  @Override
  public void refresh() {
    // runs the sub-tree of the execution tree that is concerned with
    // running the report. 
    // IMPORTANT: this sub-tree must not include the final "restart" Node.
    /*v2.7.2: use nodes instead of method names 
    MethodName start = MethodName.getCurrentObject;
    MethodName stop = MethodName.doReport;
    */
    if (refreshStartNode != null && refreshStopNode != null)
      runASubTree(refreshStartNode, refreshStopNode);
  }
  
  /**
   * @effects 
   *  if exists domain attributes of <tt>this.cls</tt> that are annotated with 
   *  {@link Input}
   *    return a {@link Map}<tt>DomainConstraint,Input</tt> of them (in the definition order)
   *  else
   *    return null
   */
  public Map<DAttr,Input> getInputAttributes() {
    return inputAttributes;
  }

  /**
   * @requires 
   *  attribName is a valid input attribute name of this
   *  
   * @effects   
   *  return the {@link Input} specification of the input attribute named <tt>attribName</tt> of this; 
   *  
   *  <p> throws NotFoundException if <tt>attribName</tt> is not found
   * @version 3.0
   */
  public Input getInputAttributeSpec(String attribName) throws NotFoundException {
    if (attribName == null)
      return null;
    
    DAttr attrib;
    for (Entry<DAttr,Input> e : inputAttributes.entrySet()) {
      attrib = e.getKey();
      if (attrib.name().equals(attribName)) {
        return e.getValue();
      } 
    }
    
    // not found
    throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {attribName, this.getClass().getSimpleName()});
  }


  /**
   * @effects 
   *  if exists domain attributes of <tt>this.cls</tt> that are annotated with 
   *  {@link Input}
   *    return a Collection of them (in the definition order)
   *  else
   *    return null
   */
  private Collection<DAttr> getInputDomainAttributes() {
    if (inputAttributes != null) {
      return inputAttributes.keySet();
    } else {
      return null;
    }
  }
  
  @Override
  protected void clearOutputViews() {
    DataController dctl = getRootDataController();
    // clear GUI and all associated resources of all non-input domain attributes
    dctl.clearGUIOnlyExceptFor(getInputDomainAttributes());
  }
  
//  /**
//   * @effects 
//   *  return a {@link OutputFilter} that is used to produce the final report result
//   *  
//   *    <p>This method returns <tt>null</tt> by default. Sub-types should override to 
//   *    return their own Filter
//   */
//  protected OutputFilter getOutputFilter() {
//    return null;
//  }

// v3.1: move the parent
//  /**
//   * @effects 
//   *  return a {@link OutputFilter} that is used to produce the final report result
//   *  
//   *  <br>Throws NotPossibleException if failed to create the filter object.
//   *  
//   * @version 2.7.4
//   *  read filter object from the Output attribute
//   */
//  protected OutputFilter getOutputFilter() throws NotPossibleException {
//    if (outputFilter == null) {
//      Map<DomainConstraint,Output> outputAttributes = getOutputAttributes();
//      
//      //TODO: assume only the first output attribute has the filter
//      Output firstAttrib = outputAttributes.values().iterator().next();
//      
//      Class filter = firstAttrib.filter();
//      
//      if (filter != MetaConstants.NullType) {
//        Class outputClass = firstAttrib.outputClass();
//        
//        if (outputClass == MetaConstants.NullType)
//          outputClass = null;
//        
//        try {
//          outputFilter = (OutputFilter) filter.newInstance();
//          outputFilter.setOutputClass(outputClass);
//        } catch (Exception e) {
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_REPORT_OUTPUT_FILTER, e, 
//              new Object[] {filter.getSimpleName()});
//        } 
//      }
//    } 
//    
//    return outputFilter;
//  }
}
