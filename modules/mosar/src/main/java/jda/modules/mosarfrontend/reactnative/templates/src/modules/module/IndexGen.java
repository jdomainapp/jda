package jda.modules.mosarfrontend.reactnative.templates.src.modules.module;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/Index.ts")
public class IndexGen extends CommonModuleGen {
    @SlotReplacementDesc(slot = "importDataType")
    public String importDataType(@RequiredParam.ModuleName String moduleName, @RequiredParam.MCC NewMCC domain) {
        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
            moduleName = moduleName + ", " + "Sub" + moduleName;
        }
        return moduleName;
    }

    @IfReplacement(id = "haveSubType")
    public boolean haveSubType(@RequiredParam.MCC NewMCC mcc) {
        return !notHaveSubType(mcc);
    }

    @IfReplacement(id = "notHaveSubType")
    public boolean notHaveSubType(@RequiredParam.MCC NewMCC mcc) {
        return mcc.getSubDomains().isEmpty();
    }
}
