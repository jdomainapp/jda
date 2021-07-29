package jda.modules.sccl.syntax.security;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @overview
 *  Configure a domain user as defined by {@link DomainUser}
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface UserDesc {
  public String name();
  
  public String login();
  
  public String password();
}
