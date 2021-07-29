package org.jda.example.courseman.modulesgen.coursemodule.modules;

import org.jda.example.courseman.modulesgen.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.modulesgen.coursemodule.modules.ModuleCourseModule;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleCompulsoryModule", modelDesc = @ModelDesc(model = CompulsoryModule.class), viewDesc = @ViewDesc(formTitle = "Module: CompulsoryModule", imageIcon = "CompulsoryModule.png", domainClassLabel = "CompulsoryModule", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCompulsoryModule extends ModuleCourseModule {

    @AttributeDesc(label = "CompulsoryModule")
    private String title;
}
