package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Enrolment2;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Student2;
import org.jda.example.courseman.model2.CourseModule2;

@ModuleDescriptor(name = "ModuleEnrolment2", modelDesc = @ModelDesc(model = Enrolment2.class), viewDesc = @ViewDesc(formTitle = "Module: Enrolment2", imageIcon = "Enrolment2.png", domainClassLabel = "Enrolment2", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleEnrolment2 {

    @AttributeDesc(label = "Enrolment2")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student2 student;

    @AttributeDesc(label = "module")
    private CourseModule2 module;

    @AttributeDesc(label = "internalMark")
    private Double internalMark;

    @AttributeDesc(label = "examMark")
    private Double examMark;

    @AttributeDesc(label = "finalMark")
    private Integer finalMark;

    @AttributeDesc(label = "finalGrade")
    private Character finalGrade;
}
