package jda.modules.mosarfrontend.angular.app;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(
        templateFile = "/src/app/app.component.html"
)
public class AppComponentGen {
    @SlotReplacement(id = "AppName")
    public String appName(@RequiredParam.AppName String appName) {
        return appName;
    }


    @LoopReplacement(id = "routerLink")
    public Slot[][] routerLink(@RequiredParam.ModulesName String[] moduleNames) {
        return NameFormatter.getBasicDomainNameSlots(moduleNames);
    }
}
