package jda.modules.mosarfrontend.vuejs.templates.src.constants;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.vuejs.VueNameUtil;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/constants/message.js"
)
public class messageGen {
    @LoopReplacementDesc(id = "ModuleMessages", slots = {"MODULE_NAME", "module__name"})
    public Slot[][] moduleComponents(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("MODULE_NAME", VueNameUtil.MODULE_NAME(moduleName)));
            slotValues.add(new Slot("module__name", VueNameUtil.module__name(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
