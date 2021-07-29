package jda.modules.common.collection;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @overview
 *  Utility methods for {@link Collection} 
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class CollectionToolkit {

  /**
   * @requires 
   *  it != null
   * @effects 
   *  return a new {@link Collection} whose content contains the objects in <tt>it</tt>
   *   
   * @version 3.0
   */
  public static <T> Collection<T> createCollection(Iterator<T> it) {
    Collection<T> col = null;
    if (it != null) {
      col = new ArrayList<>();
      while (it.hasNext()) {
        col.add(it.next());
      }
    }
    
    return col;
  }

  /**
   * @effects 
   *  create and return a new {@link Collection} that has the same actual type as <tt>col1</tt> and that  
   *  contains exactly the elements in <tt>col1</tt> but not in <tt>col2</tt>. 
   *  
   *  <p>if the intersection is empty or col2 is null then return <tt>col1</tt>; 
   *  if col1 is <tt>null</tt> or empty then return <tt>null</tt>; 
   *  
   *  <p> if could not create a new collection for some reasons return <tt>null</tt>
   *  
   * @version 3.2
   */
  public static <T> Collection<T> createCollectionFromDisjoint(
      Collection<T> col1, Collection<T> col2) {
    if (col1 == null || col1.isEmpty())
      return null;
    
    if (col2 == null)
      return col1;
    
    Collection<T> result = null;
    for (T o : col1) {
      if (!col2.contains(o)) {
        if (result == null) {
          try {
            result = col1.getClass().newInstance();
          } catch (InstantiationException | IllegalAccessException e) {
            // failed
            break;
          }
        }
        result.add(o);
      }
    }
    
    if (result != null && result.size() == col1.size())
      return col1;
    else
      return result;
  }
  
  /**
   * @requires map != null
   * 
   * @effects
   * <pre> 
   *  if not exists entry <tt>e</tt> in <tt>map</tt>, s.t <tt>equals(e.key,key)</tt>
   *    init <tt>e = Entry(key, new Collection())</tt>
   *    add <tt>e</tt> to <tt>map</tt>
   *    
   *  add <tt>value</tt> to <tt>e.value</tt>
   * </pre>
   * @version 3.1
   */
  public static <K,V> void updateCollectionBasedMap(Map<K, Collection<V>> map, K key, V value) {
    if (map == null)
      return; // do nothing
    
    Collection<V> values = map.get(key);
    if (values == null) {
      values = new ArrayList();
      map.put(key, values);
    }
    
    values.add(value);
  }
  
  /**
   * @effects 
   *  mimicks the behaviour of {@link List} for <tt>col</tt> such that the element that is <b>internally</tt>
   *  stored at the position <tt>pos</tt> with regards to <tt>col.iterator()</tt> is returned. 
   *  
   *  <p>if <tt>col is null</tt> or <tt>col.isEmpty</tt> return <tt>null</tt>.
   *  
   *  <p>Throws IndexOutOfBoundsException if <tt>pos</tt> is not within valid range
   */
  public static <V> V getElementAt(Collection<V> col, final int pos) throws IndexOutOfBoundsException {
    if (col == null || col.isEmpty())
      return null;
    
    Iterator<V> it = col.iterator();
    int index = 0;
    V element;
    while (it.hasNext()) {
      element = it.next();
      
      if (index == pos) {
        return element;
      }
      
      index++;
    }

    throw new IndexOutOfBoundsException("Invalid element position: " + pos);
  }

  /**
   * @requires 
   *  map != null /\ i >= 0
   * @effects 
   *  return the <tt>i</tt>th entry of <tt>map</tt>; 
   *    or return <tt>null</tt> if <tt>map</tt> is empty or <tt>i</tt> is not a valid entry
   *    index
   */
  public static <K,V> Entry<K,V> getMapEntryAt(Map<K, V> map, int i) {
    if (map == null || i < 0 || i >= map.size())
      return null;
    
    if (map.isEmpty())
      return null;
    
    int index = 0;
    for (Entry e : map.entrySet()) {
      if (index == i) {
        return e;
      }
      index++;
    }
    
    // should not happen
    return null;
  }

  /**
   * @effects 
   *  if <tt>type</tt> is assignable from {@link Collection}
   *    return true
   *  else
   *    return false
   * @version 5.1 
   * 
   */
  public static boolean isCollectionType(Class type) {
    if (Collection.class.isAssignableFrom(type)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  return the generic type of the specified <tt>type</tt>, 
   *  e.g. if <tt>type = Collection&lt;Customer&gt;</tt> then 
   *  result = <tt>Customer</tt>; 
   *  or return <tt>null</tt> if the collection type 
   *  uses a type variable (e.g. Collection&lt;T&gt;>)
   *  
   * @version 5.1
   * 
   */
  public static Class getGenericCollectionType(java.lang.reflect.Type type) {
    if (type == null) return null;
    
    if (type instanceof ParameterizedType) {
      ParameterizedType colType = (ParameterizedType) type;
        
      java.lang.reflect.Type[] typeVars = colType.getActualTypeArguments();
      
      java.lang.reflect.Type t = typeVars[0];
      
      if (t instanceof Class)
        return (Class) t;
      else
        // t is not a Class but a type variable (e.g. as in Collection<T>)
        return null;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if cls represents an array type (i.e. assignment to <tt>Object[]</tt>)
   *    return true
   *  else
   *    return false
   *    
   * @version 5.2
   */
  public static boolean isArrayType(Class cls) {
    // NOTE: cls.isArray() does not work !!!
    
    return (cls != null && Object[].class.isAssignableFrom(cls));
  }

  /**
   * @effects 
   *  if o is in array arr
   *    return true
   *  else
   *    return false
   * @version 5.4
   *  
   */
  public static boolean isInArray(Object o,
      Object[] arr) {
    if (o == null || arr == null) 
      return false;
    
    for(Object member : arr) {
      if (o.equals(member)) {
        return true;
      }
    }
    
    return false;
  }
}
