package org.jda.example.productsys.services.conveyor.modules;

import java.util.Collection;

import org.jda.example.productsys.services.conveyor.model.Conveyor;
import org.jda.example.productsys.services.conveyor.model.ConveyorSeq;
import org.jda.example.productsys.services.machine.model.Quality;
import org.jda.example.productsys.services.machine.model.io.MachineInput;
import org.jda.example.productsys.services.machine.model.io.MachineOutput;
import org.jda.example.productsys.services.piece.model.Piece;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleConveyor", modelDesc = @ModelDesc(model = Conveyor.class), viewDesc = @ViewDesc(formTitle = "Module: Conveyor", imageIcon = "Conveyor.png", domainClassLabel = "Conveyor", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleConveyor {

    @AttributeDesc(label = "Conveyor")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "capacity")
    private Integer capacity;

    @AttributeDesc(label = "nelems")
    private Integer nelems;

    @AttributeDesc(label = "machineInput")
    private Collection<MachineInput> machineInput;

    @AttributeDesc(label = "machineOutput")
    private Collection<MachineOutput> machineOutput;

    @AttributeDesc(label = "piece")
    private Collection<Piece> piece;

    @AttributeDesc(label = "quality")
    private Collection<Quality> quality;

    @AttributeDesc(label = "prevConveyor")
    private Collection<ConveyorSeq> prevConveyor;

    @AttributeDesc(label = "nextConveyor")
    private Collection<ConveyorSeq> nextConveyor;
}
