package jda.modules.mosarfrontend.vuejs.templates.src.api;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;

@FileTemplateDesc(templateFile = "/src/api/moduleAPI.js")
public class moduleAPIGen extends DomainNameUtil {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String moduleName) {
        return DomainNameUtil.module_name(moduleName);
    }
}
