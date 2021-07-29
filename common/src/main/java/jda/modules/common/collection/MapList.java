package jda.modules.common.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * A sub-class of <code>Map</code>, whose values are <code>List</code> objects.
 * 
 * <p>For example, a <code>MapList</code> about students would contain the followings:
 * <pre>
 *  id   -> ["S2012", "S2013", "S2014"]
 *  name -> ["Peter', "John", "Alex"]
 *  address -> ["UK", "USA", "USA"]
 * </pre>
 * 
 * In the above, a <code>key</code> is an attribute of students (e.g. <code>id</code>) and a <code>value</code> is a <code>List</code>  
 * of data values of the concerned attribute.
 * 
 * @author dmle
 *
 */
public class MapList<K,V> extends Map {
  
  /**
   * @effects places <code>val</code> at the index position <code>atIndex</code> in the <code>List</code>
   *          object corresponding to <code>key</code>, overriding any value at the specified position
   *          and throws <code>IndexOutOfBoundsException</code>
   *          if <code>atIndex < 0</code>. If <code>atIndex >= </code> the size of the <code>List</code> object
   *          then <code>null</code> values are added to this list up to the specified index and the 
   *          last element is then replaced by <code>val</code>.
   */
  public Object put(K key, Object val, int atIndex) throws IndexOutOfBoundsException {
    if (atIndex < 0)
      throw new IndexOutOfBoundsException("Invalid index " + atIndex);
    
    List l = (List) super.get(key);
    if (l == null) {
      l = new ArrayList();
      put(key, l);
    }
    
    int size = l.size();
    if (atIndex > size) {
      for (int i = size; i < atIndex; i++) l.add(null); 
    } else if (atIndex == size) 
      l.add(null);
    
    return l.set(atIndex, val);
  }

  /**
   * @effects returns <code>Object</code> value at the index position <code>atIndex</code> of the 
   *          <code>List</code> mapped to <code>key</code>
   *          
   * @throws IndexOutOfBoundsException
   */
  public Object get(K key, int atIndex) throws IndexOutOfBoundsException {
    List l = (List) get(key);
    
    return l.get(atIndex);
  }
  
  /**
   * This method returns the number of values mapped to each key in <code>this</code>. 
   * 
   * @effects if <code>this.isEmpty = false</code> and the value list of the first entry 
   *          is initialised then returns the size of this, else returns <code>0</code>
   */
  public int sizeRows() {
    if (!isEmpty())  {
      List l = (List) get(0);
      return l.size();
    } else {
      return 0;
    }
  }
  
  /**
   * @effects if <code>super.isEmpty </code> or no value is set for the first key returns <code>true</code>, 
   *          else returns <code>false</code>
   */
  public boolean isEmpty() {
    if (!super.isEmpty()) {
      List l = (List) get(0);
      return (l == null || l.isEmpty());
    } else {
      return true;
    }
  }
}
