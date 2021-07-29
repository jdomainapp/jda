package jda.mosa.controller.assets.composite;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.command.ControllerCommand;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

/**
 * @overvew
 *  An <b>active</b> {@link TaskController} in that it <i>actively</i> creates an object the first time that is run rather 
 *  than relying on the user input every time. In the sub-sequent runs, the user can still update the object state and 
 *  re-run the task with the updated object. 
 *  
 *  <p>This controller is useful for tasks that requires a domain object to get started, but the input for this object 
 *  come from sources other than the user.  
 *  
 * @author dmle
 */
public // v3.0: abstract 
  class TaskActiveController<C> extends TaskController<C> {
  
  private Node doTaskRootNode;
  private Node doTaskLoopBackNode;

  public TaskActiveController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
  }

  @Override
  protected void initRunTree() throws NotPossibleException {
    setRestartPolicy(
        RestartPolicy.None
        );
    
    setProperty("show.message.popup", Boolean.FALSE);
    DataController dctl = getRootDataController();    
    //final Class reportClass = Report.class;
    //final Class domainClass = getDomainClass();
    
    // add a node that runs once for all the subsequent runs of
    // this controller to initialise the resources
    RunComponent comp;
    Node n;

    // v2.7.4: init node
    comp = new RunComponent(this, MethodName.init.name(), null);
    n = init(comp);

    // preparation node
    comp = new RunComponent(this, MethodName.preRun.name(), null);
    n = add(comp,n);
    
    // show GUI node
    comp = new RunComponent(this, MethodName.showGUI.name(), null);
    n = add(comp,n);

    // add create new object component (run once) 
    comp = new RunComponent(this,MethodName.createObjectActively.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    // v3.0: an extra node to run the module in the sub-sequent runs. Unlike the branch below it 
    // this is executed immediately after the GUI is activated 
    // and before the user makes any input on it.
    // NOTE: implementation of refresh() must (1) if this is the first run then simply update the GUI, b/c object has just been created by 
    //    createObjectActively (above) and (2) if a subsequent run then check the 
    //    pre-conditions for refresh; if satisfied then it can simply invoke doTask
    // 
    comp = new RunComponent(this,MethodName.refreshOnShown.name(),null);
    add(comp, n);
    
    // add a component to wait for the user to create or update object
    // this node is invoked: 
    //    (1) in the first run (immediately after the above node) and 
    //    (2) after the doTask of a previous run (therefore creating a do-task-loop waiting for an updated input)  
    comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    doTaskRootNode = add(comp, n);
    
//    // use this to clear the children (if any)
//    comp = new RunComponent(dctl,
//        MethodName.clearChildren.name(),
//        null);
//    add(comp,m1);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    Node getCurrObj = add(comp,doTaskRootNode);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] {Object.class});
    Node doTask = add(comp, getCurrObj);

    // the refresh sub-tree
    refreshStartNode = getCurrObj;
    refreshStopNode = doTask;

    // loop back node (back to node m1 (above))
    comp = new RunComponent(this, MethodName.doTaskLoopBack.name(), null);
    doTaskLoopBackNode = add(comp,doTask);
  }
  
  /**
   * <b>IMPORTANT:</b> Overriding methods must have access visibility set to <b><tt>public</tt></b>
   * @effects 
   *  create in this a domain object using input <b>from sources other than the user</b>. 
   */
  //v3.0: protected abstract void createObjectActively() throws ApplicationRuntimeException, DataSourceException;
  public void createObjectActively() throws ApplicationRuntimeException, DataSourceException {
    ControllerCommand ctlCmd = getCtlCmd();
    if (ctlCmd != null) {
      // run command 
      ctlCmd.createObjectActively();
    } else {
      logError(this.getClass().getSimpleName()+".createObjectActively(): method not implemented", null);
    }
  }
  
  public void refreshOnShown() throws ApplicationRuntimeException {
    ControllerCommand ctlCmd = getCtlCmd();
    if (ctlCmd != null) {
      // run command 
      ctlCmd.refreshOnShown();
    } else {
      logError(this.getClass().getSimpleName()+".refreshOnShown(): method not implemented", null);
    }
  }
  
  public void doTaskLoopBack() throws ApplicationRuntimeException {
    // re-run the subtree defined by (doTaskRootNode, doTaskLoopBackNode)
    if (doTaskRootNode != null && doTaskLoopBackNode != null)
      runASubTree(doTaskRootNode, doTaskLoopBackNode);
  }
}
