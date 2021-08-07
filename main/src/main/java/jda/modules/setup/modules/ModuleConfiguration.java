package jda.modules.setup.modules;

import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.ObjectScroll;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Print;

import java.util.List;

import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Organisation;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JLabelField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.datafields.text.JPasswordField;
import jda.mosa.view.assets.layout.TabLayoutBuilder;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * @overview Represents application configuration parameters
 */
@ModuleDescriptor(
    name="ModuleConfiguration",
    modelDesc=@ModelDesc(
      model=Configuration.class
    ),
    viewDesc=@ViewDesc(
      formTitle="Quản lý cấu hình chương trình",
      imageIcon="configuration.jpg",
      viewType=RegionType.Data,
      excludeComponents={ 
        // exclude some tool bar buttons and the Actions panel
        //Open, 
        New, Add, Delete, First, Previous, Next, Last, ObjectScroll,
        Chart, Export, Print, 
//        Refresh,
        //Actions
      },
      view=View.class,
      layoutBuilderType=TabLayoutBuilder.class,//TwoColumnLayoutBuilder.class,
      parent=RegionName.Help
      //style=StyleName.DefaultOnWhite
    ),
    controllerDesc=@ControllerDesc(
      defaultCommand=LAName.Open,
      openPolicy=OpenPolicy.O_C,
      isDataFieldStateListener=true
      ),
    type=ModuleType.System,
    isPrimary=true
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)   
    )
public class ModuleConfiguration {
  // no title
  @AttributeDesc(label="Cấu hình chương trình"
      //styleLabel=StyleName.Heading1OnWhite
      )
  private String title;

  @AttributeDesc(label="Tên chương trình",
      type=JLabelField.class
      //styleLabel=StyleName.DefaultOnWhite,
      //styleField=StyleName.DefaultBlue
      )
  private String appName;
  
  @AttributeDesc(label="Phiên bản",type=JLabelField.class)  
  private String version;
  @AttributeDesc(label="Thư mục chương trình")  
  private String appFolder;
  @AttributeDesc(label="Ngôn ngữ",
      type=JComboField.class,
      isStateEventSource=true
      //,type=JLabelField.class
      )
  private String language;

  //v2.8
  @AttributeDesc(label="Cấu hình CSDL",type=DefaultPanel.class,
      controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O) 
      )  
  private DODMConfig dodmConfig;
  
  @AttributeDesc(label="Về công ty",type=DefaultPanel.class,
      //styleLabel=StyleName.Heading4,
      editable=false,
      controllerDesc=@ControllerDesc(openPolicy=OpenPolicy.O) // v2.6.4b
      )
  private Organisation organisation;
  
  @AttributeDesc(label="Mô-đun mặc định")
  private String defaultModule;
  
  @AttributeDesc(label="Thư mục cài đặt")
  private String setUpFolder;
  
  @AttributeDesc(label="Tên NSD mặc định"
      ,alignX=AlignmentX.Center)
  private String userName;

  @AttributeDesc(label="Mật khẩu",type=JPasswordField.class
      ,alignX=AlignmentX.Center)
  private String password;
  
  @AttributeDesc(label="Tỷ lệ kích thước <br>cửa sổ chính")
  private double mainGUISizeRatio;
  
  @AttributeDesc(label="Tỷ lệ kích thước <br>cửa sổ con")
  private double childGUISizeRatio;
  
  @AttributeDesc(label="Bảo mật?", isStateEventSource=true)    
  private boolean useSecurity;
  
  /** 
   * the number of millisecs before the current value on a list of values of 
   * a spinner control is selected 
   * Default: 25secs */
  @AttributeDesc(label="Độ trễ spinner")
  private int listSelectionTimeOut;
  
  /**
   * the location of the custom fonts
   */
  @AttributeDesc(label="Thư mục phông")
  private String fontLocation;
  
  /**
   * the location of the images
   */
  @AttributeDesc(label="Thư mục ảnh")
  private String imageLocation;
    
  @AttributeDesc(label="Các mô-đun",
      /*v2.6.4.b: type=JObjectTable.class,
       */
      type=DefaultPanel.class,
      editable=false,
      controllerDesc=@ControllerDesc(
          //openPolicy=OpenPolicy.O_C // v2.6.4b
          // v3.1: objectBrowser=PooledObjectBrowser.class,
          openPolicy=OpenPolicy.O_C
       ), 
      layoutBuilderType=TwoColumnLayoutBuilder.class,
      styleLabel=StyleName.Heading4
      )
  private List<ApplicationModule> modules;    
}