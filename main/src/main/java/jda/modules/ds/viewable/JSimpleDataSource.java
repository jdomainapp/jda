/**
 * 
 */
package jda.modules.ds.viewable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A simple {@link JDataSource} that operates directly over the object pool of the domain class. 
 *  These objects are retrieved using the {@link DODMBasic} object.  
 *  
 *  <p>This data source is simple in that it does not use a cache to store previously looked-up result.
 *  
 * @author dmle
 *
 * @version 3.2c
 */
public class JSimpleDataSource extends JDataSource {

  /** the object collection */
  private Collection objectBuffer;
  
  public JSimpleDataSource(ControllerBasic mainCtl, DODMBasic dodm,
      Class domainCls) {
    super(mainCtl, dodm, domainCls);
  }

  @Override
  public boolean isEmpty() {
    return (objectBuffer == null);
  }

  @Override
  public Iterator iterator() {
    if (objectBuffer == null) {
      objectBuffer = retrieveObjects();
    }
    
    if (objectBuffer != null)
      return objectBuffer.iterator();
    else
      return null;
  }

  /**
   * @effects 
   *  retrieve from the underlying data source the domain objects of the domain class
   *  if exist
   *    return them as {@link Collection}
   *  else
   *    return null
   */
  private Collection retrieveObjects() throws NotPossibleException, NotFoundException {
    // retrieve objects from pool
    DOMBasic dom = getDodm().getDom();
    
    Class domainCls = getDomainClass();

    /*v3.3: improved to use dom.retrieveObjects(domainCls) when it is certain that a connected OSM is being used
    // check in object pool first
    Collection objs = dom.getObjects(domainCls);
    
    if (objs == null || objs.isEmpty()) {
      // may not have been loaded 
      try {
        // retrieve from data source
        Map<Oid,Object> valMap = dom.retrieveObjects(domainCls);
        
        if (valMap == null)
          objs = null; // new ArrayList();
        else
          objs = valMap.values();
      } catch (NotPossibleException | DataSourceException e) {
        objs = null; // new ArrayList();
      }
    }    
    */
    Collection objs = null;
    
    if (DSMBasic.isTransient(domainCls) || DSMBasic.isEnum(domainCls)) {
      // non-serialisable or enum: get from memory 
      objs = dom.getObjects(domainCls);
      if (objs != null && objs.isEmpty()) objs = null;
      
    } else {
      // serialisable objects: get from data source first, if none found then try memory
      if (dom.isConnectedToDataSource()) {
        try {
          // retrieve from data source
          Map<Oid,Object> valMap = dom.retrieveObjects(domainCls);
          
          if (valMap == null)
            objs = null; // new ArrayList();
          else
            objs = valMap.values();
        } catch (NotPossibleException | DataSourceException e) {
          objs = null; // new ArrayList();
        }
      } 
      
      if (objs == null) {
        // try in memory as the last resort
        objs = dom.getObjects(domainCls);
        if (objs != null && objs.isEmpty()) objs = null;
      }
    }
    
    return objs;
  }

  @Override
  public void clearBuffer() {
    super.clearBuffer();
    
    objectBuffer = null;
  }
}
