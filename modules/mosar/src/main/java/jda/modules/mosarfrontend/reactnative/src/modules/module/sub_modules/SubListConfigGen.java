package jda.modules.mosarfrontend.reactnative.src.modules.module.sub_modules;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;
import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/modules/module/sub_modules/ListConfig.ts")
public class SubListConfigGen extends CommonSubModuleGen {
    @LoopReplacement(id = "listTitle", slots = {"moduleAlias", "fieldName"})
    public Slot[][] listTitle(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleAlias", moduleName.toLowerCase()));
            list.add(new Slot("fieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

}
