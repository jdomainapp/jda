package jda.modules.mosar.software.backend.generators;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import jda.modules.mosar.backend.annotations.bridges.AnnotationRep;
import jda.modules.mosar.backend.annotations.bridges.RestAnnotationAdapter;
import jda.modules.mosar.backend.base.controllers.NestedRestfulController;
import jda.modules.mosar.backend.base.controllers.RestfulController;
import jda.modules.mosar.config.GenerationMode;
import jda.modules.mosar.config.LangPlatform;
import jda.modules.mosar.config.RFSGenConfig;

/**
 * @author binh_dh
 */
public interface WebControllerGenerator {
    /**
     * 
     * @effects 
     *  generate the source code file for a suitable controller class for <code>type</code>. 
     *  
     *  <p>If <code>config</code> means to compile then 
     *    compile the source code file and return the class object
     *  else
     *    return null
     *  @author Duc Minh Le  
     */
    <T> Class<RestfulController<T>> getRestfulController(Class<T> type, RFSGenConfig cfg);
    
    /**
     * 
     * @effects 
     *  generate the source code file for a suitable (nested) controller class for <code>innerType</code> of 
     *  the outertype <code>outerType</code>. 
     *  
     *  <p>If <code>config</code> means to compile then 
     *    compile the source code file and return the class object
     *  else
     *    return null
     *  @author Duc Minh Le  
     */
    <T1, T2> Class<NestedRestfulController<T1, T2>> getNestedRestfulController(
        Class<T1> outerType, Class<T2> innerType, RFSGenConfig cfg);

    RestAnnotationAdapter getAnnotationAdapter();

    default List<AnnotationRep> adaptAnnotations(Annotation[] annotations, String className) {
        List<AnnotationRep> adaptedAnnotations = new LinkedList<>();
        for (Annotation ann : annotations) {
            List<AnnotationRep> annReps = adaptAnnotation(ann, className);
            if (annReps == null)
                continue;
            adaptedAnnotations.addAll(annReps);
        }
        return adaptedAnnotations;
    }

    private List<AnnotationRep> adaptAnnotation(Annotation ann, String className) {
        Class<Annotation> annType = (Class) ann.annotationType();
        AnnotationRep annRep = new AnnotationRep(annType);
        for (Method m : annType.getDeclaredMethods()) {
            try {
                annRep.setValueOf(m.getName(), m.invoke(ann));
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        annRep.setValueOf("declaredOn", className);
        RestAnnotationAdapter annotationAdapter = getAnnotationAdapter();
        annotationAdapter.addSourceAnnotation(annRep);
        return annotationAdapter.getTargetAnnotations(annType);
    }

    static WebControllerGenerator getInstance(GenerationMode mode, String outputPackage, Object... args) {
        switch (mode) {
            case BYTECODE:
                return new BytecodeWebControllerGenerator((LangPlatform) args[0], outputPackage);
            case SOURCE_CODE:
                return new SourceCodeWebControllerGenerator((LangPlatform) args[0], outputPackage, (String) args[1]);
            default:
                throw new IllegalArgumentException();
        }
    }
}
