package jda.modules.mosarfrontend.reactnative.templates.data_types;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.CustomFileName;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/data_types/DataType.ts"
)
public class DataType {
    @CustomFileName
    public String getFileName(@RequiredParam.MCC MCC mcc) {
        return mcc.getDomainClass().toString();
    }

    @LoopReplacementDesc(slots = {"field", "fieldType"}, id = "1")
    public ArrayList<ArrayList<String>> fields(@RequiredParam.MCC MCC mcc) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        return result;
    }
}
