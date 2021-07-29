package jda.modules.common.exceptions.warning;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.InfoCode;

/**
 * @overview
 *  Represent a warning 
 *  
 * @author dmle
 *
 * @version 3.4 
 */
public class DomainWarning extends ApplicationRuntimeException {
  
  public DomainWarning(InfoCode warnCode, Object...args) {
    super(warnCode, args);
  }
}
