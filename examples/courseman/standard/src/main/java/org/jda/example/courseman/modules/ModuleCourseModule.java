package org.jda.example.courseman.modules;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.assets.panels.DefaultPanel;
import org.jda.example.courseman.model.CourseModule;
import org.jda.example.courseman.model.Enrolment;

import java.util.Collection;

@ModuleDescriptor(name = "ModuleCourseModule", 
  modelDesc = @ModelDesc(
      model = CourseModule.class),
  viewDesc = @ViewDesc(formTitle = "Form: CourseModule", 
  imageIcon = "CourseModule.png", 
  domainClassLabel = "CourseModule"
  ,parent=RegionName.Tools // TODO
  ,viewType=RegionType.Data // TODO       
  ,view = jda.mosa.view.View.class), 
  controllerDesc = @ControllerDesc())
public class ModuleCourseModule {

    @AttributeDesc(label = "Course Module")
    private String title;

    @AttributeDesc(label = "Module ID")
    private int id;

    @AttributeDesc(label = "Code")
    private String code;

    @AttributeDesc(label = "Name")
    private String name;

    @AttributeDesc(label = "Semester")
    private int semester;

    @AttributeDesc(label = "Credits")
    private int credits;

    @AttributeDesc(label = "Enrolments",
        type = DefaultPanel.class
    )
    private Collection<Enrolment> enrolments;
}
