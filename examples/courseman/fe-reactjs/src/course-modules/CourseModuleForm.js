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
      validated: false,
    };

  }
  //test date range picker
  async handleSelect(ranges) {

    console.log(ranges);
    await this.setState({ranges: [ranges.selection]})
    this.props.handleStateChange("current.startDate", this.state.ranges[0].startDate, false)
    this.props.handleStateChange("current.endDate", this.state.ranges[0].endDate, false)
  }

  changeRating( newRating, name ) {
    this.setState({
      rating: newRating
    });
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


  renderTitle() {
    return (<>
      Form: Course module
    </>);
  }

  renderForm() {
    console.log(this.props.current)

    switch (this.props.current.type) {
      case 'compulsory': return (<><Form>
        <FormGroup id={"type"}>
          <Form.Label>Type</Form.Label>
          <Form.Control as="select" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== "create"} custom>
            <option value='' disabled selected>&lt;Please choose one&gt;</option>
            <option value="compulsory">compulsory</option>
            <option value="elective">elective</option>  </Form.Control>
        </FormGroup>
        <br />
        <FormGroup id={"id"}>
          <Form.Label>Id</Form.Label>
          <FormControl type="number" value={this.renderObject("current.id")} onChange={(e) => this.props.handleStateChange("current.id", e.target.value, false)} disabled />
        </FormGroup>
        <br />
        <FormGroup id={"code"}>
          <Form.Label>Code</Form.Label>
          <FormControl type="text" value={this.renderObject("current.code")} onChange={(e) => this.props.handleStateChange("current.code", e.target.value, false)} disabled />
        </FormGroup>
        <br />
        <FormGroup id={"name"}>
          <Form.Label>Name</Form.Label>
          <FormControl type="text" value={this.renderObject("current.name")} onChange={(e) => this.props.handleStateChange("current.name", e.target.value, false)}  />
        </FormGroup>
        <br />
        <FormGroup id={"semester"}>
          <Form.Label>Semester</Form.Label>
          <FormControl type="number" value={this.renderObject("current.semester")} onChange={(e) => this.props.handleStateChange("current.semester", e.target.value, false)}  />
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
        <FormGroup id={"cost"}>
          <Form.Label>Cost</Form.Label>
          <div style={{display: "flex", alignItems: "center"}}>
            <FormControl type="number" value={this.renderObject("current.cost")} onChange={(e) => this.props.handleStateChange("current.cost", e.target.value, false)}  />
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
        <FormGroup id={"rating"}>
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
        <FormGroup id={"description"}>
          <Button style={{
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
    }} placeholder={"This is a test collapsible input"} rows={"5"} onChange={(e) => this.props.handleStateChange("current.description", e.target.value, false)}></textarea>
          </div>
          <br/>
        </FormGroup>
        <br/>
        <FormGroup id={"credit"}>
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
