package jda.modules.common.collection;

import java.util.Map.Entry;

import jda.modules.common.exceptions.signal.ObsoleteStateSignal;

import java.util.TreeMap;

/**
 * @overview
 *  Represents a sorted, bounded <tt>Map</tt> whose keys are sorted in natural ordering and whose entries form a sequence of values
 *  
 * @example
 *  The sequence [1,2,3,4,5] is stored in a SortedSequentialMap<Integer,Integer> whose lowest and highest 
 *  values are 1 and 5, respectively; and whose entries are:
 *  <pre>
 *    1->2
 *    2->3
 *    3->4
 *    4->5
 *  </pre>
 *  
 * @author dmle
 */
public class SortedSequentialMap<T> {

  private T first;
  private T last;
  
  private TreeMap<T,T> map;
  
  /**
   * @effects 
   *  intitialise this as [first,....,last]
   */
  public SortedSequentialMap(T first, T last) {
    //
    this.first = first;
    this.last = last;
    map = new TreeMap<T,T>();
  }

  /**
   * @effects 
   *  if smallest value is specified
   *    return it
   *  else
   *    return null
   */
  public T first() { //throws InternalError {
//    if (first == null)
//      throw new InternalError(this.getClass().getSimpleName()+".first(): smallest value is not specified");
    
    return first;
  }

  /**
   * @effects 
   *  if exists a value v in this that immediately proceeds <tt>currentVal</tt> (i.e. entry (currentVal,v) is in this)
   *    return v
   *  else
   *    return null
   */
  public T next(T currentVal) {
    T v = map.get(currentVal);
    return v;
  }

  /**
   * @effects 
   *  if exists a value v in this that immediately precedes <tt>currentVal</tt> (i.e. entry (v,currentVal) is in this)
   *    return v
   *  else
   *    return null
   */
  public T previous(T currentVal) {
    // get the previous entry of currentVal and see if it contains currentVal as the value
    /*v3.0: improved to support keys that are not sorted 
    Entry<T,T> prevEntry = map.lowerEntry(currentVal);
    if (prevEntry != null && prevEntry.getValue().equals(currentVal)) {
      // exists
      return prevEntry.getKey();
    } else {
      // not exists
      return null;
    }
    */
    
    for (Entry<T,T> e : map.entrySet()) {
      if (e.getValue().equals(currentVal)) {
        // found the entry 
        return e.getKey();
      }
    }
    
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if highest value is specified
   *    return it
   *  else
   *    return null
   */
  public T last() { //throws InternalError {
//    if (last == null)
//      throw new InternalError(this.getClass().getSimpleName()+".last(): highest value is not specified");
    
    return last;
  }
  
  /**
   * @effects <pre>
   *  if val and nextVal are not null
   *    places entry (val,nextVal) in this
   *    if val = last
   *      set last = nextVal
   *  else
   *    throws NullPointerException </pre>
   */
  public void put(T val, T nextVal) throws NullPointerException {
    if (val == null || nextVal == null)
      throw new NullPointerException(this.getClass().getSimpleName()+".put: one of the specified values is null");
    
    map.put(val, nextVal);
    
    if (val.equals(last))
      last = nextVal;
  }

  /**
   * <b>IMPORTANT</b>: this method must be <tt>private</tt>.
   *  
   * @effects 
   *  returns value of key in this  
   */
  private T getValue(T key) {
    return map.get(key);
  }
  
//  /**
//   * This method differs from {@link #put(Object, Object)} in that the value may be null
//   * <br><b>IMPORTANT</b>: this method must be <tt>private</tt>. 
//   * @effects 
//   *  puts (key,v) in this or sets value of key in this to v  
//   */
//  private T setValue(T key, T v) {
//    if (key == null)
//      throw new NullPointerException(this.getClass().getSimpleName()+".setValue: key may not be null");
//    
//    return map.put(key, v);
//  }
  
  /**
   * @requires 
   *  val is contained in one of the entries of this
   * @effects 
   *  <pre>remove all entries containing val as either key or value
   *  if val is first or last
   *    sets first (resp. last) to null
   *    throws ObsoleteStateSignal
   *  </pre>
   */
  public void remove(T val) throws ObsoleteStateSignal {
    map.remove(val);
    T prev = previous(val);
    
    if (prev != null)
      map.remove(prev);
    
    if (val.equals(first)) {
      first = null;
    } 
    
    if (val.equals(last)) {
      last = null;
    }
    
    if (first == null || last == null)
      throw new ObsoleteStateSignal();
  }
  
  /**
   * This method differs from {@link #remove(Object)} only in that it 'heals' any potential gaps
   * that are caused by the removal of the specified value.  
   * 
   * @requires 
   *  val is contained in one of the entries of this
   *  
   * @effects 
   *  <pre>
   *  let V be the entry s.t V.key=val (i.e. V is the entry whose key is val)
   *  
   *  if val != last
   *    if exists entry e s.t. e.value=val (i.e. e precedes V)
   *      if exists entry f s.t. f.key=V.value (i.e. V precedes f)
   *        set e.value = f.key
   *      else
   *        set e.value = null
   *    else  // V is first entry
   *      set first = V.value
   *  else  // val is last
   *    if exists entry e s.t e.value=val
   *      set e.value = null
   *      set last = e.key
   *  
   *  remove V
   *  
   *  if there are no more entries left
   *    throws ObsoleteStateSignal
   *  </pre>
   */
  public void removeAndHeal(T val) throws ObsoleteStateSignal {
    T prev,v=null;
    
    prev = previous(val);
    
    if (!val.equals(last)) {
      v = getValue(val);   // v=V.value
      if (prev != null) { // V is not first entry /\ e.value=val
        put(prev,v);   // set e.value=f.key
      } else {  // V is first entry
        setFirst(v);
      }
    } else {  // val is last
      if (prev != null) { // V is not first entry /\ e.value=val
        map.remove(prev);
        setLast(prev);
      }
    }
    
    map.remove(val);
    
    if (isEmpty())
      throw new ObsoleteStateSignal("No more ids in cache");
  }
  
  /**
   * @effects 
   *  if there are no entries in this
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }
  
  @Override
  public String toString() {
    return map.toString();
  }

  /**
   * @effects 
   *  set this.first = first
   *  
   * @requires 
   *  first != null
   */
  public void setFirst(T first) {
    this.first = first;
  }
  
  /**
   * @effects 
   *  set this.last = last
   *  
   * @requires
   *  last != null
   */
  public void setLast(T last) {
    this.last = last;
  }

  /**
   * @effects 
   *  clear this, effectively making it empty
   * @version 3.0
   */
  public void clear() {
    if (map != null)
      map.clear();
    
    first = null;
    last = null;
  }
}
