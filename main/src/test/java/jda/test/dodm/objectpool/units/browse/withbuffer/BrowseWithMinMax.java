package jda.test.dodm.objectpool.units.browse.withbuffer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import org.junit.Test;

import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.Student;

public class BrowseWithMinMax extends DODMTesterWithBrowse {

  @Test
  public void doTest() throws Exception { 
    
    // initialise object buffer
    initObjectBuffer(Student.class);
    
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
    
    Boolean[] expected = {
        FALSE, 
        FALSE, TRUE, TRUE,
        FALSE, 
        FALSE, TRUE, TRUE,
        FALSE,
        FALSE,
        FALSE, TRUE, TRUE
    };
        
    browse(browseSeq, expected);
  }

}
