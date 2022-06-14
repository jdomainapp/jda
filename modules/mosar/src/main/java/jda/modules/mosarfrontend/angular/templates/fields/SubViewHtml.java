package jda.modules.mosarfrontend.angular.templates.fields;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/fields/subview.html.tp"
)
public class SubViewHtml {
//    @WithFileName
//    public String getFileName(@RequiredParam.ModuleName String name) {
//        return name;
//    }
//
//    @SlotReplacementDesc(slot = "moduleName")
//    public String moduleName(@RequiredParam.ModuleName String name) {
//        return name;
//    }
//
//    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
//    public Slot[][] fields(@RequiredParam.ModuleFields DField[] fields) {
//        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
//        for (DField field : fields) {
//            ArrayList<Slot> list = new ArrayList<>();
//            list.add(new Slot("field", field.getDAttr().name() + (field.getDAttr().optional() ? "?" : "")));
//            result.add(list);
//        }
//        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
//    }
}
