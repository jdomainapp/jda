package jda.test.dodm.objectpool.units;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class GetFirstObject extends DODMEnhancedTester {  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class cls = Student.class;
    Tuple2<Oid,Object> t = me.getFirstObject(cls);
    
    Oid id = t.getFirst();
    Object o = t.getSecond();
    
    System.out.printf("First object of %s: %s -> %s%n", cls, id, o);
  }
}
