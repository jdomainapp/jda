package jda.modules.mosarfrontend.angular.app.module.form;

import jda.modules.mosarfrontend.angular.app.module.form.inputTemplates.InputsGen;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/app/module/form/module-form.component.html"
)
public class formComponentHtmlGen extends BaseFormGen {
    @SlotReplacement(id = "normalInputs")
    public String normalInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getNormalInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "selectFormType")
    public String selectFormType(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return InputsGen.getSelectFormTypeInput(subDomains.keySet().toArray(String[]::new));
    }

    @SlotReplacement(id = "enumInputs")
    public String enumInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getEnumInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "linkedInputs")
    public String linkedInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getLinkedInputs(dFields, ModuleName, null);
    }

    @LoopReplacement(id = "subTypeInputs")
    public Slot[][] subTypeInputs(@RequiredParam.SubDomains Map<String, Domain> subDomains, @RequiredParam.ModuleName String ModuleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            result.add(new ArrayList<>(Arrays.asList(
                    new Slot("normalTypedInputs", InputsGen.getNormalInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("enumTypedInputs", InputsGen.getEnumInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("linkedTypedInputs", InputsGen.getLinkedInputs(subDomains.get(type).getDFields(), ModuleName, type))
            )));
        }
        return MethodUtils.toLoopData(result);
    }

}
