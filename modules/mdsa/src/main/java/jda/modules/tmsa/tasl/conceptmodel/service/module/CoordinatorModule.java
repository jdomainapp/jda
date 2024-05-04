package jda.modules.tmsa.tasl.conceptmodel.service.module;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorModule extends Module {
    private List<Module> modules = new ArrayList<>();
    public CoordinatorModule(Model model) {
        super(model);
    }
    public void addModule(Module module) {
        this.modules.add(module);
    }

    public void setBaseOutputPath(String outputPath) {
        super.setBaseOutputPath(outputPath);

        for(Module module : modules) {
            module.setBaseOutputPath(outputPath);
        }
    }

    public void setBaseOutputPackage(String outputPackage) {
        super.setBaseOutputPackage(outputPackage);

        for(Module module : modules) {
            module.setBaseOutputPackage(outputPackage);
        }
    }
}
