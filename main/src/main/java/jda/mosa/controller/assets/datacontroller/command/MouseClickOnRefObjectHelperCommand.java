package jda.mosa.controller.assets.datacontroller.command;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.SecurityException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview
 *  A custom helper command to handle mouse-click actions on referenced objects of this module and of the descendant modules.
 *  
 *  <p>Sub-types must override method {@link #lookUpTargetModule(Class)} to specify the target module 
 *  for each domain class for each they wish to customise the mouse-click action. 
 *  
 * @author dmle
 * 
 * @version 3.2
 */
public abstract class MouseClickOnRefObjectHelperCommand<C> extends DataControllerCommand {

  public MouseClickOnRefObjectHelperCommand(DataController dctl) {
    super(dctl);
  }

  /**
   * @requires 
   *  args != null /\ args.length > 0
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    if (args == null || args.length == 0) {
      return;
    }
    
    // the referenced object
    Object refObj = args[0];
    
    // look up the target module of the ref object and use it to display the object
    // if target module not found then use the viewer as the default
    
    Class refClass = refObj.getClass();
    final DataController dctl = getDataController(); 
    final ControllerBasic mainCtl = dctl.getCreator().getMainController();
    
    ControllerBasic targetCtl = null;
    try {
//      if (Action.class == refClass) {
//        // an Action object: use ModuleActionExec
//        targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleActionExec");
//        showBestFit(targetCtl, refObj);
//      } else if (Action4Subject.class == refClass) {
//        // an Action object: use ModuleActionExec
//        targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleAction4SubjectExec");
//        showBestFit(targetCtl, refObj);
//      } else if (Task.class == refClass) {
//        // a Task object
//        targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleTaskExec");
//        showBestFit(targetCtl, refObj);
//      } else if (Task4Subject.class == refClass) {
//        // a Task4Subject object
//        targetCtl = mainCtl.lookUpByModuleWithPermission("ModuleTask4SubjectExec");
//        showBestFit(targetCtl, refObj);
//      } 
      targetCtl = lookUpTargetModule(refClass);
      
      if (targetCtl != null) {
        // target module specified
        showBestFit(targetCtl, refObj);
      } else {
        // other cases: default behaviour
        targetCtl = mainCtl.lookUpViewerWithPermission(refClass);
        showDefault(targetCtl, refObj);
      }
    } catch (Exception ex) {
      dctl.getCreator().logError("Error looking up target handler module for object: " + refObj, ex);
      ex.printStackTrace();
    }
  }

  /**
   * @effects 
   *  return the {@link ControllerBasic} of the module that is used to handle the mouse click 
   *  action for domain objects of <tt>domainCls</tt>; 
   *  or return <tt>null</tt> if no such module is specified
   *  
   *  <p>Throws NotFoundException if the target module is expected but not found; 
   *  SecurityException if user does not have permission to access the target module
   */
  protected abstract ControllerBasic lookUpTargetModule(Class domainCls) throws NotFoundException, SecurityException;

  protected void showBestFit(ControllerBasic targetCtl, Object refObj) {
    // show object on the target controller
    targetCtl.showObjectBestFit(refObj);
  }

  protected void showDefault(ControllerBasic targetCtl, Object refObj) {
    // show object on the target controller
    targetCtl.showObject(refObj);
  }
}
