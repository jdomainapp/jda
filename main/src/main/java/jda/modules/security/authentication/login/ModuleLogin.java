package jda.modules.security.authentication.login;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.authentication.ModuleSecurity;
import jda.modules.security.authentication.login.controller.LoginController;
import jda.modules.security.def.LoginUser;
import jda.modules.security.def.Role;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.View;

@ModuleDescriptor(
    /*Note: this name must match the logical action name so that it can be looked up by the security controller*/ 
name="ModuleLogin",
modelDesc=@ModelDesc(
    model=LoginUser.class
),
viewDesc=@ViewDesc(
    formTitle="Đăng nhập", 
    imageIcon="login.gif",
    viewType=RegionType.DataLogin,
    parent=RegionName.File,
    view=View.class,
    // v2.7.4: middle of screen
    topX=0.5,topY=0.5, 
    resizable=false,
    relocatable=false
),
//controller=domainapp.controller.security.Login.class,
controllerDesc=@ControllerDesc(
    controller=LoginController.class),
type=ModuleType.Security,
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
,childModules={
  ModuleSecurity.class
}
)
public class ModuleLogin {
  @AttributeDesc(label="Thông tin tài khoản")
  private String title;
  
  @AttributeDesc(label="Tên truy cập",alignX=AlignmentX.Center)
  private String login;
  
  @AttributeDesc(label="Mật khẩu",alignX=AlignmentX.Center)
  private String password;
  
  @AttributeDesc(label="Vai trò")
  private Role role;
}

//@ModuleDescriptor(
//    name="Login",
//    label="Đăng nhập",
//    imageIcon="login.gif",
//    controller=domainapp.controller.security.Login.class,
//    parent=RegionName.File
//)
//// no GUI
//public class Login {
//  // empty
//}
