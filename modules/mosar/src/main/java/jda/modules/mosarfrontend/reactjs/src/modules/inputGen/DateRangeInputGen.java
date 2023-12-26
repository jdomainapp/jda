package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "/inputTemplates/DateRangeInput.js")
public class DateRangeInputGen extends SimpleInputGen {

    @SlotReplacement(id = "startDate")
    public String startDate(@RequiredParam.ModuleField DField field) {
        return field.getDAttr().name();
    }

    @SlotReplacement(id = "endDate")
    public String endDate(@RequiredParam.ModuleField DField sField, @RequiredParam.ModuleFields DField[] fields) {
        DField[] eFields = Arrays.stream(fields).filter(f -> f.getInputType() == InputTypes.DateRangeEnd && f.getInputID().equals(sField.getInputID())).toArray(DField[]::new);
        if (eFields.length > 0) return eFields[0].getDAttr().name();
        else return "";
    }

    @SlotReplacement(id="rangeID")
    public String rangeID(@RequiredParam.ModuleField DField field){
        return field.getInputID();
    }
}
