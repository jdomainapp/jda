package jda.modules.mosarfrontend.reactjsbhd.model.views.fields;

import org.modeshape.common.text.Inflector;

import jda.modules.dcsl.parser.statespace.metadef.FieldDef;

public abstract class AssociativeInputField extends ViewField {
    private static final Inflector inflector = Inflector.getInstance();
    private final ViewField idField;
    private final ViewField detailsField;
    private String parent;

    public AssociativeInputField(FieldDef fieldDef, FieldDef idFieldDef, String idFieldLabel) {
        super(fieldDef);
        // create ID field
        this.idField = SimpleViewField.createUsing(idFieldDef, idFieldLabel);
        this.idField.setBackingField(fieldDef.getName() + inflector.upperCamelCase(idFieldDef.getName()));

        // create details field
        this.detailsField = SimpleViewField.createUsing(fieldDef,
                inflector.humanize(
                        inflector.underscore(
                                fieldDef.getType().asClassOrInterfaceType()
                                        .getNameAsString())));
        ((SimpleViewField)this.getIdField()).setDisabled(false);
        ((SimpleViewField)this.getIdField()).setNeedApiCall(true);
        ((SimpleViewField)this.getDetailsField()).setDisabled(true);
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Override
    public String getLabel() {
        return this.detailsField.getLabel();
    }

    public ViewField getIdField() {
        return this.idField;
    }

    public ViewField getDetailsField() {
        return this.detailsField;
    }

    private static String removeUnnecessaryFormGroup(String field) {
        return field.replace("<FormGroup>\n", "")
                .replace("</FormGroup>", "");
    }

    @Override
    public String getAsString() {
        return getTemplate().getAsString()
                .replace("{{ idField }}",
                        removeUnnecessaryFormGroup(this.getIdField().getAsString()))
                .replace("{{ idBackingField }}", idField.getBackingField())
                .replace("{{ detailsBackingField }}", detailsField.getFieldDef().getName())
                .replace("{{ detailsField }}",
                        removeUnnecessaryFormGroup(this.getDetailsField().getAsString()))
                .replace("{{ classNameCamelCase }}",
                        inflector.lowerCamelCase(this.detailsField.getFieldDef().getType()
                                .asClassOrInterfaceType().getNameAsString()))
                .replace("{{ parentClassNameCamelCase }}", this.parent);
    }
}
