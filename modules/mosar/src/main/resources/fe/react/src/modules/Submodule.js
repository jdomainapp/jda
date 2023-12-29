import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import @slot{{LinkedModule}}Module from "../@slot{{linked_modules}}";

export default class @slot{{LinkedModule}}Submodule extends BaseSubmodule {
  renderModule(props) {
    return (<@slot{{LinkedModule}}Module id={"@slot{{linkedModule}}"} {...props} subWrapper={this} includeMenu={false} excludes={[@slot{{excludeFields}}]} />);
  }
}
