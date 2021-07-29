package jda.modules.common.collection.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotPossibleException;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @overview
 *  A sub-class of <tt>BoundedList</tt> that provides a element-purging policy based
 *  on the access times of the elements. 
 *  
 *  <p>An element is accessed if it is added by one of the <tt>add</tt> methods, read by method 
 *  {@link BoundedList#readElement(int)} or it is replaced by another element 
 *  using the method {@link BoundedList#set(int, Object)} 
 */
public class TimeBasedBoundedList<C> extends BoundedList<C> {
  
  // auto-generated serial UID
  private static final long serialVersionUID = -5742919753464993106L;

  // read times: maps access time to element index, sorted in the 
  // ascendingn order of the access time
  private SortedMap<Long,Integer> readTimes;
  
  public TimeBasedBoundedList(int maxSize) {
    super(maxSize);
    readTimes = new TreeMap<Long,Integer>();
  }
  
  /**
   * @effects 
   *  purge up to n objects whose access times are the smallest
   *  If there are not enough elements to remove
   *    throws NotPossibleException 
   */
  @Override
  protected void purge(int n) throws NotPossibleException {
    int count = 0;
    Set<Entry<Long,Integer>> entries = readTimes.entrySet();
    int index;
    // record the removed keys and indices to update the map afterward
    // must do this to avoid concurrent modification exception
    List<Entry<Long,Integer>> removedEntries = new ArrayList();
    List<Integer> removedIndices = new ArrayList();
    for (Entry<Long, Integer> e : entries) {
      index = e.getValue();
      // record then entries and indices to remove later
      removedIndices.add(index);
      removedEntries.add(e);
      
      count++;

      if (count == n)
        break;
    }
    
    // check
    if (count < n)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          "Không thể thực thi phương thức: {0}.{1} {2}", "TimeBasedBoundedList","purge","Không đủ chỗ cho "+n+" phần tử mới");

    // now actually remove the indices (sort them first)
    if (removedIndices.size() > 1)
      Collections.sort(removedIndices);
    
    for (int i = removedIndices.size()-1; i >= 0; i--) {
      remove(removedIndices.get(i),false);
    }
    
    // now the removal of objects above also lead to the removal of the corresponding
    // entries in the map
    purgeStateEntries(removedEntries);
  }

  @Override
  protected void onObjectAdded(int num) {
    int index;
    int size = size();
    // set the time stamp of each of the newly-added elements to the current time stamp
    // the index first element is size-num, that of the second is size-(num-1), 
    // and so on
    long timeStamp;
    for (int i = num; i >=1; i--) {
      index = size-i;
      timeStamp = System.nanoTime(); // index*-1L
      readTimes.put(timeStamp,index);
    }
  }
  
  @Override
  protected void onObjectRemoved(int index) {
    // remove the read time stamp of the specified index
    List<Entry<Long,Integer>> entries = getStateEntries(index, index);
    purgeStateEntries(entries);
  }

  @Override
  protected void onObjectRemoved(int fromIndex, int toIndex) {
    List<Entry<Long,Integer>> entries = getStateEntries(fromIndex, toIndex-1);
    purgeStateEntries(entries);
  }
  
  private List<Entry<Long,Integer>> getStateEntries(int fromIndex, int toIndex) {
    // remove the read time stamps of the indices in the specified range
    Set<Entry<Long,Integer>> entries = readTimes.entrySet();
    List<Entry<Long,Integer>> founds = new ArrayList();
    for (int index = fromIndex; index <= toIndex; index++) {
      for (Entry<Long, Integer> e : entries) {
        if (e.getValue() == index) {
          founds.add(e);
          break;
        }
      }
    }

    return founds;
  }
  
  private void purgeStateEntries(List<Entry<Long,Integer>> founds) {
    // record the removed indices to update the remaining indices
    // in the map
    List<Integer> removed = new ArrayList();
    if (!founds.isEmpty()) {
      for (Entry<Long,Integer> found : founds) {
        readTimes.remove(found.getKey());
        removed.add(found.getValue());
      }
      
      // for each removed index
      //  find entries whose indices are higher
      //  and deduct one from their index values
      Set<Entry<Long,Integer>> entries = readTimes.entrySet();
      int idx;
      int numGreater;
      for (Entry<Long, Integer> e : entries) {
        idx = e.getValue();
        numGreater = 0;
        for (Integer index : removed) {
          if (idx > index) {
            numGreater++;
          }
        }
        if (numGreater > 0)
          e.setValue(idx-numGreater);
      }
    }
  }
  
  @Override
  protected void onObjectInserted(int index, boolean purged) {
    // update entry states: add 1 to the indices greater than index
    Set<Entry<Long,Integer>> entries = readTimes.entrySet();
    int idx;
    for (Entry<Long,Integer> e : entries) {
      idx = e.getValue();
      // some entries were removed before this is invoked
      if (purged && (idx >= index)) {
        e.setValue(idx + 1);
      }        
    }    
    
    // set time stamp
    changeTimeStamp(index, System.nanoTime()); // index*(-1L)
  }

  @Override
  protected void onObjectRead(int index) {
    // change  the read time stamp
    changeTimeStamp(index, System.nanoTime());  
  }
  
  
  @Override 
  protected void onObjectSet(int index) {
    // change time stamp of the specified index
    changeTimeStamp(index, System.nanoTime());
  }
  
  /**
   * @effects 
   *  change the time stamp of the specified index
   */
  private void changeTimeStamp(int index, long newTimeStamp) {
    // update the read time stamp of the specified index
    Set<Entry<Long,Integer>> entries = readTimes.entrySet();
    Entry found = null;
    for (Entry<Long,Integer> e : entries) {
      if (e.getValue() == index) {
        found = e;
        break;
      }
    }
    
    if (found != null) {
      // remove old 
      readTimes.remove(found.getKey());
    }  

    // add new 
    readTimes.put(newTimeStamp,index);
  }

  public void printEntryStates() {
    System.out.println(readTimes);
  }
}
