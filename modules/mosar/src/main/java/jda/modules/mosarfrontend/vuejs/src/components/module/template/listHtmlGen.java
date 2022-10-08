package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/list.html"
)
public class listHtmlGen extends ModuleTemplateGenBase {

    @LoopReplacement(ids = {"normalFieldColumns","normalFieldLabels"})
    public Slot[][] normalFieldColumns(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f-> f.getDAssoc()==null && !f.getDAttr().id()).toArray(DField[]::new));
    }

    @LoopReplacement(ids = {"linkedFieldColumns","linkedFieldLabels"})
    public Slot[][] linkedFieldColumns(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f-> f.getDAssoc().ascType()== DAssoc.AssocType.One2One || f.getDAssoc().endType() != DAssoc.AssocEndType.One).toArray(DField[]::new));
    }


}
