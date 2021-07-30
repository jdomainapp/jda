package org.jda.example.restfstool.rfsgen.v1_0;

import com.hanu.courseman.SCC1;
import com.hanu.courseman.modules.ModuleMain;

import jda.modules.restfstool.frontend.bootstrap.ViewBootstrapper;

/**
 * @author binh_dh
 * Generate the frontend code to a specified package.
 */
public class FrontendGenerator {

    static final String frontendOutputPath = "src/example/java/com/hanu/courseman/frontend";

    public static void setupAndGen() {
        Class sccClass = SCC1.class;

        ViewBootstrapper bootstrapper = new ViewBootstrapper(
                frontendOutputPath, sccClass, ModuleMain.class,
                CourseManAppGenerator.models, CourseManAppGenerator.modules
        );

        bootstrapper.bootstrapAndSave();
    }

    public static void main(String[] args) {
        setupAndGen();
    }
}