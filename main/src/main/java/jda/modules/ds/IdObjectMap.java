package jda.modules.ds;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.mosa.model.Oid;

/**
 * @overview
 *  A sub-type of {@link LinkedHashMap} whose keys are object ids ({@link Oid}) and that records  
 *  the smallest and largest ids.
 *  
 * @author dmle
 */
public class IdObjectMap<K, V> extends LinkedHashMap<Oid, Object> {
  
  private Oid minId;
  
  private Oid maxId;

  /**
   * @effects 
   *  initialises this as empty
   */
  public IdObjectMap() {
    super();
  }

  /**
   * @effects 
   *  return this.minId
   */
  public Oid getMinId() {
    return minId;
  }

  /**
   * @effects 
   *  sets this.minId = minId
   */
  public void setMinId(Oid minId) {
    this.minId = minId;
  }

  /**
   * @effects 
   *  return this.maxId
   */
  public Oid getMaxId() {
    return maxId;
  }

  /**
   * @effects 
   *  sets this.maxId = maxId
   */
  public void setMaxId(Oid maxId) {
    this.maxId = maxId;
  }

  /**
   * @effects 
   * <pre>
   *  places entry (key,value) in this
   *  if maxId is not initialised OR key is greater than maxId
   *    set maxId = key
   *  
   *  return the old value associated with key (or null if no such value)</pre>
   */
  @Override
  public Object put(Oid key, Object value) {
    // put into map 
    Object old = super.put(key,value);
    
    // update max id
    if (maxId == null || key.compareTo(maxId) > 0)
      maxId = key;
    
    return old;
  }
  
  /**
   * @effects <pre> 
   *  remove entry identified by key from this
   *  
   *  if key is either minId or maxId
   *    undefine minId or maxId, respectively
   *    throws ObjsoleteIdStateSignal
   *  else
   *    return the old value associated with key (or null if no such value)</pre>
   */
  @Override
  public Object remove(Object key) throws ObsoleteStateSignal {
    // remove from map 
    Object old = super.remove(key);
    
    // update max id
    if (key.equals(maxId)) {
      maxId = null;
    } 
    
    if (key.equals(minId)) {
      minId = null;
    }
    
    if (minId == null || maxId == null)
      throw new ObsoleteStateSignal();
    
    return old;
  }
  
  /**
   * @effects <pre>
   *  invoke super.clear() to remove all entries
   *  set minId, maxId to null</pre>
   */
  @Override
  public void clear() {
    super.clear();
    minId = null;
    maxId = null;
  }
  
  /**
   * @effects 
   *  if minId AND maxId are initialised
   *    return true
   *  else
   *    return false
   */
  public boolean isIdRangeInitialised() {
    return (minId != null && maxId != null);
  }

  /**
   * @requires 
   *  objMap != null
   * @effects 
   *  add to this every entry in this – objMap 
   */
  public void update(LinkedHashMap<Oid,Object> objMap) {
    if (objMap == null)
      return;
          
    for (Entry<Oid,Object> e : objMap.entrySet()) {
      if (!this.containsKey(e.getKey())) {
        put(e.getKey(), e.getValue());
      }
    }
  }
  
  @Override
  public String toString() {
    return "IdObjectMap + (" + toStringContent() + ")";
  }

  public String toStringContent() {
    return "[" + minId + ", " + maxId + "]:\n" + // min & max
        // elements
        super.toString();
  }

  /**
   * This method differs from {@link #getMinId()} in that it returns the lowest Oid from 
   * the entries actually contained in this, while the other method returns the pre-defined lowest Oid. 
   * 
   * @effects
   *  if this is empty 
   *    return null
   *  else
   *    return the first Oid (in addition order) among <tt>this.keys</tt>
   * 
   * @version 2.6.4.a:
   *  this implementation just returns the first key in this (assuming that entries are added with the lowest Oid 
   *  first and ascending in that order)
   *  If this insertion behaviour is different then implementation must be changed.
   */
  public Oid findFirstId() {
    if (isEmpty())
      return null;
    else    
      return keySet().iterator().next();
  }
  
  /**
   * This method differs from {@link #getMaxId()} in that it returns the largest Oid from 
   * the entries actually contained in this, while the other method returns the pre-defined largest Oid. 
   * 
   * @effects
   *  if this is empty
   *    return null
   *  else 
   *    return the largest Oid (in addition order) among <tt>this.keys</tt>
   * 
   * @version 2.6.4.a:
   *  this implementation just returns the last key in this (assuming that entries are added with the lowest Oid 
   *  first and ascending in that order)
   *  If this insertion behaviour is different then implementation must be changed.
   */
  public Oid findLastId() {
    if (isEmpty())
      return null;
    
    Iterator<Oid> keyIt = keySet().iterator();
    Oid last = null;
    while (keyIt.hasNext()) 
      last = keyIt.next();
    
    return last;
  }

  /**
   * @requires 
   *  this is not empty
   *  
   * @effects 
   *  if there exists Oid immediately after <tt>id</tt> in this
   *    return it
   *  else
   *    return null
   */
  public Oid nextId(Oid id) {
    Iterator<Oid> keys = keySet().iterator();
    Oid next = null, currId;
    while (keys.hasNext()) {
      currId = keys.next();
      if (currId.equals(id)) {
        if (keys.hasNext()) { // being extra careful here
          next = keys.next();
          break;
        }
      }
    }
    
    return next;
  }
  
  /**
   * @requires 
   *  this is not empty
   * @effects 
   *  if there exists Oid immediately before <tt>id</tt> in this
   *    return it
   *  else
   *    return null
   */
  public Oid previousId(Oid id) {
    Iterator<Oid> keys = keySet().iterator();
    Oid prev = null, prevId = null, currId;
    while (keys.hasNext()) {
      currId = keys.next();
      if (currId.equals(id)) {
        if (prevId != null) { // being extra careful here
          prev = prevId;
          break;
        }
      } else {
        prevId = currId;
      }
    }
    
    return prev;
  }
}
