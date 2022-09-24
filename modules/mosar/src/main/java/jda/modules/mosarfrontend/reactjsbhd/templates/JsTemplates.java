package jda.modules.mosarfrontend.reactjsbhd.templates;

public enum JsTemplates implements JsTemplate {
    APP {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("app");
        }
    },
    INDEX {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("module");
        }
    },
    FORM {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("form");
        }
    },
    SUBFORM {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("subform");
        }
    },
    LIST {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("list");
        }
    },
    LIST_ITEM {
        @Override
        public String getAsString() {
            return TemplateReader.readFromFile("list_item");
        }
    },
    SIMPLE_VIEW_FIELD {
        @Override
        public String getAsString() {
            return "<FormGroup>\n" +
                    "  <Form.Label>{{ fieldLabel }}</Form.Label>\n" +
                    "  <FormControl type=\"{{ fieldType }}\"" +
                    " value={this.renderObject(\"current.{{ backingField }}\")}" +
                    " onChange={(e) => this.props.handleStateChange(\"current.{{ backingField }}\", e.target.value, {{ needApiCall }})}" +
                    " {{ disabledFlag }} />\n" +
                    "</FormGroup>";
        }
    },
    SELECT_OPTION {
        @Override
        public String getAsString() {
            return "<FormGroup>\n" +
                    "  <Form.Label>{{ fieldLabel }}</Form.Label>\n" +
                    "  <FormControl as=\"select\"" +
                    " value={this.renderObject(\"current.{{ backingField }}\")}" +
                    " onChange={(e) => this.props.handleStateChange(\"current.{{ backingField }}\", e.target.value, {{ needApiCall }})} custom  >\n" +
                    "    <option value='' disabled selected>&lt;Please choose one&gt;</option>" +
                    "    {{ options }}" +
                    "  </FormControl>" +
                    "</FormGroup>";
        }
    },
    CHECKBOX {
        @Override
        public String getAsString() {
            return null;
        }
    },
    ONE_ONE_INPUT_FIELD {
        @Override
        public String getAsString() {
            return "{ this.props.excludes && this.props.excludes.includes(\"{{ classNameCamelCase }}\") ? \"\" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>\n" +
                    "  {{ idField }}" +
                    "</Col>\n" +
                    "<Col md={7} className='px-0'>\n" +
                    "  {{ detailsField }}" +
                    "</Col><{{ submodule }} compact={true} mode='submodule'\n" +
                    "  viewType={this.props.viewType}\n" +
                    "  title=\"{{ classNameHumanReadable }}\"\n" +
                    "  current={this.props.current.{{ backingField }}}\n" +
                    "  currentId={this.props.current.{{ detailsBackingField }}?.{{ idBackingField }}}\n" +
                    "  parentName='{{ parentClassNameCamelCase }}' parent={this.props.current}\n" +
                    "  parentId={this.props.currentId}\n" +
                    "  parentAPI={this.props.mainAPI}\n" +
                    "  partialApplyWithCallbacks={this.partialApplyWithCallbacks}\n" +
                    "  handleUnlink={() =>\n" +
                    "    this.props.handleStateChange(\"current.{{ backingField }}\", null, false,\n" +
                    "      this.props.handleStateChange(\"current.{{ idBackingField }}\", \"\"))} /></FormGroup></> }";
        }
    },
    SUBMODULE_FIELD {
        @Override
        public String getAsString() {
            return "{this.props.excludes && this.props.excludes.includes(\"{{ classNameCamelCase }}\") ? \"\" : <>\n" +
                    "<{{ submodule }}\n" +
                    "  mode='submodule'\n" +
                    "  viewType={this.props.viewType}\n" +
                    "  title=\"{{ classNameHumanReadable }}\"\n" +
                    "  current={this.props.current.{{ backingField }}}\n" +
                    "  thisNamePlural='{{ thisNamePlural }}'" +
                    "  parentName='{{ classNameCamelCase }}' parent='{{ classNameCamelCase }}'\n" +
                    "  parentId={this.props.currentId}\n" +
                    "  parentAPI={this.props.mainAPI}\n" +
                    "  partialApplyWithCallbacks={this.partialApplyWithCallbacks} /></>}";
        }
    },
    MANY_ONE_INPUT_FIELD {
        @Override
        public String getAsString() {
            return "{ this.props.excludes && this.props.excludes.includes(\"{{ classNameCamelCase }}\") ? \"\" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>\n" +
                    "  {{ idField }}" +
                    "</Col>\n" +
                    "<Col md={9} className='px-0'>\n" +
                    "  {{ detailsField }}" +
                    "</Col></FormGroup></> }";
        }
    }

}
