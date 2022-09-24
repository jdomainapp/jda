package jda.modules.mosarfrontend.reactjsbhd.model.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modeshape.common.text.Inflector;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mosarfrontend.reactjsbhd.model.ViewableElement;
import jda.modules.mosarfrontend.reactjsbhd.model.common.FieldDefExtensions;
import jda.modules.mosarfrontend.reactjsbhd.model.common.MCCExtensions;
import jda.modules.mosarfrontend.reactjsbhd.model.common.MCCRegistry;
import jda.modules.mosarfrontend.reactjsbhd.model.views.fields.AssociativeInputField;
import jda.modules.mosarfrontend.reactjsbhd.model.views.fields.ViewField;
import jda.modules.mosarfrontend.reactjsbhd.model.views.fields.ViewFieldFactory;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;

public abstract class View implements ViewableElement {
    private static final Inflector inflector = Inflector.getInstance();

    private final String title;
    private final JsTemplate template;
    private final Collection<String> referredViews = new HashSet<>();
    private final Collection<ViewField> viewFields = new ArrayList<>();
    private final String classNameLowerCamel;

    private static String escapeQuotes(String str) {
        return str.replace("\"", "").replace("'", "");
    }

    protected View(JsTemplate template, String title) {
        this.template = template;
        this.title = escapeQuotes(title);
        this.classNameLowerCamel = "";
    }

    protected View(MCC viewDesc, JsTemplate template, boolean discardNonMccFields) {
        this.template = template;
        this.title = escapeQuotes(createTitle(viewDesc));
        this.classNameLowerCamel = inflector.lowerCamelCase(viewDesc.getDomainClass().getName());
        // init view fields
        Collection<FieldDeclaration> fields = viewDesc.getViewFields();
        Collection<FieldDeclaration> domainFields = viewDesc.getDomainClass().getDomainFields();
        this.viewFields.addAll(generateViewFields(domainFields, fields, discardNonMccFields));
        this.referredViews.addAll(
                this.viewFields.stream()
                        .map(field -> field.getReferredView())
                        .filter(referredView -> !referredView.isEmpty())
                        .collect(Collectors.toList()));
    }

    public View(MCC viewDesc, JsTemplate template) {
        this(viewDesc, template, false);
    }

    public View(ClassOrInterfaceDeclaration dClass, JsTemplate template) {
        this.template = template;
        this.title = escapeQuotes(createTitle(dClass));
        this.classNameLowerCamel = inflector.lowerCamelCase(dClass.getNameAsString());
        // init view fields
        Collection<FieldDeclaration> domainFields =
                ParserToolkit.getDomainFields(dClass);
        this.viewFields.addAll(generateViewFields(
                domainFields, null, false));
        this.referredViews.addAll(
                this.viewFields.stream()
                    .map(field -> field.getReferredView())
                    .filter(referredView -> !referredView.isEmpty())
                    .collect(Collectors.toList()));
    }

    protected View(String className,
            Collection<FieldDeclaration> domainFields,
            JsTemplate template) {
        this.template = template;
        this.title = "";
        this.classNameLowerCamel = inflector.lowerCamelCase(className);
        // init view fields

        this.viewFields.addAll(generateViewFields(
                domainFields, null, false));
        this.referredViews.addAll(
                this.viewFields.stream()
                        .map(field -> field.getReferredView())
                        .filter(referredView -> !referredView.isEmpty())
                        .collect(Collectors.toList()));
    }

    protected Collection<String> getReferredViews() {
        return this.referredViews;
    }

    public boolean isFormView() {
        return this instanceof FormView || this instanceof FormViewWithTypeSelect;
    }

    public String getTitle() {
        return this.title;
    }

    public Collection<ViewField> getViewFields() {
        return viewFields;
    }

    public abstract String getFileName();

    @Override
    public JsTemplate getTemplate() {
        return this.template;
    }

    // TODO
    public String getReferredViewModules() {
        return "";
    }

    private static String createTitle(MCC mcc) {
        return mcc.getPropertyVal("viewDesc", "formTitle").toString();
    }

    private static String createTitle(ClassOrInterfaceDeclaration dClass) {
        return "Form: " + inflector.humanize(inflector.underscore(dClass.getNameAsString()));
    }

    // lookup fields: fields that are not of the same kind as the 1st param
    private ViewField viewFieldFromFieldDeclaration(
            final FieldDeclaration domainOrViewField,
            final Collection<FieldDeclaration> lookupFields,
            final boolean firstParamIsDomainField) {
        final FieldDeclaration domainField = firstParamIsDomainField ?
                domainOrViewField :
                FieldDefExtensions.getCorrespondingViewField(domainOrViewField, lookupFields);

        final FieldDeclaration viewFieldDeclaration = firstParamIsDomainField ?
                FieldDefExtensions.getCorrespondingViewField(domainField, lookupFields)
                : domainOrViewField;
        final FieldDef domainFieldDef = ParserToolkit.getFieldDefFull(domainField);

        if (FieldDefExtensions.isPrimitiveOrEnumType(domainFieldDef)) {
            final String label;
            if (Objects.isNull(viewFieldDeclaration)) {
                label = inflector.capitalize(
                        inflector.humanize(
                                inflector.underscore(domainFieldDef.getName())));
            } else {
                label = viewFieldDeclaration
                        .getAnnotationByClass(AttributeDesc.class)
                        .map(annotation -> (NormalAnnotationExpr) annotation)
                        .flatMap(annotation -> annotation.getPairs().stream()
                                .filter(pair -> pair.getNameAsString().equals("label"))
                                .findFirst())
                        .map(ParserToolkit::parseAnoMemberValue)
                        .map(ParserToolkit::convertPropValuetoString)
                        .orElse(inflector.capitalize(inflector.humanize(
                                inflector.underscore(ParserToolkit.getFieldName(domainField)))));
            }
            return ViewFieldFactory.create(domainFieldDef, label);
        }

        // is 1-M type
        final Map<String, Object> association = FieldDefExtensions.getAssociation(domainFieldDef);
        if (association.get("ascType") == DAssoc.AssocType.One2Many
                && association.get("endType") == DAssoc.AssocEndType.One) {
            return ViewFieldFactory.create(domainFieldDef,
                    domainFieldDef.getType()
                            .asClassOrInterfaceType()
                            .getTypeArguments().get()
                            .get(0).asClassOrInterfaceType()
                            .getNameAsString(), "");
        }

        // 1-1 or M-1
        final String fieldTypeName = domainFieldDef.getType().asClassOrInterfaceType().getNameAsString();
        final MCC mcc = MCCRegistry.getInstance().getByName(fieldTypeName);
        final FieldDef idFieldDef = MCCExtensions.getIdFieldDef(mcc);
        final String idLabel = mcc.getViewFields().stream()
                .filter(field -> ParserToolkit.getFieldName(field)
                        .equals(idFieldDef.getName()))
                .map(field -> ParserToolkit.getAnnotation(field, AttributeDesc.class))
                .map(annotation -> annotation.getPairs().stream()
                        .filter(pair -> pair.getNameAsString().equals("label"))
                        .findFirst().get().getValue().toString())
                .findFirst().orElse("id");
        AssociativeInputField field = (AssociativeInputField) ViewFieldFactory.create(domainFieldDef, idFieldDef,
                idLabel, association.get("ascType"));
        field.setParent(this.classNameLowerCamel);
        return field;
    }

    private Collection<ViewField> generateViewFields(
            Collection<FieldDeclaration> domainFields,
            Collection<FieldDeclaration> viewFields,
            boolean discardNonMccFields
    ) {
        final Collection<FieldDeclaration> fieldList = discardNonMccFields ?
                viewFields : domainFields;
        final Collection<FieldDeclaration> lookupFields = !discardNonMccFields ?
                viewFields : domainFields;
        if (Objects.isNull(lookupFields)) {
            return fieldList.stream()
                    .map(field -> viewFieldFromFieldDeclaration(field, fieldList, !discardNonMccFields))
//                    .map(ParserToolkit::getFieldDefFull)
//                    .map(ViewFieldFactory::create)
                    .sorted()
                    .collect(Collectors.toList());
        }

        return fieldList.stream()
                .filter(field -> !ParserToolkit.getFieldName(field).equals("title"))
                .map(field -> viewFieldFromFieldDeclaration(field, lookupFields, !discardNonMccFields))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public String getAsString() {
        return this.getTitle();
    }
}
