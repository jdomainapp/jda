package jda.mosa.controller.assets.command;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview
 *  Represents a command that executes a task needed by some {@link ControllerBasic}. 
 *  
 * @author dmle
 * @version 3.0
 */
public abstract class ControllerCommand {

  private ControllerBasic controller;

  /**
   * @effects 
   *  initialise a new command for the specified <tt>controller</tt>
   */
  public ControllerCommand(ControllerBasic controller) {
    this.controller = controller;
  }
  
  /**
   * @effects 
   *  create and return a <tt>ControllerCommand</tt> whose type is <tt>cmdCls</tt> and that 
   *  is to support <tt>controller</tt>.
   */
  public static ControllerCommand createInstance(Class<? extends ControllerCommand> cmdCls, ControllerBasic controller) throws NotPossibleException {
    try {
      // invoke the constructor to create object 
      ControllerCommand instance = cmdCls.getConstructor(ControllerBasic.class).newInstance(controller);
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {cmdCls.getSimpleName(), controller});
    }
  }

  /**
   * @effects 
   *  return the {@link ControllerBasic} object for which <tt>this</tt> is created to support.
   */
  public ControllerBasic getController() {
    return controller;
  }
  

  /**
   * @effects <pre>
   *  if {@link #controller} has GUI
   *    update object state of the <b>root</b> data controller
   *    update the GUI display 
   *    show GUI
   *  else
   *    do nothing
   *  </pre>
   */
  protected void updateAndShowGUI() {
    if (controller.hasGUI()) {
      DataController rootDctl = controller.getRootDataController();
      
      // GUI might not have been created the first time
      controller.showGUI();  // if not already
      
      // update gui to show current status
      rootDctl.updateGUI(true);
      
      controller.getGUI().updateSizeOnComponentChange();
    }    
  }

  /**
   * @effects 
   *  run this command for {@link #controller}
   *  
   * @throws ApplicationRuntimeException
   */  
  public void preRun() throws ApplicationRuntimeException {
    // for sub-types to override
  }

  /**
   * @effects 
   *  run this command for {@link #controller}
   *  
   * @throws ApplicationRuntimeException
   */  
  public void createObjectActively() throws ApplicationRuntimeException {
    // for sub-types to override
  }

  /**
   * This method is executed immediately after the GUI is activated 
   *  and before the user makes any input on it.
   *  <br><b>NOTE</b>: implementation of refresh() must 
   *     (1) if this is the first run then simply update the GUI, b/c object has just been created by {@link #createObjectActively()} and 
   *     (2) if a subsequent run then check the pre-conditions for refresh; if satisfied then it can simply invoke doTask
   * @effects 
   *  run this command for {@link #controller}
   *  
   * @throws ApplicationRuntimeException
   */  
  public void refreshOnShown() throws ApplicationRuntimeException {
    // for sub-types to override
  }
  
  /**
   * @effects 
   *  run this command for {@link #controller}
   *  
   * @throws ApplicationRuntimeException
   */  
  public void doTask() throws ApplicationRuntimeException {
    // for sub-types to override
  }

  /**
   * @effects 
   *  run this command for {@link #controller}
   *  
   * @throws ApplicationRuntimeException
   */  
  public void stateChanged() throws ApplicationRuntimeException {
    // for sub-types to override
  }
}
