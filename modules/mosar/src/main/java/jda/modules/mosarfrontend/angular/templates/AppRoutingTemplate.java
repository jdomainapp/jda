package jda.modules.mosarfrontend.angular.templates;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/app-routing.module.ts"
)
public class AppRoutingTemplate {

    @LoopReplacementDesc(slots = {"import"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<Class<?>, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(v);
            slotValues.add(new Slot("import", prop.getMainImport()));
//            slotValues.add(new Slot("route", prop.getMainPath()));
            result.add(slotValues);
        });
//        System.out.println(result.toArray());
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "initialRoute")
    public String replaceInitialRoute(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        return "Hello";
    }

    @LoopReplacementDesc(slots = {"apiName", "moduleComponent"}, id = "2")
    public Slot[][] replaceRouteModules(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(v);        
            slotValues.add(new Slot("apiName", prop.getAPI()));
            slotValues.add(new Slot("moduleComponent", prop.getModuleName() + "Component"));
            result.add(slotValues);
        });

        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
