package org.jda.example.coursemanswref.software;

import jda.modules.common.exceptions.DataSourceException;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;
import jda.mosa.software.impl.DomSoftware;
import org.jda.example.coursemanswref.software.config.SCCCourseManDerby;

public class CourseManSoftwareRef {

    public static void main(String[] args) throws Exception {
        final Class SwCfgCls =
            // using embedded JavaDB
            SCCCourseManDerby.class
            // using PostgreSQL
//            SCCCourseMan.class
            ;
        SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);

        if (args.length == 0)
            args = new String[] {"run"};

        sw.exec(args);
    }

    private static void deleteClass(DomSoftware sw, Class c) throws DataSourceException {
        boolean isReg = sw.isRegistered(c);
        boolean isMat = sw.isMaterialised(c);
        System.out.printf("%s%n  isRegistered: %b%n  isMaterialised: %b%n",
            c.getSimpleName(), isReg, isMat);
        if (isMat) {
            Class[] toDelete = {c};
            System.out.printf("...unregistering/deleting%n");
            sw.deleteDomainModel(toDelete);
            isReg = sw.isRegistered(c);
            isMat = sw.isMaterialised(c);
            System.out.printf("  isRegistered: %b%n  isMaterialised: %b%n", isReg, isMat);
        }
    }
}
