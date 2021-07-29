package jda.modules.security.role.editable;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.Role;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.datacontroller.ObjectTableController;
import jda.mosa.view.View;
import jda.mosa.view.assets.tables.JObjectTable;

/**
 * @overview
 *  A module for managing {@link Role}.
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleRoleManager",
modelDesc=@ModelDesc(
    model=Role.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Vai trò",
    formTitle="Quản lý vai trò", 
    imageIcon="frmRole.png",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    topContainerType=JObjectTable.class,
    view=View.class,
    topX=0.5,topY=0.5
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    dataController=ObjectTableController.class,
    openPolicy=OpenPolicy.O_A
    ),
type=ModuleType.System,
isPrimary=true
,isViewer=false
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleRoleManager extends jda.modules.security.role.ModuleRoleViewer {
//  @AttributeDesc(label="Vai trò")
//  private String title;
//
//  @AttributeDesc(label="Tên vai trò"
//      ,width=20,height=MetaConstants.STANDARD_FIELD_HEIGHT,
//      alignX=AlignmentX.Center)
//  private String name;
//
//  @AttributeDesc(label="Mô tả"
//      ,width=JDataField.MAX_DISPLAYABLE_TEXT_WIDTH,height=MetaConstants.STANDARD_FIELD_HEIGHT
//  )
//  private String description;
}