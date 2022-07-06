package jda.modules.mosarfrontend.angular.templates.modules;

import jda.modules.mosarfrontend.angular.templates.fields.InputHtml;
import jda.modules.mosarfrontend.angular.templates.fields.SubViewHtml;
import jda.modules.mosarfrontend.common.AngularSlotProperty;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/modules/form.html"
)
public class FormHtml {
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
    

    @LoopReplacement(slots = {"fieldText"}, id = "field")

    public Slot[][] fields(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.TemplateFolder String templateFolder) throws Exception {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : fields) {
            ArrayList<Slot> list = new ArrayList<>();
            Class<?> fieldGenClass = getFieldGenClass(field);

            ParamsFactory.getInstance().setCurrentModuleField(field);
            String genContent = new FileFactory(fieldGenClass, "", templateFolder).genAndGetContent();
            list.add(new Slot("fieldText", genContent));

            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }


    private Class<?> getFieldGenClass(DField field) {
    	switch (field.getDAttr().type()) {
        case String:
        case StringMasked:
        case Char:
        case Image:
        case Serializable:
        case Font:
        case Color:
        case Integer:
        case BigInteger:
        case Long:
        case Float:
        case Double:
        case Short:
        case Byte:
            return InputHtml.class;
        case Domain:
        case Collection:
        	return SubViewHtml.class;
        default:
        	return InputHtml.class;
    	}
    	
    }
}
