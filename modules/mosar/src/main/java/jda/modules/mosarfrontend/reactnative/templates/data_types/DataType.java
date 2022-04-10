package jda.modules.mosarfrontend.reactnative.templates.data_types;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DAttrData;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/data_types/DataType.ts"
)
public class DataType {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
    public Slot[][] fields(@RequiredParam.ModuleFields DAttrData[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DAttrData field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getName()));
            list.add(new Slot("fieldType", field.getType().toString()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
