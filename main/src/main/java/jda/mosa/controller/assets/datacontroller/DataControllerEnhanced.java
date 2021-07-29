package jda.mosa.controller.assets.datacontroller;

import java.util.Map;

import jda.modules.common.concurrency.TaskManager;
import jda.modules.common.concurrency.Task.TaskName;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

public abstract class DataControllerEnhanced<C> extends DataController<C> {

  public DataControllerEnhanced(ControllerBasic creator, ControllerBasic user,
      DataController parent) {
    super(creator, user, parent);
  }

  @Override
  public void onOpenAll(Map objects) throws NotPossibleException,
      NotFoundException {
    // TODO Auto-generated method stub
  }

  @Override
  public void onCancel() {
    // TODO Auto-generated method stub
  }

  @Override
  protected void createChildAssociatedObjects() {
    // do this in the background
    TaskManager taskMan = getTaskManager();
    
    RunCreateChildAssociatedObjects t = (RunCreateChildAssociatedObjects) 
        taskMan.getTask(TaskName.CreateAssociatedChildObjects);
    if (t == null) {  
      // not yet created this task
      t = new RunCreateChildAssociatedObjects();
      
      taskMan.registerTask(t);
    }
    
    // run task
    //taskMan.runAndWait(t);
    taskMan.run(t);
    taskMan.waitFor(t, RunCreateChildAssociatedObjects.MAX_RUN_TIME);
    
    // when finished, display errors if any
    t.displayErrorsIfAny();
  }
}
