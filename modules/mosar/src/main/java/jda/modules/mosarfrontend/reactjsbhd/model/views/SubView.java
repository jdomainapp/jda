package jda.modules.mosarfrontend.reactjsbhd.model.views;

import org.modeshape.common.text.Inflector;

import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

public class SubView extends View {

    private final String referredClassName;
    private final String parent;
    private final Inflector inflector = Inflector.getInstance();

    public SubView(String referredClassName, String parent) {
        super(JsTemplates.SUBFORM, "");
        this.referredClassName = referredClassName;
        this.parent = parent;
    }

    @Override
    public String getFileName() {
        return referredClassName.concat("Submodule");
    }

    @Override
    public String getAsString() {
        return getTemplate().getAsString()
                .replace("{{ view.name.module }}", referredClassName.concat("Module"))
                .replace("{{ view.dir }}", inflector.pluralize(inflector.underscore(referredClassName)).replace("_", "-"))
                .replace("{{ view.name.submodule }}", referredClassName.concat("Submodule"))
                .replace("{{ view.parent }}", parent);
    }
}
