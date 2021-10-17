package jda.modules.mosar.backend.annotations.bridges;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class AnnotationRep {
    private Class<? extends Annotation> annotationClass;
    private Map<String, Object> values;

    public AnnotationRep(Class<? extends Annotation> cls) {
        this.annotationClass = cls;
        this.values = new LinkedHashMap<>();
    }

    public Class<?> getAnnotationClass() {
        return annotationClass;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public Object getValueOf(String key) {
        return values.get(key);
    }

    public void setValueOf(String key, Object value) {
        this.values.put(key, value);
    }

    public boolean isOfType(Class<?> type) {
        return annotationClass == type;
    }

}
