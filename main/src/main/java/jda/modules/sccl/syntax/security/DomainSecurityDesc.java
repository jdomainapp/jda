package jda.modules.sccl.syntax.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;

/**
 * @overview
 *  Configures domain-specific security settings of an application. It contains settings about 
 *  users and permissions. 
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
@Documented
public @interface DomainSecurityDesc {
  
  /**
   * @effects
   *  the domain users, as defined by {@link DomainUser}
   *  <br>Use an empty array (<tt>{}</tt>) if users are either not specified or already defined elsewhere
   */
  public UserDesc[] userDescs() ;
  
  /**
   * (Optional) login of the user among those defined in {@link #userDescs()} that is used to automatically login to the application when it is started
   * <br>Default: {@link CommonConstants#NullString}
   */
  String appUser() default CommonConstants.NullString;

  /**
   * (Optional) the password of the user defined in {@link #appUser()}
   * <br>Default: {@link CommonConstants#NullString}
   */
  String appPassword() default CommonConstants.NullString;
  
  /**
   * @effects
   *  the domain roles, as defined by {@link Role}
   *  <br>Use an empty array (<tt>{}</tt>) if roles are either not specified or already defined elsewhere
   */
  public RoleDesc[] roleDescs() ;
  
  /**
   * @effects
   *  the user-role assignments, as defined by {@link UserRole}
   *  <br>Use an empty array (<tt>{}</tt>) if user-roles are either not specified or already defined elsewhere
   */
  public UserRolesDesc[] userRoleDescs();
  
  /**
   * @effects
   *  the role permissions, as defined by {@link RolePermission}
   *  <br>Use an empty array (<tt>{}</tt>) if role-permissions are either not specified or already defined elsewhere
   */
  public RolePermSetDesc[] rolePermDescs();
}
