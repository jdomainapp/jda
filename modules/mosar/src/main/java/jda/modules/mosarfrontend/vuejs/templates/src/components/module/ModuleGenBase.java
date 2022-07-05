package jda.modules.mosarfrontend.vuejs.templates.src.components.module;

import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.WithFilePath;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.DomainNameUtil;

import java.util.ArrayList;

public class ModuleGenBase extends DomainNameUtil {
    @WithFilePath
    public String withFilePath(@RequiredParam.ModuleName String moduleName) {
        return "/src/components/" + DomainNameUtil.module_name(moduleName);
    }

    public static Slot[][] LinkedDomain_linked_domain(@RequiredParam.DomainFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField dField : dFields) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String name = dField.getLinkedDomain().getDomainClass().getSimpleName();
            slotValues.add(new Slot("LinkedDomain", DomainNameUtil.ModuleName(name)));
            slotValues.add(new Slot("linkedDomain", DomainNameUtil.moduleName(name)));
            slotValues.add(new Slot("Linked__domain", DomainNameUtil.Module__name(name)));
            slotValues.add(new Slot("linked_domain", DomainNameUtil.module_name(name)));
            slotValues.add(new Slot("linkedJdomain", DomainNameUtil.moduleJname(name)));
            slotValues.add(new Slot("fieldName", DomainNameUtil.moduleName(dField.getDAttr().name())));
            slotValues.add(new Slot("linkedIdField", DomainNameUtil.moduleName(dField.getLinkedDomain().getIdField().getDAttr().name())));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
