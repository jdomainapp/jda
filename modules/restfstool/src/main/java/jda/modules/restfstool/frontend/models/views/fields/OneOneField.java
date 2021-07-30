package jda.modules.restfstool.frontend.models.views.fields;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.restfstool.frontend.templates.JsTemplate;
import jda.modules.restfstool.frontend.templates.JsTemplates;

import org.modeshape.common.text.Inflector;

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
