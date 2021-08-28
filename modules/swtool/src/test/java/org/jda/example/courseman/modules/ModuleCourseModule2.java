package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.CourseModule2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import java.util.Collection;
import org.jda.example.courseman.model2.Enrolment2;

@ModuleDescriptor(name = "ModuleCourseModule2", modelDesc = @ModelDesc(model = CourseModule2.class), viewDesc = @ViewDesc(formTitle = "Module: CourseModule2", imageIcon = "CourseModule2.png", domainClassLabel = "CourseModule2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleCourseModule2 {

    @AttributeDesc(label = "CourseModule2")
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
    private Collection<Enrolment2> enrolments;
}
