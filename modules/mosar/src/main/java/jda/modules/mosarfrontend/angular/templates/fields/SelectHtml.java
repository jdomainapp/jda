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
        templateFile = "/modules/form.html"
)
public class SelectHtml {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
    public Slot[][] fields(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getDAttr().name() + (field.getDAttr().optional() ? "?" : "")));
            list.add(new Slot("fieldType", typeConverter(field)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
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
                return "string";
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
                return "Date";
            case ByteArraySmall:
            case ByteArrayLarge:
                return "number[]";
        }
        return "any";
    }
}
