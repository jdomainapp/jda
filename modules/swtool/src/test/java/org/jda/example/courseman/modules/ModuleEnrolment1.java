package org.jda.example.courseman.modules;

import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import org.jda.example.courseman.model2.Enrolment1;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import org.jda.example.courseman.model2.Student1;
import org.jda.example.courseman.model2.CourseModule1;

@ModuleDescriptor(name = "ModuleEnrolment1", modelDesc = @ModelDesc(model = Enrolment1.class), viewDesc = @ViewDesc(formTitle = "Module: Enrolment1", imageIcon = "Enrolment1.png", domainClassLabel = "Enrolment1", view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), controllerDesc = @ControllerDesc(), isPrimary = true)
public class ModuleEnrolment1 {

    @AttributeDesc(label = "Enrolment1")
    private String title;

    @AttributeDesc(label = "id")
    private int id;

    @AttributeDesc(label = "student")
    private Student1 student;

    @AttributeDesc(label = "module")
    private CourseModule1 module;

    @AttributeDesc(label = "internalMark")
    private Double internalMark;

    @AttributeDesc(label = "examMark")
    private Double examMark;

    @AttributeDesc(label = "finalMark")
    private Integer finalMark;

    @AttributeDesc(label = "finalGrade")
    private Character finalGrade;
}
