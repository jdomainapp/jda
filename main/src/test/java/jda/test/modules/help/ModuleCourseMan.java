package jda.test.modules.help;

import static jda.modules.mccl.conceptmodel.view.RegionName.*;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name="CourseMan",
viewDesc=@ViewDesc(
    formTitle="Quản lý khóa học: CourseMan",
    imageIcon="courseman.jpg",
    view=View.class,
    viewType=RegionType.Main, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    excludeComponents={
      // general actions
      Export, Print, Chart,
      // object-related actions
      Add // experimental
    }
),
type=ModuleType.DomainMain
)
public class ModuleCourseMan {

}
