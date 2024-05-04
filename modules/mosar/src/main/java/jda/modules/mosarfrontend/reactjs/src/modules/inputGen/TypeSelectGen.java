package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "/inputTemplates/TypeSelect.js")
public class TypeSelectGen {
    @SlotReplacement(id = "idField")
    public String idField(@RequiredParam.MCC NewMCC mcc) {
        return mcc.getIdField().getDAttr().name();
    }
    @LoopReplacement(id = "moduleTypeOptions")
    public Slot[][] moduleTypeOptions(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("type", NameFormatter.moduleName(type)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

}
