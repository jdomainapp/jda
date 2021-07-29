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

/**
 * @overview 
 *  An annotation used to specify information about the customer organisation to which an 
 *  application is deployed. 
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface OrgDesc {
  /**
   * organisation's name
   */
  public String name();
  
  /**
   * organisation's logo file name
   */
  public String logo();
  
  /**
   * organisation's address
   */
  public String address();
  
  /**
   * organisation's url (optional)
   * <br>Default: empty string (<tt>""</tt>)
   */
  public String url() default CommonConstants.EmptyString;
}
