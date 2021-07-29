package org.jda.example.courseman.modulesgen.coursemodule.modules;

import java.util.Collection;

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

@ModuleDescriptor(name = "ModuleCourseModule", modelDesc = @ModelDesc(model = CourseModule.class), viewDesc = @ViewDesc(formTitle = "Module: CourseModule", imageIcon = "CourseModule.png", domainClassLabel = "CourseModule", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCourseModule {

    @AttributeDesc(label = "CourseModule")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "code")
    private String code;

    @AttributeDesc(label = "name")
    private String name;

    @AttributeDesc(label = "semester")
    private int semester;

    @AttributeDesc(label = "credits")
    private int credits;

    @AttributeDesc(label = "students")
    private Collection<Student> students;

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment> enrolments;
}
