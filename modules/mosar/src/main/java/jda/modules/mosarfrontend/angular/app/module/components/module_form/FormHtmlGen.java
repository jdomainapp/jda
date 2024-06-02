package jda.modules.mosarfrontend.angular.app.module.components.module_form;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.angular.app.module.components.module_form.inputGenerators.*;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.SlotReplacement;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.Domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@FileTemplateDesc(
        templateFile = "/src/app/module/components/module_form/module-form.component.html"
)
public class FormHtmlGen extends BaseFormGen {
    @SlotReplacement(id = "subTypeSelect")
    public String subTypeSelect() {
        return new FileFactory(SelectFormTypeInput.class).genFile(false);
    }

    @LoopReplacement(id = "formInputs")
    public Slot[][] getFormInputs(@RequiredParam.ModuleFields DField[] fields) {
        return Arrays.stream(fields)
                .map(field -> new Slot[]{
                        new Slot("formInput", buildHtmlInput(field))
                }).toArray(Slot[][]::new);
    }

    private String buildHtmlInput(DField field) {
        Class<?> genClass;
        switch (getFieldType(field)) {
            case "date_range":
                if (field.getAttributeDesc().inputType().equals(InputTypes.DateRangeStart)) {
                    genClass = DateRangeInput.class;
                    break;
                } else {
                    return ""; // Skip date range end
                }
            case "date":
                genClass = DateInput.class;
                break;
            case "slider":
                genClass = SliderInput.class;
                break;
            case "enum":
                genClass = EnumInput.class;
                break;
            case "rating":
                genClass = RatingInput.class;
                break;
            case "One2Many":
                genClass = One2ManyInput.class;
                break;
            case "Many2One":
                genClass = Many2OneInput.class;
                break;
            case "One2One":
                genClass = One2OneInput.class;
                break;
            default:
                genClass = NormalInputGen.class;
        }
        ParamsFactory.getInstance().setCurrentField(field);
        return new FileFactory(genClass).genFile(false);
    }

    private static String getFieldType(DField dField) {
        if (dField.getDAssoc() != null) {
            if (dField.getDAssoc().ascType().equals(DAssoc.AssocType.One2Many)) {
                return dField.getDAssoc().endType().equals(DAssoc.AssocEndType.One) ? "One2Many" : "Many2One";
            } else {
                return "One2One";
            }
        } else {
            if (dField.getAttributeDesc() != null && dField.getAttributeDesc().inputType() != InputTypes.Undefined) {
                switch (dField.getAttributeDesc().inputType()) {
                    case DateRangeStart:
                    case DateRangeEnd:
                        return "date_range";
                    case Slider:
                        return "slider";
                    case Rating:
                        return "rating";
                    default:
                        return "";
                }
            } else {
                if (dField.getEnumValues() != null) return "enum";
                if (Objects.requireNonNull(dField.getDAttr().type()) == DAttr.Type.Date) {
                    return "date";
                }
                return "";
            }

        }
    }
}
