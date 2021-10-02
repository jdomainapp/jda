package org.jda.example.productsys.services.machine.modules;

import org.jda.example.productsys.services.machine.model.GenBars;
import org.jda.example.productsys.services.machine.modules.ModuleGenerator;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleGenBars", modelDesc = @ModelDesc(model = GenBars.class), viewDesc = @ViewDesc(formTitle = "Module: GenBars", imageIcon = "GenBars.png", domainClassLabel = "GenBars", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleGenBars extends ModuleGenerator {

    @AttributeDesc(label = "GenBars")
    private String title;
}
