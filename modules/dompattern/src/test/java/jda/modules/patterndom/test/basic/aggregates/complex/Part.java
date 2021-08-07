package jda.modules.patterndom.test.basic.aggregates.complex;

import java.util.Collection;
import java.util.Objects;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.util.events.ChangeEventSource;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Part implements Publisher {
  @DAttr(name="name",type=Type.String, id=true)
  private String name;
  @DAttr(name="price",type=Type.Double)
  private double price;
  
  @DAttr(name="items",type=Type.Collection)
  @DAssoc(ascName="part-has-orderitems",role="part",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=POLineItem.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private Collection<POLineItem> items;
  
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
  public Part(String name, Double price) {
    this.name = name;
    this.price = price;
  }

  /**
   * @effects return name
   */
  public String getName() {
    return name;
  }


  /**
   * @effects set name = name
   */
  public void setName(String name) {
    this.name = name;
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
    this.price = price;
  }

  
  /**
   * @effects return items
   */
  public Collection<POLineItem> getItems() {
    return items;
  }

  /**
   * @effects set items = items
   */
  public void setItems(Collection<POLineItem> items) {
    this.items = items;
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Part (" + name + ", " + price + ")";
  }
  
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    return Objects.hash(name);
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
    Part other = (Part) obj;
    return Objects.equals(name, other.name);
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
}
