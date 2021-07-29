package org.jda.example.courseman.modulesgen.enrolment.modules;

import org.jda.example.courseman.modulesgen.coursemodule.model.CourseModule;
import org.jda.example.courseman.modulesgen.enrolment.model.Enrolment;
import org.jda.example.courseman.modulesgen.student.model.Student;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleEnrolment", modelDesc = @ModelDesc(model = Enrolment.class), viewDesc = @ViewDesc(formTitle = "Module: Enrolment", imageIcon = "Enrolment.png", domainClassLabel = "Enrolment", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleEnrolment {

    @AttributeDesc(label = "Enrolment")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student student;

    @AttributeDesc(label = "module")
    private CourseModule module;

    @AttributeDesc(label = "internalMark")
    private Double internalMark;

    @AttributeDesc(label = "examMark")
    private Double examMark;

    @AttributeDesc(label = "finalMark")
    private Integer finalMark;

    @AttributeDesc(label = "finalGrade")
    private Character finalGrade;
}
