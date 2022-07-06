package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;

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
    
    @SlotReplacement(slot = "import")
    public String getImport(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getFormImport();
    }

    @SlotReplacement(slot = "selector")
    public String getSelector(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getSelector();
    }
    
    @SlotReplacement(slot = "componentName")
    public String getComponentName(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "Component";
    }

    @SlotReplacement(slot = "title")
    public String getTitle(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getTitle();
    }

    @SlotReplacement(slot = "api")
    public String getApi(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getAPI();
    }

    @SlotReplacement(slot = "form_class")
    public String getFormClass(@RequiredParam.ModuleName String moduleName) {
        return moduleName + "FormComponent";
    }
    
    @LoopReplacement(slots = {"field", "fieldTitle"}, id = "1")
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
