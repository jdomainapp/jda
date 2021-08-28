package org.jda.example.courseman.modules;

import org.jda.example.courseman.modules.ModuleCourseModule2;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.CompulsoryModule2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleCompulsoryModule2", modelDesc = @ModelDesc(model = CompulsoryModule2.class), viewDesc = @ViewDesc(formTitle = "Module: CompulsoryModule2", imageIcon = "CompulsoryModule2.png", domainClassLabel = "CompulsoryModule2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCompulsoryModule2 extends ModuleCourseModule2 {

    @AttributeDesc(label = "CompulsoryModule2")
    private String title;
}
