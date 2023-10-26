import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
})
export class StudentFormComponent extends BaseFormComponent implements FormComponent{
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() addressId: string = '';
    @Input() studentClassId: string = '';
    @Input() enrolmentId: string = '';

    override apiName = 'students';

    enrolment_flag: boolean = false;

    ngAfterViewInit() {
      if (this.addressId) {
        this.getAddress(this.addressId);
      }
      if (this.studentClassId) {
        this.getStudentClass(this.studentClassId);
      }
      if (this.enrolmentId) {
        this.getEnrolment(this.enrolmentId);
      }
    }

    getAddress(event: any) {
      this.getItem(event, 'address', 'addresses');
    }
    getStudentClass(event: any) {
      this.getItem(event, 'studentClass', 'student-classes');
    }
    getEnrolment(event: any) {
      this.getItem(event, 'enrolment', 'enrolments');
    }

    changeEnrolmentFlag() {
      this.enrolment_flag = !this.enrolment_flag;
    }
  }
