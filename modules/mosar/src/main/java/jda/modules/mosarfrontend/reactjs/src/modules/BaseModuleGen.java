package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class BaseModuleGen extends NameFormatter {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/" + module_names(moduleName);
    }
}
