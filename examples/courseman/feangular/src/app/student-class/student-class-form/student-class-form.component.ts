import { Component, Input} from '@angular/core';
import { BaseFormComponent } from 'src/app/base/base-form/base-form.component';
import { FormComponent } from 'src/app/common/form.component';

@Component({
  selector: 'app-student-class-form',
  templateUrl: './student-class-form.component.html',
})
export class StudentClassFormComponent extends BaseFormComponent implements FormComponent{
    // item: any = {};  
    enroll_flag: boolean = false;
    @Input()
    set _item(_item: any) {
        this.item = _item;
    }
    get _item(): number { return this.item; }

    override apiName = 'student-classes';

    changeEnrollFlag() {
      this.enroll_flag = !this.enroll_flag;
    }
  }
