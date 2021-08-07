package jda.modules.setup.modules.domainapplicationmodule;

import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule.DomainApplicationModuleWrapper;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.modules.setup.modules.domainapplicationmodule.controller.DomainApplicationModuleDataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JButtonGroupField;

/**
 * @overview
 *  A module that displays all user application modules on a button panel. Each button's image is the image icon of a module.
 *   
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleDomainApplicationModule",
modelDesc=@ModelDesc(
    model=DomainApplicationModuleWrapper.class
  //editable=false,
),
viewDesc=@ViewDesc(
  formTitle="Các chức năng chương trình",
  imageIcon="domainapplicationmodule.jpg",
  viewType=RegionType.Data, // v3.2: Type.DataAuto,
  excludeComponents={ 
    // exclude all tool bar buttons and the Actions panel
    RegionName.Open, RegionName.Refresh, RegionName.Reload, RegionName.New,  
    RegionName.Delete, RegionName.Update,
    RegionName.First, RegionName.Previous,RegionName.Next, RegionName.Last,
    RegionName.Chart, RegionName.Export,
    RegionName.Actions
  },
  parent=RegionName.Tools,
  view=View.class
  //style=StyleName.Heading2
),
controllerDesc=@ControllerDesc(
    dataController=DomainApplicationModuleDataController.class
),
type=ModuleType.System,
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleDomainApplicationModule {
//  @AttributeDesc(label="Hãy chọn chức năng chương trình")
//  private String title;
  
  @AttributeDesc(label="",type=JButtonGroupField.class,
      loadOidWithBoundValue=true,
      ref=@Select(clazz=DomainApplicationModule.class,
                  attributes={
                    //"name"
                    "imageIcon"
                  }),
      styleField=StyleName.HeadingTitle
      //height=2,  // 3 columns
      //width=0  // as many rows as necessary
      )
  private DomainApplicationModule domainModule;
}