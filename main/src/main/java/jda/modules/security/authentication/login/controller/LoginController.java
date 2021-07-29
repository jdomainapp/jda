package jda.modules.security.authentication.login.controller;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.tree.Node;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.security.authentication.controller.SecurityController;
import jda.modules.security.def.LoginUser;
import jda.modules.security.def.Security;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

/**
 * A sub-class of {@see CompositeController} to handle the login 
 * 
 * @author dmle
 * 
 */
public class LoginController extends CompositeController {
  public LoginController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);
  }
  
  @Override
  protected void initRunTree() throws NotPossibleException {
    // change restart policy to all to restart the whole tree
    setRestartPolicy(RestartPolicy.All);
    
    /*
     * RUN TREE:
     *  showGUI                              
     *  |--dctl.newObject->states<Created>   
     *  |----secCtl.login
     *  |----hideGUI                         
     *    
     */
    
    // initialise the exec run
    //Controller ctl = Controller.lookUp(LoginUser.class);
    
    // set up the run-time properties
    setProperty("show.message.status", Boolean.FALSE);
    setProperty("show.message.popup", Boolean.FALSE);
    
    // start login gui
    RunComponent comp = new RunComponent(this, MethodName.showGUIAndWait.name(), null);
    Node n = init(comp);
    
    // prompts for new login object
    comp = new RunComponent(getRootDataController(), 
        AppState.Created,
        MethodName.newObject.name(), null);
    n=add(comp,n);

    // process login details
    ControllerBasic mainCtl = getMainController();
    SecurityController secCtl = (SecurityController) mainCtl.lookUp(Security.class);
    
    comp = new RunComponent(secCtl,
        MethodName.login.name(),
        new Class[] { LoginUser.class });
    add(comp,n);
    
    // hide when finished 
    comp = new RunComponent(this, 
        MethodName.hideGUI.name(), null);
    n=add(comp,n);
    /*v2.7.2
    // run other tasks (e.g. display the default child window) 
    comp = new RunComponent(getMainController(), 
        MethodName.runDefaultModule.name(),
        null);
    add(comp,n);
    */    
  }
  
  @Override
  protected void onTerminateRunOnError() {
    // restart at the login form waiting for user to login again
    super.restart();
  }
}
