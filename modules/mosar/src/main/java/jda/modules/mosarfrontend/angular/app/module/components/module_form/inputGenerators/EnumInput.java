package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/inputTemplates/EnumInput.html")

public class EnumInput extends BaseInputGen {
    @LoopReplacement(id = "EnumOptions")
    public Slot[][] enumOptions(@RequiredParam.ModuleField DField dField) {
        if (dField.getEnumValues() == null) return new Slot[][]{};
        return Arrays.stream(dField.getEnumValues()).map(e -> new Slot[]{
                new Slot("enumName", e.name()),
                new Slot("enumValue", e.name())
        }).toArray(Slot[][]::new);
    }
}
