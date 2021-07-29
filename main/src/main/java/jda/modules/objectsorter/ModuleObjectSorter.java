package jda.modules.objectsorter;

import static jda.modules.mccl.conceptmodel.view.RegionName.Actions;
import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.ObjectScroll;
import static jda.modules.mccl.conceptmodel.view.RegionName.Open;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Print;
import static jda.modules.mccl.conceptmodel.view.RegionName.Refresh;
import static jda.modules.mccl.conceptmodel.view.RegionName.Reload;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.objectsorter.controller.ObjectSorterControllerCommand;
import jda.modules.objectsorter.model.DomainConstraintType;
import jda.modules.objectsorter.model.ObjectSorter;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.assets.composite.TaskActiveController;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.controller.assets.helper.objectbrowser.SingularIdPooledObjectBrowser;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JListField;
import jda.util.ObjectComparator.SortBy;

/**
 * @overview
 *  A module used to import domain objects from an external source.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleObjectSorter",
modelDesc=@ModelDesc(
  model=ObjectSorter.class    
),
viewDesc=@ViewDesc(
    formTitle="Sắp xếp dữ liệu...",
    imageIcon="sorting.png",
    viewType=RegionType.Data, 
    view=View.class,
    parent=RegionName.Tools,
    // hide buttons
    excludeComponents = {
      Open, First, Last, Next, Previous, 
      Delete, New, Reload, Refresh,
      Export, Chart, Print, 
      Actions, ObjectScroll
    },
    topX=0.5d,  // middle of screen
    topY=0.5d,
    resizable=false,
    relocatable=false
),
controllerDesc=@ControllerDesc(
  controller=TaskActiveController.class,
  dataController=SimpleDataController.class,
  objectBrowser=SingularIdPooledObjectBrowser.class,
  isDataFieldStateListener=true,
  props={
    @PropertyDesc(
        name=PropertyName.controller_command,
        valueIsClass=ObjectSorterControllerCommand.class, valueAsString=CommonConstants.NullValue,
        valueType=Class.class
      ),
  }
),
type=ModuleType.System
,isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleObjectSorter {
  @AttributeDesc(label="Chọn thuộc tính<br>để sắp xếp",
      type=JListField.class,// JComboField.class,//
      isStateEventSource=true,
      ref=@Select(clazz=DomainConstraintType.class,
                  attributes={
                    "label"
                  }),
        height=5,  // height
        width=200  // width (in pixels)
      )
  private DomainConstraintType selectedAttrib;
  
  @AttributeDesc(label="Chọn cách<br>sắp xếp",
      type=JListField.class,//JComboField.class,//
      isStateEventSource=true,
      height=3,  // height
      width=100  // width (in pixels)
      )
  private SortBy sortBy;
}
