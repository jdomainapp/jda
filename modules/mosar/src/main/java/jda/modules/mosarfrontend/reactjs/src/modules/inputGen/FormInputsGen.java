package jda.modules.mosarfrontend.reactjs.src.modules.inputGen;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mccl.syntax.InputTypes;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.LoopReplacement;
import jda.modules.mosarfrontend.common.anotation.gen_controlers.RequiredParam;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.factory.FileFactory;
import jda.modules.mosarfrontend.common.factory.ParamsFactory;
import jda.modules.mosarfrontend.common.factory.Slot;
import jda.modules.mosarfrontend.common.utils.DField;
import jda.modules.mosarfrontend.common.utils.common_gen.NameFormatter;

import java.util.ArrayList;

@FileTemplateDesc(templateFile = "/inputTemplates/ListInputs.js")
public class FormInputsGen extends NameFormatter {
    @LoopReplacement(id = "formInputs")
    public Slot[][] formInputs(@RequiredParam.ModuleFields DField[] dFields) {
        ArrayList<ArrayList<Slot>> result = new ArrayList<>();

        for (DField field : dFields) {
            ParamsFactory.getInstance().setCurrentModuleField(field);
            ArrayList<Slot> slotValues = new ArrayList<>();
            String inputCode = "";
            try {
                if (field.getAttributeDesc() != null && field.getAttributeDesc().inputType() != InputTypes.Undefined) {
                    System.out.println("Specific Input");
                    switch (field.getAttributeDesc().inputType()) {
                        case Rating:
                            inputCode = (new FileFactory(RatingInputGen.class)).genFile(false);
                            break;
                        case DateRangeStart:
                            inputCode = (new FileFactory(DateRangeInputGen.class)).genFile(false);
                            break;
                        case Slider:
                            inputCode = (new FileFactory(SliderInputGen.class)).genFile(false);
                            break;
                        case TextArea:
                            inputCode = (new FileFactory(TextAreaInputGen.class)).genFile(false);
                            break;
                    }
                } else if (field.getEnumValues() != null) {
                    inputCode = (new FileFactory(EnumInputGen.class)).genFile(false);
                } else if (field.getDAssoc() != null) {
                    if (field.getDAssoc().ascType() == DAssoc.AssocType.One2One)
                        inputCode = (new FileFactory(OneOneInputGen.class)).genFile(false);
                    else {
                        if (field.getDAssoc().endType() == DAssoc.AssocEndType.One)
                            inputCode = (new FileFactory(OneSideInputGen.class)).genFile(false);
                        else inputCode = (new FileFactory(ManySideInputGen.class)).genFile(false);
                    }

                } else {
                    inputCode = (new FileFactory(SimpleInputGen.class)).genFile(false);
                }
            } catch (Exception e) {
            }
            slotValues.add(new Slot("inputCode", inputCode));
            result.add(slotValues);
        }
        return result.stream().map(v -> v.toArray(Slot[]::new)).toArray(Slot[][]::new);
    }

}
