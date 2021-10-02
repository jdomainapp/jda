package org.jda.example.courseman.modules.authorisation;

import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.EnrolmentProcessing;
import org.jda.example.courseman.modules.student.ModuleStudent;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.dcsl.syntax.Select;
import jda.modules.ds.viewable.JFlexiDataSource;
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
import jda.mosa.view.assets.datafields.JTextField;

/**
 * @Overview
 *  Module for {@link Authorisation}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModuleAuthorisation",
modelDesc=@ModelDesc(
    model=Authorisation.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Authorisation",
    domainClassLabel="Authorisation"
    //,imageIcon="-"
    ,imageIcon="authorisation.jpg",
    viewType=RegionType.Data,
    //parent=RegionName.Tools,
    view=View.class
),
controllerDesc=@ControllerDesc(
    controller=Controller.class,
    isDataFieldStateListener=true
),
isPrimary=true
,childModules={ModuleStudent.class}
,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleAuthorisation  {

  @AttributeDesc(label="Enrolment")
  private String title;
  
  // attributes
  @AttributeDesc(label="Id", alignX=AlignmentX.Center)
  private int id;
  
  @AttributeDesc(label="Student",
      type=JTextField.class
      , editable=false
      ,modelDesc=@ModelDesc(model=Student.class, dataSourceType=JFlexiDataSource.class)
      // use this if this field is displayed in a JObjectTable
      //width=150,height=25,
      ,ref=@Select(clazz=Student.class,attributes={"name"})
      )
  private Student student;
  
  @AttributeDesc(label="Authorisation details", alignX=AlignmentX.Center)
  private String authorDetails;
  
  @AttributeDesc(label="Description")
  private String description;
  
//  @AttributeDesc(label="Tình trạng"
//      ,type=JComboField.class)
//  private AuthorzStatus status;
  
  @AttributeDesc(label="Status", alignX=AlignmentX.Center)
  private String statusStr;
  
  // not shown (only used to set input value from the join activity)
  @AttributeDesc(label="~"
      ,type=JTextField.class, editable=false
      ,isVisible=false
      )
  private EnrolmentProcessing enrolmentProc;
}
