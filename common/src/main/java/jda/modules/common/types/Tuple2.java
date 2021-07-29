package jda.modules.common.types;

import java.util.LinkedHashMap;
import java.util.Map;

import jda.modules.common.Toolkit;

/**
 * @overview
 *  Represents an immutable, binary tuple containing values of types <tt>U,V</tt> in that order. 
 * @author dmle
 *
 * @param <V>
 * @param <U>
 */
public class Tuple2<U,V> {
  private U u;
  private V v;
  
  // a 2-level cache of the tuples
  private static Map<Object,      // level-1 key = first element 
                      Map<Object, // level-2 key = second element
                                  Tuple2>> cache;
  
  private static boolean debug = Toolkit.getDebug(Tuple2.class);
  
  /**
   * @effects 
   *  if u and v are not null
   *    intialises this as (u,v)
   *  else
   *    throws IllegalArgumentException
   */
  public Tuple2(U u,V v) throws IllegalArgumentException {
    if (u == null || v == null)
      throw new IllegalArgumentException("Tuple2.init: both elements must be specified");
    
    this.u = u;
    this.v = v;
  }
  
  /**
   * This is a special producer operation that supports caching of the Tuple2 objects created.
   * 
   * @requires 
   *  u != null /\ v != null
   * @effects 
   * <pre>
   *  if u or v are is null
   *    throws IllegalArgumentException
   *  else     
   *    if Tuple2<u,v> exists in cache
   *      return it
   *    else
   *      create a new Tuple2<u,v>, add to cache and return it</pre> 
   */
  public static <X, Y> Tuple2<X,Y> newTuple2(X u, Y v) throws IllegalArgumentException {
    if (u == null || v == null)
      throw new IllegalArgumentException("Tuple2.init: both elements must be specified");
    
    if (cache == null)  // initialise cache
      cache = new LinkedHashMap<Object, Map<Object, Tuple2>>();
    
    Map<Object,Tuple2> secondCache = cache.get(u);
    if (secondCache == null) {
      secondCache = new LinkedHashMap<Object,Tuple2>();
      cache.put(u, secondCache);
    } 

    Tuple2 t = secondCache.get(v);
    if (t == null) {
      // not in cache
      if (debug)
        System.out.printf("Tuple2.newTuple: NOT in cache (%s,%s)%n", u, v);
        
      t = new Tuple2<X,Y>(u, v);
      secondCache.put(v, t);
    } else {
      if (debug)
        System.out.printf("Tuple2.newTuple: IN cache (%s,%s)%n", u, v);
    }
    
    return t;
  }
  
  public U getFirst() {
    return u;
  }
  
  public V getSecond() {
    return v;
  }

  @Override
  public String toString() {
    return "Tuple2 <" + u + ", " + v + ">";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((u == null) ? 0 : u.hashCode());
    result = prime * result + ((v == null) ? 0 : v.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Tuple2 other = (Tuple2) obj;
    if (u == null) {
      if (other.u != null)
        return false;
    } else if (!u.equals(other.u))
      return false;
    if (v == null) {
      if (other.v != null)
        return false;
    } else if (!v.equals(other.v))
      return false;
    return true;
  }
}
