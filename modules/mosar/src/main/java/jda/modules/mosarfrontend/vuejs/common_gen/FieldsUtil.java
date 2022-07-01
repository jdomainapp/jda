package jda.modules.mosarfrontend.vuejs.common_gen;

import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

public class FieldsUtil {
    public static Slot[][] getBasicFieldSlots(DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("fieldLabel", field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name())));
            slotValues.add(new Slot("fieldName", field.getDAttr().name()));
            if (field.getLinkedDomain() != null)
                slotValues.add(new Slot("LinkedDomain", DomainNameUtil.ModuleName(field.getLinkedDomain().getDomainClass().getSimpleName())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
