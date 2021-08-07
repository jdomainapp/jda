package jda.modules.setup.modules.dodmconfig;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.OSMProtocol;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.assets.datafields.list.JComboField;

@ModuleDescriptor(
name="ModuleDodmConfig",
modelDesc=@ModelDesc(
    model=DODMConfig.class
),
viewDesc=@ViewDesc(
    on=false
//    formTitle="Cấu hình DODM",
//    domainClassLabel="Cấu hình DODM"
),
controllerDesc=@ControllerDesc(
    isDataFieldStateListener=true
),
type=ModuleType.System,
 isPrimary=true
 ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleDodmConfig {
//  @View(label="Công ty")
//  private String title;
  
  @AttributeDesc(label="Loại giao thức",
      type=JComboField.class,
      isStateEventSource=true
      )
  private OSMProtocol osmProtocol;
  
  @AttributeDesc(label="Đặc tả giao thức (URL)")
  private String protocolSpec;
  
  @AttributeDesc(label="Cấu hình")
  private Configuration config;
}
