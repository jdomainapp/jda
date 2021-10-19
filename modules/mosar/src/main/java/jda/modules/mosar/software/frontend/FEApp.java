package jda.modules.mosar.software.frontend;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents the target platform-specific frontend app that is executed.  
 *  
 *  <p>Example: a concrete front-end app implementation would be for React.js.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public interface FEApp extends Runnable {
  /**
   * @effects 
   *  executes this app. 
   *  Throws NotPossibleException if fails. 
   */
  @Override
  public void run() throws NotPossibleException; 

  /**
   * @effects 
   *  executes this app in a thread.
   *  Throws NotPossibleException if fails. 
   */
  public default void runThreaded() throws NotPossibleException {
    Thread t = new Thread(this);
//    t.setDaemon(true);
    t.start();
//    t.join();
  }
}

