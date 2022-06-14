package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/ListConfig.ts")
public class ListConfigGen {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/modules/" + moduleName.toLowerCase();
    }

    @SlotReplacementDesc(slot = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacementDesc(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName.toLowerCase();
    }

    @LoopReplacementDesc(id = "listTitle", slots = {"moduleAlias", "fieldName"})
    public Slot[][] listTitle(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleAlias", moduleName.toLowerCase()));
            list.add(new Slot("fieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

}
