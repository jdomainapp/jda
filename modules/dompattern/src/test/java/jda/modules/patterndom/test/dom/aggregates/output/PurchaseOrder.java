package jda.modules.patterndom.test.dom.aggregates.output;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.ObjectFactory;
import jda.modules.patterndom.assets.aggregates.AGRoot;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.util.events.ChangeEventSource;

/**
* @overview 
*
* @author Duc Minh Le (ducmle)
*
* @version 
*/
@DClass()
public class PurchaseOrder implements AGRoot {

   private static final String ATTRIB_TOTAL = "total";

   private static final String ATTRIB_APPROVED_LIMIT = "approvedLimit";

   @DAttr(name = "id", type = Type.Integer, id = true)
   private int id;

   @DAttr(name = ATTRIB_APPROVED_LIMIT, type = Type.Double, mutable = false, optional = false)
   private double approvedLimit;

   @DAttr(name = ATTRIB_TOTAL, type = Type.Double, mutable = false, optional = false)
   private double total;

   //  /**
   //   * @effects 
   //   *
   //   * @version 
   //   */
   //  public PurchaseOrder(int id, int approvedLimit) {
   //    this.id = id;
   //    this.approvedLimit = approvedLimit;
   //  }
   /**
  * @effects return approvedLimit
  */
   public double getApprovedLimit() {
       return approvedLimit;
   }

   /**
  * @effects set approvedLimit = approvedLimit
  */
   public void setApprovedLimit(double approvedLimit) {
       this.approvedLimit = approvedLimit;
   }

   /**
  * @effects return total
  */
   public double getTotal() {
       return total;
   }

   /**
  * @effects 
  * 
  * @version 
  */
   @Override
   public String toString() {
       return "PurchaseOrder (" + id + ", " + approvedLimit + ", " + total + ")";
   }

   /**
  * @effects 
  * 
  * @version 
  */
   @Override
   public int hashCode() {
       return Objects.hash(id);
   }

   /**
  * @effects 
  * 
  * @version 
  */
   @Override
   public boolean equals(Object obj) {
       if (this == obj)
           return true;
       if (obj == null)
           return false;
       if (getClass() != obj.getClass())
           return false;
       PurchaseOrder other = (PurchaseOrder) obj;
       return id == other.id;
   }

   //  @Override
   public Serializable getId() {
       return id;
   }

   @DAttr(name = "members", type = Type.Collection)
   @DAssoc(ascName = "has-members", role = "root", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = POLineItem.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
   private List<POLineItem> members1;

   public PurchaseOrder() {
       members1 = new ArrayList<>();
   }

   /**
  * @modifies {@link #members1}
  * @effects 
  *  create a new member object <code>m</code> whose class is <code>memberCls</code>
  *  from arguments and 
  *  if all invariants are satisfied then 
  *    register this as a Subscriber of <code>m</code>'s domain events, 
  *    if <code>memberCls</code> is associated to this.class then add <code>m</code> this, 
  *    return <code>m</code>
  *  else
  *    throws ConstraintViolationException
  *    
  *  <p>Throws NotFoundException if no constructors of <code>memberCls</code> match <code>args</code>; 
  *  throws NotPossibleException if could not execute the constructor.
  */
   public <T extends Publisher> T addMember(Class<T> memberCls, Object... args) throws NotFoundException, NotPossibleException {
       T member = ObjectFactory.createObject(memberCls, args);
       updateOnMemberAdded(memberCls, member);
       return member;
   }

   private <T extends Publisher> void updateOnMemberAdded(Class<T> memberCls, T member) {
       // Pattern: DomainEvents 
       EventType[] evtTypes = CMEventType.values();
       member.addSubscriber(this, evtTypes);
       // Linked member block: one for each member class directly associated to this class
       if (memberCls.equals(POLineItem.class)) {
           POLineItem m1 = (POLineItem) member;
           this.members1.add(m1);
           // Pattern: Aggregates
           try {
               updateOnMember1Added(m1);
           } catch (ConstraintViolationException e) {
               // reverse the change (in reverse order of adding above)
               this.members1.remove(m1);
               member.removeSubcriber(this, evtTypes);
           }
       }
   }

   // Pattern: Aggregates
   @Override
   public void checkInvariants() throws ConstraintViolationException {
   }

   // Pattern: DomainEvent
   @Override
   public void handleEvent(EventType type, ChangeEventSource<?> source) throws ConstraintViolationException {
       CMEventType et = (CMEventType) type;
       Object srcObj = source.get(0);
       Object arg = source.get(1);
       switch(et) {
           case OnCreated:
               updateOnMember1Added((POLineItem) srcObj);
               break;
           case OnUpdated:
               // update on updated
               updateOnMember1Changed((POLineItem) srcObj, arg);
               break;
           case OnRemoved:
               updateOnMember1Removed((POLineItem) srcObj);
               break;
       }
   }

   /**
  * @effects 
  *  update the state of this when <code>someMember</code> has been created and added to this. 
  *  If some invariant is not satisfied, roll back update and 
  *  rethrow {@link ConstraintViolationException}. 
  */
   private void updateOnMember1Added(POLineItem someMember) throws ConstraintViolationException {
       // keep a record of prevState (before update)
       Object prevState = null;
       // Pattern: aggregates
       commitUpdate(prevState);
   }

   /**
  * @effects 
  *  update the state of this when <code>someMember</code>'s state is changed. 
  *  If some invariant is not satisfied, roll back update and 
  *  rethrow {@link ConstraintViolationException}. 
  */
   private void updateOnMember1Changed(POLineItem someMember, Object... args) throws ConstraintViolationException {
       // keep a record of prevState (before update)
       Object prevState = null;
       // Pattern: aggregates
       commitUpdate(prevState);
   }

   /**
  * @effects 
  *  update the state of this when <code>someMember</code> is removed. 
  *  If some invariant is not satisfied, roll back update and 
  *  rethrow {@link ConstraintViolationException}. 
  */
   private void updateOnMember1Removed(POLineItem someMember, Object... args) throws ConstraintViolationException {
       // keep a record of prevState (before update)
       Object prevState = null;
       // Pattern: aggregates
       commitUpdate(prevState);
   }

   /**
  * @effects 
  *  check all invariants of the concerned aggregate, 
  *  if some invariant is violated then 
  *    roll back the update based on <code>prevState</code>, 
  *    rethrows {@link ConstraintViolationException}
  */
   private void commitUpdate(Object prevState) throws ConstraintViolationException {
       // Pattern: aggregates
       try {
           checkInvariants();
       } catch (ConstraintViolationException e) {
           // reverse the change
           rollbackUpdate(prevState);
           throw e;
       }
   }

   /**
  * @effects 
  *  rollback the state of this to <code>prevState</code>
  */
   private void rollbackUpdate(Object prevState) {
   }
}

