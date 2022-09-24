import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";


export default class CourseModuleForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
  }

  renderTitle() {
    return (<>
      Form: Course module
      </>);
  }

  renderForm() {
    switch (this.props.current.type) {
case 'compulsory': return (<><Form>
<FormGroup>
  <Form.Label>Type</Form.Label>
  <Form.Control as="select" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== "create"} custom>
    <option value='' disabled selected>&lt;Please choose one&gt;</option>
    <option value="compulsory">compulsory</option>
    <option value="elective">elective</option>  </Form.Control>
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Code</Form.Label>
  <FormControl type="text" value={this.renderObject("current.code")} onChange={(e) => this.props.handleStateChange("current.code", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Name</Form.Label>
  <FormControl type="text" value={this.renderObject("current.name")} onChange={(e) => this.props.handleStateChange("current.name", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Semester</Form.Label>
  <FormControl type="number" value={this.renderObject("current.semester")} onChange={(e) => this.props.handleStateChange("current.semester", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Credits</Form.Label>
  <FormControl type="number" value={this.renderObject("current.credits")} onChange={(e) => this.props.handleStateChange("current.credits", e.target.value, false)}  />
</FormGroup></Form></>);
case 'elective': return (<><Form>
<FormGroup>
  <Form.Label>Type</Form.Label>
  <Form.Control as="select" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== "create"} custom>
    <option value='' disabled selected>&lt;Please choose one&gt;</option>
    <option value="compulsory">compulsory</option>
    <option value="elective">elective</option>  </Form.Control>
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Code</Form.Label>
  <FormControl type="text" value={this.renderObject("current.code")} onChange={(e) => this.props.handleStateChange("current.code", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Name</Form.Label>
  <FormControl type="text" value={this.renderObject("current.name")} onChange={(e) => this.props.handleStateChange("current.name", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Semester</Form.Label>
  <FormControl type="number" value={this.renderObject("current.semester")} onChange={(e) => this.props.handleStateChange("current.semester", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Credits</Form.Label>
  <FormControl type="number" value={this.renderObject("current.credits")} onChange={(e) => this.props.handleStateChange("current.credits", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Dept name</Form.Label>
  <FormControl type="text" value={this.renderObject("current.deptName")} onChange={(e) => this.props.handleStateChange("current.deptName", e.target.value, false)}  />
</FormGroup></Form></>);
}
  }
}
