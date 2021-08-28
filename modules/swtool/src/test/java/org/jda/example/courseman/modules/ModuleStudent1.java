package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Student1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Address1;
import java.util.Collection;
import org.jda.example.courseman.model2.Enrolment1;

@ModuleDescriptor(name = "ModuleStudent1", modelDesc = @ModelDesc(model = Student1.class), viewDesc = @ViewDesc(formTitle = "Module: Student1", imageIcon = "Student1.png", domainClassLabel = "Student1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleStudent1 {

    @AttributeDesc(label = "Student1")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "address")
    private Address1 address;

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment1> enrolments;
}
