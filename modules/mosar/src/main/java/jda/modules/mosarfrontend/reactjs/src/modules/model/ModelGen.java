package jda.modules.mosarfrontend.reactjs.src.modules.patterns.model;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/modules/model/Model.js")
public class ModelGen extends NameFormatter {
    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return String.format("/src/%s/model", NameFormatter.moduleJnames(name));
    }

    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return ModuleName(name);
    }

    @LoopReplacement(id = "searchKeys")
    public Slot[][] searchKeys(@RequiredParam.ModuleFields DField[] fiels) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField f : fiels) {
            if (f.getDAttr().searchKey()){
                ArrayList<Slot> slotValues = new ArrayList<>();

                slotValues.add(new Slot("key", moduleName(f.getDAttr().name())));
                result.add(slotValues);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
