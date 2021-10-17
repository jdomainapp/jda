package jda.modules.mosar.software.backend;

import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents the target platform-specific back app that is executed.  
 *  
 *  <p>Example: a concrete backend app implementation would be for SpringBoot.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public interface BEApp {
  
  /**
   * @effects 
   *  executes this app. 
   *  Throws NotPossibleException if fails. 
   */
  public void run(Collection<? extends Class> components) throws NotPossibleException; 

  /**
   * @effects 
   *  executes this app in a thread.
   *  Throws NotPossibleException if fails. 
   */
  public default void runThreaded(final Collection<? extends Class> components) throws NotPossibleException {
    new Thread() {
      @Override
      public void run() {
        BEApp.this.run(components);
      }
    }.start();
  }
}

