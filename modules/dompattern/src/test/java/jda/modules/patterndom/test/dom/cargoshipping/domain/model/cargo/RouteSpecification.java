package jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo;

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.Location;
import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.AbstractSpecification;
import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.ValueObject;
import jda.util.events.ChangeEventSource;

/**
 * Route specification. Describes where a cargo origin and destination is,
 * and the arrival deadline.
 * 
 */
@DClass(mutable = false)
public class RouteSpecification 
extends AbstractSpecification<Itinerary> 
implements ValueObject<RouteSpecification>, Publisher {

  @DAttr(name="origin",optional=false)
  private Location origin;
  @DAttr(name="destination",optional=false)
  private Location destination;
  @DAttr(name="arrivalDeadline",optional=false)
  private Date arrivalDeadline;

  /**
   * @param origin origin location - can't be the same as the destination
   * @param destination destination location - can't be the same as the origin
   * @param arrivalDeadline arrival deadline
   */
  public RouteSpecification(final Location origin, final Location destination, final Date arrivalDeadline) {
    Validate.notNull(origin, "Origin is required");
    Validate.notNull(destination, "Destination is required");
    Validate.notNull(arrivalDeadline, "Arrival deadline is required");
    Validate.isTrue(!origin.sameIdentityAs(destination), "Origin and destination can't be the same: " + origin);

    this.origin = origin;
    this.destination = destination;
    this.arrivalDeadline = (Date) arrivalDeadline.clone();
  }

  /**
   * @return Specified origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return Specfied destination location.
   */
  public Location destination() {
    return destination;
  }

  /**
   * @return Arrival deadline.
   */
  public Date arrivalDeadline() {
    return new Date(arrivalDeadline.getTime());
  }

  @Override
  public boolean isSatisfiedBy(final Itinerary itinerary) {
    return itinerary != null &&
           origin().sameIdentityAs(itinerary.initialDepartureLocation()) &&
           destination().sameIdentityAs(itinerary.finalArrivalLocation()) &&
           arrivalDeadline().after(itinerary.finalArrivalDate());
  }

  @Override
  public boolean sameValueAs(final RouteSpecification other) {
    return other != null && new EqualsBuilder().
      append(this.origin, other.origin).
      append(this.destination, other.destination).
      append(this.arrivalDeadline, other.arrivalDeadline).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final RouteSpecification that = (RouteSpecification) o;

    return sameValueAs(that);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(this.origin).
      append(this.destination).
      append(this.arrivalDeadline).
      toHashCode();
  }

  RouteSpecification() {
    // Needed by Hibernate
  }

  // Pattern: Publisher
  /** 
   * A reusable event source object that is shared among objects of this class. 
   * This is used where a single publiser is shared among different subscribers
   */
  private static ChangeEventSource<?> evtSrc;
  
  @Override
  public ChangeEventSource<?> getEventSource() {
    if (evtSrc == null) {
      evtSrc = createEventSource(this.getClass());
    } else {
      resetEventSource(evtSrc);
    }
    
    return evtSrc;
  }
  
}
