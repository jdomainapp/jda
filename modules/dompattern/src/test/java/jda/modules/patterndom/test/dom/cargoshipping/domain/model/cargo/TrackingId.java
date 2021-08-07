package jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo;

import org.apache.commons.lang.Validate;

import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.ValueObject;

/**
 * Uniquely identifies a particular cargo. Automatically generated by the application.
 *  
 */
public final class TrackingId implements ValueObject<TrackingId> {

  private String id;

  /**
   * Constructor.
   *
   * @param id Id string.
   */
  public TrackingId(final String id) {
    Validate.notNull(id);
    this.id = id;
  }

  /**
   * @return String representation of this tracking id.
   */
  public String idString() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TrackingId other = (TrackingId) o;

    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean sameValueAs(TrackingId other) {
    return other != null && this.id.equals(other.id);
  }

  @Override
  public String toString() {
    return id;
  }

  TrackingId() {
    // Needed by Hibernate
  }
}
