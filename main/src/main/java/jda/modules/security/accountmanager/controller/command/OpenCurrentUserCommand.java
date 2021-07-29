package jda.modules.security.accountmanager.controller.command;

import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.security.def.DomainUser;
import jda.mosa.controller.Controller;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.controller.assets.util.MessageCode;

/**
 * @overview 
 *  A {@link DataControllerCommand} that customise the {@link LAName#Open} operation which 
 *  retrieves the currently logged in user and display it on the object form.
 *   
 * @author dmle
 * @version 3.2
 */
public class OpenCurrentUserCommand<C> extends DataControllerCommand<C> {

  public OpenCurrentUserCommand(DataController<C> dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    // retrieves the currently logged in user and display it on the object form.
    DataController dctl = getDataController();
    
    Controller ctl = (Controller)dctl.getCreator();
    
    DomainUser user = ctl.getDomainUser();
    if (user != null) {
      // display user
      dctl.setCurrentObject(user, true);
    } else {
      // display error message
      ctl.displayErrorFromCode(MessageCode.ERROR_NO_DOMAIN_USER, dctl);
    }
  }

}
