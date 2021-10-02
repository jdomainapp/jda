package org.jda.example.courseman.modules.enrolmentmgmt.joined;

import static jda.modules.mccl.conceptmodel.view.RegionName.Chart;
import static jda.modules.mccl.conceptmodel.view.RegionName.Delete;
import static jda.modules.mccl.conceptmodel.view.RegionName.Export;
import static jda.modules.mccl.conceptmodel.view.RegionName.First;
import static jda.modules.mccl.conceptmodel.view.RegionName.Last;
import static jda.modules.mccl.conceptmodel.view.RegionName.Next;
import static jda.modules.mccl.conceptmodel.view.RegionName.ObjectScroll;
import static jda.modules.mccl.conceptmodel.view.RegionName.Open;
import static jda.modules.mccl.conceptmodel.view.RegionName.Previous;
import static jda.modules.mccl.conceptmodel.view.RegionName.Print;
import static jda.modules.mccl.conceptmodel.view.RegionName.Update;

import java.util.Collection;

import org.jda.example.courseman.modules.authorisation.ModuleAuthorisation;
import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.enrolment.ModuleEnrolmentApproval;
import org.jda.example.courseman.modules.enrolment.model.EnrolmentApproval;
import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.EnrolmentProcessing;
import org.jda.example.courseman.modules.payment.ModulePayment;
import org.jda.example.courseman.modules.payment.model.Payment;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mbsl.controller.command.CreateAndExecActivityCommand;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.setup.commands.CopyResourceFilesCommand;
import jda.mosa.controller.Controller;
import jda.mosa.view.View;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.layout.JoinedLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

@ModuleDescriptor(
    name="ModuleEnrolmentProcessing",
    modelDesc=@ModelDesc(
        model=EnrolmentProcessing.class
    ),
    viewDesc=@ViewDesc(
        formTitle="Manage Enrolment Management (Joined)",
        domainClassLabel="Enrolment Management",    
        imageIcon="enrolment.jpg",
        view=View.class,
        viewType=RegionType.Data,
        layoutBuilderType=JoinedLayoutBuilder.class,//TabLayoutBuilder.class,
        topX=0.5,topY=0.0,//widthRatio=0.9f,heightRatio=0.9f,
        parent=RegionName.Tools,
        excludeComponents={
          // general actions
          Export, Print, Chart,
          // object-related actions
          Open, Update, Delete, //New,
          First, Previous, Next, Last, ObjectScroll,
        }
    ),
    controllerDesc=@ControllerDesc(
        controller=Controller.class
        /*customise createObject command to create an activity object and execute the activity model*/
        ,props= {
          @PropertyDesc(name=PropertyName.controller_dataController_create, valueIsClass=CreateAndExecActivityCommand.class, valueType=Class.class, valueAsString=CommonConstants.NullString)
        }
    )
//    ,containmentTree=@ContainmentTree(
//        root=EnrolmentProcessing.class,
//            subtrees={
//          //  -> payment
//          @SubTree1L(
//            parent=EnrolmentProcessing.class,
//            children={
//              @Child(cname=Payment.class,scope={},scopeDef=".ScopeDefPayment")
//              ,@Child(cname=Authorisation.class,scope={},scopeDef=".ScopeDefAuthorisation")
//            }
//          )
//         }        
//    )
    ,isPrimary=true,
    childModules={ModulePayment.class, ModuleAuthorisation.class, ModuleEnrolmentApproval.class}
    ,setUpDesc=@SetUpDesc(postSetUp=CopyResourceFilesCommand.class)
)
public class ModuleEnrolmentProcessing {
  @AttributeDesc(label="Enrolment Management")
  private String title;

  // student
  @AttributeDesc(label="Student", 
      type=JComboField.class
      ,ref=@Select(clazz=Student.class,attributes={"name"})
      ,loadOidWithBoundValue=true  // this must be set to true if displayOidWithBoundValue = true
      ,displayOidWithBoundValue=true
      )
  private Student student;
 
  //payment 
  @AttributeDesc(label="Payment"
       ,type=DefaultPanel.class
  )
  private Collection<Payment> payments;
   
  // authorisation 
  @AttributeDesc(label="Authorisation"
      ,type=DefaultPanel.class
  )
  private Collection<Authorisation> authorisations; 
  
  // enrolment approval 
  @AttributeDesc(label="Enrolment Approval"
      ,type=DefaultPanel.class
      )
  private Collection<EnrolmentApproval> enrolApprovals;

}
