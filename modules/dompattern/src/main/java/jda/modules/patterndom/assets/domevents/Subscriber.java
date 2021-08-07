package jda.modules.patterndom.assets.domevents;

import jda.util.events.ChangeEventSource;

/**
 * @overview Represents subscribers of events.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public interface Subscriber {

  /**
   * @effects 
   *  handle <tt>event</tt> whose type is <tt>type</tt> that was raised from <tt>source</tt>
   */
  void handleEvent(EventType type, ChangeEventSource<?> source);

}
