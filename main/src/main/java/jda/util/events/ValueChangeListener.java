package jda.util.events;

import java.util.EventListener;

/**
 * @overview
 *  Listens to the specific event that the value of a data field has been changed. The event source 
 *  is the data field object. 
 *  
 * @author dmle
 *
 */
public interface ValueChangeListener extends EventListener {
  /**
   * @effects 
   *   Handles the ChangeEvent e whose source is a data field object.
   */
  void fieldValueChanged(javax.swing.event.ChangeEvent e);
}

