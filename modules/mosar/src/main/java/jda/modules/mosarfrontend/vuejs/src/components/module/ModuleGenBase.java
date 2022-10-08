package jda.modules.mosarfrontend.vuejs.src.components.module;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;

public class ModuleGenBase extends NameFormatter {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/components/" + NameFormatter.moduleJname(moduleName);
    }

    public static Slot[][] LinkedDomain_linked_domain(@RequiredParam.LinkedFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String name = dField.getLinkedDomain().getDomainClass().getSimpleName();
            slotValues.add(new Slot("LinkedDomain", NameFormatter.ModuleName(name)));
            slotValues.add(new Slot("LINKED_DOMAIN", NameFormatter.MODULE_NAME(name)));
            slotValues.add(new Slot("linkedDomain", NameFormatter.moduleName(name)));
            slotValues.add(new Slot("Linked__domain", NameFormatter.Module__name(name)));
            slotValues.add(new Slot("linked_domain", NameFormatter.module_name(name)));
            slotValues.add(new Slot("linkedJdomain", NameFormatter.moduleJname(name)));
            slotValues.add(new Slot("fieldName", NameFormatter.moduleName(dField.getDAttr().name())));
            slotValues.add(new Slot("linkedIdField", NameFormatter.moduleName(dField.getLinkedDomain().getIdField().getDAttr().name())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @SlotReplacement(id = "idField")
    public String idField(@RequiredParam.MCC NewMCC mcc){
        return mcc.getIdField().getDAttr().name();
    }
}
