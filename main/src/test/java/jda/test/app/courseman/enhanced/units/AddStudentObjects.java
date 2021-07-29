package jda.test.app.courseman.enhanced.units;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.test.app.courseman.enhanced.CourseManEnhancedTester;
import jda.test.model.basic.City;
import jda.test.model.enhanced.Instructor;
import jda.test.model.enhanced.Student;

public class AddStudentObjects extends CourseManEnhancedTester {
  
  @Test
  public void doTest() throws DataSourceException {
    method("doTest()");
    
    
    CourseManEnhancedTester inst = (CourseManEnhancedTester) instance;
    
    // register classes and load their data
    inst.addClasses();
    inst.loadObjects();
    
    DODMBasic schema = inst.getDODM();

    // initialise some new students 
    Collection<City> cities = inst.getData().get(City.class);
    
    // get the pre-defined instructors 
    Collection<Instructor> instructors = getObjects(Instructor.class);
    
    // create students (with supervisors)
    Collection<Student> students = addStudents(cities, instructors);
    
    // add student objects to database
    for (Student s : students) {
      System.out.println("Saving student " + s);
      addObject(s);//schema.addObject(s);
    }
    
    // update instructor objects
//    for (Instructor ins : instructors) {
//      System.out.println("Updating instructor " + ins);
//      schema.updateObject(ins, null);
//    }
        
    // print data
//    printDataMemory();
//    
//    printDataDB();
  }
}
