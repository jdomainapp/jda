package jda.modules.mosarfrontend.angular.app.module.components.module_form;

import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseFormGen extends NameFormatter {
    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return moduleJname(name) + "-form.component";
    }

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return "/src/app/" + moduleJname(name) + "/components/" + moduleJname(name) + "-form";
    }

    @Data
    public static class DateRangeConfig {
        DField start;
        DField end;
    }

    protected HashMap<String, DateRangeConfig> getDateRangeFields(DField[] dFields) {
        HashMap<String, DateRangeConfig> dateRangeInputs = new HashMap<String, DateRangeConfig>();
        for (DField dField : dFields) {
            if (dField.getAttributeDesc() != null) {
                InputTypes type = dField.getAttributeDesc().inputType();
                if (type.equals(InputTypes.DateRangeStart) || type.equals(InputTypes.DateRangeEnd)) {
                    String id = dField.getAttributeDesc().id();
                    if (!dateRangeInputs.containsKey(id)) {
                        dateRangeInputs.put(id, new DateRangeConfig());
                    }
                    if (type.equals(InputTypes.DateRangeStart)) {
                        dateRangeInputs.get(id).setStart(dField);
                    }
                    if (type.equals(InputTypes.DateRangeEnd)) {
                        dateRangeInputs.get(id).setEnd(dField);
                    }
                }
            }
        }
        return dateRangeInputs;
    }

    protected boolean haveInputType(DField[] dFields, InputTypes type) {
        return Arrays.stream(dFields).anyMatch(d -> d.getAttributeDesc() != null &&
                d.getAttributeDesc().inputType().equals(type));
    }

    @IfReplacement(ids = {"haveSubType", "haveSubType1", "haveSubType2"})
    public boolean haveSubType(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        return !subDomain.isEmpty();
    }


}
