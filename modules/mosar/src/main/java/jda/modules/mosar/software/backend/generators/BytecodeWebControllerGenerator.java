package jda.modules.mosar.software.backend.generators;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import org.modeshape.common.text.Inflector;
import org.springframework.stereotype.Component;

import jda.modules.mosar.backend.annotations.NestedResourceController;
import jda.modules.mosar.backend.annotations.bridges.AnnotationRep;
import jda.modules.mosar.backend.annotations.bridges.RestAnnotationAdapter;
import jda.modules.mosar.backend.base.controllers.*;
import jda.modules.mosar.config.LangPlatform;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.software.backend.svcdesc.ServiceController;
import jda.modules.mosar.utils.NamingUtils;
import jda.modules.mosar.utils.PackageUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;

/**
 * Generate web controllers based on domain models and their relationships.
 *
 * @author binh_dh
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class BytecodeWebControllerGenerator implements WebControllerGenerator {

    private static final BytecodeWebControllerGenerator instance = new BytecodeWebControllerGenerator();

    public static BytecodeWebControllerGenerator instance() {
        return instance;
    }

    private static final Class<RestfulController> restCtrlClass = (Class) RestfulController.class;
    private static final Class<RestfulController> restCtrlClassImpl = (Class) DefaultRestfulController.class;
    private static final Class<RestfulController> inheritRestCtrlClass = (Class) RestfulWithInheritanceController.class;
    private static final Class<RestfulController> inheritRestCtrlClassImpl = (Class) DefaultRestfulWithInheritanceController.class;
    private static final Class nestedRestCtrlClass = NestedRestfulController.class;
    private static final Class nestedRestCtrlImplClass = DefaultNestedRestfulController.class;

    private final Map<String, Class<?>> generatedCrudClasses;
    private final RestAnnotationAdapter annotationAdapter;
    private final String outputPackage;

    private BytecodeWebControllerGenerator() {
        generatedCrudClasses = new HashMap<>();
        annotationAdapter = RestAnnotationAdapter.adaptTo(LangPlatform.SPRING);
        outputPackage = null;
    }

    BytecodeWebControllerGenerator(LangPlatform targetType, String outputPackage) {
        this.generatedCrudClasses = new HashMap<>();
        this.annotationAdapter = RestAnnotationAdapter.adaptTo(targetType);
        this.outputPackage = outputPackage;
    }

    @Override
    public RestAnnotationAdapter getAnnotationAdapter() {
        return annotationAdapter;
    }

    /**
     * Get the RESTful controller from a generic type.
     */
    @Override
    public <T> Class<RestfulController<T>> getRestfulController(Class<T> type, RFSGenConfig cfg) {
        try {
            String typeName = type.getName();
            if (!generatedCrudClasses.containsKey(typeName)) {
                generatedCrudClasses.put(typeName, generateRestfulController(type));
            }
            return (Class<RestfulController<T>>) generatedCrudClasses.get(typeName);
        } catch (IllegalAccessException | IOException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get a nested RESTful controller from a generic type.
     */
    @Override
    public <T1, T2> Class<NestedRestfulController<T1, T2>> getNestedRestfulController(Class<T1> outerType,
            Class<T2> innerType, RFSGenConfig cfg) {
        try {
            return generateNestedRestfulController(outerType, innerType, cfg);
        } catch (IllegalAccessException | IOException | NoSuchMethodException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Name cannot have more than 1 '$' token
    private <T> Class<RestfulController<T>> generateRestfulController(Class<T> type)
            throws IllegalAccessException, IOException, NoSuchMethodException, SecurityException {
        //
        final boolean hasInheritance = Modifier.isAbstract(type.getModifiers());
        final Class<RestfulController> baseImplClass = hasInheritance ? inheritRestCtrlClassImpl : restCtrlClassImpl;
        final Class<RestfulController> baseClass = hasInheritance ? inheritRestCtrlClass : restCtrlClass;
        final Inflector inflector = Inflector.getInstance();
        final String endpoint = "/" + inflector.underscore(inflector.pluralize(type.getSimpleName())).replace("_", "-");
        final String pkg = PackageUtils.basePackageFrom(this.outputPackage, type);
        final String name = NamingUtils.classNameFrom(pkg, restCtrlClass, "Controller", type);
        Builder<RestfulController> builder = generateControllerType(baseClass, baseImplClass, name, endpoint, type)
                .annotateType(ofType(ServiceController.class).define("endpoint", endpoint).define("name",
                        "Manage " + inflector.humanize(inflector.pluralize(inflector.underscore(type.getSimpleName()))))
                        .define("className", type.getName()).build());

        builder = adaptAnnotationsOnBuilder(builder, baseClass, name);

        Unloaded<RestfulController> unloaded = builder.make();
        return (Class<RestfulController<T>>) saveAndReturnClass(unloaded, name);
    }

    private static AnnotationDescription from(AnnotationRep annRep) {
        Class<? extends Annotation> annType = (Class) annRep.getAnnotationClass();
        AnnotationDescription.Builder builder = ofType(annType);
        for (Entry<String, Object> entry : annRep.getValues().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class type = value.getClass();
            if (type.isAssignableFrom(String.class)) {
                builder = builder.define(key, (String) value);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Short.class || type == Short.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Long.class || type == Long.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Integer.class || type == Integer.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Float.class || type == Float.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Double.class || type == Double.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == Byte.class || type == Byte.TYPE) {
                builder = builder.define(key, (boolean) value);
            } else if (type == String[].class) {
                builder = builder.defineArray(key, (String[]) value);
            } else {
                // not working yet
            }
        }
        return builder.build();
    }

    private <T> Builder<T> adaptAnnotationsOnBuilder(Builder<T> builder, Class baseClass, String currentName) {
        for (Method m : baseClass.getMethods()) {
            List<AnnotationDescription> adaptedAnnotations =
                adaptAnnotations(m.getAnnotations(), currentName)
                    .stream()
                    .map(BytecodeWebControllerGenerator::from)
                    .distinct()
                    .collect(Collectors.toList());

            builder = builder.method(ElementMatchers.definedMethod(ElementMatchers.named(m.getName())))
                            .intercept(MethodCall.invokeSuper().withAllArguments()
                                .withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC))
                            .annotateMethod(adaptedAnnotations);

            Parameter[] parameters = m.getParameters();
            final AtomicInteger counter = new AtomicInteger();
            for (Parameter p : parameters) {
                builder = ((MethodDefinition)builder)
                    .annotateParameter(counter.getAndIncrement(),
                        adaptAnnotations(p.getAnnotations(), currentName)
                            .stream().map(BytecodeWebControllerGenerator::from)
                            .collect(Collectors.toSet()));
            }
        }
        return builder;
    }

    private <T1, T2> Class<NestedRestfulController<T1, T2>>
        generateNestedRestfulController(Class<T1> outerType, Class<T2> innerType, RFSGenConfig cfg)
            throws IllegalAccessException, IOException,
                NoSuchMethodException, SecurityException, NoSuchFieldException {
        //
        final Inflector inflector = Inflector.getInstance();
        final String endpoint =
            "/" + inflector.underscore(inflector.pluralize(outerType.getSimpleName())).replace("_", "-")
                + "/{id}/" + inflector.underscore(inflector.pluralize(innerType.getSimpleName())).replace("_", "-");
        final String pkg = PackageUtils.basePackageFrom(this.outputPackage, outerType);
        final String name = NamingUtils.classNameFrom(pkg, nestedRestCtrlClass, "Controller", outerType, innerType);

        final Class<NestedRestfulController> baseClass = nestedRestCtrlClass;
        final Class<NestedRestfulController> baseImplClass = nestedRestCtrlImplClass;
        Builder<NestedRestfulController> builder =
            generateControllerType(baseClass, baseImplClass, name, endpoint,
                outerType, innerType);

        // add annotation
        AnnotationRep ann = new AnnotationRep(NestedResourceController.class);
        ann.setValueOf("innerType", inflector.underscore(inflector.pluralize(innerType.getSimpleName())).replace("_", "-"));
        ann.setValueOf("outerType", inflector.underscore(inflector.pluralize(outerType.getSimpleName())).replace("_", "-"));
        annotationAdapter.addSourceAnnotation(ann);
        builder = builder.annotateType(
            annotationAdapter.getTargetAnnotations(ann.getAnnotationClass())
                .stream().map(BytecodeWebControllerGenerator::from)
                .collect(Collectors.toList()));

        builder = adaptAnnotationsOnBuilder(builder, baseClass, name);

        Unloaded<NestedRestfulController> unloaded = builder.make();
        return (Class<NestedRestfulController<T1, T2>>)
            saveAndReturnClass(unloaded, name);
    }

    private <T> Builder<T> generateControllerType(
            Class<T> superInterface, Class<T> supertypeImpl, String name, String endpoint, Class<?>... genericTypes) {
        String _name = name.substring(name.lastIndexOf("$") + 1);
        _name = _name.replace("Controller", "");
        final Inflector inflector = Inflector.getInstance();
        _name = inflector.underscore(inflector.pluralize(_name)).replace("_", "-");

        List<AnnotationDescription> annoDescs = adaptAnnotations(superInterface.getAnnotations(), _name)
                .stream().map(BytecodeWebControllerGenerator::from).collect(Collectors.toList());
        annoDescs.addAll(adaptAnnotations(supertypeImpl.getAnnotations(), _name)
                .stream().map(BytecodeWebControllerGenerator::from).collect(Collectors.toList()));

        Builder<T> builder = (Builder<T>) new ByteBuddy()
            .subclass(TypeDescription.Generic.Builder
                    .parameterizedType(supertypeImpl, genericTypes).build(),
                ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
            .annotateType(annoDescs)
            .annotateType(
                ofType(Component.class)
                    .define("value", name)
                .build()
            ).name(name);

        return builder;
    }

    private static Class saveAndReturnClass(Unloaded<?> unloaded, String name) {
        try {
            unloaded.saveIn(new File("target/classes"));
            return Class.forName(name);
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }


}
