package jda.modules.mosar.software.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @overview 
 *  Represents the output of {@link BEGen}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class BEGenOutput {

  /**
   * derived from {@link #services} and {@link #controllers}
   */
  private Collection<Class> comps;
  
  private Map<String, Class> services;
  private Collection<Class> controllers;

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public void setServices(Map<String, Class> services) {
    this.services = services;
    if (comps == null) {
      comps = new ArrayList<>();
      comps.addAll(services.values());
    }
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public void setControllers(Collection<Class> controllers) {
    this.controllers = controllers;
    if (comps == null) {
      comps = new ArrayList<>();
      comps.addAll(controllers);
    }
  }

  /**
   * @effects return services
   */
  public Map<String, Class> getServices() {
    return services;
  }

  /**
   * @effects return controllers
   */
  public Collection<Class> getControllers() {
    return controllers;
  }

  /**
   * @effects 
   * 
   */
  public Collection<Class> getComponents() {
    return comps;
  }

  /**
   * @effects 
   *  if this contains no components
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return comps == null || comps.isEmpty();
  }

  
  
}
