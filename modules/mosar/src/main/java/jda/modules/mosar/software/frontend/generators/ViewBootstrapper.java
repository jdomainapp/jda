package jda.modules.mosar.software.frontend.generators;

import jda.modules.common.io.ToolkitIO;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosar.utils.FileUtils;
import jda.modules.mosarfrontend.common.factory.AppGenerator;
import jda.modules.mosarfrontend.common.factory.GenConfig;
import jda.modules.mosarfrontend.reactjs.FEReactApp;
import jda.modules.mosarfrontend.reactjs.model.common.MCCRegistry;
import jda.modules.mosarfrontend.reactnative.ReactNativeAppGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public ViewBootstrapper(
//        final String projectSrcDir,
//        final Class sccClass, final Class moduleMainClass,
//        final Class[] models, final Class[] mccClasses
            RFSGenConfig cfg
    ) {
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
        for (MCC mcc : modules) {
            MCCRegistry.getInstance().add(mcc);
        }
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

    private void saveFile(String folder, String fileName, String content) {
        Path path = new File(projectSrcDir).toPath();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path dir = new File(projectSrcDir).toPath().resolve("./" + folder);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path classFile = dir.resolve(fileName);
        if (!Files.exists(classFile)) {
            try {
                Files.createFile(classFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.writeString(classFile, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @effects initialise common resources (e.g. serverPort) from the configuration
     * @version 5.4.1
     */
    public ViewBootstrapper init() {
        // change feServerPort in Constants.js to this.feServerPort
        String feParentProjPath = ToolkitIO.getMavenProjectRootPath(FEReactApp.class, true);
        if (feParentProjPath == null) {
            feParentProjPath = ToolkitIO.getCurrentDir();
        }

        String feProjResource = ToolkitIO.getPath(feParentProjPath,
                FileUtils.separatorsToSystem(cfg.getFeProjResource())).toString();
        File constantsTempFile = ToolkitIO.getPath(feProjResource, "common", "templates", "Constants.js").toFile();
        File constantsFile = ToolkitIO.getPath(feProjResource, "common", "Constants.js").toFile();
        String constantsJs = ToolkitIO.readTextFileContent(constantsTempFile)
                .replace("{{ beServerPort }}", beServerPort + "");
        ToolkitIO.writeTextFile(constantsFile, constantsJs, true);
        return this;
    }

    /**
     * @version 5.4.1
     * return this for use in fluent-style API
     */
    public ViewBootstrapper bootstrapAndSave() {
        final MCC mainMCC = MCCUtils.readMCC(null, moduleMainClass);
        GenConfig genConfig = new GenConfig(sccClass, mainMCC, getModelModuleMap(),projectSrcDir);
        AppGenerator appGenerator = new AppGenerator(ReactNativeAppGenerator.class, genConfig);
        appGenerator.gen();
//        for (FrontendModule frontendModule : appEntryPoint.getFrontendModules()) {
//            String folder = frontendModule.getFolder();
//            for (View view : frontendModule.getViews()) {
//                String fileName = view.getFileName() + EXTENSION;
//                String content = view.getAsString();
//                saveFile(folder, fileName, content);
//                if (view instanceof HasSubView) {
//                    HasSubView viewWithSubview = (HasSubView) view;
//                    for (View submodule : viewWithSubview.getSubViews()) {
//                        String _fileName = submodule.getFileName() + EXTENSION;
//                        String _content = submodule.getAsString();
//                        saveFile(folder, _fileName, _content);
//                    }
//                }
//            }
//            saveFile(folder, "index" + EXTENSION, frontendModule.getAsString());
//        }
//        String fileName = "./" + appEntryPoint.getFileName() + EXTENSION;
//        String content = appEntryPoint.getAsString();
//        saveFile("", fileName, content);
        return this;
    }

//    public static void main(String[] args) {
//        // initialize the model
//        final Class<?>[] models = {
//                CourseModule.class,
//                Enrolment.class,
//                Student.class,
//                Address.class,
//                StudentClass.class,
//                CompulsoryModule.class,
//                ElectiveModule.class
//        };
//        // initialize module classes
//        // one module per INHERITANCE TREE
//        final Class<?>[] modules = {
//                ModuleCourseModule.class,
//                ModuleEnrolment.class,
//                ModuleStudent.class,
//                ModuleAddress.class,
//                ModuleStudentClass.class
//        };
//        final MCC[] mccs = {
//                MCCUtils.readMCC(CourseModule.class, ModuleCourseModule.class),
//                MCCUtils.readMCC(Enrolment.class, ModuleEnrolment.class),
//                MCCUtils.readMCC(Student.class, ModuleStudent.class),
//                MCCUtils.readMCC(Address.class, ModuleAddress.class),
//                MCCUtils.readMCC(StudentClass.class, ModuleStudentClass.class)
//        };
//
//
//        Class sccClass = SCC1.class;
//
////        ViewBootstrapper bootstrapper = new ViewBootstrapper(
////                "/Users/binh_dh/Downloads/generated", sccClass, ModuleMain.class,
////                models, mccs);
////        System.out.println(bootstrapper);
////        bootstrapper.bootstrapAndSave();
//    }
}
