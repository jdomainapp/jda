package jda.modules.mosarfrontend.reactnative.templates.src;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/Main.tsx"
)
public class MainGen {
    @LoopReplacement(slots = {"ModuleName", "moduleName"}, id = "importModules")
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

    @SlotReplacement(slot = "AppName")
    public String AppName(@RequiredParam.AppName String appName) {
        return appName;
    }

    @SlotReplacement(slot = "initialRoute")
    public String replaceInitialRoute(@RequiredParam.ModulesName String[] modulesName) {
        return modulesName[0];
    }

    @LoopReplacement(slots = {"ModuleName", "ModuleTitle"}, id = "routeConfigs")
    public Slot[][] replaceRouteModules(@RequiredParam.ModulesName String[] modulesName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        Inflector wordUtil = Inflector.getInstance();
        for (String moduleName : modulesName) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("ModuleName", moduleName));
            slotValues.add(new Slot("ModuleTitle", wordUtil.humanize(wordUtil.underscore(wordUtil.pluralize(moduleName)))));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
