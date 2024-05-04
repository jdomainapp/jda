package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.utils.DField;

public class LinkedInputBaseGen extends SimpleInputGen {
    @SlotReplacement(id = "Linked__domain")
    public String Linked__domain(@RequiredParam.ModuleField DField field) {
        return Module__name(field.getLinkedDomain().getKey());
    }

    @SlotReplacement(id = "LinkedDomain")
    public String LinkedDomain(@RequiredParam.ModuleField DField field) {
        return ModuleName(field.getLinkedDomain().getKey());
    }

    @SlotReplacement(id = "linkedFields")
    public String linkedFields(@RequiredParam.ModuleField DField field) {
        if(field.getLinkedField()!=null)
            return moduleNames(field.getLinkedField().getDAttr().name());
        return "";
    }

    @SlotReplacement(id = "linkedField")
    public String linkedField(@RequiredParam.ModuleField DField field) {
        if(field.getLinkedField()!=null)
            return moduleName(field.getLinkedField().getDAttr().name());
        return "";
    }

    @SlotReplacement(id = "idType")
    public String idType(@RequiredParam.ModuleField DField field){
        if(field.getLinkedField()!=null)
        return getFieldType(field.getLinkedField().getLinkedDomain().getIdField().getDAttr().type()) ;
        return "text";
    }

    @SlotReplacement(id = "fieldNames")
    public String fieldNames(@RequiredParam.ModuleField DField field) {
        return moduleNames(field.getDAttr().name());
    }
}
