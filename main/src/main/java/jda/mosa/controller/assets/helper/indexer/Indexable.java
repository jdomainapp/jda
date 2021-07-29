package jda.mosa.controller.assets.helper.indexer;

/**
 * @overview
 *  A generic interface for a domain class to implement, which makes its objects indexable 
 *  by the application. 
 *  
 *  <p>An application that needs to use an indexable domain class first needs to invoke the 
 *  {@link #setIndex(IndexManager, int)}
 *  on its objects to set their indices. Thereafter, it can retrieve these by invoking the 
 *  {@link #getIndex()} method on the objects.
 *  
 *  <p>The object indices are useful, for instance, in a tabular report in which 
 *  the objects are presented with a sequence number. 
 *  
 * @author dmle
 * @version 
 *  2.7.2
 *  <br>2.7.4: improved to use index manager
 */
public interface Indexable {
  
  /**
   * @effects 
   *  initialises <tt>this.index</tt> to <tt>ind</tt> using <tt>indexManager</tt> 
   */
  public void setIndex(IndexManager indexManager, int ind);
  
//  /**
//   * @effects   
//   * sets the current consumer of the index to <tt>src</tt>. 
//   * 
//   * <p>This consumer is used by {@link #getIndex()} to look up the right index value to use.
//   * 
//   */
//  public void setIndexConsumer(Object src);
  
  /**
   * <b>IMPORTANT:</b> the header of this method must not be changed!!
   * 
   * @effects 
   *  if <tt>this.index</tt> is defined for the current source object
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  public Integer getIndex();
  
//  /**
//   * This method is used to reinitialise the index counter used for a given source object so that 
//   * it can be used again to re-create the indices of all the domain objects that are mapped to the 
//   * source object. 
//   * 
//   * @requires
//   *  src != null
//   * @effects
//   *  resets index counter that is mapped to <tt>src</tt> to 0
//   */
//  public void resetIndexCounter(Object src);
}
