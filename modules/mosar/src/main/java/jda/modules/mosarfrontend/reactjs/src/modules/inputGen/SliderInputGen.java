package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;

@FileTemplateDesc(templateFile = "/inputTemplates/SliderInput.js")
public class SliderInputGen extends SimpleInputGen {
    @SlotReplacement(id = "min")
    public String min(@RequiredParam.ModuleField DField field){
        return String.valueOf(field.getDAttr().min());
    }

    @SlotReplacement(id = "max")
    public String max(@RequiredParam.ModuleField DField field){
        return String.valueOf(field.getDAttr().max());
    }
}
