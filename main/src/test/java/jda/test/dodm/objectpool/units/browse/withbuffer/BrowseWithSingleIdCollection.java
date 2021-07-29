package jda.test.dodm.objectpool.units.browse.withbuffer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.Student;

public class BrowseWithSingleIdCollection extends DODMTesterWithBrowse {

  @Test
  public void doTest() throws Exception { 

    DODMEnhancedTester me = (DODMEnhancedTester) instance;
    
    Class<Student> c = Student.class;
    
    // get Id collection
    Tuple2<Oid,Student> t =
        me.getRandomObjects(c, 1).iterator().next();
        //me.getFirstObject(c);
    Collection<Oid> oids = new ArrayList();
    
    oids.add(t.getFirst());
    
    // initialise object buffer
    initObjectBuffer(c, oids);
    
    // set up a browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        LAST,FIRST,
        NEXT,
        PREV,
        LAST,
        PREV
    };
    
    Boolean[] expected = {
        FALSE, 
        TRUE, TRUE, 
        FALSE, 
        FALSE, 
        TRUE, 
        FALSE
    };
        
    browse(browseSeq, expected);
  }

}
