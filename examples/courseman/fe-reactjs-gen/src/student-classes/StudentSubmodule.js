import React from "react";
import BaseSubmodule from "../base/BaseSubmodule";
import StudentModule from "../students";
import {studentClasses} from "../common/Constants";

export default class StudentSubmodule extends BaseSubmodule {
  renderModule(props) {
    return (<StudentModule {...props} id="" name="" subWrapper={this} includeMenu={false} excludes={["studentClass"]} />);
  }
}
