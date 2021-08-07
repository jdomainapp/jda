package jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling;

import org.apache.commons.lang.Validate;

import jda.modules.patterndom.test.dom.cargoshipping.domain.shared.ValueObject;

import java.util.*;

import static java.util.Collections.sort;

/**
 * The handling history of a cargo.
 */
public class HandlingHistory implements ValueObject<HandlingHistory> {

    private final List<HandlingEvent> handlingEvents;

    public static final HandlingHistory EMPTY = new HandlingHistory(Collections.<HandlingEvent>emptyList());

    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        Validate.notNull(handlingEvents, "Handling events are required");

        this.handlingEvents = new ArrayList<>(handlingEvents);
    }

    /**
     * @return A distinct list (no duplicate registrations) of handling events, ordered by completion time.
     */
    public List<HandlingEvent> distinctEventsByCompletionTime() {
        final List<HandlingEvent> ordered = new ArrayList<>(
                new HashSet<>(handlingEvents)
        );
        sort(ordered, BY_COMPLETION_TIME_COMPARATOR);
        return Collections.unmodifiableList(ordered);
    }

    /**
     * @return Most recently completed event, or null if the delivery history is empty.
     */
    public HandlingEvent mostRecentlyCompletedEvent() {
        final List<HandlingEvent> distinctEvents = distinctEventsByCompletionTime();
        if (distinctEvents.isEmpty()) {
            return null;
        } else {
            return distinctEvents.get(distinctEvents.size() - 1);
        }
    }

    @Override
    public boolean sameValueAs(HandlingHistory other) {
        return other != null && this.handlingEvents.equals(other.handlingEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HandlingHistory other = (HandlingHistory) o;
        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return handlingEvents.hashCode();
    }

    private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR =
            (he1, he2) -> he1.completionTime().compareTo(he2.completionTime());

}
