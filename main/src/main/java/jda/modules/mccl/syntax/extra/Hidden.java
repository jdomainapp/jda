/**
 * 
 */
package jda.modules.mccl.syntax.extra;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(FIELD)
/**
 * @overview 
 *  Makes a domain attribute hidden in the view. 
 *  
 *  <b>IMPORTANT</b>: This should only be used for generating the view directly from a domain class.
 *  (When module is used then hidden should be configured using the visibility property in 
 *  the module configuration instead.)
 *  
 * @author dmle
 *
 * @version 5.0
 */
public @interface Hidden {
  // no methods
}
