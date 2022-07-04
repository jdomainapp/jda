package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.module.FormConfigGen;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/sub_modules/FormConfig.ts")
public class SubFormConfigGen extends CommonSubModuleGen {

//    @WithFilePath
//    public String withFilePath(@RequiredParam.ModuleName String moduleName, @RequiredParam.CurrentSubDomain Domain subDomain) {
//        return "/src/modules/" + moduleName.toLowerCase() + "/sub_modules/" + subDomain.getDomainClass().getSimpleName().toLowerCase();
//    }

    @SlotReplacement(slot = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacement(slot = "SubModuleName")
    public String SubModuleName(@RequiredParam.CurrentSubDomain Domain subDomain) {
        return subDomain.getDomainClass().getSimpleName();
    }

    @IfReplacement(id = "BasicFormInputGen")
    public boolean BasicFormInputGen(@RequiredParam.CurrentSubDomain Domain subDomain) {
        return Arrays.stream(subDomain.getDFields()).anyMatch(f -> f.getDAssoc() == null);
    }

    @IfReplacement(id = "ModuleFormInputGen")
    public boolean ModuleFormInputGen(@RequiredParam.CurrentSubDomain Domain subDomain) {
        return Arrays.stream(subDomain.getDFields()).anyMatch(f -> f.getDAssoc() != null);
    }

    @LoopReplacement(id = "importInputs", slots = {"FieldType"})
    public Slot[][] importImputs(@RequiredParam.CurrentSubDomain Domain subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : Arrays.stream(subDomain.getDFields()).filter(f -> f.getDAssoc() == null).toArray(DField[]::new)) {
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

    @LoopReplacement(id = "importDomainInput", slots = {"DomainName", "domainName"})
    public Slot[][] importDomainInput(@RequiredParam.CurrentSubDomain Domain subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : Arrays.stream(subDomain.getDFields()).filter(f -> f.getDAssoc() != null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            String fieldType = getFieldType(field);
            if (!imported.contains(fieldType)) {
                imported.add(fieldType);
                list.add(new Slot("DomainName", fieldType));
                list.add(new Slot("domainName", field.getDAssoc().associate().type().getSimpleName().toLowerCase()));
                result.add(list);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formConfig", slots = {"fieldName", "formType", "ruleChecks"})
    public Slot[][] formConfig(@RequiredParam.CurrentSubDomain Domain subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : subDomain.getDFields()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("fieldName", field.getDAttr().name()));
            list.add(new Slot("formType", "Form" + getFieldType(field) + "Input"));
            list.add(new Slot("ruleChecks", FormConfigGen.getRuleCheck(field.getDAttr())));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public String getFieldType(DField field) {
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
