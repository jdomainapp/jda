package jda.modules.patterndom.assets.domevents;

import java.util.Iterator;

import jda.modules.common.types.Tuple2;
import jda.util.events.ChangeEventSource;

/**
 * @overview Represents publishers of events.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public interface Publisher {
  
  /** the ChangeManager component of the Observer pattern */
  static final ChangeManager changeMan = new ChangeManager();
  
//  /**
//   * @effects 
//   *  register <tt>sub</tt> to the anonymous subscriber list {@link #anonSubs}
//   */
//  public default void addSubscriber(Subscriber sub) {
//    changeMan.addAnonymousSubscriber(this, sub);
//  }
//
//  /**
//   * @effects 
//   *  remove <tt>sub</tt> from the anonymous subscriber list {@link #anonSubs}. 
//   */
//  public default void removeSubscriber(Subscriber sub) {
//    changeMan.removeAnonymousSubscriber(this, sub);
//  }

  /**
   * @requires eventTypes != null
   * @effects 
   *  register <tt>sub</tt> to become a subscriber of the {@link EventType} specified by 
   *  <tt>eventTypes</tt>. 
   *  If <tt>eventTypes.length == 0</tt> then <tt>sub</tt> subscribes to all types of event.
   */
  public default void addSubscriberByEvent(Subscriber sub, EventType...eventTypes) {
    if (eventTypes == null) return;
    
    changeMan.addSubscriberByEvent(sub, eventTypes);
  }

  /**
   * @requires eventTypes != null
   * @effects 
   *  remove <tt>sub</tt> from each subscriber list of the {@link EventType}s in 
   *  <tt>eventTypes</tt>. 
   */
  public default void removeSubcriberByEvent(Subscriber sub, EventType...eventTypes) {
    if (eventTypes == null) return;
    
    changeMan.removeSubscriberByEvent(sub, eventTypes);
  }

  /**
   * @requires eventTypes != null
   * @effects 
   *  add <tt>sub</tt> to each subscriber list of <tt>this</tt> identified by every {@link EventType} in 
   *  <tt>eventTypes</tt>. 
   *  If <tt>eventTypes.length == 0</tt> then <tt>sub</tt> subscribes to no lists.
   */
  public default void addSubscriber(Subscriber sub, EventType...eventTypes) {
    if (eventTypes == null) return;
    
    changeMan.addSubscriber(this, sub, eventTypes);
  }

  /**
   * @requires eventTypes != null
   * @effects 
   *  remove <tt>sub</tt> from each subscriber list of <tt>this</tt> identified by {@link EventType}s in 
   *  <tt>eventTypes</tt>. 
   */
  public default void removeSubcriber(Subscriber sub, EventType...eventTypes) {
    if (eventTypes == null) return;
    
    changeMan.removeSubscriber(this, sub, eventTypes);
  }
  
  /**
   * @effects
   *  remove all subscribers of this 
   */
  public default void removeAllSubscribers() {
    changeMan.clear();
  }
  
  /**
   * NOTE: Subtypes implement this method by declaring a {@link ChangeEventSource} static field 
   * and using either {@link #createEventSource(Class)} or {@link #resetEventSource(ChangeEventSource)} to create/reset
   * its state
   *  
   * @modifies this
   * @effects 
   *   if the {@link ChangeEventSource} of this has not been initialised 
   *    initialise it
   *   else
   *    clear its data
   *    
   *   return {@link ChangeEventSource}
   */
  public ChangeEventSource<?> getEventSource();
  
  /**
   * @effects 
   *      initialise a {@link ChangeEventSource} for <tt>pubCls</tt>
   *    return it
   */
  public default <T extends Publisher> ChangeEventSource<?> createEventSource(Class<T> pubCls) {
    ChangeEventSource evtSrc = new ChangeEventSource(pubCls);
    evtSrc.add(this);
    return evtSrc;
  }

  /**
   * @effects
   *  reset <tt>evtSrc</tt> to not contain any additional test data.
   *  That is reset its state to be the same as that produced by {@link #createEventSource(Class)}.
   */
  public default void resetEventSource(ChangeEventSource evtSrc) {
    if (evtSrc.size() > 1) {
      evtSrc.clear();
      evtSrc.add(this);
    }
  }

  
//  /**
//   * Sub-types of this interface must implement this method. Part of this implementation is to use {@link #notify(EventType, ChangeEventSource)}
//   * to actually send the event notifcations to target programs. 
//   * 
//   * @requires 
//   *  <tt>type neq null</tt>
//   * @effects
//   *  inform all interested subscribers of the occurrence of event <tt>type</tt>, whose source is 
//   *  a {@link ChangeEventSource} that encapsulates the state changes that have occured is this object. 
//   */
//  public void notify(EventType type, Object...data);

  /**
   * A specialised <code>notify</code> method that supports using an array of {@link Tuple2}
   * for the data. The intended usage is to use Tuple2&lt;String,Object&gt;, where the String element
   * is the attribute name and the Object element is the old data value of the attribute. 
   * 
   * @requires 
   *  <tt>type, source neq null</tt>
   * @effects
   *  inform all interested subscribers of <tt>this</tt> of the occurrence of event <tt>type</tt>. 
   */
  public default void notifyStateChanged(EventType type, ChangeEventSource<?> source, Tuple2<?,?>...data) {
    notify(type, source, data);
  }
  
  /**
   * @requires 
   *  <tt>type, source neq null</tt>
   * @effects
   *  inform all interested subscribers of <tt>this</tt> of the occurrence of event <tt>type</tt>. 
   */
  public default void notify(EventType type, ChangeEventSource<?> source, Object...data) {
    
    Iterator<Subscriber> subIt = changeMan.getSubscribersIt(this, type);

    notifySubs(subIt, type, source, data);
  }

  /**
   * @requires 
   *  <tt>type, source neq null</tt>
   * @effects
   *  adds <tt>data</tt> to <tt>source</tt>, 
   *  inform all interested subscribers of the occurrence of event <tt>type</tt>. 
   */
  public default void notifyByEvent(EventType type, ChangeEventSource<?> source, Object...data) {
    Iterator<Subscriber> subIt = changeMan.getSubscribersByEventIt(type);
    
    notifySubs(subIt, type, source, data);
  }
  
  
  /**
   * @modifies source
   * @requires 
   *  <tt>type, source neq null</tt>
   * @effects
   *  adds <tt>data</tt> to <tt>source</tt>, 
   *  inform all subscribers in <tt>subIt</tt> of the occurrence of event <tt>type</tt> with <tt>source</tt> and 
   *  <tt>data</tt>
   */
  public default void notifySubs(Iterator<Subscriber> subIt, EventType type, ChangeEventSource source, Object...data) {
    if (subIt != null ) {
      if (data != null) {
        for (Object d: data) source.add(d);
      }

      while (subIt.hasNext()) {
        subIt.next().handleEvent(type, source);
      }
    }
  }
}
