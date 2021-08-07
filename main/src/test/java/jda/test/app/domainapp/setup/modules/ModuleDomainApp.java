package jda.test.app.domainapp.setup.modules;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name="ModuleDomainApp",
viewDesc=@ViewDesc(
    formTitle="Domain-oriented Application",
    imageIcon="test/domainapppprogram.jpg",
    viewType=RegionType.Main, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    view=View.class
),
type=ModuleType.DomainMain
)
public class ModuleDomainApp {
  //
}
