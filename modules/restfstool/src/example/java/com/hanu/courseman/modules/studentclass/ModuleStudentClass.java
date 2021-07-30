package com.hanu.courseman.modules.studentclass;

import com.hanu.courseman.modules.studentclass.model.StudentClass;

import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.panels.DefaultPanel;

import com.hanu.courseman.modules.student.ModuleStudent;
import com.hanu.courseman.modules.student.model.Student;

import java.util.List;

/**
 * @overview 
 *  Represents the module descriptor of the software module that manages the domain objects of 
 *  <tt>SClass</tt>
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleSClass",
modelDesc=@ModelDesc(
    model= StudentClass.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Student Classes",
    domainClassLabel="Student Class",
    imageIcon="sclass.jpg",
    view=View.class,
    viewType=RegionType.Data,
    parent=RegionName.Tools
),
  controllerDesc=@ControllerDesc(
      controller=Controller.class,
      openPolicy=OpenPolicy.O_C
      ),  // v2.6.4b
  isPrimary=true,
  childModules={ModuleStudent.class}
  ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleStudentClass {
  @AttributeDesc(label="Student Class")
  private String title;

  @AttributeDesc(label="Id")
  private int id;

  @AttributeDesc(label="Name")
  private String name;
  
  @AttributeDesc(label="Students",type= DefaultPanel.class,
      controllerDesc=@ControllerDesc(
          //openPolicy=OpenPolicy.O
          /*testing: load all student objects with children*/
          openPolicy=OpenPolicy.L_C
        )
      )
  private List<Student> students;
}
