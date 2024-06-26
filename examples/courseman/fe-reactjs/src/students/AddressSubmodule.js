import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import AddressModule from "../addresses";

export default class AddressSubmodule extends BaseSubmodule {
  renderModule(props) {
    return (<AddressModule {...props} id="" name="" subWrapper={this} includeMenu={false} excludes={["student"]} />);
  }
}
