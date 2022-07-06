package jda.modules.mosarfrontend.reactnative.templates.src.data_types.enums;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/data_types/enums/Modules.ts")
public class ModulesGen {
    @LoopReplacement(id = "modules", slots = {"ModuleName"})
    public Slot[][] modules(@RequiredParam.ModulesName String[] ModulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : ModulesName) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("ModuleName", moduleName));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
