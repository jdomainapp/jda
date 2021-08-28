package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Student2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Address2;
import java.util.Collection;
import org.jda.example.courseman.model2.Enrolment2;

@ModuleDescriptor(name = "ModuleStudent2", modelDesc = @ModelDesc(model = Student2.class), viewDesc = @ViewDesc(formTitle = "Module: Student2", imageIcon = "Student2.png", domainClassLabel = "Student2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleStudent2 {

    @AttributeDesc(label = "Student2")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "address")
    private Address2 address;

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment2> enrolments;
}
