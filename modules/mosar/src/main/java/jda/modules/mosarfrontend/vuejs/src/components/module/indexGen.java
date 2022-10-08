package jda.modules.mosarfrontend.vuejs.src.components.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/components/module/index.vue"
)
public class indexGen extends ModuleGenBase {
    @LoopReplacement(id = "One2ManyIds")
    public Slot[][] One2ManyIds(@RequiredParam.LinkedFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : Arrays.stream(dFields).filter(e -> e.getDAssoc().endType() == DAssoc.AssocEndType.Many).toArray(DField[]::new))
        {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String name = dField.getLinkedDomain().getDomainClass().getSimpleName();
            slotValues.add(new Slot("LinkedDomain", NameFormatter.ModuleName(name)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }


}
