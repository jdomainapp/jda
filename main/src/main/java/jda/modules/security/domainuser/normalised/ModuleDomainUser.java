package jda.modules.security.domainuser.normalised;

import java.util.Collection;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
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
import jda.modules.security.def.Role;
import jda.modules.security.role.ModuleRoleViewer;
import jda.modules.security.userrole.ModuleUserRole;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.controller.assets.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JListField;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;

/**
 * @overview
 *  An enhanced module for {@link DomainUser} that directly allows user to manipulate many-many 
 *  association with {@link Role}
 *  
 * @author dmle
 * 
 * @version 3.3
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
    layoutBuilderType=TwoColumnLayoutBuilder.class,//TabLayoutBuilder.class,
    topX=0.5,topY=0.0//,widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true  // listens to state change event of list field
    // v3.2: support many-many association with Role
    ,props={
      // custom Create object command: to create {@link UserRole} from the roles
      @PropertyDesc(name=PropertyName.controller_dataController_create,
          valueIsClass=CreateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,
          valueType=Class.class),
      // custom Update object command: to update {@link UserRole} from the roles
      @PropertyDesc(name=PropertyName.controller_dataController_update,
          valueIsClass=UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,
          valueType=Class.class)
    }
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
  
  /* v3.3: use list field for Roles 
  */
  @AttributeDesc(label="Vai trò",
      type=JListField.class
      ,modelDesc=@ModelDesc(model=Role.class)
      ,ref=@Select(clazz=Role.class,attributes={Role.Attribute_name})
      ,isStateEventSource=true
      ,width=100,height=5
  )
  private Collection<Role> theRoles;
}