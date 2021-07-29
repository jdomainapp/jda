package jda.mosa.controller.assets.helper.indexer;

import java.util.HashMap;
import java.util.Map;

import jda.mosa.controller.assets.util.AppState;
import jda.util.events.StateChangeListener;

/**
 * @overview
 *  Responsible for managing the indices of {@link Indexable} domain classes.  
 *  
 *  <p>To have an index of the domain objects, a domain class must implement {@link Indexable}.
 *  
 * <p>A domain object may have multiple index values, each is set by a different 
 * source object (<b>index consumer</b>) that needs its service. 
 * Attribute {@link #indexMap} is a 2-level map that is  
 * used to keep track of the index values of a domain object that are set by different index consumers. 
 * 
 * <p>A domain class may, therefore, be of service to more than one index consumers. However, 
 * <b>only one index consumer is active at a given time</b>. This is based on a reasonable 
 * assumption that only one index of a given object is used by the application at a time. 
 * For example, a particular object form (a typical index consumer) is viewing the state of a domain object 
 * and so the index value for this particular view is retrieved and used. When another object form, that also  
 * needs the state of the domain object, becomes active the index value for this form is read and used instead.
 * 
 * <p>Even when multiple views are being presented to the user on the desktop, it is still reasonable to assume 
 * that the user is focusing only on one view at a time. 
 * 
 * @author dmle
 */
public class IndexManager implements StateChangeListener {
  
  /** record the index value(s) of each domain object
   * maps: <tt>domain object -> Map(Index_Consumer,Integer)</tt>, 
   * where <tt>Map(Index_Consumer,Integer)</tt> maps an index consumer to the index value that it sets
   * for the domain object.  
   */
  private Map<Object,Map<Object,Integer>> indexMap;
  
  /**
   *  record the current index counters for each index cunsumer for each domain class
   */
  private Map<Class,Map<Object,Integer>> indexCounterMap;

  private Map<Class,Object> currConsumerMap;
  
  private static IndexManager instance;
  
  // singleton
  private IndexManager() {
    indexMap = new HashMap<>();
    indexCounterMap = new HashMap<>();
    currConsumerMap = new HashMap<>();
  }
  
  public static IndexManager getInstance() {
    if (instance == null) {
      instance = new IndexManager();
    }
    
    return instance;
  }
  
  /**
   * @effects 
   *  create the next object index for the consumer <tt>consumer</tt> of the domain class <tt>cls</tt> 
   */
  private int nextIndex(Class cls, Object consumer) {
    Map<Object,Integer> m = indexCounterMap.get(cls);
    if (m == null) {
      // not yet registered -> register
      m = new HashMap<Object,Integer>();
      indexCounterMap.put(cls, m);
    }
    
    Integer currIndex = m.get(consumer);
    
    if (currIndex == null)
      currIndex = 0;
    
    currIndex++;
    m.put(consumer,currIndex);
    
    return currIndex;
  }

  /**
   * @effects 
   *  reset the index counter of the consumer <tt>consumer</tt> that is used to index objects of the 
   *  domain class <tt>cls</tt> to 0.
   */
  public void resetIndexCounter(Class cls, Object consumer) { 
    //    indexCounterMap.remove(src); 
    // the index counter map of cls
    Map<Object,Integer> m = indexCounterMap.get(cls);
    if (m != null) {
      // remove the entry for src
      m.remove(consumer);
    }
  }

  /**
   * @effects 
   *  create next index value for <tt>cls</tt> w.r.t to index consumer <tt>consumer</tt> and 
   *  set it into <tt>o</tt>
   */
  public void setIndex(Class cls, Indexable o, Object consumer) {
    int nextIndex = nextIndex(cls, consumer);
    o.setIndex(this, nextIndex);
    
    // store the index into indexMap so that it can be retrieved later by getIndex
    Map<Object,Integer> m = indexMap.get(o);
    
    if (m == null) {
      m = new HashMap<>();
      indexMap.put(o, m);
    }
    
    m.put(consumer, nextIndex);
  }
  
  /**
   * @effects 
   *  if exists index of object <tt>o</tt> of the domain class <tt>cls</tt> for the current consumer of <tt>cls</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  public Integer getIndex(Class cls, Object o) {
    Map<Object,Integer> m = indexMap.get(o);
    
    Object currConsumer = currConsumerMap.get(cls);
    
    if (currConsumer != null && m != null) {
      return m.get(currConsumer);
    } else {
      return null;
    }
  }
  
  
  /**
   * @effects 
   *  set the current index consumer of all domain objects of <tt>cls</tt> to <tt>consumer</tt>, 
   *  overriding the existing one (if any)
   */
  public void setIndexConsumer(Class cls, Object consumer) {
    currConsumerMap.put(cls, consumer);
  }
  
   
  @Override // StateChangeListener
  public void stateChanged(Object src, AppState state, String messages,
      Object... data) {
    
    if (src instanceof IndexConsumer) {
      // state change was caused by an IndexConsumer
      IndexConsumer consumer = (IndexConsumer) src;
      
      // the (indexed) domain class whose objects the consumer is currently indexing
      Class<? extends Indexable> indexedClass = consumer.getIndexedClass();
      
      // update the indexing status of objects of indexedClass based on the application state that 
      // is received
      if (state == AppState.OnFocus) {
        // if consumer is indexing objects of this class then update it 
        setIndexConsumer(indexedClass, consumer);
      } else if (state == AppState.OnClearDomainClassResources) {
        // reset index counter for the source
        resetIndexCounter(indexedClass, consumer);
      }
    }
  }

  private static AppState[] statesOfInterest = {
    AppState.OnFocus, 
    AppState.OnClearDomainClassResources,
  };
  
  @Override
  public AppState[] getStates() {
    return statesOfInterest;
  }
}
