package org.jda.example.productsys.services.machine.modules;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
import org.jda.example.productsys.services.machine.model.Quality;
import org.jda.example.productsys.services.machine.modules.ModuleMachine;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleQuality", modelDesc = @ModelDesc(model = Quality.class), viewDesc = @ViewDesc(formTitle = "Module: Quality", imageIcon = "Quality.png", domainClassLabel = "Quality", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleQuality extends ModuleMachine {

    @AttributeDesc(label = "Quality")
    private String title;

    @AttributeDesc(label = "conveyor")
    private Conveyor conveyor;
}
