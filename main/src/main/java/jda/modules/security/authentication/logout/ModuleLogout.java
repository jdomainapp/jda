package jda.modules.security.authentication.logout;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.authentication.ModuleSecurity;
import jda.modules.security.authentication.logout.controller.LogoutController;
import jda.modules.setup.commands.CopyResourceFilesCommand;

@ModuleDescriptor(
/*Note: this name must match the logical action name so that it can be looked up by the security controller*/ 
name="ModuleLogout",
viewDesc=@ViewDesc(
    formTitle="Đăng xuất",
    imageIcon="logout.gif",
    parent=RegionName.File,
    on=false  // v2.7.4: added this to avoid having to create a GUI config
),
//controller=domainapp.controller.security.Logout.class,
controllerDesc=@ControllerDesc(
    controller=LogoutController.class),
type=ModuleType.Security
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
,childModules={
    ModuleSecurity.class
  }
)
// no GUI
public class ModuleLogout {
  // empty
}
