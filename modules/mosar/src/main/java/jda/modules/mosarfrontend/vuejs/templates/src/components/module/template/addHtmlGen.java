package jda.modules.mosarfrontend.vuejs.templates.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/add.html"
)
public class addHtmlGen extends ModuleTemplateGenBase {

    @LoopReplacement(id = "linkedDomainFormInput")
    public Slot[][] LinkedDomainFormInput(@RequiredParam.DomainFields DField[] dFields) {
        return LinkedDomain_linked_domain(dFields);
    }

    @LoopReplacement(id = "NormalFormInput")
    public Slot[][] NormalFormInput(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new));
    }
}
