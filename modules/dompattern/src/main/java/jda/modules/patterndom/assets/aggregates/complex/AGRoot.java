package jda.modules.patterndom.assets.aggregates.complex;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.conceptmodel.constraints.Constraint;
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
   *  return the global id  
   */
  public Serializable getId();
  
  /**
   * @modifies this
   * @effects 
   *  creates an {@link Aggregate} having this as the root, 
   *  register some {@link Constraint}s as invariants of the aggregate.
   *  
   *  The aggregate's boundary consists in a collection of classes of the 
   *  member objects that participate in the aggregate. 
   *  
   *  Add the aggregate to this.
   */
  public Aggregate createAggregate() throws NotPossibleException;
  
  /**
   * @effects 
   *  evaluate each <code>ag</code>'s invariant (represented by {@link Constraint}) against 
   *  all objects in the aggregate.  
   *  Throws {@link ConstraintViolationException} if an invariant is not satisfied.  
   */
  public void checkInvariants(Aggregate ag) throws ConstraintViolationException;
  
  /**
   * @requires ags != null /\ bound != null
   * @effects
   *  if required arguments are not specified
   *    throws NotPossibleException
   *     
   *  if exists an {@link Aggregate} in this whose boundary contain <code>bound</code>
   *    return it
   *  else
   *    throw NotFoundException 
   */
  public default Aggregate lookUpAggregateByBoundary(
      Collection<Aggregate> ags, Class... bound) 
          throws NotPossibleException, NotFoundException {
    if (ags == null || bound == null)
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] { ags, bound});
    
    Aggregate result = null;
    for (Aggregate ag : ags) {
      if (ag.contains(bound)) {
        result = ag;
        break;
      }
    };
    
    if (result == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {Aggregate.class, Arrays.toString(bound)});
    }

    return result;
  }

}