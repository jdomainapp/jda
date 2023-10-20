package org.jda.example.courseman.modules;

import java.util.Collection;

import org.jda.example.courseman.model.Enrolment;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

@ModuleDescriptor(name = "ModuleCourseModule", 
  modelDesc = @ModelDesc(
      model = org.jda.example.courseman.model.CourseModule.class), 
  viewDesc = @ViewDesc(formTitle = "Form: CourseModule", 
  imageIcon = "CourseModule.png", 
  domainClassLabel = "CourseModule"
  ,parent=RegionName.Tools // TODO
  ,viewType=RegionType.Data // TODO       
  ,view = jda.mosa.view.View.class), 
  controllerDesc = @ControllerDesc())
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

    @AttributeDesc(label = "enrolments")
    private Collection<Enrolment> enrolments;
}
