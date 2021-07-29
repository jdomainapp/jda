package jda.mosa.controller.assets.composite;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.command.ControllerCommand;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeListener;

/**
 * @overvew
 *  A sub-type of {@link CompositeController} that represents a well-defined task.
 *
 * @author dmle
 * 
 * @version 
 *  3.0: support controller command 
 */
public abstract class TaskController<C> extends CompositeController implements ChangeListener {

  protected Node<RunComponent> refreshStartNode;
  protected Node<RunComponent> refreshStopNode;

  //v3.0:      
  private ControllerCommand ctlCmd;

  public TaskController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
    ctlCmd = lookUpCommand(PropertyName.controller_command);
  }

//  @Override
//  protected void initModule() throws NotPossibleException {
//    super.initModule();
//    // add customisation here if needed
//  }

  @Override
  protected void initRunTree() throws NotPossibleException {
    setRestartPolicy(
        RestartPolicy.Node
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
    comp = new RunComponent(dctl,MethodName.newObject.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    // add a component to wait for the user to create or update object
    // this is repeated for each run
    comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    Node m1 = add(comp, n);

//    // use this to clear the children (if any)
//    comp = new RunComponent(dctl,
//        MethodName.clearChildren.name(),
//        null);
//    add(comp,m1);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    refreshStartNode = add(comp,m1);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] { Object.class });
    refreshStopNode = add(comp,refreshStartNode);

    // restart when finished 
    comp = new RunComponent(this, MethodName.restart.name(), null);
    add(comp,refreshStartNode);
  }
  
  /**
   * @effects 
   *  prepare this for running
   * 
   * @see {@link MethodName#init}
   * @version 2.7.4
   */
  public void init() {
    createGUIIfNotAlready();
  }

  /**
   * @effects 
   * clear the current report object and re-run it
   */
  @Override
  public void refresh() {
    // runs the sub-tree of the execution tree that is concerned with
    // running the report. 
    // IMPORTANT: this sub-tree must not include the final "restart" Node.
    if (refreshStartNode != null && refreshStopNode != null)
      runASubTree(refreshStartNode, refreshStopNode);
  }
  
  @Override
  protected void onTerminateRunOnError() {
    // reset the tree nodes to make them ready for next execution and restart them
    // this is needed for the tree to be executed again when user clicks the "Update" button 
    // (without using "Refresh")
    // restart will stop at the node that waits for the user to either hit the Update or Create button
    // this restarting happens in the background without the user having to know
    super.restart();
  }
  
  @Override
  public void preRun() throws ApplicationRuntimeException {
    super.preRun();
    
    if (ctlCmd != null) {
      ctlCmd.preRun();
    }
  }
  
  /**
   * @effects 
   *  perform the task using <tt>object</tt>
   */
  //v3.0: public abstract void doTask(C object) throws ApplicationRuntimeException, DataSourceException;
  public void doTask(C object) throws ApplicationRuntimeException,
  DataSourceException {
//    if (cmdDoTask == null)
//      throw new NotPossibleException(NotPossibleException.Code.NO_COMMAND, 
//          new Object[] {this.getClass().getSimpleName(), PropertyName.ctl_command_doTask});
      
    if (ctlCmd != null) {
      // run command 
      ctlCmd.doTask();
    } else {
      logError(this.getClass().getSimpleName()+".doTask(): method not implemented", null);
    }
  }
  
  @Override // ChangeListener
  public void stateChanged(ChangeEvent e) {
    if (ctlCmd != null) {
      // this command is optional
      ctlCmd.stateChanged();
    } else {
      logError(this.getClass().getSimpleName()+".stateChanged(): method not implemented", null);
    }
  }

//  /**
//   * @effects 
//   *  if exists the controller command whose name is <tt>cmdName</tt>
//   *    return it
//   *  else
//   *    return <tt>null</tt>
//   */
/* v3.2: moved to ControllerBasic
   protected ControllerCommand lookUpCommand(PropertyName cmdName) {
    ControllerConfig ctlCfg = getControllerConfig();
    
    Object cmdClsObj = ctlCfg.getControllerCommand(cmdName);
    
    ControllerCommand cmd = null;
    if (cmdClsObj != null && cmdClsObj instanceof Class) {
      Class<? extends ControllerCommand> cmdCls = (Class) cmdClsObj; 
      cmd = ControllerCommand.createInstance(cmdCls, this);
    }
    
    return cmd;
  }*/
  

  /**
   * @effects <pre>
   *  if exists a {@link ControllerCommand} specified for this
   *    return it
   *  else
   *    return <tt>null</tt>
   *    </pre>
   */
  protected ControllerCommand getCtlCmd() {
    return ctlCmd;
  }
}
