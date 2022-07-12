package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.vuejs.src.components.module.ModuleGenBase;

public class ModuleTemplateGenBase extends ModuleGenBase {
    @WithFilePath
    public String withTemplateFilePath(@RequiredParam.ModuleName String moduleName) {
        return withFilePath(moduleName) + "/template";
    }
}
