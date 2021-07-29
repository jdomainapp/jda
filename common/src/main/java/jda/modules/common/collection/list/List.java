package jda.modules.common.collection.list;

public interface List<E> extends java.util.List<E> {
  
  /**
   * @effects
   *  invoke <tt>super.get(i)</tt> to read object (throwing IndexOutOfBoundsException if fails)
   *  update the state of the read object (if any)
   *  return the object
   */  
  public E readElement(int i) throws IndexOutOfBoundsException;
}
