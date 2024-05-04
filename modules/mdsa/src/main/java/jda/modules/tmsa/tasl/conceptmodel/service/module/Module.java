package jda.modules.tmsa.tasl.conceptmodel.service.module;

import jda.modules.tasltool.contracts.IGenerator;
import jda.modules.tasltool.utils.NameUtils;
import jda.modules.tmsa.tasl.conceptmodel.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Module implements IGenerator {
    protected Model model;

    // data
    protected String name = "module";
    protected String outputPath = "";
    protected String outputPackage = "";

    public Module(Model model) {
        this.model = model;
    }

    public void setBaseOutputPath(String basePath) {
        setOutputPath(basePath + NameUtils.toPackageName(getName()));
    }

    public void setBaseOutputPackage(String basePackage) {
        setOutputPackage(basePackage + NameUtils.toPackageName(getName()));
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        this.model.setOutputPath(this.outputPath + "/model");
    }

    public void setOutputPackage(String outputPackage) {
        this.outputPackage = outputPackage;
        this.model.setOutputPackage((this.outputPackage + ".model"));
    }

    //    -----------------------------

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    @Override
    public String toString() {
        return "Module{" +
                "model=" + model +
                ", name='" + name + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", outputPackage='" + outputPackage + '\'' +
                '}';
    }
}
