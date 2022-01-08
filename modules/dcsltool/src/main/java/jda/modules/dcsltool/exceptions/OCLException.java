package jda.modules.dcsltool.exceptions;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.InfoCode;

/**
 * @overview 
 *  A sub-type of {@link ApplicationRuntimeException} for OCL-related errors. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class OCLException extends ApplicationRuntimeException {

  /**
   * @effects 
   */
  public OCLException(InfoCode errCode, Throwable e, Object[] args) {
    super(errCode, e, args);
  }

}
