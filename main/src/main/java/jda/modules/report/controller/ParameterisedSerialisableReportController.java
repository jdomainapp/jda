package jda.modules.report.controller;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.report.model.Report;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

/**
 * @overview 
 *  A sub-type of <tt>ParameterisedReportController</tt> that represents controlers for parameterised 
 *  reports whose objects are stored in the data source. A parameterised report is a report that needs 
 *  the user to input some data before it can be run.
 *  
 * @author dmle
 */
public class ParameterisedSerialisableReportController extends ParameterisedSimpleReportController {
  
  public ParameterisedSerialisableReportController(DODMBasic schema, ApplicationModule module,
    Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }
  
  @Override
  protected void initRunTree() throws NotPossibleException {
    /*
     * This run-tree differs from the super-type in that it enables the user
     * to Open existing reports  
     */
    setRestartPolicy(
        RestartPolicy.None
        );
    
    setProperty("show.message.popup", Boolean.FALSE);
    DataController dctl = getRootDataController();    
    final Class reportClass = Report.class;
    
    // add a node that runs once for all the subsequent runs of
    // this controller to initialise the resources
    RunComponent comp = new RunComponent(this, MethodName.initReport.name(), null);
    comp.setSingleRun(true);
    Node n = init(comp);

    //////////// subtree(n):
    
    // show GUI node
    comp = new RunComponent(this, MethodName.showGUI.name(), null);
    n = add(comp,n);

    // add create new object component (run once) 
    comp = new RunComponent(dctl,MethodName.openAndWait.name(),
        null);
    comp.setSingleRun(true);
    add(comp, n);

    ///// subtree of n: 
    // to move to the first report object (after open) and execute it
    // this sub-tree is executed only once
    comp = new RunComponent(dctl,MethodName.firstAndWait.name(),
        null);
    comp.setSingleRun(true);
    Node runFirstReport = add(comp, n);
    
    // obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    comp.setSingleRun(true);
    Node<RunComponent> m = add(comp,runFirstReport);

    // do the report
    comp = new RunComponent(this, MethodName.doReport.name(), 
        new Class[] {reportClass});
    comp.setSingleRun(true);
    Node endOfFirstReport = add(comp,m);
    
    ///// subtree of subtree(1): starts at the end of subtree(1), to run sub-sequent reports    
    // add a component to wait for the user to create or update object
    // this is repeated for each run
    comp = new RunComponent(dctl, 
        AppState.Created, // after a new report has been created 
        AppState.Updated, // after the current report has been updated
        AppState.First,    // after browsing to an existing report
        AppState.Previous,
        AppState.Next,
        AppState.Last
        );   
    Node runSubsequentReport = add(comp, endOfFirstReport);

    // v2.6.4b: to clear the children
    comp = new RunComponent(dctl,
        MethodName.clearChildren.name(),
        null);
    add(comp,runSubsequentReport);

    // add a node to obtain the created object
    comp = new RunComponent(dctl,
        MethodName.getCurrentObject.name(),
        null);
    m = add(comp,runSubsequentReport);
    refreshStartNode = m;
    
    // add a node to do the rest of the report
    comp = new RunComponent(this, MethodName.doReport.name(), 
        new Class[] {reportClass});
    refreshStopNode = add(comp,m);

    // restart when finished 
    comp = new RunComponent(this, MethodName.restart.name(), null);
    add(comp,m);
  }
}
