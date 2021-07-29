package jda.modules.common.cache;

import java.util.HashMap;

/**
 * @overview 
 *  A subtype of <tt>Map</tt> that is used to cache attribute values of domain objects.
 *  
 * @author dmle
 */
public class StateHistory<K,V> extends HashMap<K,V> {
  
  /*
   * v2.6.4.b: to use the default get
   *
  @Override
  public V get(Object key) {
    return remove(key);
  }
  */
}
