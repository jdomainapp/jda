package jda.test.app.courseman.basic.units.common;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;


public class CreateObjects extends CourseManBasicTester {
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
    
  @Test
  public void doTest() throws DataSourceException { 
    ((CourseManBasicTester) instance).createObjects(true); 
  }  
}
