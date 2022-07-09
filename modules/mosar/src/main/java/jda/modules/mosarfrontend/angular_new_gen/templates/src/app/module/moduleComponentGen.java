package jda.modules.mosarfrontend.angular_new_gen.templates.src.app.module;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
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
