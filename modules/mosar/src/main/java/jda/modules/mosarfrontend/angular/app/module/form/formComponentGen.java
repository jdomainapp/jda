package jda.modules.mosarfrontend.angular.app.module.form;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/app/module/form/module-form.component.ts"
)
public class formComponentGen extends BaseFormGen {
    @LoopReplacement(ids = {"getLinkedDomainMethods", "declareLinkedDomainId", "getLinkedDomainData"})
    public Slot[][] getLinkedDomainData(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null).toArray(DField[]::new));
    }


    @LoopReplacement(ids = {"linkedDomainShowFlags", "changeShowLinkedDomainFlagMethods"})
    public Slot[][] linkedDomainShowFlags(@RequiredParam.LinkedFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getLinkedDomain() != null && f.getDAssoc().ascType() == DAssoc.AssocType.One2Many && f.getDAssoc().endType() == DAssoc.AssocEndType.One).toArray(DField[]::new));
    }


}
