package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/modules/module/FormConfig.ts")
public class FormConfigGen extends CommonModuleGen {

    @IfReplacement(id = "BasicFormInputGen")
    public boolean BasicFormInputGen(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).anyMatch(f -> f.getDAssoc() == null);
    }

    @IfReplacement(id = "ModuleFormInputGen")
    public boolean ModuleFormInputGen(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).anyMatch(f -> f.getDAssoc() != null);
    }

    @LoopReplacementDesc(id = "importInputs", slots = {"FieldType"})
    public Slot[][] importImputs(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new)) {
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

    @LoopReplacementDesc(id = "importDomainInput", slots = {"DomainName", "domainName"})
    public Slot[][] importDomainInput(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            String fieldType = getFieldType(field);
            if (!imported.contains(fieldType)) {
                imported.add(fieldType);
                list.add(new Slot("DomainName", fieldType));
                list.add(new Slot("domainName", moduleName(field.getDAssoc().associate().type().getSimpleName())));
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

//    @IfReplacement(id = "FormList")
//    public boolean importTypedFormItem(@RequiredParam.MCC NewMCC mcc) {
//        return !mcc.getSubDomains().isEmpty();
//    }

    @IfReplacement(id = "importTypedFormItem")
    public boolean GenFormList(@RequiredParam.MCC NewMCC mcc) {
        return !mcc.getSubDomains().isEmpty();
    }

    @LoopReplacementDesc(id = "formTypeItem", slots = {"EnumType", "type", "SubModuleName"})
    public Slot[][] formTypeItem(@RequiredParam.ModuleName String moduleName, @RequiredParam.SubDomains Map<String, Domain> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : moduleMap.keySet()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("EnumType", moduleName));
            list.add(new Slot("type", type));
            list.add(new Slot("SubModuleName", moduleMap.get(type).getDomainClass().getSimpleName()));
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
