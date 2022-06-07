package jda.modules.mosarfrontend.reactnative.templates.src.data_types;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/data_types/EnumType.ts"
)
public class EnumTypeGen {
    @SkipGenDecision
    public boolean skipthisFile(@RequiredParam.ModuleField DField field) {
        // if domain not use enum, skip gen this file
        return field.getEnumName() == null;
    }

    @WithFileName
    public String getFileName(@RequiredParam.ModuleField DField field) {
        return field.getEnumName();
    }

    @LoopReplacementDesc(slots = {"alias", "value"}, id = "enumValues")
    public Slot[][] importInterface(@RequiredParam.ModuleField DField field) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (Enum<?> enumValue : field.getEnumValues()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("alias", enumValue.name()));
            list.add(new Slot("value", String.valueOf(enumValue.ordinal())));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "enumName")
    public String moduleName(@RequiredParam.ModuleField DField field) {
        return field.getEnumName();
    }
}
