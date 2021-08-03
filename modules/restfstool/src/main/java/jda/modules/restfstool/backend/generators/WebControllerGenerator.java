package jda.modules.restfstool.backend.generators;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import jda.modules.restfstool.backend.annotations.bridges.AnnotationRep;
import jda.modules.restfstool.backend.annotations.bridges.RestAnnotationAdapter;
import jda.modules.restfstool.backend.base.controllers.NestedRestfulController;
import jda.modules.restfstool.backend.base.controllers.RestfulController;
import jda.modules.restfstool.config.GenerationMode;
import jda.modules.restfstool.config.LangPlatform;

/**
 * @author binh_dh
 */
public interface WebControllerGenerator {
    <T> Class<RestfulController<T>> getRestfulController(Class<T> type);
    <T1, T2> Class<NestedRestfulController<T1, T2>> getNestedRestfulController(
        Class<T1> outerType, Class<T2> innerType);

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
