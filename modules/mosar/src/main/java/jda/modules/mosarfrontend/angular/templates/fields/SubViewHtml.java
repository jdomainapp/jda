package jda.modules.mosarfrontend.angular.templates.fields;

import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

@FileTemplateDesc(
        templateFile = "/fields/subview.html.tp"
)
public class SubViewHtml {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacement(id = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacement(id = "fieldName")
    public String fieldName(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
          return field.getDAttr().name();    		
    	} else {
    		return "";
    	}
    }
    
    @SlotReplacement(id = "form-selector")
    public String getSelector(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getSelector() + "-form";
    }   
}
