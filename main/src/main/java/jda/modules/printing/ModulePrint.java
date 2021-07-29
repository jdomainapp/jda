package jda.modules.printing;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.printing.controller.PrintDataController;
import jda.modules.printing.model.Printing;

/**
 * @overview A module for printing
 * 
 * @version 3.2 
 * 
 * @todo: complete implementation of the model and the view to make the action binding
 * to {@link LAName#Print} work.    
 */
@ModuleDescriptor(
    name="ModulePrint",
    modelDesc=@ModelDesc(
        model = Printing.class
    ),
    viewDesc=@ViewDesc(
      formTitle="In",
      imageIcon="printing.gif"    
//      viewType=Type.Data, 
//      view=View.class,
//      excludeComponents={
//        RegionName.Open, RegionName.Refresh, RegionName.Reload,
//        RegionName.New, RegionName.Chart, RegionName.Delete,
//        RegionName.First, RegionName.Previous, RegionName.Last, RegionName.Next,
//        RegionName.Actions
//      }
      // no tool menu item
    ),    
    //dataController=Controller.ChartController.class,
    controllerDesc=@ControllerDesc(
        dataController=PrintDataController.class),
    type=ModuleType.System,        
    isPrimary=true
    //,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
    )
public class ModulePrint {
  // no view
}
