package jda.modules.setup.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.MasterSetUp;
import jda.modules.setup.model.SetUpBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.TaskActiveController;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.view.assets.JDataContainer;
import jda.util.events.ChangeEvent;

public class SetUpController extends TaskActiveController<MasterSetUp> {

  public SetUpController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
  }

  @Override
  public void doTask(MasterSetUp su) throws ApplicationRuntimeException,
      DataSourceException {
    /*
      run/re-run the specified set-up command 
     */

    //
    DataController<MasterSetUp> dctl = getRootDataController();

    // confirm with user
    SetUpBasic targetSetUp = su.getTargetSetUp();
    Cmd command = su.getTargetCommand();

    if (command == null) {
      displayErrorFromCode(MessageCode.ERROR_NO_SETUP_COMMAND, dctl);
    } else {
      boolean ok = displayConfirmFromCode(MessageCode.CONFIRM_TASK_RUN_SETUP, 
          dctl, command.getName(), targetSetUp.getAppName());    
  
      if (ok) {
        try {
          // clear existing set-up resources (if any)
          targetSetUp.clear();
          
          // run command
          targetSetUp.run(command, null);
          
          displayMessageFromCode(MessageCode.SETUP_COMPLETED, dctl, command);
        } catch (IOException e) {
          targetSetUp.log(jda.modules.setup.model.SetUpBasic.MessageCode.UNDEFINED, 
              "Error: " + e.getMessage());
          
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, e, new Object[] {command});
        } catch (Exception e) {
          // log error
          targetSetUp.log(jda.modules.setup.model.SetUpBasic.MessageCode.UNDEFINED, 
              ToolkitIO.getStackTrace(e, "utf-8"));
          
          throw e;
        }
      }
    }
  }

  @Override // TaskActiveController
  public void createObjectActively() throws ApplicationRuntimeException,
      DataSourceException {
    DataController<MasterSetUp> dctl = getRootDataController();

    // there is no need to create another set up here because it was already created
    // when the set-up program is run. So, just get it from the config and use...
    Configuration masterSetUpConfig = getConfig();
    
    // get the 'master' SetUpSetUp object (currently used to run this module)
    MasterSetUp su = (MasterSetUp) masterSetUpConfig.getSetUpInstance(); 
    
    // register this as change listener of the object
    /*v3.0: use target setup (below) 
    su.addChangeListener(this);
    */

    // create target configuration from the input SetUp class (1st argument)
    Class targetSetUpCls= su.getArg(0, Class.class);
    
    if (!SetUpBasic.class.isAssignableFrom(targetSetUpCls)) {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT_SETUP_CLASS, new Object[] {targetSetUpCls});
    }
    
    SetUpBasic targetSetUp = SetUpBasic.createInstance(targetSetUpCls);
    
    // args for targetSetup starts from the 2nd argument
    targetSetUp.setArgs(su.getArgs(1));
    
    targetSetUp.createApplicationConfiguration();
    
    // set target set up 
    su.setTargetSetUp(targetSetUp);

    //v3.0: register this as change listener of the object
    // this is needed to obtain set-up status information and display it on the GUI
    targetSetUp.addChangeListener(this);
    
    Configuration targetConfig = su.getTargetConfig();

    if (targetConfig == null) {
      // error:
      throw new NotPossibleException(NotPossibleException.Code.NO_CONFIGURATION);
    }

    // update GUI
    dctl.setCurrentObject(su, true);
    //dctl.setCurrentState(AppState.Created);
    
    // display configuration on the GUI (manually)
    
    // register config-related objects
    // need to do this here b/c master setup and target set-up use different DODM instances
    DOMBasic dom = getDodm().getDom();
    dom.addObjectWithAssociates(targetConfig, false);
    
    Collection<Configuration> configBuffer = new ArrayList<>();
    configBuffer.add(targetConfig);
    
    JDataContainer configDcont = getChildDataContainer(MasterSetUp.class, Configuration.class);
    DataController configDctl = configDcont.getController();
    
    configDctl.openObjects(configBuffer, false);
    
    //activateDataContainer(configDcont);
    showDataContainer(configDcont);
    
    getGUI().updateSizeOnComponentChange();
  }
  
  @Override // ChangeListener
  public void stateChanged(ChangeEvent e) {
    //  i. update GUI to show the status information
    DataController<MasterSetUp> dctl = getRootDataController();
    
    // v3.0: without updating the children 
    //dctl.updateGUI(true);
    
    // v3.0: only update if there is new status info
    // this is needed b/c state change is invoked asychronously and thus 
    //  may well be after set-up has already been finished
    // 
    MasterSetUp su = dctl.getCurrentObject();
    if (su.getNewTargetStatus()) {
      dctl.updateGUI(null);      
    }

    //getGUI().updateSizeOnComponentChange();
  }

}
