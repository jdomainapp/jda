package jda.modules.mosarfrontend.reactnative.templates.data_types;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.common.anotation.GetFileName;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequireParam;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;

import java.util.ArrayList;

@FileTemplateDesc(
        templateFile = "/data_types/DataType.ts"
)
public class DataType {
    @GetFileName
    String getFileName(@RequireParam.MCC MCC mcc){
        return mcc.getDomainClass().toString();
    }
    @LoopReplacementDesc(slots = {"fieldName", "fieldType"})
    ArrayList<ArrayList<String>> fields(@RequireParam.MCC MCC mcc) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        return result;
    }
}
