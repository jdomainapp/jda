import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';
import { Options } from 'ngx-slider-v2';

@Component({
  selector: 'app-course-module-form',
  templateUrl: './course-module-form.component.html',
})
export class CourseModuleFormComponent extends BaseFormComponent {
  override apiName = 'course-modules';
  
    options: Options = {
      floor: 0, 
      ceil: 250
    };

  onChange(): void {
    this.createForm();
  }

  override createForm(): void {
    this.form = this.formBuilder.group({
      type: [this.item?.type, [Validators.required]],
      name: [this.item?.name, [Validators.required, Validators.maxLength(30)]],
      description: [this.item?.desciption, []],
      semester: [this.item?.semester, [Validators.required, Validators.maxLength(2), Validators.min(1)]],
      credits: [this.item?.credits, [Validators.required, Validators.pattern('^\\d*$'), Validators.maxLength(2), Validators.min(1)]],
      rating: [this.item?.rating, [Validators.min(1), Validators.max(5)]],
      cost: [this.item?.cost, []],
      deptName: [this.item?.deptName, this.item.type == 'compulsory' ? [] : [Validators.required]],
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
