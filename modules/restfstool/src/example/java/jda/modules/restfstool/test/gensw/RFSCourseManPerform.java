package jda.modules.restfstool.test.gensw;

import jda.modules.restfstool.RFSSoftware;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class RFSCourseManPerform {
    
    public static void main(String[] args) {
      Class scc = null; //SCC1.class;

      new RFSSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
