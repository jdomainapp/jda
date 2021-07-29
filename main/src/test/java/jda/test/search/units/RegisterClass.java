package jda.test.search.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.model.basic.City;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;
import jda.test.search.TestSearch;


public class RegisterClass extends TestSearch {  
  
  @Test
  public void doTest() throws DataSourceException { 
    //instance.addClasses(); 
    
    Class[] classes = new Class[] { //
        City.class,
        SClass.class, // 
        Student.class, //
      };
      
    instance.addClasses(classes); 
  }
}
