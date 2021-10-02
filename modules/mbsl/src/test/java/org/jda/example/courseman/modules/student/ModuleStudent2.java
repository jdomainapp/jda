package org.jda.example.courseman.modules.student;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolment.ModuleEnrolment;
import org.jda.example.courseman.modules.enrolment.model.Enrolment;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.layout.TwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

/**
 * @overview
 *  Module for {@link Student}s (which include a sub-view for enrolments)
 *  
 * @author dmle
 */
@ModuleDescriptor(
name="ModuleStudent",
modelDesc=@ModelDesc(
    model=Student.class
),
viewDesc=@ViewDesc(
    domainClassLabel="Student",
    formTitle="Manage Students", 
    imageIcon="student.jpg",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class,
    layoutBuilderType=TwoColumnLayoutBuilder.class,
    topX=0.5,topY=0.0,widthRatio=0.5f,heightRatio=0.9f
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C
    ,isDataFieldStateListener=true  // listens to state change event of list field
)
,containmentTree=@CTree(
    root=Student.class,
    stateScope={Student.A_id, Student.A_name, Student.A_enrolments}
)
,type=ModuleType.DomainData,
isViewer=true,isPrimary=true,
childModules={ ModuleEnrolment.class
    //, ModuleCourseModule.class 
    }
)
public class ModuleStudent2 {
  @AttributeDesc(label="Student")
  private String title;

  @AttributeDesc(label="Id",alignX=AlignmentX.Center)
  private int id;

  @AttributeDesc(label="Full name",alignX=AlignmentX.Center)
  private String name;

  @AttributeDesc(label="Needs help?"
      ,alignX=AlignmentX.Center
      ,isStateEventSource=true)
  private boolean helpRequested;
  
//  @AttributeDesc(label="Enrols Into",
//      type=JListField.class
//      ,modelDesc=@ModelDesc(model=CourseModule.class)
//      ,ref=@Select(clazz=CourseModule.class,attributes={"name"})
//      ,isStateEventSource=true
//      ,width=100,height=5
//  )
//  private Collection<CourseModule> modules;
  
  @AttributeDesc(label="Enrolments",type=DefaultPanel.class,
  controllerDesc=@ControllerDesc(
      openPolicy=OpenPolicy.O
    )
  )
  private Collection<Enrolment> enrolments;
}