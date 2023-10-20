package org.jda.example.courseman.modules;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import org.jda.example.courseman.model.CourseModule;
import org.jda.example.courseman.model.Enrolment;
import org.jda.example.courseman.model.Student;

@ModuleDescriptor(name = "ModuleEnrolment", 
modelDesc = @ModelDesc(model = Enrolment.class),
viewDesc = @ViewDesc(formTitle = "Form: Enrolment", 
  imageIcon = "Enrolment.png", domainClassLabel = "Enrolment"
  ,parent=RegionName.Tools // TODO
  ,viewType=RegionType.Data // TODO            
  ,view = jda.mosa.view.View.class), 
controllerDesc = @ControllerDesc()
,isPrimary=true   // TODO
)
public class ModuleEnrolment {

    @AttributeDesc(label = "Enrolment")
    private String title;

    @AttributeDesc(label = "Enrol-id")
    private int id;

    @AttributeDesc(label = "Student")
    private Student student;

    @AttributeDesc(label = "Module")
    private CourseModule module;

    @AttributeDesc(label = "Internal Mark")
    private Double internalMark;

    @AttributeDesc(label = "Exam Mark")
    private Double examMark;

    @AttributeDesc(label = "Final Mark")
    private Integer finalMark;

    @AttributeDesc(label = "Final Grade")
    private Character finalGrade;
}
