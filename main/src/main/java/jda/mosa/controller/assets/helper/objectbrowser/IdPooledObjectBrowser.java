package jda.mosa.controller.assets.helper.objectbrowser;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.dodm.DODMBasic;
import jda.modules.ds.IdObjectBuffer;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-type of {@link ObjectBrowser} that operates only on the object Oids given to it in memory and, as such, 
 *  never loads additional Oids from the data source. 
 *  
 *  <p>The behaviours w.r.t to the domain objects are still as specified by {@link ObjectBrowser}.
 *  That is, these objects are loaded from the data source on-demand.
 *  
 * @author dmle
 */
public class IdPooledObjectBrowser<T> extends ObjectBrowser<T> {

  public IdPooledObjectBrowser(DODMBasic dodm, Class<T> domainClass) {
    super(dodm, domainClass);
  }

  /**
   * @effects 
   *  <pre>
   *  if exists next id in this to move next
   *    moves to the domain object of this id
   *    if stateChangeEvent = true
   *      fire state change event
   *    
   *    if object is not already in buffer (i.e. loaded from data source)
   *      return false
   *    else
   *      return true
   *      
   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to perform method
   * </pre>
   */
  @Override
  public boolean next(boolean fireStateChangeEvent)  throws DataSourceException, NotFoundException, NotPossibleException {
    if (debug)
      System.out.println("NEXT");

    boolean inBuffer = true;

    Oid nextId;
    T o;
    Oid currId = getCurrentOid();
    IdObjectBuffer buffer = getBuffer();
    Class<T> domainClass = getDomainClass();
    
    if (currId == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_NEXT, 
          "Không thể đến đối tượng sau {0}", "no current object");
    } else {
      // look up next id
      nextId = buffer.nextId(currId);
      if (nextId == null) {
          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NEXT_NOT_FOUND, 
              "Không tìm thầy mã đối tượng tiếp theo {0}", currId);
      } 

      // look up next object
      o = (T) buffer.get(nextId);
      
      if (o == null) {
        // object not in buffer, load from data source
        if (debug)
          System.out.printf("  Loading next object from data source...%n");
        o = retrieveObject(nextId);
        
        buffer.put(nextId, o);
        
        inBuffer = false;
      } 
        
      // update id
      setCurrentOid(nextId);
      setCurrentObject(o);
      
      // fire state change
      if (fireStateChangeEvent) {
        if (//currId.equals(buffer.lastId())
            isLast()
            ) {
          fireApplicationStateChange(AppState.Last);
        } else {
          fireApplicationStateChange(AppState.Next);
        }        
      }
    }    
    
    return inBuffer;
  }
  
  /**
   * @effects 
   *  <pre>
   *  if exists previous id in this to move previous
   *    moves to the domain object of this id
   *    if stateChangeEvent = true
   *      fire state change event
   *    
   *    if object is not already in buffer (i.e. loaded from data source)
   *      return false
   *    else
   *      return true
   *      
   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to perform method
   * </pre>
   */
  @Override
  public boolean prev(boolean fireStateChangeEvent) throws DataSourceException, NotFoundException, NotPossibleException {
    if (debug)
      System.out.println("PREV");

    boolean inBuffer = true;

    Oid prevId;
    T o;
    Oid currId = getCurrentOid();
    IdObjectBuffer buffer = getBuffer();
    Class<T> domainClass = getDomainClass();
    
    if (currId == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_PREVIOUS, 
          "Không thể quay lại đối tượng liền trước: {0}", "no current object");
    } else {
      // look up previous Id
      prevId = buffer.previousId(currId);
      
      if (prevId == null) {
        throw new NotFoundException(NotFoundException.Code.OBJECT_ID_PREV_NOT_FOUND, 
            "Không tìm thầy mã đối tượng liền trước {0}", currId);
      }
      
      // look up object
      o = (T) buffer.get(prevId);
      if (o == null) {
        // object not in buffer, load from data source
        if (debug)
          System.out.printf("  Loading previous object from data source...%n");          
        o = retrieveObject(prevId);
        buffer.put(prevId, o);
        
        inBuffer = false;
      } 
      
      // update id
      setCurrentOid(prevId);
      setCurrentObject(o);
      
      // fire state change
      if (fireStateChangeEvent) {
        if (//currId.equals(buffer.firstId())
            isFirst()
            ) {
          fireApplicationStateChange(AppState.First);
        } else {
          fireApplicationStateChange(AppState.Previous);
        }
      }
    }

    return inBuffer;
  }
  
  /**
   * @effects 
   * <pre>
   *  remove from this an object o whose Oid is id, 
   *  if o is the current object 
   *    browse next or previous away from o (if possible)
   *  
   *  if removal succeeded AND browsing next or previous succeeded 
   *    return true
   *  else
   *    return false
   *    
   *  Throws DBException if failed to obtain from the data source the object to browse away from o;
   *  NotFoundException if the browsed-away-to object cannot be found </pre>
   *
   *  @modifies
   *   this
   */
  @Override  
  public boolean remove(Oid id, T o) throws DataSourceException, NotFoundException, ObsoleteStateSignal {
    Oid browseToId = null;
    Oid altId = null;
    boolean nextOrPrev = false;
    
    IdObjectBuffer buffer = getBuffer();
    Class<T> domainClass = getDomainClass();

    // determine if we need to browse away from o
    if (isCurrent(id)) {
      // o is the current object
      if (hasPrevious()) {
        // o is not the first object in the buffer -> move previous
        browseToId = buffer.previousId(id);
        nextOrPrev = false;
      } else {
        // o is the first object in the buffer -> move next (if possible)
        if (hasNext()) {
          browseToId = buffer.nextId(id);
          nextOrPrev = true;
        }
      }
    } else {
      // id is not current
      if (id.equals(buffer.firstId())) {
        // get next available id (if any) to use as alternative 
        altId = buffer.nextId(id);
      } else if (id.equals(buffer.lastId())) {
        // get previous available id (if any) to use as alternative 
        altId = buffer.previousId(id);
      }
    }
    
    // remove o
    ObsoleteStateSignal signal = null;
    
    try {
      buffer.removeAndHeal(id);
    } catch (ObsoleteStateSignal s) {
      /* if there are object ids to browse
       *  sets lowest or highest id (if they were null) to the browse-to-id
       */
      Oid replacedId = (browseToId != null) ? browseToId : altId;
      
      if (replacedId != null) {
        Oid minId = buffer.firstId();
        Oid maxId = buffer.lastId();
        if (minId == null) {  // id is first in buffer
          buffer.setMinId(replacedId);
        } else if (maxId == null) { // id is last in buffer
          buffer.setMaxId(replacedId);            
        }
      } else {
        // no more objects left
        //throw s;
        signal = s;
      }
    }
    
    boolean removedAndBrowsed = false;
    
    if (signal == null) {
      // browse to object (if specified)
      if (browseToId != null) {
        if (nextOrPrev) {
          // next
          moveTo(browseToId);
        } else {
          // prev
          moveTo(browseToId);
        }
        
        removedAndBrowsed = true;
      }
    } else {
      // no more objects
      setCurrentObject(null);
      setCurrentOid(null);
    } 
    
    // buffer.min <= buffer.max
    if (buffer.firstId() != null)
      setSingleObjectBrowser(buffer.firstId().equals(buffer.lastId()));
    else
      setSingleObjectBrowser(false);
    
    if (signal != null) // throw signal after setting state (above)
      throw signal;
    else
      return removedAndBrowsed;
  }
}
