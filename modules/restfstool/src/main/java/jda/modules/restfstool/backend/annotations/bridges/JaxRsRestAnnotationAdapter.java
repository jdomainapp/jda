package jda.modules.restfstool.backend.annotations.bridges;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

class JaxRsRestAnnotationAdapter extends DefaultRestAnnotationAdapter {

    @Override
    protected List<AnnotationRep> adaptResourceController(AnnotationRep annotation) {
        AnnotationRep consumes = new AnnotationRep(Consumes.class);
        consumes.setValueOf("value", new String[] { MediaType.APPLICATION_JSON });

        AnnotationRep produces = new AnnotationRep(Produces.class);
        produces.setValueOf("value", new String[] { MediaType.APPLICATION_JSON });

        AnnotationRep requestMapping = new AnnotationRep(Path.class);
        String resourceName = (String) annotation.getValueOf("name");
        if (resourceName == null || resourceName.isEmpty()) {
            resourceName = (String) annotation.getValueOf("declaredOn");
        }
        String path = "/" + resourceName.replace(" ", "-").toLowerCase();
        requestMapping.setValueOf("value", path);

        return List.of(requestMapping, consumes, produces);
    }

    @Override
    protected List<AnnotationRep> adaptNestedResourceController(AnnotationRep annotation) {
        AnnotationRep consumes = new AnnotationRep(Consumes.class);
        consumes.setValueOf("value", new String[] { MediaType.APPLICATION_JSON });

        AnnotationRep produces = new AnnotationRep(Produces.class);
        produces.setValueOf("value", new String[] { MediaType.APPLICATION_JSON });

        AnnotationRep requestMapping = new AnnotationRep(Path.class);
        String path = "/" + annotation.getValueOf("outerType") + "/{id}/" + annotation.getValueOf("innerType");
        requestMapping.setValueOf("value", path);

        return List.of(consumes, produces, requestMapping);
    }

    @Override
    protected List<AnnotationRep> adaptModifying(AnnotationRep annotation) {
        return List.of();
    }

    @Override
    protected List<AnnotationRep> adaptId(AnnotationRep annotation) {
        AnnotationRep annotationRep = new AnnotationRep(PathParam.class);
        annotationRep.setValueOf("value", "id");
        return List.of(
            annotationRep
        );
    }

    @Override
    protected List<AnnotationRep> adaptSubtype(AnnotationRep annotation) {
        AnnotationRep annotationRep = new AnnotationRep(QueryParam.class);
        annotationRep.setValueOf("value", "type");
        AnnotationRep defaultVal = new AnnotationRep(DefaultValue.class);
        defaultVal.setValueOf("value", "");
        return List.of(
            annotationRep,
            defaultVal
        );
    }

    @Override
    protected List<AnnotationRep> adaptCreate(AnnotationRep annotation) {
        AnnotationRep postMapping = new AnnotationRep(POST.class);
        if (annotation.getValueOf("byId").equals(Boolean.TRUE)) {
            AnnotationRep path = new AnnotationRep(Path.class);
            path.setValueOf("value", "/{id}");
            return List.of(postMapping, path);
        }
        return List.of(
            postMapping
        );
    }

    @Override
    protected List<AnnotationRep> adaptRetrieve(AnnotationRep annotation) {
        if (annotation.getValueOf("ignored").equals(Boolean.TRUE)) {
            return List.of();
        }
        AnnotationRep getMapping = new AnnotationRep(GET.class);
        if (annotation.getValueOf("byId").equals(Boolean.TRUE)) {
            AnnotationRep path = new AnnotationRep(Path.class);
            path.setValueOf("value", "/{id}");
            return List.of(getMapping, path);
        }
        return List.of(
            getMapping
        );
    }

    @Override
    protected List<AnnotationRep> adaptPagingCondition(AnnotationRep annotation) {
        return List.of(
            new AnnotationRep(BeanParam.class)
        );
    }

    @Override
    protected List<AnnotationRep> adaptUpdate(AnnotationRep annotation) {
        AnnotationRep patchMapping = new AnnotationRep(PATCH.class);
        if (annotation.getValueOf("byId").equals(Boolean.TRUE)) {
            AnnotationRep path = new AnnotationRep(Path.class);
            path.setValueOf("value", "/{id}");
            return List.of(patchMapping, path);
        }
        return List.of(
            patchMapping
        );
    }

    @Override
    protected List<AnnotationRep> adaptDelete(AnnotationRep annotation) {
        AnnotationRep deleteMapping = new AnnotationRep(DELETE.class);
        if (annotation.getValueOf("byId").equals(Boolean.TRUE)) {
            AnnotationRep path = new AnnotationRep(Path.class);
            path.setValueOf("value", "/{id}");
            return List.of(deleteMapping, path);
        }
        return List.of(
            deleteMapping
        );
    }
}
