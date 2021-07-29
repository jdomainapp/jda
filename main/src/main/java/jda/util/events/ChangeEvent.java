package jda.util.events;

import java.util.EventObject;

/**
 * @overview A clone of Java's ChangeEvent to support ChangeEventModel
 * 
 * @author dmle
 */
public class ChangeEvent extends EventObject {
  /**
   * Constructs a ChangeEvent object.
   * 
   * @effects initialise this with source
   */
  public ChangeEvent(ChangeEventSource source) {
    super(source);   
  }
}
