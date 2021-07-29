package jda.test.app.courseman.enhanced.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.enhanced.CourseManEnhancedTester;

public class UpdateStudentObject extends CourseManEnhancedTester {
  @Test
  public void doTest() throws DataSourceException {
    method("doTest()");
    
    
    CourseManEnhancedTester inst = (CourseManEnhancedTester) instance;
    
    // register classes and load their data
    inst.addClasses();
    inst.loadObjects();
    
    // update a student 
    inst.updateStudent();
    
    // print data
    printDataMemory();
    
    printDataDB();
  }
}
