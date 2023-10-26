import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { AddressFormComponent } from './address-form/address-form.component';

@Component({
  selector: 'app-address',
  templateUrl: '../base/base.component.html',
})

export class AddressComponent extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = 'Address';
    this.list_config = {
      apiName: 'addresses',
      columns: [
        {field: 'id', title: 'ID'},
        {field: 'name', title: 'City name'},
        {field: 'student', title: 'Student'},
      ]
    };

    this.form_type = AddressFormComponent;
  }

}
