package org.jda.example.productsys.services.machine.modules;

import org.jda.example.productsys.services.machine.model.Package;
import org.jda.example.productsys.services.machine.modules.ModuleMachine;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModulePackage", modelDesc = @ModelDesc(model = Package.class), viewDesc = @ViewDesc(formTitle = "Module: Package", imageIcon = "Package.png", domainClassLabel = "Package", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModulePackage extends ModuleMachine {

    @AttributeDesc(label = "Package")
    private String title;
}
