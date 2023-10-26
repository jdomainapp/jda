import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { StudentFormComponent } from './student-form/student-form.component';

@Component({
  selector: 'app-student',
  templateUrl: '../base/base.component.html',
})

export class StudentComponent extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = 'Student';
    this.list_config = {
      apiName: 'students',
      columns: [
        {field: 'id', title: 'Student ID'},
        {field: 'name', title: 'Full Name'},
        {field: 'gender', title: 'Gender'},
        {field: 'dob', title: 'Date of birth'},
        {field: 'address', title: 'Current Address'},
        {field: 'email', title: 'Email'},
        {field: 'studentClass', title: 'Student class'},
        {field: 'enrolments', title: 'Course Enrolments'},
      ]
    };

    this.form_type = StudentFormComponent;
  }

}
