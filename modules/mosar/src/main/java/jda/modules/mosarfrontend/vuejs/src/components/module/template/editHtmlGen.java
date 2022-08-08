package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/form.html"
)
public class editHtmlGen extends addHtmlGen {
    @Override
    public boolean getAddMode(){
        return false;
    }
    @WithFileName
    public String fileName(){
        return "edit";
    }

}
