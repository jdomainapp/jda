package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/list.html"
)
public class listHtmlGen extends ModuleTemplateGenBase {

    @LoopReplacement(id = "fieldLabels", slots = {"fieldLabel"})
    public Slot[][] fieldLabels(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("fieldLabel", field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "fieldNames", slots = {"fieldName"})
    public Slot[][] fieldNames(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("fieldName", dField.getDAttr().name()));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
