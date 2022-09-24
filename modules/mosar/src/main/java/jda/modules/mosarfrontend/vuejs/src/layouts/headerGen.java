package jda.modules.mosarfrontend.vuejs.src.layouts;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/layouts/header.html"
)
public class headerGen {
    @LoopReplacement(id = "routerLinks", slots = {"Module__name", "moduleJname"})
    public Slot[][] moduleComponents(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("Module__name", NameFormatter.Module__names(moduleName)));
            slotValues.add(new Slot("moduleJname", NameFormatter.moduleJname(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacement(id = "AppName")
    public String appName(@RequiredParam.AppName String appName) {
        return appName;
    }
}
