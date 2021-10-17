package jda.modules.mosar.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utilities related to inheritance (subtype-mapping).
 */
public final class InheritanceUtils {
    private static final Map<String, Map<String, String>> cachedSubtypes;

    static {
        cachedSubtypes = new HashMap<>();
    }

    /**
     * Returns the subtype map of {@link#supertype}.
     * @param supertype
     */
    public static Map<String, String> getSubtypeMapFor(Class<?> supertype) {
        String supertypeName = supertype.getName();
        if (cachedSubtypes.containsKey(supertypeName)) {
            return cachedSubtypes.get(supertypeName);
        }

        String basePkg = PackageUtils.basePackageOf(supertype);
        List<Class<?>> classes;
        try {
            classes = ClassUtils.getClasses(basePkg);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> subtypes = new HashMap<>();
        for (Class<?> cls : classes) {
            if (supertype.isAssignableFrom(cls) && cls != supertype) {
                subtypes.put(NamingUtils.subtypeShortNameFrom(cls), cls.getName());
            }
        }
        cachedSubtypes.put(supertypeName, subtypes);

        return subtypes;
    }

    private static Class<?> classFromName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Return subtypes of {@link#supertype}.
     * @param supertype
     * @return
     */
    public static List<Class<?>> getSubtypesOf(Class<?> supertype) {
        String supertypeName = supertype.getName();

        if (cachedSubtypes.containsKey(supertypeName)) {
            return cachedSubtypes.get(supertypeName)
                    .values()
                    .stream()
                    .map(InheritanceUtils::classFromName)
                    .collect(Collectors.toList());
        } else {
            getSubtypeMapFor(supertype);
            return getSubtypesOf(supertype);
        }
    }
}
