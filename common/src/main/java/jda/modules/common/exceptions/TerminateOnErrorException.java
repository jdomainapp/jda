package jda.modules.common.exceptions;

public class TerminateOnErrorException extends ApplicationRuntimeException {
  public TerminateOnErrorException(Throwable t) {
    super(null, 
        ((t!= null) ? t.getMessage() : null), 
        t);
  }
}
