package jda.modules.tasltool.software;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mosar.software.RFSoftware;
import jda.modules.tasltool.utils.DataBinder;
import jda.modules.tasltool.utils.NameUtils;
import jda.modules.tmsa.tasl.conceptmodel.App;
import jda.modules.tmsa.tasl.conceptmodel.service.Service;
import jda.modules.tmsa.tasl.conceptmodel.service.module.Module;
import jda.modules.tmsa.tasl.conceptmodel.service.module.*;
import jda.modules.tmsa.tasl.syntax.MDSGenDesc;
import jda.modules.tmsa.tasl.syntax.ServiceDesc;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Cong Nguyen (congnv)
 * @overview Automatically generates an MD software with services.
 */
public class MDSoftware extends RFSoftware {
    private App app;

    private Class<?> scc;

    private static Logger logger = (Logger) LoggerFactory.getLogger("module.mdstool." + MDSoftware.class.getSimpleName());

    static {
        logger.setLevel(Level.INFO);
    }

    /**
     * @effects Initialises this with a configuration defined in <code>scc</code>
     */
    public MDSoftware(Class<?> scc) {
        super(scc);

        this.scc = scc;
    }

    /**
     * @effects initialises resources necessary for the software
     */
    public MDSoftware init() {
        logger.info("Initiating...");

        fromMccToConceptual();

        return this;
    }

    private void fromMccToConceptual() {
        if (scc == null) {
            throw new NullPointerException("MDSoftware.init(): invalid scc");
        }

        MDSGenDesc mdsGenDesc = (MDSGenDesc) scc.getAnnotation(MDSGenDesc.class);

        if (mdsGenDesc == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{MDSGenDesc.class, scc});
        }

        app = new App();
        DataBinder.bindAnnotation(app, mdsGenDesc);

        // services
        for (Class mccService : mdsGenDesc.mccServices()) {
            Service service = createService(mccService);
            app.addService(service);
        }
    }

    private Service createService(Class mccService) {
        ServiceDesc serviceDesc = (ServiceDesc) mccService.getAnnotation(ServiceDesc.class);

        if (serviceDesc == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{ServiceDesc.class, mccService});
        }

        ModuleDescriptor moduleDescriptor = (ModuleDescriptor) mccService.getAnnotation(ModuleDescriptor.class);

        if (moduleDescriptor == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{ModuleDescriptor.class, mccService});
        }

        Model model = createModel(moduleDescriptor.modelDesc().model());
        Service service = new Service(model);
        DataBinder.bindAnnotation(service, serviceDesc);
        service.setOutputPath(app.getOutputPath() + "/services/" + service.getName());
        service.setOutputPackage(app.getOutputPackage() + "." + NameUtils.toPackageName(service.getName()));

        // modules
        Map<Class, List<Class>> modulesMap = buildModulesMap(serviceDesc.serviceTree());

        for (Class mccModule : modulesMap.keySet()) {
            // service -> service
            if (isMccService(mccModule)) {
                Service childService = createService(mccModule);
                service.addService(childService);

                continue; // no module
            }

            Module module = createModule(mccModule, modulesMap);

            module.setBaseOutputPath(service.getOutputPath() + "/src/main/java/" + service.getOutputPackage().replace(".", "/") + "/modules/");
            module.setBaseOutputPackage(service.getOutputPackage() + ".modules.");

            service.addModule(module);
        }

        return service;
    }

    private Module createModule(Class mccModule, Map<Class, List<Class>> modulesMap) {
        ModuleDescriptor moduleDescriptor = (ModuleDescriptor) mccModule.getAnnotation(ModuleDescriptor.class);

        if (moduleDescriptor == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{ModuleDescriptor.class, mccModule});
        }

        Model model = createModel(moduleDescriptor.modelDesc().model());

        List<Class> children = modulesMap.get(mccModule);

        if (children.isEmpty()) {
            // entity module
            EntityModule entityModule = new EntityModule(model);
            DataBinder.bindAnnotation(entityModule, moduleDescriptor);
            return entityModule;
        } else if (children.size() == 1 && isMccService(children.get(0))) {
            // interface module
            InterfaceModule interfaceModule = new InterfaceModule(model);
            DataBinder.bindObject(interfaceModule, moduleDescriptor);
            Service service = createService(children.get(0));
            interfaceModule.setService(service);

            return interfaceModule;
        } else {
            // coordinator module
            CoordinatorModule coordinatorModule = new CoordinatorModule(model);
            DataBinder.bindAnnotation(coordinatorModule, moduleDescriptor);

            for (Class child : children) {
                Module childModule = createModule(child, modulesMap);
                coordinatorModule.addModule(childModule);
            }
            return coordinatorModule;
        }
    }

    private static Map<Class, List<Class>> buildModulesMap(CTree serviceTree) {
        Map<Class, List<Class>> map = new HashMap<>();

        Class root = serviceTree.root();

        for (CEdge edge : serviceTree.edges()) {
            Class parent = edge.parent();
            Class child = edge.child();

            if (parent != root) { // ignore root
                if (!map.containsKey(parent)) {
                    map.put(parent, new ArrayList<>());
                }

                List<Class> children = map.get(parent);
                children.add(child);
            }

            if (!map.containsKey(child)) {
                map.put(child, new ArrayList<>());
            }
        }

        return map;
    }

    private static Model createModel(Class domainClass) {
        Model model = new Model(domainClass);

        model.setName(domainClass.getSimpleName());
        model.setOutputPackage(domainClass.getPackageName());

        return model;
    }

    private static boolean isMccService(Class mcc) {
        ServiceDesc serviceDesc = (ServiceDesc) mcc.getAnnotation(ServiceDesc.class);
        if (serviceDesc != null) {
            return true;
        }
        return false;
    }

    public MDSoftware generate() {
        logger.info("Generating...");

        app.generate();

        return this;
    }

    /**
     * @effects Runs this software
     * This could be the software that has just been generated by {@link #generate()}.
     */
    public MDSoftware run() {
        logger.info("Running...");

        app.run();

        return this;
    }
}
