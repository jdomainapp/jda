package jda.test.dodm.objectpool.units;

import org.junit.Test;

import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class DeleteStudentsFromDB extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class c = Student.class;
    
    // delete objects from db
    me.deleteObjects(c, true);
    
    System.out.printf("Deleted from db objects of %s%n", c);
  }
}
