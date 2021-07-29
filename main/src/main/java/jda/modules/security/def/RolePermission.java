package jda.modules.security.def;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * The association class between {@see Role} and {@see Permission}
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class RolePermission {
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,mutable=false,length=6)
  private int id; 
  
  @DAttr(name="role",type=Type.Domain,optional=false,length=20)
  @DAssoc(ascName=Role.Assoc_RoleAndRolePermission,role="permissions",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Role.class,cardMin=1,cardMax=1)
    ,dependsOn=true // v3.2
  ) 
  private Role role; 
  
  @DAttr(name="permission",type=Type.Domain,optional=false,length=6)
  private Permission permission;
  
  private static int idCounter;

  public RolePermission(Integer id, Role role, Permission permission) {
    this.id = nextID(id);
    this.role=role;
    this.permission=permission;
  }

  public RolePermission(Role role, Permission permission) {
    this(null,role,permission);
  }

  public int getId() {
    return id;
  }

  public Permission getPermission() {
    return permission;
  }

  public void setPermission(Permission permission) {
    this.permission=permission;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }
  
  public String toString() {
    return "RolePermission("+role.getName()+","+permission+")";
  }
}
