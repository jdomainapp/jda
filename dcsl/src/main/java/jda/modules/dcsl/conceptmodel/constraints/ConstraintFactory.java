package jda.modules.dcsl.conceptmodel.constraints;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.SpringLayout.Constraints;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Responsible for creating {@link Constraints},
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ConstraintFactory {
  protected ConstraintFactory() {}

  /**
   * @effects 
   *   create and return a {@link Constraint} object of the type <code>constraintCls</code>
   *   using <code>args</code> as input.
   *   
   *   Throws NotPossibleException if failed.
   */
  public static <T extends Constraint> T createConstraint(
      Class<T> constraintCls, Object...args) throws NotPossibleException {
    try {
      return (T) constraintCls.getDeclaredConstructors()[0].newInstance(args);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException
        | SecurityException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          new Object[] {constraintCls, Arrays.toString(args)});
    }
  }
} 
