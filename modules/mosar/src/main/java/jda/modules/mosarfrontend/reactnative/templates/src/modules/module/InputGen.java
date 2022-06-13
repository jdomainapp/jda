package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;

@FileTemplateDesc(templateFile = "/src/modules/module/Input.ts")
public class InputGen {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName){
        return "/src/modules/" + moduleName.toLowerCase();
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

}
