package org.jda.example.coursemanrestful.modules.enrolment;

import org.jda.example.coursemanrestful.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanrestful.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanrestful.modules.student.model.Student;

import jda.modules.dcsl.syntax.Select;
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
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @Overview
 *  Module for {@link Enrolment}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleEnrolment",
modelDesc=@ModelDesc(
    model= Enrolment.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Enrolment",
    domainClassLabel="Enrolment"
    //,imageIcon="-"
    ,imageIcon="enrolment.jpg",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleEnrolment  {

  @AttributeDesc(label="Manage Enrolments")
  private String title;
  
  // attributes
  @AttributeDesc(label="Id", alignX=AlignmentX.Center)
  private int id;
  
  @AttributeDesc(label="Student",
      type=JComboField.class,
      // use this if this field is displayed in a JObjectTable
      //width=150,height=25,
      ref=@Select(clazz= Student.class,attributes={"name"}),
      loadOidWithBoundValue=true,  // this must be set to true if displayOidWithBoundValue = true
      displayOidWithBoundValue=true)
  private Student student;
  
  @AttributeDesc(label="Course Module", 
      type=JComboField.class,
      // this is is needed for JObjectTable 
      width=80,height=25,
      ref=@Select(clazz= CourseModule.class,attributes={"code"}),
      isStateEventSource=true,      
      alignX=AlignmentX.Center)
  private CourseModule courseModule;
  
  @AttributeDesc(label="Internal Mark", alignX=AlignmentX.Center)
  private double internalMark;
  
  @AttributeDesc(label="Exam Mark", alignX=AlignmentX.Center)
  private double examMark;
  
  @AttributeDesc(label="Final Grade", alignX=AlignmentX.Center)
  private char finalGrade;
}
