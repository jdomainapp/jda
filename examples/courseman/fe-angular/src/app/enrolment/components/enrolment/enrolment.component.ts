import { Component, OnInit } from '@angular/core';
import { BaseManagerComponent } from 'src/app/modules/base/components/base-manager/base-manager.component';
  
@Component({
  selector: 'app-enrolment',
  templateUrl: './enrolment.component.html',
})
export class EnrolmentComponent extends BaseManagerComponent {
  override title = 'Enrolment';
  
}