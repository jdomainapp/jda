package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.reactjs.src.modules.BaseModuleGen;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/modules/inputTemplates/SimpleInput.js")
public class SimpleFieldGen extends NameFormatter {
    @SlotReplacement(id = "fieldLabel")
    public String fieldLabel(@RequiredParam.ModuleField DField field){
        return field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
    }

    @SlotReplacement(id="fieldType")
    public String fieldType(@RequiredParam.ModuleField DField field){
        return getFieldType(field.getDAttr().type());
    }

    @SlotReplacement(id="fieldOptions")
    public String fieldOptions(@RequiredParam.ModuleField DField field){
        return getFieldOptions(field.getDAttr())
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

    ArrayList<Slot> slotValues = new ArrayList<>();
    String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
    String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", fieldLabel));
                    slotValues.add(new Slot("type", type));
                    slotValues.add(new Slot("fieldName", fieldName));
                    slotValues.add(new Slot("fieldType", getFieldType(field.getDAttr().type())));
                    if (field.getEnumValues() != null)
                    slotValues.add(new Slot("enumOptions", renderEnumOption(field.getEnumValues())));
                    slotValues.add(new Slot("fieldOptions", getFieldOptions(field.getDAttr())));
                    result.add(slotValues);