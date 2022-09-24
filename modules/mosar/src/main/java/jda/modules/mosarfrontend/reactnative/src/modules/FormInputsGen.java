package jda.modules.mosarfrontend.reactnative.src.modules;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/modules/FormInputs.tsx")
public class FormInputsGen {
    @LoopReplacement(id = "importEnums", slots = {"enumName"})
    public Slot[][] importEnums(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            //For each Enum Type
            for (DField dField : Arrays.stream(mcc.getDFields()).filter(f -> f.getEnumName() != null).toArray(DField[]::new)) {
                ArrayList<Slot> listEnum = new ArrayList<>();
                listEnum.add(new Slot("enumName", dField.getEnumName()));
                result.add(listEnum);
            }
            if (!mcc.getSubDomains().isEmpty()) {
                ArrayList<Slot> listEnum = new ArrayList<>();
                listEnum.add(new Slot("enumName", mcc.getDomainClass().getSimpleName()+"Type"));
                result.add(listEnum);
            }
        }

        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "exportEnumInputs", slots = {"enumName"})
    public Slot[][] exportEnumInputs(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        return importEnums(moduleMap);
    }

}
