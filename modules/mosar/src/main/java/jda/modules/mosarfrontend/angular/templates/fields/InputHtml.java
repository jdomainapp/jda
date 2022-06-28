package jda.modules.mosarfrontend.angular.templates.fields;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/fields/input.html.tp"
)
public class InputHtml {
    @SlotReplacementDesc(slot = "fieldLabel")
    public String fieldLabel(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
    		System.out.println(field.getDAttr().name());
    		return field.getAttributeDesc() != null ? field.getAttributeDesc().label() : field.getDAttr().name();
    	} else {
    		return "ABCD";
    	}        
    }

    
    @SlotReplacementDesc(slot = "fieldType")
    public String fieldType(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
    		return typeConverter(field);
    	} else {
    		return "ABC";
    	}
    }

    @SlotReplacementDesc(slot = "fieldName")
    public String fieldName(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
          return field.getDAttr().name();    		
    	} else {
    		return "AAA";
    	}
    }
    
    private String typeConverter(DField field) {
        DAssoc ass = field.getDAssoc();
        switch (field.getDAttr().type()) {
            case String:
            case StringMasked:
            case Char:
            case Image:
            case Serializable:
            case Font:
            case Color:
                return "text";
            case Integer:
            case BigInteger:
            case Long:
            case Float:
            case Double:
            case Short:
            case Byte:
                return "number";
            case Boolean:
                return "boolean";
            case Domain:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return ass.associate().type().getSimpleName();
                } else if (field.getEnumName() != null) {
                    return field.getEnumName();
                } else {
                    return "any";
                }
            case Collection:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return ass.associate().type().getSimpleName() + "[]";
                } else return "any[]";
            case Array:
                return "any[]";
            case File:
            case Other:
                return "any";
            case Null:
                return "null";
            case Date:
                return "date";
            case ByteArraySmall:
            case ByteArrayLarge:
                return "number[]";
        }
        return "any";
    }
}
