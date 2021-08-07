package jda.modules.patterndom.test.dom.cargoshipping_original.domain.model.cargo;

import jda.modules.patterndom.test.dom.cargoshipping_original.domain.shared.ValueObject;

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
