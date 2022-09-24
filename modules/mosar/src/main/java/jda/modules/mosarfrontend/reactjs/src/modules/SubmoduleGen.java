package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SkipGenDecision;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

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

    @SlotReplacement(id = "LinkedModule")
    public String LinkedModule(@RequiredParam.ModuleField DField field) {
        return NameFormatter.ModuleName(field.getLinkedDomain().getDomainClass().getSimpleName());
    }
    @SlotReplacement(id = "linked_modules")
    public String linked_modules(@RequiredParam.ModuleField DField field) {
        return NameFormatter.module_names(field.getLinkedDomain().getDomainClass().getSimpleName());
    }

    @SlotReplacement(id = "excludeFields")
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
