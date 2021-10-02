package org.jda.example.courseman.modules.payment;

import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.EnrolmentProcessing;
import org.jda.example.courseman.modules.payment.model.Payment;
import org.jda.example.courseman.modules.payment.model.Payment.PaymentStatus;
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
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @Overview
 *  Module for {@link Payment}
 * 
 * @author dmle
 */
@ModuleDescriptor(name="ModulePayment",
modelDesc=@ModelDesc(
    model=Payment.class
),
viewDesc=@ViewDesc(
    formTitle="Manage Payment",
    domainClassLabel="Payment"
    //,imageIcon="-"
    ,imageIcon="payment.jpg",
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
public class ModulePayment  {

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
  
  @AttributeDesc(label="Payment details", alignX=AlignmentX.Center)
  private String payDetails;
  
  @AttributeDesc(label="Description")
  private String description;
  
  @AttributeDesc(label="Status", alignX=AlignmentX.Center)
  private String statusStr;
//  @AttributeDesc(label="Tình trạng"
//      ,type=JComboField.class
//      )
//  private PaymentStatus status;
  
  // not shown (only used to set input value from the join activity)
  @AttributeDesc(label="~"
      ,type=JTextField.class, editable=false
      ,isVisible=false)
  private EnrolmentProcessing enrolmentProc;
}
