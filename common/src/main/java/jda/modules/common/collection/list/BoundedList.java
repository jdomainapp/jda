package jda.modules.common.collection.list;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  A sub-class of <tt>ArrayList</tt> that has an upper bound on the number of elements
 *  that can be stored. 
 *  
 *  <p>If the list is full then existing elements are removed to make
 *  space for the new elements. The elements are removed by the  
 *  <tt>purge</tt> method, which should be overriden by the sub-classes.
 *  Each sub-class can follow a different purging policy.   
 *   
 * @author dmle
 */
public class BoundedList<C> extends ArrayList<C> implements List<C> {
  
  // auto-generated serial UID
  private static final long serialVersionUID = -689430273819960803L;

  private int maxSize;
  
  /**
   * @effects 
   *  if maxSize is valid
   *    initialise this to be an empty list whose bound is <tt>maxSize</tt>
   *  else
   *    throws NotPossibleException 
   */
  public BoundedList(int maxSize) throws NotPossibleException {
    if (maxSize > 0)
      this.maxSize = maxSize;
    else 
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          "Tham số đầu vào không đúng: {0}", maxSize);
  }
  
  /**
   * @requires <tt>n >= 0 /\ n < size()</tt> 
   * @effects 
   *  remove <tt>n</tt> elements from this,  
   *  if number of elements removed is not <tt>n</tt>
   *    throws NotPossibleException
   */
  protected void purge(int n) throws NotPossibleException {
    int previousSize = size();
    
    // remove the first n elements from this
    removeRange(0, n);
    
    // check
    if (size() != previousSize-n)
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          "Không thể thực thi phương thức: {0}.{1} {2}", "BoundedList","purge","Không đủ chỗ cho "+n+" phần tử mới");
  }

  /**
   * @effects 
   *  if this is full, i.e. <tt>size() = maxSize</tt>
   *    return <tt>true</tt> 
   *  else
   *    return <tt>false</tt> 
   */
  public boolean isFull() {
    return size() == maxSize;
  }
  
  /**
   * @effects
   *  if this is full
   *    purge one object from this
   *    throws NotPossibleException if there is not enough space to add new object
   *  
   *  invoke <tt>super.add(o)</tt> and update the state of the added element 
   */
  @Override
  public boolean add(C o) throws NotPossibleException {
    if (isFull()) {
      purge(1);
    }
    
    boolean added = super.add(o);
    if (added) {
      onObjectAdded(1);
    }
    
    return added;
  }

  /**
   * @effects 
   *  if this is full
   *    purge one object from this
   *    throws NotPossibleException if there is not enough space to add new object
   *  
   *  invoke <tt>super.add(i,o)</tt> and update the state of the added element 
   *  
   */
  @Override
  public void add(int i, C o) throws IndexOutOfBoundsException, 
  NotPossibleException {
    boolean purged = false;
    if (isFull()) {
      purge(1);
      purged = true;
    }
    
    super.add(i, o);
    
    onObjectInserted(i,purged);
  }

  /**
   * @effects
   *  if <tt>col = null</tt>
   *    throws NullPointerException 
   *     
   *  if there is enough space in this to add collection, i.e. <tt>this.size() + col.size() > maxSize</tt>
   *    purge <tt>this.size() + col.size() - maxSize</tt> objects from this
   *    throws NotPossibleException if fails
   *    
   *  invoke <tt>super.addAll(col)</tt> and update the states of the added elements
   */
  @Override
  public boolean addAll(Collection<? extends C> col) throws NullPointerException, 
  NotPossibleException {
    if (col == null)
      throw new NullPointerException("BoundedList.addAll: input collection is null");
    
    int toAdd = col.size();
    int total = toAdd + size();
    
    if (total > maxSize) {
      purge (total-maxSize);
    } 
    
    boolean added = super.addAll(col);
    if (added) {
      onObjectAdded(col.size());
    }
    
    return added;
  }
  
  /**
   * @effects 
   *    invoke <tt>super.remove(index)</tt> 
   *    update the state of the removed element 
   *    return the removed object
   */
  @Override
  public C remove(int index) throws IndexOutOfBoundsException {
    return remove(index, true);
  }

  protected C remove(int index, boolean updateState) throws IndexOutOfBoundsException {
    C removed = super.remove(index);
    
    if (updateState && removed != null) {
      onObjectRemoved(index);
    }
    
    return removed;
  }

  /**
   * @effects 
   *    invoke <tt>super.remove(o)</tt> 
   *    update the state of the removed element 
   *    return the removed object 
   */
  @Override
  public boolean remove(Object o) {
    int index = indexOf(o);
    boolean removed = super.remove(o);
    if (removed) {
      onObjectRemoved(index);
    }
    
    return removed;
  }


  /**
   * @effects
   *    invoke <tt>super.removeRange(fromIndex,toIndex)</tt> 
   *    update the states of the removed elements 
   */
  @Override
  protected void removeRange(int fromIndex, int toIndex) {
    super.removeRange(fromIndex, toIndex);
    
    onObjectRemoved(fromIndex, toIndex);
  }
  
  /**
   * @effects 
   *  invoke <tt>super.clear()</tt> 
   *  update the states of the elements
   */
  @Override
  public void clear() {
    super.clear();
    onCleared();
  }
  
  @Override
  public C readElement(int i) throws IndexOutOfBoundsException {
    C o = super.get(i);

    onObjectRead(i);
    
    return o;
  }
  
  /**
   * @effects
   *  invoke <tt>super.set(i,o)</tt> to replace object (throwing IndexOutOfBoundsException if fails)
   *  update the state of the replaced object
   *  return the object previously at the position <tt>i</tt>
   */
  @Override
  public C set(int i, C o) throws IndexOutOfBoundsException {
    C old = super.set(i, o);
    
    onObjectSet(i);
    
    return old;
  }

  /**
   * @effects  
   *  return tuple <tt> < maxSize,s > </tt>, where <tt>s = super.toString()</tt>
   */
  @Override
  public String toString() {
    return "<"+maxSize+","+super.toString()+">";
  }
  
  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the states of the <tt>num</tt> objects that have been added to this
   */
  protected void onObjectAdded(int num) {
    // for sub-classes to override
  }

  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the state of the object at the position <tt>index</tt>
   */
  protected void onObjectRemoved(int index) {
    // for sub-classes to override
  }

  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the states of the objects in the index range <tt>(fromIndex,toIndex)</tt> (excluding 
   *  <tt>toIndex</tt>) that have been removed from this
   */
  protected void onObjectRemoved(int fromIndex, int toIndex) {
    // for sub-classes to override
  }
  
  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the state of the object that has been inserted into this at the position <tt>index</tt>
   */
  protected void onObjectInserted(int index, boolean purged) {
    // for sub-classes to override
  }
  
  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the states of all the elements of this after they have been cleared
   */
  protected void onCleared() {
    // for sub-classes to override
  }

  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the state of the object at position <tt>index</tt> that has just been read.
   */
  protected void onObjectRead(int index) {
    // for sub-classes to override
  }
  
  /**
   * This method does nothing here, sub-classes should override to print the states
   * of their elements
   * @effects  
   *  print the element states on the console
   */
  public void printEntryStates() {
    //
  }
  
  /**
   * This method does nothing here. Sub-classes should override to 
   * provide their own implementation of the update.
   * 
   * @effects 
   *  update the state of the object at position <tt>index</tt> that has just been replaced.
   */
  protected void onObjectSet(int index) {
    // for sub-classes to override
  }
  
  public boolean addAll(int index, Collection<? extends C> col) throws NullPointerException {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        "");
  }
  
  
  @Override
  public boolean removeAll(Collection<?> col) {
    throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED, 
        "");
  }
}
