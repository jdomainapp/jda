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
      rating: 2,
      validated: false,
    };

  }
  //test date range picker
  async handleSelect(ranges) {
    await this.setState({ranges: [ranges]});
    console.log(this.state.ranges);
    // console.log(ranges);
    // {
    //   selection: {
    //     startDate: [native Date Object],
    //     endDate: [native Date Object],
    //   }
    // }
  }
  // renderDateRangePicker(){
  //   return (
  //
  //   )
  // }

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
          <Form.Label>Date Range</Form.Label>
          <DateRangePicker
              onChange={item => this.handleSelect(item.selection)}
              showSelectionPreview={true}
              moveRangeOnFirstSelection={false}
              months={2}
              ranges={this.state.ranges}
              direction="horizontal"
          />
        </FormGroup>
        <br />
        <FormGroup>
          <Form.Label>Price</Form.Label>
          <Slider min={100} max={500} />
        </FormGroup>
        <br/>
        <FormGroup>
          <Form.Label>Rating</Form.Label>
          <StarRatings
              rating={this.state.rating}
              starRatedColor="blue"
              numberOfStars={5}
              name='rating'
              changeRating={async (newRating, name)=>
                {
                  await this.setState({rating: newRating})
                }
              }
          />
        </FormGroup>
        <br/>
        <FormGroup>
          <Button style={{
            cursor: "pointer",
            padding: "18px",
            width: "100%",
            border: "none",
            textAlign: "left",
            outline: "none",
          }} onClick={(e)=>this.expand(e)}>Review</Button>
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
    }} placeholder={"This is a test collapsible input"} rows={"5"}></textarea>
          </div>
          <br/>
        </FormGroup>
        <br/>
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
