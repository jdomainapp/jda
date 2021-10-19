package org.courseman.software;

import org.courseman.software.config.SCCCourseManDerby;

import jda.modules.mosar.software.RFSoftware;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManRFSRun {
  
  public static void main(String[] args) {
    Class scc = SCCCourseManDerby.class;
    
    new RFSoftware(scc)
      .init()
      .run();
  }
}
