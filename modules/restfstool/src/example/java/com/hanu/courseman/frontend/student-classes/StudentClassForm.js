import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";

import StudentSubmodule from "./StudentSubmodule";

export default class StudentClassForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
  }

  renderTitle() {
    return (<>
      Form: Student class
      </>);
  }

  renderForm() {
    return (<>
<br />
<FormGroup>
  <Form.Label>Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Name</Form.Label>
  <FormControl type="text" value={this.renderObject("current.name")} onChange={(e) => this.props.handleStateChange("current.name", e.target.value, false)}  />
</FormGroup>
<br />
{this.props.excludes && this.props.excludes.includes("student-classes") ? "" : <>
<StudentSubmodule
  mode='submodule'
  viewType={this.props.viewType}
  title="Form: Student"
  current={this.props.current.students}
  thisNamePlural='students'  parentName='student-classes' parent='student-classes'
  parentId={this.props.currentId}
  parentAPI={this.props.mainAPI}
  partialApplyWithCallbacks={this.partialApplyWithCallbacks} /></>}
</>);
  }
}
