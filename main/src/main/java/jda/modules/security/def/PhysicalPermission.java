package jda.modules.security.def;

import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;

/**
 * A sub-class of {@see Permission} that represents all the physical permissions applicable to an application.<br> 
 * A physical permission is a permission between a {@see PhysicalAction} and a {@see PhysicalResource}.
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class PhysicalPermission extends Permission {
  //constructor methods
  public PhysicalPermission(Integer id, PhysicalAction action, PhysicalResource resource) {
    super(id, action, resource);
  }
  
  public PhysicalPermission(PhysicalAction action, PhysicalResource resource) {
    super(action, resource);
  }
}
