/**
 * 
 */
package jda.modules.sccl.syntax;

import java.lang.annotation.Documented;

import jda.modules.setup.model.SetUpConfigBasic;

/**
 * @overview 
 *  An annotation used to configure the application set-up process
 *  
 * @author dmle
 *
 * @version 3.3
 */
@Documented
public @interface SysSetUpDesc {

  /**
   *  (Optional) The default setup-config class
   *  Default: {@link SetUpConfigBasic} 
   */
  Class setUpConfigType() default SetUpConfigBasic.class;  
}
