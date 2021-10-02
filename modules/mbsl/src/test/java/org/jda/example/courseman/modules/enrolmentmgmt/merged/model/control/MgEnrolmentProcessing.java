package org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control;

import java.util.Collection;

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
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview 
 *  Represent the merged node for enrolment processing.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(serialisable=false)
public class MgEnrolmentProcessing {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  // sclass-regists
  @DAttr(name="sclassRegists", type=Type.Collection,filter=@Select(clazz=SClassRegistration.class),serialisable=false)
  @DAssoc(ascName="do-sclass-regist",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=SClassRegistration.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<SClassRegistration> sclassRegists;

  // orientation
  @DAttr(name="orientations", type=Type.Collection,filter=@Select(clazz=Orientation.class),serialisable=false)
  @DAssoc(ascName="do-orientation",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Orientation.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<Orientation> orientations;

  // virtual link to EnrolmentMgmt (merged)
  @DAttr(name="enrolmentMgmt",type=Type.Domain,serialisable=false)
  private org.jda.example.courseman.modules.enrolmentmgmt.merged.model.EnrolmentMgmt enrolmentMgmt;

  public MgEnrolmentProcessing(Integer id) {
    this.id = nextID(id);
  }

  // for use by object form
  public MgEnrolmentProcessing() {
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
