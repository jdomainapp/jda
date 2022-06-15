package jda.modules.mosarfrontend.reactnative.templates.src.modules;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/modules/FormInputs.tsx")
public class FormInputsGen {
    @LoopReplacementDesc(id = "importEnums", slots = {"enumName"})
    public Slot[][] importEnums(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            //For each Enum Type
            for (DField dField : Arrays.stream(mcc.getDFields()).filter(f -> f.getEnumName() != null).toArray(DField[]::new)) {
                ArrayList<Slot> listEnum = new ArrayList<>();
                listEnum.add(new Slot("enumName", dField.getEnumName()));
                result.add(listEnum);
            }
        }

        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
    @LoopReplacementDesc(id = "exportEnumInputs", slots = {"enumName"})
    public Slot[][] exportEnumInputs(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        return importEnums(moduleMap);
    }

}
