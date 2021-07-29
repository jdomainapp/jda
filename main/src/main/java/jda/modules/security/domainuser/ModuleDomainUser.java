package jda.modules.security.domainuser;

import java.util.Collection;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.UserRole;
import jda.modules.security.role.ModuleRoleViewer;
import jda.modules.security.userrole.ModuleUserRole;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.tables.JObjectTable;

/**
 * @overview
 *  A standard module for {@link DomainUser}.
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleDomainUser",
modelDesc=@ModelDesc(
    model=DomainUser.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Người dùng",
    formTitle="Quản lý người dùng", 
    imageIcon="frmDomainUser.png",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TabLayoutBuilder.class,
    topX=0.5,topY=0.0,widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true
),
type=ModuleType.System,
isPrimary=true,
childModules={ModuleUserRole.class, ModuleRoleViewer.class}
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleDomainUser {
  @AttributeDesc(label="Người dùng")
  private String title;

  @AttributeDesc(label="Họ và tên",alignX=AlignmentX.Center)
  private String name;

  @AttributeDesc(label="Tên đăng nhập",alignX=AlignmentX.Center)
  private String login;
  
  @AttributeDesc(label="Mật khẩu",alignX=AlignmentX.Center)
  private String password;
  
  @AttributeDesc(label="Vai trò",
      type=JObjectTable.class,
      controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O)
  )
  private Collection<UserRole> roles;
  
}