package jda.modules.security.userrole;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.Role;
import jda.modules.security.def.UserRole;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  A non-viewable module for {@link UserRole}.
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleUserRole",
modelDesc=@ModelDesc(
    model=UserRole.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Vai trò người dùng",
    formTitle="Quản lý phân người dùng với vai trò" 
//    imageIcon="frmUserRole.png"
//    viewType=Type.Data,
//    parent=RegionName.Tools,
//    view=View.class,
//    topX=0.5,topY=0.5
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
    ),
type=ModuleType.System,
isPrimary=true
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleUserRole {
  @AttributeDesc(label="Vai trò người dùng")
  private String title;

  @AttributeDesc(label="Người dùng", 
      editable=false,
      type=JTextField.class)
  private DomainUser user;
  
  @AttributeDesc(label="Vai trò",
      ref=@Select(clazz=Role.class,attributes={Role.Attribute_name}),
      type=JComboField.class,
      isStateEventSource=true,
      width=300, height=25
      )
  private Role role;
}