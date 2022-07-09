package jda.modules.mosarfrontend.vuejs.templates.src.components.module;

import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

@FileTemplateDesc(templateFile = "/src/components/module/add.vue")
public class addGen extends ModuleGenBase {
    @LoopReplacement(id = "importLinkedDomains")
    public Slot[][] importLinkedDomains(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "getLinkedModuleByID")
    public Slot[][] getLinkedModuleByID(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "linkedComponents")
    public Slot[][] linkedComponents(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "initLinkedModules")
    public Slot[][] initLinkedModules(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "hideSubForm")
    public Slot[][] hideSubForm(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }
}