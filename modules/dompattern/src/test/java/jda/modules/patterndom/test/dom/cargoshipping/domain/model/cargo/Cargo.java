package jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dodm.ObjectFactory;
import jda.modules.patterndom.assets.aggregates.AGRoot;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling.HandlingHistory;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.Location;
import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.DomainObjectUtils;
import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.Entity;
import jda.util.events.ChangeEventSource;

/**
 * A Cargo. This is the central class in the domain model, and it is the root of
 * the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 *
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking
 * procedure, when the tracking id is assigned. During a (short) period of time,
 * between booking and initial routing, the cargo has no itinerary.
 *
 * The booking clerk requests a list of possible routes, matching the route
 * specification, and assigns the cargo to one route. The route to which a cargo
 * is assigned is described by an itinerary.
 *
 * A cargo can be re-routed during transport, on demand of the customer, in
 * which case a new route is specified for the cargo and a new route is
 * requested. The old itinerary, being a value object, is discarded and a new
 * one is attached.
 *
 * It may also happen that a cargo is accidentally misrouted, which should
 * notify the proper personnel and also trigger a re-routing procedure.
 *
 * When a cargo is handled, the status of the delivery changes. Everything about
 * the delivery of the cargo is contained in the Delivery value object, which is
 * replaced whenever a cargo is handled by an asynchronous event triggered by
 * the registration of the handling event.
 *
 * The delivery can also be affected by routing changes, i.e. when the route
 * specification changes, or the cargo is assigned to a new route. In that case,
 * the delivery update is performed synchronously within the cargo aggregate.
 *
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 *
 * The cargo aggregate, and the entire domain model, is built to solve the
 * problem of booking and tracking cargo. All important business rules for
 * determining whether or not a cargo is misdirected, what the current status of
 * the cargo is (on board carrier, in port etc), are captured in this aggregate.
 *
 */
@DClass()
public class Cargo implements Entity<Cargo>, AGRoot {

  @DAttr(name="trackingId",id = true)
  private TrackingId trackingId;

  @DAttr(name="origin",optional=false)
  private Location origin;

  @DAttr(name="routeSpecification",optional=false)
  private RouteSpecification routeSpecification;

  @DAttr(name="itinerary",optional=false)
  private Itinerary itinerary;

  @DAttr(name="itinerary")
  private Delivery delivery;

  public Cargo(final TrackingId trackingId,
      final RouteSpecification routeSpecification) {
    Validate.notNull(trackingId, "Tracking ID is required");
    Validate.notNull(routeSpecification, "Route specification is required");
    this.trackingId = trackingId;
    // Cargo origin never changes, even if the route specification changes.
    // However, at creation, cargo orgin can be derived from the initial route
    // specification.
    this.origin = routeSpecification.origin();
    this.routeSpecification = routeSpecification;
    this.delivery = Delivery.derivedFrom(this.routeSpecification,
        this.itinerary, HandlingHistory.EMPTY);
  }

  /**
   * The tracking id is the identity of this entity, and is unique.
   * 
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return trackingId;
  }

  /**
   * @return Origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return The delivery. Never null.
   */
  public Delivery delivery() {
    return delivery;
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    return DomainObjectUtils.nullSafe(this.itinerary,
        Itinerary.EMPTY_ITINERARY);
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }

  /**
   * Specifies a new route for this cargo.
   *
   * @param routeSpecification
   *          route specification.
   */
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Validate.notNull(routeSpecification, "Route specification is required");
    this.routeSpecification = routeSpecification;
    // Handling consistency within the Cargo aggregate synchronously
    this.delivery = delivery.updateOnRouting(this.routeSpecification,
        this.itinerary);
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary
   *          an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Validate.notNull(itinerary, "Itinerary is required for assignment");
    this.itinerary = itinerary;
    // Handling consistency within the Cargo aggregate synchronously
    this.delivery = delivery.updateOnRouting(this.routeSpecification,
        this.itinerary);
  }

  /**
   * Updates all aspects of the cargo aggregate status based on the current
   * route specification, itinerary and handling of the cargo.
   * <p/>
   * When either of those three changes, i.e. when a new route is specified for
   * the cargo, the cargo is assigned to a route or when the cargo is handled,
   * the status must be re-calculated.
   * <p/>
   * {@link RouteSpecification} and {@link Itinerary} are both inside the Cargo
   * aggregate, so changes to them cause the status to be updated
   * <b>synchronously</b>, but changes to the delivery history (when a cargo is
   * handled) cause the status update to happen <b>asynchronously</b> since
   * {@link jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling.HandlingEvent}
   * is in a different aggregate.
   *
   * @param handlingHistory
   *          handling history
   */
  public void deriveDeliveryProgress(final HandlingHistory handlingHistory) {
    // TODO filter events on cargo (must be same as this cargo)
    // Delivery is a value object, so we can simply discard the old one
    // and replace it with a new
    this.delivery = Delivery.derivedFrom(routeSpecification(), itinerary(),
        handlingHistory);
  }

  @Override
  public boolean sameIdentityAs(final Cargo other) {
    return other != null && trackingId.sameValueAs(other.trackingId);
  }

  /**
   * @param object
   *          to compare
   * @return True if they have the same identity
   * @see #sameIdentityAs(Cargo)
   */
  @Override
  public boolean equals(final Object object) {
    if (this == object)
      return true;
    if (object == null || getClass() != object.getClass())
      return false;
    final Cargo other = (Cargo) object;
    return sameIdentityAs(other);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  @Override
  public String toString() {
    return trackingId.toString();
  }

  ////// TGC
  Cargo() {
    members1 = new ArrayList<>();
    // Needed by Hibernate
  }

  @DAttr(name = "id", type = Type.Serializable, id = true, optional = false)
  private Serializable id;

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "id")
  public Serializable getId() {
    return id;
  }

  @DAttr(name = "members", type = Type.Collection)
  @DAssoc(ascName = "has-members", role = "root", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = RouteSpecification.class, cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  private List<RouteSpecification> members1;

  /**
   * Factory method for creating member objects of the aggregate
   * 
   * @effects create and return a new member object <code>m</code> whose class
   *          is <code>memberCls</code> from arguments.
   * 
   *          <p>
   *          Throws NotFoundException if no constructors of
   *          <code>memberCls</code> match <code>args</code>; throws
   *          NotPossibleException if could not execute the constructor.
   */
  public static <T extends Publisher> T createMember(Class<T> memberCls,
      Object... args) throws NotFoundException, NotPossibleException {
    T member = ObjectFactory.createObject(memberCls, args);
    return member;
  }

  /**
   * @modifies {@link #members1}
   * @effects adds member to this and if all invariants are satisfied then
   *          register this as a Subscriber of <code>m</code>'s domain events,
   *          if <code>memberCls</code> is associated to this.class then add
   *          <code>m</code> to this, else throws ConstraintViolationException
   * @version
   *
   */
  public <T extends Publisher> void addMember(Class<T> memberCls, T member)
      throws NotFoundException, NotPossibleException {
    updateOnMemberAdded(memberCls, member);
  }

  /**
   * @modifies {@link #members1}
   * @effects create a new member object <code>m</code> whose class is
   *          <code>memberCls</code> from arguments and if all invariants are
   *          satisfied then register this as a Subscriber of <code>m</code>'s
   *          domain events, if <code>memberCls</code> is associated to
   *          this.class then add <code>m</code> this, return <code>m</code>
   *          else throws ConstraintViolationException
   * 
   *          <p>
   *          Throws NotFoundException if no constructors of
   *          <code>memberCls</code> match <code>args</code>; throws
   *          NotPossibleException if could not execute the constructor.
   */
  public <T extends Publisher> T addMember(Class<T> memberCls, Object... args)
      throws NotFoundException, NotPossibleException {
    T member = ObjectFactory.createObject(memberCls, args);
    updateOnMemberAdded(memberCls, member);
    return member;
  }

  private <T extends Publisher> void updateOnMemberAdded(Class<T> memberCls,
      T member) {
    // Pattern: DomainEvents
    EventType[] evtTypes = CMEventType.values();
    member.addSubscriber(this, evtTypes);
    // Linked member block: one for each member class directly associated to
    // this class
    /*
     * ducmle: modified for the Cargo's logic if (memberCls ==
     * RouteSpecification.class) { RouteSpecification m1 = (RouteSpecification)
     * member; this.members1.add(m1); // Pattern: Aggregates try {
     * updateOnMember1Added(m1); } catch (ConstraintViolationException e) { //
     * reverse the change (in reverse order of adding above)
     * this.members1.remove(m1); member.removeSubcriber(this, evtTypes); } }
     */
    // Pattern: Aggregates
    try {
      if (RouteSpecification.class.isAssignableFrom(memberCls)) {
        updateOnMember1Added((RouteSpecification) member);
      } else {
        updateOnItineraryAdded((Itinerary) member);
      }
    } catch (ConstraintViolationException e) {
      // reverse the change (in reverse order of adding above)
      // this.members1.remove(member);
      member.removeSubcriber(this, evtTypes);
    }

  }

  // Pattern: Aggregates
  @Override
  public void checkInvariants() throws ConstraintViolationException {
    // NEW
    this.delivery = delivery.updateOnRouting(this.routeSpecification,
        this.itinerary);
  }

  // Pattern: DomainEvent
  @Override
  public void handleEvent(EventType type, ChangeEventSource<?> source)
      throws ConstraintViolationException {
    CMEventType et = (CMEventType) type;
    Object srcObj = source.get(0);
    Object arg = source.get(1);
    switch (et) {
    case OnCreated:
      updateOnMember1Added((RouteSpecification) srcObj);
      break;
    case OnUpdated:
      // update on updated
      updateOnMember1Changed((RouteSpecification) srcObj, arg);
      break;
    case OnRemoved:
      updateOnMember1Removed((RouteSpecification) srcObj);
      break;
    }
  }

  /**
   * @effects update the state of this when <code>someMember</code> has been
   *          created and added to this. If some invariant is not satisfied,
   *          roll back update and rethrow {@link ConstraintViolationException}.
   */
  private void updateOnMember1Added(RouteSpecification someMember)
      throws ConstraintViolationException {
    // keep a record of prevState (before update)
    Object prevState = this.routeSpecification; // NEW

    // NEW
    // specifyNewRoute(someMember);
    this.routeSpecification = someMember;

    // Pattern: aggregates
    commitUpdate(prevState);
  }

  /**
   * @effects update the state of this when <code>someMember</code> has been
   *          created and added to this. If some invariant is not satisfied,
   *          roll back update and rethrow {@link ConstraintViolationException}.
   */
  private void updateOnItineraryAdded(Itinerary someMember)
      throws ConstraintViolationException {
    // keep a record of prevState (before update)
    Object prevState = this.itinerary; // NEW

    // NEW
    // specifyNewRoute(someMember);
    this.itinerary = someMember;

    // Pattern: aggregates
    commitUpdate(prevState);
  }

  /**
   * @effects update the state of this when <code>someMember</code>'s state is
   *          changed. If some invariant is not satisfied, roll back update and
   *          rethrow {@link ConstraintViolationException}.
   */
  private void updateOnMember1Changed(RouteSpecification someMember,
      Object... args) throws ConstraintViolationException {
    // keep a record of prevState (before update)
    Object prevState = null;
    // Pattern: aggregates
    commitUpdate(prevState);
  }

  /**
   * @effects update the state of this when <code>someMember</code> is removed.
   *          If some invariant is not satisfied, roll back update and rethrow
   *          {@link ConstraintViolationException}.
   */
  private void updateOnMember1Removed(RouteSpecification someMember,
      Object... args) throws ConstraintViolationException {
    // keep a record of prevState (before update)
    Object prevState = null;
    // Pattern: aggregates
    commitUpdate(prevState);
  }

  /**
   * @effects check all invariants of the concerned aggregate, if some invariant
   *          is violated then roll back the update based on
   *          <code>prevState</code>, rethrows
   *          {@link ConstraintViolationException}
   */
  private void commitUpdate(Object prevState)
      throws ConstraintViolationException {
    // Pattern: aggregates
    try {
      checkInvariants();
    } catch (ConstraintViolationException e) {
      // reverse the change
      rollbackUpdate(prevState);
      throw e;
    }
  }

  /**
   * @effects rollback the state of this to <code>prevState</code>
   */
  private void rollbackUpdate(Object prevState) {
    // NEW
    this.routeSpecification = (RouteSpecification) prevState;
  }
}
