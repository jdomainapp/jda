package jda.mosa.controller.assets.helper.indexer;


/**
 * @overview
 *  Represents an index consumer of {@link Indexable} class
 */
public interface IndexConsumer {
  
  /**
   * @requires
   *  the indexed class (if any) is a sub-type of {@link Indexable}
   *  
   * @effects
   *  if this is <b>actively</b> indexing the domain objects of some class
   *    return the domain class of these objects 
   *  else
   *    return <tt>null</tt>
   */
  <T extends Indexable> Class<T> getIndexedClass();
}
