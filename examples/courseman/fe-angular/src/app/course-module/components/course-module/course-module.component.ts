import { Component, OnInit } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-course-module',
  templateUrl: './course-module.component.html',
})
export class CourseModuleComponent extends BaseManagerComponent {
  override title = 'Course Module';
  
  initPatterns() {
    
  }
}