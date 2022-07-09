package jda.modules.mosarfrontend.angular_new_gen.templates.src.app.module.form;

import jda.modules.mosarfrontend.angular_new_gen.templates.src.app.module.form.inputTemplates.InputsGen;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(
        templateFile = "/src/app/module/form/module-form.component.html"
)
public class formComponentHtmlGen extends BaseFormGen {
    @SlotReplacement(id = "normalInputs")
    public String normalInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getNormalInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "enumInputs")
    public String enumInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getEnumInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "linkedInputs")
    public String linkedInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getLinkedInputs(dFields, ModuleName, null);
    }

}
