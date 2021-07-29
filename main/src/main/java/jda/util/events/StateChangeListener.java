package jda.util.events;

import java.util.EventListener;

import jda.mosa.controller.assets.util.AppState;

/**
 * @overview
 *  A customised <tt>EventListener</tt> that carries application state and 
 *  an array of information message associated to that state.  
 *  
 *  <p>This class is used to update the status bar with application messages
 *   
 * @author dmle
 */
public interface StateChangeListener extends EventListener {
  
  /**
   * @effects 
   *  handle state change event associated to the application state <tt>state</tt>
   *  that was raised by <tt>src</tt> with the information message <tt>message</tt>
   *  and state data <tt>data</tt> 
   */
  public void stateChanged(Object src, AppState state, String messages, 
      Object...data);
  
  /**
   * @effects 
   *  return an array of AppState which this listener 
   *  is interested in listening or 
   *  null if this listener is interested in listening for changes in 
   *  all states
   */
  public AppState[] getStates();
}
