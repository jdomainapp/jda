package jda.modules.mosarfrontend.angular.app.module;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

@FileTemplateDesc(
        templateFile = "/src/app/module/module.component.ts"
)
public class moduleComponentGen extends BaseModuleGen {
    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return moduleJname(name) + ".component";
    }

    @LoopReplacement(id = "fieldConfigs")
    public Slot[][] fieldConfigs(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(dFields);
    }
}
