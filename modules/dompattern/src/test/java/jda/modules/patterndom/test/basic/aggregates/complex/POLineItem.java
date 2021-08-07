package jda.modules.patterndom.test.basic.aggregates.complex;

import java.util.Objects;

import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.util.events.ChangeEventSource;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class POLineItem implements Publisher {
  @DAttr(name="part",type=Type.Domain)
  @DAssoc(ascName="part-has-orderitems",role="item",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=Part.class,cardMin=0,cardMax=1))
  private Part part;
  
  @DAttr(name="po",type=Type.Domain)
  @DAssoc(ascName="order-has-items",role="item",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=PurchaseOrder.class,cardMin=0,cardMax=1))
  private PurchaseOrder po;
  
  private int qty;
  private double price;
  private double amt;
  
  /** 
   * A reusable event source object that is shared among objects of this class. 
   * This is used where a single publiser is shared among different subscribers
   */
  private static ChangeEventSource<?> evtSrc;
  
  /**
   * @effects 
   *
   * @version 
   */
  public POLineItem(PurchaseOrder po, Part part, Integer qty, Double price) {
    this.part = part;
    this.po = po;
    this.qty = qty;
    this.price = price;
    
    updateAmt();
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private void updateAmt() {
    amt = qty * price;
  }
  
  /**
   * @effects return amt
   */
  public double getAmt() {
    return amt;
  }

  /**
   * @effects return part
   */
  public Part getPart() {
    return part;
  }
  /**
   * @effects set part = part
   */
  public void setPart(Part part) {
    this.part = part;
  }
  /**
   * @effects return po
   */
  public PurchaseOrder getPo() {
    return po;
  }
  /**
   * @effects set po = po
   */
  public void setPo(PurchaseOrder po) {
    this.po = po;
  }
  /**
   * @effects return qty
   */
  public int getQty() {
    return qty;
  }
  
  /**
   * @effects set qty = qty
   */
  public void setQty(int qty) {
    double oldAmt = getAmt();
    
    this.qty = qty;
    updateAmt();
    
    // pattern DomainEvent
    // TODO ? handle possible ConstraintViolationException 
    notifyStateChanged(CMEventType.OnUpdated, getEventSource(), 
        new Tuple2<String, Double>("amt", oldAmt));
  }
  
  /**
   * @effects return price
   */
  public double getPrice() {
    return price;
  }
  /**
   * @effects set price = price
   */
  public void setPrice(double price) {
    double oldAmt = getAmt();
    
    this.price = price;
    updateAmt();
    
    // pattern DomainEvent
    // TODO ? handle possible ConstraintViolationException 

    notifyStateChanged(CMEventType.OnUpdated, getEventSource(), 
        new Tuple2<String, Double>("amt", oldAmt));
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public ChangeEventSource<?> getEventSource() {
    if (evtSrc == null) {
      evtSrc = createEventSource(this.getClass());
    } else {
      resetEventSource(evtSrc);
    }
    
    return evtSrc;
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "POLineItem (" + po.getId() + ", " + part.getName() + ", " + qty + ", " + price + ")";
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    return Objects.hash(part, po);
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
    POLineItem other = (POLineItem) obj;
    return Objects.equals(part, other.part) && Objects.equals(po, other.po);
  }
  
  
}
