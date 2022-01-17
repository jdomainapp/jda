package org.jda.example.coursemansw.services.coursemodule;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.coursemansw.services.coursemodule.model.CourseModule;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;

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
}
