package jda.modules.mosar.backend.annotations.bridges;

import org.springframework.web.bind.annotation.*;

import java.util.List;

class SpringRestAnnotationAdapter extends DefaultRestAnnotationAdapter {

    @Override
    protected List<AnnotationRep> adaptResourceController(AnnotationRep annotation) {
        AnnotationRep restCtrl = new AnnotationRep(RestController.class);

        AnnotationRep requestMapping = new AnnotationRep(RequestMapping.class);
        String resourceName = (String) annotation.getValueOf("name");
        if (resourceName == null || resourceName.isEmpty()) {
            resourceName = (String) annotation.getValueOf("declaredOn");
        }
        String path = "/" + resourceName.replace("_", "-").toLowerCase();
        requestMapping.setValueOf("value", new String[] { path });

        return List.of(restCtrl, requestMapping);
    }

    @Override
    protected List<AnnotationRep> adaptNestedResourceController(AnnotationRep annotation) {
        AnnotationRep restCtrl = new AnnotationRep(RestController.class);

        AnnotationRep requestMapping = new AnnotationRep(RequestMapping.class);
        String path = "/" + annotation.getValueOf("outerType") + "/{id}/" + annotation.getValueOf("innerType");
        requestMapping.setValueOf("value", new String[] { path });

        return List.of(restCtrl, requestMapping);
    }

    @Override
    protected List<AnnotationRep> adaptModifying(AnnotationRep annotation) {
        return List.of(new AnnotationRep(RequestBody.class));
    }

    @Override
    protected List<AnnotationRep> adaptId(AnnotationRep annotation) {
        // id is a path parameter
        AnnotationRep annotationRep = new AnnotationRep(PathVariable.class);
        annotationRep.setValueOf("value", "id");
        return List.of(
            // annotationRep
        );
    }

    @Override
    protected List<AnnotationRep> adaptSubtype(AnnotationRep annotation) {
        // @Subtype -> @RequestParam("type")
        AnnotationRep annotationRep = new AnnotationRep(RequestParam.class);
        annotationRep.setValueOf("value", "type");
        annotationRep.setValueOf("required", false);
        return List.of(annotationRep);
    }

    @Override
    protected List<AnnotationRep> adaptCreate(AnnotationRep annotation) {
        AnnotationRep postMapping = new AnnotationRep(PostMapping.class);
        setIfHaveId(annotation, postMapping);
        return List.of(
            postMapping
        );
    }

    @Override
    protected List<AnnotationRep> adaptRetrieve(AnnotationRep annotation) {
        // Retrieve -> Get mapping
        if (annotation.getValueOf("ignored").equals(Boolean.TRUE)) {
            return List.of();
        }
        AnnotationRep getMapping = new AnnotationRep(GetMapping.class);
        setIfHaveId(annotation, getMapping);
        return List.of(
            getMapping
        );
    }

    private void setIfHaveId(AnnotationRep source, AnnotationRep target) {
        if (source.getValueOf("byId").equals(Boolean.TRUE)) {
            target.setValueOf("value", new String[]{ "/{id}" });
        }
    }

    @Override
    protected List<AnnotationRep> adaptPagingCondition(AnnotationRep annotation) {
        // paging condition -> RequestParam
        return List.of(
            // new AnnotationRep(RequestParam.class)
        );
    }

    @Override
    protected List<AnnotationRep> adaptUpdate(AnnotationRep annotation) {
        AnnotationRep patchMapping = new AnnotationRep(PatchMapping.class);
        setIfHaveId(annotation, patchMapping);
        return List.of(
            patchMapping
        );
    }

    @Override
    protected List<AnnotationRep> adaptDelete(AnnotationRep annotation) {
        AnnotationRep deleteMapping = new AnnotationRep(DeleteMapping.class);
        setIfHaveId(annotation, deleteMapping);
        return List.of(
            deleteMapping
        );
    }

}
