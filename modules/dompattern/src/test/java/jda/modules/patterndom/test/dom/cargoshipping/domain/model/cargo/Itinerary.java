package jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.Validate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.Leg;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling.HandlingEvent;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.Location;
import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.ValueObject;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.AttrRef;
import java.io.Serializable;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Subscriber;
import jda.util.events.ChangeEventSource;

/**
 * An itinerary.
 *
 */
@DClass
public class Itinerary implements ValueObject<Itinerary>, Publisher {

    @DAttr(name = "legs", type = Type.Collection)
    private List<Leg> legs = Collections.emptyList();

    static final Itinerary EMPTY_ITINERARY = new Itinerary();

    private static final Date END_OF_DAYS = new Date(Long.MAX_VALUE);

    /**
   * Constructor.
   *
   * @param legs List of legs for this itinerary.
   */
    public Itinerary(final List<Leg> legs) {
        Validate.notEmpty(legs);
        Validate.noNullElements(legs);
        this.legs = legs;
    }

    /**
   * @return the legs of this itinerary, as an <b>immutable</b> list.
   */
    public List<Leg> legs() {
        return Collections.unmodifiableList(legs);
    }

    /**
   * Test if the given handling event is expected when executing this itinerary.
   *
   * @param event Event to test.
   * @return <code>true</code> if the event is expected
   */
    public boolean isExpected(final HandlingEvent event) {
        if (legs.isEmpty()) {
            return true;
        }
        if (event.type() == HandlingEvent.Type.RECEIVE) {
            //Check that the first leg's origin is the event's location
            final Leg leg = legs.get(0);
            return (leg.loadLocation().equals(event.location()));
        }
        if (event.type() == HandlingEvent.Type.LOAD) {
            //Check that the there is one leg with same load location and voyage
            for (Leg leg : legs) {
                if (leg.loadLocation().sameIdentityAs(event.location()) && leg.voyage().sameIdentityAs(event.voyage()))
                    return true;
            }
            return false;
        }
        if (event.type() == HandlingEvent.Type.UNLOAD) {
            //Check that the there is one leg with same unload location and voyage
            for (Leg leg : legs) {
                if (leg.unloadLocation().equals(event.location()) && leg.voyage().equals(event.voyage()))
                    return true;
            }
            return false;
        }
        if (event.type() == HandlingEvent.Type.CLAIM) {
            //Check that the last leg's destination is from the event's location
            final Leg leg = lastLeg();
            return (leg.unloadLocation().equals(event.location()));
        }
        //HandlingEvent.Type.CUSTOMS;
        return true;
    }

    /**
   * @return The initial departure location.
   */
    Location initialDepartureLocation() {
        if (legs.isEmpty()) {
            return Location.UNKNOWN;
        } else {
            return legs.get(0).loadLocation();
        }
    }

    /**
   * @return The final arrival location.
   */
    Location finalArrivalLocation() {
        if (legs.isEmpty()) {
            return Location.UNKNOWN;
        } else {
            return lastLeg().unloadLocation();
        }
    }

    /**
   * @return Date when cargo arrives at final destination.
   */
    Date finalArrivalDate() {
        final Leg lastLeg = lastLeg();
        if (lastLeg == null) {
            return new Date(END_OF_DAYS.getTime());
        } else {
            return new Date(lastLeg.unloadTime().getTime());
        }
    }

    /**
   * @return The last leg on the itinerary.
   */
    Leg lastLeg() {
        if (legs.isEmpty()) {
            return null;
        } else {
            return legs.get(legs.size() - 1);
        }
    }

    /**
   * @param other itinerary to compare
   * @return <code>true</code> if the legs in this and the other itinerary are all equal.
   */
    @Override
    public boolean sameValueAs(final Itinerary other) {
        return other != null && legs.equals(other.legs);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Itinerary itinerary = (Itinerary) o;
        return sameValueAs(itinerary);
    }

    @Override
    public int hashCode() {
        return legs.hashCode();
    }

    public Itinerary() {
        // fire OnCreated event
        notify(CMEventType.OnCreated, getEventSource());
    // Needed by Hibernate
    }

    @DAttr(name = "id", type = Type.Serializable, id = true, optional = false)
    private Serializable id;

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "id")
    public Serializable getId() {
        return id;
    }

    private static ChangeEventSource evtSrc;

    /**
   * @effects update this and fire OnUpdated event.
   */
    public void updateState() {
        // keep the previous state value
        Object prevState = null;
        // fire OnCreated event
        notify(CMEventType.OnUpdated, getEventSource(), prevState);
    }

    /**
   * @effects
   *  notify register all registered listeners 
   */
    @Override
    public void finalize() throws Throwable {
        notify(CMEventType.OnRemoved, getEventSource());
    }

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
