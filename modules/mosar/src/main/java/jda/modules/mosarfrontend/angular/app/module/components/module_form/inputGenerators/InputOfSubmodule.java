package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.utils.Domain;

public class InputOfSubmodule extends BaseInputGen{
    @SlotReplacement(id = "subModuleName")
    public String subModuleName(@RequiredParam.CurrentSubDomain Domain domain){
        return "";
    }
}
