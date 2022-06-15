package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;

public class SubModuleCommonGen {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName, @RequiredParam.CurrentSubDomain Domain subDomain) {
        return "/src/modules/" + moduleName.toLowerCase() + "/sub_modules/" + subDomain.getDomainClass().getSimpleName().toLowerCase();
    }

    @SlotReplacementDesc(slot = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName.toLowerCase();
    }


    @SlotReplacementDesc(slot = "SubModuleName")
    public String SubModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getDomainClass().getSimpleName();
    }

    @SlotReplacementDesc(slot = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getDomainClass().getSimpleName().toLowerCase();
    }
}
