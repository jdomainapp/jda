package org.coursemanmdsa.software.services.coursemgnt.modules.teacher;

import jda.modules.dcsl.util.common.Gender;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.mosa.view.View;
import org.coursemanmdsa.software.services.financialhub.modules.assessment.model.Assessment;

import java.util.Date;

/**
 * @overview
 *  Module for {@link Assessment.Teacher}s.
 *
 * @author congnv
 */
@ModuleDescriptor(name = "ModuleTeacher",
        modelDesc = @ModelDesc(
                model = Assessment.Teacher.class),
        viewDesc = @ViewDesc(
                formTitle = "Form: Teacher",
                imageIcon = "-",
                domainClassLabel = "Teacher",
                view = View.class),
        controllerDesc = @ControllerDesc())
public class ModuleTeacher {
  @AttributeDesc(label = "Manage Teachers")
  private String title;

  @AttributeDesc(label = "Name")
  private String teacherName;
  @AttributeDesc(label = "Gender")
  private Gender teacherGender;
  @AttributeDesc(label = "Date of birth")
  private Date teacherDob;
  @AttributeDesc(label = "Address")
  private int addressId;
  @AttributeDesc(label = "Email")
  private String teacherEmail;
  @AttributeDesc(label = "Department name")
  private String deptName;
}
