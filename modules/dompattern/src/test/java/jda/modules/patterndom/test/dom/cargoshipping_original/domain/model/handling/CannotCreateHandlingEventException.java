package jda.modules.patterndom.test.dom.cargoshipping_original.domain.model.handling;

/**
 * If a {@link jda.modules.patterndom.test.dom.cargoshipping_original.domain.model.handling.HandlingEvent} can't be
 * created from a given set of parameters.
 *
 * It is a checked exception because it's not a programming error, but rather a
 * special case that the application is built to handle. It can occur during normal
 * program execution.
 */
public class CannotCreateHandlingEventException extends Exception {
  public CannotCreateHandlingEventException(Exception e) {
    super(e);
  }

  public CannotCreateHandlingEventException() {
    super();
  }
}
