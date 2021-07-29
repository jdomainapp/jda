package jda.mosa.controller.assets.datacontroller.command;

import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.MessageCode;

public abstract class CopyObjectDataControllerCommand<C> extends DataControllerCommand {

  public CopyObjectDataControllerCommand(DataController dctl) {
    super(dctl);
  }
  
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController dctl = getDataController();

    // debug
    //System.out.println(getDataController() + "." + this.getClass().getSimpleName());

    if (args.length == 0) {
      // invoked on the source container 
      
      // get the selected objects of the container
      Collection selectedObjs = dctl.getSelectedObjects();

      if (selectedObjs != null) {
        boolean confirmed = dctl.getCreator().displayConfirmFromCode(MessageCode.CONFIRM_COPY_OBJECTS, dctl, 
            selectedObjs.size());

        if (confirmed) { 
          // get the target data controller of this container
          DataController targetDctl = getTargetDataController(dctl);
            
          if (targetDctl == null)
            throw new NotPossibleException(NotPossibleException.Code.NO_TARGET_OBJECT_FORM_SPECIFIED, new Object[] {dctl});
          
          // get handle copy command of the target container
          DataControllerCommand targetCmd = targetDctl.lookUpCommand("HandleCopyObject");
          
          // call handle copy command with the selected objects
          targetCmd.execute(dctl, selectedObjs.toArray());
        }
      } else {
        // no objects selected
        dctl.getCreator().displayMessageFromCode(MessageCode.NO_OBJECTS_SELECTED, dctl);
      }
    } else {
      handleCopyObjectsAtTarget(src, args);
      
      updateSourceOnObjectCopied(src, args);
    }
  }

  /**
   * @effects
   *  remove copied objects from the <tt>src</tt>'s buffer
   */
  protected void updateSourceOnObjectCopied(DataController src, Object...objects) throws Exception {
    // update the source container (to exclude the input objects)
    // IMPORTANT: must ONLY delete objects from the buffer (not from the data source)
    
    src.deleteObjectsFromBuffer(objects, false);      
  }

  /**
   * This method is invoked by target data controller to 'paste' the input objects into its buffer
   * 
   * @effects
   *  create target objects from input objects specified in <tt>args</tt>
   */
  protected abstract void handleCopyObjectsAtTarget(DataController src, Object...args) throws Exception;

}
