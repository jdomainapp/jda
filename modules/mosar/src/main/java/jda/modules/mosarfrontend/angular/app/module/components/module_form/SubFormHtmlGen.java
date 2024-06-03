package jda.modules.mosarfrontend.angular.app.module.components.module_form;

import jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators.BaseInputGen;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/module-sub-form.html")
public class SubFormHtmlGen {
    @SlotReplacement(id = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain) {
        return domain.getType();
    }

    @SlotReplacement(id = "formInputs")
    public String formInputs(@RequiredParam.CurrentSubDomain Domain domain, @RequiredParam.ModuleFields DField[] dFields) {
        ParamsFactory.getInstance().setModuleFields(domain.getDFields());
        String result = new FileFactory(FormInputsHtmlGen.class).genFile(false);
        ParamsFactory.getInstance().setModuleFields(dFields);
        return result;
    }
}
