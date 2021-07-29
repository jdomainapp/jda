package jda.modules.dcsl.exceptions;

import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.InfoCode;

/**
 * @overview 
 *  A <b>checked</b> {@link ApplicationException} that is thrown when an error occured during checking 
 *  a compilation unit for its conformance to DCSL.  
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.0
 */
public class DcslParserException extends ApplicationException {
  
  public DcslParserException(InfoCode warnCode, Object...args) {
    super(warnCode, args);
  }
}
