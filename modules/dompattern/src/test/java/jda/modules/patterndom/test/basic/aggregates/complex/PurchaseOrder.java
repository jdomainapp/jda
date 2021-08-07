package jda.modules.patterndom.test.basic.aggregates.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.conceptmodel.constraints.Constraint;
import jda.modules.dcsl.conceptmodel.constraints.feedback.Feedback;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dodm.ObjectFactory;
import jda.modules.patterndom.assets.aggregates.complex.AGRoot;
import jda.modules.patterndom.assets.aggregates.complex.Aggregate;
import jda.modules.patterndom.assets.constraints.AggConstraintFactory;
import jda.modules.patterndom.assets.constraints.AttribValConstraint;
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
public class PurchaseOrder implements AGRoot {
  private static final String ATTRIB_TOTAL = "total";

  private static final String ATTRIB_APPROVED_LIMIT = "approvedLimit";

  @DAttr(name = "id",type=Type.Integer, id=true)
  private int id;

  @DAttr(name=ATTRIB_APPROVED_LIMIT,type=Type.Double, mutable=false, 
      optional=false)
  private double approvedLimit;
  
  @DAttr(name = "items",type=Type.Collection)
  @DAssoc(ascName="order-has-items",role="order",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=POLineItem.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))
  private List<POLineItem> items;
  
  @DAttr(name = ATTRIB_TOTAL,type=Type.Double, mutable=false, optional=false)
  private double total;
  
  // Pattern: aggregates
  private Map<String, Aggregate> ags;
  
  /**
   * @effects 
   *
   * @version 
   */
  public PurchaseOrder(int id, int approvedLimit) {
    this.id = id;
    this.approvedLimit = approvedLimit;
    items = new ArrayList<>();
    
    // Pattern: aggregates
    createAggregate();
  }
  
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
   * @effects return items
   */
  public List<POLineItem> getItems() {
    return items;
  }
  
  public <T extends Publisher> T addMember(Class<T> memberCls, 
      Object...args) throws NotFoundException, NotPossibleException {
    T member = ObjectFactory.createObject(memberCls, args);
    
    updateOnMemberAdded(memberCls, member);

    return member;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private <T extends Publisher> void updateOnMemberAdded(Class<T> memberCls, T member) {
    // Pattern: DomainEvent 
    EventType[] evtTypes = CMEventType.values();
    member.addSubscriber(this, evtTypes);
    
    // Pattern: aggregates
//  Aggregate ag = ags.get("part.purchaseorder");
//  ag.addMember(item);
    
    if (memberCls == POLineItem.class) {
      POLineItem item = (POLineItem) member;
      this.items.add(item);
      
      try {
        updateOnItemAdded(item);
      } catch (ConstraintViolationException e) {
        // reverse the change (in reverse order of adding above)
//        ag.removeMember(item);
        this.items.remove(item);
        item.removeSubcriber(this, evtTypes);
      }
    }    
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private void updateOnItemAdded(POLineItem newItem) throws ConstraintViolationException {
    double oldTotal = total;
    
    if (newItem == null) {
      total = 0;
      items.forEach(item -> total += item.getAmt()); 
    } else {
      total += newItem.getAmt();
    }
    
    // Pattern: aggregates
    commitUpdate(oldTotal);
  }

  /**
   * @effects 
   * 
   */
  private void updateOnItemChanged(POLineItem item, Number oldAmt) 
      throws ConstraintViolationException {
    double oldTotal = total;
    total = total - oldAmt.doubleValue() + item.getAmt();
    
    // Pattern: aggregates
    commitUpdate(oldTotal);
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private void commitUpdate(double oldTotal) throws ConstraintViolationException {
    // Pattern: aggregates
    try {
      // There may be several aggregates with this root
      Aggregate<PurchaseOrder> ag = lookUpAggregateByBoundary(ags.values(), 
          POLineItem.class); 
      
      checkInvariants(ag);
    } catch (ConstraintViolationException e) {
      Collection<Feedback> fbs = (Collection<Feedback>) e.getState()[0];
      System.out.printf("   -> NOT ok%n");
      System.out.println(fbs);
      
      // reverse the change
      rollbackUpdate(oldTotal);
      
      throw e;
    }    
  }

  /**
   * @effects 
   */
  private void rollbackUpdate(double oldTotal) {
    total = oldTotal;    
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

  /**
   * @effects return id
   */
  @Override
  public Serializable getId() {
    return id;
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Aggregate createAggregate() throws NotPossibleException {
    if (ags == null) ags = new HashMap<>();

    // there can be several aggregates with the same root
    Class[] boundary = {
        Part.class, POLineItem.class 
    };
    
    POAggregate ag = new POAggregate("part.purchaseorder", this
        , boundary);
    ags.put(ag.getName(), ag);
    
    // add constraints
    ag.addConstraint(
        AggConstraintFactory.createConstraint(
            AttribValConstraint.class, ag, 
            this.getClass(), ATTRIB_TOTAL, ATTRIB_APPROVED_LIMIT));
    return ag;
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void checkInvariants(Aggregate ag) throws ConstraintViolationException {
    /*
     * - get ag.members
     * - validate them against invariants defined in ag 
     */
    System.out.printf("%s.checkInvariant(): %s%n", 
        this.getClass().getSimpleName(), ag);
    
//    Collection<?> members = ag.getMembers();
//    System.out.printf("   members: %s%n", members);
    
    Collection<Constraint> constraints = ag.getConstraints();
    System.out.printf("   constraints: %s%n", constraints);
    
    constraints.forEach(constr -> {
      // TODO: validate constr against members
      System.out.printf("   : validating constraint: %s%n", constr);
      constr.evaluate(this);
      System.out.printf("   -> OK%n");
    });
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void handleEvent(EventType type, ChangeEventSource<?> source) 
      throws ConstraintViolationException {
    // Pattern: DomainEvent
    CMEventType et = (CMEventType) type;
//    List data = source.getObjects();
    Object srcObj = source.get(0);
    
    System.out.printf("%s.handleEvent(): %s (source: %s)%n", 
        this.getClass().getSimpleName(), et, srcObj);

    switch (et) {
      case OnCreated:
        // TODO: update on created
        break;
      case OnUpdated:
        // update on updated
        // get previous (old) value from source
        
        Tuple2<?,?> oldVal = (Tuple2<?,?>) source.get(1);
        String attrib = (String) oldVal.getFirst();
        Number oldAmt = (Number) oldVal.getSecond();
        updateOnItemChanged((POLineItem) srcObj, oldAmt);
        break;
      case OnRemoved:
        // TODO: update on removed
        break;
    }
  }
}
