package jda.modules.mosarfrontend.angular_new_gen.app.module.form;

import jda.modules.mosarfrontend.angular_new_gen.app.module.BaseModuleGen;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;

public class BaseFormGen extends BaseModuleGen {
    @WithFilePath
    public String filepath(@RequiredParam.ModuleName String name) {
        return super.filePath(name) + "/" + moduleJname(name) + "-form";
    }

    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name){
        return moduleJname(name)+"-form.component";
    }
}
