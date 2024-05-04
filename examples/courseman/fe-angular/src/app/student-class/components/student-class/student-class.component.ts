import { Component, OnInit } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
// import { PatternService } from 'src/app/pattern/pattern.consumer';

  
@Component({
  selector: 'app-student-class',
  templateUrl: './student-class.component.html',
})
export class StudentClassComponent extends BaseManagerComponent {
  override title = 'Student Class';
  
}