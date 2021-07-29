package jda.test.app.courseman.extended.units;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Enrolment;


public class DeleteEnrolments extends CourseManExtendedTester {
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
    
  @Test
  public void doTest() throws DataSourceException { 
    instance.deleteObjects(Enrolment.class, true); 
  }  
}
