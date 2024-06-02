package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.mosarfrontend.angular.app.module.components.module_form.BaseFormGen;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.reactjs.src.modules.BaseModuleGen;
import org.apache.commons.lang.reflect.FieldUtils;

import java.util.HashMap;

@FileTemplateDesc(templateFile = "src/app/module/components/module_form/inputTemplates/DateRangeInput.html")

public class DateRangeInput extends BaseFormGen {
    private DateRangeConfig getConfig(DField[] dFields, DField field) {
        HashMap<String, DateRangeConfig> dateRangeFields = getDateRangeFields(dFields);
        return dateRangeFields.get(field.getAttributeDesc().id());
    }

    @SlotReplacement(id = "range_label")
    public String range_label(@RequiredParam.ModuleField DField dField, @RequiredParam.ModuleFields DField[] dFields) {
        DateRangeConfig config = getConfig(dFields, dField);

        return FieldsUtil.getLabel(config.getStart()) +
                " - " +
                FieldsUtil.getLabel(config.getEnd());
    }

    @SlotReplacement(id = "range_id")
    public String range_id(@RequiredParam.ModuleField DField dField) {
        return dField.getAttributeDesc().id();
    }

    @SlotReplacement(id="RangeName")
    public String rangeName(@RequiredParam.ModuleField DField dField) {
        return ModuleName(dField.getAttributeDesc().id());
    }
}
