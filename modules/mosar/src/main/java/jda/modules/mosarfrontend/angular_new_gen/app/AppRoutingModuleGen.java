package jda.modules.mosarfrontend.angular_new_gen.app;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(
        templateFile = "/src/app/app-routing.module.ts"
)
public class AppRoutingModuleGen {
    @LoopReplacement(ids = {"importModuleComponent","routes"})
    public Slot[][] importModuleComponent(@RequiredParam.ModulesName String[] moduleNames){
        return NameFormatter.getBasicDomainNameSlots(moduleNames);
    }
}
