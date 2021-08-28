package org.examples.jda.sccl.courseman.coursemodule;

import java.util.Collection;

import org.examples.jda.sccl.courseman.enrolment.model.Enrolment;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

@ModuleDescriptor(name = "ModuleCourseModule", modelDesc = @jda.modules.mccl.syntax.model.ModelDesc(model = org.examples.jda.sccl.courseman.coursemodule.model.CourseModule.class), viewDesc = @jda.modules.mccl.syntax.view.ViewDesc(formTitle = "Form: CourseModule", imageIcon = "CourseModule.png", domainClassLabel = "CourseModule", view = jda.mosa.view.View.class), controllerDesc = @jda.modules.mccl.syntax.controller.ControllerDesc())
public class ModuleCourseModule {

    @AttributeDesc(label = "title")
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

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment> enrolments;
}
