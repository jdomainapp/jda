package jda.modules.restfstool.frontend.bootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.restfstool.frontend.models.common.MCCRegistry;
import jda.modules.restfstool.frontend.models.nonviews.AppEntryPoint;
import jda.modules.restfstool.frontend.models.nonviews.FrontendModule;
import jda.modules.restfstool.frontend.models.views.HasSubView;
import jda.modules.restfstool.frontend.models.views.View;
import jda.modules.restfstool.frontend.utils.MCCUtils;

public final class ViewBootstrapper {
    private static final String EXTENSION = ".js";
    private final Class moduleMainClass;
    private final Class[] models;
    private final String projectSrcDir;
    private final Class sccClass;
    private MCC[] modules;

    public ViewBootstrapper(final String projectSrcDir,
                            final Class sccClass, final Class moduleMainClass,
                            final Class[] models, final Class[] mccClasses) {
        this.projectSrcDir = projectSrcDir;
        this.sccClass = sccClass;
        this.moduleMainClass = moduleMainClass;
        this.models = models;
        this.modules = IntStream.range(0, mccClasses.length)
                .mapToObj(i -> MCCUtils.readMCC(models[i], 
                    mccClasses[i]))
                .collect(Collectors.toList())
                .toArray(new MCC[mccClasses.length]);
        for (MCC mcc : modules) {
            MCCRegistry.getInstance().add(mcc);
        }
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
                Files.createDirectory(path);
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

    public void bootstrapAndSave() {
        final MCC mainMCC = MCCUtils.readMCC(null, moduleMainClass);
        final AppEntryPoint appEntryPoint = new AppEntryPoint(sccClass, mainMCC, getModelModuleMap());
        for (FrontendModule frontendModule : appEntryPoint.getFrontendModules()) {
            String folder = frontendModule.getFolder();
            for (View view : frontendModule.getViews()) {
                String fileName = view.getFileName() + EXTENSION;
                String content = view.getAsString();
                saveFile(folder, fileName, content);
                if (view instanceof HasSubView) {
                    HasSubView viewWithSubview = (HasSubView) view;
                    for (View submodule : viewWithSubview.getSubViews()) {
                        String _fileName = submodule.getFileName() + EXTENSION;
                        String _content = submodule.getAsString();
                        saveFile(folder, _fileName, _content);
                    }
                }
            }
            saveFile(folder, "index" + EXTENSION, frontendModule.getAsString());
        }
        String fileName = "./" + appEntryPoint.getFileName() + EXTENSION;
        String content = appEntryPoint.getAsString();
        saveFile("", fileName, content);
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
