package org.jda.example.productsys.services.machine.modules;

import org.jda.example.productsys.services.machine.model.GenCylinders;
import org.jda.example.productsys.services.machine.modules.ModuleGenerator;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleGenCylinders", modelDesc = @ModelDesc(model = GenCylinders.class), viewDesc = @ViewDesc(formTitle = "Module: GenCylinders", imageIcon = "GenCylinders.png", domainClassLabel = "GenCylinders", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleGenCylinders extends ModuleGenerator {

    @AttributeDesc(label = "GenCylinders")
    private String title;
}
