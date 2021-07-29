package jda.modules.common.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * A sub-class of <code>Map</code> with added support for key protection.
 * 
 * @author dmle
 * 
 */
public class ProtectedMap<K,V> extends Map<K,V> {
  private List<K> protectedKeys; 
    
  public ProtectedMap() {
    super();
    protectedKeys = new ArrayList();
  }
  
  /**
   * @effects if key <code>k</code> is protected then throws <code>IllegalAccessException</code>, 
   *          else <code>super.put(k,v)</code> and if <code>readOnly = true</code> then 
   *          sets <code>k</code> to be a protected key
   */
  public V put(K k, V v, boolean readOnly) throws IllegalAccessException {
    if (protectedKeys.contains(k)) {
      // cannot change once set
      throw new IllegalAccessException();
    } else {
      if (readOnly) {
        protectedKeys.add(k);
      }      
      return super.put(k,v);
    }
  }
  
  /**
   * @effects if <code>this.put(k,v,false)</code> succeeds then returns the result, else
   *          returns <code>get(k)</code>.
   */
  public V put(K k, V v) {
    try {
      return this.put(k, v, false);
    } catch (IllegalAccessException e) {
      // protected keys cannot be changed 
      return get(k);
    }
  }
  
  /**
   * @effects <code>super.clear()</code> and clear the protected keys in <code>this</code>
   */
  public void clear() {
    super.clear();
    protectedKeys.clear();
  }  
}
