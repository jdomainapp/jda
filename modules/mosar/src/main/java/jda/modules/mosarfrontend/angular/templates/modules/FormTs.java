package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.dcsl.parser.statespace.metadef.DAssocDef;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/modules/form.component.ts"
)
public class FormTs {
    @WithFileName
    public String getFileName(@RequiredParam.AngularProp AngularSlotProperty prop) {
        return prop.getFileName() + "-form.component";
    }
    
    @WithFilePath
    public String getFilePath(@RequiredParam.AngularProp AngularSlotProperty prop) {
    	return "\\" + prop.getFileName() + "\\" +  prop.getFileName() + "-form";
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
    
    @LoopReplacementDesc(slots = {"field", "getSubFunction", "subAPI"}, id = "subview")
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
