package jda.modules.security.def;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;

/**
 * A sub-class of {@see Action} that represents all the physical actions that are performed by an application. <br>
 * Physical actions include database-related actions, such as <code>CREATE</code> a table or <code>INSERT</code> a row
 * in a table. 
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class PhysicalAction extends Action {
  // constructor methods
  public PhysicalAction(Integer id, String name) {
    super(id,name);
  }
  
  public PhysicalAction(String name) {
    super(name);
  }
  
  //// pre-defined actions
  public static final PhysicalAction Connect = new PhysicalAction("Connect");
  public static final PhysicalAction Disconnect = new PhysicalAction("Disconnect");
  public static final PhysicalAction Create = new PhysicalAction("Create");
  public static final PhysicalAction Drop = new PhysicalAction("Drop");
  public static final PhysicalAction Update = new PhysicalAction("Update");
  public static final PhysicalAction Insert = new PhysicalAction("Insert");
  public static final PhysicalAction Select = new PhysicalAction("Select");
  public static final PhysicalAction Delete = new PhysicalAction("Delete");
  
  @Override
  public boolean isCompatibleWith(String actionName) {
    // TODO implement this
    return true;
  }

  @Override
  public boolean isLessRestrictiveThan(Action other) {
    // TODO implement this
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        new Object[] {this.getClass().getName(),"isLessRestriveThan"});
  }
}
