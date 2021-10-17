package jda.modules.mosar.software.backend.generators;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.utils.NamingUtils;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Generated;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceCodeGenerators {
    public static CompilationUnit generateDefaultGenericInherited(
            String basePackage,
            Class superClass, Class superInterface, Class... genericTypes) {
        final Class genericType = genericTypes[0];
        final String name = NamingUtils.classNameFrom("",
                superInterface, "Service", genericType)
                .replace(superInterface.getSimpleName(), "")
                .replace("$", "");

        CompilationUnit compilationUnit = new CompilationUnit();
        ClassOrInterfaceDeclaration classDeclaration =
                ParserToolkit.createClass(compilationUnit, name, Modifier.PUBLIC);

        // package declaration
        compilationUnit.setPackageDeclaration(
                new PackageDeclaration(new Name(basePackage)));

        // extend superclass
        ClassOrInterfaceType extendedType = JavaParser
                .parseClassOrInterfaceType(superClass.getSimpleName());
        extendedType.setTypeArguments(new NodeList<>(
                Stream.of(genericTypes)
                        .map(Class::getCanonicalName)
                        .map(JavaParser::parseType)
                        .collect(Collectors.toList())
        ));
        classDeclaration.addExtendedType(extendedType);

        compilationUnit.addImport(superClass);
        compilationUnit.addImport(genericType);
        compilationUnit.addImport(Generated.class);
        compilationUnit.addImport(Autowired.class);
        compilationUnit.addImport(Identifier.class);
        compilationUnit.addImport(PagingModel.class);

        final AtomicInteger counter = new AtomicInteger(0);

        // implement method bodies
        Method[] methods = superInterface.getMethods();
        for (Method method : methods) {
            // override method
            MethodDeclaration methodDeclaration =
                    classDeclaration.addMethod(
                            method.getName(), Modifier.PUBLIC);
            NodeList<Parameter> parameters = new NodeList<>();
            counter.set(0);
            List<Expression> arguments = new ArrayList<>();
            for (Class parameterType : method.getParameterTypes()) {
                String argName = "arg" + counter.getAndIncrement();
                arguments.add(new NameExpr(argName));
                if (parameterType == Object.class) {
                    // generic (T) type
                    parameters.add(new Parameter(
                            JavaParser.parseClassOrInterfaceType(
                                    genericTypes[genericTypes.length - 1].getSimpleName()),
                            argName
                    ));
                } else {
                    parameters.add(new Parameter(
                            JavaParser.parseClassOrInterfaceType(
                                    parameterType.getSimpleName()),
                            argName
                    ));
                }
            }
            Class returnType = method.getReturnType();
            methodDeclaration.setType(returnType == Object.class ?
                    genericTypes[genericTypes.length - 1] : returnType);
            methodDeclaration.setParameters(parameters);

            BlockStmt body = new BlockStmt();
            MethodCallExpr superMethodCallExpr = new MethodCallExpr(new SuperExpr(), method.getName());
            arguments.forEach(superMethodCallExpr::addArgument);
            if (returnType == Void.TYPE) {
                body.addStatement(superMethodCallExpr);
            } else {
                ReturnStmt stmt = new ReturnStmt(superMethodCallExpr);
                body.addStatement(stmt);
            }
            methodDeclaration.setBody(body);
        }

        return compilationUnit;
    }

    public static ConstructorDeclaration generateAutowiredConstructor(
            ClassOrInterfaceDeclaration classDeclaration,
            Class superClass) {
        final AtomicInteger counter = new AtomicInteger(0);
        ConstructorDeclaration constructorDeclaration = classDeclaration.addConstructor(Modifier.PUBLIC);
        counter.set(0);
        for (Class parameterType : superClass.getConstructors()[0].getParameterTypes()) {
            if (parameterType == Class.class) continue;
            Parameter parameter = new Parameter(
                    JavaParser.parseType(parameterType.getCanonicalName()),
                    "arg" + counter.getAndIncrement()
            );
            constructorDeclaration.addParameter(parameter);
        }

        AnnotationExpr autowiredAnnotation = new NormalAnnotationExpr(
                JavaParser.parseName(Autowired.class.getSimpleName()),
                new NodeList<>());
        constructorDeclaration.addAnnotation(autowiredAnnotation);

        BlockStmt constructorBody = new BlockStmt();
        // super call
        ExplicitConstructorInvocationStmt superConstructorCall
                = new ExplicitConstructorInvocationStmt(
                false, null, new NodeList<>(
                constructorDeclaration.getParameters()
                        .stream().map(Parameter::getNameAsExpression)
                        .collect(Collectors.toList())));
        constructorBody.addStatement(superConstructorCall);
        constructorDeclaration.setBody(constructorBody);
        return constructorDeclaration;
    }
}
