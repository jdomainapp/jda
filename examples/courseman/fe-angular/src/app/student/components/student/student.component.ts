import { Component, OnInit } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-student',
  templateUrl: './student.component.html',
})
export class StudentComponent extends BaseManagerComponent {
  override title = 'Student';
  
}