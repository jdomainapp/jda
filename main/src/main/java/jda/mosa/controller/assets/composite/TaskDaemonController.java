package jda.mosa.controller.assets.composite;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.MethodName;

/**
 * @overview
 *    A sub-type of {@link TaskActiveController} that represents a daemon task, i.e. 
 *    runs in the background possibly without needing a GUI. 
 *    If a GUI does become available (possibly at some point after the controller has been run), then
 *    the controller may interact with it in the usual way. 
 *    
 *    <p>One key feature of the controller is the ability to (actively) perform task without 
 *    requiring any input from the user. An example of such task is to create a new domain object 
 *    using input from another source, for example, from the configuration data or from another system.
 *    
 *    <p>Thus, the task behaviour of this is more restrictive than {@link TaskActiveController} in that it 
 *    does not allow the user to specify input for the object in the sub-sequent runs. 
 *    The GUI is effectively <b>passive</b> in that it merely displays the object state to the user.
 *    
 * @author dmle
 * @version 2.8
 */
public //v3.0: abstract 
  class TaskDaemonController<C> extends TaskActiveController<C> {

  public TaskDaemonController(DODMBasic dodm, ApplicationModule module,
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

    // node: (actively) create object (without needing user input)
    comp = new RunComponent(this, MethodName.createObjectActively.name(), null);
    comp.setSingleRun(true); // single run
    n = add(comp,n);
    
//    // add a component to wait for object to be created (first time) or updated (subsequently by the user)
//    // this is repeated for each run
//    comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
//    n = add(comp, n);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    refreshStartNode = add(comp,n);
    
    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] { Object.class });
    refreshStopNode = add(comp,refreshStartNode);
  }
}
