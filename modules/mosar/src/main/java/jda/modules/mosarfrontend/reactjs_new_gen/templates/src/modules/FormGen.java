package jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/modules/Form.js"
)
public class FormGen extends BaseModuleGen {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String name) {
        return name + "Form";
    }

    @SlotReplacement(slot = "formBase")
    public String formBase(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return subDomains.isEmpty() ? "" : "Base";
    }

    @IfReplacement(id = "haveSubType")
    public boolean haveSubType(@RequiredParam.SubDomains Map<String,Domain> subDomains){
        return !subDomains.isEmpty();
    }
    @IfReplacement(id = "haveSubType2")
    public boolean haveSubType2(@RequiredParam.SubDomains Map<String,Domain> subDomains){
        return !subDomains.isEmpty();
    }

    @LoopReplacement(id = "formInputs")
    public Slot[][] formInputs(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(dFields).filter(f -> f.getDAssoc() == null && f.getEnumValues() == null).toArray(DField[]::new)) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", fieldLabel));
            slotValues.add(new Slot("fieldName", fieldName));
            slotValues.add(new Slot("fieldType", getFieldType(field.getDAttr().type())));
            slotValues.add(new Slot("fieldOptions", getFieldOptions(field.getDAttr())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formTypeInputs")
    public Slot[][] formTypeInputs(@RequiredParam.SubDomains Map<String,Domain> subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomain.keySet()) {
            DField[] dFields = subDomain.get(type).getDFields();
            for (DField field : Arrays.stream(dFields).filter(f -> f.getDAssoc() == null && f.getEnumValues() == null).toArray(DField[]::new)) {
                ArrayList<Slot> slotValues = new ArrayList<>();
                String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
                String fieldName = field.getDAttr().name();
                slotValues.add(new Slot("fieldLabel", fieldLabel));
                slotValues.add(new Slot("type", type));
                slotValues.add(new Slot("fieldName", fieldName));
                slotValues.add(new Slot("fieldType", getFieldType(field.getDAttr().type())));
                slotValues.add(new Slot("fieldOptions", getFieldOptions(field.getDAttr())));
                result.add(slotValues);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    private String getFieldOptions(DAttr dAttr) {
        StringBuilder fieldOptions = new StringBuilder();
        if (dAttr.id() && !dAttr.mutable())
            fieldOptions.append("disabled");
        return fieldOptions.toString();
    }

    private String getFieldType(DAttr.Type type) {
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
        return "";
    }

    @LoopReplacement(id = "formEnumInputs")
    public Slot[][] formEnumInputs(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(dFields).filter(f -> f.getEnumValues() != null).toArray(DField[]::new)) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", fieldLabel));
            slotValues.add(new Slot("fieldName", fieldName));
            slotValues.add(new Slot("enumOptions", renderEnumOption(field.getEnumValues())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formTypeEnumInputs")
    public Slot[][] formTypeEnumInputs(@RequiredParam.SubDomains Map<String,Domain> subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomain.keySet()) {
            DField[] dFields = subDomain.get(type).getDFields();
            for (DField field : Arrays.stream(dFields).filter(f -> f.getEnumValues() != null).toArray(DField[]::new)) {
                ArrayList<Slot> slotValues = new ArrayList<>();
                String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
                String fieldName = field.getDAttr().name();
                slotValues.add(new Slot("fieldLabel", fieldLabel));
                slotValues.add(new Slot("type", type));
                slotValues.add(new Slot("fieldName", fieldName));
                slotValues.add(new Slot("enumOptions", renderEnumOption(field.getEnumValues())));
                result.add(slotValues);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "importLinkedSubmodules")
    public Slot[][] importLinkedSubmodules(@RequiredParam.DomainFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null && f.getDAssoc().endType() != DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }

    @LoopReplacement(id = "formTypeLinkedInputs")
    public Slot[][] importTypeLinkedSubmodules(@RequiredParam.SubDomains Map<String,Domain> subDomains) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            DField[] dFields = Arrays.stream(subDomains.get(type).getDFields()).filter(f->f.getDAssoc()!=null).toArray(DField[]::new);
            for (DField field : dFields) {
                ArrayList<Slot> slotValues = new ArrayList<>();
                String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : DomainNameUtil.Module__name(field.getDAttr().name());
                String fieldName = field.getDAttr().name();
                slotValues.add(new Slot("fieldLabel", fieldLabel));
                slotValues.add(new Slot("fieldName", fieldName));
                slotValues.add(new Slot("type", type));
                if (field.getLinkedDomain() != null){
                    String LinkedDomain = field.getLinkedDomain().getDomainClass().getSimpleName();
                    slotValues.add(new Slot("Linked__domain", DomainNameUtil.Module__name(LinkedDomain)));
                    slotValues.add(new Slot("linkedDomain", DomainNameUtil.moduleName(LinkedDomain)));
                }
                result.add(slotValues);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formLinkedInputs")
    public Slot[][] formLinkedInputs(@RequiredParam.ModuleFields DField[] dFields) {
        return importLinkedSubmodules(dFields);
    }

    @LoopReplacement(id = "moduleTypeOptions")
    public Slot[][] moduleTypeOptions(@RequiredParam.SubDomains Map<String,Domain> subDomains){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("type", DomainNameUtil.moduleName(type)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "typedFormRender")
    public Slot[][] typedFormRender(@RequiredParam.SubDomains Map<String,Domain> subDomains){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("type", DomainNameUtil.moduleName(type)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    private String renderEnumOption(Enum[] enums) {
        StringBuilder enumOptions = new StringBuilder();
        for (Enum anEnum : enums) {
            enumOptions.append("\n          <option value=\"");
            enumOptions.append(anEnum.name());
            enumOptions.append("\">");
            enumOptions.append(anEnum.name());
            enumOptions.append("</option>");
        }
        return enumOptions.toString();
    }
}
