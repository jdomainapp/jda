package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/inputTemplates/SelectFormTypeInput.html")
public class SelectFormTypeInput {
    @LoopReplacement(id = "typeOptions")
    public Slot[][] typeOptions(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        return Arrays.stream(subDomain.keySet().toArray(String[]::new))
                .map(v -> new Slot[]{new Slot("option", v)})
                .toArray(Slot[][]::new);
    }
}
