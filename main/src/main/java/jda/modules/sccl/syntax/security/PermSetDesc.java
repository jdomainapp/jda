package jda.modules.sccl.syntax.security;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.security.def.PermType;

/**
 * @overview
 *  Configures a set of {@link LogicalPermission}s over a set of application resources that share the same {@link #permType()}. 
 *  A resource can be a module, a class, a class attribute, or objects of a class.
 *  
 * @author dmle
 *
 * @version 3.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface PermSetDesc {
  /**
   * @effects 
   *  The {@link Class}es that defines the resources to which {@link #permType()} is applied.
   *  
   *  <p><b>IMPORTANT</b> if {@link #attribName()} is specified then {@link #resourceClasses()} must be a 
   *  single-element array, which contains the {@link Class} that owns the attribute.
   */
  public Class[] resourceClasses();

  /**
   * @effects 
   *  (ONLY applied to attribute-typed resource) the attribute name
   *  <br>Default: {@link CommonConstants#NullString}
   *  
   *  <p><b>IMPORTANT</b> if {@link #attribName()} is specified then {@link #resourceClasses()} must be a 
   *  single-element array, which contains the {@link Class} that owns the attribute.
   */
  public String attribName() default CommonConstants.NullString;
  
  /**
   * @effects 
   *  Permission type 
   *  <br>Default: {@link PermType#ANY}
   */
  public PermType permType() default PermType.ANY;
}
