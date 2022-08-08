package jda.modules.mosarfrontend.angular.app;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(
        templateFile = "/src/app/app.module.ts"
)
public class AppModuleGen{
    @LoopReplacement(ids = {"declareModuleComponents","importModuleComponents"})
    public Slot[][] declareModuleComponents(@RequiredParam.ModulesName String[] moduleNames){
        return NameFormatter.getBasicDomainNameSlots(moduleNames);
    }
}
