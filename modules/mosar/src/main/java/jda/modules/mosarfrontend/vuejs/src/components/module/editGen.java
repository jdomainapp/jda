package jda.modules.mosarfrontend.vuejs.src.components.module;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/components/module/edit.vue")
public class editGen extends ModuleGenBase{
    @LoopReplacement(id = "importLinkedDomain")
    public Slot[][] importLinkedDomain(@RequiredParam.DomainFields DField[] domains){
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id="subModules")
    public Slot[][] subModules(@RequiredParam.LinkedDomains Domain[] domains){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (Domain domain : domains) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String name = domain.getDomainClass().getSimpleName();
            slotValues.add(new Slot("moduleJname", moduleJname(name)));
            slotValues.add(new Slot("module_name", module_name(name)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "initLinkedDomainValues")
    public Slot[][] linkedComponents(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "getLinkedModuleByID")
    public Slot[][] getLinkedModuleByID(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "hideSubForm")
    public Slot[][] hideSubForm(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }
}
