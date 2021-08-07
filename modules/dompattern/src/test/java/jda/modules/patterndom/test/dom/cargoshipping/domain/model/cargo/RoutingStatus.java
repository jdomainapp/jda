package jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo;

import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.ValueObject;

/**
 * Routing status. 
 */
public enum RoutingStatus implements ValueObject<RoutingStatus> {
  NOT_ROUTED, ROUTED, MISROUTED;

  @Override
  public boolean sameValueAs(final RoutingStatus other) {
    return this.equals(other);
  }
  
}
