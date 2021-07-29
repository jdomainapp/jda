package jda.modules.sccl.syntax.security;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//import domainapp.basics.model.security.Role;
//import domainapp.basics.model.security.RolePermission;

/**
 * @overview
 *  Configures a set of {@link RolePermission}s for a set of {@link Role}s.
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface RolePermSetDesc {
  /**
   * @effects 
   *   name(s) of the role(s) to which {@link #perms()} are applied 
   */
  public String[] roleNames();

  /**
   * @effects 
   *  a set of permissions one some resources that share the same permission type.
   *  
   *  <br>Thus, the same role will be referenced in different {@link RolePermSetDesc}s if it has different permission types for different resources.   
   */
  public PermSetDesc[] perms();
}
