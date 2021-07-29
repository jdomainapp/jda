package jda.modules.dcsl.exceptions;

import java.text.MessageFormat;

import jda.modules.common.exceptions.InfoCode;

/**
 * @overview 
 *  Defines all the message codes and texts that are used by the exceptions.
 *  
 * @author Duc Minh Le (ducmle)
 */
public enum DcslMesg implements InfoCode {
  /**
   * 0: source file
   */
  FAIL_TO_PARSE_UNIT("An error occured while parsing source file {0}."),
  /**
   * 0: source file 
   */
  STATE_SPACE_EMPTY("State space is empty (i.e. no domain attributes): {0}")
  ;

  private String text;

  /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
  private MessageFormat messageFormat;

  private DcslMesg(String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }
  
  @Override
  public MessageFormat getMessageFormat() {
    if (messageFormat == null) {
      messageFormat = new MessageFormat(text);
    }
    
    return messageFormat;
  } 
  
}