package jda.mosa.controller.assets.composite;

import java.util.EventObject;

/**
 * Represents an event that is fired at the conclusion of a method execution. 
 * 
 * @author dmle
 *
 */
public class MethodEvent extends EventObject {
  /** the data values (if any) that are returned as output of the method execution*/
  private Object value;
  public MethodEvent(Object src, Object value) {
    super(src);
    this.value = value;
  }
  
  public Object getValue() {
    return value;
  }
}
