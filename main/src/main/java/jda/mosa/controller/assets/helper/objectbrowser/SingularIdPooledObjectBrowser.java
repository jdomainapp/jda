package jda.mosa.controller.assets.helper.objectbrowser;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.IdObjectBuffer;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of {@link IdPooledObjectBrowser} that operates on <b>a single</b> domain object Oid  
 *  given to it in memory. Any subsequent entry added to the browser will cause the existing 
 *  entry to be removed. 
 *  
 *  <p>The object navigational behaviour is still as specified by {@link ObjectBrowser}.
 *  
 * @author dmle
 */
public class SingularIdPooledObjectBrowser<T> extends IdPooledObjectBrowser<T> {

  public SingularIdPooledObjectBrowser(DODMBasic dodm, Class<T> domainClass) {
    super(dodm, domainClass);
  }

  /**
   * @effects
   *  add to this an object o whose Oid is id
   *  remove the existing object from this.buffer
   */
  @Override
  public void add(Oid id, T o) {
    super.add(id, o);

    Oid currId = getCurrentOid();
    T currObj = getCurrentObject();
    if (currId != null) {
      try {
        super.remove(currId, currObj);
      } catch (Exception e) {
        // should not happen
        // log?
        e.printStackTrace();
      } 
    }
  }
}
