package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.*;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.RegexUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.reactjs.src.modules.inputGen.*;
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
    @IfReplacement(id = "hasDateRange")
    public boolean hasDateRange(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeStart).toArray(DField[]::new).length > 0;
    }

    @IfReplacement(id = "hasDateRange2")
    public boolean hasDateRange2(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeStart).toArray(DField[]::new).length > 0;
    }

    @IfReplacement(id = "hasDateRange3")
    public boolean hasDateRange3(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeStart).toArray(DField[]::new).length > 0;
    }


    @LoopReplacement(id = "validations")
    public Slot[][] validations(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        DField[] hasRegexFields = Arrays.stream(fields).filter(f -> f.getAttributeDesc() != null && f.getAttributeDesc().jsValidation().regex().length() > 0).toArray(DField[]::new);
        for (DField field : hasRegexFields) {
            System.out.println("ID" + field.getInputID());
            ArrayList<Slot> slotValues = FieldsUtil.getBasicFieldSlots(field);
            if(field.getAttributeDesc()!=null){
                slotValues.add(new Slot("regex", field.getAttributeDesc().jsValidation().regex()));
                slotValues.add(new Slot("validMsg", field.getAttributeDesc().jsValidation().validMsg()));
                slotValues.add(new Slot("invalidMsg", field.getAttributeDesc().jsValidation().invalidMsg()));
            }

            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(ids = {"dateRangeStates", "rangeIDMap"})
    public Slot[][] dateRangeStates(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        DField[] startFields = Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeStart).toArray(DField[]::new);
        for (DField sField : startFields) {
            System.out.println("ID" + sField.getInputID());
            ArrayList<Slot> slotValues = new ArrayList<>();
            DField[] eFields = Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeEnd && f.getInputID().equals(sField.getInputID())).toArray(DField[]::new);
            DField eField = eFields.length > 0 ? eFields[0] : null;
            System.out.println(eFields.length);
            if (eField == null) break;
            slotValues.add(new Slot("startField", sField.getDAttr().name()));
            slotValues.add(new Slot("endField", eField.getDAttr().name()));
            slotValues.add(new Slot("rangeID", sField.getInputID()));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
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
            slotValues.add(new Slot("fieldOptions", getFieldOptions(field.getDAttr())));
            result.add(slotValues);
        }
    }

    @SlotReplacement(id = "typeSelector")
    public String typeSelector(@RequiredParam.MCC NewMCC mcc){
        if(!mcc.getSubDomains().isEmpty()){
            return (new FileFactory(TypeSelectGen.class)).genFile(false);
        }
        return "";
    }
    @LoopReplacement(id = "formInputs")
    public Slot[][] formInputs( @RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();

        for (DField field : dFields) {
            ParamsFactory.getInstance().setCurrentModuleField(field);
            ArrayList<Slot> slotValues = new ArrayList<>();
            String inputCode = "";
            try {
                if (field.getAttributeDesc() != null && field.getAttributeDesc().inputType() != InputTypes.Undefined) {
                    System.out.println("Specific Input");
                    switch (field.getAttributeDesc().inputType()) {
                        case Rating:
                            inputCode = (new FileFactory(RatingInputGen.class)).genFile(false);
                            break;
                        case DateRangeStart:
                            inputCode = (new FileFactory(DateRangeInputGen.class)).genFile(false);
                            break;
                        case Slider:
                            inputCode = (new FileFactory(SliderInputGen.class)).genFile(false);
                            break;
                        case TextArea:
                            inputCode = (new FileFactory(TextAreaInputGen.class)).genFile(false);
                            break;
                    }
                } else if (field.getEnumValues() != null) {
                    inputCode = (new FileFactory(EnumInputGen.class)).genFile(false);
                } else if (field.getDAssoc() != null) {
                    if (field.getDAssoc().ascType() == DAssoc.AssocType.One2One)
                        inputCode = (new FileFactory(OneOneInputGen.class)).genFile(false);
                    else {
                        if (field.getDAssoc().endType() == DAssoc.AssocEndType.One)
                            inputCode = (new FileFactory(OneSideInputGen.class)).genFile(false);
                        else inputCode = (new FileFactory(ManySideInputGen.class)).genFile(false);
                    }

                } else {
                    inputCode = (new FileFactory(SimpleInputGen.class)).genFile(false);
                }
            } catch (Exception e) {
            }
            slotValues.add(new Slot("inputCode", inputCode));
            result.add(slotValues);
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


    @LoopReplacement(id = "importLinkedSubmodules")
    public Slot[][] importLinkedSubmodules(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null && f.getDAssoc().endType() != DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }


}
