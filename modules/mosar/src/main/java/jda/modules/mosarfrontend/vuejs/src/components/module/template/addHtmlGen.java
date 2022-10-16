package jda.modules.mosarfrontend.vuejs.src.components.module.template;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.*;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import jda.modules.mosarfrontend.vuejs.src.components.module.template.inputTemplates.InputsGen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/components/module/template/form.html"
)
public class addHtmlGen extends ModuleTemplateGenBase {
    @WithFileName
    public String fileName() {
        return "add";
    }


    @SlotReplacement(id = "normalInputs")
    public String normalInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getNormalInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "enumInputs")
    public String enumInputs(@RequiredParam.ModuleFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getEnumInputs(dFields, NameFormatter.moduleName(ModuleName), null);
    }

    private DField[] getFieldsByEndType(DField[] dFields, DAssoc.AssocEndType endType) {
        if (endType == DAssoc.AssocEndType.One)
            return Arrays.stream(dFields).filter(e -> (e.getDAssoc() != null &&
                    (e.getDAssoc().ascType() == DAssoc.AssocType.One2One ||
                    (e.getDAssoc().ascType() == DAssoc.AssocType.One2Many
                            && e.getDAssoc().endType() == DAssoc.AssocEndType.Many)))).toArray(DField[]::new);
        else
            return Arrays.stream(dFields).filter(e -> e.getLinkedField() != null && e.getDAssoc().ascType() == DAssoc.AssocType.One2Many && e.getDAssoc().endType() == DAssoc.AssocEndType.One).toArray(DField[]::new);
    }

    @SlotReplacement(id = "linkedOne2OneInputs")
    public String linkedOne2OneInputs(@RequiredParam.LinkedFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getLinkedInputOne2One(getFieldsByEndType(dFields, DAssoc.AssocEndType.One), NameFormatter.moduleName(ModuleName), null);
    }

    @SlotReplacement(id = "linkedOne2ManyInputs")
    public String linkedOne2ManyInputs(@RequiredParam.LinkedFields DField[] dFields, @RequiredParam.ModuleName String ModuleName) {
        return InputsGen.getLinkedInputOne2Many(getFieldsByEndType(dFields, DAssoc.AssocEndType.Many), NameFormatter.moduleName(ModuleName), null);
    }


    @LoopReplacement(id = "subTypeInputs")
    public Slot[][] subTypeInputs(@RequiredParam.SubDomains Map<String, Domain> subDomains, @RequiredParam.ModuleName String ModuleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            result.add(new ArrayList<>(Arrays.asList(
                    new Slot("normalTypedInputs", InputsGen.getNormalInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("enumTypedInputs", InputsGen.getEnumInputs(subDomains.get(type).getDFields(), ModuleName, type)),
                    new Slot("linkedTypedOne2OneInputs", InputsGen.getLinkedInputOne2One(getFieldsByEndType(subDomains.get(type).getDFields(), DAssoc.AssocEndType.One), ModuleName, type)),
                    new Slot("linkedTypedOne2ManyInputs", InputsGen.getLinkedInputOne2Many(getFieldsByEndType(subDomains.get(type).getDFields(), DAssoc.AssocEndType.Many), ModuleName, type)
                    ))));
        }
        return MethodUtils.toLoopData(result);
    }

    @IfReplacement(id = "hasSubType")
    public boolean hasSubType(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        return !subDomain.isEmpty();
    }

    @LoopReplacement(id = "types")
    public Slot[][] types(@RequiredParam.SubDomains Map<String, Domain> subDomains) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (String type : subDomains.keySet()) {
            result.add(new ArrayList<>(Arrays.asList(
                    new Slot("type", type))
            ));
        }
        return MethodUtils.toLoopData(result);
    }
}
