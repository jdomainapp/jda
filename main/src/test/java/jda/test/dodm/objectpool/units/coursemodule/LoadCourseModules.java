package jda.test.dodm.objectpool.units.coursemodule;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.CompulsoryModule;
import jda.test.model.basic.ElectiveModule;
import jda.test.model.basic.Module;

public class LoadCourseModules extends DODMEnhancedTester {  
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class[] classes = {
        Module.class,
        CompulsoryModule.class,
        ElectiveModule.class
    };
    
    for (Class c : classes) {
      me.loadObjects(c);
      System.out.printf("Loaded %s%n", c);
    }
  }
}
