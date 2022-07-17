package jda.modules.mosarfrontend.reactnative.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.reactnative.src.modules.module.FormConfigGen;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/sub_modules/FormConfig.ts")
public class SubFormConfigGen extends CommonSubModuleGen {

    @SlotReplacement(id = "SubModuleName")
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
            String fieldType = FormConfigGen.getFieldType(field);
            if (!imported.contains(fieldType)) {
                imported.add(fieldType);
                list.add(new Slot("FieldType", FormConfigGen.getFieldType(field)));
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
            String fieldType = FormConfigGen.getFieldType(field);
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
            list.add(new Slot("formType", "Form" + FormConfigGen.getFieldType(field) + "Input"));
            list.add(new Slot("options", FormConfigGen.getOptions(field)));
            list.add(new Slot("props", FormConfigGen.getProps(field)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
