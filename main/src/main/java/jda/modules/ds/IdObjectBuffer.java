package jda.modules.ds;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import jda.modules.common.collection.SortedSequentialMap;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.dodm.DODMBasic;
import jda.mosa.model.Oid;
import jda.util.ObjectComparator;

/**
 * @overview
 *  A bounded <tt>Map<Oid,Object></tt> that maintains a sorted, sequential map  ({@see SortedSequentialMap}) of the 
 *  <tt>Oid</tt>s that are contained in it. 
 *  
 * @author dmle
 */
public class IdObjectBuffer {
  
  private DODMBasic dodm;
  private Class domainClass;
  
//  // v3.0: record the min and max Id that were initially set 
//  // when creating this object. This is used, e.g., to reset 
//  // this after sorting is done
//  private Oid minId;
//  private Oid maxId;
  
  private IdObjectMap<Oid,Object> objBuffer;
  
  private SortedSequentialMap<Oid> idSeq;

  // the backed-up objBuffer that is used to hold the original buffer
  private IdObjectMap<Oid,Object> mainObjBuffer;

  // the backed-up id sequence that is used to hold the original sequence
  private SortedSequentialMap<Oid> mainIdSeq;
  
  // derived: number of Oids currently stored in this
  private int currNumIds;

  // v2.7.2: used to determine whether to update size
  private boolean noInitialIds; 
  
  /**
   * @effects 
   *  initialise this as an empty buffer bounded by the id-range [minId, maxId]
   */
  public IdObjectBuffer(DODMBasic dodm, Class domainClass, 
      Oid minId, Oid maxId) {
    objBuffer = new IdObjectMap<Oid,Object>();
    
//    // v3.0:
//    this.minId = minId;
//    this.maxId = maxId;
    
    objBuffer.setMinId(minId);
    objBuffer.setMaxId(maxId);
    
    idSeq = new SortedSequentialMap<Oid>(minId, maxId);
    
    this.dodm = dodm;
    this.domainClass = domainClass;
    currNumIds = 0;
    
    noInitialIds = true;
  }
  
  /**
   * @effects 
   *  initialise this as a buffer containing all elements of <tt>oids</tt> as keys
   *  
   * @requires 
   *  oids != null /\ minId and maxId are in oids 
   */
  public IdObjectBuffer(DODMBasic schema, Class domainClass, Collection<Oid> oids, Oid minId, Oid maxId) {
    this(schema, domainClass, minId, maxId);
    
    // add items of oids to cache in the order specified
    if (oids.size() > 1) {
      Oid id = null, nextId = null;
      Iterator<Oid> it = oids.iterator();
      do {
        if (nextId != null && it.hasNext()) {
          // subsequent access
          id = nextId;
          nextId = it.next();
        } else if (id == null) {
          // first access
          id = it.next();
          nextId = it.next();
        } else {
          nextId = null;
        }
        
        if (nextId != null) {
          idSeq.put(id, nextId);
        }
      } while (it.hasNext());
    } 
    
    currNumIds = oids.size();
    noInitialIds = false;
  }
  
  /**
   * @effects 
   * <pre>
   *  places entry (key,value) in this
   *  return the old value associated with key (or null if no such value)</pre>
   */
  public Object put(Oid key, Object value) {
    // put into map 
    Object old = objBuffer.put(key,value);
    
    // v2.7.2: update current-num-ids if this was initialised with no ids
    if (noInitialIds)
      currNumIds++;
    
    return old;
  }
  
  /**
   * @effects 
   *  <pre>
   *  place entry (key,value) in this such that 
   *    key becomes the new highest id (max) and that
   *    nextId(max) = key
   *  </pre>
   */
  public void add(Oid key, Object value) {
    // v2.7.2: put(key,value);
    objBuffer.put(key,value);
    
    putIdSequence(lastId(), key);
    
    // update num ids
    currNumIds++;
  }
  
  /**
   * @effects 
   *  return the Object mapped to <tt>key</tt> or null if no such value exists or it is set to null
   */
  public Object get(Oid key) {
    return objBuffer.get(key);
  }
  
  /**
   * @effects 
   *  <pre>remove entry identified by key from this
   *  if firstId() = key OR lastId() = key
   *    throws ObsoleteStateSignal if fails to do so
   *  else
   *    return the old value associated with key (or null if no such value)</pre>
   */
  public Object remove(Oid key) throws ObsoleteStateSignal {
    // remove from map
    Object old = null;
    
    ObsoleteStateSignal os = null;
    try {
      old = objBuffer.remove(key);
    } catch (ObsoleteStateSignal s) {
      os = s;
    }

    try {
      idSeq.remove(key);
    } catch (ObsoleteStateSignal s) {
      if (os != null) os = s;
    }

    // update size
    currNumIds--;
    
    // key is either the first or last Id, need to update buffer and idCache
    if (os != null) {
      throw os;
    } else {
      return old;
    }
  }
  
  /**
   * This method differs from {@link #remove(Oid)} only in that it heals any gaps in the 
   * id cache that are caused by the removal of the specified key.   
   * 
   * @effects 
   *  <pre>
   *  remove entry identified by key from this
   *  'heal' any existing id cache entries that map to key s.t. they map to the one before or after it   
   *  
   *  if firstId() = key OR lastId() = key
   *    throws ObsoleteStateSignal if fails to do so
   *  else
   *    return the old value associated with key (or null if no such value)</pre>
   */
  public Object removeAndHeal(Oid key) throws ObsoleteStateSignal {
    // remove from map
    Object old = null;
    
    ObsoleteStateSignal os = null;
    try {
      old = objBuffer.remove(key);
    } catch (ObsoleteStateSignal s) {
      os = s;
    }

    try {
      // remove and heal
      idSeq.removeAndHeal(key); 
    } catch (ObsoleteStateSignal s) {
      if (os != null) os = s;
    }

    // update size
    currNumIds--;
    
    // key is either the first or last Id, need to update buffer and idCache
    if (os != null) {
      throw os;
    } else {
      return old;
    }
  }
  
  /**
   * @effects  
   *  adds the two successive Ids (id,nextId) in this
   * @requires 
   *  id != null /\ nextId != null /\ 
   *  id, nextId are contained in this /\ 
   *  (id, nextId) are two successive Oids 
   */
  public void putIdSequence(Oid id, Oid nextId) {
    idSeq.put(id, nextId);
  }
  
  /**
   * @effects 
   *  sets the highest id of this to id
   * @requires 
   *  id is valid Oid for this
   */
  public void setMaxId(Oid id) {
//    // v3.0
//    this.maxId = id;

    objBuffer.setMaxId(id);
    idSeq.setLast(id);
  }

  /**
   * @effects 
   *  sets the lowest id of this to id
   * @requires 
   *  id is valid Oid for this
   */
  public void setMinId(Oid id) {
//    // v3.0
//    this.minId = id;
    
    objBuffer.setMinId(id);
    idSeq.setFirst(id);
  }

  /**
   * @effects 
   *  if smallest Oid of the id range 
   *    return it
   *  else
   *    return null
   */
  public Oid firstId() { //throws InternalError {
    return idSeq.first();
    //return minId;
  }

  /**
   * @effects 
   *  return the id that is in the <i>first</i> position among those <b>currently contained</b> in this
   * @version 3.0 
   * @deprecated
   */
  public Oid firstBufferedId() {
    return idSeq.first();
  }

  /**
   * @effects 
   *  if id != null AND id is the first id in the id sequence currently cached in this
   *  (<u>not necessarily</u> the smallest id)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.0
   * @deprecated
   */
  public boolean isFirstBufferedId(Oid id) {
    if (id != null) {
      Oid firstId = objBuffer.findFirstId();
      
      if (id.equals(firstId)) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if exists an Oid id in this that immediately proceeds <tt>currentId</tt> (i.e. entry (currentId,id) is in this)
   *    return id
   *  else
   *    return null
   */
  public Oid nextId(Oid currentId) {
    return idSeq.next(currentId);
  }

  /**
   * @effects 
   *  if exists an Oid id  in this that immediately precedes <tt>currentId</tt> (i.e. entry (id,currentId) is in this)
   *    return id
   *  else
   *    return null
   */
  public Oid previousId(Oid currentId) {
    return idSeq.previous(currentId);
  }
  
  /**
   * @effects 
   *  if highest Oid of the id range
   *    return it
   *  else
   *    return null
   */
  public Oid lastId() { 
    // v3.0: return idCache.last();
    //return maxId;
    return idSeq.last(); //(mainIdSeq != null) ? mainIdSeq.last() : idSeq.last();
  }

  /**
   * @effects 
   *  return the id that is in the <i>last</i> position among those <b>currently contained</b> in this
   * @version 3.0 
   * @deprecated
   * 
   */
  public Oid lastBufferedId() {
    return idSeq.last();
  }

  /**
   * @effects 
   *  if id != null AND id is the last id in the id sequence currently cached in this
   *  (<u>not necessarily</u> the highest id)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.0
   * @deprecated
   */
  public boolean isLastBufferedId(Oid id) {
    if (id != null) {
      Oid lastId = objBuffer.findLastId();
      
      if (id.equals(lastId)) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if this contains id
   *    return true
   *  else
   *    return false
   */  
  public boolean contains(Oid id) {
    return objBuffer.containsKey(id); 
  }
  

  /**
   * @effects   
   *  if this is not empty
   *    return an Iterator of the domain objects currently stored in this
   *  else
   *    return null
   */
  public Iterator getObjects() {
    if (size() > 0) {
      return objBuffer.values().iterator();
    } else {
      return null;
    }
  }


  /**
   * @requires 
   *  comparator != null /\ this.size > 1 /\
   *  this is not being modified 
   *  
   * @effects 
   *  sort objects in this by <tt>comparator</tt>; 
   *  refresh this to use the sorted entries
   *  
   *  <p>throws NotPossibleException if a pre-condition is not met or
   *  failed to sort objects as required
   * @version 3.0
   */
  public void sort(ObjectComparator comparator) {
    // backup the existing object map and id sequence
    // do this once for a sorting 'session' (which may include several sortings)
    IdObjectMap<Oid,Object> currBuff = objBuffer;
    SortedSequentialMap<Oid> currIdSeq = idSeq;
    
    if (mainObjBuffer == null) { 
      mainObjBuffer = currBuff;
      objBuffer = new IdObjectMap();
    }
    
    if (mainIdSeq == null) {
      mainIdSeq = currIdSeq;
      idSeq = new SortedSequentialMap(idSeq.first(), idSeq.last());
    }
    
    // use an auxiliary data structure to sort...
    Vector<Entry<Oid,Object>> sorted = new Vector();
    
    Oid id; Object o;
    int currSz; boolean foundIndex;
    Entry<Oid,Object> currE;
    Vector<Oid> oidsBySort = new Vector();

    for (Entry<Oid,Object> e : currBuff.entrySet()) {
      id = e.getKey();
      o = e.getValue();
      if (sorted.isEmpty()) {
        sorted.add(e);
        oidsBySort.add(id);
      } else {
        // find the 'smallest' entry that is '>' e and insert e there
        currSz = sorted.size();
        foundIndex = false;
        for (int i = 0; i < currSz; i++) {
          currE = sorted.get(i);
          if (comparator.compare(currE.getValue(), o) >= 0) {
            // found 
            foundIndex = true;
            sorted.insertElementAt(e, i);
            oidsBySort.insertElementAt(id, i);
            break;
          }
        }
        
        if (!foundIndex) {
          // e is the largest -> add 
          sorted.add(e);
          oidsBySort.add(id);
        }
      }
    }

    Oid firstId = oidsBySort.firstElement();
    Oid lastId = oidsBySort.lastElement();
    
    // clear buffer and idCache (but keep other state unchanged)
    objBuffer.clear();
    idSeq.clear();
    
    // reinitialise the state
    objBuffer.setMinId(firstId);
    objBuffer.setMaxId(lastId);
    
    // populate buffer
    for (Entry<Oid,Object> e : sorted) {
      objBuffer.put(e.getKey(), e.getValue());
    }
    
    // populate idCache
    idSeq.setFirst(firstId);
    idSeq.setLast(lastId);
    
    id = null; Oid nextId = null;
    Iterator<Oid> it = oidsBySort.iterator();
    do {
      if (nextId != null && it.hasNext()) {
        // subsequent access
        id = nextId;
        nextId = it.next();
      } else if (id == null) {
        // first access
        id = it.next();
        nextId = it.next();
      } else {
        nextId = null;
      }
      
      if (nextId != null) {
        idSeq.put(id, nextId);
      }
    } while (it.hasNext());
  }
  
  /**
   * @effects 
   *  reset range (minId, maxId) to the original values after sorting by {@link #sort(ObjectComparator)}
   *  (i.e. same as when these were initialised by the constructor)
   *  
   * @version 3.0
   */
  public void endSorting() {
//    objBuffer.setMinId(minId);
//    objBuffer.setMaxId(maxId);
//    
//    idSeq.setFirst(minId);
//    idSeq.setLast(maxId);
    
    // reset the buffer and id sequence
    objBuffer = mainObjBuffer;
    idSeq = mainIdSeq;
    
    mainObjBuffer = null;
    mainIdSeq = null;
  }
  
  /**
   * @requires 
   *  isPooling = false
   *  
   * @effects 
   *  if id = null OR id < lowest id OR id > highest id 
   *    return true
   *  else
   *    return false 
   */
  public boolean isIdOutOfBound(Oid id) {
    if (id == null ||  
        (id.compareTo(firstId()) < 0 || 
         id.compareTo(lastId()) > 0))
      return true;
    else
      return false;
  }
  
  /**
   * @effects
   * if this is initialised
   *  return the number of Oids currently stored in this
   * else 
   *  return 0  
   */
  public int size() {
    return currNumIds;
  }


  /**
   * @effects 
   *  clear all objects contained in this and 
   *  clear all the related resources (effectively making this empty)
   * @version 3.0 
   */
  public void clear() {
    if (objBuffer != null) {
      objBuffer.clear();
    }
    
    if (idSeq != null) {
      idSeq.clear();
    }
    
    mainIdSeq = null;
    mainObjBuffer = null;
        
//    minId = null;
//    maxId = null;
    
    currNumIds = 0;
    noInitialIds = false;
  }
  
  @Override
  public String toString() {
    return "IdObjectBuffer(" + objBuffer.toStringContent() + ")";
  }
}
