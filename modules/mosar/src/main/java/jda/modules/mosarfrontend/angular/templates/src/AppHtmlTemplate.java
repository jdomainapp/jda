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
import java.util.Locale;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/app.component.html"
)
public class AppHtmlTemplate {

//    @LoopReplacementDesc(slots = {"api", "title"}, id = "1")
//    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
//        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
//        for (NewMCC mcc : moduleMap.values()) {
//            ArrayList<Slot> slotValues = new ArrayList<>();
//            AngularSlotProperty prop = new AngularSlotProperty(mcc);
//            slotValues.add(new Slot("api", prop.getAPI()));
////            slotValues.add(new Slot("title", prop.getTitle()));
//            result.add(slotValues);
//        }
//        System.out.println(result.toArray());
//        return result.stream().map(v-> v.stream().toArray(Slot[]::new)).toArray(Slot[][]::new);
//    }
    
//    @LoopReplacementDesc(slots = {"api"}, id = "2")
//    public Slot[][] replaceImportModules(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
//        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
//        for (NewMCC mcc : moduleMap.values()) {
//            ArrayList<Slot> slotValues = new ArrayList<>();
////            AngularSlotProperty prop = new AngularSlotProperty(mcc);
//            slotValues.add(new Slot("api", "ABC"));
//            result.add(slotValues);
//        }
//        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
//    }
    
    @LoopReplacementDesc(id = "importEnums", slots = {"enumName"})
    public Slot[][] importEnums(@RequiredParam.ModuleMap Map<String, NewMCC> moduleMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (NewMCC mcc : moduleMap.values()) {
            //For each Enum Type
            for (DField dField : Arrays.stream(mcc.getDFields()).filter(f -> f.getEnumName() != null).toArray(DField[]::new)) {
                ArrayList<Slot> listEnum = new ArrayList<>();
                listEnum.add(new Slot("enumName", dField.getEnumName()));
                result.add(listEnum);
            }
        }

        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

}
