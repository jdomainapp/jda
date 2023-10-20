package org.jda.example.courseman.modules;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import org.jda.example.courseman.model.Address;
import org.jda.example.courseman.model.Enrolment;
import org.jda.example.courseman.model.Student;

import java.util.Collection;

@ModuleDescriptor(name = "ModuleStudent", 
modelDesc = @ModelDesc(
    model = Student.class),
viewDesc = @ViewDesc(formTitle = "Form: Student", imageIcon = "Student.png", 
domainClassLabel = "Student" 
,parent=RegionName.Tools // TODO
,viewType=RegionType.Data // TODO       
,view = jda.mosa.view.View.class), 
controllerDesc = @ControllerDesc())
public class ModuleStudent {

    @AttributeDesc(label = "Student")
    private String title;

    @AttributeDesc(label = "ID")
    private int id;

    @AttributeDesc(label = "Name")
    private String name;

    @AttributeDesc(label = "Address")
    private Address address;

    @AttributeDesc(label = "Enrolments")
    private Collection<Enrolment> enrolments;
}
