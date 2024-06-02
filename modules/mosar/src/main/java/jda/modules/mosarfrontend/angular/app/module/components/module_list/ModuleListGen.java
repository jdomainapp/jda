package jda.modules.mosarfrontend.angular.app.module.components.module_list;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;

import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "src/app/module/components/module_list/module-list.component.ts")
public class ModuleListGen extends NameFormatter {
    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return moduleJname(name) + "-list.component";
    }

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return "/src/app/" + moduleJname(name) + "/components/" + moduleJname(name) + "-list";
    }

    @LoopReplacement(id = "columnConfigs")
    public Slot[][] columnsConfigs(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields)
                .filter(dField -> dField.getLinkedDomain() == null ||
                        !dField.getDAssoc().ascType().equals(DAssoc.AssocType.One2Many) ||
                        (dField.getDAssoc().ascType().equals(DAssoc.AssocType.One2Many) &&
                                dField.getDAssoc().endType().equals(DAssoc.AssocEndType.Many))
                )
                .toArray(DField[]::new));
    }
}