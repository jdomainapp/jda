package jda.test.dodm.objectpool.units;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;

public class AddNewStudents extends CourseManBasicTester {  
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    CourseManBasicTester me = (CourseManBasicTester)instance;
    
    Collection<City> cities = instance.getData().get(City.class);
    
    Collection<Student> col = me.addStudents(cities);
    
    System.out.println("Added...");
    for (Student s : col) {
      System.out.printf("   %s%n", s);
    }
  }
  
}
