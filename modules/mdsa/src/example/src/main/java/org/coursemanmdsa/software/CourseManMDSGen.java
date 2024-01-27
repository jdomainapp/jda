package org.coursemanmdsa.software;

import jda.modules.tasltool.software.MDSoftware;
import org.coursemanmdsa.software.config.SCCCourseMan;

/**
 * @author Cong Nguyen (congnv)
 * @overview Generate MD software from MCC*.
 */
public class CourseManMDSGen {
    public static void main(String[] args) {
        Class scc = SCCCourseMan.class;

        new MDSoftware(scc)
                .init()
                .generate();
    }
}
