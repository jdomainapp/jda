package org.jda.example.courseman.swclasses.cls2.modules.student;

import java.util.Collection;

import org.jda.example.courseman.modules.coursemodule.ModuleCourseModule;
import org.jda.example.courseman.modules.coursemodule.model.CourseModule;
import org.jda.example.courseman.modules.enrolment.ModuleEnrolment;
import org.jda.example.courseman.modules.enrolment.model.Enrolment;
import org.jda.example.courseman.modules.sclass.model.SClass;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;

/**
 * @overview
 *  Module for {@link Student}s which does not include a sub-view for enrolments. 
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
    formTitle="Module: Student", 
    imageIcon="student.jpg",
    viewType=RegionType.Data,
    parent=RegionName.Tools,
    view=View.class
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    openPolicy=OpenPolicy.I_C   // SW2: controller config cust
    ,isDataFieldStateListener=true  
)
,containmentTree=@CTree(
    root=Student.class,
    stateScope={Student.A_id, Student.A_name, 
        Student.A_enrolments // SW2: use enrolments instead of modules
        }
)
,type=ModuleType.DomainData,
isViewer=true,isPrimary=true
,childModules={ ModuleEnrolment.class, ModuleCourseModule.class }
)
public class ModuleStudent {
  @AttributeDesc(label="Student")
  private String title;

  @AttributeDesc(label="id")
  private int id;

  @AttributeDesc(label="name")
  private String name;

  @AttributeDesc(label="helpRequested")
  private boolean helpRequested;
  
  @AttributeDesc(label="modules")
  private Collection<CourseModule> modules;
  
  @AttributeDesc(label="enrolments")
  private Collection<Enrolment> enrolments;
  
  @AttributeDesc(label="sclasses")
  private Collection<SClass> sclasses;
  
  @AttributeDesc(label="sclassRegists")
  private Collection<SClassRegistration> classRegists;  
}