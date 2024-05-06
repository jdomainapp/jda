import { Component, Input } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { Address } from 'src/app/address/models/address';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
})
export class StudentFormComponent extends BaseFormComponent {
  
  override apiName = 'students';

  override createForm(): void {
    this.form = this.formBuilder.group({
      name: [this.item?.name, [Validators.required, Validators.maxLength(30)]],
      dob: [this.item?.dob, [Validators.required, Validators.maxLength(15)]],
      email: [this.item?.email, [Validators.required, Validators.email, Validators.maxLength(30)]],
      gender: [this.item?.gender, [Validators.required]],
      address: [this.item?.address, []],
      studentClass: [this.item?.studentClass, []],
    });
  }

  // override menus: any[] = [
  //   { endpoint: 'abc', label: 'ID' },
  // ];

  // mapValidators(rules: any[]): any[] {
    
  // }

 
}
