package jda.modules.mosarfrontend.vuejs.src.api;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "/src/api/moduleAPI.js")
public class moduleAPIGen extends NameFormatter {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String moduleName) {
        return NameFormatter.module_name(moduleName);
    }
}
