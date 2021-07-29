package jda.test.dodm.objectpool.units.browse.withbuffer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowseAndRemove;
import jda.test.model.basic.Student;

public class BrowseAndRemoveFirst extends DODMTesterWithBrowseAndRemove {

  @Test
  public void doTest() throws Exception {
    super.doTest(Student.class);
  }
  
  @Override
  public void populateBuffer(Class c) throws Exception {
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    // first object 
    Tuple2<Oid,Object> t = getFirstObject(c);
    
    objects.add(t);
    
    // add to buffer
    putToBuffer(t.getFirst(), t.getSecond());
  }
  
  @Override
  public void browseAgain(Class c) throws Exception {
    // browse again
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    if (!dom.isEmptyExtent(c)) {
      browse(new BrowsingStep[] {
         FIRST,
         NEXT,
         NEXT,
         FIRST,
         LAST
      }, new Boolean[] {
          FALSE,
          FALSE,
          FALSE,
          TRUE,
          TRUE
      });
    }
  }
}
