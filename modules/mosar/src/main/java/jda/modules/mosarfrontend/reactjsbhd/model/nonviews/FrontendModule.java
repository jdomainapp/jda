package jda.modules.mosarfrontend.reactjsbhd.model.nonviews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.modeshape.common.text.Inflector;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.utils.ClassAssocUtils;
import jda.modules.mosar.utils.InheritanceUtils;
import jda.modules.mosarfrontend.reactjsbhd.model.JsFrontendElement;
import jda.modules.mosarfrontend.reactjsbhd.model.views.View;
import jda.modules.mosarfrontend.reactjsbhd.model.views.ViewFactory;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

// a view's index.js
public class FrontendModule implements JsFrontendElement {
    private static final Inflector inflector = Inflector.getInstance();
    private MCC viewDesc;
    private final String mainAPI;
    private final String title;
    private final Collection<String> apiNames = new ArrayList<>();
    private final Collection<String> possibleTypes = new ArrayList<>();
    private final Collection<View> views = new ArrayList<>();

    public FrontendModule(Class cls, MCC mcc) {
        this.viewDesc = mcc;
        this.title = escapeQuotes(createTitle(mcc));
        this.mainAPI = inflector.lowerCamelCase(viewDesc.getDomainClass().getName()).concat("API");
        views.add(ViewFactory.createFormView(cls));
        views.add(ViewFactory.createListView(viewDesc));
        makeApiNames(viewDesc);
        makePossibleTypes(cls);
    }

    private static String createTitle(MCC mcc) {
        return mcc.getPropertyVal("viewDesc", "formTitle").toString();
    }

    private void makePossibleTypes(Class cls) {
        this.possibleTypes.addAll(InheritanceUtils.getSubtypeMapFor(cls).keySet());
    }

    private void makeApiNames(final MCC viewDesc) {
        try {
            ClassAssocUtils.getAssociated(Class.forName(viewDesc.getDomainClass().getFqn()))
                    .stream()
                    .map(Class::getSimpleName)
                    .map(inflector::lowerCamelCase)
                    .map(name -> name + "API")
                    .forEach(apiNames::add);
            apiNames.add(mainAPI);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<View> getViews() {
        return this.views;
    }

    public MCC getViewDesc() {
        return this.viewDesc;
    }

    public String getModuleAlias() {
        return this.viewDesc.getName();
    }

    public String getFolder() {
        return inflector.pluralize(
                inflector.underscore(
                        this.viewDesc.getDomainClass().getName())
                .replace("_", "-"));
    }

    private static String makePlural(String original) {
        return inflector.pluralize(inflector.pluralize(inflector.underscore(original)))
                .replace("_", "-");
    }

    private static String escapeQuotes(String str) {
        return str.replace("\"", "").replace("'", "");
    }

    private static String makeApiDeclaration(final String apiName) {
        final String objName = apiName.replace("API", "");
        return String.format("const %sAPI = new BaseAPI(\"%s\", providers.axios);\n",
                objName, lowerFirstChar(makePlural(objName)));
    }

    private static String lowerFirstChar(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    public JsTemplate getTemplate() {
        return JsTemplates.INDEX;
    }

    @Override
    public String getAsString() {
        String baseName = viewDesc.getDomainClass().getName();
        return getTemplate().getAsString()
                .replace("{{ view.name.list }}", baseName.concat("ListView"))
                .replace("{{ view.name.form }}", baseName.concat("Form"))
                .replace("{{ view.name.main }}", baseName.concat("MainView"))
                .replace("{{ view.name.module }}", baseName.concat("Module"))
                .replace("{{ possibleTypes }}",
                        String.format("return [%s]",
                                String.join(",",
                                        possibleTypes.stream().map(s -> "'" + s + "'")
                                                .collect(Collectors.toList()))))
                .replace("{{ view.title }}", title)
                .replace("{{ view.apis.declarations }}",
                        apiNames.stream()
                                .map(FrontendModule::makeApiDeclaration)
                                .reduce("", (s1, s2) -> s1 + "\n" + s2))
                .replace("{{ view.api.bindings }}",
                        apiNames.stream()
                                .map(name -> String.format("%s={%s}", name, name))
                                .reduce("", (s1, s2) -> s1 + "\n" + s2))
                .replace("{{ view.api.main }}", mainAPI);
    }

}
