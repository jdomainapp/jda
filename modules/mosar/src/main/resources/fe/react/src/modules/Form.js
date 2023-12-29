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
const RangeIDMap={@loop{rangeIDMap}[[
    @slot{{rangeID}}:{start: '@slot{{startField}}', end:'@slot{{endField}}'}
    ]]loop{rangeIDMap}@
}
))if{hasDateRange3}@

export default class @slot{{ModuleName}}Form extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state @if{hasDateRange}((,
      @loop{dateRangeStates}[[@slot{{rangeID}}:
        [{
            startDate: new Date(),
            endDate: addDays(new Date(),7),
            key: 'selection'
        }]
      ]]loop{dateRangeStates}@
      ))if{hasDateRange}@
    };
    @if{haveSubType2}((
    this.renderBaseForm = this.renderBaseForm.bind(this)
    this.renderTypeSelect = this.renderTypeSelect.bind(this)
    this.renderInputsByType = this.renderInputsByType.bind(this)
    ))if{haveSubType2}@
  }

  @if{hasDateRange2}((
  async handleDateRangeSelect(ranges, rangeID) {
    this.props.handleStateChange(`current.\${RangeIDMap[rangeID].start}`, format(ranges.selection.startDate,'yyyy-MM-dd'), false)
    this.props.handleStateChange(`current.\${RangeIDMap[rangeID].end}`, format(ranges.selection.endDate,'yyyy-MM-dd'), false)
  }
  ))if{hasDateRange2}@

  renderTitle() {
    return (<>
      Form: @slot{{ModuleName}}
    </>);
  }

  render@slot{{formBase}}Form() {
    return (
      <>@loop{formInputs}[[
        @slot{{inputCode}}]]loop{formInputs}@
        @loop{formLinkedInputs}[[
        @slot{{AssocWithSideOne}}
        @slot{{AssocWithSideMany}}
        ]]loop{formLinkedInputs}@
      </>);
  }
  @if{haveSubType}((
  renderTypeSelect(){
    return (
    <FormGroup>
        <Form.Label>Type</Form.Label>
        <Form.Control as="select" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== "create"} custom>
            <option value='' disabled selected>&lt;Please choose one&gt;</option>@loop{moduleTypeOptions}[[
            <option value="@slot{{type}}">@slot{{type}}</option>]]loop{moduleTypeOptions}@
        </Form.Control>
    </FormGroup>
    )
  }

  renderInputsByType(type){
    return(
        <>@loop{formTypeInputs}[[
        {type==='@slot{{type}}' && <><br />
        <FormGroup>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <FormControl value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)}  type="@slot{{fieldType}}" @slot{{fieldOptions}} />
        </FormGroup></>}]]loop{formTypeInputs}@
        @loop{formTypeEnumInputs}[[
        {type==='@slot{{type}}' && <><br />
        <FormGroup>
        <Form.Label>@slot{{fieldLabel}}</Form.Label>
        <FormControl as="select" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom @slot{{fieldOptions}} >
          <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
        </FormGroup></>}]]loop{formTypeEnumInputs}@
        @loop{formTypeLinkedInputs}[[
            @slot{{AssocWithSideOne}}
            @slot{{AssocWithSideMany}}
        ]]loop{formTypeLinkedInputs}@
        </>
    )
  }


  renderForm(){
    switch(this.props.current.type){@loop{typedFormRender}[[
        case '@slot{{type}}':
            return (
                <Form>
                    {this.renderTypeSelect()}
                    {this.renderBaseForm()}
                    {this.renderInputsByType('@slot{{type}}')}
                </Form>
            )]]loop{typedFormRender}@
        default:
            return <></>
    }
  }
  ))if{haveSubType}@
}
