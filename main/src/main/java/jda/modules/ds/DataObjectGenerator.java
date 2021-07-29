package jda.modules.ds;

import java.util.Collection;

import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;

/**
 * @overview
 *  Represents a generator for domain objects that are to be added to the system.
 *  
 * @example The following code describes a typical usage of this class
 * <br>Suppose <tt>MyObjectGenerator extends DataObjectGenerator</tt> for generating domain objects of some domain class.
 *  
 *  <pre>
 *    MyObjectGenerator objGen = new MyObjectGenerator();
 *    
 *    objGen.init(dodm);
 *    objGen.genObjects(dodm);
 *  </pre>
 * @author dmle
 * @version 3.2
 */
public abstract class DataObjectGenerator<T> {

  /**
   * @effects 
   *  initialise resources needed to perform {@link #genObjects(DODMBasic)}
   */
  public abstract void init(final DODMBasic dodm) throws ApplicationRuntimeException;

  /**
   * @effects 
   *  for each T in objects
   *    add T to dodm
   *  
   *  <p>throws DataSourceException if failed
   */
  protected final void addObjects(final DODMBasic dodm, final Collection<T> objects) throws DataSourceException {
    Class actual;
    Class c = getDomainClass();
    final DOMBasic dom = dodm.getDom();
    
    for (Object o : objects) {
      // if o is a sub-type of c then register it
      actual = o.getClass();
      if (actual != c && !dodm.isRegistered(actual)) {
        dodm.registerClass(actual);
      }
      
      // now add o 
      dom.addObject(o);
    }
  }

  /**
   * @effects 
   *  create data objects of type <tt>T</tt> and return them as {@link Collection}.
   */
  public abstract Collection<T> genObjects(final DODMBasic dodm) throws ApplicationException;

  /**
   * @effects 
   *  return the actual domain class of the objects created by {@link #genObjects()}.
   */
  public abstract Class<T> getDomainClass();
}
