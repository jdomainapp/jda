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



import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file


export default class EnrolmentForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state ,
      date_range:
        [{
            startDate: new Date(),
            endDate: addDays(new Date(),7),
            key: 'selection'
        }]
      
      
    };
  }



  getInputState() {
    return {
        
    }
  }



  async handle_select_date_range(ranges) {
    this.props.handleStateChange("current.startDate", format(ranges.selection.startDate,'yyyy-MM-dd'), false)
    this.props.handleStateChange("current.endDate", format(ranges.selection.endDate,'yyyy-MM-dd'), false)
  }



  renderTitle() {
    return (<>
      Form: Enrolment
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
      {this.props.excludes && this.props.excludes.includes("student") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>Student Id</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.studentId")} onChange={(e) => this.props.handleStateChange("current.studentId", e.target.value, true)} />
          </Col>
          <Col md={9} className='px-0'>
            <Form.Label>Student</Form.Label>
            <FormControl type="text" value={this.renderObject("current.student")} onChange={(e) => this.props.handleStateChange("current.student", e.target.value, false)} readOnly />
          </Col></FormGroup></>}
        <br />
      {this.props.excludes && this.props.excludes.includes("courseModule") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>Course module Id</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.courseModuleId")} onChange={(e) => this.props.handleStateChange("current.courseModuleId", e.target.value, true)} />
          </Col>
          <Col md={9} className='px-0'>
            <Form.Label>Course Module</Form.Label>
            <FormControl type="text" value={this.renderObject("current.courseModule")} onChange={(e) => this.props.handleStateChange("current.courseModule", e.target.value, false)} readOnly />
          </Col></FormGroup></>}
        <br />
        <FormGroup>
          <Form.Label>Internal Mark</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.internalMark")}
          onChange={(e) => {
            this.props.handleStateChange("current.internalMark", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Exam Mark</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.examMark")}
          onChange={(e) => {
            this.props.handleStateChange("current.examMark", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Final Grade</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.finalGrade")}
          onChange={(e) => {
            this.props.handleStateChange("current.finalGrade", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Final mark</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.finalMark")}
          onChange={(e) => {
            this.props.handleStateChange("current.finalMark", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
        <FormGroup {...this.onModelRegionMenuItem("accordion")}>
            <Form.Label>Date range</Form.Label>
            <DateRangePicker
                  onChange={item => this.handle_select_date_range(item)}
                  showSelectionPreview={true}
                  moveRangeOnFirstSelection={false}
                  months={2}
                  ranges={[{
                    startDate: this.props.current && this.props.current.startDate ? Date.parse(this.props.current.startDate) : new Date(),
                    endDate: this.props.current && this.props.current.endDate ? Date.parse(this.props.current.endDate) : addDays(new Date(), 7),
                    key: 'selection'
                  }]}
                  direction="horizontal"
              />
        </FormGroup>
        
      
      </Form>);
  }
}
