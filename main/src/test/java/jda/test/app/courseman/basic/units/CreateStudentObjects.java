package jda.test.app.courseman.basic.units;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.Student;

public class CreateStudentObjects extends CourseManBasicTester {
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
    
  @Test
  public void doTest() throws DataSourceException { 
    instance.createObjects(Student.class); 
  }  
}
