import { Component, Input } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';
import { Student } from 'src/app/student/models/student';

@Component({
  selector: 'app-address-form',
  templateUrl: './address-form.component.html',
})
export class AddressFormComponent extends BaseFormComponent {
  
  override apiName = 'addresses';

  override createForm(): void {
    this.form = this.formBuilder.group({
      name: [this.item?.name, [Validators.required, Validators.maxLength(20)]],
      student: [this.item?.student, []]
    });
  }

  // mapValidators(rules: any[]): any[] {
    
  // }
 
}
