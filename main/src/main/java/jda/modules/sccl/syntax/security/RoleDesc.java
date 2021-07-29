package jda.modules.sccl.syntax.security;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @overview
 *  Configures a {@link Role}
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface RoleDesc {
  public String name();
  
  public String descr();
}
