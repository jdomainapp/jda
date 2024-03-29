package org.jda.example.courseman.software;

import org.jda.example.courseman.software.config.SCCCourseManDerbyBackEnd;

import jda.modules.mosar.software.RFSoftware;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManMDSRunBE {
  
  public static void main(String[] args) {
    Class scc = SCCCourseManDerbyBackEnd.class;
    
    new RFSoftware(scc)
      .init()
      .run();
  }
}
