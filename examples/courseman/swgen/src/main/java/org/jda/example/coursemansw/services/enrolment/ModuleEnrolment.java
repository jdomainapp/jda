package org.jda.example.coursemansw.services.enrolment;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.enrolment.model.Enrolment;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.coursemansw.services.student.model.Student;
import org.jda.example.coursemansw.services.coursemodule.model.CourseModule;

@ModuleDescriptor(name = "ModuleEnrolment", modelDesc = @ModelDesc(model = Enrolment.class), viewDesc = @ViewDesc(formTitle = "Module: Enrolment", imageIcon = "Enrolment.png", domainClassLabel = "Enrolment", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleEnrolment {

    @AttributeDesc(label = "Enrolment")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student student;

    @AttributeDesc(label = "courseModule")
    private CourseModule courseModule;

    @AttributeDesc(label = "internalMark")
    private Double internalMark;

    @AttributeDesc(label = "examMark")
    private Double examMark;

    @AttributeDesc(label = "finalGrade")
    private char finalGrade;

    @AttributeDesc(label = "finalMark")
    private Integer finalMark;
}
