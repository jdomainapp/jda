package org.coursemanmdsa.software;

import jda.modules.tasltool.software.MDSoftware;
import org.coursemanmdsa.software.config.SCCCourseMan;

/**
 * @author congnv
 * @overview Execute the TASL software from the generated components.
 */
public class CourseManMDSRun {

    public static void main(String[] args) {
        Class scc = SCCCourseMan.class;

        new MDSoftware(scc)
                .init()
                .run();
    }
}
