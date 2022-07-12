package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.vuejs.src.components.module.template.inputTemplates.InputsGen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/form.html"
)
public class addHtmlGen extends ModuleTemplateGenBase {
    public boolean getAddMode() {
        return true;
    }

    @WithFileName
    public String fileName() {
        return "add";
    }

    ;

    @SlotReplacement(id = "normalInputs")
    public String normalInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getNormalInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "enumInputs")
    public String enumInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getEnumInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "linkedInputs")
    public String linkedInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getLinkedInputs(dFields, NameFormatter.moduleName(ModuleName), null, this.getAddMode());
    }

    @LoopReplacement(id = "subTypeInputs")
    public Slot[][] subTypeInputs(@RequiredParam.SubDomains Map<String, Domain> subDomains, @RequiredParam.ModuleName String ModuleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            result.add(new ArrayList<>(Arrays.asList(
                    new Slot("normalTypedInputs", InputsGen.getNormalInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("enumTypedInputs", InputsGen.getEnumInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("linkedTypedInputs", InputsGen.getLinkedInputs(subDomains.get(type).getDFields(), ModuleName, type, this.getAddMode()))
            )));
        }
        return MethodUtils.toLoopData(result);
    }
}
