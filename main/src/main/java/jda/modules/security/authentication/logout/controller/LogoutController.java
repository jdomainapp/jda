package jda.modules.security.authentication.logout.controller;

import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.security.authentication.controller.SecurityController;
import jda.modules.security.def.Security;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.composite.RunComponent;
import jda.mosa.controller.assets.util.MethodName;

/**
 * A sub-class of {@see CompositeController} to handle the logout function
 * 
 * @author dmle
 * 
 */
public class LogoutController extends CompositeController {
  public LogoutController(DODMBasic schema, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(schema, module,moduleGui, parent, config);

    // initialise the exec run
    SecurityController secCtl = (SecurityController) 
        parent.lookUp(Security.class);

    RunComponent comp = new RunComponent(secCtl,
        MethodName.logout.name(),
        null);
    init(comp);
  }
}
