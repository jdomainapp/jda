package jda.modules.mosarfrontend.reactjsbhd.model.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.modeshape.common.text.Inflector;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.utils.ClassAssocUtils;
import jda.modules.mosarfrontend.reactjsbhd.model.ViewableElement;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplate;
import jda.modules.mosarfrontend.reactjsbhd.templates.JsTemplates;

public class ListView extends View {
    private final String backingClass;
    private final ListItemView listItemView;
    private final String mainAPI;
    private final Collection<String> apiNames = new ArrayList<>();
    private final Inflector inflector = Inflector.getInstance();

    public ListView(MCC viewDesc) {
        super(viewDesc, JsTemplates.LIST, true);
        this.backingClass = viewDesc.getDomainClass().getName();
        this.listItemView = new ListItemView();

        this.mainAPI = inflector.lowerCamelCase(viewDesc.getDomainClass().getName()).concat("API");
        makeApiNames(viewDesc);
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

    // view field -> <td>{field.name}</td>

    private Collection<String> getHeadings() {
        return getViewFields().stream()
                .filter(viewField -> !viewField.isOneManyField())
                .map(viewField -> {
                    if (viewField.isAssociativeField()) {
                        return String.format("{!(this.props.excludes?.includes('%s')) && <th>%s</th>}",
                                viewField.getBackingField(), viewField.getLabel());
                    }
                    return String.format("<th>%s</th>", viewField.getLabel());
                })
                .collect(Collectors.toList());
    }

    private Collection<String> getApiNames() {
        return this.apiNames;
    }

    @Override
    public String getAsString() {
        return getTemplate().getAsString()
                .replace("{{ view.name.list }}", backingClass.concat("ListView"))
                .replace("{{ view.list.headings }}", String.join("\n", getHeadings()))
                .replace("{{ view.name.listItem }}", backingClass.concat("ListItemView"))
                .replace("{{ view.api.main }}", mainAPI)
                .replace("{{ view.api.bindings }}",
                        apiNames.stream()
                                .map(name -> String.format("%s={this.props.%s}", name, name))
                                .reduce("", (s1, s2) -> s1 + " " + s2))
                .concat("\n")
                .concat(this.listItemView.getAsString());
    }

    @Override
    public String getFileName() {
        return backingClass.concat("ListView");
    }

    private class ListItemView implements ViewableElement {

        @Override
        public JsTemplate getTemplate() {
            return JsTemplates.LIST_ITEM;
        }

        private Collection<String> getVisibleColumns() {
            return getViewFields().stream()
                    .filter(viewField -> !viewField.isOneManyField())
                    .map(viewField -> {
                        if (viewField.isAssociativeField()) {
                            return String.format(
                                    "{!(this.props.excludes?.includes('%s')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.%s)}</td>}",
                                    viewField.getBackingField(),
                                    viewField.getBackingField());
                        }
                        return String.format(
                                "<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.%s)}</td>",
                                viewField.getBackingField());
                    })
                    .collect(Collectors.toList());
        }

        @Override
        public String getAsString() {
            return getTemplate().getAsString()
                    .replace("{{ view.name.listItem }}", backingClass.concat("ListItemView"))
                    .replace("{{ displayFields }}", String.join("\n", getVisibleColumns()));
        }
    }
}
