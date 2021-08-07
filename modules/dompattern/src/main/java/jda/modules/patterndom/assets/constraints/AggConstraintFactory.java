package jda.modules.patterndom.assets.constraints;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.conceptmodel.constraints.Constraint;
import jda.modules.dcsl.conceptmodel.constraints.ConstraintFactory;
import jda.modules.patterndom.assets.aggregates.complex.Aggregate;

/**
 * @overview 
 *   A specialised {@link ConstraintFactory} that is used to create {@link Constraint}s specific
 *   to the Aggregates pattern. 
 *   
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class AggConstraintFactory extends ConstraintFactory {
  private AggConstraintFactory() {}
  
  /**
   * @effects 
   *   create and return a {@link Constraint} object of the type <code>constraintCls</code>
   *   using <code>args</code> as input.
   *   
   *   Throws NotPossibleException if failed.
   */
  public static <T extends Constraint> T createConstraint(
      Class<T> constraintCls, Aggregate ag, Object...args) throws NotPossibleException {
      Object[] newArgs = new Object[args.length + 1];
      newArgs[0] = ag.getDsm();
      System.arraycopy(args, 0, newArgs, 1, args.length);
      
      return ConstraintFactory.createConstraint(constraintCls, newArgs);
  }
}
