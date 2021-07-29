package jda.modules.security.def;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
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
 *  Represents a user of the application. 
 *  
 * @author dmle
 *
 * @version 
 * - 3.3: enhanced to support many-many associations
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class DomainUser {
  public static final String Attribute_id = "id";
  public static final String Attribute_login = "login";
  public static final String Attribute_password = "password";
  public static final String Association_WithUserRole = "user-has-roles"; // user-roles
  public static final String Attribute_roles = "roles"; // v3.2
  public static final String Attribute_theRoles = "theRoles"; // v3.2
  public static final String Association_WithRole = "user-and-roles";
  
  public static final int LENGTH_NAME = 100;
  
  
  @DAttr(name=Attribute_id,id=true,auto=true,type=Type.Integer,mutable=false,length=6)
  private int id; 
  private static int idCounter;

  @DAttr(name="name",type=Type.String,optional=false,length=LENGTH_NAME)
  private String name;
  @DAttr(name=Attribute_login,type=Type.String,optional=false,length=25)
  private String login;
  @DAttr(name=Attribute_password,type=Type.StringMasked,optional=false,length=25)
  private String password;
  
  /**
   * @version 3.2
   * realise many-many association between {@link DomainUser} and {@link Role}. It is 
   * normalised by {@link #roles}.
   * 
   * <p>The value of this attribute is not really part of {@link DomainUser}'s state as such. It is 
   * only used as a means to quickly create {@link UserRole}s, which is performed automatically by 
   * a specialised data controller command of the module. 
   */
  @DAttr(name=Attribute_theRoles,type=Type.Collection,serialisable=false,optional=false,
      filter=@Select(clazz=Role.class))
  @DAssoc(ascName=Association_WithRole,role="user",
    ascType=AssocType.Many2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Role.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE),
    normAttrib=Attribute_roles)  
  private Collection<Role> theRoles;
  
  @DAttr(name=Attribute_roles,type=Type.Collection,serialisable=false,optional=false,
      //filter="UserRole",
      filter=@Select(clazz=UserRole.class)//,role="user"
      )
  @DAssoc(ascName=Association_WithUserRole,role="user",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=UserRole.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))
  private Collection<UserRole> roles;
  private int rolesCount; // v3.2
  
  // v3.2: constructor: base constructor
  public DomainUser(Integer id, String name, String login, String pwd,
      Collection<Role> theRoles //v3.2
      ) {
    this.id = nextID(id);
    this.name = name;
    this.login = login;
    this.password = pwd;
    
    this.roles = new ArrayList();
    
    this.theRoles = theRoles;
  }

  // v3.2: constructor: from data source
  public DomainUser(Integer id, String name, String login, String pwd) {
    this(id,name,login,pwd, null);
  }
  
  // v3.2: constructor: create from object form (with roles)
  public DomainUser(String name, String login, String pwd, Collection<Role> roles) {
    this(null, name, login, pwd, roles);
  }

  // constructor: create from object form (without roles)
  public DomainUser(String name, String login, String pwd) {
    this(null, name, login, pwd
        //roles 
        ,null
        );
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  /** ASSOCIATION {@link #roles} */

  public void setRoles(Collection<UserRole> roles) {
    this.roles = roles;
    
    if (roles != null)
      rolesCount = roles.size();
  }
  
  public Collection<UserRole> getRoles() {
    return roles;
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getRolesCount() {
    return rolesCount; //v3.2: .size();
  }
  
  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setRolesCount(int roleCount) {
    this.rolesCount = roleCount;
  }
  
  /**
   * @modifies {@link #roles}, {@link #theRoles}
   * @effects 
   *  clear {@link #roles} and {@link #theRoles} and add <tt>userRole</tt> to them as the only entry
   *     
   * @version 3.3
   */
  public void setSingleRole(UserRole userRole) {
    clearRoles();
    addUserRole(userRole);
  }

  /**
   * @requires role != null
   */
  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_roles)
  public void addUserRole(UserRole role) {
    if (!roles.contains(role)) {
      roles.add(role);
      
      // v3.2: update theRoles
      addRole(role.getRole());
    }
  }

  @DOpt(type=DOpt.Type.LinkAdder) @AttrRef(value=Attribute_roles)
  public void addUserRole(Collection<UserRole> roles) {
    for (UserRole ur : roles) {
      if (!this.roles.contains(ur)) {
        this.roles.add(ur);
        
        // v3.2: update theRoles
         addRole(ur.getRole());
      }
    }
  }

  @DOpt(type=DOpt.Type.LinkAdderNew) @AttrRef(value=Attribute_roles)
  public void addNewUserRole(UserRole role) {
    this.roles.add(role);
    
    // v3.2: update theRoles
     addRole(role.getRole());
    
    rolesCount++;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew) @AttrRef(value=Attribute_roles)
  public void addNewUserRole(Collection<UserRole> roles) {
    this.roles.addAll(roles);

    // v3.2: update theRoles
    for (UserRole ur : roles)
      addRole(ur.getRole());

    rolesCount += roles.size();
  }
  
  @DOpt(type=DOpt.Type.LinkRemover) @AttrRef(value=Attribute_roles)
  public void removeUserRole(UserRole role) {
    boolean removed = roles.remove(role);
    if (removed) {
      
      // update theRoles
      removeRole(role.getRole());
      
      rolesCount--;
    }
  }
  
  /** end ASSOCIATION {@link #roles} */
  
  /** ASSOCIATION {@link #theRoles}: maintained via {@link #roles} */
  public Collection<Role> getTheRoles() {
    return theRoles;
  }

  public void setTheRoles(Collection<Role> theRoles) {
    this.theRoles = theRoles;
  }

  /**
   * @effects 
   *  add <tt>role</tt> to {@link #theRoles}
   * @version 3.2
   */
  private void addRole(Role role) {
    if (theRoles == null) theRoles = new ArrayList();
    if (!theRoles.contains(role))
      theRoles.add(role);
  }
  
  /**
   * @effects 
   *  remove <tt>role</tt> to {@link #theRoles}
   * @version 3.2
   */
  private void removeRole(Role role) {
    if (theRoles != null) {
      theRoles.remove(role);
    }
  }
  
  /** end ASSOCIATION {@link #theRoles}*/
  
  /**
   * @modifies {@link #roles}, {@link #theRoles}
   * @effects 
   *  clear {@link #roles} and {@link #theRoles} 
   *     
   * @version 3.3
   */
  private void clearRoles() {
    roles.clear();
    if (theRoles != null) theRoles.clear();
  }
  
  /***/
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DomainUser other = (DomainUser) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"("+id+","+login+")";
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

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)
          idCounter = maxIdVal;
      }
      // TODO add support for other attributes here
    }
  }
}
