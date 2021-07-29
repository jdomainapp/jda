package jda.modules.exportdoc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @overview
 *  a shared global buffer between Java and the scripting engine that are used to 
 *  conveniently exchange scripting outputs.
 *  
 *  <p>It maps a {@link String}-typed key to an arbitrary value whose generic type is <tt>V</tt>.
 *  
 * @author dmle
 *
 * @version 3.3 
 */
public class ScriptOutput<V> {
  
  /**
   *  the output map that is used to record the outputs. Each output typed <tt>V</tt> is mapped to 
   *  a key typed <tt>K</tt> 
   */
  private Map<String,V> outputMap;
  
  private static int autoKey = 1; 
  
  public ScriptOutput() {
    outputMap = new HashMap<>();
  }
  
  /**
   * @effects 
   *  puts <tt>value</tt> in <tt>this</tt> using <tt>key</tt> (overwriting any existing value that is 
   *  associated with <tt>key</tt>).
   *  Return any old value that is associated with <tt>key</tt>, or <tt>null</tt> if no such value exists.
   */
  public V put(String key, V value) {
    return outputMap.put(key, value);
  }
  
  /**
   * @effects 
   *  return value of <tt>key</tt> or <tt>null</tt> if no such value exists
   */
  public V get(String key) {
    return outputMap.get(key);
  }
  
  /**
   * @effects 
   *  puts <tt>value</tt> in <tt>this</tt> using an auto-generated key
   */
  public void write(V value) {
    String key = (autoKey++)+"";
    put(key, value);
  }

  /**
   * @effects 
   *  if this is empty 
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return outputMap.isEmpty();
  }

  /**
   * @effects 
   *  clears all existing output entries in this
   */
  public void clear() {
    if (!isEmpty()) {
      outputMap.clear();
    }
  }

  /**
   * @effects 
   *  if this is not empty
   *    return the value of the first output entry
   *  else
   *    return <tt>null</tt>
   */
  public Object firstValue() {
    if (!isEmpty()) {
      return outputMap.values().iterator().next();
    } else {
      return null;
    }
  }
  

  @Override
  public String toString() {
    return "ScriptOutput (" + outputMap + ")";
  }
  
}
