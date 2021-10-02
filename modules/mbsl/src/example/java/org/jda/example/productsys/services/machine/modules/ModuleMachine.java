package org.jda.example.productsys.services.machine.modules;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

import java.util.Collection;

import org.jda.example.productsys.services.machine.model.Machine;
import org.jda.example.productsys.services.machine.model.io.MachineInput;
import org.jda.example.productsys.services.machine.model.io.MachineOutput;
import org.jda.example.productsys.services.operator.model.Operator;

@ModuleDescriptor(name = "ModuleMachine", modelDesc = @ModelDesc(model = Machine.class), viewDesc = @ViewDesc(formTitle = "Module: Machine", imageIcon = "Machine.png", domainClassLabel = "Machine", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleMachine {

    @AttributeDesc(label = "Machine")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "operator")
    private Operator operator;

    @AttributeDesc(label = "machineInput")
    private Collection<MachineInput> machineInput;

    @AttributeDesc(label = "machineOutput")
    private Collection<MachineOutput> machineOutput;
}
