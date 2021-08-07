package jda.modules.patterndom.test.dom.cargoshipping.domain.service;

import java.util.List;

import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.Itinerary;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.RouteSpecification;

/**
 * Routing service.
 *
 */
public interface RoutingService {

  /**
   * @param routeSpecification route specification
   * @return A list of itineraries that satisfy the specification. May be an empty list if no route is found.
   */
  List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification);

}
