package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.module.CommonModuleGen;
import org.modeshape.common.text.Inflector;

public class CommonSubModuleGen extends CommonModuleGen {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName, @RequiredParam.CurrentSubDomain Domain subDomain) {
        return "/src/modules/" + moduleName(moduleName) + "/sub_modules/" + subModuleName(subDomain);
    }


    @SlotReplacementDesc(slot = "SubModuleName")
    public String SubModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getDomainClass().getSimpleName();
    }

    @SlotReplacementDesc(slot = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return Inflector.getInstance().underscore(domain.getDomainClass().getSimpleName());
    }
}
