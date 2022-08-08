package jda.modules.mosarfrontend.angular.app.module.form;

import jda.modules.mosarfrontend.angular.app.module.BaseModuleGen;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;

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
