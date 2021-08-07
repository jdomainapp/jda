package jda.modules.patterndom.test.basic.aggregates;

import org.junit.Test;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;

/**
 * @overview
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class ClientProg {
  
  @Test
  public void main() {
    // create a purchase order
    PurchaseOrder order = new PurchaseOrder(12946, 1000);
    System.out.println(order);

    // create some parts
    Part p1 = order.addMember(Part.class, "Guitars", 100.0);//new Part("Guitars", 100);
    Part p2 = order.addMember(Part.class, "Trombones", 200.0);//new Part("Trombones", 200);
    Part p3 = order.addMember(Part.class, "Violins", 400.0);//new Part("Violins", 400);
    Part p4 = order.addMember(Part.class, "Piano", 1000.0);//new Part("Piano", 1000);
    
    
    // add line item to order until constraint is violated
    POLineItem item1 = order.addMember(POLineItem.class, order, p1, 3, p1.getPrice());//order.addLineItem(p1, 3, p1.getPrice());
    System.out.printf("AFTER added line item#1: %s%n", order);
    POLineItem item2 = order.addMember(POLineItem.class, order, p2, 2, p2.getPrice());//order.addLineItem(p2, 2, p2.getPrice());
    System.out.printf("AFTER added line item#2: %s%n", order);
    
    // test update event
    int qty = 5;
    Toolkit.sleep(1000, 
        String.format("%nUpdating item quantity: %s -> new-qty = %d...", item1, qty));
    item1.setQty(qty);
    System.out.printf("   Order updated: %s%n", order);

    double price = 200;
    Toolkit.sleep(1000, 
        String.format("%nUpdating item price: %s -> new-price = %.2f...", item1, price));
    try {
      item1.setPrice(price);
    } catch (ConstraintViolationException e) {
      System.err.println(e.getMessage());
      System.out.printf("   Order update rolled back: %s%n", order);
    }
  }
}
