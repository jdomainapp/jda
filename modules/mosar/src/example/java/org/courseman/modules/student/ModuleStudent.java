package org.courseman.modules.student;

import java.util.Collection;
import java.util.Date;

import org.courseman.modules.address.model.Address;
import org.courseman.modules.enrolment.model.Enrolment;
import org.courseman.modules.student.model.Gender;
import org.courseman.modules.student.model.Student;
import org.courseman.modules.studentclass.model.StudentClass;

import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;

/**
 * @overview
 *  Module for {@link Student}s.
 *
 * @author dmle
 */
@ModuleDescriptor(name = "ModuleStudent",
        modelDesc = @ModelDesc(
                model = Student.class),
        viewDesc = @ViewDesc(
                formTitle = "Form: Student",
                imageIcon = "Student.png",
                domainClassLabel = "Student",
                view = View.class),
        controllerDesc = @ControllerDesc())
public class ModuleStudent {
  @AttributeDesc(label = "Manage Students")
  private String title;

  @AttributeDesc(label = "Student ID")
  private int id;

  @AttributeDesc(label = "Full Name")
  private String name;

  @AttributeDesc(label = "Gender")
  private Gender gender;

  @AttributeDesc(label = "Date of birth")
  private Date dob;

  @AttributeDesc(label = "Email")
  private String email;

  @AttributeDesc(label = "Current Address")
  private Address address;
  
  @AttributeDesc(label = "Student class")
  private StudentClass studentClass;
  
  @AttributeDesc(label = "Course Enrolments")
  private Collection<Enrolment> enrolments;
}
