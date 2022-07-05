package jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;

@FileTemplateDesc(templateFile = "/src/modules/Submodule.js")
public class SubmoduleGen extends BaseModuleGen {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleField DField field){
        return ModuleName(field.getLinkedDomain().getDomainClass().getSimpleName())+"Submodule";
    }
    @SkipGenDecision
    public boolean skip(@RequiredParam.ModuleField DField field) {
        return field.getLinkedDomain() == null || field.getDAssoc().endType()== DAssoc.AssocEndType.Many;
    }

    @SlotReplacement(slot = "LinkedModule")
    public String LinkedModule(@RequiredParam.ModuleField DField field) {
        return DomainNameUtil.ModuleName(field.getLinkedDomain().getDomainClass().getSimpleName());
    }

    @SlotReplacement(slot = "excludeFields")
    public String excludeFields(@RequiredParam.ModuleField DField field, @RequiredParam.ModuleName String moduleName) {
        StringBuilder exclude = new StringBuilder();
        for (DField dField : field.getLinkedDomain().getDFields()) {
            if (dField.getLinkedDomain() != null && dField.getLinkedDomain().getDomainClass().getSimpleName() == moduleName) {
                exclude.append("\"");
                exclude.append(dField.getDAttr().name());
                exclude.append("\",");
            }
        }
        return exclude.toString();
    }
}
