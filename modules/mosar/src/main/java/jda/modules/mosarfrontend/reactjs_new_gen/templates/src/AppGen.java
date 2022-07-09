package jda.modules.mosarfrontend.reactjs_new_gen.templates.src;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(
        templateFile = "/src/App.js"
)
public class AppGen {
    @LoopReplacement(id = "getModules")
    public Slot[][] getModules(@RequiredParam.ModulesName String[] names) {
        return NameFormatter.getBasicDomainNameSlots(names);
    }

    @LoopReplacement(id = "moduleRoutes")
    public Slot[][] moduleRoutes(@RequiredParam.ModulesName String[] names) {
        return NameFormatter.getBasicDomainNameSlots(names);
    }

    @LoopReplacement(id = "importDomainModule")
    public Slot[][] importDomainModule(@RequiredParam.ModulesName String[] names) {
        return NameFormatter.getBasicDomainNameSlots(names);
    }

    @SlotReplacement(id = "AppName")
    public String AppName(@RequiredParam.AppName String name) {
        return name;
    }

}
