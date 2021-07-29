/**
 * 
 */
package jda.mosa.view.assets.util.function;

import jda.modules.common.exceptions.ApplicationRuntimeException;

/**
 * 
 * @overview
 *  Represents a function that is defined over the view components. For example, it could be a function to compute a value from 
 *  that of a given data field. 
 *   
 * @version 3.2c
 *
 * @author dmle
 */
public abstract class ViewFunction {
  /**
   * 
   * @effects 
   *  evaluates this and return the result
   *  <p>throws ApplicationRuntimeException if failes.
   *  
   * @throws ApplicationRuntimeException
   */
  public abstract Object eval() throws ApplicationRuntimeException;
}
