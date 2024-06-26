package org.jda.example.courseman.software;

import org.jda.example.courseman.software.config.SCCCourseManDerby;

import jda.modules.mosar.software.RFSoftware;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManMDSRun {
  
  public static void main(String[] args) throws Exception {
    Class scc = SCCCourseManDerby.class;
    
    new RFSoftware(scc)
      .init()
      .run();
  }
}
