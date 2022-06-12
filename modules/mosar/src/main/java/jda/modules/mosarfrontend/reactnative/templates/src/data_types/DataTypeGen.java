package jda.modules.mosarfrontend.reactnative.templates.src.data_types;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/data_types/DataType.ts"
)
public class DataTypeGen {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @LoopReplacementDesc(slots = {"importModuleName"}, id = "import")
    public Slot[][] importInterface(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            if (field.getDAssoc() != null) {
                ArrayList<Slot> list = new ArrayList<>();
                list.add(new Slot("importModuleName", field.getDAssoc().associate().type().getSimpleName()));
                result.add(list);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(slots = {"importEnumName"}, id = "importEnum")
    public Slot[][] importEnumInterface(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            if (field.getEnumName() != null) {
                ArrayList<Slot> list = new ArrayList<>();
                list.add(new Slot("importEnumName", field.getEnumName()));
                result.add(list);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
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

    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "subInterface")
    public Slot[][] subInterface(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getDAttr().name() + "ID" + (field.getDAttr().optional() ? "?" : "")));
            list.add(new Slot("fieldType", getIDTypeOfDomain(field, moduleMap)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(slots = {"domainFieldName"}, id = "domainFields")
    public Slot[][] domainFields(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("domainFieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @IfReplacement(id = "subtype")
    public boolean subtype(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).anyMatch(f -> f.getDAssoc() != null);
    }

    private String getIDTypeOfDomain(DField dField, Map<String, NewMCC> moduleMap) {
        NewMCC domain = moduleMap.get(dField.getDAssoc().associate().type().getSimpleName());
        String type = typeConverter(domain.getIdField());
        return dField.getDAttr().type().isCollection() ? type += "[]" : type;
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
                    return "Sub" + ass.associate().type().getSimpleName();
                } else if (field.getEnumName() != null) {
                    return field.getEnumName();
                } else {
                    return "any";
                }
            case Collection:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return "Sub" + ass.associate().type().getSimpleName() + "[]";
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
