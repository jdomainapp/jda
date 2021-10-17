package jda.modules.mosar.backend.annotations.bridges;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jda.modules.mosar.backend.annotations.*;

/**
 * Define abstract protected methods for adapting defined annotation types.
 */
abstract class DefaultRestAnnotationAdapter implements RestAnnotationAdapter {
    private final Map<String, List<AnnotationRep>> sourceAnnotations;

    public DefaultRestAnnotationAdapter() {
        this.sourceAnnotations = new LinkedHashMap<>();
    }

    @Override
    public List<AnnotationRep> getTargetAnnotations(Class<?> sourceAnnotationType) {
        return sourceAnnotations.get(sourceAnnotationType.getName());
    }

    @Override
    public void addSourceAnnotation(AnnotationRep annotation) {
        processAnnotation(annotation);
    }

    private void processAnnotation(AnnotationRep annotation) {
        if (annotation == null) return;
        final List<AnnotationRep> adapted = adapt(annotation);
        if (adapted == null) return;
        final String annotationClassName = annotation.getAnnotationClass().getName();
        this.sourceAnnotations.put(annotationClassName, adapted);
    }

    private List<AnnotationRep> adapt(AnnotationRep source) {
        if (source.isOfType(ResourceController.class)) {
            return adaptResourceController(source);
        }
        if (source.isOfType(NestedResourceController.class)) {
            return adaptNestedResourceController(source);
        }
        if (source.isOfType(Modifying.class)) {
            return adaptModifying(source);
        }
        if (source.isOfType(Create.class)) {
            return adaptCreate(source);
        }
        if (source.isOfType(Retrieve.class)) {
            return adaptRetrieve(source);
        }
        if (source.isOfType(Update.class)) {
            return adaptUpdate(source);
        }
        if (source.isOfType(Delete.class)) {
            return adaptDelete(source);
        }
        if (source.isOfType(PagingCondition.class)) {
            return adaptPagingCondition(source);
        }
        if (source.isOfType(ID.class)) {
            return adaptId(source);
        }
        if (source.isOfType(Subtype.class)) {
            return adaptSubtype(source);
        }

        return List.of();
    }

    protected abstract List<AnnotationRep> adaptResourceController(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptNestedResourceController(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptModifying(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptId(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptSubtype(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptCreate(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptRetrieve(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptPagingCondition(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptUpdate(AnnotationRep annotation);
    protected abstract List<AnnotationRep> adaptDelete(AnnotationRep annotation);
}
