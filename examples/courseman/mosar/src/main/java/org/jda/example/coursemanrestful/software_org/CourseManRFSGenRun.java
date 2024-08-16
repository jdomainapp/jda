package org.jda.example.coursemanrestful.software_org;

import jda.modules.mosar.software.RFSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class CourseManRFSGenRun {

    public static void main(String[] args) {
      Class scc = // SCCCourseManDerby.class;
          CourseManRFSGen.getSCCFromEnv();


      new RFSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
