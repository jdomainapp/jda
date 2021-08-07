package jda.modules.patterndom.test.cargoshipping;

import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.RoutingStatus.MISROUTED;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.RoutingStatus.NOT_ROUTED;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.RoutingStatus.ROUTED;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.TransportStatus.NOT_RECEIVED;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.GOTHENBURG;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.HAMBURG;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.HANGZOU;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.HONGKONG;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.MELBOURNE;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.NEWYORK;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.ROTTERDAM;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.SHANGHAI;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.STOCKHOLM;
import static jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.SampleLocations.TOKYO;

/**
 * @overview 
 *  An adapted version of CargoTest that uses the Aggregates design pattern. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import jda.modules.patterndom.test.dom.cargoshipping.application.util.DateTestUtil;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.Cargo;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.Itinerary;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.Leg;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.RouteSpecification;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo.TrackingId;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling.HandlingEvent;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.handling.HandlingHistory;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.location.Location;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.voyage.Voyage;
import jda.modules.patterndom.test.dom.cargoshipping.domain.model.voyage.VoyageNumber;

public class CargoTest {

  private List<HandlingEvent> events;
  private Voyage voyage;

  @Before
  public void setUp() {
    events = new ArrayList<HandlingEvent>();

    voyage = new Voyage.Builder(new VoyageNumber("0123"), STOCKHOLM).
      addMovement(HAMBURG, new Date(), new Date()).
      addMovement(HONGKONG, new Date(), new Date()).
      addMovement(MELBOURNE, new Date(), new Date()).
      build();
  }

  @Test
  public void testConstruction() {
    final TrackingId trackingId = new TrackingId("XYZ");
    final Date arrivalDeadline = DateTestUtil.toDate("2009-03-13");
    final RouteSpecification routeSpecification = 
        new RouteSpecification(
      STOCKHOLM, MELBOURNE, arrivalDeadline
    );

    final Cargo cargo = new Cargo(trackingId, routeSpecification);

    assertThat(cargo.delivery().routingStatus()).isEqualTo(NOT_ROUTED);
    assertThat(cargo.delivery().transportStatus()).isEqualTo(NOT_RECEIVED);
    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
    assertThat(cargo.delivery().currentVoyage()).isEqualTo(Voyage.NONE);    
  }

  // ducmle
  static final Itinerary good = Cargo.createMember(Itinerary.class); //new Itinerary();

  /**
   * @overview 
   *  created to use with pattern
   * @author Duc Minh Le (ducmle)
   */
  public static class AcceptOnlyGoodRoutingSpec extends RouteSpecification {
    public AcceptOnlyGoodRoutingSpec(final Location origin, final Location destination, final Date arrivalDeadline) {
      super(origin, destination, arrivalDeadline);
    }

    @Override
    public boolean isSatisfiedBy(Itinerary itinerary) {
      return itinerary == good;
    }
  }
  
  @Test // ducmle: updated to work with the new Cargo's Aggregates design 
  public void testRoutingStatus() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), 
        new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));
    /* ducmle
    final Itinerary bad = new Itinerary();
    final Itinerary good = new Itinerary();
    
    final RouteSpecification acceptOnlyGood = new RouteSpecification(
    cargo.origin(), cargo.routeSpecification().destination(), new Date()) {
      @Override
      public boolean isSatisfiedBy(Itinerary itinerary) {
        return itinerary == good;
      }
    };
    cargo.specifyNewRoute(acceptOnlyGood);
    */
    cargo.addMember(AcceptOnlyGoodRoutingSpec.class, 
        cargo.origin(), cargo.routeSpecification().destination(), new Date());
    
    assertThat(cargo.delivery().routingStatus()).isEqualTo(NOT_ROUTED);
    
    /* ducmle
    cargo.assignToRoute(bad);
    */
    cargo.addMember(Itinerary.class);
    
    assertThat(cargo.delivery().routingStatus()).isEqualTo(MISROUTED);
    
    /* ducmle
    cargo.assignToRoute(good);
    */
    cargo.addMember(
        Itinerary.class,
        good
        );
    
    assertThat(cargo.delivery().routingStatus()).isEqualTo(ROUTED);
  }

  @Test
  public void testlastKnownLocationUnknownWhenNoEvents() {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(Location.UNKNOWN);
  }

  @Test
  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(STOCKHOLM);
  }

  @Test
  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(MELBOURNE);
  }

  @Test
  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(HONGKONG);
  }

  @Test
  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertThat(cargo.delivery().lastKnownLocation()).isEqualTo(HAMBURG);
  }

  @Test
  public void testEquality() {
    RouteSpecification spec1 = new RouteSpecification(STOCKHOLM, HONGKONG, new Date());
    RouteSpecification spec2 = new RouteSpecification(STOCKHOLM, MELBOURNE, new Date());
    Cargo c1 = new Cargo(new TrackingId("ABC"), spec1);
    Cargo c2 = new Cargo(new TrackingId("CBA"), spec1);
    Cargo c3 = new Cargo(new TrackingId("ABC"), spec2);
    Cargo c4 = new Cargo(new TrackingId("ABC"), spec1);

    assertThat(c1.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c1.equals(c3)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c3.equals(c4)).as("Cargos should be equal when TrackingIDs are equal").isTrue();
    assertThat(c1.equals(c2)).as("Cargos are not equal when TrackingID differ").isFalse();
  }

  @Test
  public void testIsUnloadedAtFinalDestination() {
    Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Adding an event unrelated to unloading at final destination
    events.add(
      new HandlingEvent(cargo, new Date(10), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"), HANGZOU).
      addMovement(NEWYORK, new Date(), new Date()).
      build();

    // Adding an unload event, but not at the final destination
    events.add(
      new HandlingEvent(cargo, new Date(20), new Date(), HandlingEvent.Type.UNLOAD, TOKYO, voyage));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Adding an event in the final destination, but not unload
    events.add(
      new HandlingEvent(cargo, new Date(30), new Date(), HandlingEvent.Type.CUSTOMS, NEWYORK));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isFalse();

    // Finally, cargo is unloaded at final destination
    events.add(
      new HandlingEvent(cargo, new Date(40), new Date(), HandlingEvent.Type.UNLOAD, NEWYORK, voyage));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isUnloadedAtDestination()).isTrue();
  }

  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, STOCKHOLM);
    events.add(he);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    events.add(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, MELBOURNE));
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));


    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, MELBOURNE, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, voyage));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    return cargo;
  }

  @Test
  public void testIsMisdirected() {
    //A cargo with no itinerary is not misdirected
    Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(SHANGHAI, GOTHENBURG, new Date()));
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    //A cargo with no handling events is not misdirected
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();

    //Happy path
    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(90), new Date(100), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(110), new Date(120), HandlingEvent.Type.CLAIM, GOTHENBURG));
    handlingEvents.add(new HandlingEvent(cargo, new Date(130), new Date(140), HandlingEvent.Type.CUSTOMS, GOTHENBURG));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));
    assertThat(cargo.delivery().isMisdirected()).isFalse();

    //Try a couple of failing ones

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM));

    events.addAll(handlingEvents);
    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    assertThat(cargo.delivery().isMisdirected()).isTrue();
  }

  private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), new RouteSpecification(origin, destination, new Date()));

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, origin, midpoint, new Date(), new Date()),
        new Leg(voyage, midpoint, destination, new Date(), new Date())
      )
    );

    cargo.assignToRoute(itinerary);
    return cargo;
  }

  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws ParseException Thrown if parsing fails.
   */
  private Date getDate(String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }
}
