package org.jda.example.coursemanrestful.modules;

import jda.modules.common.types.Null;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.*;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.*;
import jda.mosa.controller.*;
import jda.mosa.view.View;

@ModuleDescriptor(
    name = "ModuleMain",
    modelDesc = @ModelDesc(),
    viewDesc = @ViewDesc(
        formTitle = "Course Management App: CourseMan",
        domainClassLabel = "",
        imageIcon = "courseman.jpg",
        view = View.class,
        viewType = RegionType.Main,
        parent = RegionName.Tools,
        topX = 0.5,
        topY = 0.5,
        widthRatio = 0.75f,
        heightRatio = 1f,
        children = {
            RegionName.Desktop,
		RegionName.MenuBar,
		RegionName.ToolBar,
		RegionName.StatusBar
        },
        excludeComponents = {
            RegionName.Add
        },
        props = {
            
        }
    ),
    controllerDesc = @ControllerDesc(
        controller = Controller.class,
        openPolicy = OpenPolicy.I,
        isDataFieldStateListener = false
    ),
    setUpDesc = @SetUpDesc(
        postSetUp = CopyResourceFilesCommand.class
    ),
    subtypes = {
        
    },
    type = ModuleType.DomainMain,
    isPrimary = false
)
public class ModuleMain {
}