package jda.modules.mosarfrontend.vuejs.templates.src.layouts;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.vuejs.VueNameUtil;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/layouts/header.html"
)
public class headerGen {
    @LoopReplacementDesc(id = "routerLinks", slots = {"Module__name", "module_name"})
    public Slot[][] moduleComponents(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("Module__name", VueNameUtil.Module__name(moduleName)));
            slotValues.add(new Slot("module_name", VueNameUtil.module_name(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "AppName")
    public String appName(@RequiredParam.AppName String appName) {
        return appName;
    }
}
