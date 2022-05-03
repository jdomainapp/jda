package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.CustomFileName;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.github.javaparser.ast.body.FieldDeclaration;

@FileTemplateDesc(
        templateFile = "/modules/main.component.ts"
)
public class MainModuleTs {
    @CustomFileName
    public String getFileName(@RequiredParam.MCC MCC mcc) {
        return mcc.getDomainClass().toString();
    }
//    @SlotReplacementDesc(slot = "import")
//    public String replaceImport(@RequiredParam.MCC MCC mcc) {
//    	AngularSlotProperty prop = new AngularSlotProperty(mcc);
//    	return prop.getFormImport();
//    }
//    

}
