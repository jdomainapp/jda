import { Component, OnInit } from '@angular/core';
import { BaseComponent } from '../base/base.component';
import { EnrolmentFormComponent } from './enrolment-form/enrolment-form.component';

@Component({
  selector: 'app-enrolment',
  templateUrl: '../base/base.component.html',
})
export class EnrolmentComponent extends BaseComponent implements OnInit {

  form_str: string = '';
  constructor() {
    super();
    this.title = 'Manage Enrolment';
    this.list_config = {
      apiName: 'enrolments',
      columns: [
        {field: 'id', title: 'ID'},
        {field: 'student', title: 'Student'},
        {field: 'courseModule', title: 'Course Module'},
        {field: 'internalMark', title: 'Internal Mark'},
        {field: 'examMark', title: 'Exam Mark'},
        {field: 'finalGrade', title: 'Final Grade'},
      ]
    };

    // Id	Student	Course module	Internal Mark	Exam Mark	Final Grade	

    this.form_type = EnrolmentFormComponent;
  }
}
