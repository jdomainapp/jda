package org.jda.example.courseman.modules.enrolmentmgmt.decisional.modules;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.EnrolmentMgmt;
import org.jda.example.courseman.modules.helprequest.model.HelpRequest;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

@ModuleDescriptor(name = "ModuleEnrolmentMgmt", 
  modelDesc = @ModelDesc(model = EnrolmentMgmt.class), 
  viewDesc = @ViewDesc(formTitle = "Module: EnrolmentMgmt", imageIcon = "EnrolmentMgmt.png", 
      domainClassLabel = "EnrolmentMgmt", 
      view = View.class, viewType = RegionType.Data, parent = RegionName.Tools), 
  controllerDesc = @ControllerDesc(), 
  isPrimary = true)
public class ModuleEnrolmentMgmt {

  @AttributeDesc(label = "EnrolmentMgmt")
  private String title;

  @AttributeDesc(label = "id")
  private int id;

  @AttributeDesc(label = "students")
  private Collection<Student> students;

  @AttributeDesc(label = "helpDesks")
  private Collection<HelpRequest> helpDesks;

  @AttributeDesc(label = "sclassRegists")
  private Collection<SClassRegistration> sclassRegists;
}
