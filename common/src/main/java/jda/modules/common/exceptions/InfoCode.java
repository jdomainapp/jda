package jda.modules.common.exceptions;

import java.text.MessageFormat;

/**
 * @overview 
 *  A wrapper interface used for defining error codes in the exception sub-classes
 *  
 * @author dmle
 * 
 * @version 
 * - 3.2: added support for MessageFormat
 */
public interface InfoCode {
  public String name();

  /**
   * @effects 
   *  return the text message associated to this
   * @version 2.7.3
   */
  public String getText();

  /**
   * @effects 
   *  return the {@link MessageFormat} object that is used for formatting {@link #text} 
   *  using context-specific data arguments
   *  
   * @version 3.2
   */
  public MessageFormat getMessageFormat();
}
