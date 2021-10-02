package org.jda.example.productsys.services.machine.modules;

import org.jda.example.productsys.services.machine.model.Assembler;
import org.jda.example.productsys.services.machine.modules.ModuleMachine;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleAssembler", modelDesc = @ModelDesc(model = Assembler.class), viewDesc = @ViewDesc(formTitle = "Module: Assembler", imageIcon = "Assembler.png", domainClassLabel = "Assembler", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleAssembler extends ModuleMachine {

    @AttributeDesc(label = "Assembler")
    private String title;
}
