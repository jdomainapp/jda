package jda.modules.mosarfrontend.vuejs.src.model;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.vuejs.src.Common;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/model/model.js"
)
public class modelGen extends BaseModelGen {

    private DField[] exceptOneSideInMany2OneRelation(DField[] dFields){
        return Arrays.stream(dFields).filter(d-> d.getDAssoc().ascType() == DAssoc.AssocType.One2One || d.getDAssoc().endType() != DAssoc.AssocEndType.One).toArray(DField[]::new);
    }

    @LoopReplacement(ids = {"initNormalFields","normalFieldParams"})
    public Slot[][] initNormalFields(@RequiredParam.MCC NewMCC mcc) {
        return FieldsUtil.getBasicFieldSlots(Common.getFullField(mcc));
    }

    @LoopReplacement(id = "setLinkedDomain")
    public Slot[][] setLinkedDomain(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(exceptOneSideInMany2OneRelation(dFields));
    }

    @IfReplacement(ids = {"genericModule","typeRequired"})
    public boolean isGenericModule(@RequiredParam.MCC NewMCC mcc){
        return !mcc.getSubDomains().isEmpty();
    }

}
