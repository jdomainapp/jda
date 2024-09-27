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



export default class StudentClassForm extends BaseForm {
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
      Form: StudentClass
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
         {this.props.excludes && this.props.excludes.includes("students") ? "" : <>
        <StudentSubmodule
          {...this.onModelRegionMenuItem("accordion")}
          mainForm={this.props.mainForm}
          mode='submodule'
          viewType={this.props.viewType}
          title="Form: Student"
          current={this.props.current.students}
          thisNamePlural='students' parentName='student-class' parent='student-class'
          parentId={this.props.currentId}
          parentAPI={this.props.mainAPI}
          partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
      </>}
      
      </Form>);
  }
}
