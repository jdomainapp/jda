package jda.modules.mosarfrontend.reactjs.model.views.fields;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.mosarfrontend.reactjs.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjs.templates.JsTemplates;

import org.modeshape.common.text.Inflector;

class OneManyField extends ViewField {
    private static Inflector inflector = Inflector.getInstance();

    private final String submoduleViewName;

    public OneManyField(FieldDef fieldDef) {
        super(fieldDef);
        this.submoduleViewName = fieldDef.getType()
                .asClassOrInterfaceType()
                .getNameAsString()
                .concat("Submodule");
    }

    @Override
    public String getReferredView() {
        final String fieldTypeName = this.getFieldDef()
                .getType().asClassOrInterfaceType()
                .getTypeArguments()
                .get()
                .get(0)
                .asClassOrInterfaceType()
                .getNameAsString();
        return fieldTypeName.concat("Submodule");
    }

    @Override
    public JsTemplate getTemplate() {
        return JsTemplates.SUBMODULE_FIELD;
    }

    @Override
    public String getAsString() {
        final String fieldTypeName = this.getFieldDef()
                .getType().asClassOrInterfaceType()
                .getTypeArguments()
                .get()
                .get(0)
                .asClassOrInterfaceType().getNameAsString();
        return getTemplate().getAsString()
                .replace("{{ thisNamePlural }}", inflector.lowerCamelCase(inflector.pluralize(inflector.underscore(fieldTypeName))))
                .replace("{{ submodule }}", fieldTypeName.concat("Submodule"))
                .replace("{{ classNameHumanReadable }}",
                        "Form: " + inflector.capitalize(
                                inflector.humanize(inflector.underscore(fieldTypeName))))
                .replace("{{ backingField }}", getBackingField());
    }
}
