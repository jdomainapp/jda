package jda.modules.restfstool.backend.generators;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jda.modules.restfstool.backend.annotations.bridges.TargetType;
import jda.modules.restfstool.backend.utils.ClassAssocUtils;
import jda.modules.restfstool.backend.utils.InheritanceUtils;

@SuppressWarnings({ "rawtypes" })
public class WebServiceGenerator {

    private final WebControllerGenerator webControllerGenerator;
    private final ServiceTypeGenerator serviceTypeGenerator;
    private final AnnotationGenerator annotationGenerator;
    private final List<Class> generatedControllerClasses;
    private final Map<String, Class> generatedServiceClasses;
    private Consumer<List<Class>> generateCompleteCallback;

    public WebServiceGenerator(
            TargetType targetType,
            GenerationMode generationMode,
            String outputPackage,
            String outputPath) {
        this.webControllerGenerator = WebControllerGenerator.getInstance(
                generationMode, outputPackage, targetType, outputPath);
        this.serviceTypeGenerator = ServiceTypeGenerator.getInstance(
                generationMode, outputPackage, outputPath);
        this.annotationGenerator = AnnotationGenerator.instance();

        generatedControllerClasses = new LinkedList<>();
        generatedServiceClasses = new LinkedHashMap<>();
    }

    public void setGenerateCompleteCallback(Consumer<List<Class>> generateCompleteCallback) {
        this.generateCompleteCallback = generateCompleteCallback;
    }

    public Map<String, Class> getGeneratedServiceClasses() {
        return generatedServiceClasses;
    }

    public List<Class> getGeneratedControllerClasses() {
        return generatedControllerClasses;
    }

    /**
     * Generate a simple RESTful Web Service from a number of domain classes.
     * @param classes
     */
    public void generateWebService(Class... classes) {
        List<Class<?>> ignored = getIgnoredClasses(classes);
        Class<?> tempClass;
        for (Class<?> cls : classes) {
            if (ignored.contains(cls)) continue;
//            cls = annotationGenerator.generateCircularAnnotations(cls, classes);
            cls = annotationGenerator.generateInheritanceAnnotations(cls);

            tempClass = serviceTypeGenerator.generateAutowiredServiceType(cls);
            generatedServiceClasses.put(cls.getCanonicalName(), tempClass);
            tempClass = webControllerGenerator.getRestfulController(cls);
            generatedControllerClasses.add(tempClass);
            List<Class<?>> nestedClasses = ClassAssocUtils.getNested(cls);
            for (Class<?> nested : nestedClasses) {
                if (nested == cls) continue;
                tempClass = webControllerGenerator.getNestedRestfulController(cls, nested);
                generatedControllerClasses.add(tempClass);
            }
        }
        List<Class> generatedClasses = new ArrayList<>(generatedServiceClasses.values());
        generatedClasses.addAll(generatedControllerClasses);
        onGenerateComplete(generatedClasses);
    }

    /**
     * Ignored classes are subclasses of others.
     */
    private static List<Class<?>> getIgnoredClasses(Class[] classes) {
        return Stream.of(classes).map(c -> InheritanceUtils.getSubtypesOf(c))
                .reduce((l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                }).orElse(List.of());
    }

    private void onGenerateComplete(List<Class> generatedClasses) {
        generateCompleteCallback.accept(generatedClasses);
    }
}
