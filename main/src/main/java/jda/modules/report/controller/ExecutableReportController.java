package jda.modules.report.controller;

import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.report.model.ExecutableReport;
import jda.mosa.controller.ControllerBasic;
import jda.util.SwTk;

/**
 * @overview
 *    A specialised Controller used to display a <b>single</b> {@link ExecutableReport} object.
 *    
 *    <p>Every time the report is run, the state of the <tt>Report</tt> object is refreshed and 
 *    is displayed again. 
 *     
 * @author dmle
 * 
 * @version 2.7.4
 */
public class ExecutableReportController extends ControllerBasic<ExecutableReport> {
  private boolean firstTime;
  
  public ExecutableReportController(DODMBasic schema,
      ApplicationModule module, Region moduleGui, ControllerBasic parent,
      Configuration config) throws NotPossibleException {
    super(schema, module, moduleGui, parent, config);
    firstTime = true;
  }

  @Override
  public void run() throws NotPossibleException {
    /***
     * Similar to NonParameterisedReport,  
     * we can simply override the run() method 
     */
    try {
      showGUI();
      
      DataController<ExecutableReport> dctl = getRootDataController();
      Object old = dctl.getCreator().setProperty("show.message.popup", false);

      if (firstTime) {
        // create a new report object
        dctl.createObject();
        
        /*v3.0: moved to method
        // set DODM into it so that it can use to obtain the data
        //Class cls = getDomainClass();
        ExecutableReport report = dctl.getCurrentObject(); //ExecutableReport.createInstance(cls, getDodm());
        
        report.run(getDodm());
        
        // this does not add object to browser, OK for single-object controller 
        //dctl.setCurrentObject(report, false);
        
        dctl.updateGUI(null);
        */
        doReport();
        
        firstTime = false;
      } else {
        // reload the bounded data and update the current object
        //v3.0: renamed -> reloadData();
        doReport();
      }
      
      dctl.getCreator().setProperty("show.message.popup", old);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
          "Không thể thực hiện phương thức {0}.{1}({2})", this, "run", "");
    }
  }
  
  /**
   * @effects 
   *  reload the data bounded to the bindable data fields of this, and 
   *  update the current object
   */
  protected void doReport() throws ConstraintViolationException, DataSourceException {
    DataController<ExecutableReport> dctl = getRootDataController();

    ExecutableReport report = dctl.getCurrentObject();
    
    // Re-run report to retrieve data from 
    // the data source
    report.run(getDodm());
    
    // update the report form
    dctl.updateGUI(null);
    
    // only need to refresh (below) if there are extra fields not included among 
    // the bindable fields above (because these were already freshed by the reload above)
    // dctl.refresh();
    
    // v3.0: support auto-export (to export result and show it to user)
    if (isAutoExport()) {
      DataController reportDctl = getRootDataController();
      
      try {
        //v3.1: wait until report is completed updated before running export
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
  }
  
  @Override
  public void refresh() {
    DataController dctl = getRootDataController();
    Object old = dctl.getCreator().setProperty("show.message.popup", false);
    
    try {
      doReport();
    } catch (ApplicationRuntimeException | ApplicationException e) {
      InfoCode code = (e instanceof ApplicationRuntimeException) ? 
          ((ApplicationRuntimeException) e).getCode() :
          ((ApplicationException) e).getCode();
      displayError(code, e);
    }
    
    dctl.getCreator().setProperty("show.message.popup", old);
  }
}
