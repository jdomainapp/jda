package jda.modules.mosarfrontend.vuejs.templates;

import java.util.ArrayList;
import java.util.Map;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

@FileTemplateDesc(
        templateFile = "/app.component.html"
)
public class AppHtmlTemplate {
    @LoopReplacementDesc(slots = {"title", "module"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("module", moduleName));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}

