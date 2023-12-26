package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import org.modeshape.common.text.Inflector;

@FileTemplateDesc(templateFile = "/inputTemplates/SimpleInput.js")
public class SimpleInputGen extends NameFormatter {
    @SlotReplacement(id = "fieldLabel")
    public String fieldLabel(@RequiredParam.ModuleField DField field) {
        return field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
    }

    @SlotReplacement(id = "fieldType")
    public String fieldType(@RequiredParam.ModuleField DField field) {
        return getFieldType(field.getDAttr().type());
    }

    @SlotReplacement(id = "fieldOptions")
    public String fieldOptions(@RequiredParam.ModuleField DField field) {
        return getFieldOptions(field.getDAttr());
    }

    @SlotReplacement(id = "fieldName")
    public String fieldName(@RequiredParam.ModuleField DField field) {
        return field.getDAttr().name();
    }

    private String getFieldOptions(DAttr dAttr) {
        StringBuilder fieldOptions = new StringBuilder();
        if (dAttr.id() || !dAttr.mutable() || dAttr.auto())
            fieldOptions.append("disabled ");
        if (!dAttr.optional() && !dAttr.id() && !dAttr.auto()) {
            fieldOptions.append("required ");
        }
        if (!Double.isInfinite(dAttr.max()))
            fieldOptions.append("max={" + dAttr.max() + "} ");
        if (!Double.isInfinite(dAttr.min()))
            fieldOptions.append("min={" + dAttr.min() + "} ");
        if (dAttr.length() > 0)
            fieldOptions.append("maxLength={" + dAttr.length() + "} ");
        return fieldOptions.toString();
    }

    private String getFieldType(DAttr.Type type) {
        switch (type) {
            case String:
            case StringMasked:
            case Char:
                return "text";
            case Integer:
            case BigInteger:
            case Long:
            case Float:
            case Double:
            case Short:
            case Byte:
            case ByteArraySmall:
            case ByteArrayLarge:
                return "number";
            case Date:
                return "date";
            case Boolean:
            case Domain:
            case Collection:
            case Array:
            case Color:
            case Font:
            case File:
            case Null:
            case Image:
            case Serializable:
            case Other:
                return "";
        }
        return "text";
    }

}