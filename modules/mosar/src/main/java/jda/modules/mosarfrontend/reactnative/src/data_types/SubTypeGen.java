package jda.modules.mosarfrontend.reactnative.src.data_types;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/data_types/SubType.ts")
public class SubTypeGen {
    @WithFileName
    public String getFileName(@RequiredParam.CurrentSubDomain Domain subDomain) {
        return subDomain.getDomainClass().getSimpleName();
    }

    @SlotReplacement(id = "ModuleName")
    public String ModuleName(@RequiredParam.ModuleName String moduleName) {
        return moduleName;
    }

    @SlotReplacement(id = "SubModuleName")
    public String ModuleName(@RequiredParam.CurrentSubDomain Domain subDomain) {
        return getFileName(subDomain);
    }

    @LoopReplacement(id = "fields", slots = {"field", "fieldType"})
    public Slot[][] fields(@RequiredParam.CurrentSubDomain Domain subDomain) {
        DataTypeGen dataTypeGen = new DataTypeGen();
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(subDomain.getDFields()).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getDAttr().name()));
            list.add(new Slot("fieldType", dataTypeGen.typeConverter(field)));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);

    }
}
