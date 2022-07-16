package jda.modules.mosarfrontend.reactnative.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.utils.Domain;

@FileTemplateDesc(templateFile = "/src/modules/module/sub_modules/Index.ts")
public class SubIndexGen extends CommonSubModuleGen {

    @SlotReplacement(id = "importDataType")
    public String importDataType(@RequiredParam.CurrentSubDomain Domain domain) {
        String moduleName = domain.getDomainClass().getSimpleName();
//        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
//            moduleName = moduleName + ", " + "Sub" + moduleName;
//        }
        return moduleName;
    }
}
