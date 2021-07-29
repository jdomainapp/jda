package jda.modules.dcsl.conceptmodel.constraints;

import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.conceptmodel.constraints.feedback.Feedback;
import jda.modules.dcsl.conceptmodel.constraints.state.StateTable;

/**
 * @overview 
 *  Represents a constraint on the domain model. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public abstract class Constraint {
  private int id;
  private static int idCounter = 0;
  public Constraint() {
    this.id = ++idCounter;
  }
  
  /**
   * @effects
   *  evaluate <tt>args</tt> againts this constraint using <tt>stateTable</tt>, 
   *  if some problems are found then throws {@link ConstraintViolationException}
   *  with Collection<{@link Feedback}> as the data.
   */
  public void evaluate(Object...args) 
  throws ConstraintViolationException {
    evaluate(null, args);
  }
  
  /**
   * @modifies stateTable
   * @effects
   *  evaluate <tt>args</tt> againts this constraint using <tt>stateTable</tt>, 
   *  if some problems are found then throws {@link ConstraintViolationException}
   *  with Collection<{@link Feedback}> as the data.
   *  
   *  <p>if <tt>stateTable != null</tt> then update it with any reusable state data (those that can be used to speed up 
   *  the evaluation of other constraints).  
   */
  public abstract void evaluate(StateTable stateTable, Object...args) 
      throws ConstraintViolationException;
  
  /**
   * @effects 
   *  return this.id
   */
  public int getId() {
    return id;
  }
  
  /**
   * @effects 
   *  return name constructed by {@link #id} and this class's name. 
   */
  public String getName() {
    return String.format("C%d-%s", getId(), this.getClass().getSimpleName());
  }
  
  
  @Override
  public String toString() {
    return "Constr#"+getId();
  }
}
