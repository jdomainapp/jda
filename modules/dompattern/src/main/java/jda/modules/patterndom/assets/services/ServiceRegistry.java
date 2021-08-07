package jda.modules.patterndom.assets.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  Represents a registry for {@link Service}.
 *  
 *  It is a singleton class.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ServiceRegistry {
  private Map<Serializable, Service<?>> registry;
  
  private static ServiceRegistry instance;
  
  private ServiceRegistry() {
    //
    registry = new HashMap<>();
  }
  
  /**
   * @effects 
   *   returns the single instance of this 
   */
  public static ServiceRegistry getInstance() {
    if (instance == null) {
      instance = new ServiceRegistry();
    } 
    return instance;
  }

  /**
   * @effects 
   *  if exists a {@link Service}, whose id matches <code>id</code>
   *    return it;
   *    throws NotPossibleException if failed to look up.
   *  else
   *    return null 
   */
  public Service<?> lookUp(Serializable id) throws NotPossibleException {
    return registry.get(id);
  }

  /**
   * @effects 
   *  if <code>service</code> has not been registered to this
   *    registers it; 
   *    throws NotPossibleException if fails to register
   *  else
   *    do nothing
   */
  public void register(Serializable id, Service<?> service) throws NotPossibleException {
    if (!registry.containsKey(id)) {
      registry.put(id, service);
    }
  }
  
  
}
