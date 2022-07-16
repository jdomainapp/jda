package jda.modules.mosarfrontend.vuejs.src.model;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

public class BaseModelGen extends NameFormatter {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String name) {
        return NameFormatter.module_name(name);
    }
}
