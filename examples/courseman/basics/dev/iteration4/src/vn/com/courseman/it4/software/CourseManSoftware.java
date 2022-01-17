package vn.com.courseman.it4.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.courseman.it4.model.City;
import vn.com.courseman.it4.model.CompulsoryModule;
import vn.com.courseman.it4.model.CourseModule;
import vn.com.courseman.it4.model.ElectiveModule;
import vn.com.courseman.it4.model.Enrolment;
import vn.com.courseman.it4.model.SClass;
import vn.com.courseman.it4.model.Student;

/**
 * @overview 
 *  Encapsulate the basic functions for setting up and running a software given its domain model.  
 *  
 * @author dmle
 *
 * @version 
 */
public class CourseManSoftware {
  
  // the domain model of software
  private static final Class[] model = {
      CourseModule.class, 
      CompulsoryModule.class, 
      ElectiveModule.class, 
      Enrolment.class, 
      Student.class, 
      City.class, 
      SClass.class,
  };
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    // 2. create UI software
    DomSoftware sw = SoftwareFactory.createUIDomSoftware();
    
    // 3. run
    // create in memory configuration
    System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
    
    // 3. run it
    try {
      sw.run(model);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
  }
}
