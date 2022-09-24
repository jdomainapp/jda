package jda.modules.mosarfrontend.reactjsbhd.model.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.modeshape.common.text.Inflector;

import com.github.javaparser.ast.body.FieldDeclaration;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosarfrontend.reactjsbhd.model.views.fields.ViewField;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

/**
 * TODO:
 * +- Generate the form view
 * +- Generate form view for inheritance tree
 */
public class FormView extends View implements HasSubView {
    private final String backingClass;
    private final Collection<SubView> subViews = new ArrayList<>();

    public FormView(MCC viewDesc) {
        super(viewDesc, JsTemplates.FORM);
        this.backingClass = viewDesc.getDomainClass().getName();
        populateSubmodules();
    }

    public FormView(ClassAST cls) {
        super(cls.getCls(), JsTemplates.FORM);
        this.backingClass = cls.getName();
        populateSubmodules();
    }

    public FormView(Class cls, Collection<FieldDeclaration> domainFields) {
        super(cls.getSimpleName(), domainFields, JsTemplates.FORM);
        this.backingClass = cls.getSimpleName();
        populateSubmodules();
    }

    private void populateSubmodules() {
        Collection<SubView> subViews = getReferredViews().stream()
                .map(referredView -> referredView.replace("Submodule", ""))
                .map(referredView ->
                        new SubView(referredView,
                                Inflector.getInstance().lowerCamelCase(this.backingClass)))
                .collect(Collectors.toList());
        this.subViews.addAll(subViews);
    }

    public Collection<SubView> getSubViews() {
        return subViews;
    }

    @Override
    public JsTemplate getTemplate() {
        return JsTemplates.FORM;
    }

    String renderForm() {
        return this.getViewFields().stream()
                    .map(ViewField::getAsString)
                    .reduce("", (s1, s2) -> s1 + "\n<br />\n" + s2);
    }

    @Override
    public String getAsString() {
        Inflector inflector = Inflector.getInstance();
        return getTemplate().getAsString()
                .replace("{{ view.name.form }}", backingClass.concat("Form"))
                .replace("{{ view.title }}", getTitle())
                .replace("{{ view.form }}", "return (<>" + renderForm() + "\n</>);")
                .replace("{{ view.submodule.imports }}",
                        this.getReferredViews().stream()
                                .map(view -> String.format("import %s from \"./%s\";", view, view))
                                .reduce("", (s1, s2) -> s1 + "\n" + s2))
                .replace("{{ classNameCamelCase }}",
                        inflector.pluralize(inflector.underscore(backingClass))).replace("_", "-");
    }

    @Override
    public String getFileName() {
        return backingClass.concat("Form");
    }

    private static Class<?> getClassBy(MCC viewDesc) {
        try {
            return Class.forName(viewDesc.getFqn());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Class<?> getClassBy(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
