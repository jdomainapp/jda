import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import StudentModule from "../students";

export default class StudentSubmodule extends BaseSubmodule {
  renderModule(props) {
    return (<StudentModule {...props} excludes={["studentClass"]} />);
  }
}
