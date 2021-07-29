package jda.modules.mccl.util;

import java.text.MessageFormat;

import jda.modules.common.exceptions.InfoCode;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DomainConstants {
  private DomainConstants() {};
  
  public static enum DomainMesg implements InfoCode {
    /**0: domain class*/
    MCC_OF_DOMAIN_CLASS_NOT_FOUND("Could not locate MCC of the domain class {0}"), 
    /**
     * 0: property
     * 1: value
     */
    INVALID_NONE_ANNOTATION_TYPED_PROPERTY_VALUE("Property {0} does not have an annotation-typed value ({1})")
    ;

    private String text;

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;

    private DomainMesg(String text) {
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
}
