package jda.modules.mosarfrontend.vuejs.src.model.form;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

@FileTemplateDesc(
        templateFile = "/src/model/form/form.js"
)
public class formGen extends NameFormatter {
    @LoopReplacement(id = "initHid")
    public Slot[][] initHid(@RequiredParam.ModuleFields DField[] dFields){
        return FieldsUtil.getBasicFieldSlots(dFields);
    }

    @LoopReplacement(id="setHidMethods")
    public Slot[][] setHidMethods(@RequiredParam.ModuleFields DField[] dFields){
        return FieldsUtil.getBasicFieldSlots(dFields);
    }

    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String name){
        return NameFormatter.module_name(name);
    }
}
