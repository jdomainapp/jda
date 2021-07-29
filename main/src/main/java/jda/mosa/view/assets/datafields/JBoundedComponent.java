package jda.mosa.view.assets.datafields;

import jda.modules.dcsl.syntax.DAttr;
import jda.util.events.ChangeListener;

/**
 * @overview
 *  A sub-type of {@link ChangeListener} that is implemented by {@link JBindableField}, 
 *  so that objects of this class (and all of its sub-types) are treated as bounded components 
 *  by a data source.  
 *  
 * @author dmle
 *
 */
public interface JBoundedComponent extends ChangeListener {

  /**
   * @effects 
   *  clear the binding state of this, without actually removing the binding 
   *  (in most case this involves clearing the data objects that have been loaded
   *  via the binding, so that they can be reloaded, for example)
   */
  void clearBinding();

  /**
   * @effects 
   *   refresh the binding state of this to obtain the most up-to-date data objects
   * @version 3.1
   */
  void refreshBinding();
  
  /**
   * @effects 
   * return the <b>bounded</b> attribute of the bounded component or <tt>null</tt> if no such attribute is 
   * specified.
   * 
   * @version 2.7.4
   */
  DAttr getBoundConstraint();

  /**
   * @effects 
   * return the domain attribute of the bounded component or <tt>null</tt> if no such attribute is 
   * specified.
   * 
   * @version 2.7.4
   */
  DAttr getDomainConstraint();

}
