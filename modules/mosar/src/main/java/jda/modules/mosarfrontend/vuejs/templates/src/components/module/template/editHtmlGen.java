package jda.modules.mosarfrontend.vuejs.templates.src.components.module.template;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.WithFileName;

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
