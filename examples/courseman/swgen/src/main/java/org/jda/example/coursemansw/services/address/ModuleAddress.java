package org.jda.example.coursemansw.services.address;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.address.model.Address;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.coursemansw.services.student.model.Student;

@ModuleDescriptor(name = "ModuleAddress", modelDesc = @ModelDesc(model = Address.class), viewDesc = @ViewDesc(formTitle = "Module: Address", imageIcon = "Address.png", domainClassLabel = "Address", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleAddress {

    @AttributeDesc(label = "Address")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "student")
    private Student student;
}
