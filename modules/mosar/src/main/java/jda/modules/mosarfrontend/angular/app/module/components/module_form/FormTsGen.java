package jda.modules.mosarfrontend.angular.app.module.components.module_form;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.IfReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.*;

@FileTemplateDesc(
        templateFile = "/src/app/module/components/module_form/module-form.component.ts"
)
public class FormTsGen extends BaseFormGen {

    @LoopReplacement(id = "formConfigs")
    public Slot[][] formConfigs(@RequiredParam.ModuleFields DField[] dFields) {
        DField[] fields = Arrays.stream(dFields)
                .filter(dField -> {
                    if (dField.getDAttr().id()) return false;
                    if (dField.getDAssoc() != null &&
                            dField.getDAssoc().ascType().equals(DAssoc.AssocType.One2Many) &&
                            dField.getDAssoc().endType().equals(DAssoc.AssocEndType.One) &&
                            dField.getDAssoc().associate().determinant()
                    ) return false;
                    return true;
                })
                .toArray(DField[]::new);
        return Arrays.stream(fields).map(f -> new Slot[]{
                new Slot("fieldName", f.getDAttr().name()),
                new Slot("fieldValidators", buildValidator(f)),
        }).toArray(Slot[][]::new);
    }

    private String buildValidator(DField dField) {
        ArrayList<String> validators = new ArrayList<>();
        if (!dField.getDAttr().optional())
            validators.add("Validators.required");
        if (dField.getDAttr().length() > 0)
            validators.add("Validators.maxLength(" + dField.getDAttr().length() + ")");
        if (dField.getDAttr().max() != Double.POSITIVE_INFINITY)
            validators.add("Validators.max(" + dField.getDAttr().max() + ")");
        if (dField.getDAttr().min() != Double.NEGATIVE_INFINITY)
            validators.add("Validators.min(" + dField.getDAttr().min() + ")");
        if (dField.getAttributeDesc() != null && !dField.getAttributeDesc().jsValidation().regex().isEmpty())
            validators.add("Validators.pattern(" + dField.getAttributeDesc().jsValidation().regex() + ")");
        return validators.stream().reduce((a, b) -> a + ", " + b).orElse("");
    }

    @IfReplacement(ids = {"haveDateRange1", "haveDateRange2", "haveDateRange3"})
    public boolean haveDateRange(@RequiredParam.ModuleFields DField[] dFields) {
        return haveInputType(dFields, InputTypes.DateRangeStart);
    }

    @LoopReplacement(ids = {"dateRangeInputs", "dateRangeHandlers"})
    public Slot[][] getDateRangeInputs(@RequiredParam.ModuleFields DField[] dFields) {
        HashMap<String, DateRangeConfig> dateRangeInputs = getDateRangeFields(dFields);
        return Arrays.stream(dateRangeInputs.keySet().toArray(new String[0]))
                .map(key -> new Slot[]{
                        new Slot("fieldName", key),
                        new Slot("FieldName", ModuleName(key)),
                        new Slot("start", dateRangeInputs.get(key).getStart().getDAttr().name()),
                        new Slot("end", dateRangeInputs.get(key).getEnd().getDAttr().name()),
                }).toArray(Slot[][]::new);
    }

    @IfReplacement(ids = {"haveSlider", "haveSlider1"})
    public boolean haveSlider(@RequiredParam.ModuleFields DField[] dFields) {
        return haveInputType(dFields, InputTypes.Slider);
    }


    @LoopReplacement(ids = {"sliderOptions"})
    public Slot[][] getSliderInputs(@RequiredParam.ModuleFields DField[] dFields) {
        return Arrays.stream(dFields)
                .filter(dField -> dField.getAttributeDesc() != null && dField.getAttributeDesc().inputType().equals(InputTypes.Slider))
                .map(dField -> new Slot[]{
                        new Slot("fieldName", dField.getDAttr().name()),
                        new Slot("FieldName", ModuleName(dField.getDAttr().name())),
                        new Slot("min", String.valueOf(Math.round(dField.getDAttr().min()))),
                        new Slot("max", String.valueOf(Math.round(dField.getDAttr().max()))),
                }).toArray(Slot[][]::new);
    }

    @LoopReplacement(ids = {"subTypeFields"})
    public Slot[][] subTypeFields(@RequiredParam.SubDomains Map<String, Domain> subDomain) {
        return Arrays.stream(subDomain.keySet().toArray(new String[0]))
                .map(domainName -> Arrays.stream(subDomain.get(domainName).getDFields()).map(
                        dField -> new Slot[]{
                                new Slot("fieldName", dField.getDAttr().name()),
                                new Slot("fieldValidators", buildValidator(dField)),
                                new Slot("type", domainName)
                        }).toArray(Slot[][]::new))
                .flatMap(Arrays::stream)
                .toArray(Slot[][]::new);
    }
}
