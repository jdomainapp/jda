package org.jda.example.productsys.services.conveyor.modules;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
import org.jda.example.productsys.services.conveyor.model.ConveyorSeq;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleConveyorSeq", modelDesc = @ModelDesc(model = ConveyorSeq.class), viewDesc = @ViewDesc(formTitle = "Module: ConveyorSeq", imageIcon = "ConveyorSeq.png", domainClassLabel = "ConveyorSeq", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleConveyorSeq {

    @AttributeDesc(label = "ConveyorSeq")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "prev")
    private Conveyor prev;

    @AttributeDesc(label = "next")
    private Conveyor next;
}
