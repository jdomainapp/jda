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

import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(templateFile = "/src/components/module/add.vue")
public class addGen extends ModuleGenBase {

    @IfReplacement(ids = {"hasSubType","setDefaultTypeGen", "setDefaultType"})
    public boolean isTypedModule(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return !subDomains.isEmpty();
    }
    @LoopReplacement(id = "importLinkedDomains")
    public Slot[][] importLinkedDomains(@RequiredParam.LinkedFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(id = "getLinkedModuleByID")
    public Slot[][] getLinkedModuleByID(@RequiredParam.LinkedFields DField[] domains) {
        return LinkedDomain_linked_domain(domains);
    }

    @LoopReplacement(ids={"linkedComponentsForOne2Many", "initLinkedModulesForOne2Many"})
    public Slot[][] linkedComponentsForOne2Many(@RequiredParam.LinkedFields DField[] domains) {
        return LinkedDomain_linked_domain(Arrays.stream(domains).filter(e -> e.getDAssoc().ascType() == DAssoc.AssocType.One2Many && e.getDAssoc().endType() == DAssoc.AssocEndType.One).toArray(DField[]::new));
    }

    @LoopReplacement(ids = {"initDataForSubForm","initLinkedModules", "linkedComponents", "hideSubForm", "genQuickView"})
    public Slot[][] initLinkedModules(@RequiredParam.LinkedFields DField[] domains) {
        return LinkedDomain_linked_domain(Arrays.stream(domains).filter(e -> e.getDAssoc().ascType() == DAssoc.AssocType.One2One || e.getDAssoc().endType() == DAssoc.AssocEndType.Many).toArray(DField[]::new));
    }



    @SlotReplacement(id = "defaultType")
    public String defaultType(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        if (subDomains.isEmpty()) return "";
        return subDomains.keySet().stream().findFirst().get();
    }

    @SlotReplacement(id = "hideFields")
    public String hidFields(@RequiredParam.LinkedFields DField[] linkedFields) {
        StringBuilder hidFields = new StringBuilder();
        for (DField linkedField : Arrays.stream(linkedFields).filter(e -> e.getLinkedField() != null).toArray(DField[]::new)) {
            hidFields.append("\"" + linkedField.getLinkedField().getDAttr().name() + "\",");
            hidFields.append("\"" + linkedField.getLinkedDomain().getIdField().getDAttr().name() + "\",");
        }
        return hidFields.toString();
    }

}
