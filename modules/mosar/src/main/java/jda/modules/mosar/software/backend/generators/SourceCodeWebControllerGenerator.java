package jda.modules.mosar.software.backend.generators;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Generated;

import org.mdkt.compiler.InMemoryJavaCompiler;
import org.modeshape.common.text.Inflector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import jda.modules.mosar.backend.annotations.NestedResourceController;
import jda.modules.mosar.backend.annotations.bridges.AnnotationRep;
import jda.modules.mosar.backend.annotations.bridges.RestAnnotationAdapter;
import jda.modules.mosar.backend.base.controllers.DefaultNestedRestfulController;
import jda.modules.mosar.backend.base.controllers.DefaultRestfulController;
import jda.modules.mosar.backend.base.controllers.DefaultRestfulWithInheritanceController;
import jda.modules.mosar.backend.base.controllers.NestedRestfulController;
import jda.modules.mosar.backend.base.controllers.RestfulController;
import jda.modules.mosar.backend.base.controllers.RestfulWithInheritanceController;
import jda.modules.mosar.config.LangPlatform;
import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosar.utils.NamingUtils;
import jda.modules.mosar.utils.OutputPathUtils;
import jda.modules.mosar.utils.PackageUtils;

/**
 * @author binh_dh
 */
final class SourceCodeWebControllerGenerator implements WebControllerGenerator {
    private static final Class<RestfulController> restCtrlClass = RestfulController.class;
    private static final Class<RestfulController> restCtrlClassImpl = (Class) DefaultRestfulController.class;
    private static final Class<RestfulController> inheritRestCtrlClass = (Class) RestfulWithInheritanceController.class;
    private static final Class<RestfulController> inheritRestCtrlClassImpl = (Class) DefaultRestfulWithInheritanceController.class;
    private static final Class nestedRestCtrlClass = NestedRestfulController.class;
    private static final Class nestedRestCtrlImplClass = DefaultNestedRestfulController.class;
    private static final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

    private final Map<String, Class<?>> generatedCrudClasses;
    private final RestAnnotationAdapter annotationAdapter;
    private final String outputPackage;
    private final String outputFolder;
    private final Inflector inflector = Inflector.getInstance();

    SourceCodeWebControllerGenerator(String outputPackage, String outputFolder) {
        this(LangPlatform.SPRING, outputPackage, outputFolder);
    }

    SourceCodeWebControllerGenerator(LangPlatform targetType, String outputPackage, String outputFolder) {
        generatedCrudClasses = new HashMap<>();
        annotationAdapter = RestAnnotationAdapter.adaptTo(targetType);
        this.outputFolder = outputFolder;
        if (outputPackage.endsWith("controllers")) {
            this.outputPackage = outputPackage;
        } else {
            this.outputPackage = outputPackage.concat(".controllers");
        }
    }

    @Override
    public <T> Class<RestfulController<T>> getRestfulController(Class<T> type, RFSGenConfig cfg) {
        try {
            String typeName = type.getName();
            if (!generatedCrudClasses.containsKey(typeName)) {
                generatedCrudClasses.put(typeName, 
                    generateRestfulController(type, cfg));
            }
            return (Class<RestfulController<T>>) generatedCrudClasses.get(typeName);
        } catch (IllegalAccessException | IOException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> Class<RestfulController<T>> generateRestfulController(Class<T> type, RFSGenConfig cfg)
            throws IllegalAccessException, IOException, NoSuchMethodException, SecurityException {
        final boolean hasInheritance = Modifier.isAbstract(type.getModifiers());
        final Class<RestfulController> baseImplClass = hasInheritance ? inheritRestCtrlClassImpl : restCtrlClassImpl;
        final Class<RestfulController> baseClass = hasInheritance ? inheritRestCtrlClass : restCtrlClass;

        final String endpoint = buildEndpoint(type);
        final String pkg = PackageUtils.basePackageFrom(this.outputPackage, type);
        final String name = buildClassName(restCtrlClass, type);

        final CompilationUnit compilationUnit = SourceCodeGenerators.generateDefaultGenericInherited(
                pkg, baseImplClass, baseClass, type
        );

        final ClassOrInterfaceDeclaration classDeclaration = defineClass(compilationUnit);

        SourceCodeGenerators.generateAutowiredConstructor(classDeclaration, baseImplClass);

        addAnnotations(baseClass, endpoint, name, compilationUnit, classDeclaration);

        return saveAndReturnClass(pkg, name, compilationUnit, classDeclaration, cfg);
    }

    @Override
    public RestAnnotationAdapter getAnnotationAdapter() {
        return annotationAdapter;
    }

    @Override
    public <T1, T2> Class<NestedRestfulController<T1, T2>> getNestedRestfulController(Class<T1> outerType,
                                                                                      Class<T2> innerType
                                                                                      , RFSGenConfig cfg) {
        try {
            return generateNestedRestfulController(outerType, innerType, cfg);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T1, T2> Class<NestedRestfulController<T1,T2>> generateNestedRestfulController(
            Class<T1> outerType, Class<T2> innerType, RFSGenConfig cfg) {
        //
        final String endpoint = buildNestedEndpoint(outerType, innerType);
        final String pkg = PackageUtils.basePackageFrom(this.outputPackage, outerType);
        final String name = buildClassName(nestedRestCtrlClass, outerType, innerType);

        final Class<NestedRestfulController> baseClass = nestedRestCtrlClass;
        final Class<NestedRestfulController> baseImplClass = nestedRestCtrlImplClass;

        final CompilationUnit compilationUnit = SourceCodeGenerators.generateDefaultGenericInherited(
                pkg, baseImplClass, baseClass, outerType, innerType
        );

        final ClassOrInterfaceDeclaration classDeclaration = defineClass(name, compilationUnit);

        addSourceAnnotationsFrom(outerType, innerType);
        SourceCodeGenerators.generateAutowiredConstructor(classDeclaration, baseImplClass);

        addAnnotations(baseClass, endpoint, name, compilationUnit, classDeclaration);

        return saveAndReturnClass(pkg, name, compilationUnit, classDeclaration, cfg);
    }

    private String buildNestedEndpoint(Class outerType, Class innerType) {
        final String endpoint =
                "/" + inflector.underscore(inflector.pluralize(outerType.getSimpleName())).replace("_", "-")
                        + "/{id}/" + inflector.underscore(inflector.pluralize(innerType.getSimpleName())).replace("_", "-");
        return endpoint;
    }

    private ClassOrInterfaceDeclaration defineClass(String name, CompilationUnit compilationUnit) {
        final ClassOrInterfaceDeclaration classDeclaration =
                compilationUnit.getType(0).asClassOrInterfaceDeclaration();
        classDeclaration.setName(name.substring(name.lastIndexOf(".") + 1)
            .replace("NestedRestfulController$", "")
            .replace("RestfulController$", ""));
        return classDeclaration;
    }

    private <T1, T2> void addSourceAnnotationsFrom(Class<T1> outerType, Class<T2> innerType) {
        AnnotationRep ann = new AnnotationRep(NestedResourceController.class);
        ann.setValueOf("innerType", inflector.underscore(inflector.pluralize(innerType.getSimpleName())).replace("_", "-"));
        ann.setValueOf("outerType", inflector.underscore(inflector.pluralize(outerType.getSimpleName())).replace("_", "-"));
        annotationAdapter.addSourceAnnotation(ann);
    }

    private Class saveAndReturnClass(String pkg, String name,
                                     CompilationUnit compilationUnit,
                                     ClassOrInterfaceDeclaration classDeclaration
                                     , RFSGenConfig cfg) {
        Path outputPath = Path.of(outputFolder,
                compilationUnit.getPackageDeclaration()
                        .orElse(new PackageDeclaration()).getNameAsString().replace(".", "/"),
                classDeclaration.getNameAsString() + ".java");
        OutputPathUtils.writeToSource(compilationUnit, outputPath);
        
        if (cfg.isExecSpecCompile()) {
          try {
              String className = name.contains(pkg) ? name : pkg.concat(".").concat(name);
              return compiler.ignoreWarnings()
                      .useParentClassLoader(Thread.currentThread().getContextClassLoader())
                      .compile(className, compilationUnit.toString());
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
        } else {
          return null;
        }
    }

    private String buildClassName(Class baseType, Class... genericTypes) {
        final String name = NamingUtils.classNameFrom("", baseType, "Controller", genericTypes)
                .replace("NestedRestfulController$", "")
                .replace("RestfulController$", "");
        return name;
    }

    private <T> String buildEndpoint(Class<T> type) {
        final String endpoint = "/" + inflector.underscore(inflector.pluralize(type.getSimpleName())).replace("_", "-");
        return endpoint;
    }

    private ClassOrInterfaceDeclaration defineClass(CompilationUnit compilationUnit) {
        final ClassOrInterfaceDeclaration classDeclaration =
                compilationUnit.getType(0).asClassOrInterfaceDeclaration();
        classDeclaration.setName(classDeclaration.getNameAsString()
                .replace("Service", "Controller"));
        return classDeclaration;
    }

    private void addAnnotations(Class baseClass, String endpoint, String name, CompilationUnit compilationUnit, ClassOrInterfaceDeclaration classDeclaration) {
        adaptAnnotationsOnClassDeclaration(classDeclaration, baseClass, name);

        AnnotationExpr restCtrlAnnotation = new NormalAnnotationExpr(
                JavaParser.parseName(RestController.class.getCanonicalName()).removeQualifier(),
                new NodeList<>());
        AnnotationExpr requestMappingAnnotation = new NormalAnnotationExpr(
                JavaParser.parseName(RequestMapping.class.getCanonicalName()).removeQualifier(),
                new NodeList<>(
                        new MemberValuePair("value",
                                new StringLiteralExpr(endpoint))
                ));
        compilationUnit.addImport(RestController.class);
        classDeclaration.addAnnotation(restCtrlAnnotation);
        compilationUnit.addImport(RequestMapping.class);
        classDeclaration.addAnnotation(requestMappingAnnotation);

        AnnotationExpr generatedAnnotation = new NormalAnnotationExpr(
                JavaParser.parseName(Generated.class.getSimpleName()),
                new NodeList<>(
                        new MemberValuePair("value",
                                new StringLiteralExpr(getClass().getCanonicalName()))));
        classDeclaration.addAnnotation(generatedAnnotation);
    }

    private static AnnotationExpr from(AnnotationRep annRep) {
        Class<? extends Annotation> annType = (Class) annRep.getAnnotationClass();
        NormalAnnotationExpr builder = new NormalAnnotationExpr(
                JavaParser.parseName(annType.getCanonicalName()),
                new NodeList<>()
        );
        for (Map.Entry<String, Object> entry : annRep.getValues().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Class type = value.getClass();
            if (type.isAssignableFrom(String.class)) {
                builder.addPair(key, new StringLiteralExpr((String) value));
            } else if (type == Boolean.class || type == Boolean.TYPE
                    || type == Short.class || type == Short.TYPE
                    || type == Long.class || type == Long.TYPE
                    || type == Integer.class || type == Integer.TYPE
                    || type == Float.class || type == Float.TYPE
                    || type == Double.class || type == Double.TYPE
                    || type == Byte.class || type == Byte.TYPE) {
                builder.addPair(key, value.toString());
            } else if (type == String[].class) {
                String[] array = (String[]) value;
                if (array.length == 1)
                    builder.addPair(key, new StringLiteralExpr(array[0]));
            } else {
                // not working yet
            }
        }
        return builder;
    }

    private ClassOrInterfaceDeclaration adaptAnnotationsOnClassDeclaration(
            ClassOrInterfaceDeclaration classDeclaration,
            Class baseInterface,
            String currentName) {

        for (Method baseMethod : baseInterface.getMethods()) {
            MethodDeclaration method = classDeclaration.getMethodsByName(baseMethod.getName()).get(0);

            List<AnnotationExpr> adaptedAnnotations =
                    adaptAnnotations(baseMethod.getAnnotations(), currentName)
                            .stream()
                            .map(SourceCodeWebControllerGenerator::from)
                            .distinct()
                            .collect(Collectors.toList());
            for (AnnotationExpr adaptedAnnotation : adaptedAnnotations) {
                method.addAnnotation(adaptedAnnotation);
            }

            Parameter[] parameters = baseMethod.getParameters();
            final AtomicInteger counter = new AtomicInteger(0);
            for (Parameter p : parameters) {
                com.github.javaparser.ast.body.Parameter currentParam =
                        method.getParameter(counter.getAndIncrement());
                Set<AnnotationExpr> parameterAnnotations =
                        adaptAnnotations(p.getAnnotations(), currentName)
                                .stream().map(SourceCodeWebControllerGenerator::from)
                                .collect(Collectors.toSet());
                parameterAnnotations.forEach(currentParam::addAnnotation);
            }
        }
        return classDeclaration;
    }
}


