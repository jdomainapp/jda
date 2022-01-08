package org.jda.example.coursemanrestful.modules;

import static jda.modules.mccl.conceptmodel.view.RegionName.Add;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

@ModuleDescriptor(
  name="ModuleMain",
  viewDesc=@ViewDesc(
    formTitle="Course Management App: CourseMan",
    imageIcon="courseman.jpg",
    view=View.class,
    viewType=RegionType.Main,
    topX=0.5,topY=0.0,widthRatio=0.75f,heightRatio=1f, 
    children={
        RegionName.Desktop,
        RegionName.MenuBar,
        RegionName.ToolBar,
        RegionName.StatusBar
    },
    excludeComponents={
      // general actions
      // Export, Print, Chart,
      // object-related actions
      Add // experimental
    },
    props={
      @PropertyDesc(name=PropertyName.view_toolBar_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_toolBar_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_searchToolBar_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_searchToolBar_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class),
          /* use these for object form actions
      @PropertyDesc(name=PropertyName.view_objectForm_actions_buttonIconDisplay,
          valueAsString="true",valueType=Boolean.class),
      @PropertyDesc(name=PropertyName.view_objectForm_actions_buttonTextDisplay,
          valueAsString="false",valueType=Boolean.class),
          */
      // international support
      @PropertyDesc(name=PropertyName.view_lang_international,
        valueAsString="true",valueType=Boolean.class),
    }
  ),
  controllerDesc=@ControllerDesc(controller=Controller.class),
  type=ModuleType.DomainMain
  ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleMain {

}
