package jda.modules.restfstool.backend.annotations.bridges;

import java.util.List;

/**
 * Adapt the annotations of <code>domainfs</code> to a target platform.
 */
public interface RestAnnotationAdapter {
    void addSourceAnnotation(AnnotationRep annotation);
    List<AnnotationRep> getTargetAnnotations(Class<?> sourceAnnotationType);

    public static RestAnnotationAdapter adaptTo(TargetType type) {
        switch (type) {
            case JAX_RS: return new JaxRsRestAnnotationAdapter();
            case SPRING: return new SpringRestAnnotationAdapter();
            default:
                return null;
        }
    }
}
