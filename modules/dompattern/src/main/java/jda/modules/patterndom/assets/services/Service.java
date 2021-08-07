package jda.modules.patterndom.assets.services;

import java.io.Serializable;
import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents a service in the SERVICES pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public interface Service<T> {
  
  /**
   * @effects 
   *  returns the globally unique id of this service. This id is used by the look-up operation.  
   */
  public Serializable getId();
  
  /**
   * @effects 
   *  Performs the service behaviour with input arguments <code>args</code> and 
   *  return the result of the type <code>T</code>. 
   *  
   *  Throws NotPossibleException if fails.
   */
  public T perform(Object...args) throws NotPossibleException;
  
  /**
   * @effects 
   *  if exists a {@link Service}, whose {@link #getId()} matches <code>id</code>
   *    return it
   *  else
   *    return null 
   *
   */
  public default Service<?> lookUp(Serializable id) {
    return ServiceRegistry.getInstance().lookUp(id);
  }
  
  /**
   * @effects 
   *  if this has not been registered to the service registry
   *    registers this
   *  else
   *    do nothing
   */
  public default void register() throws NotPossibleException {
    ServiceRegistry.getInstance().register(getId(), this);
  }
}
