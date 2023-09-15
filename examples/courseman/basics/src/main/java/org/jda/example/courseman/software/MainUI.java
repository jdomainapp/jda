package org.jda.example.courseman.software;

import org.jda.example.courseman.services.coursemodule.model.CompulsoryModule;
import org.jda.example.courseman.services.coursemodule.model.CourseModule;
import org.jda.example.courseman.services.coursemodule.model.ElectiveModule;
import org.jda.example.courseman.services.enrolment.model.Enrolment;
import org.jda.example.courseman.services.sclass.model.SClass;
import org.jda.example.courseman.services.student.model.City;
import org.jda.example.courseman.services.student.model.Student;
import org.jda.example.courseman.services.student.reports.StudentsByCityJoinReport;
import org.jda.example.courseman.services.student.reports.StudentsByNameReport;

import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;


/**
 * @overview 
 *  Create and run a UI-based {@link DomSoftware} for a pre-defined model.  
 *  
 * @author dmle
 */
public class MainUI {
  
  // 1. initialise the model
  static final Class[] model = {
      CourseModule.class, 
      CompulsoryModule.class, 
      ElectiveModule.class, 
      Enrolment.class, 
      Student.class, 
      City.class, 
      SClass.class,
      // reports
      StudentsByNameReport.class,
      StudentsByCityJoinReport.class
//      EnrolmentByDateReport.class
  };
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    (new MainUI()).run(args);
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public void run(String[] args) {
    System.out.println(MainUI.class);
    
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
