package jda.test.app.courseman.basic.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;

public class AddAClass extends CourseManBasicTester {

  @Test
  public void doTest() throws DataSourceException {
    boolean create = true;
    boolean read = true;
    
    Class c = City.class;
    instance.addClass(c, create, read);
    
    c = Student.class;
    instance.addClass(c, create, read);
   }
}
