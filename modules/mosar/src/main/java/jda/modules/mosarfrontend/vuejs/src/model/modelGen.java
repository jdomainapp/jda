package jda.modules.mosarfrontend.vuejs.src.model;

import jda.modules.dcsl.syntax.DAssoc;
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

    private DField[] exceptOneSideInMany2OneRelation(DField[] dFields){
        return Arrays.stream(dFields).filter(d-> d.getDAssoc().ascType() == DAssoc.AssocType.One2One || d.getDAssoc().endType() != DAssoc.AssocEndType.One).toArray(DField[]::new);
    }
    @LoopReplacement(id = "importLinkedModel")
    public Slot[][] importLinkedDomain(@RequiredParam.DomainFields DField[] linkedDomains) {
        return ModuleGenBase.LinkedDomain_linked_domain(exceptOneSideInMany2OneRelation(linkedDomains));
    }
    @LoopReplacement(id = "normalFieldParams")
    public Slot[][] normalFieldParams(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() == null || f.getDAssoc().ascType()== DAssoc.AssocType.One2One).toArray(DField[]::new);
        return FieldsUtil.getBasicFieldSlots(fields);
    }

    @LoopReplacement(id = "initNormalFields")
    public Slot[][] initNormalFields(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new);
        return FieldsUtil.getBasicFieldSlots(fields);
    }

    @LoopReplacement(id = "initLinkedOne2ManyFields")
    public Slot[][] initLinkedOne2ManyFields(@RequiredParam.DomainFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f-> f.getDAssoc().ascType()== DAssoc.AssocType.One2Many && f.getDAssoc().endType()== DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }

    @LoopReplacement(id = "initLinkedOne2OneFields")
    public Slot[][] initLinkedOne2OneFields(@RequiredParam.DomainFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f->f.getDAssoc().ascType() == DAssoc.AssocType.One2One).toArray(DField[]::new));
    }
    @LoopReplacement(id = "setLinkedDomain")
    public Slot[][] setLinkedDomain(@RequiredParam.DomainFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(exceptOneSideInMany2OneRelation(dFields));
    }

}
