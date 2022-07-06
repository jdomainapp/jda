package jda.modules.mosarfrontend.angular.templates;

import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/app.component.html"
)
public class AppHtmlTemplate {

    @LoopReplacement(slots = {"api", "title"}, id = "1")
    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            AngularSlotProperty prop = new AngularSlotProperty(mcc);
            slotValues.add(new Slot("api", prop.getAPI()));
            slotValues.add(new Slot("title", prop.getTitle()));
            result.add(slotValues);
        }
//        System.out.println(result.toArray());
        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
