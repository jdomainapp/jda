import { Component } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-student-class-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class StudentClassListComponent extends BaseListComponent {
  override apiName = "student-classes";
  
  override columns = [
    { field: 'id', title: 'ID' },
    { field: 'name', title: 'Class name' },
  ];
}
