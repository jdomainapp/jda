package jda.modules.mosarfrontend.reactnative.templates.src.modules;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;
import java.util.Locale;

@FileTemplateDesc(templateFile = "/src/modules/Modules.tsx")
public class ModulesGen {
    @LoopReplacementDesc(id = "1", slots = {"module_name"})
    public Slot[][] replace1(@RequiredParam.ModulesName String[] moduleNames) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("module_name", name));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
    @LoopReplacementDesc(id = "2", slots = {"module_name"})
    public Slot[][] replace2(@RequiredParam.ModulesName String[] moduleNames){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String name : moduleNames) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("module_name", name));
            list.add(new Slot("module_folder", name.toLowerCase(Locale.ROOT)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
    @LoopReplacementDesc(id = "3", slots = {"module_name"})
    public Slot[][] replace3(@RequiredParam.ModulesName String[] moduleNames){
        return replace1(moduleNames);
    }

}
