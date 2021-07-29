package jda.modules.mccl.syntax;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;

/**
 * @overview
 *  Customise the set up stages. Each stage is specified by a <tt>Command</tt> class, whose 
 *  instance performs a customisation routine.  
 *  
 * @author dmle
 */
@Documented
public @interface SetUpDesc {

  /**
   * The command that customise post set-up 
   *  
   * <br>Default <tt>{@link CommonConstants#NullType}</tt> 
   */
  Class postSetUp() default Null.class;
}
