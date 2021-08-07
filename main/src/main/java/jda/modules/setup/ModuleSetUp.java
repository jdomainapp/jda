package jda.modules.setup;

import static jda.modules.mccl.conceptmodel.view.RegionName.Actions;
import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
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
import static jda.modules.mccl.conceptmodel.view.RegionName.ViewCompact;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.controller.SetUpController;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.MasterSetUp;
import jda.modules.setup.modules.ModuleConfiguration;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * @overview
 *  A module configuraiton for the <tt>Set-Up</tt> module of 
 *  an application.
 *  
 *  <p>This class may be used as is.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleSetUp",
modelDesc=@ModelDesc(
  model=MasterSetUp.class
),
viewDesc=@ViewDesc(
    formTitle="Cấu hình cài đặt chương trình",
    imageIcon="setup.jpg",
    viewType=RegionType.Data, 
    view=View.class,
    //style=StyleName.Heading4,
    parent=RegionName.Tools,
    layoutBuilderType=TabLayoutBuilder.class,//TwoColumnLayoutBuilder.class,
    resizable=false,
    relocatable=false,
    topX=0.2, topY=0,
    widthRatio=0.7f,
    //heightRatio=0.8f,
    excludeComponents={
      Open, New, Add, Delete, 
      //Update, 
      First, Next, Last, Previous, ObjectScroll,
      Export, Chart, Print, 
      //SearchToolBar, 
      ViewCompact,
      Actions
    }
),
controllerDesc=@ControllerDesc(
  controller=SetUpController.class,
  dataController=SimpleDataController.class,
  openPolicy=OpenPolicy.L_C,
   // v3.1: objectBrowser=PooledObjectBrowser.class,
  isDataFieldStateListener=true
),
isPrimary=true,
type=ModuleType.System,
isMemoryBased=true,
childModules={
  ModuleConfiguration.class
}
)
public class ModuleSetUp {
  @AttributeDesc(label="Cấu hình cài đặt")
  private String title;
  
  @AttributeDesc(label="Lệnh cài đặt", 
      type=JComboField.class,
      isStateEventSource=true
      )
  private Cmd targetCommand;
  
  @AttributeDesc(label="Tiến trình",
      styleField=StyleName.DefaultOnWhite, //DefaultOnWhite
      type=JTextField.class,//JLabelField.class,
      width=50,  // number of text columns
      height=5 // number of text rows
      )
  private String targetStatus;

  @AttributeDesc(label="Cấu hình", 
      type=DefaultPanel.class,
      controllerDesc=@ControllerDesc(
       // v3.1: objectBrowser=PooledObjectBrowser.class,
        openPolicy=OpenPolicy.O_C)
      ,layoutBuilderType=TabLayoutBuilder.class
      )
  private Configuration targetConfig;
}
