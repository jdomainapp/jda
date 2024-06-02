@if{haveDateRange1}((import { DatePipe, formatDate } from '@angular/common';))if{haveDateRange1}@
import { Component, Input } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';
@if{haveSlider1}((import { Options } from 'ngx-slider-v2';))if{haveSlider1}@
@Component({
  selector: 'app-@slot{{moduleJname}}-form',
  templateUrl: './@slot{{moduleJname}}-form.component.html',
})
export class @slot{{ModuleName}}FormComponent extends BaseFormComponent {

  override apiName = '@slot{{moduleJnames}}';
  @if{haveSlider}((@loop{sliderOptions}[[
  @slot{{fieldName}}Options: Options = {
      floor: @slot{{min}},
      ceil: @slot{{max}}
    };
  ]]loop{sliderOptions}@))if{haveSlider}@
  @if{haveDateRange3}((@loop{dateRangeHandlers}[[
  onSelect@slot{{FieldName}}() {
    let range = this.form.get('range')?.value;
    this.item.@slot{{start}} = formatDate(new Date(range[0]), 'yyyy-MM-dd', "en-US");
    this.item.@slot{{end}} = formatDate(new Date(range[1]), 'yyyy-MM-dd', "en-US");

    this.form.patchValue({
      @slot{{start}}: this.item.@slot{{start}},
      @slot{{end}}: this.item.@slot{{end}},
    });
  }
  ]]loop{dateRangeHandlers}@))if{haveDateRange3}@

  override createForm(): void {
    this.form = this.formBuilder.group({
        @if{haveSubType}((type: [this.item?.type, [Validators.required]],))if{haveSubType}@
    @loop{formConfigs}[[
        @slot{{fieldName}}: [this.item?.@slot{{fieldName}}, [@slot{{fieldValidators}}]],
    ]]loop{formConfigs}@
    @if{haveDateRange2}((@loop{dateRangeInputs}[[
        @slot{{fieldName}}:[[new Date(this.item?.@slot{{start}}), new Date(this.item?.@slot{{end}})], Validators.required]
  ]]loop{dateRangeInputs}@))if{haveDateRange2}@
    @if{haveSubType2}((@loop{subTypeFields}[[
        @slot{{fieldName}}: [this.item?.@slot{{fieldName}},this.item.type == '@slot{{type}}' [@slot{{fieldValidators}}]],
  ]]loop{subTypeFields}@))if{haveSubType2}@
    });
  }
}