package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/modules/module/Index.ts")
public class IndexGen extends CommonModuleGen {
    @SlotReplacementDesc(slot = "importDataType")
    public String importDataType(@RequiredParam.ModuleName String moduleName, @RequiredParam.MCC NewMCC domain) {
        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
            moduleName = moduleName + ", " + "Sub" + moduleName;
        }
        return moduleName;
    }
    @LoopReplacementDesc(id = "formTypeItem", slots = {"EnumType", "type", "SubModuleName"})
    public Slot[][] formTypeItem(@RequiredParam.ModuleName String moduleName, @RequiredParam.SubDomains Map<String, Domain> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : moduleMap.keySet()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("EnumType", moduleName));
            list.add(new Slot("type", type));
            list.add(new Slot("SubModuleName", moduleMap.get(type).getDomainClass().getSimpleName()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "importSubModuleConfig", slots = {"SubModuleName", "submoduleFolder"})
    public Slot[][] importSubModuleConfig(@RequiredParam.SubDomains Map<String, Domain> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (Domain type : moduleMap.values()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("SubModuleName", type.getDomainClass().getSimpleName()));
            list.add(new Slot("submoduleFolder", moduleName(type.getDomainClass().getSimpleName())));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
    @IfReplacement(id = "haveSubType")
    public boolean haveSubType(@RequiredParam.MCC NewMCC mcc) {
        return !notHaveSubType(mcc);
    }

    @IfReplacement(id = "notHaveSubType")
    public boolean notHaveSubType(@RequiredParam.MCC NewMCC mcc) {
        return mcc.getSubDomains().isEmpty();
    }
}
