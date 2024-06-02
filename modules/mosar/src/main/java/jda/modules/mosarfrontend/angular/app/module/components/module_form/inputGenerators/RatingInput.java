package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/inputTemplates/RatingInput.html")

public class RatingInput extends BaseInputGen {

    @SlotReplacement(id = "min")
    public String min(@RequiredParam.ModuleField DField dField) {
        return String.valueOf((int)dField.getDAttr().min());
    }

    @SlotReplacement(id = "max")
    public String max(@RequiredParam.ModuleField DField dField) {
        return String.valueOf((int)dField.getDAttr().max());
    }
}
