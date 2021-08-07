package jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling;

import jda.modules.patterndom.test.dom.cargoshipping.domain.model.voyage.VoyageNumber;

/**
 * Thrown when trying to register an event with an unknown carrier movement id.
 */
public class UnknownVoyageException extends CannotCreateHandlingEventException {

  private final VoyageNumber voyageNumber;

  public UnknownVoyageException(VoyageNumber voyageNumber) {
    this.voyageNumber = voyageNumber;
  }

  @Override
  public String getMessage() {
    return "No voyage with number " + voyageNumber.idString() + " exists in the system";
  }
}
