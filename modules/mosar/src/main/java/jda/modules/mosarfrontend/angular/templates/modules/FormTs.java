package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/modules/form.component.ts"
)
public class FormTs {
    @WithFileName
    public String getFileName(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getFormFileName() + ".component";
    }
    
    @WithFilePath
    public String getFilePath(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
    	return "\\" + prop.getFileName() + "\\" +  prop.getFormFileName() + "\\";
    }    
    @SlotReplacement(id = "componentName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name + "FormComponent";
    }

    @SlotReplacement(id = "selector")
    public String getSelector(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getSelector() + "-form";
    }
    
    @SlotReplacement(id = "html-path")
    public String getHtmlPath(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return "./" + prop.getFormFileName() + ".component.html";
    }

    @SlotReplacement(id = "api")
    public String getApi(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getAPI();
    } 
    
    @LoopReplacement(slots = {"field", "getSubFunction", "subAPI"}, id = "subview")
    public Slot[][] fields(@RequiredParam.DomainFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            String moduleName = field.getDAssoc().associate().type().getSimpleName();
            String fieldName = field.getDAttr().name();
            AngularSlotProperty prop = new AngularSlotProperty(moduleName);
            list.add(new Slot("field", fieldName));  
            list.add(new Slot("getSubFunction", "get" + moduleName));
            list.add(new Slot("subAPI", prop.getAPI()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }    
}
