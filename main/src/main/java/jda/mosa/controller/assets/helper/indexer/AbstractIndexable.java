package jda.mosa.controller.assets.helper.indexer;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *    A helper class of {@link Indexable} used to ease the implementation of this interface for 
 *    domain classes that do not extend other super-types. 
 *  
 *   <p>Domain classes that extend this does not need to do anything. However, only <b>non-serialisable</b>
 *   classes can extend {@link AbstractIndexable}. Serialisable classes must implement the interface {@link Indexable}. 
 *   
 *   <p>For domain classes that must implement {@link Indexable} directly, just mimick the code of this 
 *   class.
 *     
 *   <p>This class must not be used by itself to create domain objects. 
 *     
 * @author dmle
 */
@DClass(serialisable=false)
public abstract class AbstractIndexable implements Indexable {

  // virtual attribute
  @DAttr(name="index",type=Type.Integer,mutable=false,length=3,
      serialisable=false // IMPORTANT
      )
  private Integer index;
  
  private IndexManager indexManager;
  
  @Override
  public void setIndex(IndexManager indexManager, int ind) {
    if (this.indexManager == null)
      this.indexManager = indexManager;
  }

  @Override
  public Integer getIndex() {
    if (indexManager != null)
      return indexManager.getIndex(this.getClass(), this);
    else 
      return null;
  }
}
