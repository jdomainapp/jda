package jda.modules.mosarfrontend.reactjs.src.modules.patterns.accordion;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/src/modules/patterns/accordion/Struct.js")
public class StructGen extends NameFormatter {
    @LoopReplacement(id = "endpoints")
    public Slot[][] endpoints(@RequiredParam.MCC NewMCC mcc, @RequiredParam.ModuleName String mKey, @RequiredParam.ParentModuleKey String pKey, @RequiredParam.ModuleFields DField[] fields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        if (mcc.getSubDomains().size() > 0) {
            ArrayList<Slot> slotValues = new ArrayList<>();
            slotValues.add(new Slot("field_name", "type"));
            slotValues.add(new Slot("fieldLabel", "Type"));
            slotValues.add(new Slot("subStructure", ""));
            result.add(slotValues);
        }
        genEndpoints(fields,result,mKey,pKey);
        if(mcc.getSubDomains().size()>0){
            for (String type: mcc.getSubDomains().keySet()){
                genEndpoints(mcc.getSubDomains().get(type).getDFields(),result,mKey,pKey);
            }
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);

    }

    private void genEndpoints(DField[] fields, ArrayList<ArrayList<Slot>> result, String mKey, String pKey){
        for (DField field : fields) {
            if(field.getInputType() == InputTypes.DateRangeStart){//only for date_range
                ArrayList<Slot> slotValuesDateRange = new ArrayList<>();
                slotValuesDateRange.add(new Slot("field_name", field.getInputID()));
                slotValuesDateRange.add(new Slot("fieldLabel", field.getAttributeDesc().label()));
                slotValuesDateRange.add(new Slot("subStructure", ""));
                result.add(slotValuesDateRange);
                continue;
            }
            if(field.getInputType() == InputTypes.DateRangeEnd) continue;
            ArrayList<Slot> slotValues = FieldsUtil.getBasicFieldSlots(field);
            if (field.getLinkedDomain() != null) {
                System.out.println(mKey + " --- " + pKey);
                if (field.getLinkedDomain().getKey().equals(pKey)) continue;

                if(field.getDAssoc().endType() == DAssoc.AssocEndType.Many && field.getDAssoc().ascType() == DAssoc.AssocType.One2Many){
                    ArrayList<Slot> slotValuesID = new ArrayList<>();
                    slotValuesID.add(new Slot("field_name", module_name(field.getDAttr().name())));
                    slotValuesID.add(new Slot("fieldLabel", Module__Name(field.getDAttr().name()) + " ID"));
                    slotValuesID.add(new Slot("subStructure", ""));
                    result.add(slotValuesID);
                    continue;
                }

                //gen id endpoint
                if(field.getDAssoc().ascType() == DAssoc.AssocType.One2One){
                    ArrayList<Slot> slotValuesID = new ArrayList<>();
                    slotValuesID.add(new Slot("field_name", module_name(field.getDAttr().name()) + "_id"));
                    slotValuesID.add(new Slot("fieldLabel", Module__Name(field.getDAttr().name()) + " ID"));
                    slotValuesID.add(new Slot("subStructure", ""));
                    result.add(slotValuesID);
                }


                System.out.println("Gen linked field " + field.getDAttr().name());
                ParamsFactory.getInstance().setCurrentModule(field.getLinkedDomain().getKey());
                ParamsFactory.getInstance().setParentKey(mKey);
                slotValues.add(new Slot("subStructure", "\"subItem\": " + (new FileFactory(StructGen.class)).genFile(false)));
                ParamsFactory.getInstance().setParentKey(pKey);
                ParamsFactory.getInstance().setCurrentModule(mKey);
            } else {
                slotValues.add(new Slot("subStructure", ""));
            }
            result.add(slotValues);
        }
    }
}
