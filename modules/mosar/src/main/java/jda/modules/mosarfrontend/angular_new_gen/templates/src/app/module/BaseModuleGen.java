package jda.modules.mosarfrontend.angular_new_gen.templates.src.app.module;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class BaseModuleGen extends NameFormatter {
    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name){
        return "/src/app/"+ moduleJname(name);
    }
}
