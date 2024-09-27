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




export default class CourseModuleForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state 
    };
  }


  expand(e, inputID) {
    e.preventDefault()
    var coll = document.getElementById(inputID);
    if (coll.style.maxHeight != "0px") {
      coll.style.maxHeight = "0px";
    } else {
      coll.style.maxHeight = coll.scrollHeight + "px";
    }
  }


  getInputState() {
    return {
        
            description: {
                    optional: true,
                    validated: undefined,
                    message: "",
                    regex: /^[A-Za-z\s]$/,
                    validMsg: "",
                    invalidMsg: "Description must only include characters!"
                  },
        
            credits: {
                    optional: false,
                    validated: undefined,
                    message: "",
                    regex: /^\d+$/,
                    validMsg: "",
                    invalidMsg: "Credits must be a number or a float number!"
                  },
        
    }
  }



  renderTitle() {
    return (<>
      Form: CourseModule
    </>);
  }

    renderSubTypeForm() {
      switch (this.props.current.type) {
      
        case "compulsory":
          return <>
            {this.props.mainForm.consumers.map(consumer => consumer.onModelRegion("skipMenuItem", { num: 0 }))}
            
          </>
        case "elective":
          return <>
            {this.props.mainForm.consumers.map(consumer => consumer.onModelRegion("skipMenuItem", { num: 0 }))}
            
        <br />
        <FormGroup>
          <Form.Label>Dept. Name</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.deptName")}
          onChange={(e) => {
            this.props.handleStateChange("current.deptName", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
          </>
        default:
          return <></>
      }
    }

  renderForm() {
    return (
      <Form>
      <FormGroup>
          <Form.Label>Type</Form.Label>
          <Form.Control
              {...this.onModelRegionMenuItem("accordion")}
              as="select" value={this.renderObject('current.type')}
              onChange={(e)=>{
                this.props.handleTypeChange(e)
               }}
              readOnly={this.props.viewType !== "create"} custom
              isValid={this.props.inputState.id ? this.props.inputState.id.validated : false}
              isInvalid={this.props.inputState.id ? !this.props.inputState.id.validated : false}
          >
            <option value='' readOnly selected>&lt;Please choose one&gt;</option>
            <option value="compulsory">compulsory</option>
            <option value="elective">elective</option>
          </Form.Control>
          {this.props.inputState.id ?
              <Form.Control.Feedback type={this.props.inputState.id.validated ? "valid" : "invalid"}>{this.props.inputState.id.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>
      
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
          <Form.Label>Code</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.code")}
          onChange={(e) => {
            this.props.handleStateChange("current.code", e.target.value, false)
            
          }}
          
           />
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
        <br/>
        <FormGroup>
          <Button 
          {...this.onModelRegionMenuItem("accordion")}
          style={{
            cursor: "pointer",
            padding: "18px",
            width: "100%",
            border: "none",
            textAlign: "left",
            outline: "none",
          }} onClick={(e)=>this.expand(e,"descriptionInput")}>Description</Button>
          <div id="descriptionInput" className={"content"}  style={{
            width: "100%",
            maxHeight: 0,
            overflow: "hidden",
            transition: "max-height 0.2s ease-out",
            backgroundColor: "#f1f1f1",
          }}>
            <textarea style={{
              width: "100%",
              border: "none",
              resize: "none",
              padding: "0",
              backgroundColor: "transparent",
              outline: "none",
              margin: "0"
            }} placeholder={"This is a test collapsible input"} rows={"5"}
            onChange={(e) => {
              this.props.handleStateChange("current.description", e.target.value, false)
              
              this.validate(this.renderObject('current.description'),"description")
              
            }}
            isValid={this.props.inputState.description && this.props.inputState.description.validated !== undefined ? this.props.inputState.description.validated : false}
            isInvalid={this.props.inputState.description && this.props.inputState.description.validated !== undefined ? !this.props.inputState.description.validated : false}
            ></textarea>
          </div>

          {this.props.inputState.description && this.props.inputState.description.validated !== undefined ?
              <Form.Control.Feedback type={this.props.inputState.description.validated ? "valid" : "invalid"}>{this.props.inputState.description.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Semester</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.semester")}
          onChange={(e) => {
            this.props.handleStateChange("current.semester", e.target.value, false)
            
          }}
          
           />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Credits</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.credits")}
          onChange={(e) => {
            this.props.handleStateChange("current.credits", e.target.value, false)
            this.validate(this.renderObject('current.credits'),"credits")
          }}
          
          isValid={this.props.inputState.credits && this.props.inputState.credits.validated !== undefined ? this.props.inputState.credits.validated : false}
          isInvalid={this.props.inputState.credits && this.props.inputState.credits.validated !== undefined ? !this.props.inputState.credits.validated : false}
          
           />
        </FormGroup>
        <br/>
        <FormGroup {...this.onModelRegionMenuItem("accordion")}>
          <Form.Label>Rating</Form.Label>
          <StarRatings
              rating={this.props.current.rating ? this.props.current.rating : 0}
              starRatedColor="blue"
              numberOfStars={5}
              name='rating'
              changeRating={(newRating, name)=>
                {
                  this.props.handleStateChange("current.rating", newRating, false)
                }
              }
          />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Cost</Form.Label>
          <div style={{display: "flex", alignItems: "center"}}>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.cost")} onChange={(e) => this.props.handleStateChange("current.cost", e.target.value, false)}  />
            <span style={{margin: "0 10px"}}>100.0</span>
            <Slider
                min={100.0}
                max={500.0}
                value={this.props.current.cost ? this.props.current.cost : 100.0}
                onChange={(value)=>{this.props.handleStateChange("current.cost", value, false)}}
            />
            <span style={{margin: "0 10px"}}>500.0</span>
          </div>
        </FormGroup>
      {this.renderSubTypeForm()}
      </Form>);
  }
}
