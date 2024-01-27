package jda.modules.tmsa.tasl.conceptmodel;

import jda.modules.tasltool.contracts.IApp;
import jda.modules.tasltool.contracts.IGenerator;
import jda.modules.tasltool.utils.NameUtils;
import jda.modules.tmsa.tasl.conceptmodel.infra.ConfigServer;
import jda.modules.tmsa.tasl.conceptmodel.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App implements IGenerator, IApp {

    // data
    private String name = "";
    private String description = "";
    private String outputPackage = "";
    private String outputPath = "";

    private String modelsPath = "";

    // services
    private List<Service> services = new ArrayList<>();

    // infra
    private ConfigServer configServer;

    public void addService(Service service) {
        this.services.add(service);
    }

    @Override
    public void run() {
        // TODO: run infra

        // run services
        for (Service service : services) {
            service.run();
        }
    }

//    public Service createService()

    //    -----------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    public void setOutputPackage(String outputPackage) {
        this.outputPackage = outputPackage;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public ConfigServer getConfigServer() {
        return configServer;
    }

    public void setConfigServer(ConfigServer configServer) {
        this.configServer = configServer;
    }

    @Override
    public String toString() {
        return "App{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", outputPackage='" + outputPackage + '\'' +
                ", outputPath='" + outputPath + '\'' +
                ", services=" + services +
                ", configServer=" + configServer +
                '}';
    }

}
