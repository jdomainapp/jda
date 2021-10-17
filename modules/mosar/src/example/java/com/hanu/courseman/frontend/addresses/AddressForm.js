import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";

import StudentSubmodule from "./StudentSubmodule";

export default class AddressForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
  }

  renderTitle() {
    return (<>
      Form: Address
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
{ this.props.excludes && this.props.excludes.includes("student") ? "" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
    <Form.Label>Student Student ID</Form.Label>
  <FormControl type="text" value={this.renderObject("current.studentId")} onChange={(e) => this.props.handleStateChange("current.studentId", e.target.value, true)}  />
</Col>
<Col md={7} className='px-0'>
    <Form.Label>Student</Form.Label>
  <FormControl type="text" value={this.renderObject("current.student")} onChange={(e) => this.props.handleStateChange("current.student", e.target.value, false)} disabled />
</Col><StudentSubmodule compact={true} mode='submodule'
  viewType={this.props.viewType}
  title="Form: Student"
  current={this.props.current.student}
  currentId={this.props.current.student?.studentId}
  parentName='address' parent={this.props.current}
  parentId={this.props.currentId}
  parentAPI={this.props.mainAPI}
  partialApplyWithCallbacks={this.partialApplyWithCallbacks}
  handleUnlink={() =>
    this.props.handleStateChange("current.student", null, false,
      this.props.handleStateChange("current.studentId", ""))} /></FormGroup></> }
</>);
  }
}
