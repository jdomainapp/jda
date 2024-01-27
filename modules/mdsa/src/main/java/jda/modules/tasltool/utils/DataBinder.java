package jda.modules.tasltool.utils;


import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.util.DClassTk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBinder {

    /**
     * @requires o1 != null /\ annotation != null
     */
    public static void bindAnnotation(Object o1, Annotation annotation) {
        for (Method method : annotation.getClass().getDeclaredMethods()) {
            String fieldName = method.getName();
            try {
                Method setter = DClassTk.findSetterMethod(o1.getClass(), fieldName);
                if (setter != null) {
                    Object value = method.invoke(annotation);
                    setter.invoke(o1, value);
                } else {
                    Field field = o1.getClass().getDeclaredField(fieldName);

                    if (field != null) {
                        Object value = method.invoke(annotation);
                        field.setAccessible(true);
                        field.set(o1, value);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            }
        }
    }

    public static void bindAnnotation(Object o1, Class<? extends Annotation> annotationClass, Class classWithAnnotation) {
        if (classWithAnnotation == null)
            return;

        Annotation desc = classWithAnnotation.getAnnotation(annotationClass);

        if (desc == null) {
            throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[]{annotationClass, classWithAnnotation});
        }

        for (Method method : desc.getClass().getDeclaredMethods()) {
            String fieldName = method.getName();
            try {
                Method setter = DClassTk.findSetterMethod(o1.getClass(), fieldName);
                if (setter != null) {
                    Object value = method.invoke(desc);
                    setter.invoke(o1, value);
                } else {
                    Field field = o1.getClass().getDeclaredField(fieldName);

                    if (field != null) {
                        Object value = method.invoke(desc);
                        field.setAccessible(true);
                        field.set(o1, value);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static void bindObject(Object o1, Object o2) {
        Map<String, Object> attributeValues = getAttributeValues(o2);

        for (Field field : o1.getClass().getDeclaredFields()) {
            if(attributeValues.containsKey(field.getName())) {
                try {
                    // invoke setter
                    field.setAccessible(true);
                    field.set(o1, attributeValues.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, Object> getAttributeValues(Object o) {
        Map<String, Object> attributeValues = new HashMap<>();

        for (Field field : getAllFieldsIncludeSuperClass(o.getClass())) {
            try {
                // if getter -> use getter
                Method getter = DClassTk.findGetterMethod(o.getClass(), field.getName());

                Object value = null;
                if (getter != null) {
                    value = getter.invoke(o);
                } else {
                    field.setAccessible(true);
                    value = field.get(o);
                }
                attributeValues.put(field.getName(), value);
            } catch (IllegalAccessException | InvocationTargetException | InaccessibleObjectException e) {
                e.printStackTrace();
            }
        }

        return attributeValues;
    }

    public static List<Field> getAllFieldsIncludeSuperClass(final Class<?> clazz) {
        List<Field> lf = new ArrayList<>();
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] f = superClass.getDeclaredFields();
            for (int j = 0; j < f.length; j++) {
                lf.add(f[j]);
            }
        }
        return lf;
    }
}
