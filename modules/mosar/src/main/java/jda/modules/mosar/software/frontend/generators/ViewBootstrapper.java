package jda.modules.mosar.software.frontend.generators;

import jda.modules.common.io.ToolkitIO;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosar.utils.FileUtils;
import jda.modules.mosarfrontend.common.factory.AppFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ViewBootstrapper {
    private static final String EXTENSION = ".js";
    private final Class moduleMainClass;
    private final Class[] models;
    private final String projectSrcDir;
    private final Class sccClass;
    private MCC[] modules;
    private long beServerPort;
    private RFSGenConfig cfg;

    public ViewBootstrapper(RFSGenConfig cfg) {
        this.projectSrcDir = cfg.getFeOutputPath();
        this.sccClass = cfg.getSCC();
        this.moduleMainClass = cfg.getMCCMain();
        this.models = cfg.getDomainModel();
        Class[] mccClasses = cfg.getMCCFuncs();
        this.modules = IntStream.range(0, mccClasses.length)
                .mapToObj(i -> MCCUtils.readMCC(models[i],
                        mccClasses[i]))
                .collect(Collectors.toList())
                .toArray(new MCC[mccClasses.length]);
        this.beServerPort = cfg.getBeServerPort();
        this.cfg = cfg;
    }

    private Map<Class, MCC> getModelModuleMap() {
        final Map<String, Class> classMap = new HashMap<>();
        final Map<String, MCC> mccMap = new HashMap<>();
        final Map<Class, MCC> classMCCMap = new HashMap<>();
        for (Class cls : models) {
            classMap.put(cls.getSimpleName(), cls);
        }
        for (MCC mcc : modules) {
            mccMap.put(mcc.getDomainClass().getName(), mcc);
        }

        for (String key : classMap.keySet()) {
            if (mccMap.containsKey(key)) {
                classMCCMap.put(classMap.get(key), mccMap.get(key));
            }
        }
        return classMCCMap;
    }

    /**
     * @effects initialise common resources (e.g. serverPort) from the configuration
     * @version 5.4.1
     */
    public ViewBootstrapper init() {
        return this;
    }

    /**
     * @version 5.4.1
     * return this for use in fluent-style API
     */
    public ViewBootstrapper bootstrapAndSave() {
        AppFactory appFactory = new AppFactory(cfg);
        appFactory.genAndSave();
        return this;
    }

}
