package jda.modules.mosarfrontend.common.utils.common_gen;

import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

public class FieldsUtil {


    public static Slot[][] getBasicFieldSlots(DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", DomainNameUtil.moduleName(fieldLabel)));
            slotValues.add(new Slot("FieldLabel", DomainNameUtil.ModuleName(fieldLabel)));
            slotValues.add(new Slot("Field__label", DomainNameUtil.Module__name(fieldLabel)));
            slotValues.add(new Slot("fieldName", DomainNameUtil.moduleName(fieldName)));
            slotValues.add(new Slot("FieldName", DomainNameUtil.ModuleName(fieldName)));
            if (field.getLinkedDomain() != null){
                String LinkedDomain = field.getLinkedDomain().getDomainClass().getSimpleName();
                slotValues.add(new Slot("LinkedDomain", DomainNameUtil.ModuleName(LinkedDomain)));
                slotValues.add(new Slot("Linked__domain", DomainNameUtil.Module__name(LinkedDomain)));
                slotValues.add(new Slot("linkedDomain", DomainNameUtil.moduleName(LinkedDomain)));
            }
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
