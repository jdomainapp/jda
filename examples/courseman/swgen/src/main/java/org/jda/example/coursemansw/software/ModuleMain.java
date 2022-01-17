package org.jda.example.coursemansw.software;

import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.mosa.controller.Controller;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.modules.mccl.syntax.ModuleDescriptor;

@ModuleDescriptor(name = "ModuleMain", viewDesc = @ViewDesc(formTitle = "Software: CourseMan", imageIcon = "courseman.png", view = View.class, viewType = RegionType.Main, topX = 0.5, topY = 0.0, widthRatio = 0.75f, heightRatio = 1.0f, children = { RegionName.Desktop, RegionName.MenuBar, RegionName.ToolBar, RegionName.StatusBar }, excludeComponents = { RegionName.Add }, props = { @PropertyDesc(name = PropertyName.view_toolBar_buttonIconDisplay, valueAsString = "true", valueType = Boolean.class), @PropertyDesc(name = PropertyName.view_toolBar_buttonTextDisplay, valueAsString = "false", valueType = Boolean.class), @PropertyDesc(name = PropertyName.view_searchToolBar_buttonIconDisplay, valueAsString = "true", valueType = Boolean.class), @PropertyDesc(name = PropertyName.view_searchToolBar_buttonTextDisplay, valueAsString = "false", valueType = Boolean.class), @PropertyDesc(name = PropertyName.view_lang_international, valueAsString = "true", valueType = Boolean.class) }), controllerDesc = @ControllerDesc(controller = Controller.class), type = ModuleType.DomainMain, setUpDesc = @SetUpDesc(postSetUp = CopyResourceFilesCommand.class))
public class ModuleMain {
}
