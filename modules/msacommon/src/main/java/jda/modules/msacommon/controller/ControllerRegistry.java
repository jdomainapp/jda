package jda.modules.msacommon.controller;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @overview: Represents a registry of the standard modules in the service tree of this service. It maps the domain class's simple name to the controller object of the each module.
 */
@SuppressWarnings({"rawtypes"})
public final class ControllerRegistry {
  private static ControllerRegistry INSTANCE;

  public static ControllerRegistry getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ControllerRegistry();
    }
    return INSTANCE;
  }

  private final Map<String, DefaultController> controllerTypeMap;

  private ControllerRegistry() {
    this.controllerTypeMap = new ConcurrentHashMap<>();
  }

  public DefaultController get(String genericType) {
    for (Map.Entry<String, DefaultController> entry : controllerTypeMap.entrySet()) {
      if (entry.getKey().toLowerCase(Locale.ROOT)
          .contains(genericType.toLowerCase(Locale.ROOT).concat("controller"))) {
        return entry.getValue();
      }
    }
    return null;
  }

  public DefaultController get(Class cls) {
    String genericType = cls.getSimpleName();
    return get(genericType);
  }

  public void put(String genericType, DefaultController controllerInstance) {
    this.controllerTypeMap.put(genericType, controllerInstance);
  }


  public Iterable<? extends Map.Entry<String, DefaultController>> entrySet() {
    return controllerTypeMap.entrySet();
  }

  public boolean containsKey(String genericType, boolean ignoreCase) {
    if (!ignoreCase) {
      return controllerTypeMap.containsKey(genericType);
    } else {
      // search each entry to match key ignoring cases
      for (String key : controllerTypeMap.keySet()) {
        if (key.equalsIgnoreCase(genericType)) {
          return true;
        }
      }

      return false;
    }
  }

  /**
   * @requires {@link #controllerTypeMap} contains domainClsName as a key
   * @effects remove the entry whose key is <tt>domainClsName</tt> from {@link #controllerTypeMap}
   */
  public void remove(String genericType) {
    controllerTypeMap.remove(genericType);
  }
}
