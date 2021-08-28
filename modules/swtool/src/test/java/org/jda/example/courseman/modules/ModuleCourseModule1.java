package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.CourseModule1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import java.util.Collection;
import org.jda.example.courseman.model2.Enrolment1;

@ModuleDescriptor(name = "ModuleCourseModule1", modelDesc = @ModelDesc(model = CourseModule1.class), viewDesc = @ViewDesc(formTitle = "Module: CourseModule1", imageIcon = "CourseModule1.png", domainClassLabel = "CourseModule1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCourseModule1 {

    @AttributeDesc(label = "CourseModule1")
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
    private Collection<Enrolment1> enrolments;
}
