package jda.test.dodm.objectpool.units;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.SClass;

public class LoadSClass extends DODMEnhancedTester {  
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class c = SClass.class;
    me.loadObjects(c);
    
    System.out.printf("Loaded %s%n", c);
  }
}
