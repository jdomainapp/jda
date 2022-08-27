import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";


export default class EnrolmentForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
  }

  renderTitle() {
    return (<>
      Form: Enrolment
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
{ this.props.excludes && this.props.excludes.includes("student") ? "" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
    <Form.Label>Student Student ID</Form.Label>
  <FormControl type="text" value={this.renderObject("current.studentId")} onChange={(e) => this.props.handleStateChange("current.studentId", e.target.value, true)}  />
</Col>
<Col md={9} className='px-0'>
    <Form.Label>Student</Form.Label>
  <FormControl type="text" value={this.renderObject("current.student")} onChange={(e) => this.props.handleStateChange("current.student", e.target.value, false)} disabled />
</Col></FormGroup></> }
<br />
{ this.props.excludes && this.props.excludes.includes("courseModule") ? "" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
    <Form.Label>Course module Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.courseModuleId")} onChange={(e) => this.props.handleStateChange("current.courseModuleId", e.target.value, true)}  />
</Col>
<Col md={9} className='px-0'>
    <Form.Label>Course module</Form.Label>
  <FormControl type="text" value={this.renderObject("current.courseModule")} onChange={(e) => this.props.handleStateChange("current.courseModule", e.target.value, false)} disabled />
</Col></FormGroup></> }
<br />
<FormGroup>
  <Form.Label>Internal mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.internalMark")} onChange={(e) => this.props.handleStateChange("current.internalMark", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Exam mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.examMark")} onChange={(e) => this.props.handleStateChange("current.examMark", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Final grade</Form.Label>
  <FormControl type="text" value={this.renderObject("current.finalGrade")} onChange={(e) => this.props.handleStateChange("current.finalGrade", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup>
  <Form.Label>Final mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.finalMark")} onChange={(e) => this.props.handleStateChange("current.finalMark", e.target.value, false)} disabled />
</FormGroup>
</>);
  }
}
