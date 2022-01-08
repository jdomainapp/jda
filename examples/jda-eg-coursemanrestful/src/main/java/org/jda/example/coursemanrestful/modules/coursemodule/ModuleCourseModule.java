package org.jda.example.coursemanrestful.modules.coursemodule;

import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

/**
 * @Overview
 *  Module for {@link CourseModule}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleCourseModule",
modelDesc=@ModelDesc(
    model= CourseModule.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Course Modules",
    domainClassLabel="Course Module",    
    imageIcon="coursemodule.jpg",
    viewType=RegionType.Data,
    view=View.class,
    parent=RegionName.Tools
),
controllerDesc=@ControllerDesc(controller=Controller.class),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleCourseModule  {
  @AttributeDesc(label="Form: Course Module")
  private String title;
  
  // attributes
  @AttributeDesc(label="Id",alignX=AlignmentX.Center)
  private int id;
  
  @AttributeDesc(label="Code", alignX=AlignmentX.Center)
  private String code;
  
  @AttributeDesc(label="Name")
  private String name;
  
  @AttributeDesc(label="Semester", alignX=AlignmentX.Center)
  private int semester;
  
  @AttributeDesc(label="Credits", alignX=AlignmentX.Center)
  private int credits;
}
