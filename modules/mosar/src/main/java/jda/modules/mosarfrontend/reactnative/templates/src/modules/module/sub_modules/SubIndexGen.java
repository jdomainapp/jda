package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/sub_modules/Index.ts")
public class SubIndexGen extends CommonSubModuleGen {

    @SlotReplacement(id = "importDataType")
    public String importDataType(@RequiredParam.CurrentSubDomain Domain domain) {
        String moduleName = domain.getDomainClass().getSimpleName();
        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
            moduleName = moduleName + ", " + "Sub" + moduleName;
        }
        return moduleName;
    }
}
