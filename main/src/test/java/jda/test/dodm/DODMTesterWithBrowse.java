package jda.test.dodm;

import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.common.types.Tuple2;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.controller.assets.helper.objectbrowser.IdPooledObjectBrowser;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.mosa.model.Oid;

/**
 * @overview
 *  simulate the behaviour of the object browser: randomly browse for objects and 
 *  cache them into a {@see IdObjectBuffer}). 
 *  
 * @author dmle
 */
public class DODMTesterWithBrowse<T> extends DODMEnhancedTester {  
  
  //@Deprecated()
  //private static Class c;
  
  protected ObjectBrowser<T> browser;
  
  public enum BrowsingStep {
    FIRST, NEXT, PREV, LAST
  }

  /**
   * @effects
   *  initialise an object buffer with min, max Oids
   */
  public void initObjectBuffer(Class<T> c) throws DataSourceException {
    System.out.println("Initialising object buffer");
//    TestObjectBrowser.c = c;
//    initObjectBuffer();
    
    System.out.println("Initialising object buffer");
    DODMEnhancedTester me = (DODMEnhancedTester) instance;
    Tuple2<Oid,Oid> idRange = me.getOidRange(c);
    Oid minId = idRange.getFirst();
    Oid maxId = idRange.getSecond();
    
    // init an object buffer
    browser = new ObjectBrowser<>(instance.getDODM(), c);
    browser.open(minId, maxId);
  }
  
//  /**
//   * @effects
//   *  initialise an object buffer with min, max Oids
//   */
//  public void initObjectBuffer() throws DBException {
//    System.out.println("Initialising object buffer");
//    TestDBMainObjectPool me = (TestDBMainObjectPool) instance;
//    Tuple2<Oid,Oid> idRange = me.getOidRange(c);
//    Oid minId = idRange.getFirst();
//    Oid maxId = idRange.getSecond();
//    
//    // init an object buffer
//    browser = new ObjectBrowser(instance.getDomainSchema(), c);
//    browser.open(minId, maxId);
//  }
  
  /**
   * @effects
   *  initialise an object buffer with a collection of Oids and min, max Oids being 
   *  the first and last elements of the collection
   *
   *  <p>memOnly means that the browser only operates on the Oids given to it and 
   *  does not need to load additional Ids from the data source.
   */
  public void initObjectBuffer(Class<T> c, Collection<Oid> oids, Oid minId, Oid maxId, boolean memOnly) throws DataSourceException {
    System.out.printf("Initialising object buffer with pre-defined Oids%n");
    
    // init an object buffer
    if (memOnly) {
      // memory based browser
      browser = new IdPooledObjectBrowser<>(instance.getDODM(), c);
    } else {
      browser = new ObjectBrowser<>(instance.getDODM(), c);
    }
    browser.open(oids, minId, maxId);    
  }

  /**
   * @effects
   *  initialise an object buffer with a collection of Oids and min, max Oids being 
   *  the first and last elements of the collection
   */
  public void initObjectBuffer(Class<T> c, Collection<Oid> oids) throws DataSourceException {
//    Iterator<Oid> it = oids.iterator();
//    Oid minId = it.next();  // first item
//    Oid maxId = minId; // last item
//    while (it.hasNext()) {
//      maxId = it.next();
//    }
//    
//    initObjectBuffer(c, oids, minId, maxId);
    initObjectBuffer(c, oids, false);
  }
  
  /**
   * @effects
   *  initialise an object buffer with a collection of Oids and min, max Oids being 
   *  the first and last elements of the collection.
   *  
   *  <p>memOnly means that the browser only operates on the Oids given to it and 
   *  does not need to load additional Ids from the data source.
   */
  public void initObjectBuffer(Class<T> c, Collection<Oid> oids, boolean memOnly) throws DataSourceException {
    Iterator<Oid> it = oids.iterator();
    Oid minId = it.next();  // first item
    Oid maxId = minId; // last item
    while (it.hasNext()) {
      maxId = it.next();
    }
    
    initObjectBuffer(c, oids, minId, maxId, memOnly);
  }
  
  /**
   * @effects 
   *  add an object o to buffer
   */
  public void addToBuffer(Oid id, T o) {
    browser.add(id, o);
  }

  /**
   * @effects 
   *  put an object o to buffer
   */
  public void putToBuffer(Oid id, T o) {
    browser.put(id, o);
  }
  
  /**
   * @effects 
   *  remove an object o from buffer
   */
  public boolean removeFromBuffer(Oid id, T o) throws DataSourceException, NotFoundException, ObsoleteStateSignal {
//    boolean removedAndBrowsed = browser.remove(randId, rand);
//    return removedAndBrowsed;
    
    moveTo(id, o);
    
    System.out.printf("Browsed to object: %n  %s%n  %s%n", id, o);
    
    // (4) Remove the object
    T curr; 
    try {
      boolean removedAndBrowsed = browser.remove(id, o);

      assert (!browser.contains(id)) : "Object not removed correctly: " + o; 
      
      curr = getCurrentObject();
      assert (curr != o) : "Browser not browsed away from the removed object correctly"; 
  
      System.out.printf("Removed from browser%n");
      
      System.out.printf("Current object (after removal): %s%n", curr);
      
      if (removedAndBrowsed) {
        // (5) Browse some (2)
        System.out.println("Browse some:");

        BrowsingStep[] seq = {
            NEXT
        };
        browse(seq, null);

        curr = getCurrentObject();
        assert (curr != o) : "Object not removed correctly: " + o; 
      }
      
      return removedAndBrowsed;
      
    } catch (ObsoleteStateSignal s) {
      assert (!browser.contains(id)) : "Object not removed correctly: " + o; 
      
      curr = getCurrentObject();
      assert (curr != o) : "Browser not browsed away from the removed object correctly"; 
  
      System.out.printf("Removed from browser%n");
      
      System.out.printf("Current object (after removal): %s%n", curr);
      
      throw s;
    } 
  }

  public T getCurrentObject() {
    return browser.getCurrentObject();
  }

  public Oid getCurrentObjectId() {
    return browser.getCurrentOid();
  }

  public boolean contains(Oid id) {
    return browser.contains(id);
  }

  public void browseFirstToLast() throws Exception {
    method("browseFirstToLast()");
    
    browser.first();
    printBrowserState();
    
    while(browser.hasNext()) {
      try {
        browser.next();
        printBrowserState();
      } catch (NotFoundException e) {
        // no more objects
        System.out.println("END!");
        break;
      }
    }
  }

  /**
   * @version 3.2
   */
  public void browseLastToFirst() throws Exception {
    method("browseLastToFirst()");
    
    browser.last();
    printBrowserState();
    
    while(browser.hasPrevious()) {
      try {
        browser.prev();
        printBrowserState();
      } catch (NotFoundException e) {
        // no more objects
        System.out.println("END!");
        break;
      }
    }
  }
  
  /**
   * @effects 
   *  browse to the first object (if any) and return it.
   */
  public T browseFirst() throws NotFoundException, DataSourceException {
    if (browser != null && browser.isOpened()) {
      browser.first();
      return (T) browser.getCurrentObject();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  invoke {@link #browseFirstToLast()} on an object of this for c and oids
   */
  public static <T> void browseFirstToLast(Class<T> c, Collection<Oid> oids) throws Exception {
    //TestObjectBrowser.c = c;
    
    DODMTesterWithBrowse<T> browser = new DODMTesterWithBrowse<>();
    browser.initObjectBuffer(c, oids);
    
    browser.browseFirstToLast();
  }

  /**
   * @requires 
   *  cls is registered
   * @effects 
   *  invoke {@link #browseFirstToLast()} on an object of this for c and with <b>all</b> Oids loaded
   *  before browsing.
   */
  public static <T> void browseFirstToLast(DODMBasic schema, Class<T> cls) throws Exception {
    /* v3.2
    // get Id collection
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Collection<Oid> oids = dom.retrieveObjectOids(cls, null);
    if (oids == null) {
      System.err.printf("No %s objects found%n", cls.getSimpleName());
    } else {
      // initialise object buffer
      DODMTesterWithBrowse.browseFirstToLast(cls, oids);
    } */
    browseFirstToLast(schema, cls, true);
  }

  /**
   * @requires 
   *  cls is registered
   * @effects 
   *  invoke {@link #browseFirstToLast()} on an object of this for c and 
   *  if <tt>loadOids = true</tt> then with all Oids loaded before browsing
   */
  public static <T> void browseFirstToLast(DODMBasic schema, Class<T> cls, boolean loadOids) throws Exception {
    if (loadOids) {
      // browse with all Oids loaded
      // get Id collection
      DSMBasic dsm = instance.getDsm();
      DOMBasic dom = instance.getDom();
      
      Collection<Oid> oids = dom.retrieveObjectOids(cls, null);
      if (oids == null) {
        System.err.printf("No %s objects found%n", cls.getSimpleName());
      } else {
        // initialise object buffer
        DODMTesterWithBrowse.browseFirstToLast(cls, oids);
      }
    } else {
      // browse with only id-range loaded
      DODMTesterWithBrowse<T> tob = new DODMTesterWithBrowse<>(); 
      tob.initObjectBuffer(cls);
      
      tob.browseFirstToLast();
    }
  }

  /**
   * @requires 
   *  cls is registered
   * @effects 
   *  load the Oids of <tt>cls</tt> and browse using them and a pre-defined sequence of 
   *  browsing steps.
   */
  public static <T> void browseRandom(DODMBasic schema, Class<T> cls) throws Exception { 
    /* v3.2
    System.out.println(DODMTesterWithBrowse.class.getSimpleName()+".browseRandom()");
    
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    // get Id collection
    Collection<Oid> oids = dom.retrieveObjectOids(cls, null);
    if (oids == null) {
      System.err.printf("No %s objects found%n", cls.getSimpleName());
    }
    
    // initialise object buffer
    DODMTesterWithBrowse<T> tob = new DODMTesterWithBrowse<>(); 
    tob.initObjectBuffer(cls, oids);
    
    // set up a browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        NEXT,PREV,NEXT,
        NEXT,
        NEXT,PREV,PREV,
        LAST,
        PREV,
        PREV, NEXT, NEXT, 
        //NEXT // -- NotFoundException
    };
    
    Boolean[] expected = null; 
//      {
//        FALSE, 
//        FALSE, TRUE, TRUE,
//        FALSE, 
//        FALSE, TRUE, TRUE,
//        FALSE,
//        TRUE,
//        TRUE, TRUE, TRUE
//    };
        
    tob.browse(browseSeq, expected);*/
    browseRandom(schema, cls, true);
  }
  
  /**
   * @requires 
   *  cls is registered
   * @effects 
   *  browse objects of <tt>cls</tt> using a pre-defined sequence of 
   *  browsing steps.
   *  <p>If <tt>loadOids = true</tt> then load <b>all</b> Oids of <tt>cls</tt> for browsing
   *  
   * @version 3.2
   */
  public static <T> void browseRandom(DODMBasic schema, Class<T> cls, boolean loadOids) throws Exception { 
    System.out.println(DODMTesterWithBrowse.class.getSimpleName()+".browseRandom()");
    
    DODMTesterWithBrowse<T> tob = new DODMTesterWithBrowse<>(); 
    if (loadOids) {
      // browse with all Oids
      DSMBasic dsm = instance.getDsm();
      DOMBasic dom = instance.getDom();
      
      // get Id collection
      Collection<Oid> oids = dom.retrieveObjectOids(cls, null);
      if (oids == null) {
        System.err.printf("No %s objects found%n", cls.getSimpleName());
        return;
      }
      
      // initialise object buffer
      tob.initObjectBuffer(cls, oids);
    } else {
      // browse with just the id range
      tob.initObjectBuffer(cls);
    }
    
    // set up a browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        NEXT,PREV,NEXT,
        NEXT,
        NEXT,PREV,PREV,
        LAST,
        PREV,
        PREV, NEXT, NEXT, 
        //NEXT // -- NotFoundException
    };
    
    Boolean[] expected = null; 
//      {
//        FALSE, 
//        FALSE, TRUE, TRUE,
//        FALSE, 
//        FALSE, TRUE, TRUE,
//        FALSE,
//        TRUE,
//        TRUE, TRUE, TRUE
//    };
        
    tob.browse(browseSeq, expected);
  }
  
  /**
   * @effects <pre>
   *  performs browsing sequence <tt>browsSeq</tt> on this
   *  if expected != null
   *    validate the result of the browsing sequence against expected</pre>
   */
  public void browse(BrowsingStep[] browseSeq, Boolean[] expected) throws DataSourceException {
    // process the browsing sequence
    // print out 
    System.out.printf("Browsing sequence:%n  %s%n", Arrays.toString(browseSeq));
    
    Boolean[] actual = new Boolean[browseSeq.length];
    
    boolean result;
    int i = 0;
    for (BrowsingStep step : browseSeq) {
      if (step == FIRST) {
        result = browser.first();
        printBrowserState();
        actual[i] = result;
      } else if (step == NEXT) {
        if (browser.hasNext()) {
          result = browser.next();
          printBrowserState();
          actual[i] = result;
        } else {
          System.out.printf("NEXT: No more objects%n");
          actual[i] = false;
        }
      } else if (step == PREV) {
        if (browser.hasPrevious()) {
          result = browser.prev();
          printBrowserState();
          actual[i] = result;
        } else {
          System.out.printf("PREV: No more objects%n");
          actual[i] = false;
        }
      } else if (step == LAST) {
        result = browser.last();
        printBrowserState();
        actual[i] = result;
      }
      i++;
    }
    
    // compare result
    if (expected != null)
      Assert.assertArrayEquals(actual, expected);
    
    //System.out.println("SUCCESS!");
  }

  /**
   * @effects 
   *  invoke {@link #browse(BrowsingStep[], Boolean[])} on an object of this for c and oids
   */
  public static void browse(Class c, Collection<Oid> oids, 
      BrowsingStep[] browseSeq, Boolean[] expected) throws Exception {
    //TestObjectBrowser.c = c;
    
    DODMTesterWithBrowse browser = new DODMTesterWithBrowse();
    browser.initObjectBuffer(c, oids);
    
    browser.browse(browseSeq, expected);
  }

  /**
   * @effects 
   *  perform <tt>steps</tt> on the domain objects of <tt>c</tt> that had been registered in <tt>schema</tt>
   * @version 3.2 
   */
  public static <T> DODMTesterWithBrowse<T> browse(DODMBasic schema, Class<T> c,
      BrowsingStep[] steps, boolean loadOids) throws Exception {
    DODMTesterWithBrowse<T> browser = new DODMTesterWithBrowse();
    
    if (loadOids) {
      // browse with all Oids
      DSMBasic dsm = instance.getDsm();
      DOMBasic dom = instance.getDom();
      
      // get Id collection
      Collection<Oid> oids = dom.retrieveObjectOids(c, null);
      if (oids == null) {
        System.err.printf("No %s objects found%n", c.getSimpleName());
        throw new NotFoundException(NotFoundException.Code.OBJECT_ID_RANGE_NOT_FOUND, 
            new Object[] {c.getSimpleName()});
      }
      
      // initialise object buffer
      browser.initObjectBuffer(c, oids);
    } else {
      browser.initObjectBuffer(c);
    }
    
    
    browser.browse(steps, null);
    
    return browser;
  }
  
  /**
   * @effects 
   *  browse a few steps in this to show some objects
   *  add newObject to the schema
   *  add newObject to this
   *  browse again
   */
  public void browseAndAdd(Class c, T newObject) throws Exception {
    System.out.printf("browseAndAdd(%s)%n", newObject);
    
    // initialise object buffer
    initObjectBuffer(c);
    
    // an initial browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        LAST,
    };
    
    Boolean[] expected = null;

    browse(browseSeq, expected);
    
    /*  
     * add a few new objects and re-test browsing
     */
    // create object
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Oid id = dom.addObject(newObject);
    
    System.out.printf("Added %s -> %s%n", id, newObject);
    
    // add object to buffer
    addToBuffer(id,newObject);
    
    // browse again just to check the new object
    browseSeq = new BrowsingStep[] {
      LAST, // the new object 
      PREV,
      NEXT,
      PREV,
      PREV,
      PREV 
    };

    expected = null; 
//        new Boolean[] {
//        TRUE,
//        TRUE,
//        TRUE,
//        TRUE,
//        FALSE,
//        FALSE
//    };
    
    browse(browseSeq, expected);  
  }
  
  private void printBrowserState() {
    if (browser != null) {
      Oid id = browser.getCurrentOid();
      Object o = browser.getCurrentObject();
      
      System.out.printf("  %s -> %s%n", id, o);
    }
      
  }

  public void moveTo(Oid id, T o) {
    if (browser != null) {
      browser.move(id, o);
    }
  }

  /**
   * @effects 
   *  if browser != null
   *    return browser.size
   *  else
   *    return -1
   * @version 3.2
   */
  public int getObjectBufferSize() {
    if (browser != null) {
      return browser.size();
    } else {
      return -1;
    }
  }
  
//  private boolean first() throws DBException, NotFoundException {
//    System.out.println(FIRST);
//    
//    TestDBMainObjectPool me = (TestDBMainObjectPool) instance;
//
//    boolean inBuffer = false;
//    
//    currId = buffer.firstId();
//    Object o = buffer.get(currId);
//    if (o == null) {
//      System.out.println("  Loading object from data source...");
//      o = me.getObject(c, currId);
//      
//      // put object into buffer
//      buffer.put(currId, o);
//    } else {
//      inBuffer = true;
//      //System.out.println("  Object in buffer...");
//    }
//    
//    System.out.printf("  %s -> %s%n",currId, o);
//    
//    return inBuffer;
//  }
//  
//  private boolean last() throws DBException, NotFoundException {
//    System.out.println(LAST);
//    TestDBMainObjectPool me = (TestDBMainObjectPool) instance;
//
//    boolean inBuffer = false;
//
//    currId = buffer.lastId();
//    Object o = buffer.get(currId);
//    if (o == null) {
//      System.out.println("  Loading object from data source...");
//      o = me.getObject(c, currId);
//
//      // put object into buffer
//      buffer.put(currId, o);
//    } else {
//      inBuffer = true;
//      //System.out.println("  Object in buffer...");
//    }
//    
//    System.out.printf("  %s -> %s%n",currId, o);
//    
//    return inBuffer;
//  }
//  
//  // return TRUE only if both previous id and object are in buffer
//  private boolean prev() throws DBException, NotFoundException, NotPossibleException {
//    System.out.println(PREV);
//
//    TestDBMainObjectPool me = (TestDBMainObjectPool) instance;
//
//    boolean inBuffer = false;
//
//    Oid prevId;
//    Object o;
//    if (currId == null) {
//      System.err.println("  Cannot move PREV: no current object");
//    } else {
//      // try buffer
//      
//      // look up previous Id
//      prevId = buffer.previousId(currId);
//      if (prevId == null) {
//        // object ID not in buffer, load from data source
//        System.out.printf("  Loading previous object Id from data source...%n");
//        prevId = me.getIdFirstBefore(c, currId);
//
//        if (prevId == null || buffer.isIdOutOfBound(prevId)) {
//          // no more ids, cannot move next
//          prevId = null;
//          System.out.printf("No more ids%n");
//        } else {  
//          // store in buffer
//          buffer.putIdSequence(prevId, currId);
//        }
//
//        inBuffer = false;
//      } else {
//        inBuffer = true;
//      }
//
//      if (prevId != null) {
//        // look up object
//        o = buffer.get(prevId);
//        if (o == null) {
//          // object not in buffer, load from data source
//          System.out.printf("  Loading previous object from data source...%n");          
//          o = me.getObject(c, prevId);
//          buffer.put(prevId, o);
//          
//          inBuffer = false;
//        } else {
//          inBuffer = inBuffer & true;
//  
//          // object already in buffer, get it
//          // System.out.printf("  Previous object is in buffer...%n");
//        }
//        
//        System.out.printf("  %s -> %s%n",prevId, o);
//        
//        // update id
//        currId = prevId;
//      }
//    }
//    
//    return inBuffer;
//  }
//
//  // return TRUE only if both next id and object are in buffer
//  private boolean next()  throws DBException, NotFoundException, NotPossibleException {
//    System.out.println(NEXT);
//
//    TestDBMainObjectPool me = (TestDBMainObjectPool) instance;
//
//    boolean inBuffer = false;
//
//    Oid nextId;
//    Object o;
//    if (currId == null) {
//      System.err.println("  Cannot move NEXT: no current object");
//    } else {
//      // try buffer
//      
//      // look up next id
//      nextId = buffer.nextId(currId);
//      if (nextId == null) {
//        // object Id not in buffer or no more object ids, try loading from data source
//        System.out.printf("  Loading next object Id from data source...%n");
//        nextId = me.getIdFirstAfter(c, currId);
//        
//        if (nextId == null || buffer.isIdOutOfBound(nextId)) {
//          // no more ids, cannot move next
//          nextId = null;
//          System.out.printf("No more ids%n");
//        } else {  
//          // store in buffer
//          buffer.putIdSequence(currId, nextId);
//        }
//        inBuffer = false;
//      } else {
//        inBuffer = true;
//      }
//
//      if (nextId != null) {
//        // look up next object
//        o = buffer.get(nextId);
//        
//        if (o == null) {
//          // object not in buffer, load from data source
//          System.out.printf("  Loading next object from data source...%n");
//          o = me.getObject(c, nextId);
//          
//          buffer.put(nextId, o);
//          
//          inBuffer = false;
//        } else {
//          inBuffer = inBuffer & true;
//          
//          // System.out.printf("  Next object is in buffer...%n");
//        }
//        
//        System.out.printf("  %s -> %s%n",nextId, o);
//  
//        // update id
//        currId = nextId;
//      }
//    }    
//    
//    return inBuffer;
//  }
}
