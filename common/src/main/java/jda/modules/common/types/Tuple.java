package jda.modules.common.types;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @overview
 *  Represents an n-ary, immutable, serialisable tuple, the elements of which can be of arbitrary and different types
 *  
 * @author dmle
 */
public class Tuple implements Serializable {
  static final long serialVersionUID = 1405417795417L;
  
  // tuple content
  private LinkedList content;
  
  /**
   * @effects 
   *  if elements = null OR elements is empty
   *    throws IllegalArgumentException
   *  else 
   *    intitialises this as a tuple whose content contains <tt>elements</tt> (in the same order)
   */
  protected Tuple(Serializable...elements) throws IllegalArgumentException {
    if (elements == null || elements.length == 0)
      throw new IllegalArgumentException("Tuple.init: no elements specified");
    
    content = new LinkedList();
    Collections.addAll(content, elements);
  }

  /**
   * @effects 
   *  if elements = null OR elements is empty
   *    throws IllegalArgumentException
   *  else 
   *    returns a new Tuple whose content contains <tt>elements</tt> (in the same order)
   */
  public static Tuple newInstance(Serializable...elements)  throws IllegalArgumentException {
    //TODO: if performance is a concerned then 
    // maintains a cache of Tuple objects created for re-use here
    return new Tuple(elements);
  }
  
  /**
   * @effects 
   *  if index < 0 OR index >= size()
   *    throws IndexOutOfBoundsException
   *  else
   *    return element at the specified <tt>index</tt>
   */
  public Object getElement(int index) {
    if (index < 0 || index >= size())
      throw new IndexOutOfBoundsException("Tuple.getElement: the specified index is invalid : " + index);
    
    return content.get(index);
  }
  
  /**
   * @effects 
   *  return the number of elements of this
   */
  public int size() {
    return content.size();
  }
  
  @Override
  public String toString() {
    StringBuffer sb =  new StringBuffer("Tuple <");
    
    int sz = size();
    Object e;
    for (int i = 0; i < sz; i++) {
      e = content.get(i);
      sb.append(e);
      if (i < sz-1)
        sb.append(",");
    }
    
    sb.append(">");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((content == null) ? 0 : content.hashCode());
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
    Tuple other = (Tuple) obj;
    if (content == null) {
      if (other.content != null)
        return false;
    } else if (!content.equals(other.content))
      return false;
    return true;
  }


}
