import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import EnrolmentModule from "../enrolments";

export default class EnrolmentSubmodule extends BaseSubmodule {
  renderModule(props) {
    return (<EnrolmentModule {...props} excludes={["student"]} />);
  }
}
