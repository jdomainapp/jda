package jda.modules.mosarfrontend.reactjsbhd.model.views.fields;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosar.utils.DomainTypeRegistry;
import jda.modules.mosarfrontend.reactjsbhd.model.common.FieldDefExtensions;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

class SimpleViewField extends ViewField {
    private final String label;
    private boolean isDisabled;
    private boolean needApiCall = false;

    private SimpleViewField(FieldDef fieldDef, String label) {
        super(fieldDef);
        this.label = escapeQuotes(label);
        final Map<String, Object> attributes = FieldDefExtensions.getAttribute(getFieldDef());
        this.isDisabled = (boolean) attributes.getOrDefault("id", false)
                || (boolean) attributes.getOrDefault("auto", false);
    }

    public static String escapeQuotes(String input) {
        return input.replace("\"", "").replace("'", "");
    }

    public static SimpleViewField createUsing(FieldDef fieldDef, String label) {
//        ensureSimpleViewFieldDef(fieldDef);
        return new SimpleViewField(fieldDef, label);
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    private boolean isDisabled() {
        return isDisabled;
    }

    void setNeedApiCall(boolean needApiCall) {
        this.needApiCall = needApiCall;
    }

    void setDisabled(boolean newDisabledState) {
        this.isDisabled = newDisabledState;
    }

    private String getInputType() {
        final DAttr.Type type = FieldDefExtensions.getDomainType(getFieldDef()).get();
        if (type.isDomainReferenceType()
                && FieldDefExtensions.isPrimitiveOrEnumType(getFieldDef())) {
            return "select";
        } else if (type.isString() || type.isDomainType()) return "text";
        else if (type.isDate()) return "date";
        else if (type.isNumeric()) return "number";
        else if (type.isColor()) return "color";
        else if (type.isBoolean()) return "checkbox";
        else {
            throw new IllegalStateException("Unsupported input type: " + type);
        }
    }

    @Override
    public JsTemplate getTemplate() {
//        if (FieldDefExtensions.isPrimitiveOrEnumType(getFieldDef())) {
//            return JsTemplates.SIMPLE_VIEW_FIELD;
//        }
        String type = getInputType();
        switch (type) {
            case "select":
                return JsTemplates.SELECT_OPTION;
            case "checkbox":
                return JsTemplates.CHECKBOX;
            case "text":
            case "date":
            case "color":
            case "number":
                return JsTemplates.SIMPLE_VIEW_FIELD;
        }
        throw new IllegalStateException("Unsupported input type: " + type);
    }

    private static void ensureSimpleViewFieldDef(FieldDef fieldDef) {
        DAttr.Type type = FieldDefExtensions.getDomainType(fieldDef).orElse(null);
        if (type == null) {
            throw new IllegalArgumentException("Not reflecting a domain field: " + fieldDef);
        }
        String fieldTypeName = fieldDef.getType().isPrimitiveType() ?
                fieldDef.getType().asPrimitiveType().getType().name():
                fieldDef.getType().asClassOrInterfaceType().getNameAsString();
        Class fieldTypeCls = DomainTypeRegistry.getInstance().getDomainTypeByName(fieldTypeName);
        if ((type.isDomainType() || type.isDomainReferenceType())
                && (Objects.nonNull(fieldTypeCls) && !fieldTypeCls.isEnum())) {
            throw new IllegalArgumentException("Not a simple domain field: " + fieldDef);
        }
    }

    @Override
    public String getAsString() {
        return this.getTemplate().getAsString()
                .replace("{{ fieldLabel }}", getLabel())
                .replace("{{ fieldType }}", getInputType())
                .replace("{{ backingField }}", getBackingField())
                .replace("{{ disabledFlag }}", this.isDisabled() ? "disabled" : "")
                .replace("{{ needApiCall }}", Boolean.toString(this.needApiCall))
                .replace("{{ options }}", getInputType().equals("select") ?
                        String.join("\n",
                                Stream.of(DomainTypeRegistry.getInstance()
                                    .getDomainTypeByName(
                                        getFieldDef().getType()
                                            .asClassOrInterfaceType()
                                            .getNameAsString()).getEnumConstants())
                                    .map(s -> String.format(
                                            "<option value='%s'>%s</option>",s, s))
                                    .collect(Collectors.toList()))
                        : "");
    }
}
