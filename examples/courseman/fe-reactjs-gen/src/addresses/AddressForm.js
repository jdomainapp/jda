import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup, Carousel} from "react-bootstrap";
import { DateRangePicker } from 'react-date-range';
import { addDays } from 'date-fns';
import {format} from 'date-fns';
import Slider from 'rc-slider';
import 'rc-slider/assets/index.css';
import StarRatings from 'react-star-ratings';
import starRatings from "react-star-ratings/build/star-ratings";
import BaseForm from "../base/BaseForm";

import StudentSubmodule from "./StudentSubmodule";



export default class AddressForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state 
    };
  }



  getInputState() {
    return {
        
    }
  }



  renderTitle() {
    return (<>
      Form: Address
    </>);
  }

  renderForm() {
    return (
      <Form>
      
      
        <br />
        <FormGroup>
          <Form.Label>Id</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.id")}
          onChange={(e) => {
            this.props.handleStateChange("current.id", e.target.value, false)
            
          }}
          
         readOnly  />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Name</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.name")}
          onChange={(e) => {
            this.props.handleStateChange("current.name", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        {this.props.excludes && this.props.excludes.includes("student") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>Student ID</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.studentId")} onChange={(e) => this.props.handleStateChange("current.studentId", e.target.value, true)} />
          </Col>
          <Col md={7} className='px-0'>
            <Form.Label>Student</Form.Label>
            <FormControl type="text" value={this.renderObject("current.student")} onChange={(e) => this.props.handleStateChange("current.student", e.target.value, false)} readOnly />
          </Col>
          <StudentSubmodule
            {...this.onModelRegionMenuItem("accordion")}
            mainForm={this.props.mainForm}
            compact={true} mode='submodule'
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
                this.props.handleStateChange("current.studentId", ""))} /></FormGroup></>
      }
      
      </Form>);
  }
}
