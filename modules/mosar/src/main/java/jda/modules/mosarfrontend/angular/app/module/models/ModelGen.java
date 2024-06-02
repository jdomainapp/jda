package jda.modules.mosarfrontend.angular.app.module.models;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFileName;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.WithFilePath;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.common_gen.FieldsUtil;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.Arrays;

@FileTemplateDesc(templateFile = "/src/app/module/models/model.ts")
public class ModelGen extends NameFormatter {
    @WithFileName
    public String fileName(@RequiredParam.ModuleName String name) {
        return moduleJname(name);
    }

    @WithFilePath
    public String filePath(@RequiredParam.ModuleName String name) {
        return "/src/app/" + moduleJname(name) + "/models";
    }

    @LoopReplacement(id = "importLinkedModules")
    public Slot[][] importLinkedModules(@RequiredParam.LinkedDomains Domain[] domains) {
        return Arrays.stream(domains).map(domain -> {
            String linkedName = domain.getDomainClass().getSimpleName();
            return new Slot[]{
                    new Slot("LinkedName", ModuleName(linkedName)),
                    new Slot("linkedJname", moduleJname(linkedName))
            };
        }).toArray(Slot[][]::new);
    }

    @LoopReplacement(id = "fields")
    public Slot[][] fieldConfigs(@RequiredParam.ModuleFields DField[] dFields) {
        return Arrays.stream(dFields).map(dField -> {
            String fieldName = dField.getDAttr().name();
            return new Slot[]{
                    new Slot("fieldName", fieldName),
                    new Slot("fieldType", typeConverter(dField))
            };
        }).toArray(Slot[][]::new);
    }

    public String typeConverter(DField field) {
        if (field.getDAttr().id()) return "any";
        DAssoc ass = field.getDAssoc();
        switch (field.getDAttr().type()) {
            case String:
            case StringMasked:
            case Char:
            case Image:
            case Serializable:
            case Font:
            case Color:
                return "string";
            case Integer:
            case BigInteger:
            case Long:
            case Float:
            case Double:
            case Short:
            case Byte:
                return "number";
            case Boolean:
                return "boolean";
            case Domain:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return ass.associate().type().getSimpleName();
                } else if (field.getEnumName() != null) {
                    return field.getEnumName();
                } else {
                    return "any";
                }
            case Collection:
                if (ass != null && ass.associate() != null && ass.associate().type() != null) {
                    return ass.associate().type().getSimpleName() + "[]";
                } else return "any[]";
            case Array:
                return "any[]";
            case File:
            case Other:
                return "any";
            case Null:
                return "null";
            case Date:
                return "Date";
            case ByteArraySmall:
            case ByteArrayLarge:
                return "number[]";
        }
        return "any";
    }
}
