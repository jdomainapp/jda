package jda.modules.mosarfrontend.angular.templates.modules;

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
        templateFile = "/modules/main.component.ts"
)
public class MainModuleTs {
    @WithFileName
    public String getFileName(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
    	return prop.getFileName() + ".component";
    }

    @WithFilePath
    public String getFilePath(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
    	return "/" + prop.getFileName();
    }
    
    @SlotReplacementDesc(slot = "import")
    public String getImport(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getFormImport();
    }

    @SlotReplacementDesc(slot = "selector")
    public String getSelector(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getSelector();
    }
    
    @SlotReplacementDesc(slot = "componentName")
    public String getComponentName(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "Component";
    }

    @SlotReplacementDesc(slot = "title")
    public String getTitle(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getTitle();
    }

    @SlotReplacementDesc(slot = "api")
    public String getApi(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getAPI();
    }

    @SlotReplacementDesc(slot = "form_class")
    public String getFormClass(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "FormComponent";
    }
    
    @LoopReplacementDesc(slots = {"field", "fieldTitle"}, id = "1")
    public Slot[][] fields(@RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("field", field.getDAttr().name()));
            list.add(new Slot("fieldTitle", field.getAttributeDesc() != null ? field.getAttributeDesc().label() : field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }  
      
}
