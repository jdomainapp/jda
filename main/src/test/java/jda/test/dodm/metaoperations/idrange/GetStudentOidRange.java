package jda.test.dodm.metaoperations.idrange;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class GetStudentOidRange extends DODMEnhancedTester {  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class cls = Student.class;
    
    // register class
    instance.registerClass(cls);
    
    // do task
    Tuple2<Oid,Oid> idRange = me.getOidRange(cls);
    Oid min = idRange.getFirst();
    Oid max = idRange.getSecond();
    
    System.out.printf("ID range of %s: [%s,%s]%n", cls, min, max);
  }
}
