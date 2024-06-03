package jda.modules.mosarfrontend.angular.app.module.components.module_form;

import jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators.SelectFormTypeInput;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/app/module/components/module_form/module-form.component.html"
)
public class FormHtmlGen extends BaseFormGen {
    @SlotReplacement(id = "subTypeSelect")
    public String subTypeSelect() {
        return new FileFactory(SelectFormTypeInput.class).genFile(false);
    }

    @SlotReplacement(id = "formInputs")
    public String formInputs() {
        return new FileFactory(FormInputsHtmlGen.class).genFile(false);
    }

    @LoopReplacement(id = "subForms")
    public Slot[][] subForms(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        return subDomains.keySet().stream().map(k -> {
            ParamsFactory.getInstance().setCurrentSubDomain(k);
            Slot[] slots = new Slot[]{
                    new Slot("subFormInputs", new FileFactory(SubFormHtmlGen.class).genFile(false))
            };
            return slots;
        }).toArray(Slot[][]::new);
    }

}
