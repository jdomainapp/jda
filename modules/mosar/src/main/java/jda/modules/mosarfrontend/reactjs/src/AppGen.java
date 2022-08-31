package jda.modules.mosarfrontend.reactjs.src;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
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
