package jda.modules.mosar.frontend;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.metadef.MetaAttrDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.utils.DomainTypeRegistry;

public class ViewStateUtils {
    private static final DomainTypeRegistry domainTypeRegistry = DomainTypeRegistry.getInstance();

    /**
     * Generate JS default value by field
     * @param fieldProperties
     * @return
     */
    public static String getDefaultValueByField(Map<String, Object> fieldProperties) {
        DAttr.Type fieldType = (DAttr.Type)fieldProperties.get("type");
        String defaultValue = (String) fieldProperties.get("defaultValue");
        if (defaultValue == null) defaultValue = "␀";
        if (!defaultValue.equals("␀")) return defaultValue;
        if (fieldType.isString()) {
            return "\"\"";
        } else if (fieldType.isBoolean()) {
            return "false";
        } else if (fieldType.isNumeric()) {
            return "0";
        } else {
            return "undefined";
        }
    }

    public static String generateViewFormState(MCC mcc, boolean fromProps, boolean isCreateForm) {
        Collection<FieldDeclaration> domainFields = mcc.getDomainClass()
                .getDomainFields()
                .stream()
                .filter(fieldDeclaration ->
                        !fieldDeclaration.isStatic() && fieldDeclaration.isPrivate())
                .collect(Collectors.toSet());

        List<String> initStates = new LinkedList<>();
        StringBuilder result = new StringBuilder();
        result.append("{").append("\n");

        for (FieldDeclaration domainField : domainFields) {
            List<String> initValue =
                    initialStateFromField(domainField, fromProps, !isCreateForm, !isCreateForm);
            initStates.addAll(initValue);
        }
        result.append("  ")
                .append(String.join(",\n  ", initStates));

        result.append("\n").append("}");
        return result.toString();
    }

    public static String submitStateFromInitState(String initStates) {
        List<String> submitStates = new LinkedList<>();
        String[] onlyStates = initStates.replace("{", "").replace("}", "").trim().split(",\n  ");
        for (String state : onlyStates) {
            String key = state.split(":")[0];
            submitStates.add(String.format("%s: this.state.%s", key, key));
        }
        return "{\n  " + String.join(",\n  ", submitStates) + "\n}";
    }

    public static List<String> initialStateFromField(FieldDeclaration field,
                                              boolean fromProps,
                                              boolean shouldHaveId,
                                              boolean shouldRetainCollections) {
        return initialStateFromField(field, fromProps,
                shouldHaveId, shouldRetainCollections, "");
    }

    static List<String> initialStateFromDomainField(FieldDef fieldDef,
                                                   String template,
                                                   String fieldName,
                                                   String initValue,
                                                   boolean fromProps,
                                                   boolean shouldHaveId) {
        ClassOrInterfaceType fieldTypeParam = fieldDef.getType().asClassOrInterfaceType();
        Class<?> actualFieldTypeParam = domainTypeRegistry.getDomainTypeByName(fieldTypeParam.getNameAsString());
        ClassAST classAST = new ClassAST(
                fieldTypeParam.getNameAsString(),
                MCCUtils.getFullPath(actualFieldTypeParam).toString());
        FieldDeclaration idField = MCCUtils.getIdFieldOf(classAST);
        List<String> stateFields = new LinkedList<>();
        stateFields.add(String.format(template, fieldName, initValue));
        stateFields.addAll(initialStateFromField(idField, fromProps, shouldHaveId, false, fieldName));
        return stateFields;
    }

    public static List<String> initialStateFromField(FieldDeclaration field,
                                              boolean fromProps,
                                              boolean shouldHaveId,
                                              boolean shouldRetainCollections,
                                              String referredField) {
        FieldDef fieldDef = ParserToolkit.getFieldDefFull(field);
        ClassOrInterfaceDeclaration cls = field.findAncestor(ClassOrInterfaceDeclaration.class).get();
        Map<String, Object> fieldProperties = MCCUtils.getPropertiesByAnnotation(fieldDef, DAttr.class);
        Map<String, Object> fieldAssocProperties = MCCUtils.getPropertiesByAnnotation(fieldDef, DAssoc.class);
        String template = fromProps ? "%s: this.props.%s" : "%s: %s";
        String fieldName = fieldProperties.get("name") instanceof NameExpr ?
                initValueOf((NameExpr) fieldProperties.get("name"), cls).toString()
                : fieldProperties.get("name").toString();
        String initValue = fromProps ? fieldName : getDefaultValueByField(fieldProperties);

        if (!fieldAssocProperties.isEmpty()) {
            DAttr.Type fieldType = (DAttr.Type) fieldProperties.get("type");
            if (fieldType.isDomainType()) {
                return initialStateFromDomainField(
                        fieldDef, template, fieldName, initValue, fromProps, shouldHaveId);
            }
            else if (!shouldRetainCollections) {
                return List.of();
            }
        }

        boolean isVirtual = fieldProperties.get("virtual") != null && (Boolean) fieldProperties.get("virtual");
        if (isVirtual) return List.of();

        boolean isId = fieldProperties.get("id") != null && (Boolean) fieldProperties.get("id");
        boolean isAuto = fieldProperties.get("auto") != null && (Boolean) fieldProperties.get("auto");
        boolean reallyShouldHaveId = !referredField.isEmpty() || shouldHaveId;
        if ((isId || isAuto) && !reallyShouldHaveId) {
            return List.of();
        }

        if (!referredField.isEmpty()) {
            fieldName = referredField + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            if (fromProps) initValue = fieldName;
        }

        return List.of(String.format(template, fieldName, initValue));
    }

    public static Map<String, Object> getDomainAttrsOfViewField(FieldDeclaration viewField, ClassOrInterfaceDeclaration domainClass) {
        String viewFieldName = ParserToolkit.getFieldName(viewField);
        FieldDeclaration domainField = ParserToolkit
                .getDomainFieldsByName(domainClass, List.of(viewFieldName)).get(0);
        Collection<MetaAttrDef> metaAttrDefs = ParserToolkit.getFieldDefFull(domainField).getAnnotations();
        return metaAttrDefs.stream()
                .map(MetaAttrDef::getProperties)
                .flatMap(m -> m.stream())
                .collect(Collectors.toMap(
                        attr -> attr.getKey(),
                        attr -> {
                            Object value = attr.getValue();
                            if (value instanceof NameExpr) {
                                return initValueOf((NameExpr) value, domainClass);
                            }
                            return value;
                        }));
    }

    public static Object initValueOf(NameExpr expr, ClassOrInterfaceDeclaration cls) {
        Object initValue = cls.getFieldByName(expr.getNameAsString())
                .get().getVariable(0).getInitializer().get();
        if (initValue instanceof LiteralStringValueExpr) {
            return ((LiteralStringValueExpr) initValue).getValue();
        } else if (initValue instanceof LongLiteralExpr) {
            return ((LongLiteralExpr) initValue).getValue();
        } else if (initValue instanceof IntegerLiteralExpr) {
            return ((LongLiteralExpr) initValue).getValue();
        } else if (initValue instanceof BooleanLiteralExpr) {
            return ((LongLiteralExpr) initValue).getValue();
        } else if (initValue instanceof DoubleLiteralExpr) {
            return ((LongLiteralExpr) initValue).getValue();
        } else if (initValue instanceof NullLiteralExpr) {
            return null;
        } else if (initValue instanceof CharLiteralExpr) {
            return ((LongLiteralExpr) initValue).getValue();
        } else if (initValue instanceof NameExpr) {
            return initValueOf((NameExpr) initValue, cls);
        } else {
            return null;
        }
    }

    public static boolean isIdOrAuto(FieldDeclaration fieldDeclaration) {
        FieldDef fieldDef = ParserToolkit.getFieldDefFull(fieldDeclaration);
        Map<String, Object> fieldProperties = MCCUtils.getPropertiesByAnnotation(fieldDef, DAttr.class);
        boolean isId = fieldProperties.get("id") != null && (Boolean) fieldProperties.get("id");
        boolean isAuto = fieldProperties.get("auto") != null && (Boolean) fieldProperties.get("auto");
        return isId || isAuto;
    }
}
