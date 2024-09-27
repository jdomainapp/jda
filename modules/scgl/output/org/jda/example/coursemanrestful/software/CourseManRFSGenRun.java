package org.jda.example.coursemanrestful.software;

import org.courseman.software.config.SCCCourseManDerby;
import org.jda.example.coursemanrestful.software.config.SCCCourseMan;

import jda.modules.mosar.software.RFSoftware;
import org.jda.example.coursemanrestful.utils.DToolkit;

import java.util.Optional;

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
