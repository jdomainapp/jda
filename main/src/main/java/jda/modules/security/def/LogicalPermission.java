package jda.modules.security.def;

import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;

/**
 * A sub-class of {@see Permission} that represents all the logical permissions applicable to an application.<br> 
 * A logical permission is a permission between a {@see LogicalAction} and a {@see LogicalResource}.
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class LogicalPermission extends Permission {
  //constructor methods
  public LogicalPermission(Integer id, LogicalAction action, LogicalResource resource) {
    super(id, action, resource);
  }
  
  public LogicalPermission(LogicalAction action, LogicalResource resource) {
    super(action, resource);
  }
}
