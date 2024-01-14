import { Component } from '@angular/core';
import { BaseListComponent } from 'src/app/modules/base/components/base-list/base-list.component';

@Component({
  selector: 'app-course-module-list',
  templateUrl: './../../../modules/base/components/base-list/base-list.component.html',
})
export class CourseModuleListComponent extends BaseListComponent {
  override apiName = "course-modules";
  
  override columns = [
    { field: 'id', title: 'ID' },
    { field: 'code', title: 'Code' },
    { field: 'name', title: 'Module Name' },
    { field: 'semester', title: 'Semester' },
    { field: 'cost', title: 'Cost' },
    { field: 'rating', title: 'Rating' },
    { field: 'description', title: 'Description' },
    { field: 'credits', title: 'Credits' }
  ];

}
