import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import @slot{{LinkedModule}}Module from "../addresses";

export default class @slot{{LinkedModule}}Submodule extends BaseSubmodule {
  renderModule(props) {
    return (<@slot{{LinkedModule}}Module {...props} excludes={[@slot{{excludeFields}}]} />);
  }
}
