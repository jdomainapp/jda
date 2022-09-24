package jda.modules.mosarfrontend.vuejs.src.api;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "/src/api/moduleAPI.js")
public class moduleAPIGen extends NameFormatter {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String moduleName) {
        return NameFormatter.module_name(moduleName);
    }
}
