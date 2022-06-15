import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
@slot{{import}}

@Component({
  selector: '@slot{{selector}}',
  templateUrl: '../base/base.component.html',
})

export class @slot{{componentName}} extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = '@slot{{title}}';
    this.list_config = {
      apiName: '@slot{{api}}',
      columns: [@loop{1}[[
        {field: '@slot{{field}}', title: '@slot{{fieldTitle}}'},]]loop{1}@
      ]
    
    this.form_type = @slot{{form_class}};
  }
    
}
