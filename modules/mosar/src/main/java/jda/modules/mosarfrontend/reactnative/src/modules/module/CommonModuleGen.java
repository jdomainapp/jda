package jda.modules.mosarfrontend.reactnative.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class CommonModuleGen extends NameFormatter {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/modules/" + module_name(moduleName);
    }

    @IfReplacement(id = "haveLinkedModule")
    public boolean haveLinkedModule(@RequiredParam.LinkedDomains Domain[] domains) {
        return domains.length > 0;
    }

}
