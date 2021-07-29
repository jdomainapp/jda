package jda.modules.report.controller.stats;

import java.awt.Component;

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
import jda.modules.report.controller.ExecutableReportController;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.assets.datafields.JBindableField;

/**
 * @overview
 *    A specialised Controller used to control statistical reports.
 *     
 *    <p>This is almost identical to {@link ExecutableReportController}, except for the behaviour 
 *     of {@link #reloadData()}, which refresh the bounded data fields before redisplaying them 
 *     on the report view. 
 *     
 * @author dmle
 * 
 * @version 2.7.4
 */
public class ReportStatisticalController<C> extends ControllerBasic {
  private boolean firstTime;
  
  public ReportStatisticalController(DODMBasic schema,
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
      
      DataController dctl = getRootDataController();
      Object old = dctl.getCreator().setProperty("show.message.popup", false);

      if (firstTime) {
        // create a new statistics object
        dctl.createObject();
        
        firstTime = false;
      } else {
        // reload the bounded data and update the current object
        reloadData();
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
  private void reloadData() throws ConstraintViolationException, DataSourceException {
    DataController dctl = getRootDataController();
    Component[] comps = dctl.getDataContainer().getComponents(null);
    for (Component c: comps) {
      ((JBindableField)c).reloadBoundedData();
    }
    
    dctl.updateObject();
    
    // only need to refresh (below) if there are extra fields not included among 
    // the bindable fields above (because these were already freshed by the reload above)
    // dctl.refresh();
  }
  
  @Override
  public void refresh() {
    DataController dctl = getRootDataController();
    Object old = dctl.getCreator().setProperty("show.message.popup", false);
    
    try {
      reloadData();
    } catch (ApplicationRuntimeException | ApplicationException e) {
      InfoCode code = (e instanceof ApplicationRuntimeException) ? 
          ((ApplicationRuntimeException) e).getCode() :
          ((ApplicationException) e).getCode();
      displayError(code, e);
    }
    
    dctl.getCreator().setProperty("show.message.popup", old);
  }
}
