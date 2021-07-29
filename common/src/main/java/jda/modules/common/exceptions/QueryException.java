package jda.modules.common.exceptions;

import java.text.MessageFormat;

public class QueryException extends ApplicationException {
  public static enum Code implements InfoCode {
    QUERY_TERM_NOT_WELL_FORMED(""), //
    OPERATOR_NOT_FOUND(""), //
    ;   
    
    private String text;
    
    private Code(String text) {
      this.text = text;
    }
    
    @Override
    public String getText() {
      return text;
    }       

    /**The {@link MessageFormat} object for formatting {@link #text} using context-specific data arguments*/
    private MessageFormat messageFormat;
    
    @Override
    public MessageFormat getMessageFormat() {
      if (messageFormat == null) {
        messageFormat = new MessageFormat(text);
      }
      
      return messageFormat;
    }
  }
  
  public QueryException(InfoCode errCode, String msg) {
    super(errCode, msg);
  }

  public QueryException(InfoCode errCode, String msg, Object... args) {
    super(errCode, msg, args);
  }

  public QueryException(InfoCode errCode, String msg, Throwable e,
      Object... args) {
    super(errCode, msg, e, args);
  }

}
