package jda.modules.patterndom.assets.domevents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @overview Implements ChangeManager component of the Publish/Subscrib (Observer) pattern.
 * It helps adds a layer of abstraction for maintaining the subscriber/publisher relationship.
 * It is used when this relationship is complex.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ChangeManager {
  /**
   * named subscribers, identified by the event types that they are interested in  
   */
  private Map<EventType, List<Subscriber>> subsByTypeMap;

  /**
   * named subscribers, identified by the event types that they are interested in and the publisher
   * to which they are registered   
   */
  private Map<EventType, Map<Publisher, List<Subscriber>>> pubSubsByTypeMap;

//  /**
//   * anonymous subscribers, not identifed by the event types; i.e. they are interested in all events
//   */
//  private Collection<Subscriber> subs;

  /**
   * @effects initialise this to be empty 
   *
   */
  public ChangeManager() {
//    subs = new ArrayList<>();
    subsByTypeMap = new HashMap<>();
    pubSubsByTypeMap = new HashMap<>();
  }
  
  /**
   * @requires sub, eventTypes != null
   * @effects 
   *  add sub to the subscriber list identified by every event type in <tt>eventTypes</tt>
   */
  public void addSubscriberByEvent(Subscriber sub,
      EventType... eventTypes) {
    if (sub == null || eventTypes == null) return;

    for (EventType et : eventTypes) {
      List<Subscriber> subList = subsByTypeMap.get(et);
      if (subList == null) {
        subList = new ArrayList<>();
        subsByTypeMap.put(et, subList);
        subList.add(sub);
      } else {
        if (!subList.contains(sub)) {
          subList.add(sub);
        }
      }
    }    
  }


  /**
   * @requires pub, sub, evenTypes != null, 
   * @effects 
   *  remove <tt>sub</tt> from each subscriber list identified by event types in <tt>eventTypes</tt> 
   */
  public void removeSubscriberByEvent(Subscriber sub,
      EventType...eventTypes) {
    if (sub == null || eventTypes == null) return;
    
    for (EventType et : eventTypes) {
      List<Subscriber> subList = subsByTypeMap.get(et);
      if (subList != null && subList.contains(sub)) {
        subList.remove(sub);
      }
    }    
  }
  
  /**
   * @requires pub, sub, evenTypes != null, 
   * @effects 
   *  add sub to each pub's subscriber list identified by every event type in <tt>eventTypes</tt>
   */
  public void addSubscriber(Publisher pub, Subscriber sub,
      EventType... eventTypes) {
    if (pub == null || sub == null || eventTypes == null) return;

    for (EventType et : eventTypes) {
      List<Subscriber> subList = null;
      Map<Publisher, List<Subscriber>> pubSubsMap = pubSubsByTypeMap.get(et);
      if (pubSubsMap == null) {
        pubSubsMap = new HashMap<>();
        pubSubsByTypeMap.put(et, pubSubsMap);
      } else {
        subList = pubSubsMap.get(pub);
      }
      
      if (subList == null) {
        subList = new ArrayList<>();
        pubSubsMap.put(pub, subList);
        subList.add(sub);
      } else {
        if (!subList.contains(sub)) {
          subList.add(sub);
        }
      }
    }    
  }

//  /**
//   * @effects 
//   *   
//   */
//  public void addAnonymousSubscriber(Publisher pub, Subscriber sub) {
//  }
//
//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  public void removeAnonymousSubscriber(Publisher pub, Subscriber sub) {
//  }

  /**
   * @requires pub, sub, evenTypes != null, 
   * @effects 
   *  remove <tt>sub</tt> from each pub's subscriber list identified by every event type in <tt>eventTypes</tt> 
   */
  public void removeSubscriber(Publisher pub, Subscriber sub,
      EventType...eventTypes) {
    if (pub == null || sub == null || eventTypes == null) return;
    
    for (EventType et : eventTypes) {
      Map<Publisher, List<Subscriber>> pubSubs = pubSubsByTypeMap.get(et);
      if (pubSubs != null) {
        List<Subscriber> subList = pubSubs.get(pub);
        if (subList != null && subList.contains(sub)) {
          subList.remove(sub);
        }
      }
    }    
  }

//  /**
//   * @effects 
//   * 
//   * @version 
//   * 
//   */
//  public Collection<Subscriber> getAnonymousSubcribers(Publisher pub) {
//    // TODO Auto-generated method stub
//    return null;
//  }

  /**
   * @requires pub, eventType != null
   * @effects 
   *  if exists a subscriber list of <tt>pub</tt> identified by <tt>eventType</tt>
   *    return it
   *  else
   *    return null 
   */
  public Collection<Subscriber> getSubscribers(Publisher pub,
      EventType eventType) {
    if (pub == null || eventType == null) return null;
    
    Map<Publisher, List<Subscriber>> pubSubs = pubSubsByTypeMap.get(eventType);
    
    if (pubSubs != null) {
      return pubSubs.get(pub);
    } else {
      return null;
    }
  }

  /**
   * @requires pub, eventType != null
   * @effects 
   *  if exists a subscriber list of <tt>pub</tt> identified by <tt>eventType</tt>
   *    return an {@link Iterator} of it
   *  else
   *    return null 
   */
  public Iterator<Subscriber> getSubscribersIt(Publisher pub,
      EventType eventType) {
    Collection subs = getSubscribers(pub, eventType);
    
    if (subs != null) {
      return subs.iterator();
    } else {
      return null;
    }
  }

  /**
   * @requires pub, eventType != null
   * @effects 
   *  if exists a subscriber list of the event identified by <tt>eventType</tt>
   *    return it
   *  else
   *    return null 
   */
  public Collection<Subscriber> getSubscribers(EventType eventType) {
    if (eventType == null) return null;
    
    List<Subscriber> subs = subsByTypeMap.get(eventType);
    
    if (subs != null) {
      return subs;
    } else {
      return null;
    }
  }

  /**
   * @requires pub, eventType != null
   * @effects 
   *  if exists a subscriber list of the event identified by <tt>eventType</tt>
   *    return an {@link Iterator} of it
   *  else
   *    return null 
   */
  public Iterator<Subscriber> getSubscribersByEventIt(EventType eventType) {
    
    Collection<Subscriber> subs = getSubscribers(eventType);
    
    if (subs != null) {
      return subs.iterator();
    } else {
      return null;
    }
  }

  
  /**
   * @effects 
   *  makes this to be empty
   */
  public void clear() {
    subsByTypeMap.clear();
    pubSubsByTypeMap.clear();
//    subs.clear();    
  }
}
