package jda.modules.mbsl.controller.command;

import jda.modules.common.concurrency.Task;
import jda.modules.common.concurrency.TaskManager;
import jda.modules.common.concurrency.Task.TaskName;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mbsl.model.ActivityModel;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  A <b>full-featured</b> {@link DataControllerCommand} that is used in an activity module to 
 *  create an activity object and execute the activity model using this object as input. 
 *  
 *  <p>This is used when the activity class does carry additional environment input needed to execute 
 *  the graph.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class CreateAndExecActivityCommand<C> extends ExecActivityCommand {

  /**
   * @effects 
   *
   * @version 
   */
  public CreateAndExecActivityCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  /**
   * @effects 
   *  create {@link #activityModel} (if not already) and execute it
   * @version 
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController dctl = getDataController();

    // create activity object
    Object activityObj = dctl.createObject();
        
    // create the activity model (if not already)
    initActivityModel(new Object[] {activityObj}); 
    
    // run activity model
    runActivityModel();
  }
}
