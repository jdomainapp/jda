package jda.modules.tmsa.tasl.conceptmodel.service;


import jda.modules.tasltool.contracts.IApp;
import jda.modules.tasltool.contracts.IGenerator;
import jda.modules.tasltool.utils.NameUtils;
import jda.modules.tmsa.tasl.conceptmodel.service.module.*;
import jda.modules.tmsa.tasl.conceptmodel.service.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service implements IGenerator, IApp {

    // config
    private Model model;
    private String name;
    private String description = "";
    private int port = 3000;

    private String outputPath = "";
    private String outputPackage = "";

    // relations
    private List<Service> services = new ArrayList<>();
    private List<Module> modules = new ArrayList<>();
    private List<EntityModule> entityModules = new ArrayList<>();
    private List<CoordinatorModule> coordinatorModules = new ArrayList<>();
    private List<InterfaceModule> interfaceModules = new ArrayList<>();

    public Service(Model model) {
        this.model = model;
    }

    public void addService(Service service) {
        this.services.add(service);
    }

    public void addModule(Module module) {
        this.modules.add(module);

        if (module instanceof EntityModule) {
            this.entityModules.add((EntityModule) module);
        } else if (module instanceof CoordinatorModule) {
            this.coordinatorModules.add((CoordinatorModule) module);
        } else if (module instanceof InterfaceModule) {
            this.interfaceModules.add((InterfaceModule) module);
        }
    }

    @Override
    public void run() {

    }

    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = IGenerator.super.getData();

        data.put("Name", NameUtils.toUpperCamelCase(this.name));
        data.put("packageFolders", this.outputPackage.replace(".", "/"));

        return data;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
        this.model.setOutputPath(this.outputPath + "/model");
    }

    public void setOutputPackage(String outputPackage) {
        this.outputPackage = outputPackage;
        this.model.setOutputPackage((this.outputPackage + ".model"));
    }

//    ----------------------------

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    public String getOutputPackage() {
        return outputPackage;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", port=" + port +
                ", outputPath='" + outputPath + '\'' +
                ", outputPackage='" + outputPackage + '\'' +
                ", modules=" + modules +
                '}';
    }

//    public String getPackage() {
//        return domain + "." + NameUtils.packageName(id);
//    }

//    public Service(Class mccService) throws NullPointerException, NotFoundException {
//        super(mccService);
//    }

//    public void init() {
//        ModuleDescriptor moduleDescriptor = (ModuleDescriptor) mcc.getAnnotation(ModuleDescriptor.class);
//
//        if (moduleDescriptor == null) {
//            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{ ModuleDescriptor.class, mcc });
//        }
//
//        name = moduleDescriptor.name();
//        domainClass = new DomainClassMeta(moduleDescriptor.modelDesc().model());
//
//        MSCGenDesc serviceDesc = (MSCGenDesc) mcc.getAnnotation(MSCGenDesc.class);
//
//        if (serviceDesc == null) {
//            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{ MSCGenDesc.class, mcc });
//        }
//
//        id = serviceDesc.id();
//        name = serviceDesc.name();
//        port = serviceDesc.port();
//
//        // parse modules
//        if (serviceDesc.serviceTree() != null) {
//            Class root = serviceDesc.serviceTree().root();
//
//            for(CEdge edge : serviceDesc.serviceTree().edges()) {
//                Module parentModule = this;
//
//                if (edge.parent() != root) {
//                    parentModule = getOrCreate(edge.parent());
//                    parentModule.setOutputPath(getOutputFile() + "/src/main/java/" + getPackage().replace(".", "/") + "/modules");
//                    parentModule.setDomain(domain + "." + NameUtils.packageName(id) + ".modules");
//                    parentModule.init();
//                }
//
//                Module childModule = parentModule.getOrCreate(edge.child());
//                this.modules.put(edge.child(), childModule);
//
//                childModule.setOutputPath(getOutputFile() + "/src/main/java/" + getPackage().replace(".", "/") + "/modules");
//                childModule.setDomain(domain + "." + NameUtils.packageName(id) + ".modules");
//                childModule.init();
//            }
//        }
//    }
//
//    public File[] getConfigFiles() {
//        return new File[] {
//            new File(getOutputFile(), "config/"+id+".properties"),
//            new File(getOutputFile(), "config/"+id+"-dev.properties"),
//        };
//    }
//
//    public void run() {
//        // run spring app
//    }
//
//    @Override
//    public void generate() {
//        // copy template folder
//        useTemplateFolder("service");
//
//        // generate modules
//        for (Module module : modules.values()) {
//            module.generate();
//        }
//    }
//
//    @Override
//    public Map<String, Object> getData() {
//        Map<String, Object> data = new HashMap<>();
//
//        data.put("id", id);
//        data.put("ID", NameUtils.toUpperCamelCase(id));
//        data.put("name", name);
//        data.put("port", port);
//        data.put("package", getPackage());
//        data.put("packageFolders", getPackage().replace(".", "/"));
//        data.put("models", getDomainClasses());
//
//        return data;
//    }

//    @Override
//    public File getOutputFile() {
//        return new File(outputPath, id);
//    }


    //    ------------------------

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }

}
