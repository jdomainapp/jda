package jda.modules.mosarfrontend.vuejs.common_gen;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFileName;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import org.modeshape.common.text.Inflector;

import java.lang.annotation.Repeatable;
import java.util.ArrayList;

public class FieldsUtil {


    public static Slot[][] getBasicFieldSlots(DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", fieldLabel));
            slotValues.add(new Slot("FieldLabel", Inflector.getInstance().camelCase(fieldName, true)));
            slotValues.add(new Slot("fieldName", fieldName));
            slotValues.add(new Slot("FieldName", Inflector.getInstance().camelCase(fieldName, true)));
            if (field.getLinkedDomain() != null)
                slotValues.add(new Slot("LinkedDomain", DomainNameUtil.ModuleName(field.getLinkedDomain().getDomainClass().getSimpleName())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
