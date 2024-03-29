package org.jda.example.coursemanmdsa.software;

import org.courseman.software.config.SCCCourseManDerby;
import jda.modules.mosar.software.RFSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class CourseManMDSGen {
    
    public static void main(String[] args) {
      Class scc = SCCCourseManDerby.class;

      new RFSoftware(scc)
        .init()
        .generate()
        ;
    }
}
