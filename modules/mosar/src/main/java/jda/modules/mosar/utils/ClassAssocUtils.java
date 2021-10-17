package jda.modules.mosar.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;

/**
 * Utilities to extract class association from domain models.
 * Depends on jDomainApp.
 */
public final class ClassAssocUtils {

    private static final Map<String, List<Class<?>>> nested;
    private static final Map<String, List<Class<?>>> associations;
    private static final Class<DAssoc> dAssocType = DAssoc.class;

    static {
        nested = new HashMap<>();
        associations = new HashMap<>();
    }

    /**
     * Get list of classes nested within {@link#cls}.
     * @param cls
     * @return
     */
    public static List<Class<?>> getNested(Class<?> cls) {
        String className = cls.getName();
        if (!nested.containsKey(className)) {
            // get nested
            findNested(cls);
        }
        return nested.get(className);
    }

    /**
     * Check if {@link#cls} has any nested classes.
     * @param cls
     * @return
     */
    public static boolean hasNested(Class<?> cls) {
        return !getNested(cls).isEmpty();
    }

    /**
     * Get list of classes associated within {@link#cls}.
     * @param cls
     * @return
     */
    public static List<Class<?>> getAssociated(Class<?> cls) {
        String className = cls.getName();
        if (!associations.containsKey(className)) {
            // get nested
            findAssociations(cls);
        }
        return associations.get(className);
    }

    /**
     * Check if {@link#cls} has any associated classes.
     * @param cls
     * @return
     */
    public static boolean hasAssociated(Class<?> cls) {
        return !getAssociated(cls).isEmpty();
    }

    private static void findAssociations(Class<?> cls) {
        List<Class<?>> associatedTypes = new LinkedList<>();
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(dAssocType)) {
                DAssoc assoc = field.getAnnotation(dAssocType);
                associatedTypes.add(assoc.associate().type());
            }
        }
        associations.put(cls.getName(), associatedTypes);
    }

    private static void findNested(Class<?> cls) {
        List<Class<?>> nestedTypes = new LinkedList<>();
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(dAssocType)) {
                DAssoc assoc = field.getAnnotation(dAssocType);

                if (assoc.ascType() == AssocType.One2Many
                        && assoc.endType() == AssocEndType.One) {
                    nestedTypes.add(assoc.associate().type());
                }
            }
        }
        nested.put(cls.getName(), nestedTypes);
    }
}
