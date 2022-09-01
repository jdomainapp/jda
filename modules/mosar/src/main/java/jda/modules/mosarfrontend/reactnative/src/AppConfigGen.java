package jda.modules.mosarfrontend.reactnative.src;

import jda.modules.mosar.config.RFSGenConfig;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.sccl.syntax.SystemDesc;

@FileTemplateDesc(templateFile = "/src/AppConfig.ts")
public class AppConfigGen {
    @SlotReplacement(id = "server_port")
    public String server_port(@RequiredParam.RFSGenConfig RFSGenConfig systemDesc){
        return String.valueOf(systemDesc.getBeServerPort());
    }
}
