package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;
import jda.modules.mosarfrontend.reactnative.templates.src.modules.module.CommonModuleGen;

public class CommonSubModuleGen extends CommonModuleGen {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName, @RequiredParam.CurrentSubDomain Domain subDomain) {
        return super.WithFilePath(moduleName) + "/sub_modules/" + DomainNameUtil.module_name(subDomain.getDomainClass().getSimpleName());
    }


    @SlotReplacement(slot = "SubModuleName")
    public String SubModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getDomainClass().getSimpleName();
    }

    @SlotReplacement(slot = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return DomainNameUtil.moduleName(SubModuleName(domain));
    }
}
