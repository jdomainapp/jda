package jda.util.events;

import java.util.EventListener;

/**
 * @overview
 *  A clone of Java's ChangeListener to handle ChangeEvents
 *  
 * @author dmle
 */
public interface ChangeListener extends EventListener {
   /**
    * @effects 
    *   Handles the ChangeEvent e
    */
   void stateChanged(ChangeEvent e);
}
