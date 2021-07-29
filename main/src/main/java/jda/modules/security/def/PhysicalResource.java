package jda.modules.security.def;

import java.util.List;

import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;

/**
 * A sub-type of {@see Resource} that represents all the physical resources in an application. <br> 
 * Physical resources include database schemas, tables, rows and columns. There is a one-to-one correspondence
 * between {@see PhysicalResource} and {@see LogicalResource}. 
 *  
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class PhysicalResource extends Resource {
//  public PhysicalResource(Integer id, String name, String description, Type type, List<PhysicalPermission> permissions) {
//    super(id, name, description, type, permissions);
//  }

  public PhysicalResource(Integer id, String name, String description, Type type) {
    super(id,name,description,type);
  }
  
  public PhysicalResource(String name, String description, Type type
//      , List<PhysicalPermission> permissions
      ) {
    super(name, description, type);
  }
}
