package jda.modules.mosarfrontend.angular.app.module;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class BaseModuleGen extends NameFormatter {
    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name){
        return "/src/app/"+ moduleJname(name);
    }
}
