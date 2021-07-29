package jda.modules.common.exceptions;

import java.text.Format;
import java.text.MessageFormat;

import org.jfree.chart.util.Args;

public class ApplicationRuntimeException extends RuntimeException {
  private Object[] args;
  private InfoCode errCode;

  // v3.2: cache formatted message once created
  private String formattedMesg;
  
  public ApplicationRuntimeException(InfoCode errCode, String msg) {
    super(msg);
    this.errCode = errCode;
  }

  public ApplicationRuntimeException(InfoCode errCode, String msg,
      Object... args) {
    super(msg);
    this.errCode = errCode;
    this.args = args;
  }

  public ApplicationRuntimeException(InfoCode errCode, String msg,
      Throwable e, Object... args) {
    super(msg, e);
    this.errCode = errCode;
    this.args = args;
  }

  // v2.7.3
  public ApplicationRuntimeException(InfoCode errCode, Object... args) {
    super(errCode.getText());
    this.errCode = errCode;
    this.args = args;
  }
  
  // v2.7.3
  public ApplicationRuntimeException(InfoCode errCode, Throwable e, Object... args) {
    super(errCode.getText(), e);
    this.errCode = errCode;
    this.args = args;
  }
  
  /**
   * @effects returns a custom error message
   */
  public String getMessage() {
    // v3.2: cache formatted message 
    if (formattedMesg == null) {
      String mesg = super.getMessage();
      
      if (errCode != null) {
        // get error context
        Class exceptionSrc = errCode.getClass().getEnclosingClass();
        // v3.3: added to support errCode without container class
        if (exceptionSrc == null) {
          // error code not defined in a container class: use this class
          exceptionSrc = getClass();
        }
        
        String exceptionSrcName = exceptionSrc.getSimpleName();
  
        // TODO: support for localisation: get message for error code from the
        // database
        // v2.7.3: use context class & errCode
        String m = getMessage(exceptionSrc, errCode);
        
        // v2.7.3: temporary support for using error code text if message is not specified
        //String errTxt = errCode.getText();
        
        if (m != null) {
          mesg = m;
        }
        // v2.7.3
        else if (mesg == null) {
          mesg = errCode.getText();// v3.2: errTxt;
        }
        
        mesg = "[" + exceptionSrcName + "." + errCode.name() + "] " + mesg;
      }
      
      if (mesg != null) {
        if (args != null) {
          Format fmt = new MessageFormat(mesg);
          mesg = fmt.format(args);
        }
      }
      
      formattedMesg = mesg;
    }
    
    return formattedMesg; //mesg;
  }
  
  /**
   * 
   * @effects 
   *  look up and return error message for <tt>code</tt> that is defined in the context class <tt>context</tt>
   * @version 2.7.3
   */
  private String getMessage(final Class exceptionSrc, final InfoCode code) {
    //TODO: implements this to look up error message from database
    // v3.2: return code.text
    return code.getText();
    //return null;
  }
  
  /**
   * @effects 
   *  return the error code of this
   * @return
   */
  public InfoCode getCode() {
    return errCode;
  }
  
  /**
   * @effects 
   *  return {@link #args}
   *  
   * @version 5.4 
   *
   */
  public Object[] getState() {
    return args;
  }
}
