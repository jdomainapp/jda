package jda.modules.mosarfrontend.reactnative.templates;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/Main.tsx"
)
public class MainTemplate {

    @LoopReplacementDesc(slots = {"moduleName"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<Class<?>, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("moduleName", v.getName()));
            result.add(slotValues);
        });
        System.out.println(result.toArray());
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "initialRoute")
    public String replaceInitialRoute(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        return "Hello";
    }

    @LoopReplacementDesc(slots = {"moduleComponent", "moduleName"}, id = "2")
    public Slot[][] replaceRouteModules(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("moduleComponent", v.getName()));
            slotValues.add(new Slot("moduleName", v.getName()));
            result.add(slotValues);
        });

        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
