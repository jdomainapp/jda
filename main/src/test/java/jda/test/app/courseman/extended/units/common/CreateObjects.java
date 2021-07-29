package jda.test.app.courseman.extended.units.common;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.extended.CourseManExtendedTester;


public class CreateObjects extends CourseManExtendedTester {
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
    
  @Test
  public void doTest() throws DataSourceException { 
    //boolean createCityObjects = true;
    ((CourseManExtendedTester) instance).createObjects(); 
  }  
}
