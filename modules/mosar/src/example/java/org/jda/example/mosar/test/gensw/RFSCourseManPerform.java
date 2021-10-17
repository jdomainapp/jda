package org.jda.example.mosar.test.gensw;

import jda.modules.mosar.software.RFSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class RFSCourseManPerform {
    
    public static void main(String[] args) {
      Class scc = null; //SCC1.class;

      new RFSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
