package jda.modules.mosarfrontend.angular.app.module.components.module;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class BaseModuleGen extends NameFormatter {
    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return moduleJname(name) + ".component";
    }

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return "/src/app/" + moduleJname(name) + "/components/" + moduleJname(name);
    }
}
