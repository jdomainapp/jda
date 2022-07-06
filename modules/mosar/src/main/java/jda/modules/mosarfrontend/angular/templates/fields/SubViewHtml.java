package jda.modules.mosarfrontend.angular.templates.fields;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/fields/subview.html.tp"
)
public class SubViewHtml {
    @WithFileName
    public String getFileName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacement(slot = "moduleName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name;
    }

    @SlotReplacement(slot = "fieldName")
    public String fieldName(@RequiredParam.ModuleField DField field) {
    	if (field != null) {
          return field.getDAttr().name();    		
    	} else {
    		return "";
    	}
    }
    
    @SlotReplacement(slot = "form-selector")
    public String getSelector(@RequiredParam.MCC NewMCC mcc) {
    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
        return prop.getSelector() + "-form";
    }   
}
