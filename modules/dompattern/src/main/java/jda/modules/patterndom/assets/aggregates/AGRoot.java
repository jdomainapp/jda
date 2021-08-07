package jda.modules.patterndom.assets.aggregates;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.patterndom.assets.domevents.Subscriber;

/**
 * @overview 
 *  Represent the aggregate root.
 *  It is also a sub-type of {@link Subscriber} to listen to update events of 
 *  the member objects.
 *  
 * @author Duc Minh Le (ducmle)
 */
public interface AGRoot extends Subscriber {
  /**
   * @effects 
   *  evaluate the aggregate's invariant against all objects in the aggregate.  
   *  Throws {@link ConstraintViolationException} if an invariant is not satisfied.  
   */
  public void checkInvariants() throws ConstraintViolationException;
}