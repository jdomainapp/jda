package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/inputTemplates/NormalInput.html")
public class NormalInputGen extends BaseInputGen {
    @IfReplacement(id = "notID")
    public boolean notID(@RequiredParam.ModuleField DField dField) {
        return !dField.getDAttr().id();
    }

}
