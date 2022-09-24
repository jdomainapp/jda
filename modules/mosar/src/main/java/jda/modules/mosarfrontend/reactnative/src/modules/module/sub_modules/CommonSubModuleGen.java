package jda.modules.mosarfrontend.reactnative.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.reactnative.src.modules.module.CommonModuleGen;

public class CommonSubModuleGen extends CommonModuleGen {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName, @RequiredParam.CurrentSubDomain Domain subDomain) {
        return super.WithFilePath(moduleName) + "/sub_modules/" + NameFormatter.module_name(subDomain.getDomainClass().getSimpleName());
    }


    @SlotReplacement(id = "SubModuleName")
    public String SubModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getDomainClass().getSimpleName();
    }

    @SlotReplacement(id = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return NameFormatter.moduleName(SubModuleName(domain));
    }
}
