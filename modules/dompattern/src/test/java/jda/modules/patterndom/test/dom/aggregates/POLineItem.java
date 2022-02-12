package jda.modules.patterndom.test.dom.aggregates;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class POLineItem {

  public POLineItem() {
    System.out.println("Initialised POLineItem...");
  }
  
  public POLineItem(PurchaseOrder root) {
    System.out.println("Initialised POLineItem with order...");
  }
  
}
