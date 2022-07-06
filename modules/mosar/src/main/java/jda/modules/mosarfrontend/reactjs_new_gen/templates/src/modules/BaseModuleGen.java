package jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;

public class BaseModuleGen extends DomainNameUtil {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/" + module_names(moduleName);
    }
}
