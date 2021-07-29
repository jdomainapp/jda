package jda.test.dodm.objectpool.units;

import org.junit.Test;

import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class DeleteStudentsFromMemory extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class c = Student.class;
    
    // delete objects from memory
    me.deleteObjects(c, false);
    
    System.out.printf("Deleted from memory objects of %s%n", c);
  }
}
