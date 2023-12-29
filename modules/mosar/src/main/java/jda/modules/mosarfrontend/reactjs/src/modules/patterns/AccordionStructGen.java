package jda.modules.mosarfrontend.reactjs.src.modules.patterns;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

@FileTemplateDesc(templateFile = "/src/modules/patterns/AccordionStruct.js")
public class AccordionStructGen extends NameFormatter {

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return String.format("/src/%s/patterns/", NameFormatter.moduleJnames(name));
    }

    @SlotReplacement(id = "moduleStruct")
    public String moduleStruct() throws Exception {
        ParamsFactory.getInstance().setParentKey(null);
        return (new FileFactory(StructGen.class)).genFile(false);
    }
}
