package jda.modules.mosarfrontend.reactnative.templates.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/modules/ModuleConfig.ts"
)
public class ModuleConfigGen {
    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/modules/" + moduleName.toLowerCase();
    }

    @SlotReplacementDesc(slot = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacementDesc(slot = "importDataType")
    public String importDataType(@RequiredParam.ModuleName String moduleName, @RequiredParam.MCC NewMCC domain) {
        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
            moduleName = moduleName + ", " + "Sub" + moduleName;
        }
        return moduleName;
    }

    @SlotReplacementDesc(slot = "fieldID")
    public String fieldID(@RequiredParam.ModuleFields DField[] fields) {
        DField[] idField = Arrays.stream(fields).filter(f -> f.getDAttr().id()).toArray(DField[]::new);
        if (idField.length > 0) {
            return idField[0].getDAttr().name();
        } else
            return fields[0].getDAttr().name();
    }

    @SlotReplacementDesc(slot = "apiResource")
    public String apiResource(@RequiredParam.ModuleName String moduleName) {
        return Inflector.getInstance().underscore(Inflector.getInstance().pluralize(moduleName)).replace("_", "-");
    }

    @SlotReplacementDesc(slot = "moduleTitle")
    public String moduleTitle(@RequiredParam.ModuleName String moduleName) {
        Inflector inflector = Inflector.getInstance();
        return inflector.titleCase(inflector.underscore(inflector.pluralize(moduleName))).replace("_", " ");
    }

    @LoopReplacementDesc(id = "importInputs", slots = {"FieldType"})
    public Slot[][] importImputs(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            String fieldType = getFieldType(field);
            if (!imported.contains(fieldType)) {
                imported.add(fieldType);
                list.add(new Slot("FieldType", getFieldType(field)));
                result.add(list);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "formConfig", slots = {"fieldName", "formType"})
    public Slot[][] formConfig(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("fieldName", field.getDAttr().name()));
            list.add(new Slot("formType", "Form" + getFieldType(field) + "Input"));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "fieldLabelConfig", slots = {"fieldName", "fieldLabel"})
    public Slot[][] fieldLabelConfig(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("fieldName", field.getDAttr().name()));
            list.add(new Slot("fieldLabel", field.getAttributeDesc() != null ? field.getAttributeDesc().label() : field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "quickRender", slots = {"moduleAlias", "fieldName"})
    public Slot[][] quickRender(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null && !f.getDAttr().optional()).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleAlias", moduleName.toLowerCase()));
            list.add(new Slot("fieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "listTitle", slots = {"moduleAlias", "fieldName"})
    public Slot[][] listTitle(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleAlias", moduleName.toLowerCase()));
            list.add(new Slot("fieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "toPOST", slots = {"moduleName1", "linkedModule", "linkedOptional", "linkedModuleIdField"})
    public Slot[][] toPOST(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName, @RequiredParam.ModuleMap Map<String, NewMCC> domainMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAttr().type() == DAttr.Type.Domain && f.getDAssoc() != null && f.getEnumName() == null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleName1", moduleName.toLowerCase()));
            list.add(new Slot("linkedModule", field.getDAttr().name()));
            list.add(new Slot("linkedOptional", field.getDAttr().optional() ? "?" : ""));
            NewMCC domain = domainMap.get(field.getDAssoc().associate().type().getSimpleName());
            list.add(new Slot("linkedModuleIdField", domain.getIdField().getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName.toLowerCase();
    }

    private String getFieldType(DField field) {
        DAssoc ass = field.getDAssoc();
        switch (field.getDAttr().type()) {
            case String:
            case StringMasked:
            case Char:
            case Image:
            case Serializable:
            case Font:
            case Color:
                return "String";
            case Integer:
            case BigInteger:
            case Long:
            case Float:
            case Double:
            case Short:
            case Byte:
                return "Number";
            case Boolean:
                // TODO this case is not handled
                return "Boolean";
            case Domain:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return ass.associate().type().getSimpleName();
                } else if (field.getEnumName() != null) {
                    return field.getEnumName();
                } else {
                    return null;
                }
            case Collection:
            case Array:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return "Multi" + ass.associate().type().getSimpleName();
                } else return null;
            case File:
            case Other:
            case Null:
                return null;
            case Date:
                return "Date";
            case ByteArraySmall:
            case ByteArrayLarge:
                return "MultiNumber";
        }
        return "any";
    }
}
