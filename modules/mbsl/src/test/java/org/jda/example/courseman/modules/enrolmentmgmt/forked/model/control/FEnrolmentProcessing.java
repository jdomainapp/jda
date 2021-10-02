package org.jda.example.courseman.modules.enrolmentmgmt.forked.model.control;

import java.util.Collection;

import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.payment.model.Payment;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represent the forked node for enrolment processing.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(serialisable=false)
public class FEnrolmentProcessing {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  // payment 
  @DAttr(name="payments", type=Type.Collection,filter=@Select(clazz=Payment.class),serialisable=false)
  @DAssoc(ascName="do-payment",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Payment.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<Payment> payments;

  // authorisation
  @DAttr(name="authorisations", type=Type.Collection,filter=@Select(clazz=Authorisation.class),serialisable=false)
  @DAssoc(ascName="do-authorisation",role="proc",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(
        type=Authorisation.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE,
        updateLink=false
     ))
  private Collection<Authorisation> authorisations;

  // virtual link to EnrolmentMgmt (forked)
  @DAttr(name="enrolmentMgmt1",type=Type.Domain,serialisable=false)
  private org.jda.example.courseman.modules.enrolmentmgmt.forked.model.EnrolmentMgmt enrolmentMgmt1;

  // virtual link to EnrolmentMgmt (forkedAndJoined)
  @DAttr(name="enrolmentMgmt2",type=Type.Domain,serialisable=false)
  private org.jda.example.courseman.modules.enrolmentmgmt.forkedandjoined.model.EnrolmentMgmt enrolmentMgmt2;


  public FEnrolmentProcessing(Integer id) {
    this.id = nextID(id);
  }

  // for use by object form
  public FEnrolmentProcessing() {
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
