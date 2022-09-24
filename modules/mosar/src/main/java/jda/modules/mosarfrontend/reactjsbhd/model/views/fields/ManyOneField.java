package jda.modules.mosarfrontend.reactjsbhd.model.views.fields;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

class ManyOneField extends AssociativeInputField {
    public ManyOneField(FieldDef fieldDef, FieldDef idFieldDef, String idLabel) {
        super(fieldDef, idFieldDef, idLabel);
    }

    @Override
    public JsTemplate getTemplate() {
        return JsTemplates.MANY_ONE_INPUT_FIELD;
    }

}
