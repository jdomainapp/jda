package jda.modules.mosarfrontend.reactnative.src.modules;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.reactnative.src.modules.module.ModuleConfigGen;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/modules/Modules.tsx")
public class ModulesGen {
    @LoopReplacement(id = "importDomainTypes", slots = {"requiredInterface", "module_name"})
    public Slot[][] importDomainTypes(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleMap.keySet()) {
            ArrayList<Slot> list = new ArrayList<>();
            String domainTypes = new ModuleConfigGen().importDataType(name, moduleMap.get(name));
            list.add(new Slot("requiredInterface", domainTypes));
            list.add(new Slot("module_name", name));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "importDomainConfig", slots = {"module_name", "module_folder"})
    public Slot[][] replace2(@RequiredParam.ModulesName String[] moduleNames) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("module_name", name));
            list.add(new Slot("module_folder", name.toLowerCase(Locale.ROOT)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }


    @LoopReplacement(id = "3", slots = {"module_name"})
    public Slot[][] replace3(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        return importDomainTypes(moduleMap);
    }

}
