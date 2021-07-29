package jda.modules.security.roleperm;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.Permission;
import jda.modules.security.def.Role;
import jda.modules.security.def.RolePermission;
import jda.mosa.controller.Controller;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  A non-viewable module for {@link RolePermission}.
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleRolePermission",
modelDesc=@ModelDesc(
    model=RolePermission.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Vai trò và quyền",
    formTitle="Quản lý phân vai trò và quyền" 
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
public class ModuleRolePermission {
  @AttributeDesc(label="Vai trò và quyền")
  private String title;

  @AttributeDesc(label="Vai trò", 
      editable=false,
      type=JTextField.class)
  private Role role;
  
  @AttributeDesc(label="Quyền",
      ref=@Select(clazz=RolePermission.class//,attributes={Permission.Attribute_name}
      ),
      type=JComboField.class,
      isStateEventSource=true,
      width=300, height=25
      )
  private Permission perm;
  

}