package jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling;

import jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.UnLocode;

public class UnknownLocationException extends CannotCreateHandlingEventException {

  private final UnLocode unlocode;

  public UnknownLocationException(final UnLocode unlocode) {
    this.unlocode = unlocode;
  }

  @Override
  public String getMessage() {
    return "No location with UN locode " + unlocode.idString() + " exists in the system";
  }
}
