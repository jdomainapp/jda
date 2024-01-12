package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.*;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.reactjs.src.modules.inputGen.FormInputsGen;
import jda.modules.mosarfrontend.reactjs.src.modules.inputGen.TypeSelectGen;

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

    @IfReplacement(ids = {"hasDateRange", "hasDateRange2", "hasDateRange3"})
    public boolean hasDateRange(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeStart).toArray(DField[]::new).length > 0;
    }

    @IfReplacement(ids = {"hasSubType", "hasSubType2", "hasSubType3"})
    public boolean hasSubType(@RequiredParam.MCC NewMCC mcc) {
        return mcc.getSubDomains().size() > 0;
    }

    @IfReplacement(id="hasTextAreaInput")
    public boolean hasTextAreaInput(@RequiredParam.ModuleFields DField[] fields){
        return Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.TextArea).toArray(DField[]::new).length > 0;
    }

    @LoopReplacement(id = "subTypeForms")
    public Slot[][] subTypeForms(@RequiredParam.SubDomains Map<String, Domain> subDomains, @RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        int skipCount = 0;
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("subtype", NameFormatter.moduleName(type)));
            slotValues.add(new Slot("skipCount", String.valueOf(skipCount)));
            skipCount += subDomains.get(type).getDFields().length;
            ParamsFactory.getInstance().setModuleFields(subDomains.get(type).getDFields());
            slotValues.add(new Slot("subTypeFormItems", (new FileFactory(FormInputsGen.class)).genFile(false)));
            ParamsFactory.getInstance().setModuleFields(fields);
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "validations")
    public Slot[][] validations(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        DField[] hasRegexFields = Arrays.stream(fields).filter(f -> f.getAttributeDesc() != null && f.getAttributeDesc().jsValidation().regex().length() > 0).toArray(DField[]::new);
        for (DField field : hasRegexFields) {
            System.out.println("ID" + field.getInputID());
            ArrayList<Slot> slotValues = FieldsUtil.getBasicFieldSlots(field);
            if (field.getAttributeDesc() != null) {
                slotValues.add(new Slot("regex", field.getAttributeDesc().jsValidation().regex()));
                slotValues.add(new Slot("validMsg", field.getAttributeDesc().jsValidation().validMsg()));
                slotValues.add(new Slot("invalidMsg", field.getAttributeDesc().jsValidation().invalidMsg()));
            }

            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(ids = {"dateRangeStates", "rangeIDMap","dateRangeSelectHandler"})
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

    @SlotReplacement(id = "typeSelector")
    public String typeSelector(@RequiredParam.MCC NewMCC mcc) {
        if (!mcc.getSubDomains().isEmpty()) {
            return (new FileFactory(TypeSelectGen.class)).genFile(false);
        }
        return "";
    }


    @SlotReplacement(id="formInputs")
    public String formInputs(){
        return new FileFactory(FormInputsGen.class).genFile(false);
    }

    @LoopReplacement(id = "importLinkedSubmodules")
    public Slot[][] importLinkedSubmodules(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null && f.getDAssoc().endType() != DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }


}
