package jda.modules.mosarfrontend.angular.templates.src;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/app-routing.module.ts"
)
public class AppRoutingTemplate {

    @LoopReplacementDesc(slots = {"import"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
//      System.out.print(moduleMap);
    	ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            //For each Enum Type
        	ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(mcc);
            slotValues.add(new Slot("import", prop.getMainImport()));
//            slotValues.add(new Slot("route", prop.getMainPath()));
            result.add(slotValues);
        }    	
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

//    @SlotReplacementDesc(slot = "initialRoute")
//    public String replaceInitialRoute(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
//        return "Hello";
//    }

    @LoopReplacementDesc(slots = {"apiName", "moduleComponent"}, id = "2")
    public Slot[][] replaceRouteModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(mcc);        
            slotValues.add(new Slot("apiName", prop.getAPI()));
            slotValues.add(new Slot("moduleComponent", prop.getModuleName() + "Component"));
            result.add(slotValues);
        }

        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
