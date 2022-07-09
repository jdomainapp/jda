package jda.modules.mosarfrontend.common.utils.common_gen;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;

public class FieldsUtil {

    private static String getFieldType(DAttr.Type type) {
        switch (type) {
            case String:
            case StringMasked:
            case Char:
                return "text";
            case Integer:
            case BigInteger:
            case Long:
            case Float:
            case Double:
            case Short:
            case Byte:
            case ByteArraySmall:
            case ByteArrayLarge:
                return "number";
            case Date:
                return "date";
            case Boolean:
            case Domain:
            case Collection:
            case Array:
            case Color:
            case Font:
            case File:
            case Null:
            case Image:
            case Serializable:
            case Other:
                return "";
        }
        return "text";
    }

    public static Slot[][] getBasicFieldSlots(DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : dFields) {
            result.add(getBasicFieldSlots(field));
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public static ArrayList<Slot> getBasicFieldSlots(DField field) {
        return getBasicFieldSlots(field, null);
    }

    public static ArrayList<Slot> getBasicFieldSlots(DField field, String moduleName) {
        ArrayList<Slot> slotValues = new ArrayList<>();
        String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
        String fieldName = field.getDAttr().name();
        slotValues.add(new Slot("fieldLabel", fieldLabel));
        slotValues.add(new Slot("FieldLabel", NameFormatter.ModuleName(fieldLabel)));
        slotValues.add(new Slot("Field__label", NameFormatter.Module__name(fieldLabel)));
        slotValues.add(new Slot("fieldName", NameFormatter.moduleName(fieldName)));
        slotValues.add(new Slot("FieldName", NameFormatter.ModuleName(fieldName)));
        if (field.getLinkedDomain() != null) {
            String LinkedDomain = field.getLinkedDomain().getDomainClass().getSimpleName();
            if (moduleName != null)
                slotValues.addAll(NameFormatter.getBasicDomainNameSlots(moduleName));
            slotValues.addAll(NameFormatter.getBasicDomainNameSlots(LinkedDomain, "LinkedDomain"));
            slotValues.add(new Slot("LinkedDomainIdType", getFieldType(field.getLinkedDomain().getIdField().getDAttr().type())));
            slotValues.add(new Slot("linkedDomainId", field.getLinkedDomain().getIdField().getDAttr().name()));
        }
        return slotValues;
    }
}
