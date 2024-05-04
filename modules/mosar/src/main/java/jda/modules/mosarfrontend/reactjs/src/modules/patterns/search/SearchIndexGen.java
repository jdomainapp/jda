package jda.modules.mosarfrontend.reactjs.src.modules.patterns.search;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "/src/modules/patterns/search/index.js")
public class SearchIndexGen extends NameFormatter {
    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return String.format("/src/%s/patterns/search", NameFormatter.moduleJnames(name));
    }
}
