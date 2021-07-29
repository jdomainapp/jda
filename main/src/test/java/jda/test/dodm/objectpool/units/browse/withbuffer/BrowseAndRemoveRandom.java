package jda.test.dodm.objectpool.units.browse.withbuffer;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowseAndRemove;
import jda.test.model.basic.Student;

public class BrowseAndRemoveRandom extends DODMTesterWithBrowseAndRemove {

  @Test
  public void doTest() throws Exception {
    super.doTest(Student.class);
  }
  
  @Override
  public void populateBuffer(Class c) throws Exception {
    // get some random objects
    final int numObjs = 1;
    
    System.out.printf("Trying to get %d objects...%n", numObjs);
    
    Collection<Tuple2<Oid,Object>> objs = getRandomObjects(c, numObjs);
    
    System.out.printf("Populating buffer with %d objects...%n", objs.size());

    for (Tuple2<Oid,Object> t : objs) {
      objects.add(t);
      
      putToBuffer(t.getFirst(), t.getSecond());
    }
  }
  
  public void browseAgain(Class c) throws Exception {
    // browse again
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    if (!dom.isEmptyExtent(c)) {
      browseFirstToLast();
    }
  }
}
