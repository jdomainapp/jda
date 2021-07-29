package jda.test.dodm.objectpool.units.coursemodule;

import org.junit.Test;

import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.ElectiveModule;

public class DeleteModulesFromDB extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    // delete objects from db
    Class c = ElectiveModule.class; //CompulsoryModule.class;
    
    me.deleteObjects(c, true);
    System.out.printf("Deleted from db objects of %s%n", c);
    
  }
}
