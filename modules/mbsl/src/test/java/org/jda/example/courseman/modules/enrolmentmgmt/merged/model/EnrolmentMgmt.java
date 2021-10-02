package org.jda.example.courseman.modules.enrolmentmgmt.merged.model;

import java.util.Collection;

import org.jda.example.courseman.modules.enrolment.model.EnrolmentClosure;
import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control.MgEnrolmentProcessing;
import org.jda.example.courseman.modules.orientation.model.Orientation;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;

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
import jda.modules.mccl.syntax.MCCLConstants;
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
@ANode(refCls=SClassRegistration.class, serviceCls=DataController.class, outClses={MgEnrolmentProcessing.class}, init=true,  
      actSeq={
        // create new
        @MAct(actName=MethodName.newObject, endStates={AppState.Created}),  
        }),
@ANode(refCls=Orientation.class, serviceCls=DataController.class, outClses={MgEnrolmentProcessing.class}, init=true,
      actSeq={
        // open a pre-defined Orientation for user to choose (assume: one Orientation) 
        @MAct(actName=MethodName.open, endStates={AppState.Last}),  
        }),
@ANode(refCls=MgEnrolmentProcessing.class, nodeType=NodeType.Merge, 
       outClses={EnrolmentClosure.class}),
@ANode(refCls=EnrolmentClosure.class, serviceCls=DataController.class, 
      actSeq={
        // create new and return immediately
        @MAct(actName=MethodName.newObject, endStates={AppState.NewObject})  
        // create EnrolmentClosure (automatically)
        ,@MAct(actName=MethodName.createObject, endStates={AppState.Created}),  
        })
})
/**END: activity graph configuration */
public class EnrolmentMgmt {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  // procs 
  @DAttr(name="procs", type=Type.Collection,filter=@Select(clazz=MgEnrolmentProcessing.class),serialisable=false)
  @DAssoc(ascName="process-enrolment",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=MgEnrolmentProcessing.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<MgEnrolmentProcessing> procs;

  // closures 
  @DAttr(name="closures", type=Type.Collection,filter=@Select(clazz=EnrolmentClosure.class),serialisable=false)
  @DAssoc(ascName="enrolment-authorisation",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=EnrolmentClosure.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
    ))
  private Collection<EnrolmentClosure> closures;
  
  public EnrolmentMgmt(Integer id) {
    this.id = nextID(id);
  }

  // for use by object form
  public EnrolmentMgmt() {
    this(null);
  }

  public int getId() {
    return id;
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
  public static final ScopeDef ScopeDefSClassRegist = new ScopeDef(SClassRegistration.class, new String[] {"*"}) {

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
      if (attribName.equals(SClassRegistration.A_student)) {
        // SClassRegistration.student: use combo field
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
      if (attribName.equals(SClassRegistration.A_student)) {
        // SClassRegistration.student: true
        return Boolean.TRUE;
      } else {
        // others: default
        return super.isEditable(attribName);
      }
    }

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#getWidth(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Integer getWidth(String attribName) {
      if (attribName.equals(SClassRegistration.A_student)) {
        // SClassRegistration.student: width for JComboField
        return 350;
      } else {
        // others: default
        return super.getWidth(attribName);
      }
    }

    /* (non-Javadoc)
     * @see domainapp.basics.model.meta.module.containment.ScopeDef#getHeight(java.lang.String)
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Integer getHeight(String attribName) {
      if (attribName.equals(SClassRegistration.A_student)) {
        // SClassRegistration.student: width for JComboField
        return MCCLConstants.STANDARD_FIELD_HEIGHT;
      } else {
        // others: default
        return super.getHeight(attribName);
      }
    }
  }; /** end {@link #ScopeDefSClassRegist} */
}
