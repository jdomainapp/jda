package jda.modules.mosarfrontend.angular.templates.src.modules;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/src/modules/form.component.ts"
)
public class FormTs {
    @WithFileName
    public String getFileName(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getFileName() + "-form.component";
    }

    @WithFilePath
    public String getFilePath(@RequiredParam.AngularProp AngularSlotProperty prop) {
    	return "/src/" + prop.getFileName() + "/" +  prop.getFileName() + "-form";
    }    
    @SlotReplacementDesc(slot = "componentName")
    public String moduleName(@RequiredParam.ModuleName String name) {
        return name + "FormComponent";
    }

    @SlotReplacementDesc(slot = "selector")
    public String getSelector(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getSelector() + "-form";
    }
    
    @SlotReplacementDesc(slot = "html-path")
    public String getHtmlPath(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return "./" + prop.getFileName() + "-form.component.html";
    }

    @SlotReplacementDesc(slot = "api")
    public String getApi(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getAPI();
    }    
}
