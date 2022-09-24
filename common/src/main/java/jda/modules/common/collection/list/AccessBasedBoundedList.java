package jda.modules.common.collection.list;

import java.util.ArrayList;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  A sub-class of <tt>BoundedList</tt> that provides a element-purging policy based
 *  on the access states of the elements. 
 *  
 *  <p>An element is accessed if it is either read by method 
 *  {@link BoundedList#readElement(int)} or it is replaced by another element 
 *  using the method {@link BoundedList#set(int, Object)} 
 */
public class AccessBasedBoundedList<C> extends BoundedList<C> {
  
  // auto-generated serial UID
  private static final long serialVersionUID = -5742919753464993106L;

  // read flags: true if an element has been read, false if otherwise
  private ArrayList<Boolean> readFlags;
  
  public AccessBasedBoundedList(int maxSize) {
    super(maxSize);
    readFlags = new ArrayList<Boolean>();
  }
  
  /**
   * @requires <tt>index >= 0 /\ index < size()</tt>
   * @effects 
   *  if element at <tt>index</tt> has been read, 
   *  i.e. <tt>readFlags[index]=true</tt>
   *    return true
   *  else
   *    return false
   */
  private boolean isRead(int index) {
    return readFlags.get(index);
  }
  
  /**
   * @effects 
   *  purge up to n objects that have not been read
   *  start from the front ( because this method is normally
   *     invoked after adding an object)
   *  if there are not enough elements to remove then 
   *    fall back to the default purge of the super class. 
   *  If after this and there are still not enough elements to remove
   *    throw NotPossibleException 
   */
  @Override
  protected void purge(int n) throws NotPossibleException {
    int previousSize = size();
    
    int count = 0;
    for (int i = 0; i < size(); i++) {
      if (!isRead(i)) {
        remove(i);
        
        count++;

        // adjust i because we start from the front
        i--;
        
        if (count == n)
          break;
      }
    }
    
    // check
    if (count < n)
      // try the default purge of the super class
      super.purge(n-count);
    
    // final check
    if (size() != previousSize-n)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          "Không thể thực thi phương thức: {0}.{1} {2}", "AccessBasedBoundedList","purge","Không đủ chỗ cho "+n+" phần tử mới");
  
  }

  @Override
  protected void onObjectAdded(int num) {
    for (int i = 0; i < num; i++) {
      readFlags.add(false);
    }
  
  }
  
  @Override
  protected void onObjectRemoved(int index) {
    // update the read flags
    readFlags.remove(index);
  }

  @Override
  protected void onObjectRemoved(int fromIndex, int toIndex) {
    // update the read flags
    for (int i = fromIndex; i < toIndex; i++) {
      readFlags.remove(i);
    }
  }
  
  @Override
  protected void onObjectInserted(int index, boolean purged) {
    // update the read flags
    readFlags.add(index, false);
  }

  @Override
  protected void onObjectRead(int index) {
    // update the read flag
    readFlags.set(index, true);  
  }
  
//  @Override 
//  public void onObjectSet(int index) {
//    // do nothing
//  }
  
  public void printEntryStates() {
    System.out.println(readFlags);
  }
}
