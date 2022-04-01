import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { StudentClassFormComponent } from './student-class-form/student-class-form.component';

@Component({
  selector: 'app-address',
  templateUrl: '../base/base.component.html',
})

export class StudentClassComponent extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = 'Student Class';
    this.list_config = {
      apiName: 'student-classes',
      columns: [
        {field: 'id', title: 'ID'},
        {field: 'name', title: 'Name'},
      ]
    };
    
    this.form_type = StudentClassFormComponent;
  }
    
}
