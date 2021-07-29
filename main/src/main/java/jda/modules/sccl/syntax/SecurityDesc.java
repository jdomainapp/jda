/**
 * 
 */
package jda.modules.sccl.syntax;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;

/**
 * @overview 
 *  An annotation that is used to specify security configuration. 
 *  
 * @author dmle
 *
 * @version 3.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface SecurityDesc {

  /**
   *  (Optional) Whether or not security is used
   *  <br>Default: <tt>false</tt> 
   */
  boolean isEnabled() default false;

  /**
   *  (Optional) the domain-specific security configuration
   *  <br>Default: {@link CommonConstants#NullType} 
   */
  Class domainSecurityDesc() default Null.class;
}
