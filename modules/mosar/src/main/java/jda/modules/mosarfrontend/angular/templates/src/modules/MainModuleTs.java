package jda.modules.mosarfrontend.angular.templates.src.modules;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/modules/main.component.ts"
)
public class MainModuleTs {
    @WithFileName
    public String getFileName(@RequiredParam.AngularProp AngularSlotProperty prop) {
    	return prop.getFileName() + ".component";
    }

    @WithFilePath
    public String getFilePath(@RequiredParam.AngularProp AngularSlotProperty prop) {
    	return "/src/" + prop.getFileName();
    }
    
    @SlotReplacementDesc(slot = "import")
    public String getImport(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getFormImport();
    }

    @SlotReplacementDesc(slot = "selector")
    public String getSelector(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getSelector();
    }
    
    @SlotReplacementDesc(slot = "componentName")
    public String getComponentName(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "Component";
    }

    @SlotReplacementDesc(slot = "title")
    public String getTitle(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getTitle();
    }

    @SlotReplacementDesc(slot = "api")
    public String getApi(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getAPI();
    }

    @SlotReplacementDesc(slot = "form_class")
    public String getFormClass(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "FormComponent";
    }
    
    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
    public Slot[][] fields(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getDAttr().name() + (field.getDAttr().optional() ? "?" : "")));
            list.add(new Slot("fieldType", "ABC"));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
