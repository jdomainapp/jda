package jda.software.setup.modules;

import static jda.modules.mccl.conceptmodel.view.RegionName.Add;
import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.New;
import static jda.modules.mccl.conceptmodel.view.RegionName.Print;
import static jda.modules.mccl.conceptmodel.view.RegionName.SearchToolBar;
import static jda.modules.mccl.conceptmodel.view.RegionName.ViewCompact;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

@ModuleDescriptor(name="ModuleSetUpMain",
viewDesc=@ViewDesc(
    formTitle="Cài đặt chương trình",
    imageIcon="setup.jpg",
    viewType=RegionType.Main, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    excludeComponents={
        //Open, 
        New, Add, Delete, 
        //Update, 
        //First, Next, Last, Previous, ObjectScroll,
        Export, Chart, Print, SearchToolBar, ViewCompact,
        //Actions
    },
    view=View.class,
    topX=0,
    topY=0
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
type=ModuleType.DomainMain,
isMemoryBased=true
)
public class ModuleSetUpMain {
  //
}