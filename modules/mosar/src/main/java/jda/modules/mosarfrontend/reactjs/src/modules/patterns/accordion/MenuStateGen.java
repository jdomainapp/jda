package jda.modules.mosarfrontend.reactjs.src.modules.patterns.accordion;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "/src/modules/patterns/accordion/MenuState.js")
public class MenuStateGen extends NameFormatter {

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return String.format("/src/%s/patterns/accordion", NameFormatter.moduleJnames(name));
    }

    @SlotReplacement(id = "moduleStruct")
    public String moduleStruct() throws Exception {
        ParamsFactory.getInstance().setParentKey(null);
        return (new FileFactory(StructGen.class)).genFile(false);
    }
}
