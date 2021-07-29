package jda.modules.javadbserver;

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
import static jda.modules.mccl.conceptmodel.view.RegionName.SearchToolBar;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;
import static jda.modules.mccl.conceptmodel.view.RegionName.ViewCompact;

import jda.modules.javadbserver.controller.JavaDbServerController;
import jda.modules.javadbserver.model.JavaDbServer;
import jda.modules.mccl.conceptmodel.module.ModuleType;
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
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JLabelField;

/**
 * @overview
 *  A module configuration class for the <tt>JavaDbServer</tt> module of 
 *  an application.
 *  
 *  <p>This class may be used as is.
 *  
 * @author dmle
 */
@ModuleDescriptor(name="ModuleJavaDbServer",
modelDesc=@ModelDesc(
  model=JavaDbServer.class
),
viewDesc=@ViewDesc(
    formTitle="Quản trị CSDL",
    imageIcon="domainapp.jpg",
    viewType=RegionType.Data, 
    view=View.class,
    style=StyleName.Heading4,
    parent=RegionName.Tools,
    resizable=false,
    relocatable=false,
    topX=0.5, topY=0.5,
    excludeComponents={
      Open, New, Add, Delete, Update, First, Next, Last, Previous, ObjectScroll,
      Export, Chart, Print, SearchToolBar, ViewCompact,
      Actions
    }
),
controllerDesc=@ControllerDesc(
  controller=JavaDbServerController.class,
  dataController=SimpleDataController.class
),
isPrimary=true,
type=ModuleType.System
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
,isMemoryBased=true
)
public class ModuleJavaDbServer {
//  @AttributeDesc(label="Trợ giúp")
//  private String title;
  
  @AttributeDesc(label="Địa chỉ/Tên máy chủ:", 
      type=JLabelField.class
      //,styleField=StyleName.DefaultOnWhite
      )
  private String host;
  
  @AttributeDesc(label="Cổng ứng dụng:", 
      type=JLabelField.class
      //,styleField=StyleName.DefaultOnWhite
      )
  private Integer port;
  
  @AttributeDesc(label="Loại CSLD:", 
      type=JLabelField.class
      //,styleField=StyleName.DefaultOnWhite
      )
  private String dataSourceType;
  
//  @AttributeDesc(label="Tệp nguồn dữ liệu", 
//      type=JLabelField.class)
//  private String dataSourceName;
  
  @AttributeDesc(label="Tình trạng:", 
      type=JLabelField.class
      //,styleField=StyleName.DefaultOnWhite
      )
  private String status;
}
