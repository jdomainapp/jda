package org.jda.example.courseman.modules.enrolmentmgmt.sequential.model;

import java.util.Set;

import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;
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
import jda.modules.mbsl.model.graph.meta.AGraph;
import jda.modules.mbsl.model.graph.meta.ANode;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

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
@ANode(refCls=Student.class, serviceCls=DataController.class, outClses={SClassRegistration.class}, init=true, 
      actSeq={
        // create new and wait until a new object is created
        @MAct(actName=MethodName.newObject, endStates={AppState.Created})
        }),
@ANode(refCls=SClassRegistration.class, serviceCls=DataController.class, 
      actSeq={
        // prepare to create new
        @MAct(actName=MethodName.newObject, endStates={AppState.NewObject}),  
        // set value for SClassRegistration.Student
        @MAct(actName=MethodName.setDataFieldValues, attribNames={SClassRegistration.A_student}, endStates={AppState.Created}),  
        })
})
/**END: activity graph configuration */
public class EnrolmentMgmt {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  // student registration 
  @DAttr(name="students", type=Type.Collection,filter=@Select(clazz=Student.class),serialisable=false)
  @DAssoc(ascName="register-students",role="mgmt",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Student.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Set<Student> students;

  // class registrations 
  @DAttr(name="sclassRegists", type=Type.Collection,filter=@Select(clazz=SClassRegistration.class),serialisable=false)
  @DAssoc(ascName="manage-class-registration",role="mgmt",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=SClassRegistration.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
    ))
  private Set<SClassRegistration> sclassRegists;
  
  // not used at the moment
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
}
