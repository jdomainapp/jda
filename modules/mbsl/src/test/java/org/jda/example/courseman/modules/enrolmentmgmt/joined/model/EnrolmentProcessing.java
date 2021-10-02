package org.jda.example.courseman.modules.enrolmentmgmt.joined.model;

import java.util.Collection;

import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.enrolment.model.EnrolmentApproval;
import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.control.JPaymentAuthorise;
import org.jda.example.courseman.modules.payment.model.Payment;
import org.jda.example.courseman.modules.student.model.Student;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mbsl.model.appmodules.meta.MAct;
import jda.modules.mbsl.model.graph.NodeType;
import jda.modules.mbsl.model.graph.meta.AGraph;
import jda.modules.mbsl.model.graph.meta.ANode;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  Represents the enrolment management activity
 *  
 * @author dmle
 * @version 3.4c
 */
@DClass(serialisable=false, singleton=true)
/**Activity graph configuration */
@AGraph(nodes={
@ANode(refCls=Payment.class, serviceCls=DataController.class, outClses={JPaymentAuthorise.class}, init=true,  
      actSeq={
        // create new
        @MAct(actName=MethodName.newObject, endStates={AppState.NewObject}),  
        // set value for Student
        @MAct(actName=MethodName.setDataFieldValue, attribNames={Payment.A_enrolmentProc}), //, endStates={AppState.Editing}),  
        // create new Payment (and cause the payment process to execute)
        @MAct(actName=MethodName.createObject, endStates={AppState.Created})  
        }),
@ANode(refCls=Authorisation.class, serviceCls=DataController.class, outClses={JPaymentAuthorise.class}, init=true,
      actSeq={
        // create new 
        @MAct(actName=MethodName.newObject, endStates={AppState.NewObject}),  
        // set value for Student
        @MAct(actName=MethodName.setDataFieldValue, attribNames={Authorisation.A_enrolmentProc}), //, endStates={AppState.Editing}),  
        // create new Authorisation (and cause the authorisation process to execute)
        @MAct(actName=MethodName.createObject, endStates={AppState.Created}),
        }),
@ANode(refCls=JPaymentAuthorise.class, nodeType=NodeType.Join, 
       outClses={EnrolmentApproval.class}),
@ANode(refCls=EnrolmentApproval.class, serviceCls=DataController.class, 
      actSeq={
        // create new and return immediately
        @MAct(actName=MethodName.newObject),  
        // set value for EnrolmentApproval
        @MAct(actName=MethodName.setDataFieldValues, attribNames={EnrolmentApproval.A_student, EnrolmentApproval.A_payment, EnrolmentApproval.A_authorisation, EnrolmentApproval.A_approved}, endStates={AppState.Created}),  
        }),
})
/**END: activity graph configuration */
public class EnrolmentProcessing {
  public static final String A_student = "student";
  
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name = A_student, type = Type.Domain, optional = false)
  //@DAssoc(ascName = "std-has-payment", role = "enr", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Student student;
  
  // payment processing 
  @DAttr(name="payments", type=Type.Collection,filter=@Select(clazz=Payment.class),serialisable=false)
  @DAssoc(ascName="process-payment",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Payment.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<Payment> payments;

  // authorisation 
  @DAttr(name="authorisations", type=Type.Collection,filter=@Select(clazz=Authorisation.class),serialisable=false)
  @DAssoc(ascName="enrolment-authorisation",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Authorisation.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
    ))
  private Collection<Authorisation> authorisations;
  
  // enrolment approval
  @DAttr(name="enrolApprovals", type=Type.Collection,filter=@Select(clazz=EnrolmentApproval.class),serialisable=false)
  @DAssoc(ascName="approve-enrolments",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=EnrolmentApproval.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<EnrolmentApproval> enrolApprovals;
  
  public EnrolmentProcessing(Integer id, Student student) {
    this.id = nextID(id);
    
    this.student = student;
  }

  // for use by object form
  public EnrolmentProcessing(Student student) {
    this(null, student);
  }

  public int getId() {
    return id;
  }

  public Student getStudent() {
    return student;
  }
  
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  /////Scope Definitions
  public static final ScopeDef ScopeDefPayment = new ScopeDef(Payment.class, new String[] {"*"}) {

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#getDisplayClass(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Class getDisplayClass(String attribName) {
      if (attribName.equals(Payment.A_student)) {
        // Payment.student: use combo field
        return JComboField.class;
      } else {
        // others: default
        return super.getDisplayClass(attribName);
      }
    }

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#isEditable(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Boolean isEditable(String attribName) {
      if (attribName.equals(Payment.A_student)) {
        // Payment.student: true
        return Boolean.TRUE;
      } else {
        // others: default
        return super.isEditable(attribName);
      }
    }
  }; /** end {@link #ScopeDefPayment} */
  
  public static final ScopeDef ScopeDefAuthorisation = new ScopeDef(Authorisation.class, new String[] {"*"}) {

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#getDisplayClass(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Class getDisplayClass(String attribName) {
      if (attribName.equals(Authorisation.A_student)) {
        // Payment.student: use combo field
        return JComboField.class;
      } else {
        // others: default
        return super.getDisplayClass(attribName);
      }
    }

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#isEditable(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Boolean isEditable(String attribName) {
      if (attribName.equals(Authorisation.A_student)) {
        // Payment.student: true
        return Boolean.TRUE;
      } else {
        // others: default
        return super.isEditable(attribName);
      }
    }
  }; /** end {@link #ScopeDefAuthorisation} */
}
