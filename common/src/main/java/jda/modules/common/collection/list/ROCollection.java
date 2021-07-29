package jda.modules.common.collection.list;

import java.util.Collection;
import java.util.Iterator;

/**
 * @overview
 *  A wrapper class over a <tt>Collection</tt> that represent a simple read-only reflection of it.
 *   
 * @author dmle
 */
public class ROCollection {
  private Collection source;
  
  /**
   * @effects 
   *  if source is null
   *    throws NullPointerException
   *  else
   *    initialise this as a read-only reflection of source
   */
  public ROCollection(Collection source) throws NullPointerException {
    if (source == null) {
      throw new NullPointerException("RoCollection.init");
    }
    
    this.source = source;
  }
  
  /**
   * @effects 
   *  if source is null
   *    throws NullPointerException
   *  else
   *    set this.source = source
   */
  public void setSource(Collection source) throws NullPointerException {
    if (source == null) {
      throw new NullPointerException("RoCollection.setSource");
    }
    
    this.source = source;    
  }
  
  /**
   * @requires 
   *  source != null
   * @effects 
   *    return the element at position specified by index or 
   *    throws IndexOutOfBoundsException if it is thrown by <tt>source</tt> 
   */
  public Object get(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= size()) {
      throw new IndexOutOfBoundsException("RoCollection.get("+index+")");
    }
    
    Iterator sit = iterator();
    int ind = 0;
    Object o = null;
    while (ind <= index) {
      o = sit.next();
      ind++;
    }
    return o;
  }
  
  /**
   * @requires
   *  source != null
   * @effects 
   *    return <tt>source.size()</tt>
   */
  public int size() {
    return source.size();  
  }
  
  /**
   * @requires 
   *  source != null
   * @effects
   *  return source.iterator()
   */
  public Iterator iterator() {
    return source.iterator();
  }
  
  /**
   * @requires  
   *  source != null
   * @effects 
   *    return <tt>source.isEmpty()</tt>
   */
  public boolean isEmpty() {
    return source.isEmpty();      
  }
  
  /**
   * @requires 
   *  source != null
   * @effects 
   *   return <tt>source.toString()</tt>
   */
  @Override
  public String toString() {
    return source.toString();
  }

  /**
   * @effects 
   *  set this.source = null
   */
  public void removeSource() {
    source = null;
  }

  /**
   * @effects 
   *  if this.source != null
   *    return true
   *  else
   *    return false
   */
  public boolean isInit() {
    return source != null;
  }
}
