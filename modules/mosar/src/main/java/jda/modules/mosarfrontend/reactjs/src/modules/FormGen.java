package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.*;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.RegexUtils;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
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

    @SlotReplacement(id = "formBase")
    public String formBase(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return subDomains.isEmpty() ? "" : "Base";
    }

    @IfReplacement(id = "haveSubType")
    public boolean haveSubType(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return !subDomains.isEmpty();
    }

    @IfReplacement(id = "haveSubType2")
    public boolean haveSubType2(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return !subDomains.isEmpty();
    }

    public void addBasicSlotForInput(DField[] dFields, ArrayList<ArrayList<Slot>> result, String type) {
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("fieldLabel", fieldLabel));
            slotValues.add(new Slot("type", type));
            slotValues.add(new Slot("fieldName", fieldName));
            slotValues.add(new Slot("fieldType", getFieldType(field.getDAttr().type())));
            if (field.getEnumValues() != null)
                slotValues.add(new Slot("enumOptions", renderEnumOption(field.getEnumValues())));
            slotValues.add(new Slot("fieldOptions", getFieldOptions(field.getDAttr())));
            result.add(slotValues);
        }
    }

    @LoopReplacement(id = "formInputs")
    public Slot[][] formInputs(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new);
        addBasicSlotForInput(fields, result, null);
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formTypeInputs")
    public Slot[][] formTypeInputs(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomain.keySet()) {
            DField[] dFields = Arrays.stream(subDomain.get(type).getDFields()).filter(f -> f.getDAssoc() == null).toArray(DField[]::new);
            addBasicSlotForInput(dFields, result, type);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    private String getFieldOptions(DAttr dAttr) {
        StringBuilder fieldOptions = new StringBuilder();
        if (dAttr.id() || !dAttr.mutable() || dAttr.auto())
            fieldOptions.append("disabled ");
        if (!dAttr.optional() && !dAttr.id() && !dAttr.auto()) {
            fieldOptions.append("required ");
        }
        if (!Double.isInfinite(dAttr.max()))
            fieldOptions.append("max={" + dAttr.max() + "} ");
        if (!Double.isInfinite(dAttr.min()))
            fieldOptions.append("min={" + dAttr.min() + "} ");
        if (dAttr.length() > 0)
            fieldOptions.append("maxLength={" + dAttr.length() + "} ");
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
        return "text";
    }

    @LoopReplacement(id = "formEnumInputs")
    public Slot[][] formEnumInputs(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        addBasicSlotForInput(Arrays.stream(dFields).filter(f -> f.getEnumValues() != null).toArray(DField[]::new), result, null);
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formTypeEnumInputs")
    public Slot[][] formTypeEnumInputs(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomain.keySet()) {
            DField[] dFields = Arrays.stream(subDomain.get(type).getDFields()).filter(f -> f.getEnumValues() != null).toArray(DField[]::new);
            addBasicSlotForInput(dFields, result, type);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "importLinkedSubmodules")
    public Slot[][] importLinkedSubmodules(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null && f.getDAssoc().endType() != DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }

    @LoopReplacement(id = "formTypeLinkedInputs")
    public Slot[][] importTypeLinkedSubmodules(@RequiredParam.SubDomains Map<String, Domain> subDomains, @RequiredParam.ModuleName String name) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            DField[] dFields = Arrays.stream(subDomains.get(type).getDFields()).filter(f -> f.getDAssoc() != null).toArray(DField[]::new);
            addLinkedInputSlots(dFields, result, type, name);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "formLinkedInputs")
    public Slot[][] formLinkedInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String name) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new);
        addLinkedInputSlots(fields, result, null, name);
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    public void addLinkedInputSlots(DField[] dFields, ArrayList<ArrayList<Slot>> result, String type, String ModuleName) {
        for (DField field : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String fieldLabel = field.getAttributeDesc() != null ? field.getAttributeDesc().label() : NameFormatter.Module__name(field.getDAttr().name());
            String fieldName = field.getDAttr().name();
            slotValues.add(new Slot("AssocWithSideOne", renderInputForSideOne(field, ModuleName)));
            slotValues.add(new Slot("AssocWithSideMany", renderInputForSideMany(field, ModuleName)));
            slotValues.add(new Slot("type", type));
            result.add(slotValues);
        }
    }

    private String renderInputForSideMany(DField field, String ModuleName) {
        if (field.getDAssoc().ascType() == DAssoc.AssocType.One2One || field.getDAssoc().endType() == DAssoc.AssocEndType.Many)
            return "";
        String template = "{this.props.excludes && this.props.excludes.includes(\"@slot{{moduleJnames}}\") ? \"\" : <>\n" +
                "        <@slot{{LinkedDomain}}Submodule\n" +
                "          mode='submodule'\n" +
                "          viewType={this.props.viewType}\n" +
                "          title=\"Form: @slot{{LinkedDomain}}\"\n" +
                "          current={this.props.current.@slot{{fieldName}}}\n" +
                "          thisNamePlural='@slot{{fieldName}}' parentName='@slot{{moduleJnames}}' parent='@slot{{moduleJnames}}'\n" +
                "          parentId={this.props.currentId}\n" +
                "          parentAPI={this.props.mainAPI}\n" +
                "          partialApplyWithCallbacks={this.partialApplyWithCallbacks} /></>}";
        String LinkedDomain = field.getLinkedDomain().getDomainClass().getSimpleName();
        template = RegexUtils.createSlotRegex("LinkedDomain").matcher(template).replaceAll(LinkedDomain);
        template = RegexUtils.createSlotRegex("moduleJnames").matcher(template).replaceAll(NameFormatter.moduleJnames(ModuleName));
        template = RegexUtils.createSlotRegex("linkedField").matcher(template).replaceAll(Arrays.stream(field.getLinkedDomain().getDFields()).filter(f -> f.getLinkedDomain() != null && f.getLinkedDomain().getDomainClass().getSimpleName() == ModuleName).toArray(DField[]::new)[0].getDAttr().name());
        template = RegexUtils.createSlotRegex("fieldName").matcher(template).replaceAll(field.getDAttr().name());
        return template;
    }

    String renderInputForSideOne(DField field, String Modulename) {
        if (field.getDAssoc().ascType() != DAssoc.AssocType.One2One && field.getDAssoc().endType() == DAssoc.AssocEndType.One)
            return "";
        String template = "{this.props.excludes && this.props.excludes.includes(\"@slot{{fieldName}}\") ? \"\" : <>\n" +
                "        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>\n" +
                "          @slot{{renderInputByID}}" +
                "          @slot{{renderCompactSubmoduleView}}\n" +
                "        </FormGroup></>}";
        template = RegexUtils.createSlotRegex("renderInputByID").matcher(template).replaceAll(renderInputByID(field));
        template = RegexUtils.createSlotRegex("fieldName").matcher(template).replaceAll(field.getDAttr().name());
        template = RegexUtils.createSlotRegex("renderCompactSubmoduleView").matcher(template).replaceAll(renderCompactSubmoduleView(field, Modulename));
        return template;
    }

    String renderInputByID(DField field) {
        if (field.getDAssoc().ascType() == DAssoc.AssocType.One2Many && field.getDAssoc().endType() == DAssoc.AssocEndType.One)
            return "";
        String template = "<Col md={2.5} className='px-0'>\n" +
                "            <Form.Label>@slot{{LinkedDomain}} ID</Form.Label>\n" +
                "            <FormControl type=\"@slot{{linkedIdType}}\" value={this.renderObject(\"current.@slot{{fieldName}}Id\")} onChange={(e) => this.props.handleStateChange(\"current.@slot{{fieldName}}Id\", e.target.value, true)} />\n" +
                "          </Col>\n" +
                "          <Col md={@slot{{colSize}}} className='px-0'>\n" +
                "            <Form.Label>@slot{{Linked__domain}}</Form.Label>\n" +
                "            <FormControl type=\"text\" value={this.renderObject(\"current.@slot{{fieldName}}\")} onChange={(e) => this.props.handleStateChange(\"current.@slot{{fieldName}}\", e.target.value, false)} disabled />\n" +
                "          </Col>";
        String LinkedDomain = field.getLinkedDomain().getDomainClass().getSimpleName();
        template = RegexUtils.createSlotRegex("LinkedDomain").matcher(template).replaceAll(LinkedDomain);
        template = RegexUtils.createSlotRegex("Linked__domain").matcher(template).replaceAll(NameFormatter.Module__name(LinkedDomain));
        template = RegexUtils.createSlotRegex("linkedIdType").matcher(template).replaceAll(getFieldType(field.getLinkedDomain().getIdField().getDAttr().type()));
        template = RegexUtils.createSlotRegex("fieldName").matcher(template).replaceAll(field.getDAttr().name());
        template = RegexUtils.createSlotRegex("colSize").matcher(template).replaceAll(field.getDAssoc().ascType() != DAssoc.AssocType.One2One ? "9" : "7");
        return template;
    }

    String renderCompactSubmoduleView(DField field, String ModuleName) {
        if (field.getDAssoc().ascType() != DAssoc.AssocType.One2One) return "";
        String template = "<@slot{{LinkedDomain}}Submodule compact={true} mode='submodule'\n" +
                "            viewType={this.props.viewType}\n" +
                "            title=\"Form: @slot{{LinkedDomain}}\"\n" +
                "            current={this.props.current.@slot{{fieldName}}}\n" +
                "            currentId={this.props.current.@slot{{fieldName}}?.@slot{{fieldName}}Id}\n" +
                "            parentName='@slot{{linkedField}}' parent={this.props.current}\n" +
                "            parentId={this.props.currentId}\n" +
                "            parentAPI={this.props.mainAPI}\n" +
                "            partialApplyWithCallbacks={this.partialApplyWithCallbacks}\n" +
                "            handleUnlink={() =>\n" +
                "              this.props.handleStateChange(\"current.@slot{{fieldName}}\", null, false,\n" +
                "                this.props.handleStateChange(\"current.@slot{{fieldName}}Id\", \"\"))} />";
        template = RegexUtils.createSlotRegex("LinkedDomain").matcher(template).replaceAll(field.getLinkedDomain().getDomainClass().getSimpleName());
        template = RegexUtils.createSlotRegex("linkedField").matcher(template).replaceAll(Arrays.stream(field.getLinkedDomain().getDFields()).filter(f -> f.getLinkedDomain() != null && f.getLinkedDomain().getDomainClass().getSimpleName() == ModuleName).toArray(DField[]::new)[0].getDAttr().name());
        template = RegexUtils.createSlotRegex("fieldName").matcher(template).replaceAll(field.getDAttr().name());
        return template;
    }

    @LoopReplacement(id = "moduleTypeOptions")
    public Slot[][] moduleTypeOptions(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("type", NameFormatter.moduleName(type)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "typedFormRender")
    public Slot[][] typedFormRender(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("type", NameFormatter.moduleName(type)));
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
