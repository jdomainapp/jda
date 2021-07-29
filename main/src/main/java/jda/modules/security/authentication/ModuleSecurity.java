package jda.modules.security.authentication;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.authentication.controller.SecurityController;
import jda.modules.security.def.Security;


/**
 * @overview  a view class for {@link jda.modules.security.def.Security}
 */
@ModuleDescriptor(
    name="ModuleSecurity",
    modelDesc=@ModelDesc(
      model=Security.class
    ),
    viewDesc=@ViewDesc(
        on=false,
        formTitle="Bảo mật"
//        imageIcon="security.gif"
    ),    
    //controller=SecurityController.class
    controllerDesc=@ControllerDesc(
        controller=SecurityController.class),
    type=ModuleType.Security    
)
// No GUI
public class ModuleSecurity {
  // empty
}
