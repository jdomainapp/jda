package jda.modules.security.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 * 
 *  Represents a role that is enhanced with support for associations to {@link UserRole} 
 * 
 * @author dmle
 * 
 * @version 3.3
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class Role {
  public static final String Attribute_name = "name";
  public static final String Attribute_perms = "perms";
  public static final String Attribute_roles = "roles";
  public static final String Assoc_RoleAndRolePermission = "role-has-permissions";
  
  @DAttr(name=Attribute_name,id=true,type=Type.String,optional=false,length=20)
  private String name;
  
  @DAttr(name="description",type=Type.String,optional=false,length=50)
  private String description;
  
  @DAttr(name=Attribute_perms,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=RolePermission.class)//,role="role"
      )
  @DAssoc(ascName=Assoc_RoleAndRolePermission,role="role",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=RolePermission.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE)) 
  private Collection<RolePermission> perms;

  /**
   * @version 3.3
   * virtual attribute used to fulfill many-many association to {@link DomainUser}. It is normalised by {@link #roles} 
   */
  @DAttr(name="theUsers",type=Type.Collection,serialisable=false,optional=false,
      filter=@Select(clazz=DomainUser.class))
  @DAssoc(ascName=DomainUser.Association_WithRole,role="role",
    ascType=AssocType.Many2Many,endType=AssocEndType.Many,
    associate=@Associate(type=DomainUser.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE),
    normAttrib="roles")  
  private Collection<DomainUser> theUsers;
  
  // v3.3
  @DAttr(name=Attribute_roles,type=Type.Collection,serialisable=false,
      filter=@Select(clazz=UserRole.class))
  @DAssoc(ascName="role-for-users",role="role",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=UserRole.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE)) 
  private List<UserRole> roles;
  private int rolesCount;
  
  public Role(String name, String description) {
    this(name, description,null);
  }

  public Role(String name, String description, Collection<RolePermission> perms) {
    this.name=name;
    this.description=description;
    if (perms == null) {
      this.setPerms(new ArrayList());
    } else {
      this.setPerms(perms);
    }
    
    roles = new ArrayList(); // v3.3
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  public String getDescription() {
    return description;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_perms)
  public void addRolePerm(RolePermission rp) {
    perms.add(rp);
  }

  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_perms)
  public void addRolePerm(Collection<RolePermission> rps) {
    perms.addAll(rps);
  }

  @DOpt(type=DOpt.Type.LinkRemover) @AttrRef(value=Attribute_perms)
  public void removeRolePerm(RolePermission rp) {
    perms.remove(rp);
  }

  public void setPerms(Collection<RolePermission> perms) {
    this.perms = perms;
  }

  public Collection<RolePermission> getPerms() {
    return perms;
  }

  public Integer getPermsCount() {
    return perms.size();
  }
  
  /** ASSOCIATION {@link #roles} */

//  public void setRoles(Collection<UserRole> roles) {
//    this.roles = roles;
//    
//    if (roles != null)
//      rolesCount = roles.size();
//  }
//  
//  public Collection<UserRole> getRoles() {
//    return roles;
//  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getRolesCount() {
    return rolesCount; //v3.2: .size();
  }
  
  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setRolesCount(int roleCount) {
    this.rolesCount = roleCount;
  }
  
  /**
   * @requires role != null
   */
  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_roles)
  public void addUserRole(UserRole role) {
    if (!roles.contains(role)) {
      roles.add(role);
      
      // v3.2: update theRoles
      addUser(role.getUser());
    }
  }

  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_roles)
  public void addUserRole(Collection<UserRole> roles) {
    for (UserRole ur : roles) {
      if (!this.roles.contains(ur)) {
        this.roles.add(ur);
        
        // v3.2: update theRoles
         addUser(ur.getUser());
      }
    }
  }

  @DOpt(type=DOpt.Type.LinkAdderNew) @AttrRef(value=Attribute_roles)
  public void addNewUserRole(UserRole role) {
    this.roles.add(role);
    
    // v3.2: update theRoles
     addUser(role.getUser());
    
    rolesCount++;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew) @AttrRef(value=Attribute_roles)
  public void addNewUserRole(Collection<UserRole> roles) {
    this.roles.addAll(roles);

    // v3.2: update theRoles
    for (UserRole ur : roles)
      addUser(ur.getUser());

    rolesCount += roles.size();
  }
  
  @DOpt(type=DOpt.Type.LinkRemover) @AttrRef(value=Attribute_roles)
  public void removeUserRole(UserRole role) {
    boolean removed = roles.remove(role);
    if (removed) {
      
      // update theRoles
      removeUser(role.getUser());
      
      rolesCount--;
    }
  }
  
  /** end ASSOCIATION {@link #roles} */
  
  /** ASSOCIATION {@link #theUsers}: maintained via {@link #roles} */
  public Collection<DomainUser> getTheUsers() {
    return theUsers;
  }

  public void setTheUsers(Collection<DomainUser> theUsers) {
    this.theUsers = theUsers;
  }

  /**
   * @effects 
   *  add <tt>user</tt> to {@link #theUsers}
   * @version 3.2
   */
  private void addUser(DomainUser user) {
    if (theUsers == null) theUsers = new ArrayList();
    if (!theUsers.contains(user))
      theUsers.add(user);
  }
  
  /**
   * @effects 
   *  remove <tt>user</tt> to {@link #theUsers}
   * @version 3.2
   */
  private void removeUser(DomainUser user) {
    if (theUsers != null) {
      theUsers.remove(user);
    }
  }
  
  /** end ASSOCIATION {@link #theRoles}*/
  
  public String toString() {
    return this.getClass().getSimpleName()+"("+name+")";
  }
}
