package jda.modules.patterndom.assets.domevents;

/**
 * @overview Represents event types. This class is used by a {@link Subscriber} to 
 *  register to listen to specific events of interest.
 *
 *  <p>Domain-specific event types can be defined using an enum that implements {@link EventType}. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public interface EventType {
  
  /**
   * @effects 
   *   return the unique name of this event type 
   */
  public String name();
}
