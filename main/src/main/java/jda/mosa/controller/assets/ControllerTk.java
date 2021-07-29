package jda.mosa.controller.assets;

import jda.modules.dodm.DODMBasic;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.controller.assets.helper.DefaultDataValidator;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ControllerTk {
  private ControllerTk () {}
  
  /**
   * @requires 
   *  if <tt>domainCls != null</tt> then 
   *    <tt>dodm.dsm.includes(domainCls)</tt> 
   *     
   * @effects 
   *  create and return an object of the default implementation of {@link DataValidator} from the 
   *  supplied input
   *  
   * @version 
   * 5.4 (20210318) 
   *
   */
  public static <T> DataValidator<T> getDomainSpecificDataValidator(
      DODMBasic dodm, Class<T> domainCls) {
    return new DefaultDataValidator<>(dodm, domainCls);
  }
}
