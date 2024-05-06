import { Component, Input } from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { BaseFormComponent } from 'src/app/modules/base/components/base-form/base-form.component';
import { Student } from 'src/app/student/models/student';
import { MenuStudentClass } from '../../patterns/accordion/menu';
import { AccordionFactory } from 'src/app/modules/base/patterns/accordion/accordion.factory';

@Component({
  selector: 'app-student-class-form',
  templateUrl: './student-class-form.component.html',
})
export class StudentClassFormComponent extends BaseFormComponent {
  
  override apiName = 'student-classes';

  override createForm(): void {
    this.form = this.formBuilder.group({
      name: [this.item?.name, [Validators.required, Validators.maxLength(20)]],
      student: [this.item?.student, []]
    });
  }

  override menus = MenuStudentClass;

  // override menus: any[] = [
  //   { endpoint: 'id', name: 'ID' },
  //   { endpoint: 'name', name: 'Name' },
  // ];

  // mapValidators(rules: any[]): any[] {
    
  // }
 
}
