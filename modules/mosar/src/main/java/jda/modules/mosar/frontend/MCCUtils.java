package jda.modules.mosar.frontend;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.metadef.MetaAttrDef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptualmodel.MCC;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MCCUtils {
    public static FieldDeclaration getIdFieldOf(ClassAST classAST) {
        return ParserToolkit.getDomainFields(classAST.getCls())
                .stream()
                .filter(fieldDeclaration -> {
                    FieldDef def = ParserToolkit.getFieldDefFull(fieldDeclaration);
                    Map<String, Object> defProperties = getPropertiesByAnnotation(def, DAttr.class);
                    return defProperties.get("id") != null && (Boolean) defProperties.get("id");
                })
                .findFirst().get();
    }

    public static Map<String, Object> getPropertiesByAnnotation(FieldDef fieldDef, Class<? extends Annotation> annotationClass) {
        MetaAttrDef attrDef = fieldDef.getAnnotation(annotationClass);
        if (attrDef == null) return Map.of();
        return attrDef.getProperties().stream()
                .collect(Collectors.toUnmodifiableMap(
                        x -> x.getKey(), x -> x.getValue()));
    }

    public static FieldDeclaration getDomainField(MCC mcc, FieldDeclaration viewField) {
        final String name = ParserToolkit.getFieldName(viewField);
        final ClassOrInterfaceDeclaration domainClass = mcc.getDomainClass().getCls();
        return ParserToolkit.getDomainFieldsByName(
                domainClass,
                List.of(name)).get(0);
    }

    static String getLongFileNameWithoutSuffix(Class<?> cls) {
        return cls.getCanonicalName().replace(".", "/");
    }

    public static Path getProjectPath(Class<?> cls) {
        String fileName = cls.getSimpleName() + ".class";
        String longFileName = getLongFileNameWithoutSuffix(cls) + ".class";
        String classPath = cls.getResource(fileName).getPath().replace(longFileName, "");
        Path projectPath = new File(classPath).toPath();
        return projectPath.getParent().getParent();
    }

    public static Path getFullPath(Class<?> cls) {
        Path projectPath = getProjectPath(cls);
        Path fullPath = projectPath.resolve("src/main/java")
                .resolve(getLongFileNameWithoutSuffix(cls).concat(".java"));
        if (Files.exists(fullPath)) return fullPath;
        fullPath = projectPath.resolve("src/")
                .resolve(getLongFileNameWithoutSuffix(cls).concat(".java"));
        if (Files.exists(fullPath)) return fullPath;
        fullPath = projectPath.resolve("src/example/java/")
                .resolve(getLongFileNameWithoutSuffix(cls).concat(".java"));
        return fullPath;
    }

    // read MCC
    public static MCC readMCC(Class<?> domainClass, Class<?> mccClass) {
        if (domainClass == null)
            return new MCC(mccClass.getSimpleName(), getFullPath(mccClass).toString(), null);
        ClassAST dcls = new ClassAST(domainClass.getSimpleName(), getFullPath(domainClass).toString());
        return new MCC(mccClass.getSimpleName(), getFullPath(mccClass).toString(), dcls);
    }

    public static Map<String, Expression> getExpressionMap(NormalAnnotationExpr expression) {
        if (expression == null) return Map.of();
        final Map<String, Expression> expressionMap = new LinkedHashMap<>();
        expression.getPairs().forEach(pair -> expressionMap.put(pair.getName().toString(), pair.getValue()));
        return expressionMap;
    }

}
