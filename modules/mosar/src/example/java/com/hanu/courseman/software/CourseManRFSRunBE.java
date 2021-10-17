package com.hanu.courseman.software;

import com.hanu.courseman.software.config.SCCCourseManDerbyBackEndOnly;

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
    Class scc = SCCCourseManDerbyBackEndOnly.class;
    
    new RFSoftware(scc)
      .init()
      .run();
  }
}
