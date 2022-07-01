package jda.modules.mosarfrontend.angular.templates.fields;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.utils.DField;

@FileTemplateDesc(
        templateFile = "/fields/input.html.tp"
)
public class InputHtml {
    @SlotReplacement(slot = "fieldLabel")
    public String fieldLabel(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
    		System.out.println(field.getDAttr().name());
    		return field.getAttributeDesc() != null ? field.getAttributeDesc().label() : field.getDAttr().name();
    	} else {
    		return "ABCD";
    	}        
    }

    
    @SlotReplacement(slot = "fieldType")
    public String fieldType(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
    		return typeConverter(field);
    	} else {
    		return "ABC";
    	}
    }

    @SlotReplacement(slot = "fieldName")
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
