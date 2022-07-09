package jda.modules.mosarfrontend.reactnative.templates.src.data_types.enums;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
@FileTemplateDesc(
        templateFile = "/src/data_types/enums/ModuleType.ts"
)
public class ModuleTypeGen {
    @SkipGenDecision
    public boolean skipThisFile(@RequiredParam.MCC NewMCC domain) {
        return domain.getSubDomains().isEmpty();
    }

    @WithFileName
    @SlotReplacement(slot = "enumName")
    public String getFileName(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "Type";
    }

    @LoopReplacement(slots = {"alias", "value"}, id = "enumValues")
    public Slot[][] importInterface(@RequiredParam.MCC NewMCC mcc) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String subType : mcc.getSubDomains().keySet()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("alias", subType));
            list.add(new Slot("value", "\""+subType+"\""));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}