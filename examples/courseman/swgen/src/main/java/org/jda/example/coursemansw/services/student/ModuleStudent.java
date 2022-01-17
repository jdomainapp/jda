package org.jda.example.coursemansw.services.student;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.student.model.Student;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.coursemansw.services.student.model.Gender;
import java.util.Date;
import org.jda.example.coursemansw.services.address.model.Address;
import org.jda.example.coursemansw.services.studentclass.model.StudentClass;
import java.util.Collection;
import org.jda.example.coursemansw.services.enrolment.model.Enrolment;

@ModuleDescriptor(name = "ModuleStudent", modelDesc = @ModelDesc(model = Student.class), viewDesc = @ViewDesc(formTitle = "Module: Student", imageIcon = "Student.png", domainClassLabel = "Student", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleStudent {

    @AttributeDesc(label = "Student")
    private String title;

    @AttributeDesc(label = "id")
    private String id;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "gender")
    private Gender gender;

    @AttributeDesc(label = "dob")
    private Date dob;

    @AttributeDesc(label = "address")
    private Address address;

    @AttributeDesc(label = "email")
    private String email;

    @AttributeDesc(label = "studentClass")
    private StudentClass studentClass;

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment> enrolments;
}
