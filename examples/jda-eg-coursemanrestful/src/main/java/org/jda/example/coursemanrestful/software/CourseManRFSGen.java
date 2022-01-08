package org.jda.example.coursemanrestful.software;

import org.jda.example.coursemanrestful.software.config.SCCCourseManDerby;

import jda.modules.mosar.software.RFSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class CourseManRFSGen {
    
    public static void main(String[] args) {
      Class scc = SCCCourseManDerby.class;

      new RFSoftware(scc)
        .init()
        .generate()
        ;
    }
}
