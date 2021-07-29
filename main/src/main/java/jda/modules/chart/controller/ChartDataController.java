package jda.modules.chart.controller;

import static jda.modules.mccl.conceptmodel.controller.LAName.Chart;

import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;

import jda.modules.chart.model.ChartWrapper;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.DataPanelController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.JDataContainer;

/**
 * @overview
 *  A data controller responsible for creating chart objects
 * @author dmle
 */
public class ChartDataController<C> extends DataPanelController<C> {

  public ChartDataController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent)
      throws NotPossibleException {
    super(creator, user, parent);
  }

  /**
   * @effects 
   *  if cmd = Chart
   *    return false
   *  else 
   *    return super.actionPerformable(cmd) 
   */
  @Override
  public boolean actionPerformable(String cmd) {
    if (cmd.equals(Chart.name())) {
      return true;
    } else{
      //return super.actionPerformable(cmd);
      return false;
    }
  }

  @Override
  protected boolean actionPerformedPreConditions(ActionEvent e) {
    return true;
  }

  @Override
  protected void actionPerformed(String cmd) {
    // only perform Chart
    try {
      createChart();
    } catch (Exception ex) {
      controller.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, this, ex, cmd);
    }
  }

  /**
   * @effects create and display a chart object from the data contained in
   *          this.dataContainer
   */
  protected void createChart() throws Exception {
//    ControllerBasic ctl = controller.getMainController().lookUpWithPermission(ChartWrapper.class);
//    if (ctl != null) {
      // create a new chart wrapper object using the controller, and use it
      // to create the chart. Then show the GUI for the user to view and edit
      // chart options

      ControllerBasic ctl = getCreator();
      
      // v2.6.2c: call preRunconfigure
      ctl.preRunConfigureGUI();

      DataController chartDctl = this; //ctl.getRootDataController();
      
      chartDctl.newObject();
      // default chart title is named after the data container's label
      String title = dataContainer.getLabel();
      if (title == null) {
        title = getUser().getGUI().getTitle();
      }

      chartDctl.setMutableState(new Object[] { title,
          ChartWrapper.ChartType.values()[0], Boolean.TRUE });
      chartDctl.createObject();

      // set the data controller to create actual chart object
      ChartWrapper chartWrapper = (ChartWrapper) chartDctl.getCurrentObject();
      chartWrapper.setDataCtl(this);

      // v2.7.3: update GUI to show chart object
      chartDctl.updateGUI(null);

      // display gui
      ctl.showGUI();
//    }
  }

  /**
   * @effects 
   *  create a report object from the report input fields and fire state change
   *  event 
   */
  @Override
  public C createObject() throws DataSourceException {
    LinkedHashMap<DAttr,Object> vals = dataContainer.getUserSpecifiedState();

    // v2.6.4.a: use the new API
    //currentObj = (C) schema.createObject(cls, vals.values().toArray());
    ControllerBasic controller = getCreator();
    
    Class cls = controller.getDomainClass();
    DOMBasic dom = controller.getDodm().getDom();
    
    Tuple2<Oid,Object> t = dom.createObject(cls, vals.values().toArray());
    currentObj = (C) t.getSecond();
    
    // update gui to display the chart
    /* v2.7.3: this call is not effective because the actual chart object has not been 
     * created at this stage
    updateGUI(null);
    */

    String mesg = null;
//      if (properties.getBooleanValue("show.message.popup", true)) {
//        mesg = displayMessage(MessageCode.CHART_OBJECT_CREATED, "Tạo biểu đồ {0}", currentObj);
//      }

    AppState state = AppState.Created;
    setCurrentState(state, mesg);
    
    return currentObj;
  }

  @Override
  public void onCancel() {
    // v5.1c: added this 
    onCancelGUI();
    // v5.1c: end
    
    clearGUI();
  }
} // end ChartController