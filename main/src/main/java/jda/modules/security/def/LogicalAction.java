package jda.modules.security.def;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.conceptmodel.controller.LAName;

/**
 * A sub-class of {@see Action} that represents all the logical actions that are performed in an application.<br>
 * Logical actions include actions that are performed on classes and objects, such as registering a class into a domain schema
 * or creating an object of a given class.  
 *  
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class LogicalAction extends Action {
  /** the actual {@link LAName} represented by {@link #getName()} 
   * @version 3.2c */
  private LAName action;
  
  // constructor methods
  public LogicalAction(LAName logicalName) {
    super(logicalName.name());
    
    this.action = logicalName;
  }

  /**
   * @requires name is a valid {@link LAName}.name
   */
  public LogicalAction(Integer id, String name) {
    super(id,name);
    
    this.action = LAName.valueOf(name);
  }
  
  /**
   * @requires name is a valid {@link LAName}.name
   */
  public LogicalAction(String name) {
    //v3.2c: super(name);
    this(LAName.valueOf(name));
  }
  
  @Override
  public boolean isCompatibleWith(String checkAction) {
    String name = getName();
    
    /*v3.2c: improved to separate actionName and name 
    if (LAName.Update.isEqualByName(actionName) || LAName.Update.isEqualByName(name)) {
      // UPDATE: actions must match exactly
      return name.equals(actionName);
    } else {
      boolean ro = LAName.isReadOnly(name);
      boolean yro = LAName.isReadOnly(actionName);
      
      if (ro || yro) {
        // READ-ONLY: both actions must be read-only
        return ro == yro;
      } else { 
        // add other cases here
        return true;
      }
    }
    */
    if (LAName.Update.isEqualByName(checkAction)) {
      // check action is writable: my action must match exactly
      return name.equals(checkAction);
    } else if (LAName.isReadOnly(name)) {
      // my action is READ-ONLY: check action must match exactly
      return LAName.isReadOnly(checkAction);
    } 
    // TODO: add other cases here (if needed)
    else { 
      // all other cases: assume compatible
      return true;
    }
  }

  @Override
  public boolean isLessRestrictiveThan(Action other) throws NotPossibleException {
    if (other.getClass() != this.getClass()) {
      // not LogicalAction
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_COMPARABLE, new Object[] {other.getClass(), LogicalAction.class});
    }

    LogicalAction otherAct = (LogicalAction) other;
    
    boolean yro = otherAct.isReadOnly();
    if (yro) {
      // other is read-only
      return isWritable();
    } else {
      // other is writable
      return isAny();
    }
  }

  /**
   * @requires {@link #action} is initialised
   * @effects 
   *  if <tt>this</tt> represents {@link LAName#LogicalAny} action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */
  private boolean isAny() {
    return action.isAny();
  }

  /**
   * @requires {@link #action} is initialised
   * @effects 
   *  if <tt>this</tt> represents a writable action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */  
  private boolean isWritable() {
    return action.isWritable();
  }

  /**
   * @requires {@link #action} is initialised
   * @effects 
   *  if <tt>this</tt> represents a READ-ONLY action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */
  private boolean isReadOnly() {
    return action.isReadOnly();
  }
}