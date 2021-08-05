package org.jda.example.restfstool.rfsgen;

import com.hanu.courseman.SCCCourseMan;

import jda.modules.restfstool.RFSSoftware;

/**
 * @overview 
 *  Execute the Back end software from the generated components. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManBESoftware {
  
  public static void main(String[] args) {
    Class scc = SCCCourseMan.class;
    new RFSSoftware(scc)
      .init()
      .run();
  }
}
