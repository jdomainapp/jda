package jda.modules.dodm.osm.relational.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @overview
 *  This class holds two maps: a standard (primary) map and (optionally) an auxiliary (secondary) map. 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class DualMap<K,V> {
  // primary map
  private HashMap<K,V> stdMap;
  
  // (optional) secondary map
  private HashMap<K,V> auxMap;
  
  public DualMap() {
    stdMap = new HashMap<>();
  }
  
  /**
   * Add a standard mapping.
   * 
   * @effects
   *  put (key,value) to {@link #stdMap} and return the previous value of that mapping. 
   */
  public V put(K key, V value) {
    return stdMap.put(key, value);
  }
  
  /**
   * Retrieve a standard mapping.
   * 
   * @effects
   *  return <tt>value</tt> of <tt>key</tt> in {@link #stdMap} or return <tt>null</tt> if no such entry is found 
   */
  public V get(K key) {
    return stdMap.get(key);
  }
  
  /**
   * Add an auxiliary mapping mapping.
   * 
   * @effects
   *  put (key,value) to {@link #auxMap} and return the previous value of that mapping. 
   */
  public V putAux(K key, V value) {
    if (auxMap == null) auxMap = new HashMap<>();
    return auxMap.put(key, value);
  }
  
  /**
   * Retrieve an auxiliary mapping.
   * 
   * @effects
   *  return <tt>value</tt> of <tt>key</tt> in {@link #auxMap} or 
   *  return <tt>null</tt> if {@link #auxMap} is not initialised or no such entry is found 
   */
  public V getAux(K key) {
    if (auxMap != null)
      return auxMap.get(key);
    else
      return null;
  }
  
  @Override
  public String toString() {
    return String.format("%s:- stdMap=%s;%n auxMap=%s", 
        getClass().getSimpleName(), 
        stdMap.toString(), 
        (auxMap != null) ? auxMap.toString() : "null"
        );
  }

  /**
   * @effects 
   *  return total number of entries in both primary and secondary maps, i.e.
   *  <pre>
   *  if {@link #auxMap} != null  
   *    result = {@link #stdMap}.size + {@link #auxMap}.size
   *  else
   *    result = {@link #stdMap}.size </pre>
   */
  public int size() {
    if (auxMap != null) {
      return stdMap.size() + auxMap.size();
    } else {
      return stdMap.size();
    }
  }

  /**
   * @effects 
   *  return the combined set of the values sets of both primary and secondary maps, i.e.
   *  <pre>
   *  if {@link #auxMap} != null  
   *    result = {@link #stdMap}.values + {@link #auxMap}.values
   *  else
   *    result = {@link #stdMap}.values </pre>
   *    
   *  <p>The result is a separate collection, and thus any subsequent modifications to the primary and secondary maps 
   *  will not be reflected in it.
   */
  public Collection<V> values() {
    Collection<V> vals = new ArrayList<>(stdMap.values());
    if (auxMap != null) {
      vals.addAll(auxMap.values());
    }
    
    return vals;
  }
}
