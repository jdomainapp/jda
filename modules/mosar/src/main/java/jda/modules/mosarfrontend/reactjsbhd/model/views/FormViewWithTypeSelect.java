package jda.modules.mosarfrontend.reactjsbhd.model.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.modeshape.common.text.Inflector;

import com.github.javaparser.ast.body.FieldDeclaration;

import jda.modules.dcsl.parser.ClassAST;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.frontend.MCCUtils;
import jda.modules.mosar.utils.InheritanceUtils;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

public class FormViewWithTypeSelect extends View implements HasSubView {
    private static final Inflector inflector = Inflector.getInstance();
    private final Map<String, String> subtypeMap;
    private final Map<String, FormView> formViewsBySubType = new LinkedHashMap<>();
    private String backingClass;
    private Collection<SubView> subViews;

    public FormViewWithTypeSelect(Class inheritanceRoot) {
        super(JsTemplates.FORM,
                "Form: " + inflector.capitalize(inflector.humanize(
                        inflector.underscore(inheritanceRoot.getSimpleName()))));
        this.subtypeMap = InheritanceUtils.getSubtypeMapFor(inheritanceRoot);
        this.backingClass = inheritanceRoot.getSimpleName();
        addSubtypes(InheritanceUtils.getSubtypesOf(inheritanceRoot));
    }

    @Override
    public Collection<SubView> getSubViews() {
        return formViewsBySubType.values().stream()
                .flatMap(form -> form.getSubViews().stream())
                .collect(Collectors.toList());
    }

    private void addSubtypes(Collection<Class<?>> subClasses) {
        subClasses.forEach(subClass -> addSubtype(subClass, null));
    }

    private void addSubtype(Class cls, MCC mcc) {
        if (mcc == null) {
            ClassAST classAST = createClassAST(cls);
            Collection<FieldDeclaration> domainFields =
                    classAST.getDomainFields() == null ?
                            new ArrayList<>() : classAST.getDomainFields();
            Class superClass = cls.getSuperclass();
            while (superClass != Object.class) {
                ClassAST superClassAST = createClassAST(superClass);
                Collection<FieldDeclaration> superDomainFields = superClassAST.getDomainFields();
                if (superDomainFields != null) {
                    superDomainFields.addAll(domainFields);
                    domainFields.clear();
                    domainFields.addAll(superDomainFields);
                }
                superClass = superClass.getSuperclass();
            }

            FormView formView = new FormView(cls, domainFields);
            String subtypeName = subtypeMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(cls.getName()))
                    .findFirst().get().getKey();
            formViewsBySubType.put(subtypeName, formView);
        } else {

        }
    }

    private ClassAST createClassAST(Class cls) {
        ClassAST classAST = new ClassAST(cls.getSimpleName(), MCCUtils.getFullPath(cls).toString());
        return classAST;
    }

    private String renderTypeSelect() {
        final Collection<String> possibleTypes = subtypeMap.keySet();
        return "<FormGroup>\n" +
                "  <Form.Label>Type</Form.Label>\n" +
                "  <Form.Control as=\"select\" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== \"create\"} custom>\n" +
                "    <option value='' disabled selected>&lt;Please choose one&gt;</option>" +
                possibleTypes.stream().map(type ->
                        String.format("    <option value=\"%s\">%s</option>", type, type))
                .reduce("", (s1, s2) -> s1 + "\n" + s2) +
                "  </Form.Control>\n" +
                "</FormGroup>";
    }

    private String renderForm() {
        final Collection<String> caseStatements =
                this.formViewsBySubType.entrySet().stream()
                        .map(entry -> String.format("case '%s': return (<><Form>%s</Form></>);",
                                entry.getKey(),
                                entry.getValue().renderForm()))
                        .map(str -> str.replace("<Form>",
                                "<Form>\n" + renderTypeSelect()))
                        .collect(Collectors.toList());
        return "switch (this.props.current.type) {\n"
                + String.join("\n", caseStatements)
                + "\n}";
    }

    @Override
    public String getAsString() {
        return getTemplate().getAsString()
                .replace("{{ view.name.form }}", backingClass.concat("Form"))
                .replace("{{ view.title }}", getTitle())
                .replace("{{ view.form }}", renderForm())
                .replace("{{ view.submodule.imports }}",
                        this.getReferredViews().stream()
                                .map(view -> String.format("import %s from \"./%s\";", view, view))
                                .reduce("", (s1, s2) -> s1 + "\n" + s2));
    }

    @Override
    public String getFileName() {
        return backingClass.concat("Form");
    }
}
