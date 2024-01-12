import { DatePipe, formatDate } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Validators } from '@angular/forms';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';

@Component({
  selector: 'app-enrolment-form',
  templateUrl: './enrolment-form.component.html',
})
export class EnrolmentFormComponent extends BaseFormComponent {
  
  override apiName = 'enrolments';

  onSelectDate() {

    let range = this.form.get('range')?.value;
    this.item.startDate = formatDate(new Date(range[0]), 'yyyy-MM-dd', "en-US");
    this.item.endDate = formatDate(new Date(range[1]), 'yyyy-MM-dd', "en-US");

    this.form.patchValue({
      startDate: this.item.startDate,
      endDate: this.item.endDate,
    });
  }

  override createForm(): void {
    this.form = this.formBuilder.group({
      student: [this.item?.student, [Validators.required]],
      courseModule: [this.item?.courseModule, [Validators.required]],
      internalMark: [this.item?.internalMark, [Validators.maxLength(4), Validators.min(0)]],
      examMark: [this.item?.examMark, [Validators.maxLength(4), Validators.min(0)]],
      startDate: [this.item?.startDate, []],
      endDate: [this.item?.endDate, []],
      range: [[new Date(this.item?.startDate), new Date(this.item?.endDate)], Validators.required]
    });
  }

  // override menus: any[] = [
  //   { name: 'id', label: 'ID' },
  //   { name: 'name', label: 'Name' },
  //   { name: 'dob', label: 'Date of birth' },
  //   { name: 'email', label: 'Email' },
  //   { name: 'gender', label: 'Gender' },
  // ];

}
