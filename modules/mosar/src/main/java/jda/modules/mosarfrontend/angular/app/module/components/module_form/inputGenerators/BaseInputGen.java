package jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.angular.app.module.components.module_form.BaseFormGen;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseInputGen extends BaseFormGen {
    @SlotReplacement(id = "fieldName")
    public String getFieldName(@RequiredParam.ModuleField DField dField) {
        return dField.getDAttr().name();
    }

    @SlotReplacement(id = "fieldLabel")
    public String fieldLabel(@RequiredParam.ModuleField DField field) {
        return field.getAttributeDesc() != null ? field.getAttributeDesc().label() : Inflector.getInstance().titleCase(field.getDAttr().name());
    }

    @SlotReplacement(id = "fieldOptions")
    public String fieldOptions(@RequiredParam.ModuleField DField dField) {
        StringBuilder options = new StringBuilder();
        if (!dField.getDAttr().mutable())
            options.append(" disabled");
        return options.toString();
    }

    @SlotReplacement(id = "fieldJnames")
    public String fieldJnames(@RequiredParam.ModuleField DField dField) {
        return moduleJnames(dField.getDAttr().name());
    }

    @SlotReplacement(id = "fieldJname")
    public String fieldJname(@RequiredParam.ModuleField DField dField) {
        return moduleJname(dField.getDAttr().name());
    }

    @SlotReplacement(id = "linkedName")
    public String linkedName(@RequiredParam.ModuleField DField dField) {
        if (dField.getLinkedField() == null) return "";
        return dField.getLinkedField().getDAttr().name();
    }

    @SlotReplacement(id = "linkedModuleJname")
    public String linkedModuleJname(@RequiredParam.ModuleField DField dField) {
        if (dField.getLinkedDomain() == null) return "";
        return moduleJname(dField.getLinkedDomain().getDomainClass().getSimpleName());
    }

    @SlotReplacement(id = "maxLength")
    public String maxLength(@RequiredParam.ModuleField DField dField) {
        return String.valueOf(dField.getDAttr().length());
    }
}
