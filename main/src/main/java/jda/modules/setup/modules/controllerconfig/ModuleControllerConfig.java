package jda.modules.setup.modules.controllerconfig;

import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;

@ModuleDescriptor(
    name="ModuleControllerConfig",
        modelDesc=@ModelDesc(
            model=ControllerConfig.class
        ),
        viewDesc=@ViewDesc(
            on=false
            //formTitle="Cấu hình điều khiển mô-đun"
//            imageIcon=""
//            // no gui    
        ),
    type=ModuleType.System, 
    isPrimary=true
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
    )
public class ModuleControllerConfig {
  @AttributeDesc(label="Mã")
  private long id;
  
  @AttributeDesc(label="Loại điều khiển")
  private String controller;

  @AttributeDesc(label="Điều khiển dữ liệu")
  private String dataController;
  
  @AttributeDesc(label="Chính sách mở dữ liệu")
  private OpenPolicy openPolicy;
  
  @AttributeDesc(label="Chức năng mặc định")
  private LAName defaultCommand;
  
  @AttributeDesc(label="Xử lí sự kiện trạng thái?")
  private boolean isStateListener;

  @AttributeDesc(label="Thuộc mô-đun")
  private ApplicationModule applicationModule;
}
