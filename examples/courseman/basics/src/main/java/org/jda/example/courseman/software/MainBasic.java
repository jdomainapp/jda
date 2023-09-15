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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.exceptions.DataSourceException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.impl.DomSoftware;
import jda.software.ddd.MicroDomSoftware;


/**
 * @overview 
 *  Create and run a UI-based {@link DomSoftware} for a pre-defined model.  
 *  
 * @author dmle
 */
public class MainBasic {
  
  private static final Logger logger = LoggerFactory.getLogger(MainBasic.class.getSimpleName());
  
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
  
  private final DomSoftware sw;
  private MicroDomSoftware[] apps;
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    System.out.println(MainBasic.class);
    
    // 1. create software
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // 2. run it
    try {
      new MainBasic(sw).init().reset().run();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }  
  }

  public MainBasic(DomSoftware sw) {
    this.sw = sw;
  }
  
  public MainBasic init() {
    apps = new MicroDomSoftware[2];
    apps[0] = new DomCity(sw);
    apps[1] = new DomStudents(sw);
    
    return this;
  }
  
  /**
   * @effects 
   * 
   * @version 
   * @throws DataSourceException 
   * 
   */
  private MainBasic reset() throws DataSourceException {
    for(MicroDomSoftware app : apps) {
      logger.info("Resetting " + app.getClass().getSimpleName());
      app.reset();
    }
    
    return this;
  }

  /**
   * @effects 
   * 
   * @version 
   * @throws DataSourceException 
   * 
   */
  private MainBasic run() throws DataSourceException {
    for(MicroDomSoftware app : apps) {
      logger.info("Running " + app.getClass().getSimpleName());
      app.run();
    } 
    return this;
  }

}
