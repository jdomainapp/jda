package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/data_types/DataType.ts"
)
public class FormHtml {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
    public Slot[][] fields(@RequiredParam.ModuleFields FieldDef[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (FieldDef field : fields) {
            DAttrDef dAttrDef = (DAttrDef) field.getAnnotation(DAttr.class);
            if(dAttrDef == null) continue;
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", dAttrDef.name()));
            DAssocDef dAssocDef= (DAssocDef) field.getAnnotation(DAssoc.class);
            list.add(new Slot("fieldType", typeConverter(dAttrDef.type(),dAssocDef)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    private String typeConverter(DAttr.Type type, DAssoc ass){
        switch (type){
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
                if(ass != null && ass.associate() != null && ass.associate().type()!= null){
                    return ass.associate().type().getSimpleName();
                } else return "any";
            case Collection:
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
