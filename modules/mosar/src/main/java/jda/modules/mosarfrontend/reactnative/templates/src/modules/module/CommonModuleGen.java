package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;
import org.modeshape.common.text.Inflector;

public class CommonModuleGen extends DomainNameUtil {
    @WithFilePath
    public String WithFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/modules/" + module_name(moduleName);
    }

    @IfReplacement(id = "haveLinkedModule")
    public boolean haveLinkedModule(@RequiredParam.LinkedDomains Domain[] domains) {
        return domains.length > 0;
    }

}