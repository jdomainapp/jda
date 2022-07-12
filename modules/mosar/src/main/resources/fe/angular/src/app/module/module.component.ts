import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { @slot{{ModuleName}}FormComponent } from './@slot{{moduleJname}}-form/@slot{{moduleJname}}-form.component';

@Component({
  selector: 'app-@slot{{moduleJname}}',
  templateUrl: '../base/base.component.html',
})

export class @slot{{ModuleName}}Component extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = '@slot{{Module__name}}';
    this.list_config = {
      apiName: '@slot{{moduleJnames}}',
      columns: [@loop{fieldConfigs}[[
        {field: '@slot{{fieldName}}', title: '@slot{{fieldLabel}}'},]]loop{fieldConfigs}@
      ]
    };

    this.form_type = @slot{{ModuleName}}FormComponent;
  }

}
