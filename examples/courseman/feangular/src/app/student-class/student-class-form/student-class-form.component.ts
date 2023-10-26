import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-student-class-form',
  templateUrl: './student-class-form.component.html',
})
export class StudentClassFormComponent extends BaseFormComponent implements FormComponent{
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    @Input() studentId: string = '';

    override apiName = 'student-classes';

    student_flag: boolean = false;

    ngAfterViewInit() {
      if (this.studentId) {
        this.getStudent(this.studentId);
      }
    }

    getStudent(event: any) {
      this.getItem(event, 'student', 'students');
    }

    changeStudentFlag() {
      this.student_flag = !this.student_flag;
    }
  }
