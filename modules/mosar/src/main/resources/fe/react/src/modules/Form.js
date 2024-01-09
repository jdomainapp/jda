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
  }

  @if{hasDateRange2}((
  async handleDateRangeSelect(ranges, rangeID) {
    this.props.handleStateChange(`current.\${RangeIDMap[rangeID].start}`, format(ranges.selection.startDate,'yyyy-MM-dd'), false)
    this.props.handleStateChange(`current.\${RangeIDMap[rangeID].end}`, format(ranges.selection.endDate,'yyyy-MM-dd'), false)
  }
  ))if{hasDateRange2}@

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

  renderTitle() {
    return (<>
      Form: @slot{{ModuleName}}
    </>);
  }

  renderForm() {
    return (
      <Form>
      @slot{{typeSelector}}
      @loop{formInputs}[[
        @slot{{inputCode}}]]loop{formInputs}@
      </Form>);
  }
}
