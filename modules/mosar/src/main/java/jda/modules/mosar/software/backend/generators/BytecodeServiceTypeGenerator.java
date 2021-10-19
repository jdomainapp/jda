package jda.modules.mosar.software.backend.generators;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import jda.modules.mosar.backend.base.services.CrudService;
import jda.modules.mosar.backend.base.services.InheritedDomServiceAdapter;
import jda.modules.mosar.backend.base.services.SimpleDomServiceAdapter;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.utils.InheritanceUtils;
import jda.modules.mosar.utils.NamingUtils;
import jda.modules.mosar.utils.PackageUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;

/**
 * @author binh_dh
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class BytecodeServiceTypeGenerator implements ServiceTypeGenerator {

    private static final Class crudServiceClass = CrudService.class;
    private static final Class absCrudServiceClass = SimpleDomServiceAdapter.class;
    private static final Class absInheritedCrudServiceClass = InheritedDomServiceAdapter.class;

    private final Map<String, Class<?>> generatedServices;
    private final String outputPackage;

    BytecodeServiceTypeGenerator(String outputPackage) {
        generatedServices = new ConcurrentHashMap<>();
        this.outputPackage = outputPackage;
    }

    /**
     * Generate service types for autowiring.
     */
    @Override
    public <T> Class<CrudService<T>> generateAutowiredServiceType(Class<T> type, RFSGenConfig config) {
        //
        String genericTypeName = type.getName();

        if (generatedServices.containsKey(genericTypeName)) {
            return (Class<CrudService<T>>)
                generatedServices.get(genericTypeName);
        }

        final String pkg = PackageUtils.basePackageFrom(this.outputPackage, type);

        final String name = NamingUtils.classNameFrom(
                pkg, crudServiceClass, "Service", type);

        final String simpleName = type.getName();

        //
        Unloaded unloaded;
        final boolean hasInherit = Modifier.isAbstract(type.getModifiers());
        Class<CrudService> superClass = hasInherit ? absInheritedCrudServiceClass : absCrudServiceClass;
        Builder<?> builder = new ByteBuddy()
                    .subclass(superClass)
                    .annotateType(
                    ofType(Service.class)
                        .define("value", simpleName)
                    .build());
        if (hasInherit) {
            unloaded = generateServiceTypeWithInherit(builder, type, name);
        } else {
            unloaded = generateServiceType(builder, type, name);
        }

        Class returning = saveAndReturnClass(unloaded, name);
        generatedServices.put(genericTypeName, returning);
        return (Class<CrudService<T>>) returning;
    }

    private static <T> Unloaded<T> generateServiceType(
        Builder<T> builder, Class<?> type, String name) {

        int numOfParams = absCrudServiceClass
            .getDeclaredConstructors()[0].getParameterTypes().length;
        return builder
            .constructor(ElementMatchers.isConstructor()
                    .and(ElementMatchers.takesArguments(numOfParams)))
            .intercept(MethodCall
                .invoke(absCrudServiceClass
                    .getDeclaredConstructors()[0])
                .withAllArguments()
                .andThen(MethodCall
                    .invoke(ElementMatchers.named("setType"))
                    .onSuper()
                    .with(type)))
            .annotateMethod(ofType(Autowired.class).build())
            .name(name)
            .make();
    }

    private static <T> Unloaded<T> generateServiceTypeWithInherit(
            Builder<T> builder, Class<?> type, String name) {

        int numOfParams = absInheritedCrudServiceClass
                        .getDeclaredConstructors()[0].getParameterTypes().length;

        return builder
            .constructor(ElementMatchers.isConstructor()
                    .and(ElementMatchers.takesArguments(numOfParams)))
            .intercept(Advice.to(InheritDomServiceAdapterConstructorAdvice.class).wrap(
                MethodCall
                    .invoke(absInheritedCrudServiceClass
                        .getDeclaredConstructors()[0])
                    .withAllArguments()
                .andThen(FieldAccessor.ofField("type")
                    .setsValue(type))
                ))
            .annotateMethod(ofType(Autowired.class).build())
            .annotateParameter(1,
                    ofType(Qualifier.class)
                    .define("value", type.getName())
                    .build())
            .name(name)
            .make();
    }

    private static Class saveAndReturnClass(Unloaded<?> unloaded, String name) {
        try {
            unloaded.saveIn(new File("target/classes"));
            return Class.forName(name);
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    static class InheritDomServiceAdapterConstructorAdvice {
        @Advice.OnMethodExit
        static void exit(@Advice.This InheritedDomServiceAdapter instance) {
            instance.setSubtypes(InheritanceUtils.getSubtypeMapFor(instance.getType()));
        }
    }
}
