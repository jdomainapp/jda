package jda.modules.setup.modules.applicationmodule;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleApplicationModule",
    modelDesc=@ModelDesc(
        model=ApplicationModule.class
    ),
    viewDesc=@ViewDesc(
       on=false
      //formTitle="Các mô-đun"
//      imageIcon=""       
    ),
    type=ModuleType.System,
    isPrimary=true
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
    )
public class ModuleApplicationModule {
  @AttributeDesc(label="Tên")
  private String name;
  
  @AttributeDesc(label="Cấu hình")
  private Configuration config; 
  
  @AttributeDesc(label="Cấu hình điều khiển",
      type=DefaultPanel.class,
      editable=true,
      controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O)
      )
  private ControllerConfig controllerCfg;
  
//  @AttributeDesc(label="Loại")
//  private String controller;
//  @AttributeDesc(label="Lớp điều khiển dữ liệu")
//  private String dataController;
  
  @AttributeDesc(label="Lớp dữ liệu")
  private String domainClass;
  
  @AttributeDesc(label="Mô-đun xem <br>dữ liệu?")  
  private boolean isViewer;

  @AttributeDesc(label="Mô-đun chính?")  
  private boolean isPrimary;
  
//  @AttributeDesc(label="Hàm chạy mặc định")
//  private LogicalAction.LAName defaultCommand;
  
}
