package jda.modules.mosarfrontend.reactjs_new_gen.templates.src.modules;

import jda.modules.dcsl.parser.Dom;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/modules/index.js"
)
public class indexGen extends BaseModuleGen {
    @SlotReplacement(slot = "moduleTypes")
    public String moduleTypes(@RequiredParam.SubDomains Map<String,Domain> domains){
        StringBuilder moduleTypes = new StringBuilder();
        for (String type : domains.keySet()) {
            moduleTypes.append("\"");
            moduleTypes.append(type);
            moduleTypes.append("\",");
        }
        return moduleTypes.toString();
    }

    @LoopReplacement(id = "linkedModuleApi")
    public Slot[][] linkedModuleApi(@RequiredParam.LinkedDomains Domain[] domains){
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (Domain domain: domains) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            String linkedDomain = domain.getDomainClass().getSimpleName();
            slotValues.add(new Slot("linkedDomain", moduleName(linkedDomain)));
            slotValues.add(new Slot("linkedJdomain", moduleJname(linkedDomain)));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "initLinkedModuleApi")
    public Slot[][] initLinkedModuleApi(@RequiredParam.LinkedDomains Domain[] domains){
        return linkedModuleApi(domains);
    }
}
