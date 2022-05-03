package jda.modules.mosarfrontend.reactnative.templates.src.modules;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;
import java.util.Locale;

@FileTemplateDesc(templateFile = "/src/modules/FormInputs.tsx")
public class FormInputsGen {
    @LoopReplacementDesc(id = "importEnums",slots = {"enumName"})
    public Slot[][] importEnums(@RequiredParam.ModulesName String[] moduleNames){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames ) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("enumName", name));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    };

    @LoopReplacementDesc(id = "importConfigs",slots = {"module_name","module_folder"})
    public Slot[][] importConfigs(@RequiredParam.ModulesName String[] moduleNames){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("module_name", name));
            list.add(new Slot("module_folder", name.toLowerCase(Locale.ROOT)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    };

    @LoopReplacementDesc(id = "exportEnumInputs",slots = {"enumName"})
    public Slot[][] exportEnumInputs(@RequiredParam.ModulesName String[] moduleNames){
        return importEnums(moduleNames);
    };

    @LoopReplacementDesc(id = "exportModuleInputs",slots = {"module_name"})
    public Slot[][] exportModuleInputs(@RequiredParam.ModulesName String[] moduleNames){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("module_name", name));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    };
}
