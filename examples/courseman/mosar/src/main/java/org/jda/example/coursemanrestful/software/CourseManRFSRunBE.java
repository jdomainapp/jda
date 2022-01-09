package org.jda.example.coursemanrestful.software;

import org.jda.example.coursemanrestful.software.config.SCCCourseManDerbyBackEnd;

import jda.modules.mosar.software.RFSoftware;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManRFSRunBE {
  
  public static void main(String[] args) {
    Class scc = SCCCourseManDerbyBackEnd.class;
    
    new RFSoftware(scc)
      .init()
      .run();
  }
}
