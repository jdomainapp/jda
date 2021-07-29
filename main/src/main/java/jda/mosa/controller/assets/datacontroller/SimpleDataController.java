package jda.mosa.controller.assets.datacontroller;

import java.util.LinkedHashMap;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.model.Oid;
import jda.mosa.view.assets.JDataContainer;

/**
 * A sub-class of {@see DataPanelController} for manipulating light-weight
 * objects.
 * 
 * @author dmle
 * 
 */
public class SimpleDataController<C> extends DataPanelController<C> {

  public SimpleDataController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent)
      throws NotPossibleException {
    super(creator, user, parent);
  }

  /**
   * @effects 
   *  create a object from the input fields and fire state change
   *  event 
   */
  @Override
  public C createObject() throws DataSourceException {
    LinkedHashMap<DAttr,Object> vals = dataContainer.getUserSpecifiedState();

    ControllerBasic controller = getCreator();
    
    // v2.6.4.a: use the new API
    //currentObj = (C) schema.createObject(cls, vals.values().toArray());
    Class cls = controller.getDomainClass();
    DOMBasic dom = controller.getDodm().getDom();
    Tuple2<Oid,Object> t = dom.createObject(cls, vals.values().toArray());
    
    Oid id = t.getFirst();
    currentObj = (C) t.getSecond();

    updateGUI(null);

    // v2.7.2: add object to the browser so that it can be used for exporting
    //if (getMainController().isBrowsingEnabled()) {
    updateBrowserOnCreate(id, currentObj);
    
    // v3.0: last(false,false,false);
    boolean forceToIndex = true;
    last(false,false,false, forceToIndex);
    
    //}
    
    String mesg = null;
    if (controller.getProperties().getBooleanValue("show.message.popup", true)) {
      mesg = controller.displayMessageFromCode(MessageCode.OBJECT_CREATED, 
          this, 
          //new Object[] {currentObj}
          controller.getDomainClassLabel()
          );
    }

    AppState state = AppState.Created;
    setCurrentState(state, mesg);

    // fire the state change event
    if (methodListenerMap.containsKey(state)) {
      fireMethodPerformed(state, currentObj);
    }
    
    return currentObj; // v3.2
  }

  /**
   * @effects 
   *  clear the current report object and re-run it
   */
  @Override
  public void refresh() {
    // v2.8
    //super.refresh();
    
    /*v2.7.2: delegate to the controller
    
    ReportController rptCtl = (ReportController) getCreator();
    
    // re-run the report from the step that finds the report output from the current report object
    rptCtl.refresh();
    */
    //Controller.this.
    getCreator().refresh();
  }
  
  @Override
  public void onCancel() {
    // v5.1c: added this 
    onCancelGUI();
    // v5.1c: end
    
    clearGUI();
  }
} // end SimpleDataController