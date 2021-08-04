package jda.modules.report.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dcsl.syntax.report.Input;
import jda.modules.dcsl.syntax.report.Output;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.report.model.OutputFilter;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.eventhandler.InputHelper;
import jda.mosa.controller.assets.util.ControllerLookUpPolicy;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.JDataContainer;
import jda.util.SwTk;

/**
 * @overview
 *  A sub-type of <tt>CompositeController</tt> that represents 
 *  controllers of reports.  
 *  
 * @author dmle
 * @version 
 * - 3.1: improved to support multiple outputs
 */
public abstract class ReportController<C> extends CompositeController {
  
  protected static final Class<Input> INPUT = Input.class;
  protected static final Class<Output> OUTPUT = Output.class;
  
  /**
   * The output attributes that hold {@link #results} of report 
   * 
   * <p>derived (to speed up report generation)
   */
  private Map<DAttr,Output> outputAttribs;
  
  /**
   * A <tt>nullable</tt> cache of the {@link OutputFilter} of each output attribute in {@link #outputAttribs}
   * (if no filters specified then this is set to <tt>null</tt>
   * 
   */
  private Map<DAttr,OutputFilter> outputFilters;

  // v3.1: support multiple outputs
  //private Collection<Oid> result;
  /**
   * Keep the result(s) of report. Each result is a {@link Collection} of {@link Oid}
   * of result objects, which will be initialised to the output attribute that is used as 
   * the key. 
   */
  private Map<DAttr,Collection<Oid>> results;
  
  /**
   * A <tt>nullable</tt> cache of the object forms of each output attribute in {@link #outputAttribs}
   * (if no output forms specified then this is set to <tt>null</tt>
   */
  private Map<DAttr,JDataContainer> outputViews;
  
  public ReportController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }
  
  /**
   * This method is associated to {@link MethodName#initReport}. 
   * 
   * @modifies outputAttribs, outputViews
   * 
   * @effects
   *  initialise resources necessary to run report
   *  (e.g. load the required domain objects)
   */
  public void initReport() throws NotFoundException, DataSourceException {
    // v2.7.4: create GUI if not yet done so
    createGUIIfNotAlready();
    
    final Class reportCls = getDomainClass();
    
    // read all the output attributes
    DODMBasic dodm = getDodm();
    
    /* v3.3: improved to exclude super-type output attributes if they share the same name as (i.e are shadowed by) those of reportCls 
    outputAttribs = dodm.getDsm().getDomainAttributes(reportCls, OUTPUT); 
    */
    outputAttribs = dodm.getDsm().getAnnotatedDomainAttributesNoDups(reportCls, OUTPUT);
    
    if (outputAttribs == null || 
           outputAttribs.isEmpty())
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, 
          "Không tìm thấy thuộc tính {0} của lớp {1}", "Output", reportCls.getSimpleName());
    
    // v3.1: init and cache output views
    JDataContainer outputView;
    for (DAttr outputAttrib: outputAttribs.keySet()) {
      // look up and cache
      outputView = getOutputView(reportCls, outputAttrib);
      
      if (outputView != null) {
        if (outputViews == null) outputViews = new HashMap();

        outputViews.put(outputAttrib, outputView);
        
        // show it (if not already)
        showOutputView(outputView);
      }
    }
    
    // v3.1: init output filters
    initOutputFilters();
    
    // show output view(s)
    //JDataContainer outputView = getOutputView(reportCls);
    
    //if (outputView != null)
      //showOutputView(outputView);
  }
  
  /**
   * @effects 
   *  initialise a {@link OutputFilter} for each output attribute in {@link #outputAttribs}
   *  
   *  <p>Throws NotPossibleException if failed to create a filter object.
   */
  private void initOutputFilters() throws NotPossibleException {
      
    Class filter, outputClass;
    OutputFilter outputFilter;
    DAttr outputAttribObj;
    Output outputAttrib;
    for (Entry<DAttr,Output> outputAttribE: outputAttribs.entrySet()) {
      outputAttribObj = outputAttribE.getKey();
      outputAttrib = outputAttribE.getValue();
      
      filter = outputAttrib.filter();
      
      if (filter != CommonConstants.NullType) {
        outputClass = outputAttrib.outputClass();
        
        if (outputClass == CommonConstants.NullType)
          outputClass = null;
        
        try {
          outputFilter = (OutputFilter) filter.newInstance();
          outputFilter.setOutputClass(outputClass);
          
          if (outputFilters == null) outputFilters = new HashMap();
          
          outputFilters.put(outputAttribObj, outputFilter);
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_REPORT_OUTPUT_FILTER, e, 
              new Object[] {filter.getSimpleName()});
        } 
      }
    }
  }
  
  /**
   * This method is associated to {@link MethodName#doReport}.
   *     
   * @effects 
   *  performs the report <tt>report</tt> and update the GUI to show the result; 
   *  
   *  <p>throws DataSourceException if fails to retrieve information from the database; 
   *  NotFoundException if necessary resources could not be found in the database;
   *  NotPossibleException if failed to auto-export the result
   */
  public boolean doReport(Report report) 
    throws DataSourceException, NotFoundException, NotPossibleException {
    // clear report first (in case this is an update)
    report.clearOutput();

    results = null;
    
    // update report
    boolean updated = updateReport(report);
    
    if (!updated) {
      clearOutputViews();
      
      displayMessageFromCode(MessageCode.ERROR_NO_REPORT_RESULT, null);
      return false;
    } else {
      // v3.0: support auto-export (to export result and show it to user)
      if (isAutoExport()) {
        DataController reportDctl = getRootDataController();
        
        try {
          // v3.1: wait until report is completed updated before running export
          // TODO: improve this to check GUI update, for now use a temporary solution
          SwTk.sleep(2000);
          
          /* v3.2: moved to shared method
          reportDctl.export();*/
          SwTk.exportDocument(reportDctl.getDataContainer());
        } catch (Exception e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_EXPORT_DOCUMENT, e, 
              new Object[] {this.getModuleName()});
        }
      }
      
      return true;
    }
  }

  /**
   * @requires 
   *  outputAttribs != null
   *  
   * @effects 
   *  return the <b>first</b> output attribute of <tt>this</tt>
   */
  protected DAttr getFirstOutputAttribute() {
    return outputAttribs.keySet().iterator().next();
  }
  
  /**
   * @effects 
   *  return <b>all</b> output attributes of <tt>this</tt>
   */
  protected Collection<DAttr> getOutputAttributes() {
    return outputAttribs.keySet();
  }

  /**
   * @effects 
   *  if exists the domain attributes used as output for <tt>reportClass</tt>
   *    return all as {@link Map}
   *  else
   *    return null
   */
  public Map<DAttr,Output> getOutputAttributesMap() {
    return outputAttribs;
  }
  
//  /**
//   * @effects 
//   *  if exists domain attributes used as output for <tt>reportClass</tt>
//   *    return the first of such attribute
//   *  else
//   *    return null
//   */
//  protected DomainConstraint getOutputDomainAttribute() {
//    return outputAttribs.keySet().iterator().next();
//  }
  
  /**
   * @effects 
   *  if exists the domain class used as input for <tt>reportClass</tt>
   *    return it
   *  else
   *    return null
   */
  public Class getInputDomainClass() {
    DAttr outputAttrib = getFirstOutputAttribute();
    
    if (outputAttrib != null) {
      // input class is the filter's class of the output attribute
      Class cls = outputAttrib.filter().clazz();
      if (cls == CommonConstants.NullType) //Select.NullClass)
        return null;
      else
        return cls;
    } else {
      return null;
    }
  }
  
  /**
   * @effects
   *  if this has GUI
   *    return the child <tt>JDataContainer</tt> of the GUI of the module of the report class <tt>reportClass</tt>
   *    that is responsible for displaying the output (this is typically the first child data container.)
   *  else
   *    return null
   *    
   *  <p>Throws NotFoundException if no controller for <tt>reportclass</tt> is found.
   */
  protected JDataContainer getOutputView(Class reportClass, DAttr outputAttribute) throws NotFoundException {
    
    // support caching of output view
    
    ControllerBasic reportCtl = lookUp(reportClass, ControllerLookUpPolicy.PrimaryOnly);
    
    if (reportCtl == null) {
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND, 
          new Object[] { reportClass.getSimpleName()});
    }
    
// v3.1: use the output attribute to find output view
//    /*v3.0: remove GUI check - some report can be run with a forked data controller
//      that has data container but without needing a GUI
//    // v2.6.4b: add support for Gui check
//    if (reportCtl.hasGUI()) {
//     */
//      DataController dctl = reportCtl.getRootDataController();
//      
//      if (dctl != null) {
//        /* v2.7.4: added support for using root container as the output view
//        Iterator<JDataContainer> childContainers = dctl.getDataContainer().getChildContainerIterator();
//        // TODO: assume the first child container
//        return childContainers.next();
//        */
//        Iterator<JDataContainer> childContainers = dctl.getDataContainer().getChildContainerIterator();
//        if (childContainers != null) {
//          // TODO: assume the first child container
//          return childContainers.next();
//        } else {
//          return dctl.getDataContainer(); 
//        }
//      } else {
//        return null;
//      }
//    /*v3.0
//    } else {
//      return null;
//    }
//    */

    DataController dctl = reportCtl.getRootDataController();
    
    if (dctl != null) {
      //DomainConstraint outputAttribute = getOutputDomainAttribute();
      
      if (outputAttribute != null) {
        // find the data container of outputAttrib
        DataController outputDctl = dctl.getChildControllerWithShadowSupport(outputAttribute);
        if (outputDctl != null) {
          return outputDctl.getDataContainer();
        } 
        /*else if (dctl.isNested()) {
          //TODO: is this case necessary?
          // no output view: use root container
          return dctl.getDataContainer();
        } */
      } else {
        //TODO: is this case necessary?
        // no output attribute specified: use root container
        return dctl.getDataContainer(); 
      }
    } 
    
    // no output view
    return null;
  }
  
  /**
   * @effects 
   *  return the result of the <b>first</b> output attribute in this or <tt>null</tt> if no such result is available
   */
  public Collection<Oid> getFirstResult() {
    //return result;
    if (results != null) {
      return results.entrySet().iterator().next().getValue();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if there are {@link OutputFilter}s specified for <tt>outputAttribs</tt>
   *    return them
   *  else
   *    return <tt>null</tt>
   */
  protected Map<DAttr, OutputFilter> getOutputFilters() {
    return outputFilters;
  }

  /**
   * @effects 
   *  if exists {@link OutputFilter} of <tt>outputAttrib</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  protected OutputFilter getOutputFilter(DAttr outputAttrib) {
    if (outputFilters != null) {
      return outputFilters.get(outputAttrib);
    } else {
      return null;
    }
  }

  
  /**
   * @effects 
   *  clear the current report object and re-run it
   * @version 
   *  this implementation currently does nothing. Sub-types need to override 
   *  if needed
   */
  @Override
  public void refresh() {
    // to be overriden by sub-types (if needed)
  }
  
  /**
   * @effects 
   *  update <tt>report</tt> with domain data based on the report input (if any)
   *  return <tt>true</tt> if the report has result and was successfully updated, 
   *  otherwise return <tt>false</tt>.
   *  
   *  <p>Throws DBException if error in accessing the database, 
   *  NotFoundException if required domain objects could not be found
   */
  protected abstract boolean updateReport(Report report)  
      throws DataSourceException, NotFoundException;
  
  /**
   * @effects 
   *  record <tt>result</tt> in <tt>this</tt> as result of the output attribute <tt>outputAttrib</tt> 
   */
  protected void setResult(DAttr outputAttrib, Collection<Oid> result) {
    //this.result = result;
    if (results == null) {
      results = new HashMap();
    }
    
    results.put(outputAttrib, result);
  }
  
  /**
   * Sub-types should override this method if needed to prepare result before displaying it. 
   * An example operation to perform is to sort the result ({@see #sortResult()}) if the <tt>Oid</tt>s contained therein 
   * are not already arranged in the sorted order. 
   * 
   * @modifies result
   * @effects 
   *  if <tt>results != null</tt>
   *    prepare it suitable for display by {@link #displayResult(Report)}
   *  else
   *    do nothing
   */
  protected void prepareResultForDisplay() {
    if (results != null) {
      // clear any other resources associated to the output domain class (e.g. index)
      // this must be done before clearing the output view (below)

      /* v3.1: support multiple outputs
      Class reportCls = getDomainClass();
      
      DataController outputDctl = getDataControllerFor(reportCls);
      
      if (outputDctl != null) { // v3.1: added this check
        outputDctl.clearDomainClassResources(true);
        
        // v2.7.2: clear output view to show result
        clearOutputView();
      }
      */
      if (outputViews != null) {
        DataController outputDctl;
        for (Entry<DAttr,JDataContainer> outputViewE : outputViews.entrySet()) {
          outputDctl = outputViewE.getValue().getController();
          outputDctl.clearDomainClassResources(true);
        }

        // clear ALL output views to show result
        clearOutputViews();
      }
    }
    
    // add other preparation tasks here (if needed)
  }
  
  /**
   * @modifies results
   * @effects 
   *  if results != null
   *    sort <tt>Oid</tt>s of each <tt>result</tt> in <tt>results</tt> in sorted order
   *  else
   *    do nothing
   */
  protected void sortResult() {
    if (results != null) {
      Collection<Oid> result;
      Collection<Oid> res;
      for (Entry<DAttr,Collection<Oid>> resultE : results.entrySet()) {
        result = resultE.getValue();
        res = new TreeSet<Oid>(); 
        for (Oid oid : result) res.add(oid);
        
        //result = res;
        results.put(resultE.getKey(), res);
      }
    }
  }
  
  /**
   * @effects 
   *  if <tt>results</tt> != null /\ this has an output UI
   *    display <tt>results</tt> on the output UI
   *  else
   *    do nothing
   * @version 
   * - 3.1: add parameter report AND support multiple output attributes
   */
  protected void displayResult(Report report) throws NotFoundException, DataSourceException {
    /*v3.1: support multiple outputs
    if (result != null) {
      Class reportCls = getDomainClass();
      JDataContainer outputView = getOutputView(reportCls);
      if (outputView != null) {
        DataController outputDCtl = outputView.getController();
        
        // display report data
        outputDCtl.open(result);
        
        // v2.7.2: if there are other view containers that are configured to be automatically opened
        // then open them
        Iterator<DataController> autoChildren = getAutoChildDataControllers(reportCls);
        if (autoChildren != null) { 
          DataController child;
          while (autoChildren.hasNext()) {
            child = autoChildren.next();
            if (child == outputDCtl)
              continue; // skip
            
            // show the container
            showOutputView(child.getDataContainer());
            
            // open data
            try {
              child.open();
            } catch (DataSourceException e) {
              logError("Failed to open auto child controller", e);
            }
          }
        }

        if (hasGUI()) { // v3.0: added this check
          // v2.7.2: update GUI size to best fit the result
          getGUI().updateSizeOnComponentChange();
        }
      } 
    }
    */
    if (results != null) {
      DAttr outputAttrib;
      Collection<Oid> result;
      boolean myDisplayed, displayed = false;
      for (Entry<DAttr,Collection<Oid>> resultE : results.entrySet()) {
        outputAttrib = resultE.getKey();
        result = resultE.getValue();
        myDisplayed = displayResult(outputAttrib, result);
        if (!displayed && myDisplayed) displayed = myDisplayed;
      }
      
      if (displayed) {
        // report results were successfully displayed
        
        // v2.7.2: if there are other view containers that are configured to be automatically opened
        // then open them
        Class reportCls = getDomainClass();

        Iterator<DataController> autoChildren = getAutoChildDataControllers(reportCls);
        if (autoChildren != null) { 
          DataController child;
          JDataContainer childContainer;
          while (autoChildren.hasNext()) {
            child = autoChildren.next();
            childContainer = child.getDataContainer();
            if (outputViews != null && outputViews.containsValue(childContainer))
              continue; // skip
            
            // show the container
            showOutputView(childContainer);
            
            // open data
            try {
              child.open();
            } catch (DataSourceException e) {
              logError("Failed to open auto child controller", e);
            }
          }
        }
    
        if (hasGUI()) { // v3.0: added this check
          // v2.7.2: update GUI size to best fit the result
          getGUI().updateSizeOnComponentChange();
        }
      }
    }
  }

  /**
   * @effects 
   *  if exists object form of <tt>outputAttrib</tt> in <tt>this.view</tt>
   *    display <tt>result</tt> on the form
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  private boolean displayResult(DAttr outputAttrib, Collection<Oid> result) throws NotFoundException, DataSourceException {
    Class reportCls = getDomainClass();
    JDataContainer outputView = getOutputView(reportCls, outputAttrib);
    
    if (outputView != null) {
      DataController outputDCtl = outputView.getController();
      // display report data
      outputDCtl.open(result);
      return true;
    } else {
      return false;
    }
  }

// v3.1: not needed  
//  /**
//   * @effects
//   *  return the <tt>DataController</tt> of the output view ({@link #getOutputView(Class)}) or 
//   *  return null if there is no output view 
//   */
//  private DataController getDataControllerFor(Class reportClass) throws NotFoundException {
//    JDataContainer outputView = getOutputView(reportClass);
//    if (outputView != null) {
//      return outputView.getController();
//    } else
//      return null;
//  }
  
  /**
   * @effects 
   *  if there are child controllers of this.rootDataController that are configured
   *  with an auto-open policy
   *    return them as Collection
   *  else
   *    return null 
   */
  protected Iterator<DataController> getAutoChildDataControllers(Class reportClass) {
    ControllerBasic reportCtl = lookUp(reportClass, ControllerLookUpPolicy.PrimaryOnly);
        
    // v2.6.4b: add support for Gui check
    if (reportCtl.hasGUI()) {
      DataController dctl = reportCtl.getRootDataController();
      
      Iterator<DataController> children = dctl.getChildAutoControllersIterator();
      return children;
    } else {
      return null;
    }
  }
  
  /**
   * @effects
   *  if outputView is not visible
   *    make it visible
   *  else
   *    do nothing
   */
  protected void showOutputView(JDataContainer outputView) {
    if (!outputView.isVisible()) {
      outputView.setVisible(true);
      getMainController().getGUI().updateContainerLabelOnVisibilityUpdate(outputView, true);
    } 
  }
  
  /**
   * @effects
   *  if outputView is not activated
   *    activate it
   *  else
   *    do nothing
   */
  protected void activateOutputView(JDataContainer outputView) {
    if (!outputView.hasFocus()) {
      InputHelper ihelper = getInputHelper();
      ihelper.activateDataContainer(outputView);
      ihelper.updateToolBarButtons();
    } 
  }
  
  /**
   * @effects 
   *  clear the report's view
   *  
   * @version 
   * - 2.7.2 <br>
   * - 3.1: improved to clear all data components bound to the non-essential attributes of the report  
   */
  protected void clearOutputViews() {
    DataController dctl = getRootDataController();
    /*v3.1
    dctl.clearChildren();
    */
    // clear GUI and all associated resources
    dctl.clearGUIOnly();
  }
  
  /**
   * @requires 
   *  inputAttrib.sourceQuery = true /\ inputAttrib is specified with an {@link QueryDef}
   * @effects 
   *  retrieve value of <tt>inputAttrib</tt> from data source using 
   *  a query that is defined by the attribute's {@link QueryDef}. 
   *  
   *  <br>if <tt>inputAttrib</tt>'s type is Array
   *    return <tt>result</tt> as <tt>Object[]<tt>
   *   else
   *    return <tt>result</tt> as <tt>Object</tt>
   * @version 3.1 
   */
  protected Object retrieveAttributeValue(Class reportCls, Report report, DAttr inputAttrib) throws 
    NotPossibleException {
    // a collection-typed source attribute specified with a SIMPLE source query
    // load the objects from data source that satisfy the query
    DOMBasic dom = getDodm().getDom();
    DSMBasic dsm = getDomainSchema();
    
    boolean isArrayType = inputAttrib.type().isArray();
    
    // retrieve the source query definition 
    QueryDef queryDesc = dsm.getDomainAttributeAnnotation(reportCls, 
        QueryDef.class, inputAttrib.name());

    if (queryDesc == null) {
      // no query descriptor found
      throw new NotPossibleException(NotPossibleException.Code.NO_OBJECT_QUERY_DESCRIPTOR, 
          new Object[] {reportCls.getSimpleName()+"."+inputAttrib.name()});
    }

    Class dataCls = queryDesc.clazz();
    String[] selector = queryDesc.selector();
    
    // we dont create a full query here, because this query only defines a selector over an 
    // attribute of a class
    //Query query = QueryToolKit.createQuery(dsm, queryDesc);
    // ASSUMEs: one attribute is specified
    String attribName = selector[0];
    DAttr attrib = dsm.getDomainConstraint(dataCls, attribName);
        
    Collection attribVals = dom.loadAttributeValues(dataCls, attrib);
      
      
    if (attribVals != null) {
      if (isArrayType)
        return attribVals.toArray();
      else
        return attribVals;
    } else {
      return null;
    }
  }
}
