import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-enrolment-form',
  templateUrl: './enrolment-form.component.html',
})
export class EnrolmentFormComponent extends BaseFormComponent implements FormComponent{
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() studentId: string = '';
    @Input() courseModuleId: string = '';

    override apiName = 'enrolments';


    ngAfterViewInit() {
      if (this.studentId) {
        this.getStudent(this.studentId);
      }
      if (this.courseModuleId) {
        this.getCourseModule(this.courseModuleId);
      }
    }

    getStudent(event: any) {
      this.getItem(event, 'student', 'students');
    }
    getCourseModule(event: any) {
      this.getItem(event, 'courseModule', 'course-modules');
    }

  }
