package jda.modules.report.controller;

import java.util.Collection;

import javax.swing.SwingUtilities;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of <tt>ReportController</tt> that represents controllers for 
 *  non-parameterised reports. A non-parameterised report is a report that does NOT need 
 *  the user to input some data before it can be run.
 *  
 * @author dmle
 */
public class NonParameterisedReportController extends  ReportController {

  /***
   * true if this report has been initialised by 
   * invoking {@link ReportController#initReport()}
   * false if otherwise    
   */
  private boolean init;
  
  private DAttr theOutputAttrib;

  public NonParameterisedReportController(DODMBasic schema, ApplicationModule module,
    Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }
  
  @Override
  public void run() throws NotPossibleException {
    /***
     * For reports that do not require user input, 
     * we can simply override the run() method 
     */
    
      // initialise resources
      if (!init) {  // run once
        setProperty("show.message.popup", Boolean.FALSE);
        try {
          initReport();
        } catch (Exception e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực hiện phương thức {0}.{1}({2})", this, "run", "");
        }
        init = true;
      }

      showGUI();

      /*v2.7.2: moved to runData() 
      // create the report object
      DataController dctl = getRootDataController();
      dctl.createObject();

      // retrieve the object
      Report report = (Report) dctl.getCurrentObject();

      // do the rest of the report
      doReport(report);
      */
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          try {  
            runData();
          } catch (Exception e) {
            NotPossibleException npe = new NotPossibleException(
                NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
                "Không thể thực hiện phương thức {0}.{1}({2})", this, "run", "");
            displayError(npe.getCode(), npe);
          }
        }
      });
  }

  /**
   * @effects 
   *  run the report to get the data and display them on the output view
   */
  private void runData() throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    // create the report object
    DataController dctl = getRootDataController();
    dctl.createObject();

    // retrieve the object
    Report report = (Report) dctl.getCurrentObject();

    // do the rest of the report
    doReport(report);  
  }
  
  @Override
  protected boolean updateReport(Report report) throws DataSourceException,
      NotFoundException {
    final DODMBasic schema = getDodm();
    
    // load object ids of the domain class used as input and 
    // open the browser on them
    Class reportClass = getDomainClass(); //report.getClass();
    
    Class domainCls = getInputDomainClass();
    
    if (domainCls == null)
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, new Object[] {"Input"});
    
    //Collection<Oid> objectIds = schema.loadObjectOids(domainCls, null);
    Collection<Oid> res = schema.getDom().retrieveObjectOids(domainCls, null);
    
    if (res != null) {
      //TODO: support multiple outputs (if needed)
      setSingleResult(res);
      
      // prepare the result (if needed) to ensure the correct viewing behaviour
      prepareResultForDisplay();
  
      // display result
      displayResult(report);
      
      //dctl.updateGUI(true);
      return true;
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  set <tt>res</tt> to the value of the first output attribute of <tt>this</tt>
   */
  protected void setSingleResult(Collection<Oid> res) {
    if (theOutputAttrib == null)
      theOutputAttrib = getFirstOutputAttribute();
    
    setResult(theOutputAttrib, res);
  }

  @Override
  public void refresh() {
    // simply re-run the report's data
    try {
      // clear report output
      clearOutputViews();
      
      runData();
    } catch (DataSourceException e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          "Không thể thực hiện phương thức {0}.{1}({2})", this, "refresh", "");
    }
  }
  
  /* ALTERNATIVE to run() above */
  //@Override
  //protected void initRunTree() throws NotPossibleException {
  //  setRestartPolicy(
  //      RestartPolicy.None
  //      );
  //  
  //  final Class reportClass = com.pvf.model.reports.ReportTabularSalesByProduct.class;
  //  
  //  setProperty("show.message.popup", Boolean.FALSE);
  //  DataController dctl = getDataController();    
  //  
  //  // add a node that runs once for all the subsequent runs of
  //  // this controller to initialise the resources
  //  RunComponent comp = new RunComponent(this, MethodName.initReport.name(), null);
  //  comp.setSingleRun(true);
  //  Node n = init(comp);
  //
  //  // show GUI node
  //  comp = new RunComponent(this, MethodName.showGUI.name(), null);
  //  n = add(comp,n);
  //
  //  // add create new object component (run once) 
  //  comp = new RunComponent(dctl,MethodName.createObject.name(),
  //      null);
  //  comp.setSingleRun(true);
  //  add(comp, n);
  //
  //  // add a node to obtain the created object
  //  comp = new RunComponent(dctl,
  //      MethodName.getCurrentObject.name(),
  //      null);
  //  n = add(comp,n);
  //
  //  // add a node to do the rest of the report
  //  comp = new RunComponent(this, "doReport", 
  //      new Class[] {reportClass});
  //  add(comp,n);
  //}
}
