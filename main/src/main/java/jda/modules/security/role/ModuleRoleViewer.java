package jda.modules.security.role;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.security.def.Role;
import jda.mosa.controller.Controller;
import jda.mosa.controller.assets.datacontroller.ObjectTableController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.tables.JObjectTable;

/**
 * @overview
 *  A view-only module for {@link Role}.
 *  
 * @author dmle
 *
 */
@ModuleDescriptor(
name="ModuleRoleViewer",
modelDesc=@ModelDesc(
    model=Role.class,
    editable=false
),
viewDesc=@ViewDesc(
    domainClassLabel="Vai trò",
    formTitle="Danh sách vai trò", 
    imageIcon="frmRole.png",
    viewType=RegionType.Data,
    // no menu item:
    // parent=RegionName.Tools,
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
isPrimary=true, 
isViewer=true
//,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
//,childModules={
//}
)
public class ModuleRoleViewer {
  @AttributeDesc(label="Vai trò")
  private String title;

  @AttributeDesc(label="Tên vai trò"
      ,width=20,height=MCCLConstants.STANDARD_FIELD_HEIGHT,
      alignX=AlignmentX.Center)
  private String name;

  @AttributeDesc(label="Mô tả"
      ,width=JDataField.MAX_DISPLAYABLE_TEXT_WIDTH,height=MCCLConstants.STANDARD_FIELD_HEIGHT
  )
  private String description;
}