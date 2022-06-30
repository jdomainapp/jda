package jda.modules.mosarfrontend.vuejs.templates.src.api;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.vuejs.VueNameUtil;

@FileTemplateDesc(templateFile = "/src/api/moduleAPI.js")
public class moduleAPIGen extends VueNameUtil {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String moduleName) {
        return VueNameUtil.module_name(moduleName);
    }
}
