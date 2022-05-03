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
        templateFile = "/app.module.ts"
)
public class AppModuleTemplate {

    @LoopReplacementDesc(slots = {"import-main", "import-form"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<Class<?>, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(v);
            slotValues.add(new Slot("import-main", prop.getMainImport()));
            slotValues.add(new Slot("import-form", prop.getFormImport()));
            result.add(slotValues);
        });
        System.out.println(result.toArray());
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacementDesc(slot = "initialRoute")
    public String replaceInitialRoute(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        return "Hello";
    }

    @LoopReplacementDesc(slots = {"decl-main", "decl-form"}, id = "2")
    public Slot[][] replaceRouteModules(@RequiredParam.ModuleMap Map<Class, MCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        moduleMap.forEach((k, v) -> {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(v);
            slotValues.add(new Slot("decl-main", prop.getModuleName() + "Component"));
            slotValues.add(new Slot("decl-form", prop.getModuleName() + "FormComponent"));
            result.add(slotValues);
        });

        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
