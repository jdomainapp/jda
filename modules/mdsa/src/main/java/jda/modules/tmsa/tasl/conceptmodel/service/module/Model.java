package jda.modules.tmsa.tasl.conceptmodel.service.module;

import jda.modules.tasltool.contracts.IData;
import jda.modules.tasltool.contracts.IGenerator;
import jda.modules.tasltool.utils.NameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Model implements IGenerator {
    private Class domainClass;

    // derived
    private String name;
    private String outputPackage;
    private String outputPath;

    public Model(Class model) {
        this.domainClass = model;
    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = IGenerator.super.getData();

        data.put("Name", name);
        data.put("name", NameUtils.toLowerCamelCase(name));

        return data;
    }

    @Override
    public void generate(Map<String, Object> data) {
        IGenerator.super.generate(data);

        if (data.containsKey("EntityModule.Service.App.modelsPath")) {
            // copy model
            File from = new File((String) data.get("EntityModule.Service.App.modelsPath"), name + ".java");
            File to = new File(outputPath, name + ".java");

            try {
                String content = FileUtils.readFileToString(from);

                content = content.replace(domainClass.getPackageName(), outputPackage);

                FileUtils.writeStringToFile(to, content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    public void setOutputPackage(String outputPackage) {
        this.outputPackage = outputPackage;
    }

    public Class getDomainClass() {
        return domainClass;
    }

    public void setDomainClass(Class domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", outputPackage='" + outputPackage + '\'' +
                '}';
    }
}
