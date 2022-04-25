import { Component, OnInit } from '@angular/core';
import { BaseComponent } from 'src/app/base/base.component';
import { CourseModuleFormComponent } from './course-module-form/course-module-form.component';

@Component({
  selector: 'app-address',
  templateUrl: '../base/base.component.html',
})

export class CourseModuleComponent extends BaseComponent implements OnInit {
  form_str: string = '';
  constructor() {
    super();
    this.title = 'Course Module';
    this.list_config = {
      apiName: 'course-modules',
      columns: [
        {field: 'id', title: 'ID'},
        {field: 'code', title: 'Code'},
        {field: 'name', title: 'Name'},
        {field: 'semester', title: 'Semester'},
        {field: 'credits', title: 'Credits'},
        {field: 'deptName', title: 'Dept Name'},
      ]
    };

    // this.form_config = {
    //   apiName: 'addresses',
    //   form: new FormItem(CourseModuleFormComponent, [])       
    // };

    this.form_type = CourseModuleFormComponent;
  }
    
}
