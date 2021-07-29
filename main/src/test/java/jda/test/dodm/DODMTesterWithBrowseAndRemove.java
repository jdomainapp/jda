package jda.test.dodm;

import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;

import java.util.ArrayList;
import java.util.Collection;

import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.common.types.Tuple2;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A sub-tupe of {@link DODMTesterWithBrowse} that specifically supports the following 
 *  sequence of operations: browse, remove some objects, browse (again).  
 *  
 * @author dmle
 *
 */
public abstract class DODMTesterWithBrowseAndRemove extends 
  //StudentObjectBrowser
  DODMTesterWithBrowse
{

  protected Collection<Tuple2<Oid,Object>> objects;
  
  public DODMTesterWithBrowseAndRemove() {
    objects = new ArrayList<Tuple2<Oid,Object>>();
  }

//  @Test
//  public void doTest() throws Exception {
//    // initialise object buffer
//    initObjectBuffer();
//    
//    // initial browsing sequence
//    BrowsingStep[] browseSeq = {
//        FIRST,
//        LAST,
//    };
//        
//    browse(browseSeq, null);
//    
//    /*TODO: 
//     * - remove a few objects and re-test browsing
//     * - remove the first object and re-test browsing
//     * - remove the last object and re-test browsing
//     */
//    populateBuffer();
//    
//    removeObjects();
//    
//    browseAgain();
//  }
  
  // to be invoked by sub-types only
  protected void doTest(Class c) throws Exception {
    // initialise object buffer
    initObjectBuffer(c);
    
    // initial browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        LAST,
    };
        
    browse(browseSeq, null);
    
    /*TODO: 
     * - remove a few objects and re-test browsing
     * - remove the first object and re-test browsing
     * - remove the last object and re-test browsing
     */
    populateBuffer(c);
    
    removeObjects(c);
    
    browseAgain(c);  
  }
  
  public abstract void populateBuffer(Class c) throws Exception;
  
  public void removeObjects(Class c) throws Exception {
    System.out.println("Removing objects...");
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();    
    Oid id;
    Object o;
    int i = 0;
    for (Tuple2<Oid,Object> t : objects) {
      id = t.getFirst();
      o = t.getSecond();
      
      // remove object
      dom.deleteObject(o, id, c);

      System.out.printf("Removed #%d. %s -> %s%n", i+1, id, o);

      // remove object from buffer
      try {
        removeFromBuffer(id, o);
      } catch (ObsoleteStateSignal s) {
        // no more objects
        System.out.println("No more objects left");
        break;
      }

      i++;
    }
  }
  
  public abstract void browseAgain(Class c) throws Exception;
}
