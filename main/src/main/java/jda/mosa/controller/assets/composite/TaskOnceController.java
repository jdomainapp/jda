package jda.mosa.controller.assets.composite;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.util.events.ChangeListener;

/**
 * @overvew
 *  A sub-type of {@link TaskController} that executes once (until the next run).
 *
 *  This differs from {@link TaskController}, which performs one run and restart prompting the user for the next run.
 *  
 * @author dmle
 */
@Deprecated // not used
public abstract class TaskOnceController<C> extends TaskController<C> implements ChangeListener {

  public TaskOnceController(DODMBasic dodm, ApplicationModule module,
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
    comp = new RunComponent(dctl,MethodName.newObject.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    // add a component to create object once (first-time) 
    //comp = new RunComponent(dctl, AppState.Created, AppState.Updated);
    comp = new RunComponent(dctl, AppState.Created, MethodName.createObject.name(), null);
    comp.setSingleRun(true);
    Node m1 = add(comp, n);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    refreshStartNode = add(comp,m1);

    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doTask.name(), 
        new Class[] { Object.class });
    refreshStopNode = add(comp,refreshStartNode);

    // reset when finished
    comp = new RunComponent(this, MethodName.resetTree.name(), null);
    add(comp,refreshStartNode);
  }
}
