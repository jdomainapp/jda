package jda.modules.mosarfrontend.reactjs.src.modules.patterns;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/modules/patterns/Struct.js")
public class StructGen {
    @LoopReplacement(id = "endpoints")
    public Slot[][] endpoints(@RequiredParam.ModuleName String mKey, @RequiredParam.ParentModuleKey String pKey, @RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> slotValues = FieldsUtil.getBasicFieldSlots(field);
            if (field.getLinkedDomain() != null) {
                System.out.println(mKey + " --- " + pKey);
                if (field.getLinkedDomain().getKey().equals(pKey) ||
                        (field.getDAssoc().endType() == DAssoc.AssocEndType.Many && field.getDAssoc().ascType() == DAssoc.AssocType.One2Many))
                    continue;
                System.out.println("Gen linked field " + field.getDAttr().name());
                ParamsFactory.getInstance().setCurrentModule(field.getLinkedDomain().getKey());
                ParamsFactory.getInstance().setParentKey(mKey);
                slotValues.add(new Slot("subStructure", (new FileFactory(StructGen.class)).genFile(false)));
                ParamsFactory.getInstance().setParentKey(pKey);
                ParamsFactory.getInstance().setCurrentModule(mKey);
            } else {
                slotValues.add(new Slot("subStructure", "undefined"));
            }
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);

    }
}
