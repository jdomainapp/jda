package jda.modules.msacommon.controller;

import jda.modules.common.io.ToolkitIO;

import javax.json.JsonObject;
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

  /**
   * @requires
   *  <tt>cls</tt> is the name of the domain class managed by the Controller being looked-up. It is specified as the first
   *  generic type of the controller class (e.g. <tt>AddressController&lt;Address,Integer&gt;</tt>)
   *
   * @effects
   *  return the {@link DefaultController} whose domain class is <tt>cls</tt> that is stored in this
   */
  public DefaultController get(String genericType) {
    for (Map.Entry<String, DefaultController> entry : controllerTypeMap.entrySet()) {
      if (entry.getKey().toLowerCase(Locale.ROOT)
          .contains(
              //genericType.toLowerCase(Locale.ROOT).concat("controller"))
              getRegistryKeyFromDomainCls(genericType).toLowerCase(Locale.ROOT))) {
        return entry.getValue();
      }
    }
    return null;
  }

  /**
   * @requires
   *  <tt>cls</tt> is the domain class managed by the Controller being looked-up
   *
   * @effects
   *  return the {@link DefaultController} whose domain class is <tt>cls</tt> that is stored in this
   */
  public DefaultController get(Class cls) {
    String genericType = cls.getSimpleName();
    return get(genericType);
  }

  public void put(String genericType, DefaultController controllerInstance) {
    String key = getRegistryKeyFromDomainCls(genericType);
    this.controllerTypeMap.put(key, controllerInstance);
  }

  /**
   * @requires <tt>domainCls</tt> is the domain class managed by <tt>controllerInstance</tt>
   * @effects
   *  adds <tt>(domainCls, controllerInstance)</tt> to {@link #controllerTypeMap}
   * @version 1.0
   */
  public void putByClass(Class domainCls, DefaultController controllerInstance) {
    put(domainCls.getSimpleName(), controllerInstance);
  }

  public Iterable<? extends Map.Entry<String, DefaultController>> entrySet() {
    return controllerTypeMap.entrySet();
  }

  /**
   * @effects
   *  checks whether this registry contains an entry whose key matches the controller of
   *  Module(<tt>domainCls</tt>)
   * @version 1.0
   */
  public boolean containsKeyByDomainCls(String domainCls, boolean ignoreCase) {
    String searchKey = getRegistryKeyFromDomainCls(domainCls);
    if (!ignoreCase) {
      return controllerTypeMap.containsKey(searchKey);
    } else {
      // search each entry to match key ignoring cases
      for (String key : controllerTypeMap.keySet()) {
        if (key.equalsIgnoreCase(searchKey)) {
          return true;
        }
      }

      return false;
    }
  }

  private String getRegistryKeyFromDomainCls(String domainCls) {
    String suffix = "Controller";
    if (domainCls.endsWith(suffix)) {
      // already has the suffix, returns as as
      return domainCls;
    } else {
      // not yet has the suffix, adds it and return
      return domainCls.concat(suffix);
    }
  }

  /**
   * @requires {@link #controllerTypeMap} contains an entry whose key matches the controller of Module(domainCls)
   * @effects remove the entry whose key matches the controller of Module(domainCls) from {@link #controllerTypeMap}
   */
  public void removeByDomainCls(String domainCls) {
    String searchKey = getRegistryKeyFromDomainCls(domainCls);

    // use case-insensitive key matching
    String foundKey = null;
    for (Map.Entry<String, DefaultController> entry : controllerTypeMap.entrySet()) {
      String key = entry.getKey();
      if (key.toLowerCase()
          .contains(searchKey.toLowerCase())) {
        foundKey = key;
        break;
      }
    }

    controllerTypeMap.remove(foundKey);
  }

  /**
   *
   * @effects
   *  creates a return a JsonObject representing {@link #controllerTypeMap}
   * @version 1.0
   */
  public JsonObject toJson() {
    return ToolkitIO.createNewJsonObject("controllerRegistry", controllerTypeMap);
  }
}
