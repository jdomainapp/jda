package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import org.modeshape.common.text.Inflector;

public class CommonModuleGen {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/modules/" + moduleName(moduleName);
    }
    @SlotReplacementDesc(slot = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String moduleName) {
        return Inflector.getInstance().underscore(moduleName);
    }


}
