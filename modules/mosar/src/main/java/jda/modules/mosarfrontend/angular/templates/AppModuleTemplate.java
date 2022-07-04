package jda.modules.mosarfrontend.angular.templates;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;

//@FileTemplateDesc(
//        templateFile = "/src/app.module.ts"
//)
//public class AppModuleTemplate {
//    @LoopReplacementDesc(slots = {"moduleName"}, id = "1")
//    public Slot[][] replaceImportModules(@RequiredParam.ModulesName String[] modulesName) {
//        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
//        for (String moduleName : modulesName) {
//            ArrayList<Slot> slotValues = new ArrayList<>();
//            slotValues.add(new Slot("moduleName", moduleName));
//            result.add(slotValues);
//        }
//        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
//    }
//
//    @SlotReplacementDesc(slot = "initialRoute")
//    public String replaceInitialRoute(@RequiredParam.ModulesName String[] modulesName) {
//        return modulesName[0];
//    }
//
//    @LoopReplacementDesc(slots = {"moduleName"}, id = "2")
//    public Slot[][] replaceRouteModules(@RequiredParam.ModulesName String[] modulesName) {
//        return replaceImportModules(modulesName);
//    }
//}

//package jda.modules.mosarfrontend.angular.templates;
//

import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.Map;

@FileTemplateDesc(
        templateFile = "/app.module.ts"
)
public class AppModuleTemplate {
    
    @LoopReplacementDesc(slots = {"import-main", "import-form"}, id = "import")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(mcc);
            slotValues.add(new Slot("import-main", prop.getMainImport()));
            slotValues.add(new Slot("import-form", prop.getFormImportFull()));
            result.add(slotValues);
        }
//        System.out.println(result.toArray());
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }    

    @LoopReplacementDesc(slots = {"decl-main", "decl-form"}, id = "declare")
    public Slot[][] replaceRouteModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(mcc);
            slotValues.add(new Slot("decl-main", prop.getModuleName() + "Component"));
            slotValues.add(new Slot("decl-form", prop.getModuleName() + "FormComponent"));
            result.add(slotValues);
        }

        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
