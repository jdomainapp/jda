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

import AddressSubmodule from "./AddressSubmodule";
import EnrolmentSubmodule from "./EnrolmentSubmodule";



export default class StudentForm extends BaseForm {
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
      Form: Student
    </>);
  }

  renderForm() {
    return (
      <Form>
      
      
        <br />
        <FormGroup>
          <Form.Label>Id</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.id")}
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
        <br />
      <FormGroup>
        <Form.Label>Gender</Form.Label>
        <FormControl {...this.onModelRegionMenuItem("accordion")} as="select" value={this.renderObject("current.gender")}
          onChange={(e) => this.props.handleStateChange("current.gender", e.target.value, false)} custom  >
          <option value='' readOnly selected>&lt;Please choose one&gt;</option>
          <option value="Male">Male</option>
          <option value="Female">Female</option>
        </FormControl>
      </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Date of birth</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="date" value={this.renderObject("current.dob")}
          onChange={(e) => {
            this.props.handleStateChange("current.dob", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        {this.props.excludes && this.props.excludes.includes("address") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>Address ID</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.addressId")} onChange={(e) => this.props.handleStateChange("current.addressId", e.target.value, true)} />
          </Col>
          <Col md={7} className='px-0'>
            <Form.Label>Current Address</Form.Label>
            <FormControl type="text" value={this.renderObject("current.address")} onChange={(e) => this.props.handleStateChange("current.address", e.target.value, false)} readOnly />
          </Col>
          <AddressSubmodule
            {...this.onModelRegionMenuItem("accordion")}
            mainForm={this.props.mainForm}
            compact={true} mode='submodule'
            viewType={this.props.viewType}
            title="Form: Address"
            current={this.props.current.address}
            currentId={this.props.current.address?.addressId}
            parentName='student' parent={this.props.current}
            parentId={this.props.currentId}
            parentAPI={this.props.mainAPI}
            partialApplyWithCallbacks={this.partialApplyWithCallbacks}
            handleUnlink={() =>
              this.props.handleStateChange("current.address", null, false,
                this.props.handleStateChange("current.addressId", ""))} /></FormGroup></>
      }
        <br />
        <FormGroup>
          <Form.Label>Email</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.email")}
          onChange={(e) => {
            this.props.handleStateChange("current.email", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
      {this.props.excludes && this.props.excludes.includes("studentClass") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>Student class Id</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.studentClassId")} onChange={(e) => this.props.handleStateChange("current.studentClassId", e.target.value, true)} />
          </Col>
          <Col md={9} className='px-0'>
            <Form.Label>Student class</Form.Label>
            <FormControl type="text" value={this.renderObject("current.studentClass")} onChange={(e) => this.props.handleStateChange("current.studentClass", e.target.value, false)} readOnly />
          </Col></FormGroup></>}
         {this.props.excludes && this.props.excludes.includes("enrolments") ? "" : <>
        <EnrolmentSubmodule
          {...this.onModelRegionMenuItem("accordion")}
          mainForm={this.props.mainForm}
          mode='submodule'
          viewType={this.props.viewType}
          title="Form: Enrolment"
          current={this.props.current.enrolments}
          thisNamePlural='enrolments' parentName='student' parent='student'
          parentId={this.props.currentId}
          parentAPI={this.props.mainAPI}
          partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
      </>}
      
      </Form>);
  }
}
