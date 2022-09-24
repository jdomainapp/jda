package jda.modules.mosarfrontend.reactjsbhd.model.views.fields;

import org.modeshape.common.text.Inflector;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

class OneOneField extends AssociativeInputField {

    private static Inflector inflector = Inflector.getInstance();

    public OneOneField(FieldDef fieldDef, FieldDef idFieldDef, String idFieldLabel) {
        super(fieldDef, idFieldDef, idFieldLabel);
    }

    @Override
    public String getReferredView() {
        final String fieldTypeName = this.getFieldDef()
                .getType().asClassOrInterfaceType().getNameAsString();
        return fieldTypeName.concat("Submodule");
    }

    @Override
    public JsTemplate getTemplate() {
        return JsTemplates.ONE_ONE_INPUT_FIELD;
    }

    @Override
    public String getAsString() {
        final String fieldTypeName = this.getFieldDef()
                .getType().asClassOrInterfaceType().getNameAsString();

        return super.getAsString()
                .replace("{{ classNameCamelCase }}", inflector.lowerCamelCase(fieldTypeName))
                .replace("{{ submodule }}", fieldTypeName.concat("Submodule"))
                .replace("{{ classNameHumanReadable }}",
                        "Form: " + inflector.capitalize(
                                inflector.humanize(inflector.underscore(fieldTypeName))))
                .replace("{{ backingField }}", getDetailsField().getBackingField())
                .replace("{{ idBackingField }}", getIdField().getBackingField());

    }
}
