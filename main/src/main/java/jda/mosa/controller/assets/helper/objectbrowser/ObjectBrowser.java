package jda.mosa.controller.assets.helper.objectbrowser;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.ds.IdObjectBuffer;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.model.Oid;
import jda.util.ObjectComparator;
import jda.util.events.StateChangeListener;

/**
 * @overview
 *  A helper responsible for handling browsing actions that are performed over 
 *  a sub-set of domain objects. 
 *  
 *  <p>The objects are loaded from the data source on-demand, i.e. when they are requested
 *  by a browsing action.
 *   
 * @author dmle
 */
public class ObjectBrowser<T> {
  private DODMBasic dodm;
  private Class<T> domainClass;

  private Oid currId;
  private T currObj;
  private IdObjectBuffer buffer;
  
  /** v2.7.4: added to record the index position of currId in buffer */ 
  //private int currIndex;
  
  private Collection<StateChangeListener> stateListeners;
  
  // derived attributes
  private boolean singleObjectBrowser; // whether or not min id = max id
  
  // v3.0: the comparator used for sorting objects in this
  private ObjectComparator sorter;
  
  /** v3.0: whether or not the browser is running in pooling mode. When in this mode
  // all the necessary objects are available in the object buffer and there is no need 
  // to load them from the data source
   * 
   * <p>See {@link #isPooling()} for explanation of the cases under which the browser is running in this mode. 
   */
  private boolean isPooling;
  
  protected static final boolean debug = Toolkit.getDebug(ObjectBrowser.class);
  
  private static final String DOT = "\u2219";
  private static final String UNKNOWN = "\u22ef";//DOT+"/"+DOT;
  
  /**
   * @effects 
   *  initialise this as a browser for <tt>domainClass</tt> using <tt>dodm</tt>
   */
  public ObjectBrowser(DODMBasic dodm, Class<T> domainClass) {
    this.dodm = dodm;
    this.domainClass = domainClass;
    stateListeners = new ArrayList<StateChangeListener>();
  }
  
  /**
   * @effects 
   *  create and return a <tt>ObjectBrowser</tt> object whose actual type is specified by <tt>browserType</tt>
   *  
   *  <p>Throws NotPossibleException if failed to create the instance. 
   */
  public static <U> ObjectBrowser<U> createInstance(Class<? extends ObjectBrowser> browserType, DODMBasic dodm, Class<U> domainClass) 
  throws NotPossibleException {
    Constructor<? extends ObjectBrowser> cons = null; 
    try {
      cons = browserType.getConstructor(DODMBasic.class, Class.class);
      
      return cons.newInstance(dodm, domainClass);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          "Không thể tạo đối tượng lớp: {0}.{1}({2})", browserType.getSimpleName(), cons, dodm +","+ domainClass.getSimpleName());
    }
  }
  
  /**
   * @effects 
   *  registers l as a state change listener of this
   */
  public void addApplicationStateChangeListener(StateChangeListener l) {
    stateListeners.add(l);
  }
  
  /**
   * @effects 
   *  informs state change listeners of this about <tt>newState</tt>
   */
  protected final void fireApplicationStateChange(AppState newState) {
    for (StateChangeListener l : stateListeners) {
      l.stateChanged(this, newState, null, 
          // v2.7.4: browser state
          getBrowserStateAsString()
          );
    }
  }
  
  /**
   * @effects 
   *  return the browser state string of the form: 
   *  <ul>
   *     <li><tt>N</tt>: is either the total number of objects currently in the buffer (if known) or 
   *     the empty string <tt>""</tt> (if not known) 
   *  </ul>
   *  
   *  @version 2.7.4
   */
  public String getBrowserStateAsString() {
    /*
    int currIndx = getCurrentObjectIndex();
    
    if (currIndx > -1)
      return currIndx + "/" + getBrowserCapacity();
    else
      return "";
      */
    int sz = size();
    if (sz > 0) {
      return DOT + " " + sz + " " + DOT; 
    } else {
      return UNKNOWN;
    }
  }

//  /**
//   * @effects 
//   *  if size > 0
//   *    return size
//   *  else
//   *    return <tt>...</tt>
//   * @version 2.7.4
//   */
//  private String getBrowserCapacity() {
//    int sz = size();
//    if (sz > 0) {
//      return sz + ""; 
//    } else {
//      return "...";
//    }
//  }

//  /**
//   * @effects 
//   *  if currId != null
//   *    return its current position in this.buffer
//   *  else
//   *    return -1
//   *    
//   * @version 2.7.4
//   */
//  private int getCurrentObjectIndex() {
//    return currIndex;
//  }

  /**
   * @requires minId != null /\ maxId != null /\
   * @effects
   *  initialise an object buffer with min, max Oids
   */
  public void open(Oid minId, Oid maxId) {
    currId = null;
    currObj = null;
    buffer = new IdObjectBuffer(dodm, domainClass, minId, maxId);
    singleObjectBrowser = minId.equals(maxId);
  }
  
  /**
   * @requires oids != null, minId != null /\ maxId != null /\
   *  oids.contains(minId) /\ oids.contains(maxId)
   * 
   * @effects
   *  initialise an object buffer with a collection of Oids and min, max Oids being 
   *  the first and last elements 
   */
  public void open(Collection<Oid> oids, Oid minId, Oid maxId) {
    currId = null;
    currObj = null;
    buffer = new IdObjectBuffer(dodm, domainClass, oids, minId, maxId);     
    singleObjectBrowser = minId.equals(maxId);
  }

  /**
   * @requires pool != null /\ size(pool) > 0 
   * @effects 
   *  intitialises this using the objects and their Oids in <tt>pool</tt>; 
   *  turn pooling mode to <tt>on</tt>
   *   
   * @note 
   * - pool is used by reference (i.e. its content is NOT copied) <br>
   * - this method must generally be used with <b>read-only</b> browser, b/c the min and maxId 
   *    may not be the min, max Id of the id range 
   * 
   * @version 3.0
   */
  public void openPool(Map<Oid,T> pool) throws IllegalArgumentException {
    if (pool == null || pool.isEmpty())
      throw new IllegalArgumentException(ObjectBrowser.class.getName()+"(): invalid object pool: " + pool);
    
    // register the oids into buffer 
    Collection<Oid> oids = pool.keySet();
    Iterator<Oid> it = oids.iterator();
    Oid minId = it.next();  // first item
    Oid maxId = minId; // last item
    while (it.hasNext()) {
      maxId = it.next();
    }
    open(oids, minId, maxId);
    
    // put all objects into the buffer as well
    for (Entry<Oid,T> entry : pool.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
    
    isPooling = true;
  }
  
  /**
   * @requires 
   *  buffer.contains(id) = false
   *  
   * @effects 
   *  add to this an object o whose Oid is id
   */
  public void add(Oid id, T o) {
    buffer.add(id, o);
    
    if (singleObjectBrowser)
      singleObjectBrowser = false;
  }

  /**
   * @requires 
   *  buffer.contains(id) = true
   * @effects 
   *  put into this an object o whose Oid is id
   */
  public void put(Oid id, T o) {
    buffer.put(id, o);
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
  public boolean remove(Oid id, T o) throws DataSourceException, NotFoundException, ObsoleteStateSignal {
    Oid browseToId = null;
    Oid altId = null;
    boolean nextOrPrev = false;
    
    // determine if we need to browse away from o
    if (isCurrent(id)) {
      // o is the current object
      if (hasPrevious()) {
        // o is not the first object in the buffer -> move previous
        browseToId = buffer.previousId(id);
        if (browseToId == null) {
          // safe to do this because of the 2 ways the browser is populated with ids
          browseToId = dodm.getDom().retrieveIdFirstBefore(domainClass, id);
        }
        nextOrPrev = false;
      } else {
        // o is the first object in the buffer -> move next (if possible)
        if (hasNext()) {
          browseToId = buffer.nextId(id);
          if (browseToId == null) {
            // safe to do this because of the 2 ways the browser is populated with ids
            browseToId = dodm.getDom().retrieveIdFirstAfter(domainClass, id);
          }
          nextOrPrev = true;
        }
      }
    } else {
      // id is not current
      if (id.equals(buffer.firstId())) {
        // get next available id (if any) to use as alternative 
        altId = buffer.nextId(id);
        if (altId == null) {
          // safe to do this because of the 2 ways the browser is populated with ids
          altId = dodm.getDom().retrieveIdFirstAfter(domainClass, id);
        }
      } else if (id.equals(buffer.lastId())) {
        // get previous available id (if any) to use as alternative 
        altId = buffer.previousId(id);
        if (altId == null) {
          // safe to do this because of the 2 ways the browser is populated with ids
          altId = dodm.getDom().retrieveIdFirstBefore(domainClass, id);
        }
      }
    }
    
    // remove o
    ObsoleteStateSignal signal = null;
    
    try {
      buffer.remove(id);
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
      singleObjectBrowser = buffer.firstId().equals(buffer.lastId());
    else
      singleObjectBrowser = false;
    
    if (signal != null) // throw signal after setting state (above)
      throw signal;
    else
      return removedAndBrowsed;
  }

  /**
   * @requires 
   *  comparator != null /\ this.size > 1
   * 
   * @effects 
   *  sort objects <b>currently</b> contained in this by <tt>comparator</tt>; 
   *  refresh this to use the sorted entries
   *  
   *  <p>If silent = true then skip the initial sorting check 
   *  
   *  <p>throws NotPossibleException if a pre-condition is not met or
   *  failed to sort objects as required
   *  
   * @version 
   * - 3.0 <br>
   * - 3.1: add silent 
   */
  public void sort(ObjectComparator comparator, boolean silent) throws NotPossibleException {
    int sz = size();
    if (sz <= 1) {
      if (!silent)
        throw new NotPossibleException(NotPossibleException.Code.INVALID_BROWSER_STATE_FOR_SORTING,
          new Object[] {this.toString(), sz, 2});
      else
        return;
    }
    
    buffer.sort(comparator);
    
    sorter = comparator;
    
    isPooling = true;
  }
  
  /**
   * @effects 
   *  reset range (minId, maxId) to the original values after sorting  by {@link #sort(ObjectComparator)}
   *  (i.e. same as when these were initialised by one of the <tt>open</tt> operations)
   *  
   * @version 3.0
   */
  public void endSorting() {
    if (buffer != null) {
      buffer.endSorting();
    }
    
    isPooling = false;
  }
  
  /**
   * @effects 
   *  if this is currently in sorting mode
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  @version 3.0
   */
  public boolean isSortingOn() {
    return sorter != null;
  }

  /**
   * @effects 
   *  if this is currently sorting objects by <b>id</b> domain attribute  AND in ascending order
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *  @version 3.0
   */
  public boolean isSortingIdAttributeAsc() {
    return sorter != null && sorter.isSortingIdAttributeAsc();
  }
  
//  /**
//   * @effects
//   *  if this is currently running <b>object pooling</b> (i.e. all the necessary objects are loaded in the browser's buffer) 
//   *    return <tt>true</tt>
//   *  else
//   *    return <tt>false</tt>
//   *  
//   *  <p>A browsing is running in this mode under one of the following cases: <br>
//   *  - it has just performed a sorting on the objects (by {@link #sort(ObjectComparator)}), <br>
//   *  - {@link #isPooling} option is manually turned on (via {@link #openPool(Map)})<br> 
//   *      
//   * @version 3.0
//   */
//  private boolean isPooling() {
//    return isPooling;
//  }

//  /**
//   * This method is used by the program to manually turn on/off the <tt>isPooling</tt> parameter,
//   * depending on how it is browsing through the objects managed by this. A typical example of when 
//   * this is set to <tt>true</tt> is when the program is browsing through all the objects at once to populate the 
//   * browser with these objects, so that it can display them on a view (e.g. a data table) that 
//   * requires all the objects to be loaded.
//   *  
//   * <p>The browser may be in the pooling mode under other cases, 
//   * as explained in {@link #isPooling()}.  
//   * 
//   * @effects 
//   *  set this.isPooling = tf
//   * @version 3.0
//   */
//  public void setIsPooling(boolean tf) {
//    this.isPooling = tf;
//  }
  
  /**
   * @effects 
   *  return the currently active object 
   */
  public T getCurrentObject() {
    return currObj;
  }

  /**
   * @effects 
   *  return the Oid of the currently active object 
   */
  public Oid getCurrentOid() {
    return currId;
  }

  /**
   * This method should only be used by sub-type to access the current id
   * 
   * @effects 
   *  set this.currId = id
   */
  protected final void setCurrentOid(Oid id) {
    currId = id;
  }
  
  /**
   * This method should only be used by sub-type to access the current object
   * 
   * @effects 
   *  set this.currObj = o
   */
  protected final void setCurrentObject(T o) {
    currObj = o;
  }
  
  /**
   * This method should only be used by sub-type to access the buffer
   * 
   * @effects 
   *  return this.buffer
   */
  protected final IdObjectBuffer getBuffer() {
    return buffer;
  }

  /**
   * This method only returns the objects currently loaded in the buffer (which is 
   * in general a sub-set of those whose ids are in the browser) 
   * 
   * @effects   
   *  if this is not empty
   *    return an Iterator of the domain objects currently loaded in the buffer
   *  else
   *    return null
   */
  public Iterator<T> getObjectBuffer() {
    if (isEmpty()) {
      return null;
    } else {
      return buffer.getObjects();
    }
  }
  
  /**
   * This method should only be used by sub-type to access the domain class
   * 
   * @effects 
   *  return this.domainClass
   */
  public final Class<T> getDomainClass() {
    return domainClass;
  }
  
  /**
   * @effects 
   *  if this is to contain only one object
   *    return true
   *  else
   *    return false
   */
  public boolean isSingleObjectBrowser() {
    return singleObjectBrowser;
  }
  
  /**
   * This method should only be used by sub-type to access the concerned attribute
   * 
   * @effects 
   *  set this.singleObjectBrowser = tf
   */  
  protected final void setSingleObjectBrowser(boolean tf) {
    this.singleObjectBrowser = tf;
  }
  
  /**
   * @effects 
   *  if this has been opened (i.e. by invoking one of the <tt>open(...)</tt> methods)
   *    return true
   *  else
   *    return false
   */
  public boolean isOpened() {
    return buffer != null;
  }
  
  /**
   * @effects
   *  if id == currId
   *    return true
   *  else
   *    return false
   */
  public boolean isCurrent(Oid id) {
    return (id != null && currId != null && id.equals(currId));
  }
  
  /**
   * @effects 
   *  if this is opened AND the current object is the first object
   *    return true
   *  else
   *    return false
   */
  public boolean isFirst() {
    return isOpened() && currId != null && 
        currId.equals(buffer.firstId())
        /* v3.0: support sorting */
        /*
        ( (isPooling && buffer.isFirstBufferedId(currId)) ||
          (!isPooling && currId.equals(buffer.firstId()))
        )
        */
        ;  
  }
  
  /**
   * @effects 
   *  if this is opened AND the current object is the last object
   *    return true
   *  else
   *    return false
   */
  public boolean isLast() {
    return isOpened() && currId != null &&
        /*v3.0: support sorting */
        currId.equals(buffer.lastId());
    /*
        ( (isPooling && buffer.isLastBufferedId(currId)) ||
            (!isPooling && currId.equals(buffer.lastId()))
          )        
        ;
    */  
  }
  
  /**
   * This method invokes {@link #first(boolean)} with <tt>true</tt>.
   * 
   * @effects 
   *  <pre>moves to the first domain object managed by this
   *  
   *  fire state change event
   *  
   *  if object is in cache of this
   *    return true
   *  else
   *    return false
   *  
   * throws DBException if fails to obtain object from source;
   * NotFoundException if no such object is found
   * </pre>
   */
  public boolean first() throws DataSourceException, NotFoundException {
    return first(true);
  }
  
  /**
   * @effects 
   *  <pre>moves to the first domain object managed by this
   *  
   *  if fireStateChange = true
   *    fire state change event
   *  
   *  if object is in cache of this
   *    return true
   *  else
   *    return false
   *    
   * throws DBException if fails to obtain object from source;
   * NotFoundException if no such object is found
   * </pre>
   */
  public boolean first(boolean fireStateChange) throws DataSourceException, NotFoundException {
    if (debug)
      System.out.println("FIRST");
    
    boolean inBuffer = false;
    
    currId = buffer.firstId(); //isPooling ? buffer.firstBufferedId() : buffer.firstId();
    Object o = buffer.get(currId);
    if (o == null) {
      if (debug)
        System.out.println("  Loading object from data source...");
      o = retrieveObject(currId);
      
      // put object into buffer
      buffer.put(currId, o);
    } else {
      inBuffer = true;
      //System.out.println("  Object in buffer...");
    }
    
    currObj = (T) o;
    
    if (fireStateChange)
      fireApplicationStateChange(AppState.First);
    
    return inBuffer;
  }
  
  /**
   * @effects 
   *  <pre>moves to the last domain object managed by this
   *  
   *  fire state change
   *  
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no such object is found
   * </pre>
   */
  public boolean last() throws DataSourceException, NotFoundException {
    return last(true);
  }
  
  /**
   * @effects 
   *  <pre>moves to the last domain object managed by this
   *  
   *  if fireStateChange = true
   *    fire state change
   *    
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no such object is found
   * </pre>
   */
  public boolean last(boolean fireStateChange) throws DataSourceException, NotFoundException {
    if (debug)
      System.out.println("LAST");

    boolean inBuffer = false;

    currId = buffer.lastId(); //(isPooling) ? buffer.lastBufferedId() : buffer.lastId();
    Object o = buffer.get(currId);
    if (o == null) {
      if (debug)
        System.out.println("  Loading object from data source...");
      o = retrieveObject(currId);

      // put object into buffer
      buffer.put(currId, o);
    } else {
      inBuffer = true;
      //System.out.println("  Object in buffer...");
    }
    
    currObj = (T) o;
    
    if (fireStateChange)
      fireApplicationStateChange(AppState.Last);

    return inBuffer;
  }
  
  /**
   * @effects 
   *  <pre>moves to the previous domain object managed by this
   *  
   *  fire state change event
   *  
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to performe method
   * </pre>
   */
  public boolean prev() throws DataSourceException, NotFoundException, NotPossibleException {
    return prev(true);
  }
  
  /**
   * @effects 
   *  <pre>moves to the previous domain object managed by this
   *  
   *  if fireStateChangeEvent = true
   *    fire state change event
   *    
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to performe method
   * </pre>
   */
  public boolean prev(boolean fireStateChangeEvent) throws DataSourceException, NotFoundException, NotPossibleException {
    if (debug)
      System.out.println("PREV");

    boolean inBuffer = false;

    Oid prevId;
    Object o;
    if (currId == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_PREVIOUS, 
          "Không thể quay lại đối tượng liền trước: {0}", "no current object");
    } else {
      // try buffer
      
      // look up previous Id
      prevId = buffer.previousId(currId);
      if (prevId == null) {
        // object ID not in buffer, load from data source
        if (debug)
          System.out.printf("  Loading previous object Id from data source...%n");
        prevId = dodm.getDom().retrieveIdFirstBefore(domainClass, currId);

        if (prevId == null || buffer.isIdOutOfBound(prevId)) {
          // no more ids, cannot move
          //prevId = null;
          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_PREV_NOT_FOUND, 
              "Không tìm thầy mã đối tượng liền trước {0}", currId);
        } else {  
          // store in buffer
          buffer.putIdSequence(prevId, currId);
        }

        inBuffer = false;
      } else {
        inBuffer = true;
      }

      if (prevId != null) {
        // look up object
        o = buffer.get(prevId);
        if (o == null) {
          // object not in buffer, load from data source
          if (debug)
            System.out.printf("  Loading previous object from data source...%n");          
          o = retrieveObject(prevId);
          buffer.put(prevId, o);
          
          inBuffer = false;
        } else {
          inBuffer = inBuffer & true;
  
          // object already in buffer, get it
          // System.out.printf("  Previous object is in buffer...%n");
        }
        
        // update id
        currId = prevId;
        currObj = (T) o;
        
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
    }

    return inBuffer;
  }

  /**
   * @effects 
   *  <pre>moves to the next domain object managed by this
   *  
   *  fire state change event
   *  
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to performe method
   * </pre>
   */
  public boolean next()  throws DataSourceException, NotFoundException, NotPossibleException {
    return next(true);
  }
  
  /**
   * @effects 
   *  <pre>moves to the next domain object managed by this
   *  
   *  if stateChangeEvent = true
   *    fire state change event
   *    
   *  if object is in cache of this
   *    return true
   *  else
   *    return false

   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * NotPossibleException if fails to performe method
   * </pre>
   */
  public boolean next(boolean fireStateChangeEvent)  throws DataSourceException, NotFoundException, NotPossibleException {
    if (debug)
      System.out.println("NEXT");

    boolean inBuffer = false;

    Oid nextId;
    Object o;
    if (currId == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_MOVE_NEXT, 
          "Không thể đến đối tượng sau {0}", "no current object");
    } else {
      // try buffer
      
      // look up next id
      nextId = buffer.nextId(currId);
      if (nextId == null) {
        // object Id not in buffer or no more object ids, try loading from data source
        if (debug)
          System.out.printf("  Loading next object Id from data source...%n");
        nextId = dodm.getDom().retrieveIdFirstAfter(domainClass, currId);
        
        if (nextId == null || buffer.isIdOutOfBound(nextId)) {
          // no more ids, cannot move next
          //nextId = null;
          throw new NotFoundException(NotFoundException.Code.OBJECT_ID_NEXT_NOT_FOUND, 
              "Không tìm thầy mã đối tượng tiếp theo {0}", currId);
        } else {  
          // store in buffer
          buffer.putIdSequence(currId, nextId);
        }
        inBuffer = false;
      } else {
        inBuffer = true;
      }

      if (nextId != null) {
        // look up next object
        o = buffer.get(nextId);
        
        if (o == null) {
          // object not in buffer, load from data source
          if (debug)
            System.out.printf("  Loading next object from data source...%n");
          o = retrieveObject(nextId);
          
          buffer.put(nextId, o);
          
          inBuffer = false;
        } else {
          inBuffer = inBuffer & true;
          
          // System.out.printf("  Next object is in buffer...%n");
        }
        
        // update id
        currId = nextId;
        currObj = (T) o;
        
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
    }    
    
    return inBuffer;
  }
  
//  /**
//   * This method works similar to the second part of {@link #prev()} (after 
//   * the next object id has been determined). It is used specifically for 
//   * operations (e.g. {@link #remove(Oid, Object)}) that need to perform a browsing function as part of 
//   * its logic but do not rely on the current Id of this.
//   * 
//   * @effects 
//   * <pre>moves previous to the domain object specified by id 
//   * throws DBException if fails to obtain object from source;
//   * NotFoundException if no object id or no object is found;
//   * </pre>
//   */
//  private void prev(Oid id) throws DBException, NotFoundException {
//    // look up object
//    Object o = buffer.get(id);
//    if (o == null) {
//      // object not in buffer, load from data source
//      if (debug)
//        System.out.printf("  Loading previous object from data source...%n");          
//      o = getObject(domainClass, id);
//      buffer.put(id, o);
//    } else {
//      // object already in buffer, get it
//      // System.out.printf("  Previous object is in buffer...%n");
//    }
//    
//    // update id
//    currId = id;
//    currObj = (T) o;
//    
//    // fire state change
//    if (currId.equals(buffer.firstId())) {
//      fireApplicationStateChange(AppState.First);
//    } else {
//      fireApplicationStateChange(AppState.Previous);
//    }
//  }
//  
  /**
   * This method works similar to the second part of {@link #next()} or {@link #prev()} (after 
   * the next object id has been determined). It is used specifically for 
   * operations (e.g. {@link #remove(Oid, Object)}) that need to perform a browsing function as part of 
   * its logic but do not rely on the current Id of this.
   * 
   * @effects 
   * <pre>moves next to the domain object specified by id 
   * throws DBException if fails to obtain object from source;
   * NotFoundException if no object id or no object is found;
   * </pre>
   */
  protected final void moveTo(Oid id) throws DataSourceException, NotFoundException {
    //  look up next object
    Object o = buffer.get(id);
    
    if (o == null) {
      // object not in buffer, load from data source
      if (debug)
        System.out.printf("  Loading next object from data source...%n");
      o = retrieveObject(id);
      
      buffer.put(id, o);
      
    } else {
      // System.out.printf("  Next object is in buffer...%n");
    }
    
    // update id
    currId = id;
    currObj = (T) o;
    
    // fire state change
    if (currId.equals(buffer.lastId()
        )) {
      fireApplicationStateChange(AppState.Last);
    } else if (currId.equals(buffer.firstId()
        )) {
      fireApplicationStateChange(AppState.First);
    } else {
      // either Next or Previous is fine
      fireApplicationStateChange(AppState.Next);
    }
  }
  
  /**
   * @effects 
   *  if smallest id != highest id AND currId < highest ID
   *    return true
   *  else
   *    return false 
   * @version 
   *  - 3.0: updated to support pooling
   */
  public boolean hasNext() {
    /* v3.0
    if (!singleObjectBrowser && 
        (currId != null && 
           currId.compareTo(buffer.lastId()) < 0
        )
       )
      return true;
    else
      return false;
    */
    if (!singleObjectBrowser && currId != null) {
      if ((isPooling && !currId.equals(buffer.lastId())) //!buffer.isLastBufferedId(currId))  // in sorting  
          || 
          (!isPooling && currId.compareTo(buffer.lastId()) < 0)) {
        return true;
      }
    }
    
    // otherwise
    return false;
  }

  /**
   * @effects 
   *  if smallest id != highest id AND currId > smallest ID
   *    return true
   *  else
   *    return false
   * @version 
   *  - 3.0: updated to support pooling     
   */  
  public boolean hasPrevious() {
    /* v3.0
    if (!singleObjectBrowser && 
        (currId != null &&
          currId.compareTo(buffer.firstId()) > 0
         )
        )
      return true;
    else
      return false;
    */
    if (!singleObjectBrowser && currId != null) {
      if ((isPooling && !currId.equals(buffer.firstId())) //!buffer.isFirstBufferedId(currId))  // in sorting  
          || 
          (!isPooling && currId.compareTo(buffer.firstId()) > 0)) { 
        return true;
      }
    }
    
    // otherwise
    return false;
  }
  
  /**
   * @requires 
   *  id != null /\ o != null /\ o and oid are contained in this
   * 
   * @effects 
   *  update current Id and object of this to <tt>id</tt> and <tt>o</tt> 
   */
  public void move(Oid id, T o) {
    currId = id;
    currObj = o;
  }
  
  /**
   * @effects 
   *  if this contains id
   *    return true
   *  else
   *    return false
   */
  public boolean contains(Oid id) {
    return buffer.contains(id);
  }

  /**
   * @effects
   * if this is opened
   *  return the current number of entries in this
   * else 
   *  return 0  
   */
  public int size() {
    if (buffer != null) {
      return buffer.size();
    } else {
      return 0;
    }
  }

  /**
   * @effects 
   *  if this is not opened or this contains no entries
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * @effects 
   *  clear the object-dependent state of this
   */
  public void clear() {
    currId = null;
    currObj = null;
    buffer = null;
    singleObjectBrowser = false;
    sorter = null;  // v3.0
    isPooling = false;  // v3.0
  }
  
  /**
   * @requires
   *  id is a valid Oid of domainClass
   * @effects 
   *  <pre>if a domain object of domainClass whose Oid is id is in c's object pool
   *    return the object
   *  else
   *    load and return object from source
   *    
   *  throws DBException if fails to load object from source;
   *  NotFoundException if no such object is found</pre>
   */
  protected T retrieveObject(Oid id) throws DataSourceException, NotFoundException {
    DOMBasic dom = dodm.getDom();
    
    T o = (T) dom.getObject(domainClass, id);
    
    if (o == null
        && dom.isObjectSerialised() // v2.8: added this check
        )
      o = (T) dom.loadObject(domainClass, id);
    
    return o;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+" (" + domainClass + ")";
  }
}
