package jda.modules.sccl.syntax.security;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @overview
 *  Configures the {@link Role}s that a given {@link DomainUser} plays.
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface UserRolesDesc {
  /**
   * @effects 
   *  login of the user (to look up)
   */
  public String userLogin();
  
  /**
   * @effects 
   *  names of the roles of {@link #userLogin()}
   */
  public String[] roleNames();
}
