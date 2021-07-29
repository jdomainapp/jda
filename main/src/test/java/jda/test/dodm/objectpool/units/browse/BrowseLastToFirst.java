package jda.test.dodm.objectpool.units.browse;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class BrowseLastToFirst extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    Class c = Student.class;
    Tuple2<Oid,Oid> idRange = me.getOidRange(c);
    Oid minId = idRange.getFirst();
    Oid maxId = idRange.getSecond();
    
    // loop from max to min to get prev
    Oid oid = maxId;
    Object o;
    
    System.out.printf("Current id: %s%n", oid);
    o = me.retrieveObject(c, oid);
    System.out.printf("--> object: %s%n", o);
    do {
      oid = me.getIdFirstBefore(c, oid);
      if (oid != null) {
        System.out.printf("Prev id: %s%n", oid);
        o = me.retrieveObject(c, oid);
        System.out.printf("--> object: %s%n", o);
      }
    } while (oid != null);
    
    System.out.println("No more ids!");
  }
}
