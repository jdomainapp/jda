package org.jda.example.productsys.software;

import org.jda.example.productsys.software.config.SCC1;

import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;

public class AGCLSoftware {

    public static void main(String[] args) throws Exception {
        final Class SwCfgCls = SCC1.class;
        SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);
        sw.exec(args);
    }
}
