package jda.modules.mosarfrontend.vuejs.src.components.module;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.Arrays;
import java.util.Map;

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

    @LoopReplacement(ids = {"initLinkedModules", "linkedComponents", "hideSubForm"})
    public Slot[][] initLinkedModules(@RequiredParam.DomainFields DField[] domains) {
        return LinkedDomain_linked_domain(Arrays.stream(domains).filter(e -> e.getDAssoc().endType() == DAssoc.AssocEndType.One).toArray(DField[]::new));
    }

    @IfReplacement(ids= {"setDefaultTypeGen","typedModule","setDefaultType"})
    public boolean isTypedModule(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return !subDomains.isEmpty();
    }

    @SlotReplacement(id = "defaultType")
    public String defaultType(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        if (subDomains.isEmpty()) return "";
        return subDomains.keySet().stream().findFirst().get();
    }
}
