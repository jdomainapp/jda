package jda.modules.mosarfrontend.reactjs.src.modules;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.Arrays;

@FileTemplateDesc(
        templateFile = "/src/modules/ListView.js"
)
public class ListViewGen extends BaseModuleGen {
    @WithFileName
    public String withFileName(@RequiredParam.ModuleName String name) {
        return ModuleName(name) + "ListView";
    }

    @LoopReplacement(id = "tableHeader")
    public Slot[][] tableHeader(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new));
    }

    @LoopReplacement(id = "tableHeaderForLinkedModule")
    public Slot[][] tableHeaderForLinkedModule(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getDAssoc() != null &&
                (f.getDAssoc().ascType() != DAssoc.AssocType.One2Many || f.getDAssoc().endType() != DAssoc.AssocEndType.One)).toArray(DField[]::new));
    }

    @LoopReplacement(id = "LinkedDomainApi")
    public Slot[][] linkedModuleApi(@RequiredParam.ModuleFields DField[] dFields) {
        return tableHeaderForLinkedModule(dFields);
    }

    @LoopReplacement(id = "linkedFieldRender")
    public Slot[][] linkedFieldRender(@RequiredParam.ModuleFields DField[] dFields) {
        return tableHeaderForLinkedModule(dFields);
    }

    @LoopReplacement(id = "fieldRender")
    public Slot[][] fieldRender(@RequiredParam.ModuleFields DField[] dFields) {
        return FieldsUtil.getBasicFieldSlots(Arrays.stream(dFields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new));
    }


}
