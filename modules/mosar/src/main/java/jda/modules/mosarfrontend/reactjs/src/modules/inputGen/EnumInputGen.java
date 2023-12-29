package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;

@FileTemplateDesc(templateFile = "/inputTemplates/EnumInput.js")

public class EnumInputGen extends SimpleInputGen {
    @SlotReplacement(id = "enumOptions")
    public String enumOptions(@RequiredParam.ModuleField DField field){
        return renderEnumOption(field.getEnumValues());
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
