package jda.modules.mosarfrontend.angular_new_gen.templates.src.app;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
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
