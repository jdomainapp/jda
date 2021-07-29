package jda.test.dodm.objectpool.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.City;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;


public class RegisterClass extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws DataSourceException { 
    Class[] classes = new Class[] { //
      City.class,
      SClass.class, // 
      Student.class, //
    };
    
    instance.addClasses(classes); 
  }
}
