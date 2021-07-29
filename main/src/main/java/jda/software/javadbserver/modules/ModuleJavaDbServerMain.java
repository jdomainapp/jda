package jda.software.javadbserver.modules;

import static jda.modules.mccl.conceptmodel.view.RegionName.*;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

@ModuleDescriptor(name="ModuleJavaDbServerMain",
viewDesc=@ViewDesc(
    formTitle="Quản trị Cơ sở dữ liệu",
    imageIcon="javadbserver.jpg",
    viewType=RegionType.Main, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    excludeComponents={
        Open, New, Add, Delete, Update, First, Next, Last, Previous, ObjectScroll,
        Export, Chart, Print, SearchToolBar, ViewCompact,
        Actions
    },
    view=View.class,
    topX=0.5,
    topY=0.5,
    widthRatio=0.5f,
    heightRatio=0.5f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class
),
type=ModuleType.DomainMain,
isMemoryBased=true
)
public class ModuleJavaDbServerMain {
  //
}