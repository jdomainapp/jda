package jda.modules.mosarfrontend.vuejs.src.components.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/components/module/list.vue"
)
public class listGen extends ModuleGenBase {

    @IfReplacement(ids = {"hasProps","hasParent"})
    public boolean hasProps(@RequiredParam.DomainFields DField[] dFields) {
        return Arrays.stream(dFields).filter(e -> e.getDAssoc().endType() == DAssoc.AssocEndType.Many).count() > 0;
    }

    @LoopReplacement(id = "initParentID")
    public Slot[][] initParentID(@RequiredParam.DomainFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(e -> e.getDAssoc().endType() == DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }
}
