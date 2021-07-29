package jda.test.app.courseman.enhanced.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.enhanced.CourseManEnhancedTester;


public class UpdateObject extends CourseManEnhancedTester {  
  
  @Test
  public void doTest() throws DataSourceException { 
    ((CourseManEnhancedTester)instance).updateStudentObject(); 
  }  
}
