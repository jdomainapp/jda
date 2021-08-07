package jda.modules.patterndom.assets.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.conceptmodel.constraints.Constraint;
import jda.modules.dcsl.conceptmodel.constraints.feedback.Feedback;
import jda.modules.dcsl.conceptmodel.constraints.feedback.FeedbackError;
import jda.modules.dcsl.conceptmodel.constraints.state.StateTable;
import jda.modules.dodm.dsm.DSM;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class AttribValConstraint extends Constraint {
  
  private DSM dsm;
  private Class<?> cls;
  private String valueAttrib;
  private String limitAttrib;

  public AttribValConstraint(DSM dsm, 
      Class<?> cls, String valueAttrib, String limitAttrib) {
    this.dsm = dsm;
    this.cls = cls;
    this.valueAttrib = valueAttrib;
    this.limitAttrib = limitAttrib;
  }
  
  @Override
  public void evaluate(StateTable stateTable, Object...args)
      throws NotPossibleException, ConstraintViolationException {
    if (args == null || args.length < 1)
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {Arrays.toString(args)});
    
    Object domObj = args[0];
    Object attribVal = dsm.getAttributeValue(domObj, valueAttrib);
    Object limitVal = dsm.getAttributeValue(domObj, limitAttrib);

    // TODO ?: improved to support different value types and value range
    Number val = Double.parseDouble(attribVal+"");
    Number limit = Double.parseDouble(limitVal+"");
    
    if (val.doubleValue() > limit.doubleValue()) {
      // violation
      Collection<Feedback> fbs = new ArrayList<>();
      Feedback fb = new FeedbackError(
          String.format("%s.%s: value (%.2f) exceeds limit (%.2f)", 
              cls.getSimpleName(), valueAttrib, attribVal, limitVal 
              ));
      fbs.add(fb);
      
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.OBJECT_STATE_VIOLATES_RULE, 
          new Object[] {fbs, getName()});
    }
  }
}
