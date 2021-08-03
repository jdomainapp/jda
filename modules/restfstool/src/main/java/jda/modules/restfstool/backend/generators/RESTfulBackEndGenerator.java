package jda.modules.restfstool.backend.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jda.modules.restfstool.backend.BEGenOutput;
import jda.modules.restfstool.backend.utils.ClassAssocUtils;
import jda.modules.restfstool.backend.utils.InheritanceUtils;
import jda.modules.restfstool.config.GenerationMode;
import jda.modules.restfstool.config.LangPlatform;

@SuppressWarnings({ "rawtypes" })
public class RESTfulBackEndGenerator {

    private final WebControllerGenerator webControllerGenerator;
    private final ServiceTypeGenerator serviceTypeGenerator;
    private final AnnotationGenerator annotationGenerator;
    
    private BEGenOutput output;
    
    // these are deprecated by BEGenOutput
//    private final List<Class> generatedControllerClasses;
//    private final Map<String, Class> generatedServiceClasses;
    
    /**
     * derived from {@link #generatedControllerClasses} & {@link #generatedServiceClasses}
     */
//    private List<Class> generatedClasses;
    
    private Consumer<List<Class>> generateCompleteCallback;

    public RESTfulBackEndGenerator(
            LangPlatform targetType,
            GenerationMode generationMode,
            String outputPackage,
            String outputPath
            ) {
        this.webControllerGenerator = WebControllerGenerator.getInstance(
                generationMode, outputPackage, targetType, outputPath);
        this.serviceTypeGenerator = ServiceTypeGenerator.getInstance(
                generationMode, outputPackage, outputPath);
        this.annotationGenerator = AnnotationGenerator.instance();

//        generatedControllerClasses = new LinkedList<>();
//        generatedServiceClasses = new LinkedHashMap<>();
//        generatedClasses = new LinkedList<>();
    }

    public void setGenerateCompleteCallback(Consumer<List<Class>> generateCompleteCallback) {
        this.generateCompleteCallback = generateCompleteCallback;
    }

//    public Map<String, Class> getGeneratedServiceClasses() {
//        return generatedServiceClasses;
//    }
//
//    public List<Class> getGeneratedControllerClasses() {
//        return generatedControllerClasses;
//    }
//
//    public List<Class> getGeneratedClasses() {
//      return generatedClasses;
//    }
    
    /**
     * Generate a simple RESTful Web Service from a number of domain classes.
     * @param classes
     */
    public BEGenOutput run(Class... classes) {
        List<Class<?>> ignored = getIgnoredClasses(classes);
        Class<?> tempClass;
        
        final List<Class> generatedControllerClasses = new LinkedList<>();
        final Map<String, Class> generatedServiceClasses = new HashMap<>();
        
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

        // TODO: deprecated
        List generatedClasses = new ArrayList<>(generatedServiceClasses.values());
        generatedClasses.addAll(generatedControllerClasses);
        
        if (generateCompleteCallback != null)
          onGenerateComplete(generatedClasses);
        // end deprecated
        
        output = new BEGenOutput();
        output.setServices(generatedServiceClasses);
        output.setControllers(generatedControllerClasses);
        
        return output;
    }

    /**
     * @effects return output
     */
    public BEGenOutput getOutput() {
      return output;
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
