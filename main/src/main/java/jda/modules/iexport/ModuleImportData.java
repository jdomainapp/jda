package jda.modules.iexport;

import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Refresh;

import jda.modules.dcsl.syntax.Select;
import jda.modules.iexport.controller.ImportController;
import jda.modules.iexport.model.DomainClassType;
import jda.modules.iexport.model.Import;
import jda.modules.iexport.model.dodm.OSMType;
import jda.modules.iexport.setup.ImportPostSetUp;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.controller.assets.helper.objectbrowser.SingularIdPooledObjectBrowser;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JListField;

/**
 * @overview
 *  A module used to import domain objects from an external source.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleImportData",
modelDesc=@ModelDesc(
  model=Import.class    
),
viewDesc=@ViewDesc(
    formTitle="Nhập dữ liệu",
    imageIcon="import.gif",
    viewType=RegionType.Data, 
    view=View.class,
    parent=RegionName.Tools,
    // hide buttons
    excludeComponents = {
      First, Last, Next, Previous, 
      Delete, New, Refresh, RegionName.Reload,
    },
    topX=0.5,  // middle of screen
    topY=0.5
),
controllerDesc=@ControllerDesc(
  controller=ImportController.class,
  dataController=SimpleDataController.class,
  objectBrowser=SingularIdPooledObjectBrowser.class,
  isDataFieldStateListener=true
),
setUpDesc=@SetUpDesc(postSetUp=ImportPostSetUp.class),
type=ModuleType.System
)
public class ModuleImportData {
  @AttributeDesc(label="Chọn loại <br>lưu trữ",
      type=JListField.class,
      isStateEventSource=true,
      ref=@Select(clazz=OSMType.class,
                  attributes={
                    "classLabel"
                  }),
        height=5,  // visible rows
        width=200  // actual width (in pixels)
      )
  private OSMType osmType;
  
  @AttributeDesc(label="Chọn loại dữ liệu",
      type=JListField.class,
      isStateEventSource=true,
      ref=@Select(clazz=DomainClassType.class,
                  attributes={
                    "classLabel"
                  }),
        height=5,  // visible rows
        width=200  // actual width (in pixels)
      )
  private DomainClassType domainClass;
}
