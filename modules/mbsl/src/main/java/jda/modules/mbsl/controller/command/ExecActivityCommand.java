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
 *  A <b>light-weight</b> {@link DataControllerCommand} that is used in an activity module to 
 *  execute the activity model. 
 *  
 *  <p>This is light-weight in the sense that it does not actually create a new instance of the activity class.
 *  This is useful to conserve memory space, when the activity class does not contain any environment input needed to execute 
 *  the graph.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class ExecActivityCommand<C> extends DataControllerCommand {

  /**
   * @overview 
   *  A runnable {@link Task} that is responsible for executing {@link #activityModel} on a 
   *  thread as part of the {@link TaskManager} of the data controller of this command. 
   *  
   * @author Duc Minh Le (ducmle)
   *
   * @version 4.0
   */
  private static class ExecActivityModelTask extends Task {

    private ModuleService mService;
    private Object[] serviceArgs;
    private ActivityModel activityModel;

    /**
     * @effects 
     *
     * @version 
     */
    public ExecActivityModelTask(TaskName name, 
        ActivityModel activityModel, 
        ModuleService mService, Object[] serviceArgs) {
      super(name);
      this.activityModel = activityModel;
      this.mService = mService;
      this.serviceArgs = serviceArgs;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public void run() {
      activityModel.exec(mService, serviceArgs);
    }

    /**
     * @effects 
     *  set {@link #serviceArgs} = <tt>serviceArgs</tt>
     */
    public void setServiceArgs(Object[] serviceArgs) {
      this.serviceArgs = serviceArgs;
    }

  } /**end {@link ExecActivityModelTask} */

  /**
   * A shared {@link ActivityModel} instance for the activity module that owns the data controller of this.
   * All instances of this command will use this same instance.
   */
  private static ActivityModel activityModel;
  private static ExecActivityModelTask task;

  /**
   * @effects 
   *
   * @version 
   */
  public ExecActivityCommand(DataController dctl) {
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

    // create the activity model (if not already)
//    if (activityModel == null) {
//      Class domainCls = getDomainClass();
//      DSMBasic dsm = getDodm().getDsm();
//
//      activityModel = new ActivityModel("Activity: " + domainCls.getSimpleName(), dsm);
//      activityModel.setActivityCls(domainCls);
//      
//      // register its execution as a runnable task in the task manager of dataController
//      ModuleService mService = dctl;
//      Object[] serviceArgs = null;
//      
//      task = new ExecActivityModelTask(TaskName.ExecuteDomainModel, activityModel, mService, serviceArgs);
//      dctl.getTaskManager().registerTask(task);
//    } 
//    
//    dctl.getTaskManager().run(task);
    
    // create the activity model (if not already)
    if (!isActivityModelInit()) {
      Object[] serviceArgs = null;
      initActivityModel(serviceArgs); 
    }
    
    // run activity model
    runActivityModel();
  }
  
  /**
   * @effects 
   *  if {@link #activityModel} = null
   *    initialise {@link #activityModel} to execute with <tt>serviceArgs</tt> as environment input
   *  else
   *    update {@link #activityModel} to execute with <tt>serviceArgs</tt> as environment input
   */
  protected void initActivityModel(final Object[] serviceArgs) {
    if (activityModel == null) {
      DataController dctl = getDataController();

      Class domainCls = getDomainClass();
      DSMBasic dsm = getDodm().getDsm();
      
      activityModel = new ActivityModel("Activity: " + domainCls.getSimpleName(), dsm);
      activityModel.setActivityCls(domainCls);
      
      // register its execution as a runnable task in the task manager of dataController
      ModuleService mService = dctl;
      
      task = new ExecActivityModelTask(TaskName.ExecuteDomainModel, activityModel, mService, serviceArgs);
      dctl.getTaskManager().registerTask(task);
    } else {
      // update serviceArgs
      task.setServiceArgs(serviceArgs);
    }
    
  }

  /**
   * @effects 
   *  if {@link #activityModel} is initialised
   *    return true
   *  else
   *    return false
   */
  protected static boolean isActivityModelInit() {
    return activityModel != null;
  }

  /**
   * @requires {@link #activityModel} is initialised
   * 
   * @effects 
   *  execute {@link #activityModel} 
   */
  protected void runActivityModel() {
    DataController dctl = getDataController();

    dctl.getTaskManager().run(task);    
  }
}
