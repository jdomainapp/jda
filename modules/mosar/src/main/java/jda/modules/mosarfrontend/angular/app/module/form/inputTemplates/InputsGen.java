package jda.modules.mosarfrontend.angular.app.module.form.inputTemplates;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputsGen {
    private static String templateFolder = "src/app/module/form/inputTemplates/";

    private static ArrayList getBasicSlots(DField dField, String moduleName, String type) {
        moduleName = NameFormatter.moduleName(moduleName);
        ArrayList<Slot> list = FieldsUtil.getBasicFieldSlots(dField, moduleName);
        list.addAll(Arrays.asList(
                new Slot("ngIfForType", type != null ? String.format("*ngIf=\"item.type == '%s'\"", type) : ""),
                new Slot("fieldOptions", getFieldOptions(dField.getDAttr()))
        ));
        return list;

    }

    private static String getFieldOptions(DAttr dAttr) {
        StringBuilder fieldOptions = new StringBuilder();
        if (dAttr.id() || !dAttr.mutable() || dAttr.auto())
            fieldOptions.append("disabled ");
        if (!dAttr.optional() && !dAttr.id() && !dAttr.auto()) {
            fieldOptions.append("required ");
        }
        if (!Double.isInfinite(dAttr.max()))
            fieldOptions.append("max=\"" + dAttr.max() + "\" ");
        if (!Double.isInfinite(dAttr.min()))
            fieldOptions.append("min=\"" + dAttr.min() + "\" ");
        if (dAttr.length() > 0)
            fieldOptions.append("maxLength=\"" + dAttr.length() + "\" ");
        return fieldOptions.toString();
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

    public static String getNormalInputs(DField[] dFields, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : Arrays.stream(dFields).filter(f -> f.getDAssoc() == null && f.getEnumValues() == null).toArray(DField[]::new)) {
            ArrayList<Slot> inputFieldSlots = getBasicSlots(dField, moduleName, type);
            inputFieldSlots.add(new Slot("type", getFieldType(dField.getDAttr().type())));
            result.add(inputFieldSlots);
        }
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "NormalInput.html", "NormalFormInputs", MethodUtils.toLoopData(result));
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
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "EnumInput.html", "EnumInputs", MethodUtils.toLoopData(result));
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
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "EnumOptions.html", "EnumOptions", MethodUtils.toLoopData(result));
        } catch (Exception e) {
            return "";
        }
    }

    public static String getLinkedInputs(DField[] dFields, String moduleName, String type) {
        StringBuilder result = new StringBuilder();
        for (DField dField : Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null).toArray(DField[]::new)) {
            if (dField.getDAssoc().ascType() == DAssoc.AssocType.One2One) {
                result.append(getLinkedOneOneInput(dField, moduleName, type));
            } else if (dField.getDAssoc().endType() == DAssoc.AssocEndType.One) {
                result.append(getLinkedOneSideInput(dField, moduleName, type));
            } else if (dField.getDAssoc().endType() == DAssoc.AssocEndType.Many) {
                result.append(getLinkedManySideInput(dField, moduleName, type));
            }
        }
        return result.toString();
    }

    public static String getLinkedManySideInput(DField dField, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<Slot> basicSlots = getBasicSlots(dField, moduleName, type);
        basicSlots.addAll(FieldsUtil.getBasicFieldSlots(dField));
        result.add(basicSlots);

        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "LinkedDomainManySideInput.html", "linkedDomainFormInput", MethodUtils.toLoopData(result));
        } catch (Exception e) {
            System.out.println("----------ERROR GEN LinkedDomainManySideInput.html---------" + e);
            return "";
        }
    }

    public static String getLinkedOneSideInput(DField dField, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<Slot> basicSlots = getBasicSlots(dField, moduleName, type);
        basicSlots.addAll(FieldsUtil.getBasicFieldSlots(dField));
        result.add(basicSlots);
        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "LinkedDomainOneSideInput.html", "linkedDomainFormInput", MethodUtils.toLoopData(result));
        } catch (Exception e) {
            System.out.println("----------ERROR GEN LinkedDomainOneSideInput.html---------" + e);
            return "";
        }
    }

    public static String getLinkedOneOneInput(DField dField, String moduleName, String type) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        ArrayList<Slot> basicSlots = getBasicSlots(dField, moduleName, type);
        basicSlots.addAll(FieldsUtil.getBasicFieldSlots(dField));
        result.add(basicSlots);

        try {
            return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "LinkedDomainOneOneInput.html", "linkedDomainFormInput", MethodUtils.toLoopData(result));
        } catch (Exception e) {
            System.out.println("----------ERROR GEN LinkedDomainOneOneInput.html---------" + e);
            return "";
        }
    }

    public static String getSelectFormTypeInput(String[] types) {
        if (types.length > 0) {
            ArrayList<ArrayList<Slot>> result = new ArrayList<>();
            for (String type : types) {
                result.add(new ArrayList<>(Arrays.asList(
                        new Slot("formType", type)
                )));
            }
            try {
                return FileFactory.replaceLoopWithFileTemplate(InputsGen.templateFolder + "SelectFormTypeInput.html", "typeOptions", MethodUtils.toLoopData(result));
            } catch (Exception e) {
                System.out.println("----------ERROR GEN LinkedDomainOneOneInput.html---------" + e);
                return "";
            }
        } else return "";
    }
}
