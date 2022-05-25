import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
// import { AddressFormComponent } from './address-form/address-form.component';
@slot{{import}}
import { {{ view.name.form }} } from '{{ view.name.form.path }}';

@Component({
  selector: '@slot{{selector}}',
  templateUrl: '../base/base.component.html',
})

export class {{ view.name.main }} extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = '@slot{{title}}';
    this.list_config = {
      apiName: '@slot{{api}}',
      columns: [@loop{1}[[
        {field: '@slot{{field}}', title: '@slot{{fieldTitle}}'},]]loop{1}@
      ]
    };
    
    // this.form_config = {
    //   apiName: 'addresses',
    //   form: new FormItem(AddressFormComponent, [])       
    // };

    this.form_type = @slot{{form_class}};
  }
    
}
