import { Component, Input, OnInit } from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';

@Component({
  selector: 'app-enrolment-form',
  templateUrl: './enrolment-form.component.html'
})
export class EnrolmentFormComponent extends BaseFormComponent implements OnInit {
  @Input()
  set _item(_item: any) {
      this.item = _item;
  }
  get _item(): number { return this.item; }

  override apiName = 'enrolments';

  getStudent(event: any) {
    this.service.init('students');
    this.getItem(event, 'student');
    this.service.init(this.apiName);
  }

  getCourseModule(event: any) {
    this.service.init('course-modules');
    this.getItem(event, 'courseModule'); 
    this.service.init(this.apiName);    
  }
}
