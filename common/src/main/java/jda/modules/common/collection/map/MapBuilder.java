package jda.modules.common.collection.map;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview 
 *  A convenient fluent-based builder class to easily construct a {@link Map}. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class MapBuilder<T1, T2> {
  private Map<T1, T2> map;
  
  /**
   * @effects 
   *  initialises an empty {@link Map} whose type is <code>type</code> 
   */
  public MapBuilder(Class<? extends Map> type) 
      throws NotPossibleException {
    try {
      map = type.getConstructor(null).newInstance(null);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {type, ""});
    }
  }
  
  /**
   * @effects 
   *  puts <code>(key, null)</code> entry into the resulted map 
   */
  public MapBuilder<T1,T2> put(T1 key) {
    return this.put(key, null);
  }
  
  /**
   * @effects 
   *  puts <code>(key, value)</code> entry into the resulted map 
   */
  public MapBuilder<T1,T2> put(T1 key, T2 val) {
    map.put(key, val);
    return this;
  }

  /**
   * @effects 
   *  return the resulted map
   */
  public Map<T1, T2> getMap() {
    return map;
  }
}
