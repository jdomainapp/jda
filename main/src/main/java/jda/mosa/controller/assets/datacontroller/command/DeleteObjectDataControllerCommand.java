package jda.mosa.controller.assets.datacontroller.command;

import java.util.Collection;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.MessageCode;

public class DeleteObjectDataControllerCommand<C> extends DataControllerCommand {

  public DeleteObjectDataControllerCommand(DataController dctl) {
    super(dctl);
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    // perform the normal deletion by invoking dctl.delete()
    DataController dctl = getDataController();
    
    Collection selectedObjs = dctl.getSelectedObjects();
    
    if (selectedObjs != null) {
      //confirm with user
    
      boolean confirmed = dctl.getCreator().displayConfirmFromCode(MessageCode.CONFIRM_DELETE_OBJECTS, dctl, 
          selectedObjs.size());

      if (confirmed) { 
        // to delete
        
        // turn-off message pop-up
        ControllerBasic ctl = dctl.getCreator();
        Object messageState = ctl.setProperty("show.message.popup", false);
//        
//        for (Object o : selectedObjs) {
//          dctl.deleteObject(o, toConfirm);
//        }
//        
        deleteSelectedObjects(dctl, selectedObjs);
        
        ctl.setProperty("show.message.popup", messageState);
        // reset property
        
        // update the target
        // get the target data controller (whose model and view are affected by deletion) 
        DataController targetDctl = getTargetDataController(dctl);
    
        if (targetDctl != null) {
          updateTargetOnDeleted(targetDctl, selectedObjs);
        }
      }
    }
  }

  /**
   * @effects 
   *  delete <tt>selectedObjs</tt> from <tt>dctl</tt>
   */
  protected void deleteSelectedObjects(DataController dctl, Collection selectedObjs) throws NotPossibleException, NotFoundException, DataSourceException {

    boolean toConfirm = false;

    for (Object o : selectedObjs) {
      dctl.deleteObject(o, toConfirm);
    }
  }

  protected void updateTargetOnDeleted(DataController targetDctl, Object...objects) throws Exception {
    // this only works if objects have the same type as that managed by the target
    //  targetDctl.addObjectToBuffer(objects);
    targetDctl.reopen();
  }

}
