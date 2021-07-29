package jda.util;

import java.util.LinkedHashMap;
import java.util.Vector;

import jda.modules.common.types.Tuple2;

/**
 * @overview 
 *  A helper class that is used to incrementally sort domain objects that are otherwise added to 
 *  a <tt>Map</tt>, based on the values of a domain attribute. 
 *  
 *  <p>The sorting configuration is specified in a {@link ObjectComparator}.
 *  
 * @author dmle
 */
public class ObjectMapSorter<K,V> {

  private ObjectComparator comparator;
  
  private Vector<Tuple2<K, V>> buffer;

  public ObjectMapSorter(ObjectComparator comparator) {
    this.comparator = comparator;
    buffer = new Vector<Tuple2<K,V>>();
  }
  
  /**
   * @effects 
   *  if this is empty
   *    add (key,value) to this
   *  else
   *    find the right location for (key,value) in this based on the sorting order specified 
   *    by the comparator and insert it there
   */
  public void put(K key, V value) {
    Tuple2<K,V> e = new Tuple2(key, value);
    
    if (buffer.isEmpty()) {
      // first entry
      buffer.add(e);
    } else {
      // a new entry: find the right location for it
      // find the 'smallest' entry that is '>' e and insert e there
      final int currSz = buffer.size();
      int foundIndex = -1;
      Tuple2<K,V> currE;
      for (int i = 0; i < currSz; i++) {
        currE = buffer.get(i);
        if (comparator.compare(currE.getSecond(), value) >= 0) {
          // found
          foundIndex = i;
          break;
        }
      }

      if (foundIndex > -1) {
        // e is within the existing range of entries
        buffer.insertElementAt(e, foundIndex);
      } else {
        // e is larger than all existing entries 
        buffer.add(e);
      }
    }
  }
  
  /**
   * @effects 
   *  transfer sorted entries in this to <tt>targetMap</tt>, preserving 
   *  the sorting order
   */
  public void copyTo (LinkedHashMap<K,V> targetMap) {
    for (Tuple2<K,V> entry : buffer) {
      targetMap.put(entry.getFirst(), entry.getSecond());
    }
  }
  
  public void clear() {
    buffer.clear();
  }
}
