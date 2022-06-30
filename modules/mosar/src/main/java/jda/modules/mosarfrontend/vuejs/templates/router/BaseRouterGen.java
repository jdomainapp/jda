package jda.modules.mosarfrontend.vuejs.templates.router;

import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.vuejs.VueNameUtil;

import java.util.ArrayList;

public class BaseRouterGen {
    @LoopReplacementDesc(id = "MODULE_NAME&module_name", slots = {"MODULE_NAME", "module_name"})
    public Slot[][] moduleComponents(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("MODULE_NAME", VueNameUtil.MODULE_NAME(moduleName)));
            slotValues.add(new Slot("module_name", VueNameUtil.module_name(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public Slot[][] MODULE_NAME(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("MODULE_NAME", VueNameUtil.MODULE_NAME(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
