import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import {{ view.name.module }} from "../{{ view.dir }}";

export default class {{ view.name.submodule }} extends BaseSubmodule {
  renderModule(props) {
    return (<{{ view.name.module }} {...props} excludes={["{{ view.parent }}"]} />);
  }
}