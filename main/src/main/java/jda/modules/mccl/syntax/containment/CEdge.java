/**
 * 
 */
package jda.modules.mccl.syntax.containment;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
/**
 * @overview 
 *  Represents containment edge. 
 *  
 * @author ducmle
 *
 * @version 5.1c 
 */
public @interface CEdge {
  /**
   * Domain class of the parent node of this edge.
   */
  Class parent();

  /**
   * Domain class of the child node of this edge.
   */
  Class child();

  /**
   * (Optional) the customised scope configuration for a subset of the view fields of {@link #child()}'s module.
   * <br>Default: <tt>@ScopeDesc()</tt> 
   */
  ScopeDesc scopeDesc() default @ScopeDesc();
}
