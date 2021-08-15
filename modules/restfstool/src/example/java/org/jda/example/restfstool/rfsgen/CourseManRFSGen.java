package org.jda.example.restfstool.rfsgen;

import com.hanu.courseman.SCCCourseMan;

import jda.modules.restfstool.RFSSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class CourseManRFSGen {
    
    public static void main(String[] args) {
      Class scc = SCCCourseMan.class;

      new RFSSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
