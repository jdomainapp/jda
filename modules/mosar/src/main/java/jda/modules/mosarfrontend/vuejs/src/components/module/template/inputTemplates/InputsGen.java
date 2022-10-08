package jda.modules.mosarfrontend.vuejs.src.components.module.template.inputTemplates;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputsGen {
    private static String templateFolder = "src/components/module/template/inputTemplates/";

    private static ArrayList getBasicSlots(DField dField, String moduleName, String type) {
        moduleName = NameFormatter.moduleName(moduleName);
        return new ArrayList(Arrays.asList(
                new Slot("vIfForTyped", type != null ? "v-if=\"" + moduleName + ".type == '" + type + "'\"" : ""),
                new Slot("fieldName", dField.getDAttr().name()),
                new Slot("FieldName", NameFormatter.ModuleName(dField.getDAttr().name())),
                new Slot("moduleName", moduleName),
                new Slot("fieldLabel", dField.getAttributeDesc() != null ? dField.getAttributeDesc().label() : NameFormatter.Module__name(dField.getDAttr().name()))
        ));
    }

    public static Slot[][] linkedDomainSlots(DField[] dFields, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null).toArray(DField[]::new)) {
            ArrayList<Slot> inputFieldSlots = getBasicSlots(dField, moduleName, type);
            String LinkedDomain = dField.getLinkedDomain().getDomainClass().getSimpleName();
            inputFieldSlots.add(new Slot("Linked__domain", NameFormatter.Module__name(LinkedDomain)));
            inputFieldSlots.add(new Slot("LinkedDomain", LinkedDomain));
            inputFieldSlots.add(new Slot("linkedJdomain", NameFormatter.moduleJname(LinkedDomain)));
            inputFieldSlots.add(new Slot("linkedDomain", NameFormatter.moduleName(LinkedDomain)));
            inputFieldSlots.add(new Slot("linkedIdField", dField.getLinkedDomain().getIdField().getDAttr().name()));
            result.add(inputFieldSlots);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

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

    public static String getInputParams(DField dField) {
        StringBuilder params = new StringBuilder("");
        if (dField.getDAttr().id() || dField.getDAttr().auto()) params.append("disabled");
        return params.toString();
    }

    public static String getNormalInputs(DField[] dFields, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : Arrays.stream(dFields).filter(f -> f.getDAssoc() == null && f.getEnumValues() == null).toArray(DField[]::new)) {
            ArrayList<Slot> inputFieldSlots = getBasicSlots(dField, moduleName, type);
            inputFieldSlots.add(new Slot("type", getFieldType(dField.getDAttr().type())));
            inputFieldSlots.add(new Slot("inputParams", getInputParams(dField)));
            result.add(inputFieldSlots);
        }
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "NormalInput.html", "NormalFormInputs", result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new));
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public static String getEnumInputs(DField[] dFields, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<ArrayList<Slot>>();
        for (DField dField : Arrays.stream(dFields).filter(f -> f.getEnumValues() != null).toArray(DField[]::new)) {
            ArrayList<Slot> inputFieldSlots = getBasicSlots(dField, moduleName, type);
            inputFieldSlots.add(new Slot("EnumOptions", getEnumOptions(dField)));
            result.add(inputFieldSlots);
        }
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "EnumInput.html", "EnumInputs", result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new));
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    private static String getEnumOptions(DField dField) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (Enum<?> enumValue : dField.getEnumValues()) {
            result.add(new ArrayList(List.of(
                    new Slot("enumName", enumValue.name())
            )));
        }
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "EnumOptions.html", "EnumOptions", result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getLinkedInputOne2One(DField[] dFields, String moduleName, String type) {
        Slot[][] result = linkedDomainSlots(dFields, moduleName, type);
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "LinkedDomainInputOne2One.html", "linkedDomainInputOne2One", result);
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public static String getLinkedInputOne2Many(DField[] dFields, String moduleName, String type) {
        Slot[][] result = linkedDomainSlots(dFields, moduleName, type);
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "LinkedDomainInputOne2Many.html", "linkedDomainInputOne2Many", result);
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }


}
