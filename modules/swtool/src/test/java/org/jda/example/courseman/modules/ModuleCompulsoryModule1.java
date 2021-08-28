package org.jda.example.courseman.modules;

import org.jda.example.courseman.modules.ModuleCourseModule1;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.CompulsoryModule1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleCompulsoryModule1", modelDesc = @ModelDesc(model = CompulsoryModule1.class), viewDesc = @ViewDesc(formTitle = "Module: CompulsoryModule1", imageIcon = "CompulsoryModule1.png", domainClassLabel = "CompulsoryModule1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCompulsoryModule1 extends ModuleCourseModule1 {

    @AttributeDesc(label = "CompulsoryModule1")
    private String title;
}
