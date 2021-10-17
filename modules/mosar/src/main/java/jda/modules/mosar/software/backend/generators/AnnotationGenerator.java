package jda.modules.mosar.software.backend.generators;

import com.fasterxml.jackson.annotation.*;

import jda.modules.mosar.utils.InheritanceUtils;
import jda.modules.mosar.utils.NamingUtils;
import jda.modules.mosar.utils.PackageUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;
import static net.bytebuddy.matcher.ElementMatchers.is;

public final class AnnotationGenerator {
    private static AnnotationGenerator INSTANCE;

    public static AnnotationGenerator instance() {
        if (INSTANCE == null) {
            INSTANCE = new AnnotationGenerator();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    private static final ClassReloadingStrategy DEFAULT_RELOADING_STRATEGY;
    private static final File saveDir = new File("target/classes");
    private final Map<String, List<String>> ignoredFields;

    static {
        ByteBuddyAgent.install();
        DEFAULT_RELOADING_STRATEGY = ClassReloadingStrategy.fromInstalledAgent();
    }

    private AnnotationGenerator() {
        ignoredFields = new LinkedHashMap<>();
    }

    /**
     * Generate @JsonIgnoreProperties to handle circular dependencies.
     * @param cls
     * @param defined
     */
    Class<?> generateCircularAnnotations(Class<?> cls, Class<?>[] defined) {
        ByteBuddyAgent.install();
        try {
            Builder<?> builder = new ByteBuddy().rebase(cls);
            final String[] ignoredFields = getIgnoredFields(defined);
            for (Field f : cls.getDeclaredFields()) {
                if (!f.isAnnotationPresent(JsonProperty.class)) {
                    builder = builder.field(is(f))
                            .annotateField(
                                    ofType(JsonProperty.class).build());
                }
                if (!isDefinedTypeField(f)) continue;
                builder = builder.field(is(f))
                    .annotateField(
                        ofType(JsonIgnoreProperties.class)
                            .defineArray("value", ignoredFields)
                            .build());
            }
            return builder.make()
                            .load(cls.getClassLoader(), DEFAULT_RELOADING_STRATEGY)
                            .getLoaded();
//                .saveIn(saveDir);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        // builder.make()
    }

    /**
     * Generate @JsonSubTypes, @JsonTypeInfo, @JsonTypeName to handle inheritance.
     * @param cls
     */
    public Class<?> generateInheritanceAnnotations(Class<?> cls) {
        List<Class<?>> subtypes = InheritanceUtils.getSubtypesOf(cls);
        if (subtypes.isEmpty()) return cls;
        for (Class<?> subtype : subtypes) {
            try {
                new ByteBuddy().decorate(subtype)
                    .annotateType(ofType(JsonTypeName.class)
                        .define("value", NamingUtils.subtypeShortNameFrom(subtype))
                        .build())
                    .make()
//                    .saveIn(saveDir);
                    .load(cls.getClassLoader(), DEFAULT_RELOADING_STRATEGY);
            } catch (Throwable ex) {

            }
        }
        // decorate super
        List<AnnotationDescription> subtypeAnnotations =
            subtypes.stream()
                .map(c -> {
                    try {
                        return ofType(JsonSubTypes.Type.class)
                            .define("value", c)
                            .define("name", NamingUtils.subtypeShortNameFrom(c))
                            .build();
                    } catch (Throwable ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        try {
            return new ByteBuddy().decorate(cls)
                .annotateType(
                    ofType(JsonTypeInfo.class)
                        .define("use", JsonTypeInfo.Id.NAME)
                        .define("include", JsonTypeInfo.As.PROPERTY)
                        .define("property", "type")
                        .build(),
                    ofType(JsonSubTypes.class)
                        .defineAnnotationArray("value",
                            ForLoadedType.of(JsonSubTypes.Type.class),
                            subtypeAnnotations.toArray(
                                new AnnotationDescription[subtypeAnnotations.size()]))
                        .build())
                .make()
                .load(cls.getClassLoader(), DEFAULT_RELOADING_STRATEGY)
                .getLoaded();
            // .saveIn(saveDir);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

    }

    private String[] getIgnoredFields(Class<?>[] defined) {
        final Set<String> result = new HashSet<>();
        for (Class<?> cls : defined) {
            final String className = cls.getName();
            if (ignoredFields.containsKey(className)) {
                result.addAll(ignoredFields.get(className));
                continue;
            }
            final List<String> definedTypeFields = Stream
                .of(cls.getDeclaredFields())
                .filter(AnnotationGenerator::isDefinedTypeField)
                .map(f -> f.getName())
                .collect(Collectors.toList());
            ignoredFields.put(className, definedTypeFields);
            result.addAll(definedTypeFields);
        }
        return result.toArray(new String[result.size()]);
    }

    private static boolean isDefinedTypeField(Field f) {
        if (f.getType().isEnum()) return false;
        Type type = f.getGenericType();
        Class<?> declaringType = f.getDeclaringClass();
        String rootPackage = PackageUtils.basePackageOf(declaringType);
//        if (Collection.class.isAssignableFrom(type)) {
//            TypeVariable<?> tp = type.getTypeParameters()[0];
//            tp.getGenericDeclaration()
//            return tp.getTypeName().contains(rootPackage);
//        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            for (Type t : pt.getActualTypeArguments()) {
                if (t.getTypeName().contains(rootPackage)) {
                    return true;
                }
            }
        }
        return type.getTypeName().contains(rootPackage);
    }
}
