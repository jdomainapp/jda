package org.jda.example.coursemansw.services.coursemodule;

import org.jda.example.coursemansw.services.coursemodule.ModuleCourseModule;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.coursemodule.model.CompulsoryModule;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleCompulsoryModule", modelDesc = @ModelDesc(model = CompulsoryModule.class), viewDesc = @ViewDesc(formTitle = "Module: CompulsoryModule", imageIcon = "CompulsoryModule.png", domainClassLabel = "CompulsoryModule", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCompulsoryModule extends ModuleCourseModule {

    @AttributeDesc(label = "CompulsoryModule")
    private String title;
}
