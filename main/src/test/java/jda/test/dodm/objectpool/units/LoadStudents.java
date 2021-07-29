package jda.test.dodm.objectpool.units;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class LoadStudents extends DODMEnhancedTester {  
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class c = Student.class;
    me.loadObjects(c);
    
    System.out.printf("Loaded %s%n", c);
  }
}
