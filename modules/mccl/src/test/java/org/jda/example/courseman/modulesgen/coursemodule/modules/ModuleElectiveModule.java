package org.jda.example.courseman.modulesgen.coursemodule.modules;

import org.jda.example.courseman.modulesgen.coursemodule.model.ElectiveModule;
import org.jda.example.courseman.modulesgen.coursemodule.modules.ModuleCourseModule;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleElectiveModule", modelDesc = @ModelDesc(model = ElectiveModule.class), viewDesc = @ViewDesc(formTitle = "Module: ElectiveModule", imageIcon = "ElectiveModule.png", domainClassLabel = "ElectiveModule", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleElectiveModule extends ModuleCourseModule {

    @AttributeDesc(label = "ElectiveModule")
    private String title;

    @AttributeDesc(label = "deptName")
    private String deptName;
}
