import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";


import { DateRangePicker } from 'react-date-range';
import { addDays } from 'date-fns';
import {format} from 'date-fns';


export default class EnrolmentForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state,
      ranges:
          [{
            startDate: new Date(),
            endDate: addDays(new Date(), 7),
            key: 'selection'
          }],
    };
  }

  //test date range picker
  async handleSelect(ranges) {
    // this.props.handleStateChange("current.startDate", format(ranges.selection.startDate,'yyyy-MM-dd'), false)
    // this.props.handleStateChange("current.endDate", format(ranges.selection.endDate,'yyyy-MM-dd'), false)
    this.props.handleStateChange("current.startDate", ranges.selection.startDate, false)
    this.props.handleStateChange("current.endDate", ranges.selection.endDate, false)
  }

  renderTitle() {
    return (<>
      Form: Enrolment
      </>);
  }

  renderForm() {
    return (<>
<br />
<FormGroup id="id">
  <Form.Label>Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
</FormGroup>
<br />
{ this.props.excludes && this.props.excludes.includes("student") ? "" : <><FormGroup id="student-id" className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
    <Form.Label>Student Student ID</Form.Label>
  <FormControl type="text" value={this.renderObject("current.studentId")} onChange={(e) => this.props.handleStateChange("current.studentId", e.target.value, true)}  />
</Col>
<Col md={9} className='px-0'>
    <Form.Label>Student</Form.Label>
  <FormControl type="text" value={this.renderObject("current.student")} onChange={(e) => this.props.handleStateChange("current.student", e.target.value, false)} disabled />
</Col></FormGroup></> }
<br />
{ this.props.excludes && this.props.excludes.includes("courseModule") ? "" : <><FormGroup id="course" className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
    <Form.Label>Course module Id</Form.Label>
  <FormControl type="number" value={this.renderObject("current.courseModuleId")} onChange={(e) => this.props.handleStateChange("current.courseModuleId", e.target.value, true)}  />
</Col>
<Col md={9} className='px-0'>
    <Form.Label>Course module</Form.Label>
  <FormControl type="text" value={this.renderObject("current.courseModule")} onChange={(e) => this.props.handleStateChange("current.courseModule", e.target.value, false)} disabled />
</Col></FormGroup></> }
<br />
<FormGroup id="internal-mark">
  <Form.Label>Internal mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.internalMark")} onChange={(e) => this.props.handleStateChange("current.internalMark", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup id="exam-mark">
  <Form.Label>Exam mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.examMark")} onChange={(e) => this.props.handleStateChange("current.examMark", e.target.value, false)}  />
</FormGroup>
<br />
<FormGroup id="final-grade">
  <Form.Label>Final grade</Form.Label>
  <FormControl type="text" value={this.renderObject("current.finalGrade")} onChange={(e) => this.props.handleStateChange("current.finalGrade", e.target.value, false)} disabled />
</FormGroup>
<br />
<FormGroup id="final-mark">
  <Form.Label>Final mark</Form.Label>
  <FormControl type="number" value={this.renderObject("current.finalMark")} onChange={(e) => this.props.handleStateChange("current.finalMark", e.target.value, false)} disabled />
</FormGroup>

<FormGroup id="date-range">
  <Form.Label>Date Range</Form.Label>
  <DateRangePicker
      onChange={item => this.handleSelect(item)}
      showSelectionPreview={true}
      moveRangeOnFirstSelection={false}
      months={2}
      ranges={[{
        startDate: this.props.current && this.props.current.startDate ? this.props.current.startDate : new Date(),
        endDate: this.props.current && this.props.current.endDate ? this.props.current.endDate : addDays(new Date(), 7),
        key: 'selection'
      }]}
      direction="horizontal"
  />
</FormGroup>
</>);
  }
}
