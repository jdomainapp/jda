package jda.test.dodm.objectpool.units;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class DeleteRandomStudent extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class c = Student.class;
    
    // get objects
    Tuple2<Oid,Object> deleted = me.deleteRandom(c);
    
    Oid oid = deleted.getFirst();
    Object o = deleted.getSecond();
    
    System.out.printf("Deleted: %s -> %s%n", oid, o);
  }
}
