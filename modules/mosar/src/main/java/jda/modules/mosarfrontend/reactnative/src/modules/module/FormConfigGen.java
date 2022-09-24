package jda.modules.mosarfrontend.reactnative.src.modules.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

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

    @LoopReplacement(id = "importInputs", slots = {"FieldType"})
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

    @LoopReplacement(id = "importDomainInput")
    public Slot[][] importDomainInput(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<String> imported = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            String fieldType = getFieldType(field);
            if (!imported.contains(fieldType)) {
                imported.add(fieldType);
                list.add(new Slot("InputType", fieldType));
                list.add(new Slot("linked_domain", module_name(field.getDAssoc().associate().type().getSimpleName())));
                result.add(list);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);

    }

    @LoopReplacement(id = "formConfig", slots = {"fieldName", "formType", "options"})
    public Slot[][] formConfig(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("fieldName", field.getDAttr().name()));
            list.add(new Slot("formType", "Form" + getFieldType(field) + "Input"));
            list.add(new Slot("options", getOptions(field)));
            list.add(new Slot("props", getProps(field)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public static String getProps(DField field) {
        return field.getLinkedField() != null ? String.format("props:{%s:'%s'}", field.getLinkedField().getDAttr().type() == DAttr.Type.Collection ? "associateCollection" : "associateField", field.getLinkedField().getDAttr().name()) : "";

    }

    public static String getOptions(DField field) {
        boolean haveOption = false;
        String options = "options:{";
        if (field.getLinkedDomain() != null) {
            haveOption = true;
            options += "module: Modules." + field.getLinkedDomain().getDomainClass().getSimpleName() + ",";
        }
        if (!field.getDAttr().mutable() && field.getDAttr().id() || field.getDAttr().auto()) {
            haveOption = true;
            options += "disabled:true, ";
        }
        String ruleCheck = getRuleCheck(field.getDAttr());
        if (ruleCheck.length() > 0) {
            haveOption = true;
            options += "rules:{" + ruleCheck + "},";
        }
//        if (field.getLinkedDomain() != null && field.getDAssoc().dependsOn()) {
//            haveOption = true;
//            options += "hideInMode: [JDAFormMode.CREATE, JDAFormMode.EDIT], ";
//        }
        options += "},";
        return haveOption ? options : "";
    }

    public static String getRuleCheck(DAttr dAttr) {
        String ruleCheck = "";
        if (!dAttr.optional() && !dAttr.id() && !dAttr.auto())
            ruleCheck += "required:true, ";
        if (!Double.isInfinite(dAttr.max()))
            ruleCheck += "max:" + dAttr.max() + ", ";
        if (!Double.isInfinite(dAttr.min()))
            ruleCheck += "min:" + dAttr.min() + ", ";
        if (dAttr.length() > 0)
            ruleCheck += "maxLength:" + dAttr.length() + ", ";
        return ruleCheck;
    }

    @LoopReplacement(id = "formTypeItem", slots = {"EnumType", "type", "SubModuleName"})
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


    public static String getFieldType(DField field) {
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
