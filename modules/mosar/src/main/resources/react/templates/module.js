// import {{ view.name.main }} from "./{{ view.name.main }}";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import {{ view.name.list }} from "./{{ view.name.list }}";
import {{ view.name.form }} from "./{{ view.name.form }}";
// {{ view.submodule.imports }}
{{ view.apis.declarations }}

export default function {{ view.name.module }}(props) {
  return <{{ view.name.main }}
    mainAPI={{{ view.api.main }}}
    {{ view.api.bindings }}
    {...props}
  />
}

// export default
class {{ view.name.main }} extends BaseMainForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state,
      current: {
        ...this.state.current,
        ...this.props.current,
        type: this.getPossibleTypes.bind(this)() ? this.getPossibleTypes()[0] : undefined
      },
      currentId: this.props.currentId
    }
  }
  getPossibleTypes() {
    {{ possibleTypes }}
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">{{ view.title }}</h2>
      </>
    );
  }

  renderListView() {
    return <{{ view.name.list }} {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <{{ view.name.form }} {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
