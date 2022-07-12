package jda.modules.mosarfrontend.reactnative.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
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
