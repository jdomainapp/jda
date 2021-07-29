package jda.modules.chart;

import org.jfree.chart.JFreeChart;

import jda.modules.chart.controller.ChartDataController;
import jda.modules.chart.model.ChartWrapper;
import jda.modules.chart.model.ChartWrapper.ChartType;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.JPanelField;

/**
 * @overview A module for {@link ChartWrapper}
 */
@ModuleDescriptor(
    name="ModuleChart",
    modelDesc=@ModelDesc(
        model = ChartWrapper.class
    ),
    viewDesc=@ViewDesc(
      formTitle="Biểu đồ",
      imageIcon="chart.gif",    
      viewType=RegionType.Data, 
      view=View.class,
      excludeComponents={
        RegionName.Open, RegionName.Refresh, RegionName.Reload,
        RegionName.New, RegionName.Chart, RegionName.Delete,
        RegionName.First, RegionName.Previous, RegionName.Last, RegionName.Next,
        RegionName.Actions
      }
      // no tool menu item
    ),    
    //dataController=Controller.ChartController.class,
    controllerDesc=@ControllerDesc(
        dataController=ChartDataController.class),
    type=ModuleType.System,        
    isPrimary=true
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
    )
public class ModuleChart {

  // no title
  @AttributeDesc(label="Tiêu đề")
  private String chartTitle;
  
  @AttributeDesc(label="Loại biểu đồ")
  private ChartType chartType;

  @AttributeDesc(label="Nhóm dữ liệu <br>theo cột")
  private boolean categoryByColumn;

  @AttributeDesc(label="",type=JPanelField.class)
  private JFreeChart chart;
}
