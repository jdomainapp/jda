package jda.modules.mosarfrontend.vuejs.templates.src.model;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;

public class BaseModelGen extends DomainNameUtil {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String name) {
        return DomainNameUtil.module_name(name);
    }
}
