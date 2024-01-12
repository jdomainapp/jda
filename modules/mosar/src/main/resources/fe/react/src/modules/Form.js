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
@loop{importLinkedSubmodules}[[
import @slot{{LinkedDomain}}Submodule from "./@slot{{LinkedDomain}}Submodule";]]loop{importLinkedSubmodules}@

@if{hasDateRange3}((
import 'react-date-range/dist/styles.css'; // main style file
import 'react-date-range/dist/theme/default.css'; // theme css file
))if{hasDateRange3}@

export default class @slot{{ModuleName}}Form extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state @if{hasDateRange}((,
      @loop{dateRangeStates}[[@slot{{rangeID}}:
        [{
            @slot{{startField}}: new Date(),
            @slot{{endField}}: addDays(new Date(),7),
            key: 'selection'
        }]
      ]]loop{dateRangeStates}@
      ))if{hasDateRange}@
    };
  }

@if{hasTextAreaInput}((
  expand(e, inputID) {
    e.preventDefault()
    var coll = document.getElementById(inputID);
    if (coll.style.maxHeight != "0px") {
      coll.style.maxHeight = "0px";
    } else {
      coll.style.maxHeight = coll.scrollHeight + "px";
    }
  }
))if{hasTextAreaInput}@

  getInputState() {
    return {
        @loop{validations}[[
            @slot{{fieldName}}: {
                    optional: false,
                    validated: undefined,
                    message: "",
                    regex: @slot{{regex}},
                    validMsg: "@slot{{validMsg}}",
                    invalidMsg: "@slot{{invalidMsg}}"
                  },
        ]]loop{validations}@
    }
  }

@if{hasDateRange2}((
@loop{dateRangeSelectHandler}[[
  async handle_select_@slot{{rangeID}}(ranges) {
    this.props.handleStateChange("current.@slot{{startField}}", format(ranges.selection.startDate,'yyyy-MM-dd'), false)
    this.props.handleStateChange("current.@slot{{endField}}", format(ranges.selection.endDate,'yyyy-MM-dd'), false)
  }
]]loop{dateRangeSelectHandler}@
))if{hasDateRange2}@

  renderTitle() {
    return (<>
      Form: @slot{{ModuleName}}
    </>);
  }
@if{hasSubType}((
    renderSubTypeForm() {
      switch (this.props.current.type) {
      @loop{subTypeForms}[[
        case "@slot{{subtype}}":
          return <>
            {this.props.mainForm.consumers.map(consumer => consumer.onModelRegion("skipMenuItem", { num: @slot{{skipCount}} }))}
            @slot{{subTypeFormItems}}
          </>]]loop{subTypeForms}@
        default:
          return <></>
      }
    }
))if{hasSubType}@
  renderForm() {
    return (
      <Form>
      @if{hasSubType2}((@slot{{typeSelector}}))if{hasSubType2}@
      @slot{{formInputs}}
      @if{hasSubType3}(({this.renderSubTypeForm()}))if{hasSubType3}@
      </Form>);
  }
}
