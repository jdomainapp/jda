import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup, Carousel} from "react-bootstrap";
import BaseForm from "../base/BaseForm";

//test date range
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file

import { DateRangePicker } from 'react-date-range';
import { addDays } from 'date-fns';

//test slider
import Slider from 'rc-slider';
import 'rc-slider/assets/index.css';

//test ratings
import StarRatings from 'react-star-ratings';
import starRatings from "react-star-ratings/build/star-ratings";
//
// import AirDatepicker from 'air-datepicker'
// import 'air-datepicker/air-datepicker.css'

export default class CourseModuleForm extends BaseForm {
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
      // validation object

    };
  }

  expand(e) {
    e.preventDefault()
    var coll = document.getElementById("input");
    if (coll.style.maxHeight != "0px"){
      coll.style.maxHeight = "0px";
    } else {
      coll.style.maxHeight = coll.scrollHeight + "px";
    }
  }

  getInputState() {
    return {
      // id: {
      //   optional: false,
      //   validated: undefined,
      //   message: "",
      //   regex: /^S\d+$/,
      //   validMsg: "",
      //   invalidMsg: ""
      // },
      // code: {
      //   optional: false,
      //   validated: undefined,
      //   message: "",
      //   regex: /^S\d+$/,
      //   validMsg: "",
      //   invalidMsg: ""
      // },
      name: {
        optional: false,
            validated: undefined,
            message: "",
            regex: /^S\d+$/,
            validMsg: "",
            invalidMsg: "Name must start with 'S' and followed by one or more numbers!"
      },
      description: {
        optional: true,
            validated: undefined,
            message: "",
            regex: /^[A-Za-z\s]+$/,
            validMsg: "",
            invalidMsg: "Description must only include characters!"
      },
      // semester: {
      //   optional: false,
      //   validated: undefined,
      //   message: "",
      //   regex: /^S\d+$/,
      //   validMsg: "",
      //   invalidMsg: ""
      // },
      credits: {
        optional: false,
            validated: undefined,
            message: "",
            regex: /^\d+$/,
            validMsg: "",
            invalidMsg: "Name must be a number or a float number!"
      },
      // rating: {
      //   optional: true,
      //   validated: undefined,
      //   message: "",
      //   regex: /^S\d+$/,
      //   validMsg: "",
      //   invalidMsg: ""
      // },
      // cost: {
      //   optional: true,
      //   validated: undefined,
      //   message: "",
      //   regex: /^S\d+$/,
      //   validMsg: "",
      //   invalidMsg: ""
      // }
    }
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
          <Form.Control
              {...this.onModelRegionMenuItem("accordion")}
              as="select" value={this.renderObject('current.type')}
              onChange={(e)=>{
                this.props.handleTypeChange(e)
               }}
              disabled={this.props.viewType !== "create"} custom
              isValid={this.props.inputState.id ? this.props.inputState.id.validated : false}
              isInvalid={this.props.inputState.id ? !this.props.inputState.id.validated : false}
          >
            <option value='' disabled selected>&lt;Please choose one&gt;</option>
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
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Code</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="text" value={this.renderObject("current.code")} onChange={(e) => this.props.handleStateChange("current.code", e.target.value, false)} disabled />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Name</Form.Label>
          <FormControl 
                      {...this.onModelRegionMenuItem("accordion")}
                      type="text" value={this.renderObject("current.name")}
                      onChange={(e) => {
                         this.props.handleStateChange("current.name", e.target.value, false)
                         this.validate(
                             this.renderObject('current.name'),
                             "name"
                         )
                       }}
                      isValid={this.props.inputState.name && this.props.inputState.name.validated !== undefined ? this.props.inputState.name.validated : false}
                      isInvalid={this.props.inputState.name && this.props.inputState.name.validated !== undefined ? !this.props.inputState.name.validated : false}
          />
          {this.props.inputState.name && this.props.inputState.name.validated !== undefined ?
              <Form.Control.Feedback type={this.props.inputState.name.validated ? "valid" : "invalid"}>{this.props.inputState.name.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Semester</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.semester")} onChange={(e) => this.props.handleStateChange("current.semester", e.target.value, false)}  />
        </FormGroup>
        <br />
        <Carousel>
          <Carousel.Item>
            <h3>1</h3>
          </Carousel.Item>
          <Carousel.Item>
            <h3>2</h3>
          </Carousel.Item>
          <Carousel.Item>
            <h3>3</h3>
          </Carousel.Item>
          <Carousel.Item>
            <h3>4</h3>
          </Carousel.Item>
          <Carousel.Item>
            <h3>5</h3>
          </Carousel.Item>
          <Carousel.Item>
            <h3>6</h3>
          </Carousel.Item>
        </Carousel>
        <br />
        <FormGroup>
          <Form.Label>Cost</Form.Label>
          <div style={{display: "flex", alignItems: "center"}}>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.cost")} onChange={(e) => this.props.handleStateChange("current.cost", e.target.value, false)}  />
            <span style={{margin: "0 10px"}}>100</span>
            <Slider
                min={100}
                max={500}
                value={this.props.current.cost ? this.props.current.cost : 100}
                onChange={(value)=>{this.props.handleStateChange("current.cost", value, false)}}
            />
            <span style={{margin: "0 10px"}}>500</span>
          </div>
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
          }} onClick={(e)=>this.expand(e)}>Description</Button>
          <div id="input" className={"content"}  style={{
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
              this.validate(
                  this.renderObject('current.description'),
                  "description",
              )
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
        <br/>
        <FormGroup>
          <Form.Label>Credits</Form.Label>
          <FormControl 
                      {...this.onModelRegionMenuItem("accordion")}
                      type="number" value={this.renderObject("current.credits")}
                      onChange={(e) => {
                         this.props.handleStateChange("current.credits", e.target.value, false)
                         this.validate(
                             this.renderObject('current.credits'),
                             "credits"
                         )
                       }}
                       isValid={this.props.inputState.credits && this.props.inputState.credits.validated !== undefined ? this.props.inputState.credits.validated : false}
                       isInvalid={this.props.inputState.credits && this.props.inputState.credits.validated !== undefined ? !this.props.inputState.credits.validated : false}
          />

          {this.props.inputState.credits && this.props.inputState.credits.validated !== undefined ?
              <Form.Control.Feedback type={this.props.inputState.credits.validated ? "valid" : "invalid"}>{this.props.inputState.credits.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>
      </Form></>);
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
