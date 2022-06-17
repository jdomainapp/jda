package jda.modules.mosarfrontend.reactnative.templates.src;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/Main.tsx"
)
public class MainGen {
    @LoopReplacementDesc(slots = {"ModuleName", "moduleName"}, id = "importModules")
    public Slot[][] replaceImportModules(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("moduleName", Inflector.getInstance().underscore(moduleName)));
            slotValues.add(new Slot("ModuleName", moduleName));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "AppName")
    public String AppName(@RequiredParam.AppName String appName) {
        return appName;
    }

    @SlotReplacementDesc(slot = "initialRoute")
    public String replaceInitialRoute(@RequiredParam.ModulesName String[] modulesName) {
        return modulesName[0];
    }

    @LoopReplacementDesc(slots = {"ModuleName", "ModuleTitle"}, id = "routeConfigs")
    public Slot[][] replaceRouteModules(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("ModuleName", moduleName));
            slotValues.add(new Slot("ModuleTitle", Inflector.getInstance().titleCase(moduleName)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
