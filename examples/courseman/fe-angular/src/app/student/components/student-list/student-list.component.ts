import { Component, Input } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-student-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class StudentListComponent extends BaseListComponent {
  override apiName = "students";
  
  override columns = [
    { field: 'id', title: 'ID' },
    { field: 'name', title: 'Full Name' },
    { field: 'dob', title: 'Date of birth' },
    { field: 'email', title: 'Email' },
    { field: 'gender', title: 'Gender' },
  ];

}
