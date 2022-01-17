package org.jda.example.coursemansw.software;

import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;
import org.jda.example.coursemansw.software.config.SCC1;

public class CourseManSoftware {

    public static void main(String[] args) throws Exception {
        final Class SwCfgCls = SCC1.class;
        SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);
        sw.exec(args);
    }
}
