import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { StudentClassFormComponent } from './student-class-form/student-class-form.component';

@Component({
  selector: 'app-student-class',
  templateUrl: '../base/base.component.html',
})

export class StudentClassComponent extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = 'Student class';
    this.list_config = {
      apiName: 'student-classes',
      columns: [
        {field: 'id', title: 'Id'},
        {field: 'name', title: 'Name'},
        {field: 'students', title: 'Students'},
      ]
    };

    this.form_type = StudentClassFormComponent;
  }

}
