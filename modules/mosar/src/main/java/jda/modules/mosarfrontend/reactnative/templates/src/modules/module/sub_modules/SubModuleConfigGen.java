package jda.modules.mosarfrontend.reactnative.templates.src.modules.module.sub_modules;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import jda.modules.mosarfrontend.common.anotation.LoopReplacementDesc;
import jda.modules.mosarfrontend.common.anotation.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.SlotReplacementDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;
import jda.modules.mosarfrontend.common.utils.NewMCC;
import org.modeshape.common.text.Inflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@FileTemplateDesc(
        templateFile = "/src/modules/module/sub_modules/ModuleConfig.ts"
)
public class SubModuleConfigGen extends SubModuleCommonGen {

    @SlotReplacementDesc(slot = "importDataType")
    public String importDataType(@RequiredParam.ModuleName String moduleName, @RequiredParam.MCC NewMCC domain) {
        if (Arrays.stream(domain.getDFields()).anyMatch(f -> f.getDAssoc() != null)) {
            moduleName = moduleName + ", " + "Sub" + moduleName;
        }
        return moduleName;
    }

    @SlotReplacementDesc(slot = "fieldID")
    public String fieldID(@RequiredParam.ModuleFields DField[] fields) {
        DField[] idField = Arrays.stream(fields).filter(f -> f.getDAttr().id()).toArray(DField[]::new);
        if (idField.length > 0) {
            return idField[0].getDAttr().name();
        } else
            return fields[0].getDAttr().name();
    }

    @SlotReplacementDesc(slot = "apiResource")
    public String apiResource(@RequiredParam.ModuleName String moduleName) {
        return Inflector.getInstance().underscore(Inflector.getInstance().pluralize(moduleName)).replace("_", "-");
    }

    @SlotReplacementDesc(slot = "moduleTitle")
    public String moduleTitle(@RequiredParam.ModuleName String moduleName) {
        Inflector inflector = Inflector.getInstance();
        return inflector.titleCase(inflector.underscore(inflector.pluralize(moduleName))).replace("_", " ");
    }

    @LoopReplacementDesc(id = "fieldLabelConfig", slots = {"fieldName", "fieldLabel"})
    public Slot[][] fieldLabelConfig(@RequiredParam.CurrentSubDomain Domain subDomain) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : subDomain.getDFields()) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("fieldName", field.getDAttr().name()));
            list.add(new Slot("fieldLabel", field.getAttributeDesc() != null ? field.getAttributeDesc().label() : field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "quickRender", slots = {"moduleAlias", "fieldName"})
    public Slot[][] quickRender(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAssoc() == null && !f.getDAttr().optional()).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleAlias", moduleName.toLowerCase()));
            list.add(new Slot("fieldName", field.getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

    @LoopReplacementDesc(id = "toPOST", slots = {"moduleName1", "linkedModule", "linkedOptional", "linkedModuleIdField"})
    public Slot[][] toPOST(@RequiredParam.ModuleFields DField[] fields, @RequiredParam.ModuleName String moduleName, @RequiredParam.ModuleMap Map<String, NewMCC> domainMap) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();
        for (DField field : Arrays.stream(fields).filter(f -> f.getDAttr().type() == DAttr.Type.Domain && f.getDAssoc() != null && f.getEnumName() == null).toArray(DField[]::new)) {
            ArrayList<Slot> list = new ArrayList<>();
            list.add(new Slot("moduleName1", moduleName.toLowerCase()));
            list.add(new Slot("linkedModule", field.getDAttr().name()));
            list.add(new Slot("linkedOptional", field.getDAttr().optional() ? "?" : ""));
            NewMCC domain = domainMap.get(field.getDAssoc().associate().type().getSimpleName());
            list.add(new Slot("linkedModuleIdField", domain.getIdField().getDAttr().name()));
            result.add(list);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }
}
