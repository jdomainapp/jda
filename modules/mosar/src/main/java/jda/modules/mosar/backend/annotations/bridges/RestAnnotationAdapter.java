package jda.modules.mosar.backend.annotations.bridges;

import java.util.List;

import jda.modules.mosar.config.LangPlatform;

/**
 * Adapt the annotations of <code>domainfs</code> to a target platform.
 */
public interface RestAnnotationAdapter {
    void addSourceAnnotation(AnnotationRep annotation);
    List<AnnotationRep> getTargetAnnotations(Class<?> sourceAnnotationType);

    public static RestAnnotationAdapter adaptTo(LangPlatform type) {
        switch (type) {
// ducmle:        case JAX_RS: return new JaxRsRestAnnotationAdapter();
            case SPRING: return new SpringRestAnnotationAdapter();
            default:
                return null;
        }
    }
}
