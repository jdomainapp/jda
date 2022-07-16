package jda.modules.mosarfrontend.vuejs.src.model;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.vuejs.src.components.module.ModuleGenBase;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/model/model.js"
)
public class modelGen extends BaseModelGen {
    @LoopReplacement(id = "importLinkedModel")
    public Slot[][] importLinkedDomain(@RequiredParam.DomainFields DField[] linkedDomains) {
        return ModuleGenBase.LinkedDomain_linked_domain(linkedDomains);
    }
    @LoopReplacement(id = "normalFieldParams")
    public Slot[][] normalFieldParams(@RequiredParam.ModuleFields DField[] dFields) {
        return initNormalFields(dFields);
    }

    @LoopReplacement(id = "initNormalFields")
    public Slot[][] initNormalFields(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new);
        return FieldsUtil.getBasicFieldSlots(fields);
    }

    @LoopReplacement(id = "initLinkedFields")
    public Slot[][] initLinkedFields(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new);
        return FieldsUtil.getBasicFieldSlots(fields);
    }
    @LoopReplacement(id = "setLinkedDomain")
    public Slot[][] setLinkedDomain(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() != null).toArray(DField[]::new);
        return FieldsUtil.getBasicFieldSlots(fields);
    }


}
