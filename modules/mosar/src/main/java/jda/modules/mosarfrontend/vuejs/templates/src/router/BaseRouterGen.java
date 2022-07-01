package jda.modules.mosarfrontend.vuejs.templates.src.router;

import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.vuejs.common_gen.DomainNameUtil;

import java.util.ArrayList;

public class BaseRouterGen {
    @LoopReplacement(id = "MODULE_NAME&moduleJname", slots = {"MODULE_NAME", "moduleJname"})
    public Slot[][] moduleComponents(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("MODULE_NAME", DomainNameUtil.MODULE_NAME(moduleName)));
            slotValues.add(new Slot("moduleJname", DomainNameUtil.moduleJname(moduleName)));
            slotValues.add(new Slot("module_name", DomainNameUtil.module_name(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public Slot[][] MODULE_NAME(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("MODULE_NAME", DomainNameUtil.MODULE_NAME(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
